package com.jandadav.simpleapiservice;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RestApiController {

    private final ApplicationInfo info;

    @GetMapping("/")
    ResponseEntity<ApplicationInfo> homePage() {
        return ResponseEntity.ok(info);
    }
}
