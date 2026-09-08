package com.zenika.zenikia.coaching.infrastructure;

import com.zenika.zenikia.coaching.application.FinalReportContext;
import com.zenika.zenikia.coaching.application.FinalReportGenerator;
import com.zenika.zenikia.coaching.application.FinalReportNarrative;
import com.zenika.zenikia.shared.domain.AiProviderException;
import com.zenika.zenikia.shared.infrastructure.PromptTemplateLoader;
import com.zenika.zenikia.shared.infrastructure.StructuredAiCall;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

/** {@link FinalReportGenerator} implementation backed by an LLM via Spring AI structured outputs. */
@Component
class SpringAiFinalReportGenerator implements FinalReportGenerator {

    private final ChatClient chatClient;
    private final String promptTemplate;

    SpringAiFinalReportGenerator(
            ChatClient zenikiaChatClient,
            @Value("classpath:prompts/final-report-system.st") Resource promptResource
    ) {
        this.chatClient = zenikiaChatClient;
        this.promptTemplate = PromptTemplateLoader.load(promptResource);
    }

    @Override
    public FinalReportNarrative generate(FinalReportContext context) {
        String prompt = PromptTemplateLoader.render(promptTemplate, Map.of(
                "personaName", context.personaName(),
                "candidateRole", context.candidateRole(),
                "totalQuestions", String.valueOf(context.totalQuestions()),
                "averageTechnicalScore", String.format("%.1f", context.averageTechnicalScore()),
                "averageCommunicationScore", String.format("%.1f", context.averageCommunicationScore()),
                "averageWordsPerMinute", String.format("%.0f", context.averageWordsPerMinute()),
                "totalFillerWordOccurrences", String.valueOf(context.totalFillerWordOccurrences()),
                "allStrengths", PromptTemplateLoader.joinOrNone(context.allStrengths()),
                "allWeaknesses", PromptTemplateLoader.joinOrNone(context.allWeaknesses()),
                "allMissingConcepts", PromptTemplateLoader.joinOrNone(context.allMissingConcepts())
        ));

        FinalReportAiResponse response;
        try {
            response = StructuredAiCall.call(chatClient, prompt, FinalReportAiResponse.class);
        } catch (Exception e) {
            throw new AiProviderException("Échec de la génération du rapport final : " + e.getMessage(), e);
        }

        return new FinalReportNarrative(response.topStrengths(), response.topPriorities(), response.recommendedNextSteps());
    }
}
