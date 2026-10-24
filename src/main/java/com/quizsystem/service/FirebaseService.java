package com.quizsystem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.quizsystem.config.FirebaseConfig;
import com.quizsystem.model.Attempt;
import com.quizsystem.model.FirebaseStatus;
import com.quizsystem.model.Quiz;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class FirebaseService {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseService.class);
    private static final String QUIZZES_COLLECTION = "quizzes";
    private static final String ATTEMPTS_COLLECTION = "attempts";

    private final FirebaseConfig firebaseConfig;
    private final ObjectMapper objectMapper;

    private final Map<String, Quiz> localQuizzes = new ConcurrentHashMap<>();
    private final Map<String, Attempt> localAttempts = new ConcurrentHashMap<>();
    private final File dataDir = new File("data");
    private final File quizzesFile = new File("data/quizzes.json");
    private final File attemptsFile = new File("data/attempts.json");

    @Autowired
    public FirebaseService(FirebaseConfig firebaseConfig) {
        this.firebaseConfig = firebaseConfig;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @PostConstruct
    public void init() {
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        loadLocalData();

        if (localQuizzes.isEmpty()) {
            loadInitialSeedData();
        }

        // If Firebase is connected, sync initial data if Firebase collection is empty
        if (firebaseConfig.isInitialized()) {
            syncToFirestoreIfEmpty();
        }
    }

    private synchronized void loadLocalData() {
        try {
            if (quizzesFile.exists() && quizzesFile.length() > 0) {
                List<Quiz> list = objectMapper.readValue(quizzesFile, new TypeReference<List<Quiz>>() {});
                for (Quiz q : list) {
                    if (q.getId() != null) {
                        localQuizzes.put(q.getId(), q);
                    }
                }
                logger.info("Loaded {} quizzes from local storage: {}", localQuizzes.size(), quizzesFile.getPath());
            }

            if (attemptsFile.exists() && attemptsFile.length() > 0) {
                List<Attempt> list = objectMapper.readValue(attemptsFile, new TypeReference<List<Attempt>>() {});
                for (Attempt a : list) {
                    if (a.getId() != null) {
                        localAttempts.put(a.getId(), a);
                    }
                }
                logger.info("Loaded {} attempts from local storage", localAttempts.size());
            }
        } catch (Exception e) {
            logger.error("Error reading local data files: {}", e.getMessage(), e);
        }
    }

    private synchronized void persistLocalData() {
        try {
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            objectMapper.writeValue(quizzesFile, new ArrayList<>(localQuizzes.values()));
            objectMapper.writeValue(attemptsFile, new ArrayList<>(localAttempts.values()));
        } catch (Exception e) {
            logger.error("Error writing data to disk: {}", e.getMessage(), e);
        }
    }

    private void loadInitialSeedData() {
        try {
            ClassPathResource res = new ClassPathResource("initial-quizzes.json");
            if (res.exists()) {
                List<Quiz> list = objectMapper.readValue(res.getInputStream(), new TypeReference<List<Quiz>>() {});
                for (Quiz q : list) {
                    localQuizzes.put(q.getId(), q);
                }
                persistLocalData();
                logger.info("Seeded {} initial quizzes into local database", localQuizzes.size());
            }
        } catch (Exception e) {
            logger.error("Failed to load initial quizzes: {}", e.getMessage(), e);
        }
    }

    private void syncToFirestoreIfEmpty() {
        try {
            Firestore db = firebaseConfig.getFirestore();
            if (db == null) return;
            ApiFuture<QuerySnapshot> future = db.collection(QUIZZES_COLLECTION).limit(1).get();
            QuerySnapshot snapshot = future.get();
            if (snapshot.isEmpty()) {
                logger.info("Firestore quizzes collection is empty. Syncing local quizzes to Firestore...");
                for (Quiz q : localQuizzes.values()) {
                    db.collection(QUIZZES_COLLECTION).document(q.getId()).set(q).get();
                }
                logger.info("Synced {} quizzes to Firestore!", localQuizzes.size());
            }
        } catch (Exception e) {
            logger.warn("Could not check/sync Firestore quizzes: {}", e.getMessage());
        }
    }

    // --- QUIZ OPERATIONS ---

    public List<Quiz> getAllQuizzes() {
        if (firebaseConfig.isInitialized()) {
            try {
                Firestore db = firebaseConfig.getFirestore();
                ApiFuture<QuerySnapshot> future = db.collection(QUIZZES_COLLECTION).get();
                List<QueryDocumentSnapshot> documents = future.get().getDocuments();
                List<Quiz> list = new ArrayList<>();
                for (DocumentSnapshot doc : documents) {
                    Quiz quiz = doc.toObject(Quiz.class);
                    if (quiz != null) {
                        list.add(quiz);
                        localQuizzes.put(quiz.getId(), quiz);
                    }
                }
                persistLocalData();
                return list;
            } catch (Exception e) {
                logger.error("Firestore read error, falling back to local: {}", e.getMessage());
            }
        }
        return new ArrayList<>(localQuizzes.values());
    }

    public Optional<Quiz> getQuizById(String id) {
        if (id == null) return Optional.empty();
        if (firebaseConfig.isInitialized()) {
            try {
                Firestore db = firebaseConfig.getFirestore();
                DocumentReference docRef = db.collection(QUIZZES_COLLECTION).document(id);
                ApiFuture<DocumentSnapshot> future = docRef.get();
                DocumentSnapshot doc = future.get();
                if (doc.exists()) {
                    Quiz q = doc.toObject(Quiz.class);
                    if (q != null) {
                        localQuizzes.put(q.getId(), q);
                        return Optional.of(q);
                    }
                }
            } catch (Exception e) {
                logger.error("Firestore get quiz error: {}", e.getMessage());
            }
        }
        return Optional.ofNullable(localQuizzes.get(id));
    }

    public Quiz saveQuiz(Quiz quiz) {
        if (quiz.getId() == null || quiz.getId().trim().isEmpty()) {
            quiz.setId("quiz-" + UUID.randomUUID().toString().substring(0, 8));
        }
        localQuizzes.put(quiz.getId(), quiz);
        persistLocalData();

        if (firebaseConfig.isInitialized()) {
            try {
                Firestore db = firebaseConfig.getFirestore();
                ApiFuture<WriteResult> future = db.collection(QUIZZES_COLLECTION).document(quiz.getId()).set(quiz);
                future.get();
                logger.info("Saved quiz {} to Firebase Firestore", quiz.getId());
            } catch (Exception e) {
                logger.error("Failed to write quiz to Firestore: {}", e.getMessage());
            }
        }
        return quiz;
    }

    public boolean deleteQuiz(String id) {
        if (id == null) return false;
        boolean removed = localQuizzes.remove(id) != null;
        persistLocalData();

        if (firebaseConfig.isInitialized()) {
            try {
                Firestore db = firebaseConfig.getFirestore();
                db.collection(QUIZZES_COLLECTION).document(id).delete().get();
                logger.info("Deleted quiz {} from Firebase Firestore", id);
            } catch (Exception e) {
                logger.error("Failed to delete quiz from Firestore: {}", e.getMessage());
            }
        }
        return removed;
    }

    // --- ATTEMPTS OPERATIONS ---

    public Attempt saveAttempt(Attempt attempt) {
        if (attempt.getId() == null || attempt.getId().trim().isEmpty()) {
            attempt.setId("att-" + UUID.randomUUID().toString().substring(0, 8));
        }
        localAttempts.put(attempt.getId(), attempt);
        persistLocalData();

        if (firebaseConfig.isInitialized()) {
            try {
                Firestore db = firebaseConfig.getFirestore();
                db.collection(ATTEMPTS_COLLECTION).document(attempt.getId()).set(attempt).get();
                logger.info("Saved attempt {} to Firebase Firestore", attempt.getId());
            } catch (Exception e) {
                logger.error("Failed to save attempt to Firestore: {}", e.getMessage());
            }
        }
        return attempt;
    }

    public List<Attempt> getAllAttempts() {
        if (firebaseConfig.isInitialized()) {
            try {
                Firestore db = firebaseConfig.getFirestore();
                ApiFuture<QuerySnapshot> future = db.collection(ATTEMPTS_COLLECTION).get();
                List<QueryDocumentSnapshot> docs = future.get().getDocuments();
                List<Attempt> list = new ArrayList<>();
                for (DocumentSnapshot doc : docs) {
                    Attempt att = doc.toObject(Attempt.class);
                    if (att != null) {
                        list.add(att);
                        localAttempts.put(att.getId(), att);
                    }
                }
                persistLocalData();
                return list;
            } catch (Exception e) {
                logger.error("Firestore get attempts error: {}", e.getMessage());
            }
        }
        return new ArrayList<>(localAttempts.values());
    }

    public List<Attempt> getLeaderboard(String quizId, int limit) {
        List<Attempt> attempts = getAllAttempts();
        return attempts.stream()
                .filter(a -> quizId == null || quizId.isEmpty() || quizId.equalsIgnoreCase("all") || quizId.equals(a.getQuizId()))
                .sorted(Comparator.comparingDouble(Attempt::getPercentage).reversed()
                        .thenComparingInt(Attempt::getTimeSpentSeconds))
                .limit(limit > 0 ? limit : 20)
                .collect(Collectors.toList());
    }

    // --- FIREBASE STATUS & RECONNECT ---

    public FirebaseStatus getStatus() {
        boolean connected = firebaseConfig.isInitialized();
        String mode = connected ? "FIREBASE_FIRESTORE" : "LOCAL_EMULATOR";
        String projectId = firebaseConfig.getProjectId();
        String database = connected ? "Cloud Firestore (Google Cloud)" : "Local Firestore Emulated Storage (JSON file-backed)";
        String message = firebaseConfig.getStatusMessage();
        long quizzesCount = localQuizzes.size();
        long attemptsCount = localAttempts.size();

        return new FirebaseStatus(connected, mode, projectId, database, message, quizzesCount, attemptsCount);
    }

    public boolean configureFirebase(InputStream credentialsStream) {
        boolean success = firebaseConfig.initializeFirebase(credentialsStream);
        if (success) {
            syncToFirestoreIfEmpty();
        }
        return success;
    }

    public void resetSeedData() {
        localQuizzes.clear();
        loadInitialSeedData();
        if (firebaseConfig.isInitialized()) {
            syncToFirestoreIfEmpty();
        }
    }
}
