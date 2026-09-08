package com.zenika.zenikia.interview.application;

import com.zenika.zenikia.interview.domain.InterviewContext;
import com.zenika.zenikia.skill.domain.SkillClaim;

import java.util.List;

/**
 * Input to {@link InterviewQuestionGenerator}. Which skill to target and
 * what difficulty to aim for are already decided by the application layer
 * (see {@link InterviewContext#currentTargetSkill()} /
 * {@link InterviewContext#currentDifficulty()}) — the generator's only job
 * is to phrase a good question for that skill, persona and adaptive action.
 *
 * @param interviewContext synthetic session context
 * @param opening          true only for the very first question of the session
 * @param candidateClaims  claims from the CV (for CLAIM_CHALLENGE-style questions, especially persona CTO)
 */
public record QuestionGenerationContext(
        InterviewContext interviewContext,
        boolean opening,
        List<SkillClaim> candidateClaims
) {
    public QuestionGenerationContext {
        candidateClaims = candidateClaims == null ? List.of() : List.copyOf(candidateClaims);
    }
}
