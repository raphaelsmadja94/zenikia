package com.zenika.zenikia.interview.api;

import com.zenika.zenikia.communication.domain.CommunicationAssessment;

import java.util.List;
import java.util.Map;

/** API-facing view of a {@link CommunicationAssessment} — "la forme". */
public record CommunicationAssessmentResponse(
        double durationSeconds,
        int wordCount,
        double wordsPerMinute,
        Map<String, Integer> fillerWords,
        int totalFillerWordCount,
        int repetitionCount,
        int longPauseCount,
        int clarity,
        int structure,
        int concision,
        int fluency,
        int impact,
        int vulgarisation,
        int adaptationToPersona,
        double averageScore,
        List<String> strengths,
        List<String> weaknesses
) {
    public static CommunicationAssessmentResponse from(CommunicationAssessment ca) {
        return new CommunicationAssessmentResponse(
                ca.durationSeconds(), ca.wordCount(), ca.wordsPerMinute(), ca.fillerWords(), ca.totalFillerWordCount(),
                ca.repetitionCount(), ca.longPauseCount(), ca.clarity(), ca.structure(), ca.concision(), ca.fluency(),
                ca.impact(), ca.vulgarisation(), ca.adaptationToPersona(), ca.averageScore(), ca.strengths(), ca.weaknesses()
        );
    }
}
