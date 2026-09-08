package com.zenika.zenikia.assessment.domain;

import java.util.List;

/**
 * Rubric-based evaluation of the technical content ("le fond") of one
 * answer. Every score is bounded 0-5 and produced against an explicit
 * rubric (see {@code prompts/technical-assessment-system.st}) — never a
 * bare "note sur 10" asked to the LLM.
 *
 * <p>Rubric for {@code depth} (identical scale used for the other
 * dimensions):
 * <pre>
 * 0 = aucune compréhension
 * 1 = définition superficielle
 * 2 = principaux concepts compris
 * 3 = maîtrise opérationnelle
 * 4 = maîtrise avancée
 * 5 = limites, alternatives et trade-offs compris
 * </pre>
 *
 * @param correctness         exactitude factuelle de la réponse
 * @param depth               profondeur de compréhension
 * @param reasoning           qualité du raisonnement
 * @param realWorldExperience indices d'expérience réelle vs récitation
 * @param examples            présence et pertinence d'exemples concrets
 * @param tradeOffThinking    compréhension des compromis / alternatives
 * @param productionAwareness maîtrise des enjeux de mise en production
 * @param strengths           points forts observés (phrases courtes)
 * @param weaknesses          points faibles observés (phrases courtes)
 * @param missingConcepts     concepts attendus mais absents de la réponse
 */
public record TechnicalAssessment(
        int correctness,
        int depth,
        int reasoning,
        int realWorldExperience,
        int examples,
        int tradeOffThinking,
        int productionAwareness,
        List<String> strengths,
        List<String> weaknesses,
        List<String> missingConcepts
) {
    public static final int MIN_SCORE = 0;
    public static final int MAX_SCORE = 5;

    public TechnicalAssessment {
        correctness = clamp(correctness);
        depth = clamp(depth);
        reasoning = clamp(reasoning);
        realWorldExperience = clamp(realWorldExperience);
        examples = clamp(examples);
        tradeOffThinking = clamp(tradeOffThinking);
        productionAwareness = clamp(productionAwareness);
        strengths = strengths == null ? List.of() : List.copyOf(strengths);
        weaknesses = weaknesses == null ? List.of() : List.copyOf(weaknesses);
        missingConcepts = missingConcepts == null ? List.of() : List.copyOf(missingConcepts);
    }

    private static int clamp(int score) {
        return Math.max(MIN_SCORE, Math.min(MAX_SCORE, score));
    }

    /** Mean of the seven dimensions, on the same 0-5 scale. Used to feed adaptive difficulty and the final report. */
    public double averageScore() {
        return (correctness + depth + reasoning + realWorldExperience + examples + tradeOffThinking + productionAwareness) / 7.0;
    }
}
