package com.quizsystem.controller;

import com.quizsystem.model.Attempt;
import com.quizsystem.service.AttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaderboard")
@CrossOrigin(origins = "*")
public class LeaderboardController {

    private final AttemptService attemptService;

    @Autowired
    public LeaderboardController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @GetMapping
    public ResponseEntity<List<Attempt>> getLeaderboard(
            @RequestParam(required = false, defaultValue = "all") String quizId,
            @RequestParam(required = false, defaultValue = "20") int limit) {
        return ResponseEntity.ok(attemptService.getLeaderboard(quizId, limit));
    }
}
