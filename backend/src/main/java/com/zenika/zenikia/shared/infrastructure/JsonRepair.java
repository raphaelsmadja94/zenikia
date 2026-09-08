package com.zenika.zenikia.shared.infrastructure;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Last-resort repair for a JSON object/array cut off mid-generation — a small local model
 * (see README "Run for free with Ollama") occasionally stops emitting tokens right after
 * closing its last string value, without ever writing the closing {@code }}/{@code ]}. This
 * balances brackets and, if generation stopped mid-string, closes that string too, so the
 * response has a real chance of parsing instead of failing outright.
 *
 * <p>This is a recovery heuristic, not a JSON parser — it does not validate the result is
 * semantically correct, only that it is structurally closed. Callers still need normal JSON
 * parsing (and should still treat a parse failure on the repaired text as a real failure).
 */
final class JsonRepair {

    private JsonRepair() {
    }

    static String closeTruncatedJson(String rawText) {
        String text = stripCodeFence(rawText);

        Deque<Character> expectedClosers = new ArrayDeque<>();
        boolean inString = false;
        boolean escaped = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (c == '\\') {
                    escaped = true;
                } else if (c == '"') {
                    inString = false;
                }
                continue;
            }
            switch (c) {
                case '"' -> inString = true;
                case '{' -> expectedClosers.push('}');
                case '[' -> expectedClosers.push(']');
                case '}', ']' -> {
                    if (!expectedClosers.isEmpty()) {
                        expectedClosers.pop();
                    }
                }
                default -> {
                }
            }
        }

        StringBuilder repaired = new StringBuilder(text);
        if (inString) {
            repaired.append('"');
        }
        while (!expectedClosers.isEmpty()) {
            repaired.append(expectedClosers.pop());
        }
        return repaired.toString();
    }

    private static String stripCodeFence(String text) {
        String trimmed = text.strip();
        if (!trimmed.startsWith("```")) {
            return trimmed;
        }
        int firstNewline = trimmed.indexOf('\n');
        String withoutOpeningFence = firstNewline >= 0 ? trimmed.substring(firstNewline + 1) : trimmed;
        int fenceEnd = withoutOpeningFence.lastIndexOf("```");
        return (fenceEnd >= 0 ? withoutOpeningFence.substring(0, fenceEnd) : withoutOpeningFence).strip();
    }
}
