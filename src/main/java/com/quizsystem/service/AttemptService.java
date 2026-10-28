package com.quizsystem.service;

import com.quizsystem.concurrency.QuizTimerManager;
import com.quizsystem.concurrency.SubmissionSyncManager;
import com.quizsystem.evaluator.QuizEvaluator;
import com.quizsystem.exception.InvalidInputException;
import com.quizsystem.exception.QuizExpiredException;
import com.quizsystem.model.*;
import com.quizsystem.report.ReportGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service orchestrating quiz attempts, evaluations, and performance analytics.
 * CO-2: Demonstrates Polymorphism through QuizEvaluator and ReportGenerator interfaces.
 * CO-3: Demonstrates Exception Handling, Timer Expiry checks, and Synchronized duplicate prevention.
 * CO-4: Demonstrates extensive ArrayList collection usage and Java Stream processing.
 */
@Service
public class AttemptService {
    private static final Logger logger = LoggerFactory.getLogger(AttemptService.class);

    private final FirebaseService firebaseService;
    private final UserService userService;
    private final QuizEvaluator quizEvaluator;
    private final ReportGenerator reportGenerator;
    private final QuizTimerManager quizTimerManager;
    private final SubmissionSyncManager submissionSyncManager;

    @Autowired
    public AttemptService(
            FirebaseService firebaseService,
            UserService userService,
            @Qualifier("standardEvaluator") QuizEvaluator quizEvaluator,
            @Qualifier("performanceReportGenerator") ReportGenerator reportGenerator,
            QuizTimerManager quizTimerManager,
            SubmissionSyncManager submissionSyncManager
    ) {
        this.firebaseService = firebaseService;
        this.userService = userService;
        this.quizEvaluator = quizEvaluator;
        this.reportGenerator = reportGenerator;
        this.quizTimerManager = quizTimerManager;
        this.submissionSyncManager = submissionSyncManager;
    }

    /**
     * Evaluates and records candidate submission.
     * Incorporates CO-2 (Polymorphic evaluator), CO-3 (Concurrency, Timer, Exceptions), CO-4 (Streams).
     */
    public QuizResult evaluateAndSubmit(String quizId, QuizSubmission submission) {
        if (quizId == null || quizId.trim().isEmpty()) {
            throw new InvalidInputException("Quiz ID cannot be null or blank");
        }
        if (submission == null) {
            throw new InvalidInputException("Quiz submission payload cannot be null");
        }

        // CO-3: Duplicate Submission Prevention using synchronized locking
        String studentPhone = (submission.getStudentEmail() != null && !submission.getStudentEmail().trim().isEmpty())
                ? submission.getStudentEmail().trim()
                : "guest";
        String submissionFingerprint = quizId + "_" + studentPhone + "_" + (submission.getAnswers() != null ? submission.getAnswers().size() : 0);

        submissionSyncManager.acquireSubmissionPermit(submissionFingerprint);

        try {
            // Retrieve quiz
            Optional<Quiz> quizOpt = firebaseService.getQuizById(quizId);
            if (quizOpt.isEmpty()) {
                throw new InvalidInputException("Examination with ID '" + quizId + "' does not exist");
            }
            Quiz quiz = quizOpt.get();

            // CO-3: Timer Expiry Verification
            String sessionKey = studentPhone + "_" + quizId;
            if (quizTimerManager.isSessionExpired(sessionKey)) {
                logger.warn("CO-3: Candidate submission rejected because exam timer has expired for session: {}", sessionKey);
                throw new QuizExpiredException("The allocated time limit for this examination has expired. Late submissions cannot be processed.");
            }

            // CO-2: Polymorphic evaluation via QuizEvaluator interface
            logger.info("Evaluating quiz using strategy: {}", quizEvaluator.getStrategyName());
            QuizResult result = quizEvaluator.evaluate(quiz, submission);

            // Create Attempt Record
            Attempt attempt = new Attempt(
                    result.getAttemptId(),
                    quiz.getId(),
                    quiz.getTitle(),
                    quiz.getCategory(),
                    result.getStudentName(),
                    result.getStudentEmail(),
                    result.getScore(),
                    result.getTotalPoints(),
                    result.getPercentage(),
                    result.getGrade(),
                    result.isPassed(),
                    submission.getTimeSpentSeconds()
            );
            attempt.setAnswers(submission.getAnswers());

            // Save Attempt
            firebaseService.saveAttempt(attempt);

            // Update candidate record in UserService
            userService.recordExamResult(studentPhone, result.isPassed());

            // Finalize session & unlock
            quizTimerManager.completeSession(sessionKey);
            submissionSyncManager.finalizeSubmission(submissionFingerprint);

            return result;
        } catch (Exception e) {
            submissionSyncManager.releaseInFlight(submissionFingerprint);
            throw e;
        }
    }

    /**
     * CO-4: Uses ArrayList and Stream operations to filter and return all attempts.
     */
    public List<Attempt> getAllAttempts() {
        List<Attempt> rawAttempts = firebaseService.getAllAttempts();
        // CO-4: Stream processing to sort and collect into ArrayList
        return rawAttempts.stream()
                .sorted(Comparator.comparingLong(Attempt::getTimestamp).reversed())
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * CO-4: Uses Java Stream operations to filter by quiz, sort by percentage descending, and limit.
     */
    public List<Attempt> getLeaderboard(String quizId, int limit) {
        List<Attempt> allAttempts = getAllAttempts();
        return allAttempts.stream()
                .filter(a -> quizId == null || quizId.isEmpty() || quizId.equalsIgnoreCase("all") || quizId.equals(a.getQuizId()))
                .sorted(Comparator.comparingDouble(Attempt::getPercentage).reversed()
                        .thenComparingInt(Attempt::getTimeSpentSeconds))
                .limit(limit > 0 ? limit : 20)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * CO-4: Uses Java Stream operations (filter, average, count, groupingBy) to compute analytics summary.
     */
    public Map<String, Object> getAnalyticsSummary() {
        List<Quiz> quizzes = firebaseService.getAllQuizzes();
        List<Attempt> attempts = getAllAttempts();

        int totalQuizzes = quizzes.size();
        // CO-4: Stream mapToInt and sum
        int totalQuestions = quizzes.stream().mapToInt(Quiz::getTotalQuestions).sum();
        int totalAttempts = attempts.size();

        // CO-4: Stream mapToDouble and average
        double avgScore = attempts.isEmpty() ? 0.0 :
                attempts.stream().mapToDouble(Attempt::getPercentage).average().orElse(0.0);
        avgScore = Math.round(avgScore * 100.0) / 100.0;

        // CO-4: Stream filter passed candidates
        long passedCount = attempts.stream().filter(Attempt::isPassed).count();
        double passRate = attempts.isEmpty() ? 0.0 : ((double) passedCount / attempts.size()) * 100.0;
        passRate = Math.round(passRate * 100.0) / 100.0;

        // CO-4: Stream groupingBy category
        Map<String, Long> categoryCount = quizzes.stream()
                .filter(q -> q.getCategory() != null)
                .collect(Collectors.groupingBy(Quiz::getCategory, Collectors.counting()));

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalQuizzes", totalQuizzes);
        summary.put("totalQuestions", totalQuestions);
        summary.put("totalAttempts", totalAttempts);
        summary.put("passedCount", passedCount);
        summary.put("failedCount", totalAttempts - passedCount);
        summary.put("averageScore", avgScore);
        summary.put("passRate", passRate);
        summary.put("categoryBreakdown", categoryCount);

        return summary;
    }

    /**
     * CO-2: Polymorphic report generation using ReportGenerator interface.
     */
    public String generatePerformanceTextReport() {
        return reportGenerator.generateReport(getAllAttempts());
    }
}
