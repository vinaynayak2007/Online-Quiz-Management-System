package com.quizsystem.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.config.path:serviceAccountKey.json}")
    private String configPath;

    @Value("${firebase.database.url:}")
    private String databaseUrl;

    private boolean initialized = false;
    private String projectId = "micro-project-96796";
    private String clientEmail = "";
    private Firestore firestore = null;
    private String statusMessage = "Using Local Firebase Storage & Emulator";
    private final ObjectMapper mapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        initializeFirebase(null);
    }

    public synchronized boolean initializeFirebase(InputStream customStream) {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                for (FirebaseApp app : FirebaseApp.getApps()) {
                    app.delete();
                }
            }

            byte[] keyBytes = null;

            if (customStream != null) {
                keyBytes = customStream.readAllBytes();
            } else {
                File file = new File(configPath);
                if (file.exists() && file.isFile()) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        keyBytes = fis.readAllBytes();
                        logger.info("Found Firebase service account at path: {}", file.getAbsolutePath());
                    }
                } else {
                    String envPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
                    if (envPath != null && new File(envPath).exists()) {
                        try (FileInputStream fis = new FileInputStream(envPath)) {
                            keyBytes = fis.readAllBytes();
                            logger.info("Found Firebase credentials from GOOGLE_APPLICATION_CREDENTIALS: {}", envPath);
                        }
                    } else {
                        InputStream cpStream = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
                        if (cpStream != null) {
                            keyBytes = cpStream.readAllBytes();
                            logger.info("Found Firebase credentials in classpath: serviceAccountKey.json");
                        }
                    }
                }
            }

            if (keyBytes != null && keyBytes.length > 0) {
                try {
                    JsonNode root = mapper.readTree(keyBytes);
                    if (root.has("project_id")) {
                        this.projectId = root.get("project_id").asText();
                    }
                    if (root.has("client_email")) {
                        this.clientEmail = root.get("client_email").asText();
                    }
                } catch (Exception e) {
                    logger.warn("Could not parse JSON fields from key: {}", e.getMessage());
                }

                GoogleCredentials credentials = GoogleCredentials.fromStream(new ByteArrayInputStream(keyBytes));
                if (credentials instanceof ServiceAccountCredentials) {
                    ServiceAccountCredentials sac = (ServiceAccountCredentials) credentials;
                    if (sac.getProjectId() != null && !sac.getProjectId().isEmpty()) {
                        this.projectId = sac.getProjectId();
                    }
                    if (sac.getClientEmail() != null && !sac.getClientEmail().isEmpty()) {
                        this.clientEmail = sac.getClientEmail();
                    }
                }

                FirebaseOptions.Builder builder = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .setProjectId(this.projectId);

                if (databaseUrl != null && !databaseUrl.trim().isEmpty()) {
                    builder.setDatabaseUrl(databaseUrl.trim());
                }

                FirebaseOptions options = builder.build();
                FirebaseApp app = FirebaseApp.initializeApp(options);
                this.firestore = FirestoreClient.getFirestore(app);
                this.initialized = true;
                this.statusMessage = "Connected to Live Firebase Firestore (Project: " + this.projectId + ")";
                logger.info(">>> Firebase Admin SDK successfully initialized! Project: {}, Client: {}", this.projectId, this.clientEmail);
                return true;
            } else {
                this.initialized = false;
                this.statusMessage = "Local Firebase Emulator Mode (serviceAccountKey.json not detected). All quiz data persists to data/ directory.";
                logger.warn(">>> No Firebase serviceAccountKey.json found. Running in Local Firestore Emulated Storage mode.");
                return false;
            }
        } catch (Exception e) {
            this.initialized = false;
            this.firestore = null;
            this.statusMessage = "Firebase initialization error: " + e.getMessage();
            logger.error("Failed to initialize Firebase Admin SDK: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Firestore getFirestore() {
        return firestore;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public String getStatusMessage() {
        return statusMessage;
    }
}
