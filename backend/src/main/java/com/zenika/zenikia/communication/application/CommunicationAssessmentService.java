package com.zenika.zenikia.communication.application;

import com.zenika.zenikia.communication.domain.CommunicationAssessment;
import com.zenika.zenikia.communication.domain.FillerWordCatalog;
import com.zenika.zenikia.communication.domain.TranscriptMetricsCalculator;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Orchestrates the two halves of a communication assessment: deterministic
 * transcript/audio metrics (this service, no LLM involved) and rubric-based
 * judgment (delegated to {@link CommunicationEvaluator}). Never mixes the
 * two into a single opaque score.
 */
@Service
public class CommunicationAssessmentService {

    private final CommunicationEvaluator communicationEvaluator;

    public CommunicationAssessmentService(CommunicationEvaluator communicationEvaluator) {
        this.communicationEvaluator = communicationEvaluator;
    }

    public CommunicationAssessment assess(String personaName, String questionText, String transcript, double durationSeconds) {
        int wordCount = TranscriptMetricsCalculator.countWords(transcript);
        double wordsPerMinute = TranscriptMetricsCalculator.wordsPerMinute(wordCount, durationSeconds);
        Map<String, Integer> fillerWords = TranscriptMetricsCalculator.detectFillerWords(transcript, FillerWordCatalog.DEFAULT_FRENCH_FILLERS);
        int repetitionCount = TranscriptMetricsCalculator.countImmediateRepetitions(transcript);

        CommunicationEvaluationContext context = new CommunicationEvaluationContext(
                personaName, questionText, transcript, durationSeconds, wordCount, wordsPerMinute, fillerWords, repetitionCount
        );
        CommunicationRubricScores rubric = communicationEvaluator.evaluate(context);

        return new CommunicationAssessment(
                durationSeconds,
                wordCount,
                wordsPerMinute,
                fillerWords,
                repetitionCount,
                0, // longPauseCount: not reliably measurable in the POC, see CommunicationAssessment javadoc
                rubric.clarity(),
                rubric.structure(),
                rubric.concision(),
                rubric.fluency(),
                rubric.impact(),
                rubric.vulgarisation(),
                rubric.adaptationToPersona(),
                rubric.strengths(),
                rubric.weaknesses()
        );
    }
}
