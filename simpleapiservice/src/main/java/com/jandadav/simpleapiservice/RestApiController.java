package com.jandadav.simpleapiservice;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class RestApiController {

    private final ApplicationInfo info;

    @GetMapping("/")
    ResponseEntity<ApplicationInfo> homePage() {
        return ResponseEntity.ok(info);
    }

    @GetMapping("/request")
    ResponseEntity<Map<String, String>> request(@RequestHeader Map<String, String> headers) {
        return ResponseEntity.ok(headers);
    }
}
