package com.revature.exceptions;

/**
 * If a client attempts to update an object such as user or reimbursement but it fails, throw this exception.
 */
public class UpdateObjectException extends RuntimeException{
    public UpdateObjectException(final String message) {
        super(message);
    }
}
