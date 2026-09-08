package com.zenika.zenikia.speech.infrastructure;

import com.zenika.zenikia.shared.infrastructure.PromptTemplateLoader;
import com.zenika.zenikia.speech.application.TranscriptCorrectionContext;
import com.zenika.zenikia.speech.application.TranscriptCorrector;
import com.zenika.zenikia.speech.domain.Transcript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * {@link TranscriptCorrector} implementation backed by an LLM via Spring AI — plain text output
 * (no structured output/JSON needed for a text-to-text correction), which also sidesteps the
 * JSON-parsing fragility a smaller local model can otherwise show (see README "Run for free
 * with Ollama"). Any doubt about the result falls back to the original transcript: this feature
 * must never be the reason an answer submission fails, and it must never be allowed to silently
 * rewrite more than a smaller model was told to.
 */
@Component
class SpringAiTranscriptCorrector implements TranscriptCorrector {

    private static final Logger log = LoggerFactory.getLogger(SpringAiTranscriptCorrector.class);

    /** If the "corrected" text is wildly longer than the original, the model ignored its instructions — discard it. */
    private static final double MAX_LENGTH_RATIO = 1.5;

    private final ChatClient chatClient;
    private final String promptTemplate;

    SpringAiTranscriptCorrector(
            ChatClient zenikiaChatClient,
            @Value("classpath:prompts/transcript-correction-system.st") Resource promptResource
    ) {
        this.chatClient = zenikiaChatClient;
        this.promptTemplate = PromptTemplateLoader.load(promptResource);
    }

    @Override
    public Transcript correct(TranscriptCorrectionContext context) {
        if (context.rawTranscript() == null || context.rawTranscript().isBlank()) {
            return new Transcript(context.rawTranscript());
        }

        String prompt = PromptTemplateLoader.render(promptTemplate, Map.of(
                "targetSkill", context.targetSkill(),
                "questionText", context.questionText(),
                "rawTranscript", context.rawTranscript()
        ));

        try {
            String corrected = chatClient.prompt().user(prompt).call().content();
            if (isPlausibleCorrection(context.rawTranscript(), corrected)) {
                return new Transcript(corrected);
            }
            log.debug("Discarding implausible transcript correction, keeping the original transcript.");
        } catch (Exception e) {
            log.warn("Transcript correction call failed, keeping the original transcript: {}", e.getMessage());
        }
        return new Transcript(context.rawTranscript());
    }

    private boolean isPlausibleCorrection(String original, String corrected) {
        if (corrected == null || corrected.isBlank()) {
            return false;
        }
        String trimmed = corrected.trim();
        return trimmed.length() <= original.length() * MAX_LENGTH_RATIO;
    }
}
