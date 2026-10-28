package com.quizsystem.exception;

/**
 * CO-3: Thrown when synchronization detects a duplicate or concurrent submission for the same candidate/session.
 */
public class DuplicateSubmissionException extends QuizSystemException {
    private static final long serialVersionUID = 1L;

    public DuplicateSubmissionException(String message) {
        super(message);
    }
}
