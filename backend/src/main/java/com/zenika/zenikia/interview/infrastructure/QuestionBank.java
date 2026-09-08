package com.zenika.zenikia.interview.infrastructure;

import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Reference bank of real, vetted interview questions (see
 * {@code resources/question-bank.yml}) — curated from actual Zenika client
 * interview questions. Used purely as few-shot grounding for
 * {@link SpringAiInterviewQuestionGenerator}: it keeps the generated
 * question personalized to the candidate/persona/history (never a static
 * list handed out as-is, per spec §8), while anchoring a smaller/local
 * model's phrasing and topic choice in questions that actually read like a
 * real interview instead of improvising freely.
 */
@Component
class QuestionBank {

    private static final int DEFAULT_LIMIT = 4;

    private final Map<String, List<String>> byKey;

    QuestionBank(org.springframework.core.io.ResourceLoader resourceLoader) {
        this.byKey = load(resourceLoader.getResource("classpath:question-bank.yml"));
    }

    /**
     * Reference questions for a given skill name (e.g. "Spring Boot", "Angular 17"), matched
     * loosely against the bank's keys since CV-extracted skill names don't exactly match a fixed
     * vocabulary. Falls back to the "craft" bucket (general engineering questions) if nothing
     * matches, so every skill gets at least some grounding rather than none.
     */
    List<String> examplesFor(String skillName) {
        return examplesFor(skillName, DEFAULT_LIMIT);
    }

    List<String> examplesFor(String skillName, int limit) {
        String normalized = normalize(skillName);
        List<String> matched = byKey.entrySet().stream()
                .filter(e -> normalized.contains(e.getKey()) || e.getKey().contains(normalized))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseGet(() -> byKey.getOrDefault("craft", List.of()));
        return sample(matched, limit);
    }

    /** Behavioral/motivation questions — used for the RECRUITER persona beyond the opener. */
    List<String> postureQuestions(int limit) {
        return sample(byKey.getOrDefault("posture", List.of()), limit);
    }

    private List<String> sample(List<String> source, int limit) {
        if (source.isEmpty()) {
            return List.of();
        }
        List<String> shuffled = new ArrayList<>(source);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, Math.min(limit, shuffled.size()));
    }

    private String normalize(String skillName) {
        return skillName == null ? "" : skillName.toLowerCase(Locale.ROOT).trim();
    }

    @SuppressWarnings("unchecked")
    private Map<String, List<String>> load(Resource resource) {
        try (InputStream input = resource.getInputStream()) {
            Map<String, Object> raw = new Yaml().load(input);
            Map<String, List<String>> result = new LinkedHashMap<>();
            if (raw != null) {
                raw.forEach((key, value) -> result.put(key.toLowerCase(Locale.ROOT), (List<String>) value));
            }
            return result;
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de charger question-bank.yml", e);
        }
    }
}
