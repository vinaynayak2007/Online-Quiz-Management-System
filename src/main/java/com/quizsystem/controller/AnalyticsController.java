package com.quizsystem.controller;

import com.quizsystem.service.AttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    private final AttemptService attemptService;

    @Autowired
    public AnalyticsController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        return ResponseEntity.ok(attemptService.getAnalyticsSummary());
    }
}
