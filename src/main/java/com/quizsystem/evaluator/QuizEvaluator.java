package com.quizsystem.evaluator;

import com.quizsystem.model.Quiz;
import com.quizsystem.model.QuizResult;
import com.quizsystem.model.QuizSubmission;

/**
 * CO-2: Interface defining common operations for quiz evaluation.
 * Demonstrates abstraction and polymorphism across multiple evaluation strategies.
 */
public interface QuizEvaluator {

    /**
     * Polymorphic method to evaluate candidate submissions against quiz answer keys.
     */
    QuizResult evaluate(Quiz quiz, QuizSubmission submission);

    /**
     * Returns the name of the evaluation strategy.
     */
    String getStrategyName();
}
