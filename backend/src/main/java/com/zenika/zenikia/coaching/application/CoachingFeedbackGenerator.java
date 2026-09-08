package com.zenika.zenikia.coaching.application;

import com.zenika.zenikia.coaching.domain.CoachingFeedback;

/**
 * Port: turns two independent assessments (technical + communication) into
 * one actionable, specific piece of feedback. Implemented via Spring AI in
 * infrastructure.
 */
public interface CoachingFeedbackGenerator {

    CoachingFeedback generate(CoachingContext context);
}
