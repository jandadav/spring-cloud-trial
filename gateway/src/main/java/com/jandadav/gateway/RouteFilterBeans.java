package com.jandadav.gateway;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteFilterBeans {

    @Bean
    public GlobalFilter someGlobalFilter() {
        return new SampleGlobalFilter();
    }
}
