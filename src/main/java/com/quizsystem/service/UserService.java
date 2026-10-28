package com.quizsystem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.quizsystem.exception.InvalidInputException;
import com.quizsystem.model.AdminUser;
import com.quizsystem.model.Candidate;
import com.quizsystem.model.User;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * CO-1: Demonstrates working with User, Candidate, and AdminUser classes.
 * CO-4: Uses Java Collections and Stream API to filter, sort, and process candidates.
 */
@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final Map<String, User> usersByPhone = new ConcurrentHashMap<>();
    private final File usersFile = new File("data/users.json");
    private final ObjectMapper objectMapper;

    public UserService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @PostConstruct
    public void init() {
        File dataDir = new File("data");
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        loadUsers();

        // Seed default demo candidate if empty
        if (usersByPhone.isEmpty()) {
            Candidate demoCandidate = new Candidate("usr-demo-1", "Vinay Nayak", "9876543210", "student123", System.currentTimeMillis());
            demoCandidate.setExamsTaken(1);
            demoCandidate.setExamsPassed(1);
            usersByPhone.put(demoCandidate.getPhone(), demoCandidate);
            persistUsers();
        }
    }

    private synchronized void loadUsers() {
        if (usersFile.exists() && usersFile.length() > 0) {
            try {
                List<User> list = objectMapper.readValue(usersFile, new TypeReference<List<User>>() {});
                for (User u : list) {
                    if (u.getPhone() != null) {
                        usersByPhone.put(u.getPhone().trim(), u);
                    }
                }
                logger.info("Loaded {} users from {}", usersByPhone.size(), usersFile.getPath());
            } catch (Exception e) {
                logger.warn("Could not read users file: {}", e.getMessage());
            }
        }
    }

    private synchronized void persistUsers() {
        try {
            objectMapper.writeValue(usersFile, new ArrayList<>(usersByPhone.values()));
        } catch (Exception e) {
            logger.error("Could not write users file: {}", e.getMessage());
        }
    }

    public synchronized Candidate registerCandidate(String name, String phone, String password) {
        if (phone == null || phone.trim().isEmpty() || !phone.trim().matches("\\d{10}")) {
            throw new InvalidInputException("Phone number must be a valid 10-digit number");
        }
        String cleanPhone = phone.trim();
        if (usersByPhone.containsKey(cleanPhone)) {
            throw new InvalidInputException("User with phone " + cleanPhone + " is already registered. Please sign in.");
        }
        if (password == null || password.trim().length() < 4) {
            throw new InvalidInputException("Password must be at least 4 characters long");
        }

        String cleanName = (name != null && !name.trim().isEmpty()) ? name.trim() : "Student";
        String id = "usr-" + UUID.randomUUID().toString().substring(0, 8);

        Candidate candidate = new Candidate(id, cleanName, cleanPhone, password.trim(), System.currentTimeMillis());
        usersByPhone.put(cleanPhone, candidate);
        persistUsers();
        logger.info("Registered new candidate: {}", candidate.getDisplayName());
        return candidate;
    }

    public Optional<User> authenticate(String identifier, String password) {
        if (identifier == null || password == null) {
            return Optional.empty();
        }
        String idClean = identifier.trim();
        String passClean = password.trim();

        // 1. Admin Authentication: Admin / Admin123 (Subclass of User)
        if (idClean.equalsIgnoreCase("Admin") && passClean.equals("Admin123")) {
            AdminUser admin = new AdminUser("admin-root", "Administrator", "Admin", "", System.currentTimeMillis(), "SUPER_ADMIN");
            return Optional.of(admin);
        }

        // 2. Candidate Authentication by phone
        User user = usersByPhone.get(idClean);
        if (user != null && user.getPassword() != null && user.getPassword().equals(passClean)) {
            return Optional.of(user);
        }

        // 3. Fallback search by Name
        for (User u : usersByPhone.values()) {
            if (u.getName() != null && u.getName().equalsIgnoreCase(idClean) && u.getPassword() != null && u.getPassword().equals(passClean)) {
                return Optional.of(u);
            }
        }

        return Optional.empty();
    }

    public List<User> getAllUsers() {
        return usersByPhone.values().stream()
                .sorted(Comparator.comparingLong(User::getRegisteredAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new)); // CO-4: Stream to ArrayList
    }

    public List<Candidate> getAllCandidates() {
        return usersByPhone.values().stream()
                .filter(u -> u instanceof Candidate)
                .map(u -> (Candidate) u)
                .sorted(Comparator.comparingLong(User::getRegisteredAt).reversed())
                .collect(Collectors.toCollection(ArrayList::new)); // CO-4: Stream to ArrayList
    }

    public synchronized void recordExamResult(String phone, boolean passed) {
        if (phone == null) return;
        User user = usersByPhone.get(phone.trim());
        if (user instanceof Candidate) {
            Candidate candidate = (Candidate) user;
            candidate.recordExamResult(passed);
            persistUsers();
        }
    }
}
