package com.chuan.ad.index.keyword;

import com.chuan.ad.index.IndexAware;
import com.chuan.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 关键词限制--索引操作
 *
 * IndexAware<String, Set<Long>>
 *
 * 是倒排索引的一种实现 ： 【 String-->关键词, Set<Long> -->推广单元id集合】
 */

@Slf4j
@Component
public class UnitKeywordIndex implements IndexAware<String, Set<Long>> {
    /**
     * 关键词(key)到推广单元id(unitId)集合   map         倒排索引
     */
    private static Map<String, Set<Long>> keywordUnitMap;
    /**
     * 推广单元id(unitId)到关键词(key)集合  map         正向索引
     */
    private static Map<Long, Set<String>> unitKeywordMap;

    static {
        keywordUnitMap = new ConcurrentHashMap<>();
        unitKeywordMap = new ConcurrentHashMap<>();
    }

    @Override
    public Set<Long> get(String key) {

        if (StringUtils.isEmpty(key)) {
            return Collections.emptySet();
        }

        Set<Long> result = keywordUnitMap.get(key);
        //关键词不存在任何推广单元
        if (result == null) {
            return Collections.emptySet();
        }

        return result;
    }

    @Override
    public void add(String key, Set<Long> value) {

        log.info("UnitKeywordIndex, before add: {}", unitKeywordMap);
        //1、添加到 【关键词(key)到推广单元id(unitId)集合】的map  倒排索引
        /**
         *当keywordUnitMap不存在key时，新增key/value,value为新建的空set对象  (类似一个map初始化过程)
         *当keywordUnitMap存在key时,直接返回value
         */
        Set<Long> unitIdSet = CommonUtils.getorCreate(
                key, keywordUnitMap,
                ConcurrentSkipListSet::new
        );
        unitIdSet.addAll(value);

        /**
         *2、添加到  【推广单元id(unitId)到关键词(key)集合】的map         正向索引
         */
        for (Long unitId : value) {
            Set<String> keywordSet = CommonUtils.getorCreate(
                    unitId, unitKeywordMap,
                    ConcurrentSkipListSet::new
            );
            keywordSet.add(key);
        }

        log.info("UnitKeywordIndex, after add: {}", unitKeywordMap);
    }

    @Override
    public void update(String key, Set<Long> value) {

        log.error("keyword index can not support update");
    }

    @Override
    public void delete(String key, Set<Long> value) {

        log.info("UnitKeywordIndex, before delete: {}", unitKeywordMap);
        //1、删除 【关键词(key)到推广单元id(unitId)集合】的map  倒排索引
        /**
         *当keywordUnitMap不存在key时，新增key/value,value为新建的空set对象  (类似一个map初始化过程)
         *当keywordUnitMap存在key时,直接返回value
         */
        Set<Long> unitIds = CommonUtils.getorCreate(
                key, keywordUnitMap,
                ConcurrentSkipListSet::new
        );
        //部分删除
        unitIds.removeAll(value);

        /**
         *2、删除  【推广单元id(unitId)到关键词(key)集合】的map         正向索引
         */
        for (Long unitId : value) {

            Set<String> keywordSet = CommonUtils.getorCreate(
                    unitId, unitKeywordMap,
                    ConcurrentSkipListSet::new
            );
            //部分删除
            keywordSet.remove(key);
        }

        log.info("UnitKeywordIndex, after delete: {}", unitKeywordMap);
    }

    /**
     * 判断JVM中是否存在推广单元id为unitId，且关键词在keywords中的 关键词限制数据
     *
     * @param unitId
     * @param keywords
     * @return
     */
    public boolean match(Long unitId, List<String> keywords) {
        //1、unitKeywordMap  根据推广单元id(unitId)包含的关键词不为空
        if (unitKeywordMap.containsKey(unitId)
                && CollectionUtils.isNotEmpty(unitKeywordMap.get(unitId))) {
            //2、根据unit获取所有关联的关键词
            Set<String> unitKeywords = unitKeywordMap.get(unitId);
            //3、看是否包含关键词keywords
            return CollectionUtils.isSubCollection(keywords, unitKeywords);
        }
        return false;
    }
}
