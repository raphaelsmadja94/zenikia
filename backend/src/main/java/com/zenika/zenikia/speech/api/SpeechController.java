package com.zenika.zenikia.speech.api;

import com.zenika.zenikia.speech.application.TextToSpeechProvider;
import com.zenika.zenikia.speech.domain.AudioOutput;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Optional server-side speech synthesis endpoint. The default interview UI
 * reads questions aloud with the browser's own speech synthesis (see
 * {@link TextToSpeechProvider} javadoc) — this endpoint exists so the
 * OpenAI-backed voice can be demonstrated / swapped in later.
 */
@RestController
class SpeechController {

    private final TextToSpeechProvider textToSpeechProvider;

    SpeechController(TextToSpeechProvider textToSpeechProvider) {
        this.textToSpeechProvider = textToSpeechProvider;
    }

    @PostMapping("/api/speech/tts")
    ResponseEntity<byte[]> synthesize(@Valid @RequestBody TextToSpeechRequest request) {
        AudioOutput audio = textToSpeechProvider.synthesize(request.text());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(audio.mimeType()))
                .body(audio.bytes());
    }
}
