package com.quizsystem.exception;

/**
 * CO-3: Thrown when database operations (Google Firestore or JSON file persistence) fail.
 */
public class DatabaseException extends QuizSystemException {
    private static final long serialVersionUID = 1L;

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
