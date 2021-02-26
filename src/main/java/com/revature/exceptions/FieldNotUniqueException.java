package com.revature.exceptions;

/**
 * If client attempts to register a new user but provides a non unique field value, then throw this exception.
 */
public class FieldNotUniqueException extends RuntimeException{
    public FieldNotUniqueException(final String message) {
        super(message);
    }
}
