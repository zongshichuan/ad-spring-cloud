package com.chuan.ad.handler;

import com.alibaba.fastjson.JSON;
import com.chuan.ad.dump.table.*;
import com.chuan.ad.index.DataTable;
import com.chuan.ad.index.IndexAware;
import com.chuan.ad.index.adplan.AdPlanIndex;
import com.chuan.ad.index.adplan.AdPlanObject;
import com.chuan.ad.index.adunit.AdUnitIndex;
import com.chuan.ad.index.adunit.AdUnitObject;
import com.chuan.ad.index.creative.CreativeIndex;
import com.chuan.ad.index.creative.CreativeObject;
import com.chuan.ad.index.creativeunit.CreativeUnitIndex;
import com.chuan.ad.index.creativeunit.CreativeUnitObject;
import com.chuan.ad.index.district.UnitDistrictIndex;
import com.chuan.ad.index.interest.UnitItIndex;
import com.chuan.ad.index.keyword.UnitKeywordIndex;
import com.chuan.ad.constant.OpType;
import com.chuan.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 1. 索引之间存在着层级的划分, 也就是依赖关系的划分
 * 2. 加载全量索引其实是增量索引 "添加" 的一种特殊实现
 */
@Slf4j
public class AdLevelDataHandler {

    /**
     * 二级索引：不依赖其他索引
     *
     * 二级索引(推广计划)操作实现
     *
     * @param planTable
     * @param type
     */
    public static void handleLevel2(AdPlanTable planTable, OpType type) {

        //1、封装AdPlanObject索引对象
        AdPlanObject planObject = new AdPlanObject(
                planTable.getId(),
                planTable.getUserId(),
                planTable.getPlanStatus(),
                planTable.getStartDate(),
                planTable.getEndDate()
        );
        //2、添加/删除/更新 推广计划索引数据到自己的容器(ConcurrentHashMap)中
        handleBinlogEvent(
                DataTable.of(AdPlanIndex.class),
                planObject.getPlanId(),
                planObject,
                type
        );
    }

    /**
     * 二级索引：不依赖其他索引
     *
     * 二级索引(广告创意)操作实现
     * @param creativeTable
     * @param type
     */
    public static void handleLevel2(AdCreativeTable creativeTable,
                                    OpType type) {

        //1、封装CreativeObject索引对象
        CreativeObject creativeObject = new CreativeObject(
                creativeTable.getAdId(),
                creativeTable.getName(),
                creativeTable.getType(),
                creativeTable.getMaterialType(),
                creativeTable.getHeight(),
                creativeTable.getWidth(),
                creativeTable.getAuditStatus(),
                creativeTable.getAdUrl()
        );
        //2、添加/删除/更新 广告创意索引数据到自己的容器(ConcurrentHashMap)中
        handleBinlogEvent(
                DataTable.of(CreativeIndex.class),
                creativeObject.getAdId(),
                creativeObject,
                type
        );
    }

    /**
     *
     * 三级索引(推广单元)操作实现       推广单元依赖推广计划
     *
     * @param unitTable
     * @param type
     */
    public static void handleLevel3(AdUnitTable unitTable, OpType type) {
        //1、从容器中获取 推广计划索引操作类AdPlanIndex，然后再从AdPlanIndex中的容器(ConcurrentHashMap)中根据推广计划id(planId)拿
        //  到推广计划索引对象AdPlanObject
        AdPlanObject adPlanObject = DataTable.of(
                AdPlanIndex.class
        ).get(unitTable.getPlanId());
        if (null == adPlanObject) {
            log.error("handleLevel3 found AdPlanObject error: {}",
                    unitTable.getPlanId());
            return;
        }
        //2、推广计划(二级索引)存在，封装CreativeObject索引对象(三级索引)
        AdUnitObject unitObject = new AdUnitObject(
                unitTable.getUnitId(),
                unitTable.getUnitStatus(),
                unitTable.getPositionType(),
                unitTable.getPlanId(),
                adPlanObject
        );
        //3、添加/删除/更新 推广单元索引数据到自己的容器(ConcurrentHashMap)中
        handleBinlogEvent(
                DataTable.of(AdUnitIndex.class),
                unitTable.getUnitId(),
                unitObject,
                type
        );
    }

    /**
     * 三级索引(广告创意和推广单元关联)操作实现       依赖推广单元和广告创意  --> 推广单元(依赖推广计划)
     *
     * @param creativeUnitTable
     * @param type
     */
    public static void handleLevel3(AdCreativeUnitTable creativeUnitTable,
                                    OpType type) {
        //更新不能处理   采用先删再加的方式
        if (type == OpType.UPDATE) {
            log.error("CreativeUnitIndex not support update");
            return;
        }
        //1、从容器中获取 推广单元索引操作类AdUnitIndex和广告创意索引操作类CreativeIndex，
        //   1-1、然后再从AdPlanIndex中的容器(ConcurrentHashMap)中根据推广单元id(UnitId)拿到推广单元索引对象AdUnitObject
        //   1-2、然后再从CreativeIndex中的容器(ConcurrentHashMap)中根据广告创意id(AdId)拿到广告创意索引对象CreativeObject
        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(creativeUnitTable.getUnitId());
        CreativeObject creativeObject = DataTable.of(
                CreativeIndex.class
        ).get(creativeUnitTable.getAdId());

        if (null == unitObject || null == creativeObject) {
            log.error("AdCreativeUnitTable index error: {}",
                    JSON.toJSONString(creativeUnitTable));
            return;
        }
        //2、推广单元和广告创意(二/三级索引)存在，封装CreativeUnitObject索引对象(三级索引)
        CreativeUnitObject creativeUnitObject = new CreativeUnitObject(
                creativeUnitTable.getAdId(),
                creativeUnitTable.getUnitId()
        );
        //3、添加/删除/更新 广告创意和推广单元关联索引数据到自己的容器(ConcurrentHashMap)中
        handleBinlogEvent(
                DataTable.of(CreativeUnitIndex.class),
                CommonUtils.stringConcat(
                        creativeUnitObject.getAdId().toString(),
                        creativeUnitObject.getUnitId().toString()
                ),
                creativeUnitObject,
                type
        );
    }

    /**
     * 四级索引(地域限制)操作实现        依赖于推广单元-->推广单元依赖于-->推广计划
     *
     * @param unitDistrictTable
     * @param type
     */
    public static void handleLevel4(AdUnitDistrictTable unitDistrictTable,
                                    OpType type) {
        //更新不能处理   采用先删再加的方式
        if (type == OpType.UPDATE) {
            log.error("district index can not support update");
            return;
        }
        //1、从容器中获取 推广单元索引操作类AdUnitIndex，然后再从AdPlanIndex中的容器(ConcurrentHashMap)中根据推广单元id(UnitId)拿到推广单元索引对象AdUnitObject
        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(unitDistrictTable.getUnitId());
        //推广单元为空，则不能创建地域索引
        if (unitObject == null) {
            log.error("AdUnitDistrictTable index error: {}",
                    unitDistrictTable.getUnitId());
            return;
        }
        //2、推广单元(二级索引)存在
        String key = CommonUtils.stringConcat(
                unitDistrictTable.getProvince(),
                unitDistrictTable.getCity()
        );
        Set<Long> value = new HashSet<>(
                Collections.singleton(unitDistrictTable.getUnitId())
        );
        //3、添加/删除/更新 地域限制索引数据到自己的容器(ConcurrentHashMap)中
        handleBinlogEvent(
                DataTable.of(UnitDistrictIndex.class),
                key, value,
                type
        );
    }

    /**
     * 四级索引(兴趣限制)操作实现        依赖于推广单元-->推广单元依赖于-->推广计划
     * @param unitItTable
     * @param type
     */
    public static void handleLevel4(AdUnitItTable unitItTable, OpType type) {

        if (type == OpType.UPDATE) {
            log.error("it index can not support update");
            return;
        }
        //1、从容器中获取 推广单元索引操作类AdUnitIndex，然后再从AdPlanIndex中的容器(ConcurrentHashMap)中根据推广单元id(UnitId)拿到推广单元索引对象AdUnitObject
        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(unitItTable.getUnitId());
        if (unitObject == null) {
            log.error("AdUnitItTable index error: {}",
                    unitItTable.getUnitId());
            return;
        }
        //2、推广单元(二级索引)存在
        Set<Long> value = new HashSet<>(
                Collections.singleton(unitItTable.getUnitId())
        );
        //3、添加/删除/更新 兴趣限制索引数据到自己的容器(ConcurrentHashMap)中
        handleBinlogEvent(
                DataTable.of(UnitItIndex.class),
                unitItTable.getItTag(),
                value,
                type
        );
    }

    /**
     * 四级索引(关键词限制)操作实现        依赖于推广单元-->推广单元依赖于-->推广计划
     * @param keywordTable
     * @param type
     */
    public static void handleLevel4(AdUnitKeywordTable keywordTable,
                                    OpType type) {

        if (type == OpType.UPDATE) {
            log.error("keyword index can not support update");
            return;
        }
        //1、从容器中获取 推广单元索引操作类AdUnitIndex，然后再从AdPlanIndex中的容器(ConcurrentHashMap)中根据推广单元id(UnitId)拿到推广单元索引对象AdUnitObject
        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(keywordTable.getUnitId());
        if (unitObject == null) {
            log.error("AdUnitKeywordTable index error: {}",
                    keywordTable.getUnitId());
            return;
        }
        //2、推广单元(二级索引)存在
        Set<Long> value = new HashSet<>(
                Collections.singleton(keywordTable.getUnitId())
        );
        //3、添加/删除/更新 关键词限制索引数据到自己的容器(ConcurrentHashMap)中
        handleBinlogEvent(
                DataTable.of(UnitKeywordIndex.class),
                keywordTable.getKeyword(),
                value,
                type
        );
    }

    /**
     *
     * @param index  具体的索引操作类(使用IndexAware接口，统一处理各级索引操作)
     * @param key    存入索引操作类的数据存储容器(ConcurrentHashMap)的key
     * @param value  存入索引操作类的数据存储容器(ConcurrentHashMap)的value
     * @param type   操作的类型(ADD/UPDATE/DELETE)
     * @param <K>
     * @param <V>
     */
    private static <K, V> void handleBinlogEvent(
            IndexAware<K, V> index,
            K key,
            V value,
            OpType type) {

        switch (type) {
            case ADD:
                index.add(key, value);
                break;
            case UPDATE:
                index.update(key, value);
                break;
            case DELETE:
                index.delete(key, value);
                break;
            default:
                break;
        }
    }
}
