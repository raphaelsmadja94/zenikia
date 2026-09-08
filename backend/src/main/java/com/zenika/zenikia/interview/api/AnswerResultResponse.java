package com.zenika.zenikia.interview.api;

/**
 * Response for {@code POST /api/interviews/{id}/answers} — bundles the
 * independent assessments, the coaching feedback, the adaptive decision,
 * and (unless the interview just finished) the next question already
 * generated, matching the flow: one round trip, immediate next question.
 */
public record AnswerResultResponse(
        String turnId,
        String transcript,
        TechnicalAssessmentResponse technicalAssessment,
        CommunicationAssessmentResponse communicationAssessment,
        CoachingFeedbackResponse coachingFeedback,
        String nextAction,
        InterviewQuestionResponse nextQuestion,
        boolean interviewFinished
) {
}
