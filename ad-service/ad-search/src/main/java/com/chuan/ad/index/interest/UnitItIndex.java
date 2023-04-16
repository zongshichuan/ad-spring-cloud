package com.chuan.ad.index.interest;

import com.chuan.ad.index.IndexAware;
import com.chuan.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 兴趣限制 ---索引操作
 *
 * IndexAware<String, Set<Long>>
 *
 * 是倒排索引的一种实现 ： 【 String-->兴趣, Set<Long> -->推广单元id集合】
 */
@Slf4j
@Component
public class UnitItIndex implements IndexAware<String, Set<Long>> {

    /**
     * 兴趣(key)到推广单元id(unitId)集合   map         倒排索引
     */
    private static Map<String, Set<Long>> itUnitMap;

    /**
     * 推广单元id(unitId)到兴趣(key)集合  map         正向索引
     */
    private static Map<Long, Set<String>> unitItMap;

    static {
        itUnitMap = new ConcurrentHashMap<>();
        unitItMap = new ConcurrentHashMap<>();
    }

    @Override
    public Set<Long> get(String key) {
        return itUnitMap.get(key);
    }

    @Override
    public void add(String key, Set<Long> value) {

        log.info("UnitItIndex, before add: {}", unitItMap);
        //1、添加到 【兴趣(key)到推广单元id(unitId)集合】的map  倒排索引
        /**
         *当keywordUnitMap不存在key时，新增key/value,value为新建的空set对象  (类似一个map初始化过程)
         *当keywordUnitMap存在key时,直接返回value
         */
        Set<Long> unitIds = CommonUtils.getorCreate(
                key, itUnitMap,
                ConcurrentSkipListSet::new
        );
        unitIds.addAll(value);
        /**
         *2、添加到  【推广单元id(unitId)到兴趣(key)集合】的map         正向索引
         */
        for (Long unitId : value) {

            Set<String> its = CommonUtils.getorCreate(
                    unitId, unitItMap,
                    ConcurrentSkipListSet::new
            );
            its.add(key);
        }

        log.info("UnitItIndex, after add: {}", unitItMap);
    }

    @Override
    public void update(String key, Set<Long> value) {

        log.error("it index can not support update");
    }

    @Override
    public void delete(String key, Set<Long> value) {

        log.info("UnitItIndex, before delete: {}", unitItMap);
        //1、删除 【兴趣(key)到推广单元id(unitId)集合】的map  倒排索引
        /**
         *当keywordUnitMap不存在key时，新增key/value,value为新建的空set对象  (类似一个map初始化过程)
         *当keywordUnitMap存在key时,直接返回value
         */
        Set<Long> unitIds = CommonUtils.getorCreate(
                key, itUnitMap,
                ConcurrentSkipListSet::new
        );
        //部分删除
        unitIds.removeAll(value);

        /**
         *2、删除  【推广单元id(unitId)到兴趣(key)集合】的map         正向索引
         */
        for (Long unitId : value) {
            Set<String> itTagSet = CommonUtils.getorCreate(
                    unitId, unitItMap,
                    ConcurrentSkipListSet::new
            );
            //部分删除
            itTagSet.remove(key);
        }

        log.info("UnitItIndex, after delete: {}", unitItMap);
    }

    public boolean match(Long unitId, List<String> itTags) {
        //1、unitItMap  根据推广单元id(unitId)包含的兴趣不为空
        if (unitItMap.containsKey(unitId)
                && CollectionUtils.isNotEmpty(unitItMap.get(unitId))) {
            //2、根据unit获取所有关联的兴趣itTags
            Set<String> unitKeywords = unitItMap.get(unitId);
            //3、看是否包含兴趣
            return CollectionUtils.isSubCollection(itTags, unitKeywords);
        }

        return false;
    }
}
