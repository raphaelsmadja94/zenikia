import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { API_BASE_URL } from '../api-config';
import { CandidateProfileResponse } from '../models/candidate-profile.model';
import {
  AnswerResultResponse,
  FinalReportResponse,
  InterviewPersona,
  InterviewSessionResponse,
  RetryResultResponse,
} from '../models/interview.model';

/**
 * Thin HTTP client wrapper — one method per backend endpoint, no business
 * logic. Components/state services call this; it never calls itself.
 */
@Injectable({ providedIn: 'root' })
export class ApiService {
  private readonly http = inject(HttpClient);

  analyzeCv(file: File): Observable<CandidateProfileResponse> {
    const formData = new FormData();
    formData.append('file', file, file.name);
    return this.http.post<CandidateProfileResponse>(`${API_BASE_URL}/cvs/analyze`, formData);
  }

  getProfile(id: string): Observable<CandidateProfileResponse> {
    return this.http.get<CandidateProfileResponse>(`${API_BASE_URL}/cvs/${id}`);
  }

  startInterview(candidateProfileId: string, persona: InterviewPersona): Observable<InterviewSessionResponse> {
    return this.http.post<InterviewSessionResponse>(`${API_BASE_URL}/interviews`, { candidateProfileId, persona });
  }

  getSession(id: string): Observable<InterviewSessionResponse> {
    return this.http.get<InterviewSessionResponse>(`${API_BASE_URL}/interviews/${id}`);
  }

  /**
   * @param clientTranscript transcript already produced by the browser's own speech
   *   recognition (free, no server-side STT call needed) — omit to let the backend
   *   transcribe the audio itself via its `SpeechToTextProvider` port.
   */
  submitAnswer(
    sessionId: string,
    turnId: string,
    audio: Blob,
    durationSeconds: number,
    clientTranscript?: string,
  ): Observable<AnswerResultResponse> {
    const formData = this.buildAudioForm(turnId, audio, durationSeconds, clientTranscript);
    return this.http.post<AnswerResultResponse>(`${API_BASE_URL}/interviews/${sessionId}/answers`, formData);
  }

  retryAnswer(
    sessionId: string,
    turnId: string,
    audio: Blob,
    durationSeconds: number,
    clientTranscript?: string,
  ): Observable<RetryResultResponse> {
    const formData = this.buildAudioForm(turnId, audio, durationSeconds, clientTranscript);
    return this.http.post<RetryResultResponse>(`${API_BASE_URL}/interviews/${sessionId}/retry`, formData);
  }

  finishInterview(sessionId: string): Observable<InterviewSessionResponse> {
    return this.http.post<InterviewSessionResponse>(`${API_BASE_URL}/interviews/${sessionId}/finish`, {});
  }

  getReport(sessionId: string): Observable<FinalReportResponse> {
    return this.http.get<FinalReportResponse>(`${API_BASE_URL}/interviews/${sessionId}/report`);
  }

  private buildAudioForm(turnId: string, audio: Blob, durationSeconds: number, clientTranscript?: string): FormData {
    const formData = new FormData();
    formData.append('turnId', turnId);
    formData.append('durationSeconds', String(durationSeconds));
    formData.append('audio', audio, `answer.${this.extensionFor(audio.type)}`);
    if (clientTranscript && clientTranscript.trim()) {
      formData.append('transcript', clientTranscript.trim());
    }
    return formData;
  }

  private extensionFor(mimeType: string): string {
    if (mimeType.includes('wav')) return 'wav';
    if (mimeType.includes('ogg')) return 'ogg';
    if (mimeType.includes('mp4') || mimeType.includes('m4a')) return 'm4a';
    return 'webm';
  }
}
