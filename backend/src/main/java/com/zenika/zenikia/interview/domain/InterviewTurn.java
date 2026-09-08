package com.zenika.zenikia.interview.domain;

import com.zenika.zenikia.assessment.domain.TechnicalAssessment;
import com.zenika.zenikia.coaching.domain.CoachingFeedback;
import com.zenika.zenikia.communication.domain.CommunicationAssessment;
import com.zenika.zenikia.shared.domain.InvalidStateException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * One question/answer/assessment cycle within an {@link InterviewSession}.
 * A turn is filled progressively (question first, then answer, then
 * assessments) and is sealed once assessed — a retry never mutates an
 * existing turn, it creates a new one via {@link #retryOf(InterviewTurn)}
 * so the "before/after" comparison (see Retry Mode) always has two
 * distinct, immutable data points to compare.
 */
public final class InterviewTurn {

    private final String id;
    private final InterviewQuestion question;
    private final boolean retry;
    private final String retryOfTurnId;
    private final Instant createdAt;

    private InterviewAnswer answer;
    private TechnicalAssessment technicalAssessment;
    private CommunicationAssessment communicationAssessment;
    private CoachingFeedback coachingFeedback;
    private InterviewNextAction nextAction;

    private InterviewTurn(String id, InterviewQuestion question, boolean retry, String retryOfTurnId, Instant createdAt) {
        this.id = id;
        this.question = question;
        this.retry = retry;
        this.retryOfTurnId = retryOfTurnId;
        this.createdAt = createdAt;
    }

    public static InterviewTurn forQuestion(InterviewQuestion question) {
        return new InterviewTurn(UUID.randomUUID().toString(), question, false, null, Instant.now());
    }

    public static InterviewTurn retryOf(InterviewTurn original) {
        if (!original.isAssessed()) {
            throw new InvalidStateException("Impossible de réessayer une question qui n'a pas encore été évaluée.");
        }
        return new InterviewTurn(UUID.randomUUID().toString(), original.question, true, original.id, Instant.now());
    }

    public void submitAnswer(InterviewAnswer newAnswer) {
        if (this.answer != null) {
            throw new InvalidStateException("Cette question a déjà reçu une réponse.");
        }
        this.answer = newAnswer;
    }

    public void applyAssessment(
            TechnicalAssessment technicalAssessment,
            CommunicationAssessment communicationAssessment,
            CoachingFeedback coachingFeedback,
            InterviewNextAction nextAction
    ) {
        if (this.answer == null) {
            throw new InvalidStateException("Impossible d'évaluer une question sans réponse.");
        }
        if (this.technicalAssessment != null) {
            throw new InvalidStateException("Cette réponse a déjà été évaluée.");
        }
        this.technicalAssessment = technicalAssessment;
        this.communicationAssessment = communicationAssessment;
        this.coachingFeedback = coachingFeedback;
        this.nextAction = nextAction;
    }

    public String id() {
        return id;
    }

    public InterviewQuestion question() {
        return question;
    }

    public boolean isRetry() {
        return retry;
    }

    public Optional<String> retryOfTurnId() {
        return Optional.ofNullable(retryOfTurnId);
    }

    public Instant createdAt() {
        return createdAt;
    }

    public Optional<InterviewAnswer> answer() {
        return Optional.ofNullable(answer);
    }

    public Optional<TechnicalAssessment> technicalAssessment() {
        return Optional.ofNullable(technicalAssessment);
    }

    public Optional<CommunicationAssessment> communicationAssessment() {
        return Optional.ofNullable(communicationAssessment);
    }

    public Optional<CoachingFeedback> coachingFeedback() {
        return Optional.ofNullable(coachingFeedback);
    }

    public Optional<InterviewNextAction> nextAction() {
        return Optional.ofNullable(nextAction);
    }

    public boolean isAnswered() {
        return answer != null;
    }

    public boolean isAssessed() {
        return technicalAssessment != null;
    }
}
