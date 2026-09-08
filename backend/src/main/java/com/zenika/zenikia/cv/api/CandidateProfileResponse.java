package com.zenika.zenikia.cv.api;

import com.zenika.zenikia.cv.domain.CandidateProfile;

import java.util.List;

/** API-facing view of a {@link CandidateProfile}. */
public record CandidateProfileResponse(
        String id,
        String role,
        Integer experienceYears,
        List<SkillResponse> skills,
        List<SkillClaimResponse> claims,
        List<ExperienceResponse> experiences
) {
    public static CandidateProfileResponse from(CandidateProfile profile) {
        return new CandidateProfileResponse(
                profile.id(),
                profile.role(),
                profile.experienceYears(),
                profile.skills().stream().map(SkillResponse::from).toList(),
                profile.claims().stream().map(SkillClaimResponse::from).toList(),
                profile.experiences().stream().map(ExperienceResponse::from).toList()
        );
    }
}
