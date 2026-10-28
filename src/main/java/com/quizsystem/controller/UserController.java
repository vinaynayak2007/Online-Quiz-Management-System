package com.quizsystem.controller;

import com.quizsystem.model.Attempt;
import com.quizsystem.model.Candidate;
import com.quizsystem.model.User;
import com.quizsystem.service.AttemptService;
import com.quizsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;
    private final AttemptService attemptService;

    @Autowired
    public UserController(UserService userService, AttemptService attemptService) {
        this.userService = userService;
        this.attemptService = attemptService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> body) {
        try {
            String name = body.get("name");
            String phone = body.get("phone");
            String password = body.get("password");

            if (phone == null || phone.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Phone number is required"));
            }
            if (password == null || password.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));
            }

            Candidate user = userService.registerCandidate(name, phone, password);
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("id", user.getId());
            resp.put("name", user.getName());
            resp.put("phone", user.getPhone());
            resp.put("role", user.getRole());
            resp.put("message", "Registration successful!");
            return ResponseEntity.status(HttpStatus.CREATED).body(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        String identifier = body.get("identifier");
        if (identifier == null) {
            identifier = body.get("phone");
        }
        if (identifier == null) {
            identifier = body.get("name");
        }
        String password = body.get("password");

        if (identifier == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "Identifier and password required"));
        }

        Optional<User> authOpt = userService.authenticate(identifier, password);
        if (authOpt.isPresent()) {
            User u = authOpt.get();
            Map<String, Object> resp = new HashMap<>();
            resp.put("success", true);
            resp.put("role", u.getRole());
            resp.put("name", u.getName());
            resp.put("phone", u.getPhone());
            resp.put("id", u.getId());
            return ResponseEntity.ok(resp);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "Invalid credentials. Use Admin/Admin123 for Admin or your registered phone & password."));
        }
    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/admin-summary")
    public ResponseEntity<Map<String, Object>> getAdminSummary() {
        List<User> users = userService.getAllUsers();
        List<Attempt> attempts = attemptService.getAllAttempts();

        long passedAttempts = attempts.stream().filter(Attempt::isPassed).count();
        long failedAttempts = attempts.size() - passedAttempts;
        double passRate = attempts.isEmpty() ? 0.0 : ((double) passedAttempts / attempts.size()) * 100.0;
        passRate = Math.round(passRate * 10.0) / 10.0;

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalUsers", users.size());
        summary.put("users", users);
        summary.put("totalAttempts", attempts.size());
        summary.put("passedCount", passedAttempts);
        summary.put("failedCount", failedAttempts);
        summary.put("passRate", passRate);
        summary.put("attempts", attempts);

        return ResponseEntity.ok(summary);
    }
}
