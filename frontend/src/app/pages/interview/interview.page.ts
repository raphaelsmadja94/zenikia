import { DecimalPipe, KeyValuePipe } from '@angular/common';
import { Component, OnDestroy, OnInit, computed, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService } from '../../core/services/api.service';
import { AudioRecorderService } from '../../core/services/audio-recorder.service';
import { SpeechRecognitionService } from '../../core/services/speech-recognition.service';
import { SpeechSynthesisService } from '../../core/services/speech-synthesis.service';
import { AlertComponent, BadgeComponent, ButtonComponent, CardComponent, ProgressMeterComponent } from '../../shared/ui';
import {
  AnswerResultResponse,
  CoachingFeedbackResponse,
  CommunicationAssessmentResponse,
  InterviewQuestionResponse,
  InterviewSessionResponse,
  RetryResultResponse,
  TechnicalAssessmentResponse,
} from '../../core/models/interview.model';

export type InterviewUiState =
  | 'READY'
  | 'AI_SPEAKING'
  | 'LISTENING'
  | 'TRANSCRIBING'
  | 'ANALYZING'
  | 'FEEDBACK'
  | 'FINISHED';

interface DisplayedFeedback {
  transcript: string;
  technical: TechnicalAssessmentResponse;
  communication: CommunicationAssessmentResponse;
  coaching: CoachingFeedbackResponse;
  isRetry: boolean;
}

const PERSONA_LABELS: Record<string, string> = {
  RECRUITER: 'Recruiter',
  DEVELOPER: 'Developer',
  TECH_LEAD: 'Tech Lead',
  CTO: 'CTO',
};

@Component({
  selector: 'app-interview-page',
  imports: [DecimalPipe, KeyValuePipe, CardComponent, BadgeComponent, AlertComponent, ButtonComponent, ProgressMeterComponent],
  templateUrl: './interview.page.html',
  styleUrl: './interview.page.css',
})
export class InterviewPage implements OnInit, OnDestroy {
  readonly sessionId: string;

  readonly session = signal<InterviewSessionResponse | null>(null);
  readonly currentQuestion = signal<InterviewQuestionResponse | null>(null);
  readonly uiState = signal<InterviewUiState>('READY');
  readonly errorMessage = signal<string | null>(null);
  readonly elapsedSeconds = signal(0);
  readonly retrying = signal(false);
  readonly showModelAnswer = signal(false);

  private readonly answerResult = signal<AnswerResultResponse | null>(null);
  private readonly retryResult = signal<RetryResultResponse | null>(null);
  private timerHandle?: ReturnType<typeof setInterval>;

  readonly displayedFeedback = computed<DisplayedFeedback | null>(() => {
    const retry = this.retryResult();
    if (retry) {
      return {
        transcript: retry.transcript,
        technical: retry.technicalAssessment,
        communication: retry.communicationAssessment,
        coaching: retry.coachingFeedback,
        isRetry: true,
      };
    }
    const answer = this.answerResult();
    if (answer) {
      return {
        transcript: answer.transcript,
        technical: answer.technicalAssessment,
        communication: answer.communicationAssessment,
        coaching: answer.coachingFeedback,
        isRetry: false,
      };
    }
    return null;
  });

  readonly retryComparison = computed(() => this.retryResult()?.comparison ?? null);
  readonly canGoNext = computed(() => this.answerResult() !== null);
  readonly interviewFinished = computed(() => this.answerResult()?.interviewFinished ?? false);

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly api: ApiService,
    readonly recorder: AudioRecorderService,
    readonly tts: SpeechSynthesisService,
    readonly speechRecognition: SpeechRecognitionService,
  ) {
    this.sessionId = this.route.snapshot.paramMap.get('id') ?? '';
  }

  ngOnInit(): void {
    this.api.getSession(this.sessionId).subscribe({
      next: (session) => {
        this.session.set(session);
        this.currentQuestion.set(session.currentQuestion);
        if (session.status === 'COMPLETED' || !session.currentQuestion) {
          this.uiState.set('FINISHED');
        }
      },
      error: () => this.errorMessage.set("Impossible de charger cette session d'entretien."),
    });
  }

  ngOnDestroy(): void {
    this.stopTimer();
    this.tts.stop();
  }

  personaLabel(persona: string): string {
    return PERSONA_LABELS[persona] ?? persona;
  }

  speakQuestion(): void {
    const question = this.currentQuestion();
    if (!question) {
      return;
    }
    this.uiState.set('AI_SPEAKING');
    this.tts.speak(question.text);
    const check = setInterval(() => {
      if (!this.tts.speaking()) {
        clearInterval(check);
        if (this.uiState() === 'AI_SPEAKING') {
          this.uiState.set('READY');
        }
      }
    }, 200);
  }

  async startRecording(): Promise<void> {
    this.errorMessage.set(null);
    try {
      await this.recorder.start();
      // Free, browser-native transcription running alongside the recording — see
      // SpeechRecognitionService. No-op (silently) in browsers that don't support it; the
      // backend then falls back to server-side Whisper transcription.
      this.speechRecognition.start();
      this.uiState.set('LISTENING');
      this.elapsedSeconds.set(0);
      this.timerHandle = setInterval(() => this.elapsedSeconds.update((s) => s + 1), 1000);
    } catch {
      this.errorMessage.set(this.recorder.errorMessage() ?? "Impossible d'accéder au micro.");
    }
  }

  async stopAndSubmit(): Promise<void> {
    const question = this.currentQuestion();
    if (!question) {
      return;
    }
    this.stopTimer();
    this.uiState.set('TRANSCRIBING');
    const clientTranscript = this.speechRecognition.stop();
    const recording = await this.recorder.stop();
    this.uiState.set('ANALYZING');

    this.api.submitAnswer(this.sessionId, question.turnId, recording.blob, recording.durationSeconds, clientTranscript).subscribe({
      next: (result) => {
        this.answerResult.set(result);
        this.retryResult.set(null);
        this.showModelAnswer.set(false);
        this.uiState.set('FEEDBACK');
      },
      error: () => {
        this.errorMessage.set("L'analyse de ta réponse a échoué. Réessaie.");
        this.uiState.set('READY');
      },
    });
  }

  async startRetryRecording(): Promise<void> {
    this.retrying.set(true);
    await this.startRecording();
  }

  async stopAndSubmitRetry(): Promise<void> {
    const question = this.currentQuestion();
    if (!question) {
      return;
    }
    this.stopTimer();
    this.uiState.set('TRANSCRIBING');
    const clientTranscript = this.speechRecognition.stop();
    const recording = await this.recorder.stop();
    this.uiState.set('ANALYZING');

    this.api.retryAnswer(this.sessionId, question.turnId, recording.blob, recording.durationSeconds, clientTranscript).subscribe({
      next: (result) => {
        this.retryResult.set(result);
        this.retrying.set(false);
        this.showModelAnswer.set(false);
        this.uiState.set('FEEDBACK');
      },
      error: () => {
        this.retrying.set(false);
        this.errorMessage.set('Le nouvel essai a échoué. Réessaie.');
        this.uiState.set('FEEDBACK');
      },
    });
  }

  goToNextQuestion(): void {
    const next = this.answerResult()?.nextQuestion ?? null;
    if (!next) {
      this.router.navigate(['/report', this.sessionId]);
      return;
    }
    this.currentQuestion.set(next);
    this.answerResult.set(null);
    this.retryResult.set(null);
    this.showModelAnswer.set(false);
    this.recorder.reset();
    this.uiState.set('READY');
  }

  toggleModelAnswer(): void {
    this.showModelAnswer.update((shown) => !shown);
  }

  goToReport(): void {
    this.router.navigate(['/report', this.sessionId]);
  }

  private stopTimer(): void {
    if (this.timerHandle) {
      clearInterval(this.timerHandle);
      this.timerHandle = undefined;
    }
  }
}
