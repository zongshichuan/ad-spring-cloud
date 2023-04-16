package com.chuan.ad;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.alibaba.sentinel.annotation.SentinelRestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableFeignClients
public class SearchApplication {
    public static void main(String[] args) {

        new SpringApplicationBuilder(SearchApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }

    @Bean
    @LoadBalanced  //为restTemplate整合ribbon
    RestTemplate restTemplate(){
        return new RestTemplate();
    }



}
