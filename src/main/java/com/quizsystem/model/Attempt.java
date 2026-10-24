package com.quizsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Attempt {
    private String id;
    private String quizId;
    private String quizTitle;
    private String category;
    private String studentName;
    private String studentEmail;
    private int score;
    private int totalPoints;
    private double percentage;
    private String grade;
    private boolean passed;
    private int timeSpentSeconds;
    private long timestamp = System.currentTimeMillis();
    private List<StudentAnswer> answers = new ArrayList<>();

    public Attempt() {}

    public Attempt(String id, String quizId, String quizTitle, String category, String studentName, String studentEmail,
                   int score, int totalPoints, double percentage, String grade, boolean passed, int timeSpentSeconds) {
        this.id = id;
        this.quizId = quizId;
        this.quizTitle = quizTitle;
        this.category = category;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.score = score;
        this.totalPoints = totalPoints;
        this.percentage = percentage;
        this.grade = grade;
        this.passed = passed;
        this.timeSpentSeconds = timeSpentSeconds;
        this.timestamp = System.currentTimeMillis();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getQuizId() { return quizId; }
    public void setQuizId(String quizId) { this.quizId = quizId; }

    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public List<StudentAnswer> getAnswers() { return answers; }
    public void setAnswers(List<StudentAnswer> answers) { this.answers = answers; }
}
