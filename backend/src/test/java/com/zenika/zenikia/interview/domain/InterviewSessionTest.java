package com.zenika.zenikia.interview.domain;

import com.zenika.zenikia.assessment.domain.TechnicalAssessment;
import com.zenika.zenikia.coaching.domain.CoachingFeedback;
import com.zenika.zenikia.communication.domain.CommunicationAssessment;
import com.zenika.zenikia.cv.domain.CandidateProfile;
import com.zenika.zenikia.shared.domain.InvalidStateException;
import com.zenika.zenikia.shared.domain.ResourceNotFoundException;
import com.zenika.zenikia.skill.domain.ClaimedLevel;
import com.zenika.zenikia.skill.domain.Skill;
import com.zenika.zenikia.skill.domain.SkillCategory;
import com.zenika.zenikia.speech.domain.AudioMetadata;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InterviewSessionTest {

    private CandidateProfile aProfile() {
        return CandidateProfile.create(
                "Senior Java Developer",
                8,
                List.of(new Skill("Kafka", SkillCategory.DATA_MESSAGING, ClaimedLevel.ADVANCED)),
                List.of(),
                List.of()
        );
    }

    private TechnicalAssessment technicalAssessment(int score) {
        return new TechnicalAssessment(score, score, score, score, score, score, score,
                List.of("Bonne maîtrise des partitions"), List.of(), List.of());
    }

    private CommunicationAssessment communicationAssessment() {
        return new CommunicationAssessment(60, 100, 100, Map.of(), 0, 0, 3, 3, 3, 3, 3, 3, 3, List.of(), List.of());
    }

    private CoachingFeedback coachingFeedback() {
        return new CoachingFeedback("Observation.", "Recommandation.", List.of(), null, "Réponse modèle.");
    }

    @Test
    void start_beginsInProgressWithDefaultDifficulty() {
        InterviewSession session = InterviewSession.start(aProfile(), InterviewPersona.CTO, "Kafka");

        assertThat(session.status()).isEqualTo(InterviewStatus.IN_PROGRESS);
        assertThat(session.currentDifficulty()).isEqualTo(Difficulty.DEFAULT_STARTING);
        assertThat(session.currentTargetSkill()).isEqualTo("Kafka");
        assertThat(session.turns()).isEmpty();
    }

    @Test
    void askQuestion_appendsAPendingTurn() {
        InterviewSession session = InterviewSession.start(aProfile(), InterviewPersona.CTO, "Kafka");
        InterviewQuestion question = InterviewQuestion.create("Que se passe-t-il lors d'un rebalance ?", "Kafka", 3, QuestionType.TECHNICAL_CONCEPT);

        InterviewTurn turn = session.askQuestion(question);

        assertThat(session.turns()).hasSize(1);
        assertThat(turn.isAnswered()).isFalse();
        assertThat(session.lastTurn()).contains(turn);
    }

    @Test
    void applyAssessmentAndAdvance_onGoodAnswerIncreasesDifficultyAndReturnsChallenge() {
        InterviewSession session = InterviewSession.start(aProfile(), InterviewPersona.CTO, "Kafka");
        InterviewQuestion question = InterviewQuestion.create("Question ?", "Kafka", 2, QuestionType.TECHNICAL_CONCEPT);
        InterviewTurn turn = session.askQuestion(question);
        session.submitAnswer(turn.id(), InterviewAnswer.now(question.id(), "Réponse détaillée.", new AudioMetadata(60, "audio/webm", 1000)));

        InterviewNextAction action = session.applyAssessmentAndAdvance(turn.id(), technicalAssessment(3), communicationAssessment(), coachingFeedback());

        assertThat(action).isEqualTo(InterviewNextAction.CHALLENGE);
        assertThat(session.currentDifficulty()).isEqualTo(3);
        assertThat(turn.isAssessed()).isTrue();
    }

    @Test
    void applyAssessmentAndAdvance_onWeakAnswerNeverBrutallyDropsDifficultyOnFirstClarify() {
        InterviewSession session = InterviewSession.start(aProfile(), InterviewPersona.CTO, "Kafka");
        InterviewQuestion question = InterviewQuestion.create("Question ?", "Kafka", 3, QuestionType.TECHNICAL_CONCEPT);
        InterviewTurn turn = session.askQuestion(question);
        session.submitAnswer(turn.id(), InterviewAnswer.now(question.id(), "Je ne sais pas.", new AudioMetadata(10, "audio/webm", 100)));

        InterviewNextAction action = session.applyAssessmentAndAdvance(turn.id(), technicalAssessment(0), communicationAssessment(), coachingFeedback());

        assertThat(action).isEqualTo(InterviewNextAction.CLARIFY);
        assertThat(session.currentDifficulty()).isEqualTo(Difficulty.DEFAULT_STARTING); // unchanged, no brutal drop
    }

    @Test
    void applyAssessmentAndAdvance_onFinishActionCompletesTheSession() {
        InterviewSession session = InterviewSession.start(aProfile(), InterviewPersona.CTO, "Kafka");
        // Drive the session until the adaptive policy decides to FINISH (bounded by MAX_TOTAL_TURNS).
        InterviewNextAction lastAction = null;
        for (int i = 0; i < InterviewProgressionPolicy.MAX_TOTAL_TURNS && session.status() == InterviewStatus.IN_PROGRESS; i++) {
            InterviewQuestion q = InterviewQuestion.create("Q" + i, "Kafka", session.currentDifficulty(), QuestionType.TECHNICAL_CONCEPT);
            InterviewTurn t = session.askQuestion(q);
            session.submitAnswer(t.id(), InterviewAnswer.now(q.id(), "Réponse.", new AudioMetadata(30, "audio/webm", 500)));
            lastAction = session.applyAssessmentAndAdvance(t.id(), technicalAssessment(3), communicationAssessment(), coachingFeedback());
        }

        assertThat(lastAction).isEqualTo(InterviewNextAction.FINISH);
        assertThat(session.status()).isEqualTo(InterviewStatus.COMPLETED);
    }

    @Test
    void submitAnswer_twiceOnSameTurnThrows() {
        InterviewSession session = InterviewSession.start(aProfile(), InterviewPersona.CTO, "Kafka");
        InterviewQuestion question = InterviewQuestion.create("Question ?", "Kafka", 2, QuestionType.TECHNICAL_CONCEPT);
        InterviewTurn turn = session.askQuestion(question);
        session.submitAnswer(turn.id(), InterviewAnswer.now(question.id(), "Réponse.", new AudioMetadata(20, "audio/webm", 100)));

        assertThatThrownBy(() -> session.submitAnswer(turn.id(), InterviewAnswer.now(question.id(), "Autre réponse.", new AudioMetadata(20, "audio/webm", 100))))
                .isInstanceOf(InvalidStateException.class);
    }

    @Test
    void getTurn_unknownIdThrowsResourceNotFound() {
        InterviewSession session = InterviewSession.start(aProfile(), InterviewPersona.CTO, "Kafka");

        assertThatThrownBy(() -> session.getTurn("does-not-exist")).isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void askRetryQuestion_requiresTheOriginalTurnToBeAlreadyAssessed() {
        InterviewSession session = InterviewSession.start(aProfile(), InterviewPersona.CTO, "Kafka");
        InterviewQuestion question = InterviewQuestion.create("Question ?", "Kafka", 2, QuestionType.TECHNICAL_CONCEPT);
        InterviewTurn turn = session.askQuestion(question);

        assertThatThrownBy(() -> session.askRetryQuestion(turn.id())).isInstanceOf(InvalidStateException.class);
    }

    @Test
    void askRetryQuestion_createsANewTurnForTheSameQuestion() {
        InterviewSession session = InterviewSession.start(aProfile(), InterviewPersona.CTO, "Kafka");
        InterviewQuestion question = InterviewQuestion.create("Question ?", "Kafka", 2, QuestionType.TECHNICAL_CONCEPT);
        InterviewTurn turn = session.askQuestion(question);
        session.submitAnswer(turn.id(), InterviewAnswer.now(question.id(), "Réponse initiale.", new AudioMetadata(20, "audio/webm", 100)));
        session.applyAssessmentAndAdvance(turn.id(), technicalAssessment(1), communicationAssessment(), coachingFeedback());

        InterviewTurn retryTurn = session.askRetryQuestion(turn.id());

        assertThat(retryTurn.isRetry()).isTrue();
        assertThat(retryTurn.retryOfTurnId()).contains(turn.id());
        assertThat(retryTurn.question().id()).isEqualTo(question.id());
        assertThat(retryTurn.id()).isNotEqualTo(turn.id());
    }

    @Test
    void verifiedConcepts_collectsStrengthsFromAssessedTurnsMostRecentFirst() {
        InterviewSession session = InterviewSession.start(aProfile(), InterviewPersona.CTO, "Kafka");
        InterviewQuestion question = InterviewQuestion.create("Question ?", "Kafka", 2, QuestionType.TECHNICAL_CONCEPT);
        InterviewTurn turn = session.askQuestion(question);
        session.submitAnswer(turn.id(), InterviewAnswer.now(question.id(), "Réponse.", new AudioMetadata(20, "audio/webm", 100)));
        session.applyAssessmentAndAdvance(turn.id(), technicalAssessment(4), communicationAssessment(), coachingFeedback());

        assertThat(session.verifiedConcepts(5)).contains("Bonne maîtrise des partitions");
    }
}
