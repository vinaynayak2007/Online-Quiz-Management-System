package com.quizsystem.exception;

/**
 * CO-3: Base runtime exception for the Online Quiz Management System.
 */
public class QuizSystemException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public QuizSystemException(String message) {
        super(message);
    }

    public QuizSystemException(String message, Throwable cause) {
        super(message, cause);
    }
}
