package com.zenika.zenikia.cv.api;

import com.zenika.zenikia.cv.domain.CandidateProfile;
import com.zenika.zenikia.cv.domain.Experience;
import com.zenika.zenikia.skill.domain.ClaimedLevel;
import com.zenika.zenikia.skill.domain.Skill;
import com.zenika.zenikia.skill.domain.SkillCategory;
import com.zenika.zenikia.skill.domain.SkillClaim;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CandidateProfileResponseTest {

    @Test
    void from_mapsEveryFieldOfTheDomainProfileToTheApiResponse() {
        CandidateProfile profile = CandidateProfile.create(
                "Tech Lead Java/Kafka",
                6,
                List.of(new Skill("Kafka", SkillCategory.DATA_MESSAGING, ClaimedLevel.ADVANCED)),
                List.of(new SkillClaim("c1", "Architecture microservices scalable", List.of("Kafka"), List.of("scalabilité"))),
                List.of(new Experience("Tech Lead", "ExampleCorp", "2021-2024", "Migration vers Kafka"))
        );

        CandidateProfileResponse response = CandidateProfileResponse.from(profile);

        assertThat(response.id()).isEqualTo(profile.id());
        assertThat(response.role()).isEqualTo("Tech Lead Java/Kafka");
        assertThat(response.experienceYears()).isEqualTo(6);
        assertThat(response.skills()).hasSize(1);
        assertThat(response.skills().get(0).name()).isEqualTo("Kafka");
        assertThat(response.skills().get(0).claimedLevel()).isEqualTo("ADVANCED");
        assertThat(response.claims()).hasSize(1);
        assertThat(response.claims().get(0).challengeableConcepts()).containsExactly("scalabilité");
        assertThat(response.experiences()).hasSize(1);
        assertThat(response.experiences().get(0).company()).isEqualTo("ExampleCorp");
    }

    @Test
    void from_neverExposesAClaimedLevelAsCertified() {
        // Regression guard for the "claimed, never certified" rule (spec §31): the API DTO must keep
        // using the same ClaimedLevel vocabulary as the domain, never invent a "CERTIFIED" wording.
        Skill skill = new Skill("Java", SkillCategory.LANGUAGE, ClaimedLevel.EXPERT);
        SkillResponse response = SkillResponse.from(skill);

        assertThat(response.claimedLevel()).isIn("UNSPECIFIED", "BEGINNER", "INTERMEDIATE", "ADVANCED", "EXPERT");
    }
}
