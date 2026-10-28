package com.quizsystem.evaluator;

import com.quizsystem.model.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * CO-2: Standard implementation of QuizEvaluator demonstrating Polymorphism.
 * CO-4: Uses ArrayList to store question evaluations and candidate responses.
 */
@Component("standardEvaluator")
public class StandardQuizEvaluator implements QuizEvaluator {

    @Override
    public QuizResult evaluate(Quiz quiz, QuizSubmission submission) {
        // CO-4: ArrayList for questions and evaluations
        List<Question> questions = quiz.getQuestions() != null ? quiz.getQuestions() : new ArrayList<>();
        List<QuestionEvaluation> evaluations = new ArrayList<>();

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

        for (Question q : questions) {
            totalPoints += q.getPoints();
            List<Integer> selected = submittedAnswersMap.get(q.getId());
            List<Integer> correct = q.getCorrectAnswers() != null ? q.getCorrectAnswers() : new ArrayList<>();

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
            eval.setSelectedOptions(selected != null ? new ArrayList<>(selected) : new ArrayList<>());
            eval.setCorrectAnswers(new ArrayList<>(correct));
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
        String studentName = (submission.getStudentName() != null && !submission.getStudentName().trim().isEmpty())
                ? submission.getStudentName().trim()
                : "Candidate";
        String studentEmail = submission.getStudentEmail() != null ? submission.getStudentEmail().trim() : "";

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

    private String calculateGrade(double percentage) {
        if (percentage >= 90.0) return "A+";
        if (percentage >= 80.0) return "A";
        if (percentage >= 70.0) return "B";
        if (percentage >= 60.0) return "C";
        if (percentage >= 50.0) return "D";
        return "F";
    }

    @Override
    public String getStrategyName() {
        return "STANDARD_LINEAR_EVALUATOR";
    }
}
