package com.zenika.zenikia.interview.api;

import com.zenika.zenikia.coaching.domain.StarBreakdown;

/** API-facing view of a {@link StarBreakdown}. */
public record StarBreakdownResponse(
        boolean situationPresent,
        boolean taskPresent,
        boolean actionPresent,
        boolean resultPresent,
        String note
) {
    public static StarBreakdownResponse from(StarBreakdown star) {
        return new StarBreakdownResponse(star.situationPresent(), star.taskPresent(), star.actionPresent(), star.resultPresent(), star.note());
    }
}
