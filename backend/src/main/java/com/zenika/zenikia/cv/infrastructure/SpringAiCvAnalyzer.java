package com.zenika.zenikia.cv.infrastructure;

import com.zenika.zenikia.cv.application.CvAnalysisResult;
import com.zenika.zenikia.cv.application.CvAnalyzer;
import com.zenika.zenikia.cv.domain.Experience;
import com.zenika.zenikia.skill.domain.ClaimedLevel;
import com.zenika.zenikia.skill.domain.Skill;
import com.zenika.zenikia.skill.domain.SkillCategory;
import com.zenika.zenikia.skill.domain.SkillClaim;
import com.zenika.zenikia.shared.domain.AiProviderException;
import com.zenika.zenikia.shared.infrastructure.PromptTemplateLoader;
import com.zenika.zenikia.shared.infrastructure.StructuredAiCall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

/** {@link CvAnalyzer} implementation backed by an LLM via Spring AI structured outputs. */
@Component
class SpringAiCvAnalyzer implements CvAnalyzer {

    private static final Logger log = LoggerFactory.getLogger(SpringAiCvAnalyzer.class);
    private static final int MAX_CV_CHARS = 12_000;

    private final ChatClient chatClient;
    private final String promptTemplate;

    SpringAiCvAnalyzer(
            ChatClient zenikiaChatClient,
            @Value("classpath:prompts/cv-analysis-system.st") Resource promptResource
    ) {
        this.chatClient = zenikiaChatClient;
        this.promptTemplate = PromptTemplateLoader.load(promptResource);
    }

    @Override
    public CvAnalysisResult analyze(String rawCvText) {
        String truncated = rawCvText.length() > MAX_CV_CHARS
                ? rawCvText.substring(0, MAX_CV_CHARS)
                : rawCvText;
        String prompt = PromptTemplateLoader.render(promptTemplate, java.util.Map.of("cvText", truncated));

        CvAnalysisAiResponse response;
        try {
            response = StructuredAiCall.call(chatClient, prompt, CvAnalysisAiResponse.class);
        } catch (Exception e) {
            throw new AiProviderException("Échec de l'analyse IA du CV : " + e.getMessage(), e);
        }

        return new CvAnalysisResult(
                response.role(),
                response.experienceYears(),
                toSkills(response.skills()),
                toClaims(response.claims()),
                toExperiences(response.experiences())
        );
    }

    private List<Skill> toSkills(List<CvAnalysisAiResponse.SkillAi> skills) {
        if (skills == null) {
            return List.of();
        }
        return skills.stream()
                .filter(s -> s.name() != null && !s.name().isBlank())
                .map(s -> new Skill(s.name().trim(), parseCategory(s.category()), parseLevel(s.claimedLevel())))
                .toList();
    }

    private List<SkillClaim> toClaims(List<CvAnalysisAiResponse.ClaimAi> claims) {
        if (claims == null) {
            return List.of();
        }
        // A small local model occasionally gets stuck in a repetition loop and emits the same
        // claim object several times in a row (see README "Run for free with Ollama") — dedupe
        // defensively by normalized statement rather than trusting the model not to repeat itself.
        java.util.Set<String> seenStatements = new java.util.HashSet<>();
        return claims.stream()
                .filter(c -> c.statement() != null && !c.statement().isBlank())
                .filter(c -> seenStatements.add(c.statement().trim().toLowerCase(Locale.ROOT)))
                .map(c -> new SkillClaim(
                        UUID.randomUUID().toString(),
                        c.statement().trim(),
                        c.relatedSkills(),
                        c.challengeableConcepts()))
                .toList();
    }

    private List<Experience> toExperiences(List<CvAnalysisAiResponse.ExperienceAi> experiences) {
        if (experiences == null) {
            return List.of();
        }
        return experiences.stream()
                .map(e -> new Experience(e.title(), e.company(), e.period(), e.summary()))
                .toList();
    }

    private SkillCategory parseCategory(String raw) {
        if (raw == null) {
            return SkillCategory.OTHER;
        }
        try {
            return SkillCategory.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return SkillCategory.OTHER;
        }
    }

    private ClaimedLevel parseLevel(String raw) {
        if (raw == null) {
            return ClaimedLevel.UNSPECIFIED;
        }
        String normalized = raw.trim().toUpperCase(Locale.ROOT);
        try {
            return ClaimedLevel.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return synonymLevel(normalized);
        }
    }

    /**
     * A local model is told to map CV wording ("Senior", "Confirmé"...) to the exact enum name,
     * but sometimes echoes the CV's own word instead. This absorbs the common French/English
     * synonyms directly rather than losing the signal to UNSPECIFIED.
     */
    private ClaimedLevel synonymLevel(String normalized) {
        if (normalized.contains("SENIOR") || normalized.contains("EXPERT") || normalized.contains("LEAD")) {
            return ClaimedLevel.EXPERT;
        }
        if (normalized.contains("AVANC") || normalized.contains("ADVANCED") || normalized.contains("CONFIRM")) {
            return ClaimedLevel.ADVANCED;
        }
        if (normalized.contains("INTERMEDIA")) {
            return ClaimedLevel.INTERMEDIATE;
        }
        if (normalized.contains("JUNIOR") || normalized.contains("DEBUT") || normalized.contains("BEGINNER")
                || normalized.contains("FORMATION") || normalized.contains("NOTION")) {
            return ClaimedLevel.BEGINNER;
        }
        return ClaimedLevel.UNSPECIFIED;
    }
}
