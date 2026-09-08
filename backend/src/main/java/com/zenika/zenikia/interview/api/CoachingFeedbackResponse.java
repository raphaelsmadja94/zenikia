package com.zenika.zenikia.interview.api;

import com.zenika.zenikia.coaching.domain.CoachingFeedback;

import java.util.List;

/** API-facing view of a {@link CoachingFeedback}. */
public record CoachingFeedbackResponse(
        String observation,
        String recommendation,
        List<String> priorityActions,
        StarBreakdownResponse starBreakdown,
        String modelAnswer
) {
    public static CoachingFeedbackResponse from(CoachingFeedback feedback) {
        return new CoachingFeedbackResponse(
                feedback.observation(),
                feedback.recommendation(),
                feedback.priorityActions(),
                feedback.starBreakdown() == null ? null : StarBreakdownResponse.from(feedback.starBreakdown()),
                feedback.modelAnswer()
        );
    }
}
