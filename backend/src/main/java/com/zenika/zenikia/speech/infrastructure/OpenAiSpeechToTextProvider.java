package com.zenika.zenikia.speech.infrastructure;

import com.zenika.zenikia.shared.domain.AiProviderException;
import com.zenika.zenikia.speech.application.SpeechToTextProvider;
import com.zenika.zenikia.speech.domain.AudioInput;
import com.zenika.zenikia.speech.domain.Transcript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

/**
 * {@link SpeechToTextProvider} backed by OpenAI Whisper via Spring AI.
 * The audio bytes are transcribed in-memory and never written to disk —
 * nothing here persists audio beyond the request.
 */
@Component
class OpenAiSpeechToTextProvider implements SpeechToTextProvider {

    private static final Logger log = LoggerFactory.getLogger(OpenAiSpeechToTextProvider.class);

    private final OpenAiAudioTranscriptionModel transcriptionModel;

    OpenAiSpeechToTextProvider(OpenAiAudioTranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    @Override
    public Transcript transcribe(AudioInput audio) {
        try {
            ByteArrayResource resource = new ByteArrayResource(audio.bytes()) {
                @Override
                public String getFilename() {
                    return "answer." + extensionFor(audio.mimeType());
                }
            };
            String text = transcriptionModel.call(resource);
            return new Transcript(text);
        } catch (Exception e) {
            // Never log audio bytes or transcript content, only that the call failed.
            log.warn("Speech-to-text call failed: {}", e.getMessage());
            throw new AiProviderException("Échec de la transcription audio : " + e.getMessage(), e);
        }
    }

    private String extensionFor(String mimeType) {
        if (mimeType == null) {
            return "webm";
        }
        String lower = mimeType.toLowerCase();
        if (lower.contains("wav")) {
            return "wav";
        }
        if (lower.contains("m4a") || lower.contains("mp4")) {
            return "m4a";
        }
        if (lower.contains("mpeg") || lower.contains("mp3")) {
            return "mp3";
        }
        if (lower.contains("ogg")) {
            return "ogg";
        }
        return "webm";
    }
}
