package com.zenika.zenikia.interview.domain;

import com.zenika.zenikia.assessment.domain.TechnicalAssessment;
import com.zenika.zenikia.coaching.domain.CoachingFeedback;
import com.zenika.zenikia.communication.domain.CommunicationAssessment;
import com.zenika.zenikia.cv.domain.CandidateProfile;
import com.zenika.zenikia.shared.domain.InvalidStateException;
import com.zenika.zenikia.shared.domain.ResourceNotFoundException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

/**
 * Aggregate root for one interview. A mutable class (not a record) on
 * purpose: unlike the value objects around it, a session has a lifecycle
 * and behavior — it is the only place that mutates {@link InterviewTurn}
 * collections and advances difficulty/topic, applying
 * {@link InterviewProgressionPolicy}.
 */
public final class InterviewSession {

    private final String id;
    private final CandidateProfile candidateProfile;
    private final InterviewPersona persona;
    private final Instant startedAt;
    private final List<InterviewTurn> turns = new ArrayList<>();

    private InterviewStatus status;
    private Instant completedAt;
    private int currentDifficulty;
    private String currentTargetSkill;
    private int consecutiveClarifies;

    private InterviewSession(String id, CandidateProfile candidateProfile, InterviewPersona persona, Instant startedAt, String initialTargetSkill) {
        this.id = id;
        this.candidateProfile = candidateProfile;
        this.persona = persona;
        this.startedAt = startedAt;
        this.status = InterviewStatus.IN_PROGRESS;
        this.currentDifficulty = Difficulty.DEFAULT_STARTING;
        this.currentTargetSkill = initialTargetSkill;
        this.consecutiveClarifies = 0;
    }

    public static InterviewSession start(CandidateProfile candidateProfile, InterviewPersona persona, String initialTargetSkill) {
        return new InterviewSession(UUID.randomUUID().toString(), candidateProfile, persona, Instant.now(), initialTargetSkill);
    }

    public InterviewTurn askQuestion(InterviewQuestion question) {
        ensureInProgress();
        InterviewTurn turn = InterviewTurn.forQuestion(question);
        turns.add(turn);
        return turn;
    }

    public InterviewTurn askRetryQuestion(String originalTurnId) {
        ensureInProgress();
        InterviewTurn retryTurn = InterviewTurn.retryOf(getTurn(originalTurnId));
        turns.add(retryTurn);
        return retryTurn;
    }

    public InterviewTurn submitAnswer(String turnId, InterviewAnswer answer) {
        InterviewTurn turn = getTurn(turnId);
        turn.submitAnswer(answer);
        return turn;
    }

    /**
     * Applies the assessment to a turn and lets {@link InterviewProgressionPolicy}
     * decide + apply the next action (adjusting difficulty as a side effect).
     * Retry turns never move difficulty or turn counts — see spec §18.
     */
    public InterviewNextAction applyAssessmentAndAdvance(
            String turnId,
            TechnicalAssessment technicalAssessment,
            CommunicationAssessment communicationAssessment,
            CoachingFeedback coachingFeedback
    ) {
        InterviewTurn turn = getTurn(turnId);

        if (turn.isRetry()) {
            InterviewNextAction retryAction = InterviewProgressionPolicy.determineNextAction(technicalAssessment, 0, turns.size());
            turn.applyAssessment(technicalAssessment, communicationAssessment, coachingFeedback, retryAction);
            return retryAction;
        }

        int turnsOnSkill = countTurnsForSkill(turn.question().targetSkill());
        InterviewNextAction action = InterviewProgressionPolicy.determineNextAction(technicalAssessment, turnsOnSkill, turns.size());
        turn.applyAssessment(technicalAssessment, communicationAssessment, coachingFeedback, action);

        consecutiveClarifies = action == InterviewNextAction.CLARIFY ? consecutiveClarifies + 1 : 0;
        currentDifficulty = InterviewProgressionPolicy.nextDifficulty(currentDifficulty, action, consecutiveClarifies);
        if (action == InterviewNextAction.CLARIFY && consecutiveClarifies >= 2) {
            consecutiveClarifies = 0; // difficulty already eased off once, restart the streak count
        }
        if (action == InterviewNextAction.FINISH) {
            finish();
        }
        return action;
    }

    public void changeTargetSkill(String newTargetSkill) {
        this.currentTargetSkill = (newTargetSkill == null || newTargetSkill.isBlank()) ? this.currentTargetSkill : newTargetSkill;
    }

    public void finish() {
        if (status != InterviewStatus.COMPLETED) {
            status = InterviewStatus.COMPLETED;
            completedAt = Instant.now();
        }
    }

    private void ensureInProgress() {
        if (status != InterviewStatus.IN_PROGRESS) {
            throw new InvalidStateException("Cette session d'entretien est terminée.");
        }
    }

    private int countTurnsForSkill(String skill) {
        return (int) turns.stream()
                .filter(t -> !t.isRetry())
                .filter(t -> t.question().targetSkill().equalsIgnoreCase(skill))
                .count();
    }

    public InterviewTurn getTurn(String turnId) {
        return turns.stream()
                .filter(t -> t.id().equals(turnId))
                .findFirst()
                .orElseThrow(() -> ResourceNotFoundException.of("InterviewTurn", turnId));
    }

    public Optional<InterviewTurn> lastTurn() {
        return turns.isEmpty() ? Optional.empty() : Optional.of(turns.get(turns.size() - 1));
    }

    /** Concepts confirmed (technical strengths) across assessed, non-retry turns — most recent first, capped at {@code limit}. */
    public List<String> verifiedConcepts(int limit) {
        return collectConcepts(t -> t.technicalAssessment().map(TechnicalAssessment::strengths).orElse(List.of()), limit);
    }

    /** Weaknesses + missing concepts across assessed, non-retry turns — most recent first, capped at {@code limit}. */
    public List<String> weakConcepts(int limit) {
        return collectConcepts(
                t -> t.technicalAssessment().map(ta -> concat(ta.weaknesses(), ta.missingConcepts())).orElse(List.of()),
                limit
        );
    }

    private List<String> collectConcepts(Function<InterviewTurn, List<String>> extractor, int limit) {
        List<String> result = new ArrayList<>();
        for (int i = turns.size() - 1; i >= 0 && result.size() < limit; i--) {
            if (turns.get(i).isRetry()) {
                continue;
            }
            for (String concept : extractor.apply(turns.get(i))) {
                if (!result.contains(concept)) {
                    result.add(concept);
                }
                if (result.size() >= limit) {
                    break;
                }
            }
        }
        return result;
    }

    private static List<String> concat(List<String> a, List<String> b) {
        List<String> combined = new ArrayList<>(a);
        combined.addAll(b);
        return combined;
    }

    public String id() {
        return id;
    }

    public CandidateProfile candidateProfile() {
        return candidateProfile;
    }

    public InterviewPersona persona() {
        return persona;
    }

    public Instant startedAt() {
        return startedAt;
    }

    public Optional<Instant> completedAt() {
        return Optional.ofNullable(completedAt);
    }

    public InterviewStatus status() {
        return status;
    }

    public int currentDifficulty() {
        return currentDifficulty;
    }

    public String currentTargetSkill() {
        return currentTargetSkill;
    }

    public List<InterviewTurn> turns() {
        return List.copyOf(turns);
    }
}
