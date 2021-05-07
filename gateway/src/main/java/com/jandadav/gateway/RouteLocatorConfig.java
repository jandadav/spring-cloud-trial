package com.jandadav.gateway;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.ReactiveDiscoveryClient;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.handler.predicate.PathRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.*;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.Expression;
import reactor.core.publisher.Flux;

import java.net.URI;
import java.util.*;

@Configuration
public class RouteLocatorConfig {

    /**
     * Static route locator that mirrors the default routes from DiscoveryClient looks like this.
     * Adds opportunity to add per-route filters on predicate match
     *
     * https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/#gatewayfilter-factories
     */
//    @Bean
//    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
//
//        return builder.routes()
//                .route("custom_simpleapiservice", r -> r.path("/SIMPLEAPISERVICE/**")
//                        .filters(f-> f.rewritePath("/SIMPLEAPISERVICE/?(?<remaining>.*)", "/${remaining}"))
//                        .uri("lb://SIMPLEAPISERVICE")
//                ).build();
//    }

    /**
     * Dynamic DiscoveryClient-enabled route locator.
     *
     */
    @Bean
    public RouteDefinitionLocator discoveryRouteLocator(ReactiveDiscoveryClient discoveryClient, DiscoveryLocatorProperties props) {
        return new DiscoveryClientRouteLocator(discoveryClient, props);
    }
}
