package com.zenika.zenikia.cv.domain;

import com.zenika.zenikia.skill.domain.Skill;
import com.zenika.zenikia.skill.domain.SkillClaim;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Profile extracted from a candidate's CV: what they say about themselves,
 * never a verified assessment of their actual level. The interview is what
 * produces an assessed signal, not this profile.
 *
 * @param id               stable identifier
 * @param role             detected current/target role (e.g. "Senior Fullstack Developer")
 * @param experienceYears  total years of professional experience detected (may be null if unclear)
 * @param skills           skills detected, with claimed (not certified) level
 * @param claims           statements extracted from the CV, challengeable in interview
 * @param experiences      professional experience entries
 * @param analyzedAt       when the analysis was produced
 */
public record CandidateProfile(
        String id,
        String role,
        Integer experienceYears,
        List<Skill> skills,
        List<SkillClaim> claims,
        List<Experience> experiences,
        Instant analyzedAt
) {
    public CandidateProfile {
        role = (role == null || role.isBlank()) ? "Rôle non déterminé" : role;
        skills = skills == null ? List.of() : List.copyOf(skills);
        claims = claims == null ? List.of() : List.copyOf(claims);
        experiences = experiences == null ? List.of() : List.copyOf(experiences);
    }

    public static CandidateProfile create(
            String role,
            Integer experienceYears,
            List<Skill> skills,
            List<SkillClaim> claims,
            List<Experience> experiences
    ) {
        return new CandidateProfile(
                UUID.randomUUID().toString(),
                role,
                experienceYears,
                skills,
                claims,
                experiences,
                Instant.now()
        );
    }
}
