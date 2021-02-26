package com.revature.exceptions;

/**
 * If client sends Invalid Id for a User or reimbursement throw this exception.
 */
public class InvalidIdException extends RuntimeException{
    public InvalidIdException(final String message) {
        super(message);
    }
}
