package com.zenika.zenikia.communication.application;

import java.util.Map;

/**
 * Context sent to a {@link CommunicationEvaluator}. Includes the
 * deterministic metrics already computed from the transcript so the LLM
 * can reference concrete numbers in its feedback (e.g. "'du coup' × 11 sur
 * 4 minutes") instead of vague judgments.
 */
public record CommunicationEvaluationContext(
        String personaName,
        String questionText,
        String answerTranscript,
        double durationSeconds,
        int wordCount,
        double wordsPerMinute,
        Map<String, Integer> fillerWords,
        int repetitionCount
) {
}
