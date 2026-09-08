import { Injectable, signal } from '@angular/core';

/**
 * Minimal shape of the non-standard Web Speech API `SpeechRecognition`
 * interface — not part of TypeScript's DOM lib, so declared locally rather
 * than reaching for `any` everywhere it's used.
 */
interface SpeechRecognitionLike extends EventTarget {
  lang: string;
  continuous: boolean;
  interimResults: boolean;
  start(): void;
  stop(): void;
  onresult: ((event: SpeechRecognitionEventLike) => void) | null;
  onerror: ((event: unknown) => void) | null;
  onend: (() => void) | null;
}

interface SpeechRecognitionEventLike {
  resultIndex: number;
  results: ArrayLike<{ isFinal: boolean; 0: { transcript: string } }>;
}

type SpeechRecognitionConstructor = new () => SpeechRecognitionLike;

function resolveConstructor(): SpeechRecognitionConstructor | null {
  const w = window as unknown as Record<string, unknown>;
  return (w['SpeechRecognition'] as SpeechRecognitionConstructor | undefined)
    ?? (w['webkitSpeechRecognition'] as SpeechRecognitionConstructor | undefined)
    ?? null;
}

/**
 * Free, browser-native speech-to-text (used to avoid any dependency on a
 * paid STT API for the POC's default path — see backend
 * `speech.application.SpeechToTextProvider` javadoc). Reliable support is
 * essentially Chrome/Edge only; other browsers fall back to server-side
 * Whisper transcription (which then needs `OPENAI_API_KEY` + credits).
 *
 * Note: in Chrome, this still sends audio to Google's speech recognition
 * service in the background — it's free and needs no API key of ours, but
 * it isn't a fully offline/local computation.
 */
@Injectable({ providedIn: 'root' })
export class SpeechRecognitionService {
  private readonly ctor = resolveConstructor();
  readonly supported = this.ctor !== null;

  readonly listening = signal(false);
  readonly transcript = signal('');

  private recognition: SpeechRecognitionLike | null = null;
  private finalText = '';

  start(): void {
    if (!this.ctor) {
      return;
    }
    this.finalText = '';
    this.transcript.set('');

    const recognition = new this.ctor();
    recognition.lang = 'fr-FR';
    recognition.continuous = true;
    recognition.interimResults = true;
    recognition.onresult = (event) => {
      let interim = '';
      for (let i = event.resultIndex; i < event.results.length; i++) {
        const result = event.results[i];
        if (result.isFinal) {
          this.finalText += result[0].transcript + ' ';
        } else {
          interim += result[0].transcript;
        }
      }
      this.transcript.set((this.finalText + interim).trim());
    };
    recognition.onerror = () => {
      // Non-fatal: the backend falls back to server-side STT if no transcript is produced.
    };
    recognition.onend = () => this.listening.set(false);

    this.recognition = recognition;
    recognition.start();
    this.listening.set(true);
  }

  /** Stops listening and returns whatever transcript was accumulated (may be empty). */
  stop(): string {
    this.recognition?.stop();
    this.recognition = null;
    this.listening.set(false);
    return this.transcript().trim();
  }
}
