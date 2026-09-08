package com.zenika.zenikia.interview.domain;

/**
 * The adaptive decision applied after assessing one answer. An LLM may be
 * consulted upstream, but this transition is ultimately decided by
 * {@link InterviewSession} business rules — never blindly trusted from a
 * model response.
 */
public enum InterviewNextAction {
    /** The answer was incorrect or off-topic: ask a simpler, clarifying question on the same topic. */
    CLARIFY,
    /** The answer was partial: go one level deeper on the same topic, same difficulty. */
    DEEPEN,
    /** The answer was correct: push further on the same topic, one difficulty level up. */
    CHALLENGE,
    /** The answer was advanced: ask about trade-offs/alternatives, one difficulty level up. */
    TRADE_OFF,
    /** The topic has been sufficiently covered: move to a different skill. */
    CHANGE_TOPIC,
    /** Enough signal has been gathered: end the interview. */
    FINISH
}
