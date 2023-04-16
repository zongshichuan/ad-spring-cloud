package com.chuan.ad.index.adplan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 推广计划---索引对象
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdPlanObject {

    private Long planId;
    private Long userId;
    private Integer planStatus;
    private Date startDate;
    private Date endDate;

    /**
     * 更新属性方法
     *
     * @param newObject
     */
    public void update(AdPlanObject newObject){
        if (null != newObject.getPlanId()){
            this.planId = newObject.getPlanId();
        }
        if (null != newObject.getUserId()){
            this.userId = newObject.getUserId();
        }
        if (null != newObject.getPlanStatus()){
            this.planStatus = newObject.getPlanStatus();
        }
        if (null != newObject.getStartDate()) {
            this.startDate = newObject.getStartDate();
        }
        if (null != newObject.getEndDate()) {
            this.endDate = newObject.getEndDate();
        }
    }
}
