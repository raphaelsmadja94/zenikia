package com.zenika.zenikia.cv.application;

import com.zenika.zenikia.cv.domain.CandidateProfile;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * Orchestrates the CV → CandidateProfile pipeline:
 * PDF bytes → text extraction → AI analysis → domain aggregate → persistence.
 *
 * <p>No business rule about what a "skill" or a "claim" is lives here — that
 * belongs to the domain and to the {@link CvAnalyzer} prompt rubric. This
 * service only orchestrates.
 */
@Service
public class CvAnalysisService {

    private final PdfTextExtractor pdfTextExtractor;
    private final CvAnalyzer cvAnalyzer;
    private final CandidateProfileRepository candidateProfileRepository;

    public CvAnalysisService(
            PdfTextExtractor pdfTextExtractor,
            CvAnalyzer cvAnalyzer,
            CandidateProfileRepository candidateProfileRepository
    ) {
        this.pdfTextExtractor = pdfTextExtractor;
        this.cvAnalyzer = cvAnalyzer;
        this.candidateProfileRepository = candidateProfileRepository;
    }

    public CandidateProfile analyzeCv(byte[] pdfBytes) {
        String rawText = pdfTextExtractor.extractText(pdfBytes);
        if (!StringUtils.hasText(rawText)) {
            throw new IllegalArgumentException(
                    "Impossible d'extraire du texte de ce PDF. Vérifiez qu'il ne s'agit pas d'un scan sans OCR.");
        }

        CvAnalysisResult result = cvAnalyzer.analyze(rawText);

        CandidateProfile profile = CandidateProfile.create(
                result.role(),
                result.experienceYears(),
                result.skills(),
                result.claims(),
                result.experiences()
        );

        return candidateProfileRepository.save(profile);
    }

    public CandidateProfile getProfile(String id) {
        return candidateProfileRepository.findById(id)
                .orElseThrow(() -> com.zenika.zenikia.shared.domain.ResourceNotFoundException.of("CandidateProfile", id));
    }
}
