package com.zenika.zenikia.shared.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** Minimal liveness endpoint used by the frontend and by manual smoke tests. */
@RestController
class HealthController {

    @GetMapping("/api/health")
    Map<String, String> health() {
        return Map.of("status", "UP", "service", "zenikia");
    }
}
