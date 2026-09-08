package com.zenika.zenikia.skill.domain;

import java.util.List;

/**
 * A statement extracted from the CV that asserts something the candidate
 * did or achieved (e.g. "Mise en place d'une architecture microservices
 * scalable avec Kafka"). A claim is the raw material for the Evidence /
 * Claim Challenger: {@code challengeableConcepts} lists the vague or
 * strong words worth probing in interview (scalable, resilient,
 * high-availability...).
 *
 * @param id                     stable identifier within the profile
 * @param statement              the claim as written/paraphrased from the CV
 * @param relatedSkills          skill names this claim relates to
 * @param challengeableConcepts  concepts an interviewer should challenge (e.g. "scalabilité", "microservices")
 */
public record SkillClaim(
        String id,
        String statement,
        List<String> relatedSkills,
        List<String> challengeableConcepts
) {
    public SkillClaim {
        if (statement == null || statement.isBlank()) {
            throw new IllegalArgumentException("SkillClaim statement must not be blank");
        }
        relatedSkills = relatedSkills == null ? List.of() : List.copyOf(relatedSkills);
        challengeableConcepts = challengeableConcepts == null ? List.of() : List.copyOf(challengeableConcepts);
    }
}
