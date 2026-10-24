package com.quizsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class QuestionEvaluation {
    private String questionId;
    private String questionText;
    private String type;
    private List<String> options = new ArrayList<>();
    private List<Integer> selectedOptions = new ArrayList<>();
    private List<Integer> correctAnswers = new ArrayList<>();
    private boolean correct;
    private boolean skipped;
    private int pointsEarned;
    private int pointsPossible;
    private String explanation;

    public QuestionEvaluation() {}

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public List<Integer> getSelectedOptions() { return selectedOptions; }
    public void setSelectedOptions(List<Integer> selectedOptions) { this.selectedOptions = selectedOptions; }

    public List<Integer> getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(List<Integer> correctAnswers) { this.correctAnswers = correctAnswers; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }

    public boolean isSkipped() { return skipped; }
    public void setSkipped(boolean skipped) { this.skipped = skipped; }

    public int getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; }

    public int getPointsPossible() { return pointsPossible; }
    public void setPointsPossible(int pointsPossible) { this.pointsPossible = pointsPossible; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
