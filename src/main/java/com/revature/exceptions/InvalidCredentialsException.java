package com.revature.exceptions;

/**
 * If CLient attempts to access a User but provides invalid credits then throw this exception.
 */
public class InvalidCredentialsException extends RuntimeException{
    public InvalidCredentialsException(final String message) {
        super(message);
    }
}
