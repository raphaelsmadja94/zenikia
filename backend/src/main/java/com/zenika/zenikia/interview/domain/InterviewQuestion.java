package com.zenika.zenikia.interview.domain;

import java.util.UUID;

/**
 * A single question asked to the candidate.
 *
 * @param id           stable identifier
 * @param text         the question as asked
 * @param targetSkill  the skill this question probes (e.g. "Kafka")
 * @param difficulty   1-5, see {@link Difficulty}
 * @param questionType broad shape of the question
 */
public record InterviewQuestion(String id, String text, String targetSkill, int difficulty, QuestionType questionType) {

    public InterviewQuestion {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Question text must not be blank");
        }
        targetSkill = (targetSkill == null || targetSkill.isBlank()) ? "Général" : targetSkill;
        difficulty = Difficulty.clamp(difficulty);
        questionType = questionType == null ? QuestionType.TECHNICAL_CONCEPT : questionType;
    }

    public static InterviewQuestion create(String text, String targetSkill, int difficulty, QuestionType questionType) {
        return new InterviewQuestion(UUID.randomUUID().toString(), text, targetSkill, difficulty, questionType);
    }
}
