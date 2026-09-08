package com.zenika.zenikia.communication.domain;

import java.util.List;
import java.util.Map;

/**
 * Evaluation of the FORM ("la forme") of one answer — never confused with
 * technical competence. Two families of fields:
 *
 * <ul>
 *   <li><b>Deterministic, transcript-based metrics</b> (duration*, wordCount,
 *       wordsPerMinute, fillerWords, repetitionCount): computed by rules,
 *       never by an LLM — they are counts, not judgments.</li>
 *   <li><b>Rubric-based, LLM-judged scores</b> (clarity, structure,
 *       concision, fluency, impact, vulgarisation, adaptationToPersona):
 *       bounded 0-5, produced against an explicit rubric.</li>
 * </ul>
 *
 * {@code longPauseCount} is always 0 in the POC: reliable pause detection
 * would require word/segment-level audio timestamps that are not captured
 * by this integration (see CLAUDE.md §"transcript et audio sont
 * différents" / README "Current limitations"). The field is kept so the
 * architecture is ready for a richer audio pipeline later, without
 * pretending to measure something we don't.
 */
public record CommunicationAssessment(
        double durationSeconds,
        int wordCount,
        double wordsPerMinute,
        Map<String, Integer> fillerWords,
        int repetitionCount,
        int longPauseCount,
        int clarity,
        int structure,
        int concision,
        int fluency,
        int impact,
        int vulgarisation,
        int adaptationToPersona,
        List<String> strengths,
        List<String> weaknesses
) {
    public static final int MIN_SCORE = 0;
    public static final int MAX_SCORE = 5;

    public CommunicationAssessment {
        durationSeconds = Math.max(0, durationSeconds);
        wordCount = Math.max(0, wordCount);
        wordsPerMinute = Math.max(0, wordsPerMinute);
        fillerWords = fillerWords == null ? Map.of() : Map.copyOf(fillerWords);
        repetitionCount = Math.max(0, repetitionCount);
        longPauseCount = Math.max(0, longPauseCount);
        clarity = clamp(clarity);
        structure = clamp(structure);
        concision = clamp(concision);
        fluency = clamp(fluency);
        impact = clamp(impact);
        vulgarisation = clamp(vulgarisation);
        adaptationToPersona = clamp(adaptationToPersona);
        strengths = strengths == null ? List.of() : List.copyOf(strengths);
        weaknesses = weaknesses == null ? List.of() : List.copyOf(weaknesses);
    }

    private static int clamp(int score) {
        return Math.max(MIN_SCORE, Math.min(MAX_SCORE, score));
    }

    /** Total filler word occurrences across every tracked expression. */
    public int totalFillerWordCount() {
        return fillerWords.values().stream().mapToInt(Integer::intValue).sum();
    }

    /** Mean of the seven rubric-based scores, on the same 0-5 scale. */
    public double averageScore() {
        return (clarity + structure + concision + fluency + impact + vulgarisation + adaptationToPersona) / 7.0;
    }
}
