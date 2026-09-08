package com.zenika.zenikia.coaching.domain;

/**
 * Pure math turning 0-5 average assessment scores into the /10 figures
 * shown in the final report. Deliberately NOT delegated to an LLM: these
 * numbers must be consistent and reproducible, not a model's guess.
 *
 * <p>{@code interviewEffectiveness} weighs technical content more than
 * communication (60/40) — in a technical interview, what you know still
 * matters more than how smoothly you say it, but both count, matching the
 * product's "fond ≠ forme, jamais confondus" principle while still giving
 * one practical "how did the interview go" indicator.
 */
public final class FinalReportScoring {

    private static final double TECHNICAL_WEIGHT = 0.6;
    private static final double COMMUNICATION_WEIGHT = 0.4;

    private FinalReportScoring() {
    }

    public static InterviewScores compute(double averageTechnicalScore0to5, double averageCommunicationScore0to5) {
        double technical10 = round1(averageTechnicalScore0to5 * 2);
        double communication10 = round1(averageCommunicationScore0to5 * 2);
        double effectiveness10 = round1(technical10 * TECHNICAL_WEIGHT + communication10 * COMMUNICATION_WEIGHT);
        return new InterviewScores(technical10, communication10, effectiveness10);
    }

    private static double round1(double value) {
        return Math.round(value * 10) / 10.0;
    }
}
