package com.zenika.zenikia.interview.application;

import com.zenika.zenikia.interview.domain.InterviewSession;

/** Result of a retry attempt: the updated session, the original turn, and the new retry turn. */
public record RetryOutcome(InterviewSession session, String originalTurnId, String retryTurnId) {
}
