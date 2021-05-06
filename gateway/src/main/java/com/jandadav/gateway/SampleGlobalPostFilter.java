package com.jandadav.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * There can also be PRE-POST filters in the same instance
 *
 * doc:
 * Due to the nature of the filter chain, a filter with lower precedence (a lower order in the chain) will execute its “pre”
 * logic in an earlier stage, but it's “post” implementation will get invoked later:
 */
@Slf4j
public class SampleGlobalPostFilter implements GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return chain.filter(exchange).then(Mono.fromRunnable(()-> { //Reactive stuffs
                    log.info("IM POST FILTER");
                }));
    }
}
