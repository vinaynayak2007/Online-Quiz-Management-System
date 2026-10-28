package com.quizsystem.exception;

/**
 * CO-3: Thrown when an exam submission is attempted after the allocated timer has expired.
 */
public class QuizExpiredException extends QuizSystemException {
    private static final long serialVersionUID = 1L;

    public QuizExpiredException(String message) {
        super(message);
    }
}
