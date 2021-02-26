package com.revature.exceptions;

/**
 * if client attempts to save a reimbursement but there is a problem, throw this exception.
 */
public class ReimbursementSaveException extends RuntimeException {
    public ReimbursementSaveException(final String message) {
        super(message);
    }
}
