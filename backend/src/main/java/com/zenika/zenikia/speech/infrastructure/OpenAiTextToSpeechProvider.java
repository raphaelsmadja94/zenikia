package com.zenika.zenikia.speech.infrastructure;

import com.zenika.zenikia.shared.domain.AiProviderException;
import com.zenika.zenikia.speech.application.TextToSpeechProvider;
import com.zenika.zenikia.speech.domain.AudioOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.stereotype.Component;

/**
 * {@link TextToSpeechProvider} backed by OpenAI TTS via Spring AI. Not wired
 * to the UI by default in the POC (see {@link TextToSpeechProvider}
 * javadoc) but fully functional — exposed through {@code speech.api} for
 * demonstration/future use.
 */
@Component
class OpenAiTextToSpeechProvider implements TextToSpeechProvider {

    private static final Logger log = LoggerFactory.getLogger(OpenAiTextToSpeechProvider.class);

    private final OpenAiAudioSpeechModel speechModel;

    OpenAiTextToSpeechProvider(OpenAiAudioSpeechModel speechModel) {
        this.speechModel = speechModel;
    }

    @Override
    public AudioOutput synthesize(String text) {
        try {
            byte[] audio = speechModel.call(text);
            return new AudioOutput(audio, "audio/mpeg");
        } catch (Exception e) {
            log.warn("Text-to-speech call failed: {}", e.getMessage());
            throw new AiProviderException("Échec de la synthèse vocale : " + e.getMessage(), e);
        }
    }
}
