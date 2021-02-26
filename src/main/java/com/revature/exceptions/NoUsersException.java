package com.revature.exceptions;

/**
 * If Client attempts to get all users but no users can be found, then throw this exception.
 */
public class NoUsersException extends RuntimeException {
    public NoUsersException(final String message) {
        super(message);
    }
}
