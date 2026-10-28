package com.quizsystem.exception;

/**
 * CO-3: Thrown when user inputs (e.g. invalid phone number, blank questions, illegal options) violate system rules.
 */
public class InvalidInputException extends QuizSystemException {
    private static final long serialVersionUID = 1L;

    public InvalidInputException(String message) {
        super(message);
    }
}
