package com.chuan.ad.index.adunit;

import com.chuan.ad.index.adplan.AdPlanObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 推广单元--索引对象
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdUnitObject {
    /**
     * 推广单元id
     */
    private Long unitId;
    /**
     * 推广单元状态
     */
    private Integer unitStatus;
    /**
     * 推广单元类型  广告位类型
     */
    private Integer positionType;
    /**
     * 推广计划id
     */
    private Long planId;

    /**
     * 推广计划引用
     */
    private AdPlanObject adPlanObject;

    void update(AdUnitObject newObject) {

        if (null != newObject.getUnitId()) {
            this.unitId = newObject.getUnitId();
        }
        if (null != newObject.getUnitStatus()) {
            this.unitStatus = newObject.getUnitStatus();
        }
        if (null != newObject.getPositionType()) {
            this.positionType = newObject.getPositionType();
        }
        if (null != planId) {
            this.planId = newObject.getPlanId();
        }
        if (null != newObject.getAdPlanObject()) {
            this.adPlanObject = newObject.getAdPlanObject();
        }
    }
    /*
    * 判断是否是开屏广告
    * */
    private static boolean isKaiPing(int positionType) {
        return (positionType & AdUnitConstants.POSITION_TYPE.KAIPING) > 0;
    }
    /*
     * 判断是否是贴片广告
     * */
    private static boolean isTiePian(int positionType) {
        return (positionType & AdUnitConstants.POSITION_TYPE.TIEPIAN) > 0;
    }
    /*
     * 判断是否是中贴广告
     * */
    private static boolean isTiePianMiddle(int positionType) {
        return (positionType & AdUnitConstants.POSITION_TYPE.TIEPIAN_MIDDLE) > 0;
    }
    /*
     * 判断是否是暂停贴广告
     * */
    private static boolean isTiePianPause(int positionType) {
        return (positionType & AdUnitConstants.POSITION_TYPE.TIEPIAN_PAUSE) > 0;
    }
    /*
     * 判断是否是后贴广告
     * */
    private static boolean isTiePianPost(int positionType) {
        return (positionType & AdUnitConstants.POSITION_TYPE.TIEPIAN_POST) > 0;
    }

    /*
    * 统一判断
    * */
    public static boolean isAdSlotTypeOK(int adSlotType, int positionType) {

        switch (adSlotType) {
            case AdUnitConstants.POSITION_TYPE.KAIPING:
                return isKaiPing(positionType);
            case AdUnitConstants.POSITION_TYPE.TIEPIAN:
                return isTiePian(positionType);
            case AdUnitConstants.POSITION_TYPE.TIEPIAN_MIDDLE:
                return isTiePianMiddle(positionType);
            case AdUnitConstants.POSITION_TYPE.TIEPIAN_PAUSE:
                return isTiePianPause(positionType);
            case AdUnitConstants.POSITION_TYPE.TIEPIAN_POST:
                return isTiePianPost(positionType);
            default:
                return false;
        }
    }
}
