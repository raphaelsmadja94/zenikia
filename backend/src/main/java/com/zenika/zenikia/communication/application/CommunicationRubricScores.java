package com.zenika.zenikia.communication.application;

import java.util.List;

/**
 * The subset of {@code CommunicationAssessment} that genuinely requires
 * semantic judgment (rubric-based scores + narrative strengths/weaknesses).
 * Everything else in {@code CommunicationAssessment} is computed
 * deterministically from the transcript/audio metadata.
 */
public record CommunicationRubricScores(
        int clarity,
        int structure,
        int concision,
        int fluency,
        int impact,
        int vulgarisation,
        int adaptationToPersona,
        List<String> strengths,
        List<String> weaknesses
) {
}
