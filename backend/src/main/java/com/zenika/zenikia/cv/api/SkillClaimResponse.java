package com.zenika.zenikia.cv.api;

import com.zenika.zenikia.skill.domain.SkillClaim;

import java.util.List;

/** API-facing view of a {@link SkillClaim}. */
public record SkillClaimResponse(
        String id,
        String statement,
        List<String> relatedSkills,
        List<String> challengeableConcepts
) {
    public static SkillClaimResponse from(SkillClaim claim) {
        return new SkillClaimResponse(claim.id(), claim.statement(), claim.relatedSkills(), claim.challengeableConcepts());
    }
}
