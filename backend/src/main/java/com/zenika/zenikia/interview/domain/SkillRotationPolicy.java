package com.zenika.zenikia.interview.domain;

import com.zenika.zenikia.skill.domain.Skill;
import com.zenika.zenikia.skill.domain.SkillCategory;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Picks which skill the next question should target: the first
 * not-yet-covered skill from the CV (soft skills excluded — they don't
 * make for a good technical deep-dive), falling back to cycling back to
 * the first skill once every skill has been covered at least once.
 */
public final class SkillRotationPolicy {

    private static final String FALLBACK_SKILL = "Général";

    private SkillRotationPolicy() {
    }

    public static String nextTargetSkill(List<Skill> allSkills, List<String> alreadyCoveredSkillNames) {
        Set<String> covered = new LinkedHashSet<>(alreadyCoveredSkillNames);

        List<String> candidateNames = allSkills.stream()
                .filter(s -> s.category() != SkillCategory.SOFT_SKILL)
                .map(Skill::name)
                .distinct()
                .toList();

        if (candidateNames.isEmpty()) {
            return FALLBACK_SKILL;
        }
        for (String name : candidateNames) {
            if (!covered.contains(name)) {
                return name;
            }
        }
        return candidateNames.get(0);
    }
}
