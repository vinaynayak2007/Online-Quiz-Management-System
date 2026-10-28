package com.quizsystem.report;

import com.quizsystem.model.Attempt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CO-2: Polymorphic implementation of ReportGenerator for executive performance summaries.
 * CO-4: Uses Java Stream operations (filter, mapToDouble, average, groupingBy, partitioningBy)
 * to process scores and generate performance summaries.
 */
@Component("performanceReportGenerator")
public class PerformanceSummaryReportGenerator implements ReportGenerator {

    @Override
    public String generateReport(List<Attempt> attempts) {
        // CO-4: Ensure ArrayList collection usage
        List<Attempt> attemptList = (attempts != null) ? new ArrayList<>(attempts) : new ArrayList<>();

        if (attemptList.isEmpty()) {
            return "No examination attempts recorded yet.";
        }

        // CO-4 Stream 1: Filter & Count passed candidates
        long totalAttempts = attemptList.size();
        long passedCount = attemptList.stream()
                .filter(Attempt::isPassed)
                .count();

        long failedCount = totalAttempts - passedCount;

        // CO-4 Stream 2: Map to Double and Compute Average Score
        double averageScore = attemptList.stream()
                .mapToDouble(Attempt::getPercentage)
                .average()
                .orElse(0.0);
        averageScore = Math.round(averageScore * 10.0) / 10.0;

        // CO-4 Stream 3: Partitioning by Pass/Fail
        Map<Boolean, List<Attempt>> partitioned = attemptList.stream()
                .collect(Collectors.partitioningBy(Attempt::isPassed));

        // CO-4 Stream 4: Grouping by Exam Title to calculate attempts per quiz
        Map<String, Long> attemptsPerQuiz = attemptList.stream()
                .collect(Collectors.groupingBy(
                        a -> (a.getQuizTitle() != null ? a.getQuizTitle() : "General"),
                        Collectors.counting()
                ));

        double passRate = totalAttempts > 0 ? ((double) passedCount / totalAttempts) * 100.0 : 0.0;
        passRate = Math.round(passRate * 10.0) / 10.0;

        StringBuilder sb = new StringBuilder();
        sb.append("=====================================================\n");
        sb.append("    ACADEMIC EXAMINATION PERFORMANCE SUMMARY REPORT   \n");
        sb.append("=====================================================\n");
        sb.append("Total Examinations Submitted : ").append(totalAttempts).append("\n");
        sb.append("Candidates Passed            : ").append(passedCount).append("\n");
        sb.append("Candidates Failed            : ").append(failedCount).append("\n");
        sb.append("Overall Pass Rate            : ").append(passRate).append("%\n");
        sb.append("Overall Class Average Score  : ").append(averageScore).append("%\n");
        sb.append("-----------------------------------------------------\n");
        sb.append("Submissions by Examination Track:\n");
        attemptsPerQuiz.forEach((title, count) ->
                sb.append(" • ").append(title).append(": ").append(count).append(" attempts\n")
        );
        sb.append("=====================================================\n");

        return sb.toString();
    }

    @Override
    public String getReportFormat() {
        return "TEXT_EXECUTIVE_SUMMARY";
    }
}
