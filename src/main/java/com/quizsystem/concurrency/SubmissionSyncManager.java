package com.quizsystem.concurrency;

import com.quizsystem.exception.DuplicateSubmissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CO-3: Use synchronization to manage quiz submissions and prevent duplicate submissions.
 * Thread-safe synchronization manager ensuring single submission per candidate session.
 */
@Component
public class SubmissionSyncManager {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionSyncManager.class);

    // Set storing processed submission fingerprints to detect duplicates
    private final Set<String> processedSubmissions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    // Set storing in-progress submissions to guard against concurrent duplicate requests
    private final Set<String> inFlightSubmissions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private final Object syncLock = new Object();

    /**
     * Synchronized validation of candidate submission token.
     * Throws DuplicateSubmissionException if a duplicate submission is detected.
     */
    public void acquireSubmissionPermit(String submissionKey) {
        synchronized (syncLock) {
            if (processedSubmissions.contains(submissionKey)) {
                logger.warn("CO-3 Synchronization: Duplicate submission blocked for key: {}", submissionKey);
                throw new DuplicateSubmissionException("Duplicate submission detected! This exam has already been submitted and processed.");
            }

            if (inFlightSubmissions.contains(submissionKey)) {
                logger.warn("CO-3 Synchronization: Concurrent duplicate submission in flight for key: {}", submissionKey);
                throw new DuplicateSubmissionException("An identical submission is currently being evaluated. Duplicate ignored.");
            }

            // Mark as in-flight
            inFlightSubmissions.add(submissionKey);
        }
    }

    /**
     * Marks the submission as completed and permanently stored.
     */
    public void finalizeSubmission(String submissionKey) {
        synchronized (syncLock) {
            inFlightSubmissions.remove(submissionKey);
            processedSubmissions.add(submissionKey);
            logger.info("CO-3 Synchronization: Finalized submission for key: {}", submissionKey);
        }
    }

    /**
     * Releases lock in case of an error during processing.
     */
    public void releaseInFlight(String submissionKey) {
        synchronized (syncLock) {
            inFlightSubmissions.remove(submissionKey);
        }
    }
}
