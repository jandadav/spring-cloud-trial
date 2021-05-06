package com.jandadav.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class SampleGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // This would be the filter's api and the arguments here are filter interaction points

        log.info("I AM ALIVE!");
        //log.info(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR); this class seems to hold constants and is somehow significant
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
