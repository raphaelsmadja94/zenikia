package com.zenika.zenikia.assessment.infrastructure;

import java.util.List;

/** Structured output shape requested from the LLM, mirroring {@code TechnicalAssessment} 1:1. */
record TechnicalAssessmentAiResponse(
        int correctness,
        int depth,
        int reasoning,
        int realWorldExperience,
        int examples,
        int tradeOffThinking,
        int productionAwareness,
        List<String> strengths,
        List<String> weaknesses,
        List<String> missingConcepts
) {
}
