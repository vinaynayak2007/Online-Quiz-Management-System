package com.quizsystem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Quiz {
    private String id;
    private String title;
    private String description;
    private String category;
    private String difficulty; // Beginner, Intermediate, Advanced
    private int durationMinutes = 10;
    private int passingScorePercent = 60;
    private boolean active = true;
    private long createdAt = System.currentTimeMillis();
    private List<String> tags = new ArrayList<>();
    private List<Question> questions = new ArrayList<>();

    public Quiz() {}

    public Quiz(String id, String title, String description, String category, String difficulty,
                int durationMinutes, int passingScorePercent, List<Question> questions) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.difficulty = difficulty != null ? difficulty : "Intermediate";
        this.durationMinutes = durationMinutes > 0 ? durationMinutes : 10;
        this.passingScorePercent = passingScorePercent > 0 ? passingScorePercent : 60;
        this.questions = questions != null ? questions : new ArrayList<>();
        this.createdAt = System.currentTimeMillis();
        this.active = true;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public int getPassingScorePercent() { return passingScorePercent; }
    public void setPassingScorePercent(int passingScorePercent) { this.passingScorePercent = passingScorePercent; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public List<Question> getQuestions() { return questions; }
    public void setQuestions(List<Question> questions) { this.questions = questions; }

    public int getTotalQuestions() {
        return questions != null ? questions.size() : 0;
    }

    public int getTotalPoints() {
        if (questions == null) return 0;
        int total = 0;
        for (Question q : questions) {
            total += q.getPoints();
        }
        return total;
    }
}
