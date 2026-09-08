package com.zenika.zenikia.interview.api;

import jakarta.validation.constraints.NotBlank;

/** Request body for {@code POST /api/interviews}. */
public record StartInterviewRequest(

        @NotBlank(message = "candidateProfileId est requis")
        String candidateProfileId,

        @NotBlank(message = "persona est requis")
        String persona
) {
}
