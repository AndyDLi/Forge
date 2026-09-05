package com.forge.domain.user;

import java.util.Locale;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Creates user accounts.
 */
@Service
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String EMAIL_UNIQUE_CONSTRAINT = "users_email_key";

    public UserRegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user with the given email and password.
     * @param email the email address to register, normalized to lower case
     * @param rawPassword the password in plain text, hashed before storing
     * @return the newly created user
     * @throws DuplicateEmailException if the email address is already registered
     */
    @Transactional
    public User registerUser(String email, String rawPassword) {
        String normalizedEmail = this.normalizeEmail(email);
        if (this.userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException();
        }

        User user = new User(normalizedEmail, this.passwordEncoder.encode(rawPassword));
        try {
            // checks constraints to catch any violation before exiting the transaction method
            return this.userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException ex) {
            // safety net for concurrent race conditions

            // checks if the exception is due to a unique constraint violation on the email field
            if (this.violatesEmailUniqueness(ex)) {
                throw new DuplicateEmailException(ex);
            }

            throw ex;
        }
    }

    private String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    private boolean violatesEmailUniqueness(DataIntegrityViolationException ex) {
        return ex.getCause() instanceof ConstraintViolationException cause
            && EMAIL_UNIQUE_CONSTRAINT.equals(cause.getConstraintName());
    }
}
