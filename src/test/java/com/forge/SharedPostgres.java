package com.forge;

import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * A single PostgreSQL container shared by every test class for integration testing.
 */
final class SharedPostgres {

    static final PostgreSQLContainer INSTANCE = new PostgreSQLContainer("postgres:17-alpine");

    static {
        INSTANCE.start();
    }

    private SharedPostgres() {
    }
}
