package com.zenika.zenikia.assessment.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TechnicalAssessmentTest {

    @Test
    void scores_areClampedToTheZeroToFiveRange() {
        TechnicalAssessment assessment = new TechnicalAssessment(-3, 8, 5, 0, 100, -1, 5, List.of(), List.of(), List.of());

        assertThat(assessment.correctness()).isEqualTo(0);
        assertThat(assessment.depth()).isEqualTo(5);
        assertThat(assessment.examples()).isEqualTo(5);
        assertThat(assessment.tradeOffThinking()).isEqualTo(0);
    }

    @Test
    void averageScore_isTheMeanOfTheSevenDimensions() {
        TechnicalAssessment assessment = new TechnicalAssessment(5, 5, 5, 5, 5, 5, 5, List.of(), List.of(), List.of());
        assertThat(assessment.averageScore()).isEqualTo(5.0);

        TechnicalAssessment zero = new TechnicalAssessment(0, 0, 0, 0, 0, 0, 0, List.of(), List.of(), List.of());
        assertThat(zero.averageScore()).isEqualTo(0.0);
    }
}
