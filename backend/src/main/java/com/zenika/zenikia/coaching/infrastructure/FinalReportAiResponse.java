package com.zenika.zenikia.coaching.infrastructure;

import java.util.List;

/** Structured output shape requested from the LLM, mirroring {@code FinalReportNarrative} 1:1. */
record FinalReportAiResponse(
        List<String> topStrengths,
        List<String> topPriorities,
        List<String> recommendedNextSteps
) {
}
