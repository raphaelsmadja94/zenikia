package com.zenika.zenikia.cv.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Small helpers to read fields defensively from a {@link JsonNode}. Smaller/local LLMs (e.g. a
 * 4B Ollama model — see README "Run for free with Ollama") don't always honor a nested-object
 * schema as reliably as OpenAI's native structured outputs: a "skills" entry that should be
 * {@code {"name": "Kafka", ...}} sometimes comes back as the bare string {@code "Kafka"}. These
 * helpers absorb that variance so a smaller model doesn't crash the whole analysis with a
 * deserialization error — they don't replace structured outputs, they make the mapping tolerant
 * of a slightly-off-schema response.
 */
final class LenientJson {

    private LenientJson() {
    }

    static String text(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return (value == null || value.isNull()) ? null : value.asText();
    }

    /**
     * Same as {@link #text(JsonNode, String)} but tries several candidate field names in order
     * and returns the first present value. A local model doesn't always honor the exact camelCase
     * field name from the requested schema (e.g. it may write {@code "level"} or {@code "niveau"}
     * instead of {@code "claimedLevel"}) — this absorbs that drift instead of silently losing the
     * value to a strict single-key lookup.
     */
    static String firstText(JsonNode node, String... fields) {
        for (String field : fields) {
            String value = text(node, field);
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    /** Reads a field expected to be an array of strings, but tolerates a single bare string too. */
    static List<String> stringList(JsonNode node, String field) {
        JsonNode value = node.get(field);
        List<String> result = new ArrayList<>();
        if (value == null || value.isNull()) {
            return result;
        }
        if (value.isArray()) {
            value.forEach(element -> result.add(element.asText()));
        } else {
            result.add(value.asText());
        }
        return result;
    }
}
