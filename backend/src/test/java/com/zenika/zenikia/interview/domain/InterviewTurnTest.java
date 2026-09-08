package com.zenika.zenikia.interview.domain;

import com.zenika.zenikia.assessment.domain.TechnicalAssessment;
import com.zenika.zenikia.coaching.domain.CoachingFeedback;
import com.zenika.zenikia.communication.domain.CommunicationAssessment;
import com.zenika.zenikia.shared.domain.InvalidStateException;
import com.zenika.zenikia.speech.domain.AudioMetadata;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterviewTurnTest {

    private InterviewQuestion aQuestion() {
        return InterviewQuestion.create("Explique les consumer groups Kafka.", "Kafka", 3, QuestionType.TECHNICAL_CONCEPT);
    }

    private TechnicalAssessment anAssessment() {
        return new TechnicalAssessment(3, 3, 3, 3, 3, 3, 3, List.of(), List.of(), List.of());
    }

    private CommunicationAssessment aCommunicationAssessment() {
        return new CommunicationAssessment(30, 60, 60, Map.of(), 0, 0, 3, 3, 3, 3, 3, 3, 3, List.of(), List.of());
    }

    @Test
    void newTurn_startsUnansweredAndUnassessed() {
        InterviewTurn turn = InterviewTurn.forQuestion(aQuestion());

        assertThat(turn.isAnswered()).isFalse();
        assertThat(turn.isAssessed()).isFalse();
        assertThat(turn.isRetry()).isFalse();
    }

    @Test
    void applyAssessment_beforeAnswerThrows() {
        InterviewTurn turn = InterviewTurn.forQuestion(aQuestion());

        assertThatThrownBy(() -> turn.applyAssessment(anAssessment(), aCommunicationAssessment(), new CoachingFeedback("o", "r", List.of(), null, ""), InterviewNextAction.DEEPEN))
                .isInstanceOf(InvalidStateException.class);
    }

    @Test
    void applyAssessment_twiceThrows() {
        InterviewQuestion question = aQuestion();
        InterviewTurn turn = InterviewTurn.forQuestion(question);
        turn.submitAnswer(InterviewAnswer.now(question.id(), "Réponse.", new AudioMetadata(20, "audio/webm", 100)));
        turn.applyAssessment(anAssessment(), aCommunicationAssessment(), new CoachingFeedback("o", "r", List.of(), null, ""), InterviewNextAction.DEEPEN);

        assertThatThrownBy(() -> turn.applyAssessment(anAssessment(), aCommunicationAssessment(), new CoachingFeedback("o", "r", List.of(), null, ""), InterviewNextAction.DEEPEN))
                .isInstanceOf(InvalidStateException.class);
    }

    @Test
    void retryOf_requiresOriginalTurnToBeAssessed() {
        InterviewTurn original = InterviewTurn.forQuestion(aQuestion());

        assertThatThrownBy(() -> InterviewTurn.retryOf(original)).isInstanceOf(InvalidStateException.class);
    }
}
