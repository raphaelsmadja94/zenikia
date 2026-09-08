package com.zenika.zenikia.interview.api;

import com.zenika.zenikia.interview.domain.InterviewQuestion;

/** API-facing view of an {@link InterviewQuestion}, scoped to one turn. */
public record InterviewQuestionResponse(
        String turnId,
        String questionId,
        String text,
        String targetSkill,
        int difficulty,
        String questionType
) {
    public static InterviewQuestionResponse from(String turnId, InterviewQuestion question) {
        return new InterviewQuestionResponse(
                turnId, question.id(), question.text(), question.targetSkill(), question.difficulty(), question.questionType().name()
        );
    }
}
