package com.forge.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Load worker components only when running under the "worker" profile.
 */
@Configuration
@Profile("worker")
@ComponentScan("com.forge.worker")
public class WorkerRoleConfig {
}
