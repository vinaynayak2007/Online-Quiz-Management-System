package com.quizsystem.model;

public class FirebaseStatus {
    private boolean connected;
    private String mode; // "FIREBASE_FIRESTORE" or "LOCAL_EMULATOR"
    private String projectId;
    private String clientEmail;
    private String database;
    private String message;
    private long totalQuizzes;
    private long totalQuestions;
    private long totalAttempts;

    public FirebaseStatus() {}

    public FirebaseStatus(boolean connected, String mode, String projectId, String clientEmail, String database, String message, long totalQuizzes, long totalQuestions, long totalAttempts) {
        this.connected = connected;
        this.mode = mode;
        this.projectId = projectId;
        this.clientEmail = clientEmail;
        this.database = database;
        this.message = message;
        this.totalQuizzes = totalQuizzes;
        this.totalQuestions = totalQuestions;
        this.totalAttempts = totalAttempts;
    }

    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) { this.connected = connected; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public String getProjectId() { return projectId; }
    public void setProjectId(String projectId) { this.projectId = projectId; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getDatabase() { return database; }
    public void setDatabase(String database) { this.database = database; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public long getTotalQuizzes() { return totalQuizzes; }
    public void setTotalQuizzes(long totalQuizzes) { this.totalQuizzes = totalQuizzes; }

    public long getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(long totalQuestions) { this.totalQuestions = totalQuestions; }

    public long getTotalAttempts() { return totalAttempts; }
    public void setTotalAttempts(long totalAttempts) { this.totalAttempts = totalAttempts; }
}
