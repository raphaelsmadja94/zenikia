package com.zenika.zenikia.shared.infrastructure;

import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * Loads a `.st` prompt template from the classpath and substitutes
 * {@code {placeholder}} tokens. Used by every AI-backed adapter so prompts
 * stay in {@code resources/prompts/*.st} instead of being built as Java
 * string concatenation (see CLAUDE.md prompt-engineering rule).
 */
public final class PromptTemplateLoader {

    private PromptTemplateLoader() {
    }

    public static String load(Resource resource) {
        try {
            return resource.getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible de charger le template de prompt " + resource, e);
        }
    }

    public static String render(String template, Map<String, String> variables) {
        String result = template;
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue() == null ? "" : entry.getValue());
        }
        return result;
    }

    /** Formats a list for prompt display, with an explicit "aucun" fallback rather than an empty/misleading string. */
    public static String joinOrNone(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "aucun";
        }
        return String.join(", ", values);
    }
}
