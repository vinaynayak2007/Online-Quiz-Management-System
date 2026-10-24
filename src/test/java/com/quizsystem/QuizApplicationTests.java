package com.quizsystem;

import com.quizsystem.model.*;
import com.quizsystem.service.AttemptService;
import com.quizsystem.service.FirebaseService;
import com.quizsystem.service.QuizService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class QuizApplicationTests {

    @Autowired
    private QuizService quizService;

    @Autowired
    private AttemptService attemptService;

    @Autowired
    private FirebaseService firebaseService;

    @Test
    void contextLoads() {
        assertNotNull(quizService);
        assertNotNull(attemptService);
        assertNotNull(firebaseService);
    }

    @Test
    void testQuizRetrievalAndSanitization() {
        List<Quiz> quizzes = quizService.getPublicQuizzes(null, null, null);
        assertFalse(quizzes.isEmpty(), "Should have seeded quizzes available");

        Quiz first = quizzes.get(0);
        assertNotNull(first.getId());
        assertNotNull(first.getTitle());

        // Ensure public quizzes sanitize and hide correct answers from student view
        if (!first.getQuestions().isEmpty()) {
            Question q = first.getQuestions().get(0);
            assertTrue(q.getCorrectAnswers().isEmpty(), "Public quiz question must not leak correct answers");
            assertNull(q.getExplanation(), "Public quiz question must not leak explanation before submit");
        }
    }

    @Test
    void testEvaluationEngineAndScoring() {
        List<Quiz> adminQuizzes = quizService.getAllQuizzesForAdmin();
        assertFalse(adminQuizzes.isEmpty());
        Quiz targetQuiz = adminQuizzes.get(0);

        Question q1 = targetQuiz.getQuestions().get(0);

        // Submit correct answer
        QuizSubmission submission = new QuizSubmission();
        submission.setStudentName("Test Student");
        submission.setStudentEmail("student@test.com");
        submission.setTimeSpentSeconds(45);
        submission.setAnswers(List.of(
                new StudentAnswer(q1.getId(), q1.getCorrectAnswers())
        ));

        QuizResult result = attemptService.evaluateAndSubmit(targetQuiz.getId(), submission);
        assertNotNull(result);
        assertEquals(targetQuiz.getId(), result.getQuizId());
        assertTrue(result.getScore() >= q1.getPoints());
        assertEquals("Test Student", result.getStudentName());
        assertFalse(result.getEvaluations().isEmpty());
    }

    @Test
    void testFirebaseStatus() {
        FirebaseStatus status = firebaseService.getStatus();
        assertNotNull(status);
        assertNotNull(status.getMode());
        assertTrue(status.getTotalQuizzes() > 0);
    }
}
