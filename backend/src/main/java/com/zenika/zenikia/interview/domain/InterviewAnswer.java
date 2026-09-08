package com.zenika.zenikia.interview.domain;

import com.zenika.zenikia.speech.domain.AudioMetadata;

import java.time.Instant;

/**
 * The candidate's answer to one question. The transcript and the audio
 * metadata are kept as distinct fields on purpose (see CLAUDE.md
 * §"transcript et audio sont différents") — some communication metrics
 * come from one, some from the other.
 */
public record InterviewAnswer(String questionId, String transcript, AudioMetadata audioMetadata, Instant submittedAt) {

    public InterviewAnswer {
        if (questionId == null || questionId.isBlank()) {
            throw new IllegalArgumentException("questionId must not be blank");
        }
        transcript = transcript == null ? "" : transcript;
    }

    public static InterviewAnswer now(String questionId, String transcript, AudioMetadata audioMetadata) {
        return new InterviewAnswer(questionId, transcript, audioMetadata, Instant.now());
    }
}
