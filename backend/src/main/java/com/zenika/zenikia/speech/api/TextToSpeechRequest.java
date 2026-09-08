package com.zenika.zenikia.speech.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Request body for the (optional, not wired to the default UI flow) server-side TTS endpoint. */
public record TextToSpeechRequest(
        @NotBlank(message = "text est requis")
        @Size(max = 2000, message = "text ne doit pas dépasser 2000 caractères")
        String text
) {
}
