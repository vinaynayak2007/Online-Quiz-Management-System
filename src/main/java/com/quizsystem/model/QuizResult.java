package com.quizsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuizResult {
    private String attemptId;
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
    private int passingScorePercent;
    private int timeSpentSeconds;
    private int totalQuestions;
    private int correctCount;
    private int incorrectCount;
    private int skippedCount;
    private long completedAt;
    private List<QuestionEvaluation> evaluations = new ArrayList<>();

    public QuizResult() {}

    public String getAttemptId() { return attemptId; }
    public void setAttemptId(String attemptId) { this.attemptId = attemptId; }

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

    public int getPassingScorePercent() { return passingScorePercent; }
    public void setPassingScorePercent(int passingScorePercent) { this.passingScorePercent = passingScorePercent; }

    public int getTimeSpentSeconds() { return timeSpentSeconds; }
    public void setTimeSpentSeconds(int timeSpentSeconds) { this.timeSpentSeconds = timeSpentSeconds; }

    public int getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(int totalQuestions) { this.totalQuestions = totalQuestions; }

    public int getCorrectCount() { return correctCount; }
    public void setCorrectCount(int correctCount) { this.correctCount = correctCount; }

    public int getIncorrectCount() { return incorrectCount; }
    public void setIncorrectCount(int incorrectCount) { this.incorrectCount = incorrectCount; }

    public int getSkippedCount() { return skippedCount; }
    public void setSkippedCount(int skippedCount) { this.skippedCount = skippedCount; }

    public long getCompletedAt() { return completedAt; }
    public void setCompletedAt(long completedAt) { this.completedAt = completedAt; }

    public List<QuestionEvaluation> getEvaluations() { return evaluations; }
    public void setEvaluations(List<QuestionEvaluation> evaluations) { this.evaluations = evaluations; }
}
