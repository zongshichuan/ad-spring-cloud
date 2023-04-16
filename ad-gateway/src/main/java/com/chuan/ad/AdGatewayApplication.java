package com.chuan.ad;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class AdGatewayApplication {
    public static void main(String[] args) {

        new SpringApplicationBuilder(AdGatewayApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
