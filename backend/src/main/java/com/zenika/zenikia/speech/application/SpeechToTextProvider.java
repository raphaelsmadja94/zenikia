package com.zenika.zenikia.speech.application;

import com.zenika.zenikia.speech.domain.AudioInput;
import com.zenika.zenikia.speech.domain.Transcript;

/**
 * Port: transcribes audio into text. Implemented via OpenAI Whisper in
 * infrastructure. Never called directly from a controller.
 */
public interface SpeechToTextProvider {

    Transcript transcribe(AudioInput audio);
}
