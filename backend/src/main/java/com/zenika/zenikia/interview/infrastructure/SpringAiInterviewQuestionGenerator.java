package com.zenika.zenikia.interview.infrastructure;

import com.zenika.zenikia.interview.application.InterviewQuestionGenerator;
import com.zenika.zenikia.interview.application.QuestionGenerationContext;
import com.zenika.zenikia.interview.domain.InterviewContext;
import com.zenika.zenikia.interview.domain.InterviewPersona;
import com.zenika.zenikia.interview.domain.InterviewQuestion;
import com.zenika.zenikia.interview.domain.QuestionType;
import com.zenika.zenikia.shared.domain.AiProviderException;
import com.zenika.zenikia.shared.infrastructure.PromptTemplateLoader;
import com.zenika.zenikia.shared.infrastructure.StructuredAiCall;
import com.zenika.zenikia.skill.domain.SkillClaim;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/** {@link InterviewQuestionGenerator} implementation backed by an LLM via Spring AI structured outputs. */
@Component
class SpringAiInterviewQuestionGenerator implements InterviewQuestionGenerator {

    private final ChatClient chatClient;
    private final String promptTemplate;
    private final QuestionBank questionBank;

    SpringAiInterviewQuestionGenerator(
            ChatClient zenikiaChatClient,
            @Value("classpath:prompts/question-generation-system.st") Resource promptResource,
            QuestionBank questionBank
    ) {
        this.chatClient = zenikiaChatClient;
        this.promptTemplate = PromptTemplateLoader.load(promptResource);
        this.questionBank = questionBank;
    }

    @Override
    public InterviewQuestion generate(QuestionGenerationContext context) {
        InterviewContext ctx = context.interviewContext();

        Map<String, String> vars = new HashMap<>();
        vars.put("personaName", ctx.persona().displayName());
        vars.put("personaBriefing", ctx.persona().briefing());
        vars.put("candidateSummary", ctx.candidateSummary());
        vars.put("currentTargetSkill", ctx.currentTargetSkill());
        vars.put("currentDifficulty", String.valueOf(ctx.currentDifficulty()));
        vars.put("verifiedConcepts", PromptTemplateLoader.joinOrNone(ctx.verifiedConcepts()));
        vars.put("weakConcepts", PromptTemplateLoader.joinOrNone(ctx.weakConcepts()));
        vars.put("candidateClaims", relevantClaimsSummary(context.candidateClaims(), ctx.currentTargetSkill()));
        vars.put("referenceQuestions", referenceQuestionsFor(ctx));
        vars.put("lastQuestionText", ctx.lastQuestionText());
        vars.put("lastAnswerTranscript", ctx.lastAnswerTranscript());
        vars.put("lastNextAction", ctx.lastNextAction() == null ? "aucune (première question)" : ctx.lastNextAction().name());
        vars.put("opening", String.valueOf(context.opening()));

        String prompt = PromptTemplateLoader.render(promptTemplate, vars);

        QuestionGenerationAiResponse response;
        try {
            response = StructuredAiCall.call(chatClient, prompt, QuestionGenerationAiResponse.class);
        } catch (Exception e) {
            throw new AiProviderException("Échec de la génération de question : " + e.getMessage(), e);
        }
        if (response.text() == null || response.text().isBlank()) {
            throw new AiProviderException("La génération de question n'a renvoyé aucun texte.");
        }

        return InterviewQuestion.create(response.text(), ctx.currentTargetSkill(), ctx.currentDifficulty(), parseType(response.questionType()));
    }

    /**
     * RECRUITER stays on parcours/motivation/storytelling beyond the opener (posture bank);
     * every other persona gets questions grounded in the skill currently being probed.
     */
    private String referenceQuestionsFor(InterviewContext ctx) {
        List<String> examples = ctx.persona() == InterviewPersona.RECRUITER
                ? questionBank.postureQuestions(4)
                : questionBank.examplesFor(ctx.currentTargetSkill());
        if (examples.isEmpty()) {
            return "aucune";
        }
        return examples.stream().map(q -> "- " + q).collect(Collectors.joining("\n"));
    }

    private String relevantClaimsSummary(List<SkillClaim> claims, String targetSkill) {
        List<String> relevant = claims.stream()
                .filter(c -> c.relatedSkills().stream().anyMatch(s -> s.equalsIgnoreCase(targetSkill)))
                .map(c -> "\"%s\" (à challenger : %s)".formatted(c.statement(), PromptTemplateLoader.joinOrNone(c.challengeableConcepts())))
                .collect(Collectors.toList());
        return relevant.isEmpty() ? "aucune" : String.join(" | ", relevant);
    }

    private QuestionType parseType(String raw) {
        if (raw == null) {
            return QuestionType.TECHNICAL_CONCEPT;
        }
        try {
            return QuestionType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return QuestionType.TECHNICAL_CONCEPT;
        }
    }
}
