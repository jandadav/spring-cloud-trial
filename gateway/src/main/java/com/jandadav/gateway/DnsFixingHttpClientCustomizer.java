package com.jandadav.gateway;

import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.cloud.gateway.config.HttpClientCustomizer;
import org.springframework.stereotype.Component;
import reactor.netty.http.client.HttpClient;

/**
 * This is to fix the DNS resolution of local hostname
 */
@Component
public class DnsFixingHttpClientCustomizer implements HttpClientCustomizer {

    @Override
    public HttpClient customize(HttpClient httpClient) {
        return httpClient.resolver(DefaultAddressResolverGroup.INSTANCE);
    }
}
