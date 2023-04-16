package com.chuan.ad.index.adplan;

import com.chuan.ad.index.IndexAware;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 推广计划---索引操作
 * 
 * 1、索引对象数据存入JVM内存(使用ConcurrentHashMap存储)
 * 2、对索引对象数据进行CRUD
 */
@Component
@Slf4j
public class AdPlanIndex implements IndexAware<Long,AdPlanObject> {

    //<planId,AdPlanObject>
    private static Map<Long, AdPlanObject> objectMap;

    static {
        objectMap = new ConcurrentHashMap<>();
    }

    @Override
    public AdPlanObject get(Long key) {
        return objectMap.get(key);
    }

    @Override
    public void add(Long key, AdPlanObject value) {

        log.info("AdPlan before add: {}", objectMap);
        objectMap.put(key, value);
        log.info("AdPlan after add: {}", objectMap);
    }

    @Override
    public void update(Long key, AdPlanObject value) {

        log.info("AdPlan before update: {}", objectMap);

        AdPlanObject oldObject = objectMap.get(key);
        if (null == oldObject) {
            objectMap.put(key, value);
        } else {
            oldObject.update(value);
        }

        log.info("AdPlan after update: {}", objectMap);
    }

    @Override
    public void delete(Long key, AdPlanObject value) {

        log.info("AdPlan before delete: {}", objectMap);
        objectMap.remove(key);
        log.info("AdPlan after delete: {}", objectMap);
    }
}
