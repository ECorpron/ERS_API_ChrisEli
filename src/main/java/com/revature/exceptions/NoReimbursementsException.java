package com.revature.exceptions;

/**
 * If a client attempts to get all reimbursements but there are none to get, throw this exception.
 */
public class NoReimbursementsException extends RuntimeException {
    public NoReimbursementsException(final String message) {
        super(message);
    }
}
