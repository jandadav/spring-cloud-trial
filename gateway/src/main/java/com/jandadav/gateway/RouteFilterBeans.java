package com.jandadav.gateway;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteFilterBeans {

    @Bean
    public GlobalFilter someGlobalFilter() {
        return new SampleGlobalPreFilter();
    }

    @Bean
    public GlobalFilter someGlobalPostFilter() {
        return new SampleGlobalPostFilter();
    }

    /**
     * mutate() api is used in filters that change something on req/resp
     * filter can be declared inline as Lambda
     * @return
     */
    @Bean
    public GlobalFilter mutatingFilter() {
        return (exchange, chain) -> {
            exchange.getRequest().mutate().headers(h -> h.set("customheader", "customvalue"));
            return chain.filter(exchange);
        };
    }
}
