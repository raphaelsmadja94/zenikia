package com.zenika.zenikia.assessment.application;

import com.zenika.zenikia.assessment.domain.TechnicalAssessment;

/**
 * Port: evaluates the technical content of an answer against an explicit
 * rubric. Implemented via Spring AI in infrastructure — the domain and the
 * rest of the application layer never call an LLM directly.
 */
public interface TechnicalAnswerEvaluator {

    TechnicalAssessment evaluate(TechnicalEvaluationContext context);
}
