package com.zenika.zenikia.coaching.domain;

import java.util.List;

/**
 * Actionable coaching feedback for one answer. Deliberately structured as
 * observation → recommendation rather than a vague verdict — "Sois plus
 * clair" is forbidden by design; the shape here forces something like
 * "Ta réponse dure 1m48 et commence par 40 secondes de contexte..." followed
 * by a concrete structure to try next time.
 *
 * @param observation      a concrete, specific observation about this answer (timing, structure, numbers)
 * @param recommendation   a concrete structure or technique to try on retry
 * @param priorityActions  the (at most 3) highest-impact next steps
 * @param starBreakdown    Situation/Task/Action/Result reading — only for behavioral questions
 * @param modelAnswer      a concise example answer to the question — shown on demand (not by
 *                         default), never as a substitute for the candidate figuring it out:
 *                         ZenikIA challenges, it doesn't hand out answers, but a reference point
 *                         is useful once the candidate has already attempted the question.
 */
public record CoachingFeedback(
        String observation,
        String recommendation,
        List<String> priorityActions,
        StarBreakdown starBreakdown,
        String modelAnswer
) {
    public CoachingFeedback {
        priorityActions = priorityActions == null ? List.of() : List.copyOf(priorityActions);
        modelAnswer = modelAnswer == null ? "" : modelAnswer;
    }
}
