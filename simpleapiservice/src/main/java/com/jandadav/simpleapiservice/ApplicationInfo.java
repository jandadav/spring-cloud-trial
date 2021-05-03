package com.jandadav.simpleapiservice;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
public class ApplicationInfo {
    @Value("${server.port}")
    int port;

    @Value("${spring.application.name}")
    String applicationName;
}
