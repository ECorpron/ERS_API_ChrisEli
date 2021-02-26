package com.revature.exceptions;

/**
 * If Client attempts to register a User but provides invalid credentials then throw this exception.
 */
public class InvalidUserFieldsException extends RuntimeException{
    public InvalidUserFieldsException(final String message) {
        super(message);
    }
}
