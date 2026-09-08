package com.zenika.zenikia.communication.application;

import com.zenika.zenikia.communication.domain.CommunicationAssessment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommunicationAssessmentServiceTest {

    @Mock
    private CommunicationEvaluator communicationEvaluator;

    @Test
    void assess_combinesDeterministicMetricsWithLlmRubricScores_withoutLettingEitherOverrideTheOther() {
        CommunicationAssessmentService service = new CommunicationAssessmentService(communicationEvaluator);
        String transcript = "Du coup on a mis en place Kafka du coup, du coup ça a marché du coup, du coup voilà.";

        when(communicationEvaluator.evaluate(org.mockito.ArgumentMatchers.any()))
                .thenReturn(new CommunicationRubricScores(2, 2, 2, 2, 2, 3, 3, List.of("Vocabulaire adapté"), List.of("Trop de tics de langage")));

        CommunicationAssessment result = service.assess("CTO", "Pourquoi Kafka ?", transcript, 60.0);

        // Deterministic side: computed by the service itself, not by the mocked evaluator.
        assertThat(result.wordCount()).isGreaterThan(0);
        assertThat(result.fillerWords()).containsKey("du coup");
        assertThat(result.longPauseCount()).isZero(); // not reliably measurable in the POC

        // Rubric side: comes straight from the (mocked) evaluator.
        assertThat(result.clarity()).isEqualTo(2);
        assertThat(result.vulgarisation()).isEqualTo(3);
        assertThat(result.weaknesses()).containsExactly("Trop de tics de langage");

        ArgumentCaptor<CommunicationEvaluationContext> captor = ArgumentCaptor.forClass(CommunicationEvaluationContext.class);
        verify(communicationEvaluator).evaluate(captor.capture());
        assertThat(captor.getValue().fillerWords()).containsKey("du coup");
        assertThat(captor.getValue().durationSeconds()).isEqualTo(60.0);
    }
}
