package com.zenika.zenikia.shared.infrastructure;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Regression tests for the exact failure mode a smaller local model produces (see README
 * "Run for free with Ollama"): generation stops right after the last string value, before the
 * closing bracket(s).
 */
class JsonRepairTest {

    @Test
    void closeTruncatedJson_appendsTheMissingClosingBraceOfASimpleObject() {
        String truncated = """
                {
                  "questionType" : "CLARIFY",
                  "text" : "Qu'est-ce que tu entends par 'event-driven' ?\"""";

        String repaired = JsonRepair.closeTruncatedJson(truncated);

        assertThat(repaired).endsWith("}");
        assertThat(isBalanced(repaired)).isTrue();
    }

    @Test
    void closeTruncatedJson_closesAnUnterminatedStringBeforeClosingTheObject() {
        String truncated = "{\"text\": \"réponse coupée en pleine phr";

        String repaired = JsonRepair.closeTruncatedJson(truncated);

        assertThat(repaired).isEqualTo("{\"text\": \"réponse coupée en pleine phr\"}");
    }

    @Test
    void closeTruncatedJson_closesNestedStructuresInTheCorrectOrder() {
        String truncated = "{\"strengths\": [\"a\", \"b\", {\"nested\": \"value\"";

        String repaired = JsonRepair.closeTruncatedJson(truncated);

        assertThat(repaired).isEqualTo("{\"strengths\": [\"a\", \"b\", {\"nested\": \"value\"}]}");
    }

    @Test
    void closeTruncatedJson_leavesAnAlreadyWellFormedObjectUnchanged() {
        String wellFormed = "{\"text\": \"ok\", \"questionType\": \"CLARIFY\"}";

        assertThat(JsonRepair.closeTruncatedJson(wellFormed)).isEqualTo(wellFormed);
    }

    @Test
    void closeTruncatedJson_stripsAMarkdownCodeFenceBeforeRepairing() {
        String fenced = "```json\n{\"text\": \"ok\"";

        String repaired = JsonRepair.closeTruncatedJson(fenced);

        assertThat(repaired).isEqualTo("{\"text\": \"ok\"}");
    }

    @Test
    void closeTruncatedJson_doesNotMiscountBracesEscapedInsideAStringValue() {
        // The brace inside the string value must not be treated as a structural bracket.
        String truncated = "{\"text\": \"il a dit \\\"regarde ce {code}\\\" hier";

        String repaired = JsonRepair.closeTruncatedJson(truncated);

        assertThat(isBalanced(repaired)).isTrue();
    }

    private boolean isBalanced(String json) {
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (char c : json.toCharArray()) {
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
            if (c == '"') {
                inString = true;
            } else if (c == '{' || c == '[') {
                depth++;
            } else if (c == '}' || c == ']') {
                depth--;
            }
        }
        return depth == 0 && !inString;
    }
}
