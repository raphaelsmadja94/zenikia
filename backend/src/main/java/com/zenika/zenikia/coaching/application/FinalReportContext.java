package com.zenika.zenikia.coaching.application;

import java.util.List;

/**
 * Aggregated, session-wide data handed to {@link FinalReportGenerator}.
 * Built by {@code interview.application} from every assessed turn — the
 * raw numeric scores are NOT recomputed by the LLM (see
 * {@code coaching.domain.FinalReportScoring}); only the narrative parts are.
 */
public record FinalReportContext(
        String personaName,
        String candidateRole,
        int totalQuestions,
        double averageTechnicalScore,
        double averageCommunicationScore,
        List<String> allStrengths,
        List<String> allWeaknesses,
        List<String> allMissingConcepts,
        int totalFillerWordOccurrences,
        double averageWordsPerMinute
) {
}
