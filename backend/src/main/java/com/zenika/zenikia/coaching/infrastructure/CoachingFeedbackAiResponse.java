package com.zenika.zenikia.coaching.infrastructure;

import java.util.List;

/** Structured output shape requested from the LLM, mirroring {@code CoachingFeedback} 1:1. */
record CoachingFeedbackAiResponse(
        String observation,
        String recommendation,
        List<String> priorityActions,
        StarBreakdownAi starBreakdown,
        String modelAnswer
) {
    record StarBreakdownAi(
            boolean situationPresent,
            boolean taskPresent,
            boolean actionPresent,
            boolean resultPresent,
            String note
    ) {
    }
}
