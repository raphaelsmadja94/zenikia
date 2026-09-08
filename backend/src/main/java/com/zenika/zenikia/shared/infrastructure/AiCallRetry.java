package com.zenika.zenikia.shared.infrastructure;

import java.util.function.Supplier;

/**
 * Retries a flaky structured-output AI call once before giving up. LLM output is stochastic —
 * a smaller/local model in particular can occasionally cut its JSON off mid-object (see
 * README "Run for free with Ollama") for no reason tied to the input at all; a single retry
 * meaningfully improves reliability without the complexity of a full backoff policy. Every
 * Spring AI adapter in this codebase should route its {@code ChatClient...entity(...)} call
 * through this rather than calling it directly.
 */
public final class AiCallRetry {

    private AiCallRetry() {
    }

    public static <T> T withOneRetry(Supplier<T> call) {
        try {
            return call.get();
        } catch (RuntimeException firstAttemptFailure) {
            return call.get();
        }
    }
}
