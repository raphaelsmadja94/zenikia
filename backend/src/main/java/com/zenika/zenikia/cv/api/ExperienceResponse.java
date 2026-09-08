package com.zenika.zenikia.cv.api;

import com.zenika.zenikia.cv.domain.Experience;

/** API-facing view of an {@link Experience}. */
public record ExperienceResponse(String title, String company, String period, String summary) {

    public static ExperienceResponse from(Experience experience) {
        return new ExperienceResponse(experience.title(), experience.company(), experience.period(), experience.summary());
    }
}
