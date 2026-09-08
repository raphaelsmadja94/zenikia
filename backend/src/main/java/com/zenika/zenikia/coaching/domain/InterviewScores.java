package com.zenika.zenikia.coaching.domain;

/**
 * The three scores the final report must ALWAYS show separately (spec
 * §16) — never a single number that would hide the difference between
 * knowing something and being able to explain/defend it.
 *
 * @param technicalMastery        /10, derived only from technical assessments
 * @param communication           /10, derived only from communication assessments
 * @param interviewEffectiveness  /10, composite (see {@link FinalReportScoring})
 */
public record InterviewScores(double technicalMastery, double communication, double interviewEffectiveness) {
}
