package com.zenika.zenikia.interview.api;

/**
 * Before/after comparison for Retry Mode (spec §18). These are coaching
 * signals, not precise measurements — kept intentionally simple (a handful
 * of headline numbers) rather than over-precise deltas across every
 * dimension.
 */
public record RetryComparisonResponse(
        double clarityBefore,
        double clarityAfter,
        double concisionBefore,
        double concisionAfter,
        int fillerWordsBefore,
        int fillerWordsAfter,
        double technicalScoreBefore,
        double technicalScoreAfter
) {
}
