package com.chuan.ad.client;

import com.chuan.ad.client.fallbackfactory.SponsorClientFallbackFactory;
import com.chuan.ad.client.vo.AdPlan;
import com.chuan.ad.client.vo.AdPlanGetRequest;
import com.chuan.ad.vo.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(value = "ad-sponsor",fallbackFactory = SponsorClientFallbackFactory.class)
public interface SponsorClient {

    @RequestMapping(value = "/ad-sponsor/get/adPlan",method = RequestMethod.POST)
    CommonResponse<List<AdPlan>> getAdPlans(@RequestBody AdPlanGetRequest request);
}









