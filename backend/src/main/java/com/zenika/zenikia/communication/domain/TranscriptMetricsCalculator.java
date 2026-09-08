package com.zenika.zenikia.communication.domain;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pure, deterministic transcript metrics — no LLM, no framework dependency.
 * These are the measurements the POC treats as reliable (see CLAUDE.md
 * §"transcript et audio sont différents"): counts derived from the text
 * itself, plus words-per-minute derived from a client-measured duration.
 */
public final class TranscriptMetricsCalculator {

    /** Below this many minutes of speech, a WPM figure is too noisy to be meaningful. */
    private static final double MIN_DURATION_SECONDS_FOR_WPM = 3.0;

    private TranscriptMetricsCalculator() {
    }

    public static int countWords(String transcript) {
        if (transcript == null || transcript.isBlank()) {
            return 0;
        }
        return transcript.trim().split("\\s+").length;
    }

    /** Returns 0 when the duration is too short to produce a meaningful rate, rather than an inflated number. */
    public static double wordsPerMinute(int wordCount, double durationSeconds) {
        if (durationSeconds < MIN_DURATION_SECONDS_FOR_WPM) {
            return 0;
        }
        return wordCount / (durationSeconds / 60.0);
    }

    /** Counts occurrences of each catalog expression in the transcript (case-insensitive, word-boundary aware). */
    public static Map<String, Integer> detectFillerWords(String transcript, List<String> catalog) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        if (transcript == null || transcript.isBlank()) {
            return counts;
        }
        String normalized = transcript.toLowerCase(Locale.FRENCH);
        for (String phrase : catalog) {
            // UNICODE_CHARACTER_CLASS so \b treats accented letters (é, à, ê...) as word characters —
            // without it, French words like "voilà" never match \b at all under plain ASCII \w.
            Pattern pattern = Pattern.compile(
                    "\\b" + Pattern.quote(phrase.toLowerCase(Locale.FRENCH)) + "\\b",
                    Pattern.UNICODE_CHARACTER_CLASS
            );
            Matcher matcher = pattern.matcher(normalized);
            int count = 0;
            while (matcher.find()) {
                count++;
            }
            if (count > 0) {
                counts.put(phrase, count);
            }
        }
        return counts;
    }

    /** Counts immediate word-for-word repetitions (e.g. "le le service"), a simple, honest proxy for hesitation stutters. */
    public static int countImmediateRepetitions(String transcript) {
        if (transcript == null || transcript.isBlank()) {
            return 0;
        }
        String[] words = transcript.toLowerCase(Locale.FRENCH).trim().split("\\s+");
        int repetitions = 0;
        for (int i = 1; i < words.length; i++) {
            String previous = stripPunctuation(words[i - 1]);
            String current = stripPunctuation(words[i]);
            if (!previous.isEmpty() && previous.equals(current)) {
                repetitions++;
            }
        }
        return repetitions;
    }

    /**
     * A filler word count only becomes a coaching-worthy "signal" above a
     * frequency threshold — mirrors the spec's example ("du coup" × 11 over
     * 4 minutes is a signal, a single "euh" is not).
     */
    public static boolean isFrequencySignal(int count, double durationSeconds) {
        if (count < 3) {
            return false;
        }
        if (durationSeconds < MIN_DURATION_SECONDS_FOR_WPM) {
            return count >= 5;
        }
        double perMinute = count / (durationSeconds / 60.0);
        return perMinute >= 2.0;
    }

    private static String stripPunctuation(String word) {
        return word.replaceAll("[^\\p{L}]", "");
    }
}
