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

    @PostMapping("/admin/reset")
    public ResponseEntity<?> resetSeedData() {
        quizService.resetSeedData();
        return ResponseEntity.ok(Map.of("message", "Database successfully reset to default sample quizzes"));
    }
}
