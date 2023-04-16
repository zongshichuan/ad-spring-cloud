package com.chuan.ad.filterfactory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.OrderedGatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractNameValueGatewayFilterFactory;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

/**
 * 自定义gateway过滤器工厂
 *
 * 对请求头中添加参数--记录请求到达时间   在AccessLogGatewayFilterFactory中取出时间打印日志
 *
 * 注：类名必须以"GatewayFilterFactory"结尾
 */
@Component
@Slf4j
public class PreLogGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory implements Ordered {
    @Override
    public GatewayFilter apply(NameValueConfig config) {
        /**
         * 可以在过滤器的filter方法中获取请求对象ServerWebExchange，然后使用ServerWebExchange.getRequest()方法获取ServerHttpRequest，
         * 再调用ServerHttpRequest.mutate()方法获取一个可变的请求对象，最后使用ServerHttpRequest.headers()方法修改请求头即可。
         */
        return (((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpRequest newRequest = request.mutate()
                    .header("startTime", String.valueOf(System.currentTimeMillis()))
                    .build();
            ServerWebExchange newExchange = exchange.mutate().request(newRequest).build();
            return chain.filter(newExchange);
        }));
        //控制过滤器的执行顺序
//        return new OrderedGatewayFilter(filter,0);
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
