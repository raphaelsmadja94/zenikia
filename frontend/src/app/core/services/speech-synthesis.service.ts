import { Injectable, signal } from '@angular/core';

/**
 * Browser-native TTS fallback (`window.speechSynthesis`) used to read
 * interview questions aloud. Chosen over the backend's OpenAI TTS port for
 * the default UI flow — zero latency, zero cost, no round trip. See
 * CLAUDE.md / README for the architecture note on the server-side port
 * that exists behind `POST /api/speech/tts` for later use.
 */
@Injectable({ providedIn: 'root' })
export class SpeechSynthesisService {
  readonly speaking = signal(false);
  readonly supported = typeof window !== 'undefined' && 'speechSynthesis' in window;

  speak(text: string): void {
    if (!this.supported || !text.trim()) {
      return;
    }
    window.speechSynthesis.cancel();
    const utterance = new SpeechSynthesisUtterance(text);
    utterance.lang = 'fr-FR';
    utterance.rate = 1;
    utterance.onstart = () => this.speaking.set(true);
    utterance.onend = () => this.speaking.set(false);
    utterance.onerror = () => this.speaking.set(false);
    window.speechSynthesis.speak(utterance);
  }

  stop(): void {
    if (this.supported) {
      window.speechSynthesis.cancel();
      this.speaking.set(false);
    }
  }
}
