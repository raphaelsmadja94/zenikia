package com.zenika.zenikia.coaching.application;

/**
 * Port: turns aggregated session data into the narrative part of the final
 * report (top strengths, top priorities, recommended next steps).
 * Implemented via Spring AI in infrastructure. Numeric scores are computed
 * separately and deterministically — see {@code FinalReportService}.
 */
public interface FinalReportGenerator {

    FinalReportNarrative generate(FinalReportContext context);
}
