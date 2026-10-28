package com.quizsystem.report;

import com.quizsystem.model.Attempt;
import java.util.List;

/**
 * CO-2: Interface defining common operations for report generation.
 * Demonstrates polymorphism for reporting and analytics summaries.
 */
public interface ReportGenerator {

    /**
     * Generates a formatted report from a collection of candidate exam attempts.
     */
    String generateReport(List<Attempt> attempts);

    /**
     * Identifies the format/type of the generated report.
     */
    String getReportFormat();
}
