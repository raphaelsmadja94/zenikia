package com.zenika.zenikia.cv.infrastructure;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.util.List;

/**
 * Shape requested from the LLM for CV analysis (Spring AI structured
 * output). Deliberately separate from the domain model: the LLM never
 * controls identifiers, and this type can evolve independently of
 * {@code cv.domain}.
 *
 * <p>The nested list items use a lenient deserializer (see
 * {@link LenientJson}) because not every provider honors a nested-object
 * schema as strictly as OpenAI's native structured outputs — a local model
 * may return a bare string where an object was requested.
 */
record CvAnalysisAiResponse(
        String role,
        Integer experienceYears,
        List<SkillAi> skills,
        List<ClaimAi> claims,
        List<ExperienceAi> experiences
) {
    @JsonDeserialize(using = SkillAiDeserializer.class)
    record SkillAi(String name, String category, String claimedLevel) {
    }

    @JsonDeserialize(using = ClaimAiDeserializer.class)
    record ClaimAi(String statement, List<String> relatedSkills, List<String> challengeableConcepts) {
    }

    @JsonDeserialize(using = ExperienceAiDeserializer.class)
    record ExperienceAi(String title, String company, String period, String summary) {
    }

    static class SkillAiDeserializer extends JsonDeserializer<SkillAi> {
        @Override
        public SkillAi deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            JsonNode node = parser.getCodec().readTree(parser);
            if (node.isTextual()) {
                return new SkillAi(node.asText(), null, null);
            }
            return new SkillAi(
                    LenientJson.firstText(node, "name", "skill", "skillName"),
                    LenientJson.firstText(node, "category", "type", "categorie"),
                    LenientJson.firstText(node, "claimedLevel", "level", "niveau", "proficiency", "proficiencyLevel")
            );
        }
    }

    static class ClaimAiDeserializer extends JsonDeserializer<ClaimAi> {
        @Override
        public ClaimAi deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            JsonNode node = parser.getCodec().readTree(parser);
            if (node.isTextual()) {
                return new ClaimAi(node.asText(), List.of(), List.of());
            }
            return new ClaimAi(
                    LenientJson.text(node, "statement"),
                    LenientJson.stringList(node, "relatedSkills"),
                    LenientJson.stringList(node, "challengeableConcepts")
            );
        }
    }

    static class ExperienceAiDeserializer extends JsonDeserializer<ExperienceAi> {
        @Override
        public ExperienceAi deserialize(JsonParser parser, DeserializationContext context) throws IOException {
            JsonNode node = parser.getCodec().readTree(parser);
            if (node.isTextual()) {
                return new ExperienceAi(node.asText(), "", "", "");
            }
            return new ExperienceAi(
                    LenientJson.text(node, "title"),
                    LenientJson.text(node, "company"),
                    LenientJson.text(node, "period"),
                    LenientJson.text(node, "summary")
            );
        }
    }
}
