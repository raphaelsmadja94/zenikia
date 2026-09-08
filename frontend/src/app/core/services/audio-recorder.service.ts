import { Injectable, signal } from '@angular/core';

export type RecorderState = 'idle' | 'requesting' | 'recording' | 'stopped' | 'error';

export interface RecordingResult {
  blob: Blob;
  mimeType: string;
  /** Measured client-side from start() to stop() — the only duration source this POC treats as reliable. */
  durationSeconds: number;
}

/** Preferred MIME types in order — the browser picks the first one it actually supports. */
const PREFERRED_MIME_TYPES = ['audio/webm;codecs=opus', 'audio/webm', 'audio/ogg;codecs=opus', 'audio/mp4'];

/**
 * Wraps `MediaRecorder` + `getUserMedia` behind a small signal-based API.
 * Kept entirely in the frontend — the backend never sees raw microphone
 * access, only the finished audio blob.
 */
@Injectable({ providedIn: 'root' })
export class AudioRecorderService {
  readonly state = signal<RecorderState>('idle');
  readonly errorMessage = signal<string | null>(null);

  private mediaRecorder: MediaRecorder | null = null;
  private chunks: BlobPart[] = [];
  private stream: MediaStream | null = null;
  private startedAt = 0;

  async start(): Promise<void> {
    this.errorMessage.set(null);
    this.state.set('requesting');
    try {
      this.stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      const mimeType = PREFERRED_MIME_TYPES.find((type) => MediaRecorder.isTypeSupported(type));
      this.mediaRecorder = mimeType ? new MediaRecorder(this.stream, { mimeType }) : new MediaRecorder(this.stream);
      this.chunks = [];
      this.mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          this.chunks.push(event.data);
        }
      };
      this.startedAt = Date.now();
      this.mediaRecorder.start();
      this.state.set('recording');
    } catch (error) {
      this.state.set('error');
      this.errorMessage.set(
        "Impossible d'accéder au microphone. Vérifie les autorisations du navigateur pour ce site.",
      );
      throw error;
    }
  }

  stop(): Promise<RecordingResult> {
    return new Promise((resolve, reject) => {
      const recorder = this.mediaRecorder;
      if (!recorder || this.state() !== 'recording') {
        reject(new Error('Aucun enregistrement en cours.'));
        return;
      }

      recorder.onstop = () => {
        const durationSeconds = Math.max(0.1, (Date.now() - this.startedAt) / 1000);
        const mimeType = recorder.mimeType || 'audio/webm';
        const blob = new Blob(this.chunks, { type: mimeType });
        this.releaseStream();
        this.state.set('stopped');
        resolve({ blob, mimeType, durationSeconds });
      };
      recorder.stop();
    });
  }

  reset(): void {
    this.releaseStream();
    this.state.set('idle');
    this.errorMessage.set(null);
  }

  private releaseStream(): void {
    this.stream?.getTracks().forEach((track) => track.stop());
    this.stream = null;
    this.mediaRecorder = null;
  }
}
