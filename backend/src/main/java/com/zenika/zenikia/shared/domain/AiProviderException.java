package com.zenika.zenikia.shared.domain;

/**
 * Raised when an AI/STT/TTS provider adapter fails (missing credentials,
 * network error, unparsable structured output...). The domain never catches
 * provider-specific exceptions directly; adapters translate them into this
 * type so the application layer stays provider-agnostic.
 */
public class AiProviderException extends ZenikiaException {

    public AiProviderException(String message) {
        super(message);
    }

    public AiProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
