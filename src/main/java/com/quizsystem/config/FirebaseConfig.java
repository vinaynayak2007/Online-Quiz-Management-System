package com.quizsystem.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.config.path:serviceAccountKey.json}")
    private String configPath;

    @Value("${firebase.database.url:}")
    private String databaseUrl;

    private boolean initialized = false;
    private String projectId = "quiz-system-demo";
    private Firestore firestore = null;
    private String statusMessage = "Using Local Firebase Storage & Emulator";

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

            InputStream serviceAccount = customStream;

            if (serviceAccount == null) {
                File file = new File(configPath);
                if (file.exists() && file.isFile()) {
                    serviceAccount = new FileInputStream(file);
                    logger.info("Found Firebase service account at path: {}", file.getAbsolutePath());
                } else {
                    String envPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
                    if (envPath != null && new File(envPath).exists()) {
                        serviceAccount = new FileInputStream(envPath);
                        logger.info("Found Firebase credentials from GOOGLE_APPLICATION_CREDENTIALS: {}", envPath);
                    } else {
                        InputStream cpStream = getClass().getClassLoader().getResourceAsStream("serviceAccountKey.json");
                        if (cpStream != null) {
                            serviceAccount = cpStream;
                            logger.info("Found Firebase credentials in classpath: serviceAccountKey.json");
                        }
                    }
                }
            }

            if (serviceAccount != null) {
                GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
                FirebaseOptions.Builder builder = FirebaseOptions.builder()
                        .setCredentials(credentials);

                if (databaseUrl != null && !databaseUrl.trim().isEmpty()) {
                    builder.setDatabaseUrl(databaseUrl.trim());
                }

                FirebaseOptions options = builder.build();
                FirebaseApp app = FirebaseApp.initializeApp(options);
                this.firestore = FirestoreClient.getFirestore(app);
                this.projectId = options.getProjectId() != null ? options.getProjectId() : "firebase-quiz-project";
                this.initialized = true;
                this.statusMessage = "Connected to Live Firebase Firestore (Project: " + this.projectId + ")";
                logger.info(">>> Firebase Admin SDK successfully initialized! Project: {}", this.projectId);
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

    public String getStatusMessage() {
        return statusMessage;
    }
}
