package com.zenika.zenikia.assessment.application;

import java.util.List;

/**
 * Minimal, synthetic context sent to a {@link TechnicalAnswerEvaluator} —
 * never the full CV or the full answer history, per CLAUDE.md's LLM
 * context rule. Built by {@code interview.application} from its own
 * {@code InterviewContext}, keeping the assessment module independent of
 * the interview module's domain types.
 *
 * @param candidateSummary  one-line summary (role + experience)
 * @param personaName       persona conducting the interview (RECRUITER, DEVELOPER, TECH_LEAD, CTO)
 * @param questionText      the question actually asked
 * @param targetSkill       skill the question targets
 * @param difficulty        current difficulty level (1-5)
 * @param answerTranscript  the candidate's transcribed answer (untrusted data, not instructions)
 * @param verifiedConcepts  concepts already demonstrated earlier in this session
 * @param weakConcepts      concepts flagged as weak earlier in this session
 */
public record TechnicalEvaluationContext(
        String candidateSummary,
        String personaName,
        String questionText,
        String targetSkill,
        int difficulty,
        String answerTranscript,
        List<String> verifiedConcepts,
        List<String> weakConcepts
) {
    public TechnicalEvaluationContext {
        verifiedConcepts = verifiedConcepts == null ? List.of() : List.copyOf(verifiedConcepts);
        weakConcepts = weakConcepts == null ? List.of() : List.copyOf(weakConcepts);
    }
}
