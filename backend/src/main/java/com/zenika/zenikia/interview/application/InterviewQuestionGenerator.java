package com.zenika.zenikia.interview.application;

import com.zenika.zenikia.interview.domain.InterviewQuestion;

/**
 * Port: generates the next interview question. Implemented via Spring AI in
 * infrastructure. This is the single most important port in the
 * application — see CLAUDE.md product priorities: question quality first.
 */
public interface InterviewQuestionGenerator {

    InterviewQuestion generate(QuestionGenerationContext context);
}
