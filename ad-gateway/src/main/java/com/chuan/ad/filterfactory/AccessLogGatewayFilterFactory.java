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
 * 对请求头中添加参数--记录请求延迟信息
 *
 * 注：类名必须以"GatewayFilterFactory"结尾
 */
@Slf4j
@Component
public class AccessLogGatewayFilterFactory extends AbstractNameValueGatewayFilterFactory implements Ordered {
    @Override
    public GatewayFilter apply(NameValueConfig config) {
         return (((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            Long startTime = Long.valueOf(request.getHeaders().getFirst("startTime"));
            String path = request.getURI().getPath();
            long duration = System.currentTimeMillis() - startTime;

            log.info(">>> uri:{},duration:{}ms",path,duration/100);

            //被修改的请求
            ServerHttpRequest modifiedRequest = exchange.getRequest()
                    .mutate()
                    .build();
            //被修改的exchange
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(modifiedRequest)
                    .build();
            return chain.filter(modifiedExchange);
        }));

    }

    //Ordered.LOWEST_PRECEDENCE+1:确保这个过滤器最后执行
    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE+1;
    }
}
