package com.quizsystem.concurrency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.*;

/**
 * CO-3: Use a separate timer thread and synchronization to manage quiz expiry.
 * Runs a dedicated background daemon thread checking and enforcing exam time limits.
 */
@Component
public class QuizTimerManager {
    private static final Logger logger = LoggerFactory.getLogger(QuizTimerManager.class);

    // Map storing active candidate sessions: sessionKey -> expiryTimestamp (epoch millis)
    private final Map<String, Long> activeSessionExpiryMap = new ConcurrentHashMap<>();

    // Map storing expired sessions
    private final Map<String, Boolean> expiredSessionsMap = new ConcurrentHashMap<>();

    // Dedicated timer background thread executor
    private ScheduledExecutorService timerThreadPool;

    @PostConstruct
    public void startTimerThread() {
        // Dedicated single-thread executor with custom named thread factory
        timerThreadPool = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            private int counter = 0;
            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r, "Quiz-Timer-Thread-" + (++counter));
                t.setDaemon(true); // Daemon thread stops when application terminates
                return t;
            }
        });

        // Background thread runs every 1 second to inspect active sessions for expiry
        timerThreadPool.scheduleAtFixedRate(this::checkSessionExpirations, 1, 1, TimeUnit.SECONDS);
        logger.info(">>> CO-3: Background Quiz Timer Thread started successfully.");
    }

    @PreDestroy
    public void stopTimerThread() {
        if (timerThreadPool != null && !timerThreadPool.isShutdown()) {
            timerThreadPool.shutdownNow();
            logger.info("Quiz Timer Thread stopped.");
        }
    }

    /**
     * Registers a new candidate exam session with an allocated duration in minutes.
     */
    public synchronized void registerSession(String sessionKey, int durationMinutes) {
        long bufferSeconds = 30; // 30 seconds grace buffer for network latency
        long totalMillis = ((long) durationMinutes * 60 + bufferSeconds) * 1000;
        long expiryTime = System.currentTimeMillis() + totalMillis;

        activeSessionExpiryMap.put(sessionKey, expiryTime);
        expiredSessionsMap.remove(sessionKey);
        logger.info("Session {} registered. Timer thread monitoring until {} ({} mins)", sessionKey, expiryTime, durationMinutes);
    }

    /**
     * Executed by the separate timer thread every second to check for expirations.
     */
    private void checkSessionExpirations() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> entry : activeSessionExpiryMap.entrySet()) {
            String sessionKey = entry.getKey();
            long expiry = entry.getValue();

            if (now > expiry) {
                // Synchronized transition to expired state
                synchronized (this) {
                    if (activeSessionExpiryMap.containsKey(sessionKey)) {
                        activeSessionExpiryMap.remove(sessionKey);
                        expiredSessionsMap.put(sessionKey, true);
                        logger.warn("CO-3 Timer Thread: Session {} has EXPIRED automatically!", sessionKey);
                    }
                }
            }
        }
    }

    /**
     * Checks if a session is expired.
     */
    public synchronized boolean isSessionExpired(String sessionKey) {
        if (expiredSessionsMap.getOrDefault(sessionKey, false)) {
            return true;
        }
        Long expiry = activeSessionExpiryMap.get(sessionKey);
        if (expiry != null && System.currentTimeMillis() > expiry) {
            expiredSessionsMap.put(sessionKey, true);
            activeSessionExpiryMap.remove(sessionKey);
            return true;
        }
        return false;
    }

    /**
     * Completes and deregisters session once submitted.
     */
    public synchronized void completeSession(String sessionKey) {
        activeSessionExpiryMap.remove(sessionKey);
        expiredSessionsMap.remove(sessionKey);
    }
}
