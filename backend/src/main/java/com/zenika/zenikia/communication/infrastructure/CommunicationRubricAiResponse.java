package com.zenika.zenikia.communication.infrastructure;

import java.util.List;

/** Structured output shape requested from the LLM, mirroring {@code CommunicationRubricScores} 1:1. */
record CommunicationRubricAiResponse(
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
