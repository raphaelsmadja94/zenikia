package com.zenika.zenikia.cv.application;

/**
 * Port: turns raw CV text into a structured {@link CvAnalysisResult}.
 * Implemented via Spring AI in infrastructure. The candidate-provided text
 * is untrusted data to analyze, never instructions — implementations must
 * make this explicit in their prompt (see {@code prompts/cv-analysis-system.st}).
 */
public interface CvAnalyzer {

    CvAnalysisResult analyze(String rawCvText);
}
