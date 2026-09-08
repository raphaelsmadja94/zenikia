package com.zenika.zenikia.shared.infrastructure;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;

/**
 * Structured-output call used by every Spring AI adapter in this codebase instead of
 * {@code ChatClient...call().entity(Class)} directly. Behaves the same way (format instructions
 * from {@link BeanOutputConverter} appended to the prompt, same JSON→bean conversion) but adds
 * one thing {@code entity(...)} doesn't give us: a chance to repair a response a smaller/local
 * model cut off mid-object (see {@link JsonRepair}) before giving up on that attempt, on top of
 * the one full retry {@link AiCallRetry} already provides. Never falls back to manual, ad-hoc
 * text parsing — {@link BeanOutputConverter} still owns the actual JSON→bean mapping.
 */
public final class StructuredAiCall {

    private StructuredAiCall() {
    }

    public static <T> T call(ChatClient chatClient, String userPrompt, Class<T> responseType) {
        BeanOutputConverter<T> converter = new BeanOutputConverter<>(responseType);
        String fullPrompt = userPrompt + System.lineSeparator() + System.lineSeparator() + converter.getFormat();

        return AiCallRetry.withOneRetry(() -> {
            String raw = chatClient.prompt().user(fullPrompt).call().content();
            return parse(converter, raw);
        });
    }

    private static <T> T parse(BeanOutputConverter<T> converter, String raw) {
        if (raw == null || raw.isBlank()) {
            throw new IllegalStateException("La réponse IA est vide.");
        }
        try {
            return converter.convert(raw);
        } catch (RuntimeException firstAttemptFailure) {
            return converter.convert(JsonRepair.closeTruncatedJson(raw));
        }
    }
}
