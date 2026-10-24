package com.quizsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuizSubmission {
    private String studentName;
    private String studentEmail;
    private int timeSpentSeconds;
    private List<StudentAnswer> answers = new ArrayList<>();

    public QuizSubmission() {}

    public QuizSubmission(String studentName, String studentEmail, int timeSpentSeconds, List<StudentAnswer> answers) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.timeSpentSeconds = timeSpentSeconds;
        this.answers = answers != null ? answers : new ArrayList<>();
    }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }

    public List<StudentAnswer> getAnswers() { return answers; }
    public void setAnswers(List<StudentAnswer> answers) { this.answers = answers; }
}
