package com.quizsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StudentAnswer {
    private String questionId;
    private List<Integer> selectedOptions = new ArrayList<>();

    public StudentAnswer() {}

    public StudentAnswer(String questionId, List<Integer> selectedOptions) {
        this.questionId = questionId;
        this.selectedOptions = selectedOptions != null ? selectedOptions : new ArrayList<>();
    }

    public String getQuestionId() { return questionId; }
    public void setQuestionId(String questionId) { this.questionId = questionId; }

    public List<Integer> getSelectedOptions() { return selectedOptions; }
    public void setSelectedOptions(List<Integer> selectedOptions) { this.selectedOptions = selectedOptions; }
}
