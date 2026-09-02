package com.forge.infrastructure;

import java.util.List;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

/**
 * Runs a check for required configuration properties after all property sources
 * are loaded but before any database connection or bean is created.
 */
public class RequiredConfigurationCheck implements ApplicationListener<ApplicationPreparedEvent> {

    private record Required(String property, String variable) { }

    private static final List<Required> REQUIRED = List.of(
        new Required("spring.datasource.url", "FORGE_DB_URL"),
        new Required("spring.datasource.username", "FORGE_DB_USER"),
        new Required("spring.datasource.password", "FORGE_DB_PASSWORD"),
        new Required("spring.data.redis.host", "FORGE_REDIS_HOST"),
        new Required("spring.data.redis.port", "FORGE_REDIS_PORT"),
        new Required("forge.s3.internal-endpoint", "FORGE_S3_ENDPOINT_INTERNAL"),
        new Required("forge.s3.external-endpoint", "FORGE_S3_ENDPOINT_EXTERNAL"),
        new Required("forge.s3.region", "FORGE_S3_REGION"),
        new Required("forge.s3.access-key", "FORGE_S3_ACCESS_KEY"),
        new Required("forge.s3.secret-key", "FORGE_S3_SECRET_KEY"),
        new Required("forge.s3.bucket", "FORGE_S3_BUCKET")
    );

    /**
     * Checks that all required configuration properties are present and non-blank.
     * @param event the event carrying the fully prepared application context
     * @throws IllegalStateException if any required configuration is missing or blank
     */
    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        Environment environment = event.getApplicationContext().getEnvironment();
        List<String> missing = REQUIRED.stream()
            .filter(required -> !isUsable(environment, required.property()))
            .map(required -> required.variable() + " (" + required.property() + ")")
            .toList();

        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                "Required configuration is missing or blank: " + String.join(", ", missing)
            );
        }
    }

    private boolean isUsable(Environment environment, String property) {
        try {
            return StringUtils.hasText(environment.getProperty(property));
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }
}
