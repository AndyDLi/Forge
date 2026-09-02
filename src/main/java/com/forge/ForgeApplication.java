package com.forge;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * The main entry point for the Forge application.
 * Scans the base packages for Spring components, configurations, and services to register them as beans.
 */
@SpringBootApplication(scanBasePackages = {
        "com.forge.config",
        "com.forge.domain",
        "com.forge.infrastructure"
})
@ConfigurationPropertiesScan("com.forge.infrastructure")
public class ForgeApplication {

    public static void main(String[] args) {
        SpringApplication.run(ForgeApplication.class, args);
    }
}
