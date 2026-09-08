/** Mirrors backend `interview.api` DTOs. */

export type InterviewPersona = 'RECRUITER' | 'DEVELOPER' | 'TECH_LEAD' | 'CTO';

export type InterviewNextAction = 'CLARIFY' | 'DEEPEN' | 'CHALLENGE' | 'TRADE_OFF' | 'CHANGE_TOPIC' | 'FINISH';

export type QuestionType =
  | 'OPENING'
  | 'TECHNICAL_CONCEPT'
  | 'EXPERIENCE_STORY'
  | 'CLAIM_CHALLENGE'
  | 'TRADE_OFF'
  | 'BEHAVIORAL_STAR';

export interface InterviewQuestionResponse {
  turnId: string;
  questionId: string;
  text: string;
  targetSkill: string;
  difficulty: number;
  questionType: QuestionType;
}

export interface InterviewSessionResponse {
  id: string;
  persona: InterviewPersona;
  status: 'IN_PROGRESS' | 'COMPLETED';
  candidateRole: string;
  currentDifficulty: number;
  currentTargetSkill: string;
  totalTurns: number;
  currentQuestion: InterviewQuestionResponse | null;
}

export interface TechnicalAssessmentResponse {
  correctness: number;
  depth: number;
  reasoning: number;
  realWorldExperience: number;
  examples: number;
  tradeOffThinking: number;
  productionAwareness: number;
  averageScore: number;
  strengths: string[];
  weaknesses: string[];
  missingConcepts: string[];
}

export interface CommunicationAssessmentResponse {
  durationSeconds: number;
  wordCount: number;
  wordsPerMinute: number;
  fillerWords: Record<string, number>;
  totalFillerWordCount: number;
  repetitionCount: number;
  longPauseCount: number;
  clarity: number;
  structure: number;
  concision: number;
  fluency: number;
  impact: number;
  vulgarisation: number;
  adaptationToPersona: number;
  averageScore: number;
  strengths: string[];
  weaknesses: string[];
}

export interface StarBreakdownResponse {
  situationPresent: boolean;
  taskPresent: boolean;
  actionPresent: boolean;
  resultPresent: boolean;
  note: string;
}

export interface CoachingFeedbackResponse {
  observation: string;
  recommendation: string;
  priorityActions: string[];
  starBreakdown: StarBreakdownResponse | null;
  /** Shown only if the user explicitly asks to see it — see interview.page.html. */
  modelAnswer: string;
}

export interface AnswerResultResponse {
  turnId: string;
  transcript: string;
  technicalAssessment: TechnicalAssessmentResponse;
  communicationAssessment: CommunicationAssessmentResponse;
  coachingFeedback: CoachingFeedbackResponse;
  nextAction: InterviewNextAction;
  nextQuestion: InterviewQuestionResponse | null;
  interviewFinished: boolean;
}

export interface RetryComparisonResponse {
  clarityBefore: number;
  clarityAfter: number;
  concisionBefore: number;
  concisionAfter: number;
  fillerWordsBefore: number;
  fillerWordsAfter: number;
  technicalScoreBefore: number;
  technicalScoreAfter: number;
}

export interface RetryResultResponse {
  originalTurnId: string;
  retryTurnId: string;
  transcript: string;
  technicalAssessment: TechnicalAssessmentResponse;
  communicationAssessment: CommunicationAssessmentResponse;
  coachingFeedback: CoachingFeedbackResponse;
  comparison: RetryComparisonResponse;
}

export interface FinalReportResponse {
  technicalMastery: number;
  communication: number;
  interviewEffectiveness: number;
  topStrengths: string[];
  topPriorities: string[];
  recommendedNextSteps: string[];
  disclaimer: string;
}
