package com.zenika.zenikia.interview.application;

import com.zenika.zenikia.assessment.application.TechnicalAnswerEvaluator;
import com.zenika.zenikia.assessment.application.TechnicalEvaluationContext;
import com.zenika.zenikia.assessment.domain.TechnicalAssessment;
import com.zenika.zenikia.coaching.application.CoachingContext;
import com.zenika.zenikia.coaching.application.CoachingFeedbackGenerator;
import com.zenika.zenikia.coaching.application.FinalReportContext;
import com.zenika.zenikia.coaching.application.FinalReportService;
import com.zenika.zenikia.coaching.domain.CoachingFeedback;
import com.zenika.zenikia.coaching.domain.FinalReport;
import com.zenika.zenikia.communication.application.CommunicationAssessmentService;
import com.zenika.zenikia.communication.domain.CommunicationAssessment;
import com.zenika.zenikia.cv.application.CvAnalysisService;
import com.zenika.zenikia.cv.domain.CandidateProfile;
import com.zenika.zenikia.interview.domain.Difficulty;
import com.zenika.zenikia.interview.domain.InterviewContext;
import com.zenika.zenikia.interview.domain.InterviewNextAction;
import com.zenika.zenikia.interview.domain.InterviewPersona;
import com.zenika.zenikia.interview.domain.InterviewQuestion;
import com.zenika.zenikia.interview.domain.InterviewSession;
import com.zenika.zenikia.interview.domain.InterviewTurn;
import com.zenika.zenikia.interview.domain.QuestionType;
import com.zenika.zenikia.interview.domain.SkillRotationPolicy;
import com.zenika.zenikia.shared.domain.ResourceNotFoundException;
import com.zenika.zenikia.speech.application.SpeechToTextProvider;
import com.zenika.zenikia.speech.application.TranscriptCorrectionContext;
import com.zenika.zenikia.speech.application.TranscriptCorrector;
import com.zenika.zenikia.speech.domain.AudioInput;
import com.zenika.zenikia.speech.domain.AudioMetadata;
import com.zenika.zenikia.speech.domain.Transcript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Orchestrates the full adaptive interview loop: question → answer → STT →
 * technical assessment + communication assessment (independent) → coaching
 * feedback → adaptive next action → next question.
 *
 * <p>No business rule lives here beyond wiring — difficulty/topic/next-
 * action decisions belong to {@code interview.domain} policies, and each
 * assessment's rubric belongs to its own module.
 */
@Service
public class InterviewOrchestrationService {

    private static final int CONTEXT_CONCEPT_LIMIT = 5;
    /**
     * Caps how much of the PREVIOUS answer's transcript rides along into the next question's
     * context — it's there for continuity, not for scoring, so a preview is enough. Without this,
     * a long spoken answer (a local model's smaller context window makes this bite much sooner
     * than with OpenAI) can leave too little room for the model to actually produce its response,
     * truncating the JSON output mid-object.
     */
    private static final int LAST_ANSWER_CONTEXT_CHAR_LIMIT = 1500;

    private final InterviewSessionRepository sessionRepository;
    private final CvAnalysisService cvAnalysisService;
    private final InterviewQuestionGenerator questionGenerator;
    private final SpeechToTextProvider speechToTextProvider;
    private final TranscriptCorrector transcriptCorrector;
    private final TechnicalAnswerEvaluator technicalAnswerEvaluator;
    private final CommunicationAssessmentService communicationAssessmentService;
    private final CoachingFeedbackGenerator coachingFeedbackGenerator;
    private final FinalReportService finalReportService;

    public InterviewOrchestrationService(
            InterviewSessionRepository sessionRepository,
            CvAnalysisService cvAnalysisService,
            InterviewQuestionGenerator questionGenerator,
            SpeechToTextProvider speechToTextProvider,
            TranscriptCorrector transcriptCorrector,
            TechnicalAnswerEvaluator technicalAnswerEvaluator,
            CommunicationAssessmentService communicationAssessmentService,
            CoachingFeedbackGenerator coachingFeedbackGenerator,
            FinalReportService finalReportService
    ) {
        this.sessionRepository = sessionRepository;
        this.cvAnalysisService = cvAnalysisService;
        this.questionGenerator = questionGenerator;
        this.speechToTextProvider = speechToTextProvider;
        this.transcriptCorrector = transcriptCorrector;
        this.technicalAnswerEvaluator = technicalAnswerEvaluator;
        this.communicationAssessmentService = communicationAssessmentService;
        this.coachingFeedbackGenerator = coachingFeedbackGenerator;
        this.finalReportService = finalReportService;
    }

    public InterviewSession startInterview(String candidateProfileId, InterviewPersona persona) {
        CandidateProfile profile = cvAnalysisService.getProfile(candidateProfileId);
        String initialSkill = SkillRotationPolicy.nextTargetSkill(profile.skills(), List.of());

        InterviewSession session = InterviewSession.start(profile, persona, initialSkill);
        InterviewQuestion firstQuestion = generateQuestion(session, true);
        session.askQuestion(firstQuestion);

        return sessionRepository.save(session);
    }

    public InterviewSession getSession(String sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> ResourceNotFoundException.of("InterviewSession", sessionId));
    }

    public AnswerOutcome submitAnswer(String sessionId, String turnId, AudioInput audio, String clientProvidedTranscript) {
        InterviewSession session = getSession(sessionId);
        InterviewTurn turn = session.getTurn(turnId);

        Transcript transcript = resolveTranscript(audio, clientProvidedTranscript, turn.question().targetSkill(), turn.question().text());
        AudioMetadata audioMetadata = new AudioMetadata(audio.clientMeasuredDurationSeconds(), audio.mimeType(), audio.bytes().length);
        session.submitAnswer(turnId, com.zenika.zenikia.interview.domain.InterviewAnswer.now(turn.question().id(), transcript.text(), audioMetadata));

        AssessmentBundle bundle = assess(session, turn, transcript.text(), audioMetadata);
        InterviewNextAction action = session.applyAssessmentAndAdvance(
                turnId, bundle.technicalAssessment(), bundle.communicationAssessment(), bundle.coachingFeedback());

        String nextTurnId = null;
        if (action != InterviewNextAction.FINISH) {
            if (action == InterviewNextAction.CHANGE_TOPIC) {
                List<String> covered = session.turns().stream()
                        .filter(t -> !t.isRetry())
                        .map(t -> t.question().targetSkill())
                        .distinct()
                        .toList();
                session.changeTargetSkill(SkillRotationPolicy.nextTargetSkill(session.candidateProfile().skills(), covered));
            }
            InterviewQuestion nextQuestion = generateQuestion(session, false);
            nextTurnId = session.askQuestion(nextQuestion).id();
        }

        sessionRepository.save(session);
        return new AnswerOutcome(session, turnId, nextTurnId);
    }

    public RetryOutcome retryAnswer(String sessionId, String originalTurnId, AudioInput audio, String clientProvidedTranscript) {
        InterviewSession session = getSession(sessionId);
        InterviewTurn retryTurn = session.askRetryQuestion(originalTurnId);

        Transcript transcript = resolveTranscript(audio, clientProvidedTranscript, retryTurn.question().targetSkill(), retryTurn.question().text());
        AudioMetadata audioMetadata = new AudioMetadata(audio.clientMeasuredDurationSeconds(), audio.mimeType(), audio.bytes().length);
        session.submitAnswer(retryTurn.id(), com.zenika.zenikia.interview.domain.InterviewAnswer.now(retryTurn.question().id(), transcript.text(), audioMetadata));

        AssessmentBundle bundle = assess(session, retryTurn, transcript.text(), audioMetadata);
        session.applyAssessmentAndAdvance(retryTurn.id(), bundle.technicalAssessment(), bundle.communicationAssessment(), bundle.coachingFeedback());

        sessionRepository.save(session);
        return new RetryOutcome(session, originalTurnId, retryTurn.id());
    }

    /**
     * Prefers a transcript the frontend already produced (e.g. the browser's own speech
     * recognition — free, no server-side STT call) over calling {@link SpeechToTextProvider}.
     * This keeps the whole loop usable with zero paid API calls when the client can transcribe
     * itself, while leaving the server-side STT port fully wired for when it can't.
     *
     * <p>A client-provided transcript also goes through {@link TranscriptCorrector} — free
     * browser speech recognition is the weak link on technical jargon (e.g. "levain driven" for
     * "event-driven"), which server-side Whisper is already noticeably better at, so there is no
     * need to pay that extra correction pass on that path too.
     */
    private Transcript resolveTranscript(AudioInput audio, String clientProvidedTranscript, String targetSkill, String questionText) {
        if (clientProvidedTranscript != null && !clientProvidedTranscript.isBlank()) {
            return transcriptCorrector.correct(new TranscriptCorrectionContext(clientProvidedTranscript, targetSkill, questionText));
        }
        return speechToTextProvider.transcribe(audio);
    }

    public InterviewSession finishInterview(String sessionId) {
        InterviewSession session = getSession(sessionId);
        session.finish();
        return sessionRepository.save(session);
    }

    public FinalReport buildReport(String sessionId) {
        InterviewSession session = getSession(sessionId);

        // For a retried question, use its latest retry attempt in the aggregate (the candidate's final,
        // improved answer) rather than the original — the retry comparison itself stays available via
        // AnswerOutcome/RetryOutcome for the "before/after" UI.
        Map<String, InterviewTurn> effectiveByRootTurn = new LinkedHashMap<>();
        for (InterviewTurn turn : session.turns()) {
            if (!turn.isAssessed()) {
                continue;
            }
            String rootId = turn.isRetry() ? turn.retryOfTurnId().orElse(turn.id()) : turn.id();
            effectiveByRootTurn.put(rootId, turn);
        }
        List<InterviewTurn> effectiveTurns = new ArrayList<>(effectiveByRootTurn.values());

        double avgTechnical = effectiveTurns.stream()
                .mapToDouble(t -> t.technicalAssessment().map(TechnicalAssessment::averageScore).orElse(0.0))
                .average().orElse(0.0);
        double avgCommunication = effectiveTurns.stream()
                .mapToDouble(t -> t.communicationAssessment().map(CommunicationAssessment::averageScore).orElse(0.0))
                .average().orElse(0.0);
        double avgWpm = effectiveTurns.stream()
                .mapToDouble(t -> t.communicationAssessment().map(CommunicationAssessment::wordsPerMinute).orElse(0.0))
                .filter(wpm -> wpm > 0)
                .average().orElse(0.0);
        int totalFillerWords = effectiveTurns.stream()
                .mapToInt(t -> t.communicationAssessment().map(CommunicationAssessment::totalFillerWordCount).orElse(0))
                .sum();

        List<String> allStrengths = new ArrayList<>();
        List<String> allWeaknesses = new ArrayList<>();
        List<String> allMissingConcepts = new ArrayList<>();
        for (InterviewTurn turn : effectiveTurns) {
            turn.technicalAssessment().ifPresent(ta -> {
                allStrengths.addAll(ta.strengths());
                allWeaknesses.addAll(ta.weaknesses());
                allMissingConcepts.addAll(ta.missingConcepts());
            });
            turn.communicationAssessment().ifPresent(ca -> {
                allStrengths.addAll(ca.strengths());
                allWeaknesses.addAll(ca.weaknesses());
            });
        }

        FinalReportContext context = new FinalReportContext(
                session.persona().displayName(),
                session.candidateProfile().role(),
                effectiveTurns.size(),
                avgTechnical,
                avgCommunication,
                allStrengths,
                allWeaknesses,
                allMissingConcepts,
                totalFillerWords,
                avgWpm
        );

        return finalReportService.buildReport(context);
    }

    private AssessmentBundle assess(InterviewSession session, InterviewTurn turn, String transcriptText, AudioMetadata audioMetadata) {
        String candidateSummary = candidateSummary(session.candidateProfile());

        TechnicalEvaluationContext taContext = new TechnicalEvaluationContext(
                candidateSummary,
                session.persona().name(),
                turn.question().text(),
                turn.question().targetSkill(),
                session.currentDifficulty(),
                transcriptText,
                session.verifiedConcepts(CONTEXT_CONCEPT_LIMIT),
                session.weakConcepts(CONTEXT_CONCEPT_LIMIT)
        );
        TechnicalAssessment technicalAssessment = technicalAnswerEvaluator.evaluate(taContext);

        CommunicationAssessment communicationAssessment = communicationAssessmentService.assess(
                session.persona().name(), turn.question().text(), transcriptText, audioMetadata.durationSeconds());

        CoachingContext coachingContext = new CoachingContext(
                session.persona().name(),
                turn.question().text(),
                turn.question().questionType().name(),
                transcriptText,
                technicalAssessment,
                communicationAssessment
        );
        CoachingFeedback coachingFeedback = coachingFeedbackGenerator.generate(coachingContext);

        return new AssessmentBundle(technicalAssessment, communicationAssessment, coachingFeedback);
    }

    private InterviewQuestion generateQuestion(InterviewSession session, boolean opening) {
        // The spec gives this question's wording verbatim for RECRUITER's opener — asking a
        // smaller/local LLM to reproduce it exactly, in a prompt that also carries a specific
        // target skill and CV claims, was unreliable (it kept drifting back to that skill
        // instead of the literal presentation question). No need to ask at all: just build it.
        if (opening && session.persona() == InterviewPersona.RECRUITER) {
            return InterviewQuestion.create(
                    "Présente-toi en cinq minutes.",
                    "Présentation",
                    Difficulty.clamp(session.currentDifficulty()),
                    QuestionType.OPENING
            );
        }

        InterviewContext context = buildContext(session);
        QuestionGenerationContext generationContext = new QuestionGenerationContext(
                context, opening, session.candidateProfile().claims());

        InterviewQuestion generated = questionGenerator.generate(generationContext);

        // The application layer keeps control of difficulty/skill — never trust the LLM's own numbers for these.
        return new InterviewQuestion(
                generated.id(),
                generated.text(),
                session.currentTargetSkill(),
                Difficulty.clamp(session.currentDifficulty()),
                generated.questionType()
        );
    }

    private InterviewContext buildContext(InterviewSession session) {
        var lastTurn = session.lastTurn();
        String lastQuestionText = lastTurn.map(t -> t.question().text()).orElse("");
        String lastAnswerTranscript = lastTurn.flatMap(InterviewTurn::answer)
                .map(com.zenika.zenikia.interview.domain.InterviewAnswer::transcript)
                .map(text -> truncate(text, LAST_ANSWER_CONTEXT_CHAR_LIMIT))
                .orElse("");
        InterviewNextAction lastNextAction = lastTurn.flatMap(InterviewTurn::nextAction).orElse(null);

        return new InterviewContext(
                candidateSummary(session.candidateProfile()),
                session.persona(),
                session.currentTargetSkill(),
                session.verifiedConcepts(CONTEXT_CONCEPT_LIMIT),
                session.weakConcepts(CONTEXT_CONCEPT_LIMIT),
                session.currentDifficulty(),
                lastQuestionText,
                lastAnswerTranscript,
                lastNextAction
        );
    }

    private String candidateSummary(CandidateProfile profile) {
        return profile.experienceYears() != null
                ? "%s, %d ans d'expérience".formatted(profile.role(), profile.experienceYears())
                : profile.role();
    }

    private String truncate(String text, int maxChars) {
        return text.length() > maxChars ? text.substring(0, maxChars) + "…" : text;
    }

    private record AssessmentBundle(
            TechnicalAssessment technicalAssessment,
            CommunicationAssessment communicationAssessment,
            CoachingFeedback coachingFeedback
    ) {
    }
}
