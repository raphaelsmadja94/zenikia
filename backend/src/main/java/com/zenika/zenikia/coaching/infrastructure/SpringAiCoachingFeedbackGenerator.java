package com.zenika.zenikia.coaching.infrastructure;

import com.zenika.zenikia.assessment.domain.TechnicalAssessment;
import com.zenika.zenikia.coaching.application.CoachingContext;
import com.zenika.zenikia.coaching.application.CoachingFeedbackGenerator;
import com.zenika.zenikia.coaching.domain.CoachingFeedback;
import com.zenika.zenikia.coaching.domain.StarBreakdown;
import com.zenika.zenikia.communication.domain.CommunicationAssessment;
import com.zenika.zenikia.shared.domain.AiProviderException;
import com.zenika.zenikia.shared.infrastructure.PromptTemplateLoader;
import com.zenika.zenikia.shared.infrastructure.StructuredAiCall;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/** {@link CoachingFeedbackGenerator} implementation backed by an LLM via Spring AI structured outputs. */
@Component
class SpringAiCoachingFeedbackGenerator implements CoachingFeedbackGenerator {

    private final ChatClient chatClient;
    private final String promptTemplate;

    SpringAiCoachingFeedbackGenerator(
            ChatClient zenikiaChatClient,
            @Value("classpath:prompts/coaching-feedback-system.st") Resource promptResource
    ) {
        this.chatClient = zenikiaChatClient;
        this.promptTemplate = PromptTemplateLoader.load(promptResource);
    }

    @Override
    public CoachingFeedback generate(CoachingContext context) {
        TechnicalAssessment ta = context.technicalAssessment();
        CommunicationAssessment ca = context.communicationAssessment();

        Map<String, String> vars = new HashMap<>();
        vars.put("personaName", context.personaName());
        vars.put("questionType", context.questionType());
        vars.put("questionText", context.questionText());
        vars.put("answerTranscript", context.answerTranscript());
        vars.put("correctness", String.valueOf(ta.correctness()));
        vars.put("depth", String.valueOf(ta.depth()));
        vars.put("reasoning", String.valueOf(ta.reasoning()));
        vars.put("realWorldExperience", String.valueOf(ta.realWorldExperience()));
        vars.put("examples", String.valueOf(ta.examples()));
        vars.put("tradeOffThinking", String.valueOf(ta.tradeOffThinking()));
        vars.put("productionAwareness", String.valueOf(ta.productionAwareness()));
        vars.put("technicalWeaknesses", PromptTemplateLoader.joinOrNone(ta.weaknesses()));
        vars.put("missingConcepts", PromptTemplateLoader.joinOrNone(ta.missingConcepts()));
        vars.put("clarity", String.valueOf(ca.clarity()));
        vars.put("structure", String.valueOf(ca.structure()));
        vars.put("concision", String.valueOf(ca.concision()));
        vars.put("fluency", String.valueOf(ca.fluency()));
        vars.put("impact", String.valueOf(ca.impact()));
        vars.put("vulgarisation", String.valueOf(ca.vulgarisation()));
        vars.put("adaptationToPersona", String.valueOf(ca.adaptationToPersona()));
        vars.put("durationSeconds", String.format("%.0f", ca.durationSeconds()));
        vars.put("wordsPerMinute", String.format("%.0f", ca.wordsPerMinute()));
        vars.put("communicationWeaknesses", PromptTemplateLoader.joinOrNone(ca.weaknesses()));

        String prompt = PromptTemplateLoader.render(promptTemplate, vars);

        CoachingFeedbackAiResponse response;
        try {
            response = StructuredAiCall.call(chatClient, prompt, CoachingFeedbackAiResponse.class);
        } catch (Exception e) {
            throw new AiProviderException("Échec de la génération du feedback : " + e.getMessage(), e);
        }

        StarBreakdown starBreakdown = null;
        if (response.starBreakdown() != null) {
            var s = response.starBreakdown();
            if (s.situationPresent() || s.taskPresent() || s.actionPresent() || s.resultPresent()
                    || (s.note() != null && !s.note().isBlank())) {
                starBreakdown = new StarBreakdown(s.situationPresent(), s.taskPresent(), s.actionPresent(), s.resultPresent(), s.note());
            }
        }

        return new CoachingFeedback(response.observation(), response.recommendation(), response.priorityActions(), starBreakdown, response.modelAnswer());
    }
}
