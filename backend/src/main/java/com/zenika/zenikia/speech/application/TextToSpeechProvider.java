package com.zenika.zenikia.speech.application;

import com.zenika.zenikia.speech.domain.AudioOutput;

/**
 * Port: synthesizes speech from text. Implemented via OpenAI TTS in
 * infrastructure. For the POC, the UI defaults to the browser's
 * {@code speechSynthesis} API for reading questions aloud (zero latency,
 * zero cost); this port exists so a real server-side voice can be swapped
 * in later without touching application/domain code.
 */
public interface TextToSpeechProvider {

    AudioOutput synthesize(String text);
}
