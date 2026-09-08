package com.zenika.zenikia.coaching.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FinalReportScoringTest {

    @Test
    void compute_scalesZeroToFiveAveragesUpToZeroToTen() {
        InterviewScores scores = FinalReportScoring.compute(4.0, 3.0);

        assertThat(scores.technicalMastery()).isEqualTo(8.0);
        assertThat(scores.communication()).isEqualTo(6.0);
    }

    @Test
    void compute_neverCollapsesTechnicalAndCommunicationIntoTheSameFigure() {
        InterviewScores scores = FinalReportScoring.compute(4.5, 1.5);

        assertThat(scores.technicalMastery()).isNotEqualTo(scores.communication());
    }

    @Test
    void compute_weighsTechnicalMoreThanCommunicationInEffectivenessScore() {
        InterviewScores strongTechnical = FinalReportScoring.compute(5.0, 0.0);
        InterviewScores strongCommunication = FinalReportScoring.compute(0.0, 5.0);

        assertThat(strongTechnical.interviewEffectiveness()).isGreaterThan(strongCommunication.interviewEffectiveness());
    }
}
