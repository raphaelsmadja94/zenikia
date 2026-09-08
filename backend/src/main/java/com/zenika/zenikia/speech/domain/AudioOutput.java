package com.zenika.zenikia.speech.domain;

/** Synthesized audio produced by a {@code TextToSpeechProvider}. */
public record AudioOutput(byte[] bytes, String mimeType) {

    public AudioOutput {
        mimeType = (mimeType == null || mimeType.isBlank()) ? "audio/mpeg" : mimeType;
    }
}
