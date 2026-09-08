package com.zenika.zenikia.assessment.infrastructure;

import com.zenika.zenikia.assessment.application.TechnicalAnswerEvaluator;
import com.zenika.zenikia.assessment.application.TechnicalEvaluationContext;
import com.zenika.zenikia.assessment.domain.TechnicalAssessment;
import com.zenika.zenikia.shared.domain.AiProviderException;
import com.zenika.zenikia.shared.infrastructure.PromptTemplateLoader;
import com.zenika.zenikia.shared.infrastructure.StructuredAiCall;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;

/** {@link TechnicalAnswerEvaluator} implementation backed by an LLM via Spring AI structured outputs. */
@Component
class SpringAiTechnicalAnswerEvaluator implements TechnicalAnswerEvaluator {

    private final ChatClient chatClient;
    private final String promptTemplate;

    SpringAiTechnicalAnswerEvaluator(
            ChatClient zenikiaChatClient,
            @Value("classpath:prompts/technical-assessment-system.st") Resource promptResource
    ) {
        this.chatClient = zenikiaChatClient;
        this.promptTemplate = PromptTemplateLoader.load(promptResource);
    }

    @Override
    public TechnicalAssessment evaluate(TechnicalEvaluationContext context) {
        String prompt = PromptTemplateLoader.render(promptTemplate, Map.of(
                "candidateSummary", context.candidateSummary(),
                "personaName", context.personaName(),
                "targetSkill", context.targetSkill(),
                "difficulty", String.valueOf(context.difficulty()),
                "verifiedConcepts", PromptTemplateLoader.joinOrNone(context.verifiedConcepts()),
                "weakConcepts", PromptTemplateLoader.joinOrNone(context.weakConcepts()),
                "questionText", context.questionText(),
                "answerTranscript", context.answerTranscript()
        ));

        TechnicalAssessmentAiResponse response;
        try {
            response = StructuredAiCall.call(chatClient, prompt, TechnicalAssessmentAiResponse.class);
        } catch (Exception e) {
            throw new AiProviderException("Échec de l'évaluation technique : " + e.getMessage(), e);
        }

        return new TechnicalAssessment(
                response.correctness(),
                response.depth(),
                response.reasoning(),
                response.realWorldExperience(),
                response.examples(),
                response.tradeOffThinking(),
                response.productionAwareness(),
                response.strengths(),
                response.weaknesses(),
                response.missingConcepts()
        );
    }
}
