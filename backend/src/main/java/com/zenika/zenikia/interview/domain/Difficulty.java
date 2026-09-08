package com.zenika.zenikia.interview.domain;

/**
 * Difficulty scale used across the interview:
 * <pre>
 * 1 = fondamentaux
 * 2 = utilisation
 * 3 = opérationnel
 * 4 = avancé / production
 * 5 = architecture / trade-offs
 * </pre>
 * A good answer may raise the difficulty; a partial answer keeps it flat
 * (deepen); a bad answer never causes a brutal drop — see
 * {@link InterviewSession}'s adaptive rules.
 */
public final class Difficulty {

    public static final int MIN = 1;
    public static final int MAX = 5;
    public static final int DEFAULT_STARTING = 2;

    private Difficulty() {
    }

    public static int clamp(int value) {
        return Math.max(MIN, Math.min(MAX, value));
    }
}
