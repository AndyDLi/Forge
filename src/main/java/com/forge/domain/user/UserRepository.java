package com.forge.domain.user;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Persistence access for users.
 */
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * @param email the email address to look up, already normalized to lower case
     * @return the user with the given email address, if any
     */
    Optional<User> findByEmail(String email);

    /**
     * @param email the email address to look up, already normalized to lower case
     * @return true if a user with the given email address exists, false otherwise
     */
    boolean existsByEmail(String email);
}
