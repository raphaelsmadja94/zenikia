package com.zenika.zenikia.speech.domain;

/**
 * Characteristics of a submitted answer's audio, as reliably available in
 * the POC. {@code durationSeconds} is measured client-side (recording
 * start/stop), which is more trustworthy than trying to infer it
 * server-side from a compressed audio container.
 */
public record AudioMetadata(double durationSeconds, String mimeType, long sizeBytes) {

    public AudioMetadata {
        if (durationSeconds < 0) {
            throw new IllegalArgumentException("durationSeconds must not be negative");
        }
        mimeType = (mimeType == null || mimeType.isBlank()) ? "audio/webm" : mimeType;
    }
}
