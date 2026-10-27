package com.quizsystem.controller;

import com.quizsystem.model.Quiz;
import com.quizsystem.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/quizzes")
@CrossOrigin(origins = "*")
public class QuizController {

    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public ResponseEntity<List<Quiz>> getPublicQuizzes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(quizService.getPublicQuizzes(category, difficulty, search));
    }

    @GetMapping("/categories")
    public ResponseEntity<Set<String>> getCategories() {
        return ResponseEntity.ok(quizService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getQuizForTaking(@PathVariable String id) {
        return quizService.getQuizForTaking(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/admin")
    public ResponseEntity<?> getQuizWithAnswers(@PathVariable String id) {
        return quizService.getQuizWithAnswers(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<Quiz>> getAllQuizzesForAdmin() {
        return ResponseEntity.ok(quizService.getAllQuizzesForAdmin());
    }

    @PostMapping
    public ResponseEntity<Quiz> createQuiz(@RequestBody Quiz quiz) {
        Quiz created = quizService.createQuiz(quiz);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateQuiz(@PathVariable String id, @RequestBody Quiz quiz) {
        return quizService.updateQuiz(id, quiz)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQuiz(@PathVariable String id) {
        boolean deleted = quizService.deleteQuiz(id);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Quiz deleted successfully", "id", id));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- QUESTION LEVEL ENDPOINTS ---

    @GetMapping("/{quizId}/questions")
    public ResponseEntity<List<com.quizsystem.model.Question>> getQuestionsForQuiz(@PathVariable String quizId) {
        return ResponseEntity.ok(quizService.getQuestionsForAdmin(quizId));
    }

    @GetMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<?> getQuestion(@PathVariable String quizId, @PathVariable String questionId) {
        return quizService.getQuestion(quizId, questionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{quizId}/questions")
    public ResponseEntity<?> addQuestion(@PathVariable String quizId, @RequestBody com.quizsystem.model.Question question) {
        return quizService.addQuestion(quizId, question)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<?> updateQuestion(
            @PathVariable String quizId,
            @PathVariable String questionId,
            @RequestBody com.quizsystem.model.Question question) {
        return quizService.updateQuestion(quizId, questionId, question)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{quizId}/questions/{questionId}/right-answer")
    public ResponseEntity<?> updateRightAnswer(
            @PathVariable String quizId,
            @PathVariable String questionId,
            @RequestBody Map<String, Object> body) {
        Object rawAnswers = body.get("correctAnswers");
        List<Integer> correctAnswers = new java.util.ArrayList<>();
        if (rawAnswers instanceof List) {
            for (Object item : (List<?>) rawAnswers) {
                if (item instanceof Number) {
                    correctAnswers.add(((Number) item).intValue());
                } else if (item != null) {
                    try {
                        correctAnswers.add(Integer.parseInt(item.toString()));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } else if (body.containsKey("correctAnswer")) {
            Object single = body.get("correctAnswer");
            if (single instanceof Number) {
                correctAnswers.add(((Number) single).intValue());
            } else if (single != null) {
                try {
                    correctAnswers.add(Integer.parseInt(single.toString()));
                } catch (NumberFormatException ignored) {}
            }
        }

        return quizService.updateRightAnswer(quizId, questionId, correctAnswers)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{quizId}/questions/{questionId}")
    public ResponseEntity<?> deleteQuestion(@PathVariable String quizId, @PathVariable String questionId) {
        boolean deleted = quizService.deleteQuestion(quizId, questionId);
        if (deleted) {
            return ResponseEntity.ok(Map.of("message", "Question deleted successfully", "id", questionId));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/admin/reset")
    public ResponseEntity<?> resetSeedData() {
        quizService.resetSeedData();
        return ResponseEntity.ok(Map.of("message", "Database successfully reset to default sample quizzes"));
    }
}
