package com.zenika.zenikia.interview.application;

import com.zenika.zenikia.interview.domain.InterviewSession;

/**
 * Result of submitting an answer: the updated session, the turn that was
 * just assessed, and the newly generated next turn — {@code nextTurnId} is
 * {@code null} when the adaptive logic decided to {@code FINISH}.
 */
public record AnswerOutcome(InterviewSession session, String answeredTurnId, String nextTurnId) {
}
