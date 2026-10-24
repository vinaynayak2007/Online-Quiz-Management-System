package com.quizsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {
    private String id;
    private String text;
    private String type; // SINGLE_CHOICE, MULTIPLE_CHOICE, TRUE_FALSE
    private List<String> options = new ArrayList<>();
    private List<Integer> correctAnswers = new ArrayList<>(); // 0-based indices
    private String explanation;
    private int points = 1;

    public Question() {}

    public Question(String id, String text, String type, List<String> options, List<Integer> correctAnswers, String explanation, int points) {
        this.id = id;
        this.text = text;
        this.type = type != null ? type : "SINGLE_CHOICE";
        this.options = options != null ? options : new ArrayList<>();
        this.correctAnswers = correctAnswers != null ? correctAnswers : new ArrayList<>();
        this.explanation = explanation;
        this.points = points > 0 ? points : 1;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public List<Integer> getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(List<Integer> correctAnswers) { this.correctAnswers = correctAnswers; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }
}
