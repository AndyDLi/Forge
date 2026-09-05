package com.forge.domain.user;

/**
 * Raised when registration is attempted for an address that is already in use.
 */
public class DuplicateEmailException extends RuntimeException {

    private static final String MESSAGE = "Email address is already registered.";

    /**
     * Constructs a new exception with the default message.
     */
    public DuplicateEmailException() {
        super(MESSAGE);
    }

    /**
     * Constructs a new exception with the default message and the specified cause.
     * @param cause the cause of the exception
     */
    public DuplicateEmailException(Throwable cause) {
        super(MESSAGE, cause);
    }
}
