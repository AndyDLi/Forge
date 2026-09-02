package com.forge.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Load API components only when running under the "api" profile.
 */
@Configuration
@Profile("api")
@ComponentScan("com.forge.api")
public class ApiRoleConfig {
}
