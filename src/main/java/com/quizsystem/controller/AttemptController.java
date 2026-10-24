package com.quizsystem.controller;

import com.quizsystem.model.Attempt;
import com.quizsystem.model.QuizResult;
import com.quizsystem.model.QuizSubmission;
import com.quizsystem.service.AttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class AttemptController {

    private final AttemptService attemptService;

    @Autowired
    public AttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @PostMapping("/api/quizzes/{id}/submit")
    public ResponseEntity<?> submitQuiz(@PathVariable String id, @RequestBody QuizSubmission submission) {
        try {
            QuizResult result = attemptService.evaluateAndSubmit(id, submission);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error evaluating quiz: " + e.getMessage());
        }
    }

    @GetMapping("/api/attempts")
    public ResponseEntity<List<Attempt>> getAllAttempts() {
        return ResponseEntity.ok(attemptService.getAllAttempts());
    }
}
