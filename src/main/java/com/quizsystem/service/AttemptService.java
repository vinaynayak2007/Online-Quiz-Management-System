package com.quizsystem.service;

import com.quizsystem.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AttemptService {

    private final FirebaseService firebaseService;

    @Autowired
    public AttemptService(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    public QuizResult evaluateAndSubmit(String quizId, QuizSubmission submission) {
        Optional<Quiz> quizOpt = firebaseService.getQuizById(quizId);
        if (quizOpt.isEmpty()) {
            throw new IllegalArgumentException("Quiz with ID " + quizId + " not found");
        }

        Quiz quiz = quizOpt.get();
        List<Question> questions = quiz.getQuestions() != null ? quiz.getQuestions() : Collections.emptyList();

        Map<String, List<Integer>> submittedAnswersMap = new HashMap<>();
        if (submission.getAnswers() != null) {
            for (StudentAnswer sa : submission.getAnswers()) {
                if (sa.getQuestionId() != null) {
                    submittedAnswersMap.put(sa.getQuestionId(), sa.getSelectedOptions());
                }
            }
        }

        int score = 0;
        int totalPoints = 0;
        int correctCount = 0;
        int incorrectCount = 0;
        int skippedCount = 0;

        List<QuestionEvaluation> evaluations = new ArrayList<>();

        for (Question q : questions) {
            totalPoints += q.getPoints();
            List<Integer> selected = submittedAnswersMap.get(q.getId());
            List<Integer> correct = q.getCorrectAnswers() != null ? q.getCorrectAnswers() : Collections.emptyList();

            boolean isSkipped = (selected == null || selected.isEmpty());
            boolean isCorrect = false;

            if (!isSkipped) {
                Set<Integer> selSet = new TreeSet<>(selected);
                Set<Integer> corSet = new TreeSet<>(correct);
                isCorrect = selSet.equals(corSet);
            }

            int pointsEarned = isCorrect ? q.getPoints() : 0;
            score += pointsEarned;

            if (isSkipped) {
                skippedCount++;
            } else if (isCorrect) {
                correctCount++;
            } else {
                incorrectCount++;
            }

            QuestionEvaluation eval = new QuestionEvaluation();
            eval.setQuestionId(q.getId());
            eval.setQuestionText(q.getText());
            eval.setType(q.getType());
            eval.setOptions(q.getOptions());
            eval.setSelectedOptions(selected != null ? selected : new ArrayList<>());
            eval.setCorrectAnswers(correct);
            eval.setCorrect(isCorrect);
            eval.setSkipped(isSkipped);
            eval.setPointsEarned(pointsEarned);
            eval.setPointsPossible(q.getPoints());
            eval.setExplanation(q.getExplanation());

            evaluations.add(eval);
        }

        double percentage = totalPoints > 0 ? ((double) score / totalPoints) * 100.0 : 0.0;
        percentage = Math.round(percentage * 100.0) / 100.0;

        boolean passed = percentage >= quiz.getPassingScorePercent();
        String grade = calculateGrade(percentage);

        String attemptId = "att-" + UUID.randomUUID().toString().substring(0, 8);
        String studentName = submission.getStudentName() != null && !submission.getStudentName().trim().isEmpty()
                ? submission.getStudentName().trim()
                : "Anonymous Student";
        String studentEmail = submission.getStudentEmail() != null ? submission.getStudentEmail().trim() : "";

        Attempt attempt = new Attempt(
                attemptId,
                quiz.getId(),
                quiz.getTitle(),
                quiz.getCategory(),
                studentName,
                studentEmail,
                score,
                totalPoints,
                percentage,
                grade,
                passed,
                submission.getTimeSpentSeconds()
        );
        attempt.setAnswers(submission.getAnswers());
        firebaseService.saveAttempt(attempt);

        QuizResult result = new QuizResult();
        result.setAttemptId(attemptId);
        result.setQuizId(quiz.getId());
        result.setQuizTitle(quiz.getTitle());
        result.setCategory(quiz.getCategory());
        result.setStudentName(studentName);
        result.setStudentEmail(studentEmail);
        result.setScore(score);
        result.setTotalPoints(totalPoints);
        result.setPercentage(percentage);
        result.setGrade(grade);
        result.setPassed(passed);
        result.setPassingScorePercent(quiz.getPassingScorePercent());
        result.setTimeSpentSeconds(submission.getTimeSpentSeconds());
        result.setTotalQuestions(questions.size());
        result.setCorrectCount(correctCount);
        result.setIncorrectCount(incorrectCount);
        result.setSkippedCount(skippedCount);
        result.setCompletedAt(System.currentTimeMillis());
        result.setEvaluations(evaluations);

        return result;
    }

    public List<Attempt> getAllAttempts() {
        return firebaseService.getAllAttempts().stream()
                .sorted(Comparator.comparingLong(Attempt::getTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<Attempt> getLeaderboard(String quizId, int limit) {
        return firebaseService.getLeaderboard(quizId, limit);
    }

    public Map<String, Object> getAnalyticsSummary() {
        List<Quiz> quizzes = firebaseService.getAllQuizzes();
        List<Attempt> attempts = firebaseService.getAllAttempts();

        int totalQuizzes = quizzes.size();
        int totalQuestions = quizzes.stream().mapToInt(Quiz::getTotalQuestions).sum();
        int totalAttempts = attempts.size();

        double avgScore = attempts.isEmpty() ? 0.0 :
                attempts.stream().mapToDouble(Attempt::getPercentage).average().orElse(0.0);
        avgScore = Math.round(avgScore * 100.0) / 100.0;

        long passedCount = attempts.stream().filter(Attempt::isPassed).count();
        double passRate = attempts.isEmpty() ? 0.0 : ((double) passedCount / attempts.size()) * 100.0;
        passRate = Math.round(passRate * 100.0) / 100.0;

        Map<String, Long> categoryCount = quizzes.stream()
                .filter(q -> q.getCategory() != null)
                .collect(Collectors.groupingBy(Quiz::getCategory, Collectors.counting()));

        Map<String, Object> summary = new HashMap<>();
        summary.put("totalQuizzes", totalQuizzes);
        summary.put("totalQuestions", totalQuestions);
        summary.put("totalAttempts", totalAttempts);
        summary.put("averageScore", avgScore);
        summary.put("passRate", passRate);
        summary.put("categoryBreakdown", categoryCount);
        return summary;
    }

    private String calculateGrade(double percentage) {
        if (percentage >= 90.0) return "A+";
        if (percentage >= 80.0) return "A";
        if (percentage >= 70.0) return "B";
        if (percentage >= 60.0) return "C";
        if (percentage >= 50.0) return "D";
        return "F";
    }
}
