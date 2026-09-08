package com.zenika.zenikia.cv.api;

import com.zenika.zenikia.skill.domain.Skill;

/** API-facing view of a {@link Skill}. */
public record SkillResponse(String name, String category, String claimedLevel) {

    public static SkillResponse from(Skill skill) {
        return new SkillResponse(skill.name(), skill.category().name(), skill.claimedLevel().name());
    }
}
