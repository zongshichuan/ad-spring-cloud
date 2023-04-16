package com.chuan.ad.client.fallbackfactory;

import com.chuan.ad.client.SponsorClient;
import com.chuan.ad.client.vo.AdPlan;
import com.chuan.ad.client.vo.AdPlanGetRequest;
import com.chuan.ad.vo.CommonResponse;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class SponsorClientFallbackFactory implements FallbackFactory<SponsorClient> {
    @Override
    public SponsorClient create(Throwable throwable) {
        return new SponsorClient() {
            @Override
            public CommonResponse<List<AdPlan>> getAdPlans(AdPlanGetRequest request) {
                log.warn("远程调用异常：",throwable);

                return new CommonResponse<>(-1,"ad-sponsor feign error");
            }
        };
    }
}
