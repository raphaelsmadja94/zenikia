package com.zenika.zenikia.coaching.application;

import com.zenika.zenikia.assessment.domain.TechnicalAssessment;
import com.zenika.zenikia.communication.domain.CommunicationAssessment;

/**
 * Everything a {@link CoachingFeedbackGenerator} needs: the question asked,
 * the candidate's transcript, and the two independent assessments already
 * produced (never re-derives them). Reuses the assessment/communication
 * domain records directly rather than duplicating their shape.
 */
public record CoachingContext(
        String personaName,
        String questionText,
        String questionType,
        String answerTranscript,
        TechnicalAssessment technicalAssessment,
        CommunicationAssessment communicationAssessment
) {
}
