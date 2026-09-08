package com.zenika.zenikia.interview.api;

import com.zenika.zenikia.assessment.domain.TechnicalAssessment;

import java.util.List;

/** API-facing view of a {@link TechnicalAssessment} — "le fond". */
public record TechnicalAssessmentResponse(
        int correctness,
        int depth,
        int reasoning,
        int realWorldExperience,
        int examples,
        int tradeOffThinking,
        int productionAwareness,
        double averageScore,
        List<String> strengths,
        List<String> weaknesses,
        List<String> missingConcepts
) {
    public static TechnicalAssessmentResponse from(TechnicalAssessment ta) {
        return new TechnicalAssessmentResponse(
                ta.correctness(), ta.depth(), ta.reasoning(), ta.realWorldExperience(), ta.examples(),
                ta.tradeOffThinking(), ta.productionAwareness(), ta.averageScore(),
                ta.strengths(), ta.weaknesses(), ta.missingConcepts()
        );
    }
}
