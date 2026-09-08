package com.zenika.zenikia.interview.api;

import com.zenika.zenikia.interview.application.AnswerOutcome;
import com.zenika.zenikia.interview.application.InterviewOrchestrationService;
import com.zenika.zenikia.interview.application.RetryOutcome;
import com.zenika.zenikia.interview.domain.InterviewAnswer;
import com.zenika.zenikia.interview.domain.InterviewPersona;
import com.zenika.zenikia.interview.domain.InterviewSession;
import com.zenika.zenikia.interview.domain.InterviewTurn;
import com.zenika.zenikia.speech.domain.AudioInput;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Locale;

/**
 * Thin controller: parses request shapes and maps domain results to
 * response DTOs. All orchestration lives in
 * {@link InterviewOrchestrationService}.
 */
@RestController
class InterviewController {

    private final InterviewOrchestrationService orchestrationService;

    InterviewController(InterviewOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping("/api/interviews")
    InterviewSessionResponse start(@Valid @RequestBody StartInterviewRequest request) {
        InterviewPersona persona = parsePersona(request.persona());
        InterviewSession session = orchestrationService.startInterview(request.candidateProfileId(), persona);
        return InterviewSessionResponse.from(session);
    }

    @GetMapping("/api/interviews/{id}")
    InterviewSessionResponse get(@PathVariable String id) {
        return InterviewSessionResponse.from(orchestrationService.getSession(id));
    }

    @PostMapping("/api/interviews/{id}/answers")
    AnswerResultResponse submitAnswer(
            @PathVariable String id,
            @RequestParam String turnId,
            @RequestParam("audio") MultipartFile audio,
            @RequestParam double durationSeconds,
            @RequestParam(required = false) String transcript
    ) {
        AudioInput audioInput = toAudioInput(audio, durationSeconds);
        AnswerOutcome outcome = orchestrationService.submitAnswer(id, turnId, audioInput, transcript);
        return toAnswerResult(outcome);
    }

    @PostMapping("/api/interviews/{id}/retry")
    RetryResultResponse retry(
            @PathVariable String id,
            @RequestParam String turnId,
            @RequestParam("audio") MultipartFile audio,
            @RequestParam double durationSeconds,
            @RequestParam(required = false) String transcript
    ) {
        AudioInput audioInput = toAudioInput(audio, durationSeconds);
        RetryOutcome outcome = orchestrationService.retryAnswer(id, turnId, audioInput, transcript);
        return toRetryResult(outcome);
    }

    @PostMapping("/api/interviews/{id}/finish")
    InterviewSessionResponse finish(@PathVariable String id) {
        return InterviewSessionResponse.from(orchestrationService.finishInterview(id));
    }

    @GetMapping("/api/interviews/{id}/report")
    FinalReportResponse report(@PathVariable String id) {
        return FinalReportResponse.from(orchestrationService.buildReport(id));
    }

    private AnswerResultResponse toAnswerResult(AnswerOutcome outcome) {
        InterviewTurn answered = outcome.session().getTurn(outcome.answeredTurnId());
        String transcript = answered.answer().map(InterviewAnswer::transcript).orElse("");

        InterviewQuestionResponse nextQuestion = outcome.nextTurnId() == null
                ? null
                : InterviewQuestionResponse.from(outcome.nextTurnId(), outcome.session().getTurn(outcome.nextTurnId()).question());

        return new AnswerResultResponse(
                answered.id(),
                transcript,
                answered.technicalAssessment().map(TechnicalAssessmentResponse::from).orElse(null),
                answered.communicationAssessment().map(CommunicationAssessmentResponse::from).orElse(null),
                answered.coachingFeedback().map(CoachingFeedbackResponse::from).orElse(null),
                answered.nextAction().map(Enum::name).orElse(null),
                nextQuestion,
                nextQuestion == null
        );
    }

    private RetryResultResponse toRetryResult(RetryOutcome outcome) {
        InterviewTurn original = outcome.session().getTurn(outcome.originalTurnId());
        InterviewTurn retryTurn = outcome.session().getTurn(outcome.retryTurnId());
        String transcript = retryTurn.answer().map(InterviewAnswer::transcript).orElse("");

        var originalComm = original.communicationAssessment().orElseThrow();
        var retryComm = retryTurn.communicationAssessment().orElseThrow();
        var originalTech = original.technicalAssessment().orElseThrow();
        var retryTech = retryTurn.technicalAssessment().orElseThrow();

        RetryComparisonResponse comparison = new RetryComparisonResponse(
                originalComm.clarity(), retryComm.clarity(),
                originalComm.concision(), retryComm.concision(),
                originalComm.totalFillerWordCount(), retryComm.totalFillerWordCount(),
                originalTech.averageScore(), retryTech.averageScore()
        );

        return new RetryResultResponse(
                original.id(),
                retryTurn.id(),
                transcript,
                TechnicalAssessmentResponse.from(retryTech),
                CommunicationAssessmentResponse.from(retryComm),
                retryTurn.coachingFeedback().map(CoachingFeedbackResponse::from).orElse(null),
                comparison
        );
    }

    private AudioInput toAudioInput(MultipartFile audio, double durationSeconds) {
        if (audio.isEmpty()) {
            throw new IllegalArgumentException("Le fichier audio est vide.");
        }
        if (durationSeconds <= 0) {
            throw new IllegalArgumentException("durationSeconds doit être strictement positif.");
        }
        try {
            return new AudioInput(audio.getBytes(), audio.getContentType(), durationSeconds);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible de lire le fichier audio envoyé.", e);
        }
    }

    private InterviewPersona parsePersona(String raw) {
        try {
            return InterviewPersona.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Persona inconnue : " + raw);
        }
    }
}
