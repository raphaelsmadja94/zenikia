package com.zenika.zenikia.speech.application;

/**
 * Context for {@link TranscriptCorrector}: the target skill and question give the model enough
 * of a hint to recognize garbled technical jargon (e.g. "levain driven" → "event-driven") without
 * needing a hardcoded vocabulary list.
 */
public record TranscriptCorrectionContext(String rawTranscript, String targetSkill, String questionText) {
}
