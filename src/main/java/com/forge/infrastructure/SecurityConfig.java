package com.forge.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Security configuration for the application: password encoding.
 */
@Configuration
public class SecurityConfig {

    /**
     * A delegating encoder that hashes with Bcrypt.
     * Resulting hash is prefixed with the algorithm identifier.
     * @return the application password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
