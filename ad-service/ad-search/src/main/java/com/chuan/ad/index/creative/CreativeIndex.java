package com.chuan.ad.index.creative;

import com.chuan.ad.index.IndexAware;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 广告创意 ---索引操作
 *
 * 1、索引对象数据存入JVM内存(使用ConcurrentHashMap存储)
 * 2、对索引对象数据进行CRUD
 */

@Slf4j
@Component
public class CreativeIndex implements IndexAware<Long, CreativeObject> {
    //<adId,CreativeObject>
    private static Map<Long, CreativeObject> objectMap;

    static {
        objectMap = new ConcurrentHashMap<>();
    }

    /**
     * 根据创意id集合  获取CreativeObject集合
     *
     * @param adIds
     * @return
     */
    public List<CreativeObject> fetch(Collection<Long> adIds) {

        if (CollectionUtils.isEmpty(adIds)) {
            return Collections.emptyList();
        }

        List<CreativeObject> result = new ArrayList<>();

        adIds.forEach(u -> {
            CreativeObject object = get(u);
            if (null == object) {
                log.error("CreativeObject not found: {}", u);
                return;
            }

            result.add(object);
        });

        return result;
    }

    @Override
    public CreativeObject get(Long key) {
        return objectMap.get(key);
    }

    @Override
    public void add(Long key, CreativeObject value) {

        log.info("Creative before add: {}", objectMap);
        objectMap.put(key, value);
        log.info("Creative after add: {}", objectMap);
    }

    @Override
    public void update(Long key, CreativeObject value) {

        log.info("Creative before update: {}", objectMap);

        CreativeObject oldObject = objectMap.get(key);
        if (null == oldObject) {
            objectMap.put(key, value);
        } else {
            oldObject.update(value);
        }

        log.info("Creative after update: {}", objectMap);
    }

    @Override
    public void delete(Long key, CreativeObject value) {

        log.info("Creative before delete: {}", objectMap);
        objectMap.remove(key);
        log.info("Creative after delete: {}", objectMap);
    }
}
