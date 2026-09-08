package com.zenika.zenikia.speech.application;

import com.zenika.zenikia.speech.domain.Transcript;

/**
 * Port: repairs obvious speech-recognition mistakes on technical jargon (e.g. free browser
 * speech recognition mishearing "event-driven" as "levain driven") — nothing else. Implementations
 * must NOT rewrite, restructure, or improve the answer itself: doing so would mean the technical/
 * communication assessment stops judging what the candidate actually said. On any doubt or
 * failure, an implementation should return the original transcript unchanged rather than risk
 * corrupting it — this port is a best-effort quality-of-life improvement, not a critical step.
 */
public interface TranscriptCorrector {

    Transcript correct(TranscriptCorrectionContext context);
}
