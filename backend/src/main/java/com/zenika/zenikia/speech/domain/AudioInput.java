package com.zenika.zenikia.speech.domain;

/**
 * Raw audio submitted for transcription.
 *
 * @param bytes                       raw audio bytes
 * @param mimeType                    e.g. "audio/webm", "audio/wav"
 * @param clientMeasuredDurationSeconds duration as measured on the client (recording
 *                                    start/stop) — this is the only duration source the
 *                                    POC treats as reliable
 */
public record AudioInput(byte[] bytes, String mimeType, double clientMeasuredDurationSeconds) {

    public AudioInput {
        if (bytes == null || bytes.length == 0) {
            throw new IllegalArgumentException("Audio input must not be empty");
        }
        mimeType = (mimeType == null || mimeType.isBlank()) ? "audio/webm" : mimeType;
    }
}
