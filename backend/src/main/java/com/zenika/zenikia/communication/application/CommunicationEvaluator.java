package com.zenika.zenikia.communication.application;

/**
 * Port: judges the rubric-based dimensions of communication ("la forme")
 * that a deterministic calculator cannot measure — clarity, structure,
 * concision, fluency, impact, vulgarisation, adaptation to the persona.
 * Implemented via Spring AI in infrastructure.
 */
public interface CommunicationEvaluator {

    CommunicationRubricScores evaluate(CommunicationEvaluationContext context);
}
