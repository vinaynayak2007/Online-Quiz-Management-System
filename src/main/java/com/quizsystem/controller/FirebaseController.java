package com.quizsystem.controller;

import com.quizsystem.model.FirebaseStatus;
import com.quizsystem.service.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@RestController
@RequestMapping("/api/firebase")
@CrossOrigin(origins = "*")
public class FirebaseController {

    private final FirebaseService firebaseService;

    @Autowired
    public FirebaseController(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    @GetMapping("/status")
    public ResponseEntity<FirebaseStatus> getStatus() {
        return ResponseEntity.ok(firebaseService.getStatus());
    }

    @PostMapping("/configure")
    public ResponseEntity<?> configureFirebase(@RequestBody Map<String, String> payload) {
        String jsonCredentials = payload.get("credentials");
        if (jsonCredentials == null || jsonCredentials.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "No 'credentials' JSON provided in request body."));
        }

        try {
            ByteArrayInputStream stream = new ByteArrayInputStream(jsonCredentials.getBytes(StandardCharsets.UTF_8));
            boolean success = firebaseService.configureFirebase(stream);
            if (success) {
                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "Firebase successfully configured and connected!",
                        "status", firebaseService.getStatus()
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "error", "Failed to initialize Firebase with provided credentials. Check log details."
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }
}
