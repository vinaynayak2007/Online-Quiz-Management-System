package com.quizsystem.evaluator;

import com.quizsystem.model.*;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * CO-2: Secondary implementation of QuizEvaluator demonstrating Polymorphism.
 * Implements strict competitive exam evaluation with negative marking penalties for wrong answers.
 */
@Component("strictPenaltyEvaluator")
public class StrictPenaltyQuizEvaluator implements QuizEvaluator {

    private final StandardQuizEvaluator delegate = new StandardQuizEvaluator();

    @Override
    public QuizResult evaluate(Quiz quiz, QuizSubmission submission) {
        QuizResult baseResult = delegate.evaluate(quiz, submission);

        // Strict mode applies a 2-point penalty for each incorrect answer
        int penaltyPerIncorrect = 2;
        int penaltyTotal = baseResult.getIncorrectCount() * penaltyPerIncorrect;
        int adjustedScore = Math.max(0, baseResult.getScore() - penaltyTotal);

        double adjustedPercentage = baseResult.getTotalPoints() > 0
                ? ((double) adjustedScore / baseResult.getTotalPoints()) * 100.0
                : 0.0;
        adjustedPercentage = Math.round(adjustedPercentage * 100.0) / 100.0;

        baseResult.setScore(adjustedScore);
        baseResult.setPercentage(adjustedPercentage);
        baseResult.setPassed(adjustedPercentage >= quiz.getPassingScorePercent());

        return baseResult;
    }

    @Override
    public String getStrategyName() {
        return "STRICT_PENALTY_EVALUATOR";
    }
}
