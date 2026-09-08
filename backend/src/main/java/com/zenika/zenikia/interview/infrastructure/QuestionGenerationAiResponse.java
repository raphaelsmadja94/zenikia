package com.zenika.zenikia.interview.infrastructure;

/** Structured output shape requested from the LLM for question generation. */
record QuestionGenerationAiResponse(String text, String questionType) {
}
