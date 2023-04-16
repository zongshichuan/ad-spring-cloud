package com.chuan.ad.index.adunit;

import com.chuan.ad.index.IndexAware;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 推广单元--索引操作
 *
 * 1、索引对象数据存入JVM内存(使用ConcurrentHashMap存储)
 * 2、对索引对象数据进行CRUD
 */

@Slf4j
@Component
public class AdUnitIndex implements IndexAware<Long, AdUnitObject> {
    
    //<unitId,AdUnitObject>
    private static Map<Long, AdUnitObject> objectMap;

    static {
        objectMap = new ConcurrentHashMap<>();
    }

    /**
     * 判断是否有满足positionType类型的推广单元
     *
     * @param positionType
     * @return
     */
    public Set<Long> match(Integer positionType) {

        Set<Long> adUnitIds = new HashSet<>();

        objectMap.forEach((k, v) -> {
            if (AdUnitObject.isAdSlotTypeOK(positionType, v.getPositionType())) {
                adUnitIds.add(k);
            }
        });

        return adUnitIds;
    }

    /**
     * 返回对应的推广单元对象
     *
     * @param adUnitIds
     * @return
     */
    public List<AdUnitObject> fetch(Collection<Long> adUnitIds) {

        if (CollectionUtils.isEmpty(adUnitIds)) {
            return Collections.emptyList();
        }

        List<AdUnitObject> result = new ArrayList<>();

        adUnitIds.forEach(u -> {
            AdUnitObject object = get(u);
            if (object == null) {
                log.error("AdUnitObject not found: {}", u);
                return;
            }
            result.add(object);
        });

        return result;
    }

    @Override
    public AdUnitObject get(Long key) {
        return objectMap.get(key);
    }

    @Override
    public void add(Long key, AdUnitObject value) {

        log.info("AdUnit before add: {}", objectMap);
        objectMap.put(key, value);
        log.info("AdUnit after add: {}", objectMap);
    }

    @Override
    public void update(Long key, AdUnitObject value) {

        log.info("AdUnit before update: {}", objectMap);

        AdUnitObject oldObject = objectMap.get(key);
        if (null == oldObject) {
            objectMap.put(key, value);
        } else {
            oldObject.update(value);
        }

        log.info("AdUnit after update: {}", objectMap);
    }

    @Override
    public void delete(Long key, AdUnitObject value) {

        log.info("AdUnit before delete: {}", objectMap);
        objectMap.remove(key);
        log.info("AdUnit after delete: {}", objectMap);
    }
}
