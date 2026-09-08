package com.zenika.zenikia.interview.api;

import com.zenika.zenikia.coaching.domain.FinalReport;

import java.util.List;

/**
 * API-facing view of a {@link FinalReport}. Always exposes technical,
 * communication and interview-effectiveness as three separate figures
 * (spec §16) — the frontend must never collapse them into one score.
 */
public record FinalReportResponse(
        double technicalMastery,
        double communication,
        double interviewEffectiveness,
        List<String> topStrengths,
        List<String> topPriorities,
        List<String> recommendedNextSteps,
        String disclaimer
) {
    public static FinalReportResponse from(FinalReport report) {
        return new FinalReportResponse(
                report.scores().technicalMastery(),
                report.scores().communication(),
                report.scores().interviewEffectiveness(),
                report.topStrengths(),
                report.topPriorities(),
                report.recommendedNextSteps(),
                report.disclaimer()
        );
    }
}
