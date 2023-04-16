package com.chuan.ad;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients//否则会找不到@FeignClient注释的类
public class SponsorApplication {
    public static void main(String[] args) {

        new SpringApplicationBuilder(SponsorApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
