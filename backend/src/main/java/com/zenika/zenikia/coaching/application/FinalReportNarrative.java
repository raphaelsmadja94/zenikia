package com.zenika.zenikia.coaching.application;

import java.util.List;

/** The narrative parts of the final report — the only parts an LLM produces (see {@link FinalReportGenerator}). */
public record FinalReportNarrative(
        List<String> topStrengths,
        List<String> topPriorities,
        List<String> recommendedNextSteps
) {
}
