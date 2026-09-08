package com.zenika.zenikia.interview.domain;

import com.zenika.zenikia.assessment.domain.TechnicalAssessment;

/**
 * Pure business rules for the adaptive interview loop (spec §8-9). An LLM
 * may suggest things, but this is the single place that actually decides
 * the next action and how difficulty moves — easy to unit test, no
 * framework dependency.
 *
 * <pre>
 * incorrect → CLARIFY   (same difficulty; two CLARIFY in a row ease off by one level, never brutally)
 * partial   → DEEPEN    (same difficulty, same topic)
 * correct   → CHALLENGE (+1 difficulty, same topic — unless the topic is exhausted, then CHANGE_TOPIC)
 * advanced  → TRADE_OFF (+1 difficulty, same topic)
 * </pre>
 */
public final class InterviewProgressionPolicy {

    public static final int MAX_TOTAL_TURNS = 8;
    public static final int MAX_TURNS_PER_SKILL = 2;

    private static final double CLARIFY_THRESHOLD = 1.5;
    private static final double DEEPEN_THRESHOLD = 2.75;
    private static final double TRADE_OFF_THRESHOLD = 4.0;

    private InterviewProgressionPolicy() {
    }

    public static InterviewNextAction determineNextAction(TechnicalAssessment assessment, int turnsOnCurrentSkill, int totalTurnsSoFar) {
        if (totalTurnsSoFar + 1 >= MAX_TOTAL_TURNS) {
            return InterviewNextAction.FINISH;
        }
        // Checked BEFORE the score-based branches on purpose: a candidate who stays stuck on a
        // partial/weak answer must still see the interview move across the rest of their CV
        // (Angular, Kubernetes, Java...), not get stuck deepening the same skill for the whole
        // session just because their score never crosses the DEEPEN threshold.
        if (turnsOnCurrentSkill >= MAX_TURNS_PER_SKILL) {
            return InterviewNextAction.CHANGE_TOPIC;
        }
        double avg = assessment.averageScore();
        if (avg < CLARIFY_THRESHOLD) {
            return InterviewNextAction.CLARIFY;
        }
        if (avg < DEEPEN_THRESHOLD) {
            return InterviewNextAction.DEEPEN;
        }
        return avg < TRADE_OFF_THRESHOLD ? InterviewNextAction.CHALLENGE : InterviewNextAction.TRADE_OFF;
    }

    /**
     * @param consecutiveClarifies number of CLARIFY actions just triggered in a row on the same skill,
     *                             AFTER applying the action about to be returned by {@link #determineNextAction}
     */
    public static int nextDifficulty(int currentDifficulty, InterviewNextAction action, int consecutiveClarifies) {
        return switch (action) {
            case CHALLENGE, TRADE_OFF -> Difficulty.clamp(currentDifficulty + 1);
            // Never an automatic, brutal drop: only ease off by one level after two CLARIFY in a row.
            case CLARIFY -> consecutiveClarifies >= 2 ? Difficulty.clamp(currentDifficulty - 1) : currentDifficulty;
            case DEEPEN, CHANGE_TOPIC, FINISH -> currentDifficulty;
        };
    }
}
