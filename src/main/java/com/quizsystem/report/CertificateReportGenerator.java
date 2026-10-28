package com.quizsystem.report;

import com.quizsystem.model.Attempt;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CO-2: Polymorphic implementation of ReportGenerator for Certificate Verification.
 * CO-4: Uses Java Stream filter and collect to isolate passed candidates.
 */
@Component("certificateReportGenerator")
public class CertificateReportGenerator implements ReportGenerator {

    @Override
    public String generateReport(List<Attempt> attempts) {
        List<Attempt> attemptList = (attempts != null) ? new ArrayList<>(attempts) : new ArrayList<>();

        // CO-4: Stream filter passed candidates
        List<Attempt> passedAttempts = attemptList.stream()
                .filter(Attempt::isPassed)
                .collect(Collectors.toCollection(ArrayList::new));

        StringBuilder sb = new StringBuilder();
        sb.append("=====================================================\n");
        sb.append("       ACCREDITATION & CERTIFICATION ROSTER          \n");
        sb.append("=====================================================\n");
        sb.append("Eligible Certified Candidates: ").append(passedAttempts.size()).append("\n\n");

        for (Attempt a : passedAttempts) {
            sb.append("🎓 CERTIFICATE OF ACHIEVEMENT\n")
              .append("Candidate: ").append(a.getStudentName()).append("\n")
              .append("Examination: ").append(a.getQuizTitle()).append("\n")
              .append("Grade: ").append(a.getGrade()).append(" (").append(a.getPercentage()).append("%)\n")
              .append("Verification Token: ").append(a.getId()).append("\n")
              .append("-----------------------------------------------------\n");
        }

        return sb.toString();
    }

    @Override
    public String getReportFormat() {
        return "CERTIFICATE_ACCREDITATION_ROSTER";
    }
}
