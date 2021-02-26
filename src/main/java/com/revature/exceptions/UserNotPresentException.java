package com.revature.exceptions;

/**
 * If Client attempts to access User who is not in the database then throw this exception.
 */
public class UserNotPresentException extends RuntimeException{
    public UserNotPresentException(final String message) {
        super(message);
    }
}
