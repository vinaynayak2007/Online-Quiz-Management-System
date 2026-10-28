package com.quizsystem.model;

/**
 * CO-1: Candidate class demonstrating Inheritance in Java (Candidate extends User).
 * Represents a student or exam taker with metrics, attempt counts, and pass ratios.
 */
@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class Candidate extends User {
    private static final long serialVersionUID = 1L;

    private String studentId;
    private int examsTaken;
    private int examsPassed;
    private String targetCategory; // e.g. "Java", "Cloud", "Algorithms"

    public Candidate() {
        super();
        this.role = "STUDENT";
    }

    public Candidate(String id, String name, String phone, String password, long registeredAt) {
        super(id, name, phone, password, "STUDENT", registeredAt);
        this.studentId = "STU-" + phone;
        this.examsTaken = 0;
        this.examsPassed = 0;
        this.targetCategory = "General";
    }

    public Candidate(String id, String name, String phone, String password, long registeredAt, String studentId, String targetCategory) {
        super(id, name, phone, password, "STUDENT", registeredAt);
        this.studentId = studentId;
        this.examsTaken = 0;
        this.examsPassed = 0;
        this.targetCategory = targetCategory;
    }

    // Business Logic Methods
    public void recordExamResult(boolean passed) {
        this.examsTaken++;
        if (passed) {
            this.examsPassed++;
        }
    }

    public double getPassPercentage() {
        if (this.examsTaken == 0) return 0.0;
        double rate = ((double) this.examsPassed / this.examsTaken) * 100.0;
        return Math.round(rate * 10.0) / 10.0;
    }

    public String getPerformanceStatus() {
        if (this.examsTaken == 0) return "UNATTEMPTED";
        if (this.examsPassed == this.examsTaken) return "EXCELLENT";
        if (this.examsPassed > 0) return "SATISFACTORY";
        return "NEEDS_IMPROVEMENT";
    }

    @Override
    public String getDisplayName() {
        return "[Student] " + this.name + " (ID: " + this.studentId + ", Phone: " + this.phone + ")";
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getExamsTaken() {
        return examsTaken;
    }

    public void setExamsTaken(int examsTaken) {
        this.examsTaken = examsTaken;
    }

    public int getExamsPassed() {
        return examsPassed;
    }

    public void setExamsPassed(int examsPassed) {
        this.examsPassed = examsPassed;
    }

    public String getTargetCategory() {
        return targetCategory;
    }

    public void setTargetCategory(String targetCategory) {
        this.targetCategory = targetCategory;
    }
}
