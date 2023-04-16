package com.chuan.ad.index.creativeunit;

import com.chuan.ad.index.IndexAware;
import com.chuan.ad.index.adunit.AdUnitObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 *
 * IndexAware<String, CreativeUnitObject>
 *
 * 是正向索引的一种实现 ： 【 String-->{adId(创意id)-unitId(推广id)}, CreativeUnitObject -->关联对象】
 */
@Slf4j
@Component
public class CreativeUnitIndex implements
        IndexAware<String, CreativeUnitObject> {

    // <adId-unitId, CreativeUnitObject>
    private static Map<String, CreativeUnitObject> objectMap;
    // <adId, unitId Set>
    private static Map<Long, Set<Long>> creativeUnitMap;
    // <unitId, adId set>
    private static Map<Long, Set<Long>> unitCreativeMap;

    static {
        objectMap = new ConcurrentHashMap<>();
        creativeUnitMap = new ConcurrentHashMap<>();
        unitCreativeMap = new ConcurrentHashMap<>();
    }

    @Override
    public CreativeUnitObject get(String key) {
        return objectMap.get(key);
    }

    @Override
    public void add(String key, CreativeUnitObject value) {

        log.info("CreativeUnit before add: {}", objectMap);

        objectMap.put(key, value);

        Set<Long> unitSet = creativeUnitMap.get(value.getAdId());
        if (CollectionUtils.isEmpty(unitSet)) {
            unitSet = new ConcurrentSkipListSet<>();
            creativeUnitMap.put(value.getAdId(), unitSet);
        }
        unitSet.add(value.getUnitId());

        Set<Long> creativeSet = unitCreativeMap.get(value.getUnitId());
        if (CollectionUtils.isEmpty(creativeSet)) {
            creativeSet = new ConcurrentSkipListSet<>();
            unitCreativeMap.put(value.getUnitId(), creativeSet);
        }
        creativeSet.add(value.getAdId());

        log.info("CreativeUnit after add: {}", objectMap);
    }

    @Override
    public void update(String key, CreativeUnitObject value) {

        log.error("CreativeUnitIndex not support update");
    }

    @Override
    public void delete(String key, CreativeUnitObject value) {

        log.info("CreativeUnit before delete: {}", objectMap);

        objectMap.remove(key);

        Set<Long> unitSet = creativeUnitMap.get(value.getAdId());
        if (CollectionUtils.isNotEmpty(unitSet)) {
            unitSet.remove(value.getUnitId());
        }

        Set<Long> creativeSet = unitCreativeMap.get(value.getUnitId());
        if (CollectionUtils.isNotEmpty(creativeSet)) {
            creativeSet.remove(value.getAdId());
        }

        log.info("CreativeUnit after delete: {}", objectMap);
    }

    /**
     * 根据推广单元集合 获取对应的广告创意id集合
     *
     * @param unitObjects
     * @return
     */
    public List<Long> selectAds(List<AdUnitObject> unitObjects) {

        if (CollectionUtils.isEmpty(unitObjects)) {
            return Collections.emptyList();
        }

        List<Long> result = new ArrayList<>();

        for (AdUnitObject unitObject : unitObjects) {
            //1、根据推广单元id，获取对应的广告创意id集合
            Set<Long> adIds = unitCreativeMap.get(unitObject.getUnitId());
            if (CollectionUtils.isNotEmpty(adIds)) {
                result.addAll(adIds);
            }
        }

        return result;
    }
}
