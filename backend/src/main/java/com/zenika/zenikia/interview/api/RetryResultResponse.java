package com.zenika.zenikia.interview.api;

/** Response for {@code POST /api/interviews/{id}/retry}. */
public record RetryResultResponse(
        String originalTurnId,
        String retryTurnId,
        String transcript,
        TechnicalAssessmentResponse technicalAssessment,
        CommunicationAssessmentResponse communicationAssessment,
        CoachingFeedbackResponse coachingFeedback,
        RetryComparisonResponse comparison
) {
}
