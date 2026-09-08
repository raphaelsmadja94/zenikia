package com.zenika.zenikia.interview.domain;

import java.util.List;

/**
 * Synthetic, session-scoped context handed to every LLM call — never the
 * full CV, never the full question/answer history. Rebuilt fresh from
 * {@link InterviewSession} on every turn.
 *
 * @param candidateSummary    one-line summary (role + years of experience)
 * @param persona             persona conducting the interview
 * @param currentTargetSkill  skill currently under discussion
 * @param verifiedConcepts    concepts demonstrated so far in this session (most recent first, capped)
 * @param weakConcepts        concepts flagged as weak so far in this session (most recent first, capped)
 * @param currentDifficulty   1-5
 * @param lastQuestionText    the previous question asked (blank for the first question)
 * @param lastAnswerTranscript the previous answer's transcript (blank for the first question)
 * @param lastNextAction      the adaptive action that led to the current question (null for the first question)
 */
public record InterviewContext(
        String candidateSummary,
        InterviewPersona persona,
        String currentTargetSkill,
        List<String> verifiedConcepts,
        List<String> weakConcepts,
        int currentDifficulty,
        String lastQuestionText,
        String lastAnswerTranscript,
        InterviewNextAction lastNextAction
) {
    public InterviewContext {
        verifiedConcepts = verifiedConcepts == null ? List.of() : List.copyOf(verifiedConcepts);
        weakConcepts = weakConcepts == null ? List.of() : List.copyOf(weakConcepts);
        currentDifficulty = Difficulty.clamp(currentDifficulty);
        lastQuestionText = lastQuestionText == null ? "" : lastQuestionText;
        lastAnswerTranscript = lastAnswerTranscript == null ? "" : lastAnswerTranscript;
    }
}
