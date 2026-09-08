package com.zenika.zenikia.coaching.domain;

import java.util.List;

/**
 * Final report of an interview session. Always presents technical mastery,
 * communication, and interview effectiveness as separate figures — never
 * collapsed into one opaque score (spec §16).
 *
 * @param disclaimer pedagogical framing, e.g. "Assessment generated from
 *                    this interview session." — never "certified" or
 *                    "official level" wording (spec §31).
 */
public record FinalReport(
        InterviewScores scores,
        List<String> topStrengths,
        List<String> topPriorities,
        List<String> recommendedNextSteps,
        String disclaimer
) {
    public FinalReport {
        topStrengths = topStrengths == null ? List.of() : List.copyOf(topStrengths);
        topPriorities = topPriorities == null ? List.of() : List.copyOf(topPriorities);
        recommendedNextSteps = recommendedNextSteps == null ? List.of() : List.copyOf(recommendedNextSteps);
        disclaimer = (disclaimer == null || disclaimer.isBlank())
                ? "Indicateur de coaching généré à partir de cette session — pas un niveau certifié."
                : disclaimer;
    }
}
