package com.zenika.zenikia.interview.domain;

import com.zenika.zenikia.assessment.domain.TechnicalAssessment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InterviewProgressionPolicyTest {

    private TechnicalAssessment withAverage(int score) {
        return new TechnicalAssessment(score, score, score, score, score, score, score, List.of(), List.of(), List.of());
    }

    @ParameterizedTest
    @CsvSource({
            "0,CLARIFY",
            "1,CLARIFY",
            "2,DEEPEN",
            "3,CHALLENGE",
            "4,TRADE_OFF",
            "5,TRADE_OFF"
    })
    void determineNextAction_mapsAverageScoreToExpectedAction(int score, InterviewNextAction expected) {
        InterviewNextAction action = InterviewProgressionPolicy.determineNextAction(withAverage(score), 0, 0);
        assertThat(action).isEqualTo(expected);
    }

    @Test
    void determineNextAction_switchesTopicWhenSkillExhaustedEvenOnGoodAnswer() {
        InterviewNextAction action = InterviewProgressionPolicy.determineNextAction(
                withAverage(4), InterviewProgressionPolicy.MAX_TURNS_PER_SKILL, 2);

        assertThat(action).isEqualTo(InterviewNextAction.CHANGE_TOPIC);
    }

    @Test
    void determineNextAction_switchesTopicWhenSkillExhaustedEvenOnWeakAnswer() {
        // Regression guard: a candidate stuck on partial/weak answers must still see the
        // interview move across the rest of their CV, never get stuck deepening one skill for
        // the whole session just because their score never crosses the DEEPEN threshold.
        InterviewNextAction action = InterviewProgressionPolicy.determineNextAction(
                withAverage(0), InterviewProgressionPolicy.MAX_TURNS_PER_SKILL, 2);

        assertThat(action).isEqualTo(InterviewNextAction.CHANGE_TOPIC);
    }

    @Test
    void determineNextAction_finishesWhenMaxTotalTurnsReached() {
        InterviewNextAction action = InterviewProgressionPolicy.determineNextAction(
                withAverage(3), 0, InterviewProgressionPolicy.MAX_TOTAL_TURNS - 1);

        assertThat(action).isEqualTo(InterviewNextAction.FINISH);
    }

    @Test
    void nextDifficulty_increasesOnChallengeAndTradeOff() {
        assertThat(InterviewProgressionPolicy.nextDifficulty(2, InterviewNextAction.CHALLENGE, 0)).isEqualTo(3);
        assertThat(InterviewProgressionPolicy.nextDifficulty(2, InterviewNextAction.TRADE_OFF, 0)).isEqualTo(3);
    }

    @Test
    void nextDifficulty_neverExceedsMax() {
        assertThat(InterviewProgressionPolicy.nextDifficulty(Difficulty.MAX, InterviewNextAction.CHALLENGE, 0))
                .isEqualTo(Difficulty.MAX);
    }

    @Test
    void nextDifficulty_staysFlatOnSingleClarify_neverBrutalDrop() {
        assertThat(InterviewProgressionPolicy.nextDifficulty(3, InterviewNextAction.CLARIFY, 1)).isEqualTo(3);
    }

    @Test
    void nextDifficulty_easesOffByOneOnlyAfterTwoConsecutiveClarifies() {
        assertThat(InterviewProgressionPolicy.nextDifficulty(3, InterviewNextAction.CLARIFY, 2)).isEqualTo(2);
    }

    @Test
    void nextDifficulty_neverDropsBelowMinEvenAfterRepeatedClarifies() {
        assertThat(InterviewProgressionPolicy.nextDifficulty(Difficulty.MIN, InterviewNextAction.CLARIFY, 2))
                .isEqualTo(Difficulty.MIN);
    }

    @Test
    void nextDifficulty_staysFlatOnDeepenAndChangeTopic() {
        assertThat(InterviewProgressionPolicy.nextDifficulty(3, InterviewNextAction.DEEPEN, 0)).isEqualTo(3);
        assertThat(InterviewProgressionPolicy.nextDifficulty(3, InterviewNextAction.CHANGE_TOPIC, 0)).isEqualTo(3);
    }
}
