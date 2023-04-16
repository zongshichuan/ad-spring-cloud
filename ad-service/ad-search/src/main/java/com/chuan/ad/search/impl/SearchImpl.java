package com.chuan.ad.search.impl;

import com.alibaba.fastjson.JSON;
import com.chuan.ad.index.CommonStatus;
import com.chuan.ad.index.DataTable;
import com.chuan.ad.index.adunit.AdUnitIndex;
import com.chuan.ad.index.adunit.AdUnitObject;
import com.chuan.ad.index.creative.CreativeIndex;
import com.chuan.ad.index.creative.CreativeObject;
import com.chuan.ad.index.creativeunit.CreativeUnitIndex;
import com.chuan.ad.index.district.UnitDistrictIndex;
import com.chuan.ad.index.interest.UnitItIndex;
import com.chuan.ad.index.keyword.UnitKeywordIndex;
import com.chuan.ad.search.ISearch;
import com.chuan.ad.search.vo.SearchRequest;
import com.chuan.ad.search.vo.SearchResponse;
import com.chuan.ad.search.vo.feature.DistrictFeature;
import com.chuan.ad.search.vo.feature.FeatureRelation;
import com.chuan.ad.search.vo.feature.ItFeature;
import com.chuan.ad.search.vo.feature.KeywordFeature;
import com.chuan.ad.search.vo.media.AdSlot;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 *
 */
@Slf4j
@Service
public class SearchImpl implements ISearch {

    public SearchResponse fallback(SearchRequest request, Throwable e) {
        return null;
    }

    @Override
    public SearchResponse fetchAds(SearchRequest request) {

        // 1、获取请求的广告位信息
        List<AdSlot> adSlots = request.getRequestInfo().getAdSlots();

        // 2、获取地域/兴趣/关键词三个 Feature
        KeywordFeature keywordFeature =
                request.getFeatureInfo().getKeywordFeature();
        DistrictFeature districtFeature =
                request.getFeatureInfo().getDistrictFeature();
        ItFeature itFeature =
                request.getFeatureInfo().getItFeature();
        //3、获取三个feature之间的关系
        FeatureRelation relation = request.getFeatureInfo().getRelation();

        //4、构造响应对象
        SearchResponse response = new SearchResponse();
        Map<String, List<SearchResponse.Creative>> adSlot2Ads =
                response.getAdSlot2Ads();
        //填充adSlot2Ads
        for (AdSlot adSlot : adSlots) {

            Set<Long> targetUnitIdSet;

            // 4-1、根据流量类型获取满足条件的推广单元的id集合  （根据流量类型过滤）
            Set<Long> adUnitIdSet = DataTable.of(
                    AdUnitIndex.class
            ).match(adSlot.getPositionType());
            //4-2、feature之间的关系为ADD,过滤三个Feature
            if (relation == FeatureRelation.AND) {
                //(根据关键词过滤)
                filterKeywordFeature(adUnitIdSet, keywordFeature);
                //(根据地域过滤)
                filterDistrictFeature(adUnitIdSet, districtFeature);
                //(根据兴趣过滤)
                filterItTagFeature(adUnitIdSet, itFeature);

                targetUnitIdSet = adUnitIdSet;

            }
            //4-3、feature之间的关系为OR,过滤三个Feature求并集
            else {
                targetUnitIdSet = getORRelationUnitIds(
                        adUnitIdSet,
                        keywordFeature,
                        districtFeature,
                        itFeature
                );
            }
            //4-4、根据对应的推广单元id集合，返回对应的推广单元对象
            List<AdUnitObject> unitObjects = DataTable.of(AdUnitIndex.class).fetch(targetUnitIdSet);
            //4-5、根据推广单元对象集合中  推广单元状态和推广计划状态  过滤出指定状态的unitObjects
            filterAdUnitAndPlanStatus(unitObjects, CommonStatus.VALID);

            //4-6、根据推广单元集合 获取对应的广告创意id集合
            List<Long> adIds = DataTable.of(CreativeUnitIndex.class)
                    .selectAds(unitObjects);
            //4-7、根据广告创意id集合 获取CreativeObject集合
            List<CreativeObject> creatives = DataTable.of(CreativeIndex.class)
                    .fetch(adIds);

            // (根据广告位信息AdSlot 实现对 CreativeObject 的过滤)
            filterCreativeByAdSlot(
                    creatives,
                    adSlot.getWidth(),
                    adSlot.getHeight(),
                    adSlot.getType()
            );

            adSlot2Ads.put(
                    adSlot.getAdSlotCode(), buildCreativeResponse(creatives)
            );
        }

        log.info("fetchAds: {}-{}",
                JSON.toJSONString(request),
                JSON.toJSONString(response));

        return response;
    }

    /**
     * 三个Feature过滤就并集
     *
     * @param adUnitIdSet
     * @param keywordFeature
     * @param districtFeature
     * @param itFeature
     * @return
     */
    private Set<Long> getORRelationUnitIds(Set<Long> adUnitIdSet,
                                           KeywordFeature keywordFeature,
                                           DistrictFeature districtFeature,
                                           ItFeature itFeature) {

        if (CollectionUtils.isEmpty(adUnitIdSet)) {
            return Collections.emptySet();
        }

        Set<Long> keywordUnitIdSet = new HashSet<>(adUnitIdSet);
        Set<Long> districtUnitIdSet = new HashSet<>(adUnitIdSet);
        Set<Long> itUnitIdSet = new HashSet<>(adUnitIdSet);

        //1、三个feature的过滤
        filterKeywordFeature(keywordUnitIdSet, keywordFeature);
        filterDistrictFeature(districtUnitIdSet, districtFeature);
        filterItTagFeature(itUnitIdSet, itFeature);
        //2、求并集
        return new HashSet<>(
                CollectionUtils.union(
                        CollectionUtils.union(keywordUnitIdSet, districtUnitIdSet),
                        itUnitIdSet
                )
        );
    }

    /**
     *过滤 推广单元id为adUnitId，且关键词在keywordFeature.getKeywords()中的 关键词限制数据
     *
     * @param adUnitIds       推广单元的id集合
     * @param keywordFeature
     */
    private void filterKeywordFeature(
            Collection<Long> adUnitIds, KeywordFeature keywordFeature) {

        if (CollectionUtils.isEmpty(adUnitIds)) {
            return;
        }

        if (CollectionUtils.isNotEmpty(keywordFeature.getKeywords())) {
            //过滤出  推广单元id为adUnitId，且关键词在keywordFeature.getKeywords()中的 关键词限制数据
            CollectionUtils.filter(
                    adUnitIds,
                    adUnitId ->
                            DataTable.of(UnitKeywordIndex.class)
                                    .match(adUnitId,
                                            keywordFeature.getKeywords())
            );
        }
    }

    /**
     * 过滤出  推广单元id为adUnitIds，地域在districts中的 地域限制数据
     *
     * @param adUnitIds         推广单元的id集合
     * @param districtFeature
     */
    private void filterDistrictFeature(
            Collection<Long> adUnitIds, DistrictFeature districtFeature
    ) {
        if (CollectionUtils.isEmpty(adUnitIds)) {
            return;
        }

        if (CollectionUtils.isNotEmpty(districtFeature.getDistricts())) {
            //过滤出  推广单元id为adUnitId，地域在districts中的 地域限制数据
            CollectionUtils.filter(
                    adUnitIds,
                    adUnitId ->
                            DataTable.of(UnitDistrictIndex.class)
                                    .match(adUnitId,
                                            districtFeature.getDistricts())
            );
        }
    }

    /**
     * 过滤出  推广单元id为adUnitIds，兴趣在districts中的 兴趣限制数据
     *
     * @param adUnitIds       推广单元的id集合
     * @param itFeature
     */
    private void filterItTagFeature(Collection<Long> adUnitIds,
                                    ItFeature itFeature) {

        if (CollectionUtils.isEmpty(adUnitIds)) {
            return;
        }

        if (CollectionUtils.isNotEmpty(itFeature.getIts())) {
            //过滤出  推广单元id为adUnitIds，兴趣在districts中的 兴趣限制数据
            CollectionUtils.filter(
                    adUnitIds,
                    adUnitId ->
                            DataTable.of(UnitItIndex.class)
                                    .match(adUnitId,
                                            itFeature.getIts())
            );
        }
    }

    /**
     * 根据推广单元对象集合中  推广单元状态和推广计划状态  过滤出指定状态的unitObjects
     *
     * @param unitObjects
     * @param status
     */
    private void filterAdUnitAndPlanStatus(List<AdUnitObject> unitObjects,
                                           CommonStatus status) {

        if (CollectionUtils.isEmpty(unitObjects)) {
            return;
        }
        //
        CollectionUtils.filter(
                unitObjects,
                object -> object.getUnitStatus().equals(status.getStatus())
                && object.getAdPlanObject().getPlanStatus().equals(status.getStatus())
        );
    }

    /**
     * 根据AdSlot广告位信息 过滤 creatives
     * @param creatives     想要过滤的集合对象
     * @param width         广告位的宽
     * @param height        广告位的长
     * @param type          广告物料类型: 图片, 视频
     */
    private void filterCreativeByAdSlot(List<CreativeObject> creatives,
                                        Integer width,
                                        Integer height,
                                        List<Integer> type) {

        if (CollectionUtils.isEmpty(creatives)) {
            return;
        }

        CollectionUtils.filter(
                creatives,
                creative ->
                        creative.getAuditStatus().equals(CommonStatus.VALID.getStatus())
                && creative.getWidth().equals(width)
                && creative.getHeight().equals(height)
                && type.contains(creative.getType())
        );
    }

    /**
     *
     * @param creatives
     * @return
     */
    private List<SearchResponse.Creative> buildCreativeResponse(
            List<CreativeObject> creatives
    ) {

        if (CollectionUtils.isEmpty(creatives)) {
            return Collections.emptyList();
        }

        CreativeObject randomObject = creatives.get(
                Math.abs(new Random().nextInt()) % creatives.size()
        );

        return Collections.singletonList(
                SearchResponse.convert(randomObject)
        );
    }
}
