package com.zenika.zenikia.coaching.domain;

/**
 * Situation / Task / Action / Result reading of a behavioral answer.
 * Populated only for behavioral (STAR-style) questions — {@code null}
 * otherwise. Prepared per spec §20 (STAR Coach) without building a
 * separate full engine: it rides on the same coaching LLM call.
 *
 * @param situationPresent whether the context/situation was clearly set
 * @param taskPresent      whether the objective/responsibility was clear
 * @param actionPresent    whether concrete actions taken were described
 * @param resultPresent    whether a measurable/observable result was given
 * @param note             one short sentence explaining the weakest part, if any
 */
public record StarBreakdown(
        boolean situationPresent,
        boolean taskPresent,
        boolean actionPresent,
        boolean resultPresent,
        String note
) {
}
