package com.forge.api;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST API endpoint returning the application role, used as a health/identity probe.
 */
@RestController
public class RoleProbeController {

    @GetMapping("/internal/role")
    public Map<String, String> role() {
        return Map.of("role", "api");
    }
}
