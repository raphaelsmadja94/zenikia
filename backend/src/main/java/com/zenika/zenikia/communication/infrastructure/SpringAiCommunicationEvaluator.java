package com.zenika.zenikia.communication.infrastructure;

import com.zenika.zenikia.communication.application.CommunicationEvaluationContext;
import com.zenika.zenikia.communication.application.CommunicationEvaluator;
import com.zenika.zenikia.communication.application.CommunicationRubricScores;
import com.zenika.zenikia.shared.domain.AiProviderException;
import com.zenika.zenikia.shared.infrastructure.PromptTemplateLoader;
import com.zenika.zenikia.shared.infrastructure.StructuredAiCall;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

/** {@link CommunicationEvaluator} implementation backed by an LLM via Spring AI structured outputs. */
@Component
class SpringAiCommunicationEvaluator implements CommunicationEvaluator {

    private final ChatClient chatClient;
    private final String promptTemplate;

    SpringAiCommunicationEvaluator(
            ChatClient zenikiaChatClient,
            @Value("classpath:prompts/communication-assessment-system.st") Resource promptResource
    ) {
        this.chatClient = zenikiaChatClient;
        this.promptTemplate = PromptTemplateLoader.load(promptResource);
    }

    @Override
    public CommunicationRubricScores evaluate(CommunicationEvaluationContext context) {
        String fillerWordsDisplay = context.fillerWords().isEmpty()
                ? "aucun tic notable détecté"
                : context.fillerWords().entrySet().stream()
                        .map(e -> "\"%s\" × %d".formatted(e.getKey(), e.getValue()))
                        .collect(Collectors.joining(", "));

        String prompt = PromptTemplateLoader.render(promptTemplate, Map.of(
                "personaName", context.personaName(),
                "questionText", context.questionText(),
                "durationSeconds", String.format("%.1f", context.durationSeconds()),
                "wordCount", String.valueOf(context.wordCount()),
                "wordsPerMinute", String.format("%.0f", context.wordsPerMinute()),
                "fillerWords", fillerWordsDisplay,
                "repetitionCount", String.valueOf(context.repetitionCount()),
                "answerTranscript", context.answerTranscript()
        ));

        CommunicationRubricAiResponse response;
        try {
            response = StructuredAiCall.call(chatClient, prompt, CommunicationRubricAiResponse.class);
        } catch (Exception e) {
            throw new AiProviderException("Échec de l'évaluation de communication : " + e.getMessage(), e);
        }

        return new CommunicationRubricScores(
                response.clarity(),
                response.structure(),
                response.concision(),
                response.fluency(),
                response.impact(),
                response.vulgarisation(),
                response.adaptationToPersona(),
                response.strengths(),
                response.weaknesses()
        );
    }
}
