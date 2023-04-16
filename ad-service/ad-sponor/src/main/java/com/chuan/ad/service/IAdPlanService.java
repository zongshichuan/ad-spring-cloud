package com.chuan.ad.service;



import com.chuan.ad.entity.AdPlan;
import com.chuan.ad.exception.AdException;
import com.chuan.ad.vo.AdPlanGetRequest;
import com.chuan.ad.vo.AdPlanRequest;
import com.chuan.ad.vo.AdPlanResponse;

import java.util.List;

public interface IAdPlanService {

    /**
     * <h2>创建推广计划</h2>
     * */
    AdPlanResponse createAdPlan(AdPlanRequest request) throws AdException;

    /**
     * <h2>获取推广计划</h2>
     * */
    List<AdPlan> getAdPlanByIds(AdPlanGetRequest request) throws AdException;

    /**
     * <h2>更新推广计划</h2>
     * */
    AdPlanResponse updateAdPlan(AdPlanRequest request) throws AdException;

    /**
     * <h2>删除推广计划</h2>
     * */
    void deleteAdPlan(AdPlanRequest request) throws AdException;
}
