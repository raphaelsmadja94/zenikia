package com.zenika.zenikia.interview.domain;

import com.zenika.zenikia.skill.domain.ClaimedLevel;
import com.zenika.zenikia.skill.domain.Skill;
import com.zenika.zenikia.skill.domain.SkillCategory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SkillRotationPolicyTest {

    private final Skill java = new Skill("Java", SkillCategory.LANGUAGE, ClaimedLevel.ADVANCED);
    private final Skill kafka = new Skill("Kafka", SkillCategory.DATA_MESSAGING, ClaimedLevel.INTERMEDIATE);
    private final Skill communication = new Skill("Communication", SkillCategory.SOFT_SKILL, ClaimedLevel.ADVANCED);

    @Test
    void nextTargetSkill_picksFirstUncoveredSkill() {
        String next = SkillRotationPolicy.nextTargetSkill(List.of(java, kafka), List.of("Java"));
        assertThat(next).isEqualTo("Kafka");
    }

    @Test
    void nextTargetSkill_excludesSoftSkills() {
        String next = SkillRotationPolicy.nextTargetSkill(List.of(communication, kafka), List.of());
        assertThat(next).isEqualTo("Kafka");
    }

    @Test
    void nextTargetSkill_cyclesBackToFirstWhenEverythingCovered() {
        String next = SkillRotationPolicy.nextTargetSkill(List.of(java, kafka), List.of("Java", "Kafka"));
        assertThat(next).isEqualTo("Java");
    }

    @Test
    void nextTargetSkill_fallsBackToGeneralWhenProfileHasNoTechnicalSkills() {
        String next = SkillRotationPolicy.nextTargetSkill(List.of(communication), List.of());
        assertThat(next).isEqualTo("Général");
    }
}
