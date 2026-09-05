package com.forge.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Unit tests for registration. The repository is mocked and a real encoder is used. No database is required.
 */
@ExtendWith(MockitoExtension.class)
class UserRegistrationServiceTest {

    private static final String PRIMARY_EMAIL = "test@example.com";
    private static final String UNNORMALIZED_EMAIL = "  tESt@ExAMplE.CoM  ";
    private static final String FIRST_EMAIL = "first@example.com";
    private static final String SECOND_EMAIL = "second@example.com";
    private static final String PASSWORD = "secret";
    private static final String INCORRECT_PASSWORD = "incorrectSecret";

    @Mock
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    private UserRegistrationService registrationService;

    @BeforeEach
    void setUp() {
        this.passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        this.registrationService = new UserRegistrationService(this.userRepository, this.passwordEncoder);
    }

    @Test
    void storesAHashThatVerifiesAndNeverThePlaintext() {
        givenAnyAddressIsFree();

        this.registrationService.registerUser(PRIMARY_EMAIL, PASSWORD);

        String storedHash = persistedUser().getPasswordHash();
        assertThat(storedHash).isNotEqualTo(PASSWORD).doesNotContain(PASSWORD);
        assertThat(this.passwordEncoder.matches(PASSWORD, storedHash)).isTrue();
        assertThat(this.passwordEncoder.matches(INCORRECT_PASSWORD, storedHash)).isFalse();
    }

    @Test
    void producesADistinctHashForEachRegistrationOfTheSamePassword() {
        givenAnyAddressIsFree();

        User first = this.registrationService.registerUser(FIRST_EMAIL, PASSWORD);
        User second = this.registrationService.registerUser(SECOND_EMAIL, PASSWORD);

        assertThat(first.getPasswordHash()).isNotEqualTo(second.getPasswordHash());
    }

    @Test
    void normalisesTheAddressBeforeLookupAndStorage() {
        givenAnyAddressIsFree();

        this.registrationService.registerUser(UNNORMALIZED_EMAIL, PASSWORD);

        assertThat(persistedUser().getEmail()).isEqualTo(PRIMARY_EMAIL);
    }

    @Test
    void rejectsAnAddressThatIsAlreadyRegistered() {
        given(this.userRepository.existsByEmail(PRIMARY_EMAIL)).willReturn(true);

        assertThatThrownBy(() -> this.registrationService.registerUser(UNNORMALIZED_EMAIL, PASSWORD))
            .isInstanceOf(DuplicateEmailException.class);

        verify(this.userRepository, never()).saveAndFlush(any(User.class));
    }

    @Test
    void rejectsADuplicateThatOnlyTheDatabaseCatches() {
        given(this.userRepository.existsByEmail(PRIMARY_EMAIL)).willReturn(false);
        given(this.userRepository.saveAndFlush(any(User.class)))
            .willThrow(new DataIntegrityViolationException(
                "Could not execute statement",
                new ConstraintViolationException(
                    "Duplicate key value violation", new SQLException("23505"), "users_email_key")));

        assertThatThrownBy(() -> this.registrationService.registerUser(PRIMARY_EMAIL, PASSWORD))
            .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void doesNotMistakeAnUnrelatedDataErrorForADuplicate() {
        given(this.userRepository.existsByEmail(PRIMARY_EMAIL)).willReturn(false);
        given(this.userRepository.saveAndFlush(any(User.class)))
            .willThrow(new DataIntegrityViolationException(
                "Value too long for type character varying(254)"));

        assertThatThrownBy(() -> this.registrationService.registerUser(PRIMARY_EMAIL, PASSWORD))
            .isInstanceOf(DataIntegrityViolationException.class)
            .isNotInstanceOf(DuplicateEmailException.class);
    }

    /**
     * Simulate a scenario where the email is not yet registered and the repository will accept a new user.
     */
    private void givenAnyAddressIsFree() {
        given(this.userRepository.existsByEmail(anyString())).willReturn(false);
        given(this.userRepository.saveAndFlush(any(User.class)))
            .willAnswer(invocation -> invocation.getArgument(0));
    }

    /**
     * Captures the user that was persisted to the repository and returns it.
     * @return the user that was persisted
     */
    private User persistedUser() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(this.userRepository).saveAndFlush(captor.capture());
        return captor.getValue();
    }
}
