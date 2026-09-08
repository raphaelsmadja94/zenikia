package com.zenika.zenikia.skill.domain;

/**
 * Self-reported proficiency level as it appears (explicitly or implicitly)
 * in the CV. This is NEVER a certified or verified level — only the
 * interview itself can produce an assessed signal (see
 * {@code assessment.domain.TechnicalAssessment}). Naming it "claimed"
 * everywhere in the codebase is deliberate: it prevents confusing what the
 * candidate says with what has been demonstrated.
 */
public enum ClaimedLevel {
    UNSPECIFIED,
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    EXPERT
}
