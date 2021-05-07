package com.jandadav.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteLocatorConfig {

    /**
     * Static route locator that mirrors the default routes from DiscoveryClient looks like this.
     * Adds opportunity to add per-route filters on predicate match
     *
     */
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                .route("custom_simpleapiservice", r -> r.path("/SIMPLEAPISERVICE/**")
                        .filters(f-> f.rewritePath("/SIMPLEAPISERVICE/?(?<remaining>.*)", "/${remaining}"))
                        .uri("lb://SIMPLEAPISERVICE")
                ).build();
    }

}
