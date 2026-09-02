package com.forge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@ActiveProfiles("api")
@TestPropertySource(properties = {
        "spring.data.redis.host=localhost",
        "spring.data.redis.port=6379",
        "forge.s3.internal-endpoint=http://localhost:4566",
        "forge.s3.external-endpoint=http://localhost:4566",
        "forge.s3.region=us-east-1",
        "forge.s3.access-key=test",
        "forge.s3.secret-key=test",
        "forge.s3.bucket=forge-artifacts"
})
class ForgeApplicationTests {

    /**
     * Dynamically sets the Spring datasource properties to point to the shared PostgreSQL container,
     * fetching on-demand and ensuring thread safety to prevent parallel test execution issues.
     * @param registry the DynamicPropertyRegistry used to register properties for the Spring context
     */
    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", SharedPostgres.INSTANCE::getJdbcUrl);
        registry.add("spring.datasource.username", SharedPostgres.INSTANCE::getUsername);
        registry.add("spring.datasource.password", SharedPostgres.INSTANCE::getPassword);
    }

    @Test
    void contextLoads() {
    }
}
