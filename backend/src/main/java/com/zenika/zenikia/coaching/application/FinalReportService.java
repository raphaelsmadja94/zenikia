package com.zenika.zenikia.coaching.application;

import com.zenika.zenikia.coaching.domain.FinalReport;
import com.zenika.zenikia.coaching.domain.FinalReportScoring;
import com.zenika.zenikia.coaching.domain.InterviewScores;
import org.springframework.stereotype.Service;

/**
 * Assembles a {@link FinalReport}: deterministic scores
 * ({@link FinalReportScoring}) plus an LLM-generated narrative
 * ({@link FinalReportGenerator}). Never lets the narrative side influence
 * the numbers, and vice versa.
 */
@Service
public class FinalReportService {

    private final FinalReportGenerator finalReportGenerator;

    public FinalReportService(FinalReportGenerator finalReportGenerator) {
        this.finalReportGenerator = finalReportGenerator;
    }

    public FinalReport buildReport(FinalReportContext context) {
        InterviewScores scores = FinalReportScoring.compute(context.averageTechnicalScore(), context.averageCommunicationScore());
        FinalReportNarrative narrative = finalReportGenerator.generate(context);

        return new FinalReport(
                scores,
                narrative.topStrengths(),
                narrative.topPriorities(),
                narrative.recommendedNextSteps(),
                "Assessment generated from this interview session — indicateur de coaching, pas un niveau certifié."
        );
    }
}
