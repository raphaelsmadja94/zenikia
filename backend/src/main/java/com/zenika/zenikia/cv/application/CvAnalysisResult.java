package com.zenika.zenikia.cv.application;

import com.zenika.zenikia.cv.domain.Experience;
import com.zenika.zenikia.skill.domain.Skill;
import com.zenika.zenikia.skill.domain.SkillClaim;

import java.util.List;

/**
 * Output of a {@link CvAnalyzer} run — the raw material the application
 * service turns into a {@link com.zenika.zenikia.cv.domain.CandidateProfile}.
 * Kept separate from the domain aggregate so the analyzer port stays a pure
 * data-producing contract.
 */
public record CvAnalysisResult(
        String role,
        Integer experienceYears,
        List<Skill> skills,
        List<SkillClaim> claims,
        List<Experience> experiences
) {
}
