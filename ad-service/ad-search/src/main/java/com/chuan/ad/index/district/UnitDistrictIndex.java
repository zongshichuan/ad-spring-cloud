package com.chuan.ad.index.district;

import com.chuan.ad.index.IndexAware;
import com.chuan.ad.search.vo.feature.DistrictFeature;
import com.chuan.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

/**
 * 地域限制 ---索引操作
 *
 * IndexAware<String, Set<Long>>
 *
 * 是倒排索引的一种实现 ： 【 String-->地域(province-city), Set<Long> -->推广单元id集合】
 */

@Slf4j
@Component
public class UnitDistrictIndex implements IndexAware<String, Set<Long>> {

    /**
     * 地域(key)到推广单元id(unitId)集合   map         倒排索引
     */
    private static Map<String, Set<Long>> districtUnitMap;
    /**
     * 推广单元id(unitId)到地域(key)集合  map         正向索引
     */
    private static Map<Long, Set<String>> unitDistrictMap;

    static {
        districtUnitMap = new ConcurrentHashMap<>();
        unitDistrictMap = new ConcurrentHashMap<>();
    }

    @Override
    public Set<Long> get(String key) {
        return districtUnitMap.get(key);
    }

    @Override
    public void add(String key, Set<Long> value) {

        log.info("UnitDistrictIndex, before add: {}", unitDistrictMap);
        //1、添加到 【地域(key)到推广单元id(unitId)集合】的map  倒排索引
        /**
         *当keywordUnitMap不存在key时，新增key/value,value为新建的空set对象  (类似一个map初始化过程)
         *当keywordUnitMap存在key时,直接返回value
         */
        Set<Long> unitIds = CommonUtils.getorCreate(
                key, districtUnitMap,
                ConcurrentSkipListSet::new
        );
        unitIds.addAll(value);
        /**
         *2、添加到  【推广单元id(unitId)到地域(key)集合】的map         正向索引
         */
        for (Long unitId : value) {

            Set<String> districts = CommonUtils.getorCreate(
                    unitId, unitDistrictMap,
                    ConcurrentSkipListSet::new
            );
            districts.add(key);
        }

        log.info("UnitDistrictIndex, after add: {}", unitDistrictMap);
    }

    @Override
    public void update(String key, Set<Long> value) {

        log.error("district index can not support update");
    }

    @Override
    public void delete(String key, Set<Long> value) {

        log.info("UnitDistrictIndex, before delete: {}", unitDistrictMap);
        //1、删除 【地域(key)到推广单元id(unitId)集合】的map  倒排索引
        /**
         *当keywordUnitMap不存在key时，新增key/value,value为新建的空set对象  (类似一个map初始化过程)
         *当keywordUnitMap存在key时,直接返回value
         */
        Set<Long> unitIds = CommonUtils.getorCreate(
                key, districtUnitMap,
                ConcurrentSkipListSet::new
        );
        unitIds.removeAll(value);
        /**
         *2、删除  【推广单元id(unitId)到地域(key)集合】的map         正向索引
         */
        for (Long unitId : value) {

            Set<String> districts = CommonUtils.getorCreate(
                    unitId, unitDistrictMap,
                    ConcurrentSkipListSet::new
            );
            districts.remove(key);
        }

        log.info("UnitDistrictIndex, after delete: {}", unitDistrictMap);
    }

    /**
     * 判断JVM中是否存在推广单元id为adUnitId，地域在districts中的 地域限制数据
     *
     * @param adUnitId
     * @param districts
     * @return
     */
    public boolean match(Long adUnitId,
                         List<DistrictFeature.ProvinceAndCity> districts) {
        //1、判断JVM中是否存在推广单元id为adUnitId的推广单元
        if (unitDistrictMap.containsKey(adUnitId) &&
                CollectionUtils.isNotEmpty(unitDistrictMap.get(adUnitId))) {
            //2、获取该推广单元对应的地域
            Set<String> unitDistricts = unitDistrictMap.get(adUnitId);

            List<String> targetDistricts = districts.stream()
                    .map(
                            d -> CommonUtils.stringConcat(d.getProvince(), d.getCity())
                    ).collect(Collectors.toList());
            //3、根据推广单元id获取到的地域和传入的地域求子集  返回子集是否为空
            return CollectionUtils.isSubCollection(targetDistricts, unitDistricts);
        }

        return false;
    }
}
