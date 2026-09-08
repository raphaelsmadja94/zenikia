package com.zenika.zenikia.interview.domain;

/** Broad shape of an interview question, used to steer generation and coaching. */
public enum QuestionType {
    /** Opening question of the session (e.g. "Présente-toi en cinq minutes."). */
    OPENING,
    /** Asks how/why a technical concept works. */
    TECHNICAL_CONCEPT,
    /** Asks the candidate to recount a real experience/mission. */
    EXPERIENCE_STORY,
    /** Directly challenges a vague or strong claim from the CV (Evidence / Claim Challenger). */
    CLAIM_CHALLENGE,
    /** Asks about alternatives, compromises, limits of a chosen solution. */
    TRADE_OFF,
    /** Behavioral question expecting a Situation/Task/Action/Result answer. */
    BEHAVIORAL_STAR
}
