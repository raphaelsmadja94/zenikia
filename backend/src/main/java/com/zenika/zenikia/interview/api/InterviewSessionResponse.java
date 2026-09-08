package com.zenika.zenikia.interview.api;

import com.zenika.zenikia.interview.domain.InterviewSession;

/** API-facing view of an {@link InterviewSession}. */
public record InterviewSessionResponse(
        String id,
        String persona,
        String status,
        String candidateRole,
        int currentDifficulty,
        String currentTargetSkill,
        int totalTurns,
        InterviewQuestionResponse currentQuestion
) {
    public static InterviewSessionResponse from(InterviewSession session) {
        InterviewQuestionResponse currentQuestion = session.lastTurn()
                .filter(turn -> !turn.isAnswered())
                .map(turn -> InterviewQuestionResponse.from(turn.id(), turn.question()))
                .orElse(null);

        return new InterviewSessionResponse(
                session.id(),
                session.persona().name(),
                session.status().name(),
                session.candidateProfile().role(),
                session.currentDifficulty(),
                session.currentTargetSkill(),
                session.turns().size(),
                currentQuestion
        );
    }
}
