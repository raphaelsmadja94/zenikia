package com.zenika.zenikia.skill.domain;

/**
 * A technical or methodological skill detected in the candidate's CV
 * (e.g. Java, Spring Boot, Kafka, Angular, Kubernetes, Architecture).
 *
 * @param name          canonical, human-readable skill name
 * @param category      broad grouping used for display
 * @param claimedLevel  self-reported level as it reads in the CV — never certified
 */
public record Skill(String name, SkillCategory category, ClaimedLevel claimedLevel) {

    public Skill {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Skill name must not be blank");
        }
        category = category == null ? SkillCategory.OTHER : category;
        claimedLevel = claimedLevel == null ? ClaimedLevel.UNSPECIFIED : claimedLevel;
    }
}
