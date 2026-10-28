package com.quizsystem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.quizsystem.model.User;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

        // Ensure default sample student is seeded if empty
        if (usersByPhone.isEmpty()) {
            User demoStudent = new User("usr-demo-1", "Vinay Nayak", "9876543210", "student123", "STUDENT", System.currentTimeMillis());
            demoStudent.setExamsTaken(1);
            demoStudent.setExamsPassed(1);
            usersByPhone.put(demoStudent.getPhone(), demoStudent);
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

    public synchronized User register(String name, String phone, String password) {
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number is required");
        }
        String cleanPhone = phone.trim();
        if (usersByPhone.containsKey(cleanPhone)) {
            throw new IllegalArgumentException("User with phone " + cleanPhone + " already registered");
        }
        String cleanName = (name != null && !name.trim().isEmpty()) ? name.trim() : "Student";
        String cleanPass = (password != null) ? password : "";

        String id = "usr-" + UUID.randomUUID().toString().substring(0, 8);
        User user = new User(id, cleanName, cleanPhone, cleanPass, "STUDENT", System.currentTimeMillis());
        usersByPhone.put(cleanPhone, user);
        persistUsers();
        return user;
    }

    public Optional<User> authenticate(String identifier, String password) {
        if (identifier == null || password == null) {
            return Optional.empty();
        }
        String idClean = identifier.trim();
        String passClean = password.trim();

        // 1. Admin Authentication: Admin / Admin123
        if (idClean.equalsIgnoreCase("Admin") && passClean.equals("Admin123")) {
            User admin = new User("admin-root", "Administrator", "Admin", "", "ADMIN", System.currentTimeMillis());
            return Optional.of(admin);
        }

        // 2. Student Authentication by phone
        User user = usersByPhone.get(idClean);
        if (user != null && user.getPassword() != null && user.getPassword().equals(passClean)) {
            return Optional.of(user);
        }

        // 3. Fallback: Search by name
        for (User u : usersByPhone.values()) {
            if (u.getName() != null && u.getName().equalsIgnoreCase(idClean) && u.getPassword() != null && u.getPassword().equals(passClean)) {
                return Optional.of(u);
            }
        }

        return Optional.empty();
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>(usersByPhone.values());
        list.sort(Comparator.comparingLong(User::getRegisteredAt).reversed());
        return list;
    }

    public synchronized void recordExamResult(String phone, boolean passed) {
        if (phone == null) return;
        User user = usersByPhone.get(phone.trim());
        if (user != null) {
            user.setExamsTaken(user.getExamsTaken() + 1);
            if (passed) {
                user.setExamsPassed(user.getExamsPassed() + 1);
            }
            persistUsers();
        }
    }
}
