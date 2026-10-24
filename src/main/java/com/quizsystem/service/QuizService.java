package com.quizsystem.service;

import com.quizsystem.model.Question;
import com.quizsystem.model.Quiz;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final FirebaseService firebaseService;

    @Autowired
    public QuizService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    public List<Quiz> getPublicQuizzes(String category, String difficulty, String search) {
        List<Quiz> quizzes = firebaseService.getAllQuizzes();
        return quizzes.stream()
                .filter(Quiz::isActive)
                .filter(q -> category == null || category.isEmpty() || category.equalsIgnoreCase("all") || q.getCategory().equalsIgnoreCase(category))
                .filter(q -> difficulty == null || difficulty.isEmpty() || difficulty.equalsIgnoreCase("all") || q.getDifficulty().equalsIgnoreCase(difficulty))
                .filter(q -> {
                    if (search == null || search.trim().isEmpty()) return true;
                    String s = search.toLowerCase();
                    return q.getTitle().toLowerCase().contains(s) ||
                            (q.getDescription() != null && q.getDescription().toLowerCase().contains(s)) ||
                            (q.getTags() != null && q.getTags().stream().anyMatch(t -> t.toLowerCase().contains(s)));
                })
                .map(this::sanitizeForStudent)
                .collect(Collectors.toList());
    }

    public List<Quiz> getAllQuizzesForAdmin() {
        return firebaseService.getAllQuizzes();
    }

    public Optional<Quiz> getQuizForTaking(String id) {
        return firebaseService.getQuizById(id).map(this::sanitizeForStudent);
    }

    public Optional<Quiz> getQuizWithAnswers(String id) {
        return firebaseService.getQuizById(id);
    }

    public Quiz createQuiz(Quiz quiz) {
        if (quiz.getId() == null || quiz.getId().trim().isEmpty()) {
            quiz.setId("quiz-" + UUID.randomUUID().toString().substring(0, 8));
        }
        quiz.setCreatedAt(System.currentTimeMillis());
        if (quiz.getQuestions() != null) {
            for (Question q : quiz.getQuestions()) {
                if (q.getId() == null || q.getId().trim().isEmpty()) {
                    q.setId("q-" + UUID.randomUUID().toString().substring(0, 8));
                }
            }
        }
        return firebaseService.saveQuiz(quiz);
    }

    public Optional<Quiz> updateQuiz(String id, Quiz updated) {
        Optional<Quiz> existingOpt = firebaseService.getQuizById(id);
        if (existingOpt.isEmpty()) {
            return Optional.empty();
        }
        Quiz existing = existingOpt.get();
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setCategory(updated.getCategory());
        existing.setDifficulty(updated.getDifficulty());
        existing.setDurationMinutes(updated.getDurationMinutes());
        existing.setPassingScorePercent(updated.getPassingScorePercent());
        existing.setActive(updated.isActive());
        existing.setTags(updated.getTags());

        if (updated.getQuestions() != null) {
            for (Question q : updated.getQuestions()) {
                if (q.getId() == null || q.getId().trim().isEmpty()) {
                    q.setId("q-" + UUID.randomUUID().toString().substring(0, 8));
                }
            }
            existing.setQuestions(updated.getQuestions());
        }

        return Optional.of(firebaseService.saveQuiz(existing));
    }

    public boolean deleteQuiz(String id) {
        return firebaseService.deleteQuiz(id);
    }

    public Set<String> getAllCategories() {
        return firebaseService.getAllQuizzes().stream()
                .map(Quiz::getCategory)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    public void resetSeedData() {
        firebaseService.resetSeedData();
    }

    /**
     * Sanitizes a quiz by removing correct answers and explanations so students cannot view them via Developer Tools.
     */
    private Quiz sanitizeForStudent(Quiz original) {
        Quiz clean = new Quiz();
        clean.setId(original.getId());
        clean.setTitle(original.getTitle());
        clean.setDescription(original.getDescription());
        clean.setCategory(original.getCategory());
        clean.setDifficulty(original.getDifficulty());
        clean.setDurationMinutes(original.getDurationMinutes());
        clean.setPassingScorePercent(original.getPassingScorePercent());
        clean.setActive(original.isActive());
        clean.setCreatedAt(original.getCreatedAt());
        clean.setTags(original.getTags());

        if (original.getQuestions() != null) {
            List<Question> cleanQuestions = new ArrayList<>();
            for (Question q : original.getQuestions()) {
                Question cq = new Question();
                cq.setId(q.getId());
                cq.setText(q.getText());
                cq.setType(q.getType());
                cq.setOptions(q.getOptions());
                cq.setPoints(q.getPoints());
                // Intentionally leave correctAnswers and explanation empty for student player!
                cleanQuestions.add(cq);
            }
            clean.setQuestions(cleanQuestions);
        }
        return clean;
    }
}
