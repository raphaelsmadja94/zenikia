package com.zenika.zenikia.communication.domain;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TranscriptMetricsCalculatorTest {

    @Test
    void countWords_countsWhitespaceSeparatedTokens() {
        assertThat(TranscriptMetricsCalculator.countWords("Alors du coup on a mis en place Kafka")).isEqualTo(9);
    }

    @Test
    void countWords_returnsZeroForBlankTranscript() {
        assertThat(TranscriptMetricsCalculator.countWords("   ")).isZero();
        assertThat(TranscriptMetricsCalculator.countWords(null)).isZero();
    }

    @Test
    void wordsPerMinute_computesRateForLongEnoughAnswer() {
        // 90 words over 60 seconds => 90 wpm
        double wpm = TranscriptMetricsCalculator.wordsPerMinute(90, 60);
        assertThat(wpm).isEqualTo(90.0);
    }

    @Test
    void wordsPerMinute_returnsZeroWhenAnswerTooShortToBeMeaningful() {
        assertThat(TranscriptMetricsCalculator.wordsPerMinute(10, 1)).isZero();
    }

    @Test
    void detectFillerWords_countsExactPhraseOccurrencesCaseInsensitively() {
        String transcript = "Du coup on a fait ça, et Du Coup ça a marché, du coup voilà.";

        Map<String, Integer> result = TranscriptMetricsCalculator.detectFillerWords(transcript, FillerWordCatalog.DEFAULT_FRENCH_FILLERS);

        assertThat(result).containsEntry("du coup", 3);
        assertThat(result).containsEntry("voilà", 1);
    }

    @Test
    void detectFillerWords_omitsExpressionsNotPresentAtAll() {
        Map<String, Integer> result = TranscriptMetricsCalculator.detectFillerWords("Une réponse propre et structurée.", FillerWordCatalog.DEFAULT_FRENCH_FILLERS);

        assertThat(result).isEmpty();
    }

    @Test
    void detectFillerWords_doesNotMatchSubstringsAcrossWordBoundaries() {
        // "bah" must not match as a mere substring of an unrelated word like "abaht"
        Map<String, Integer> result = TranscriptMetricsCalculator.detectFillerWords("Il porte un abaht.", List.of("bah"));

        assertThat(result).isEmpty();
    }

    @Test
    void countImmediateRepetitions_detectsConsecutiveDuplicateWords() {
        assertThat(TranscriptMetricsCalculator.countImmediateRepetitions("Alors le le service a été mis à jour")).isEqualTo(1);
        assertThat(TranscriptMetricsCalculator.countImmediateRepetitions("Une réponse fluide sans hésitation")).isZero();
    }

    @Test
    void isFrequencySignal_singleOccurrenceIsNeverASignal() {
        assertThat(TranscriptMetricsCalculator.isFrequencySignal(1, 240)).isFalse();
    }

    @Test
    void isFrequencySignal_highFrequencyOverSeveralMinutesIsASignal() {
        // "du coup" x 11 over 4 minutes, matching the spec's own example
        assertThat(TranscriptMetricsCalculator.isFrequencySignal(11, 240)).isTrue();
    }
}
