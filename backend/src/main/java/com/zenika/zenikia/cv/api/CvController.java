package com.zenika.zenikia.cv.api;

import com.zenika.zenikia.cv.application.CvAnalysisService;
import com.zenika.zenikia.cv.domain.CandidateProfile;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;

/**
 * Thin controller: validates the incoming upload shape only, delegates all
 * extraction/analysis logic to {@link CvAnalysisService}.
 */
@RestController
class CvController {

    private final CvAnalysisService cvAnalysisService;

    CvController(CvAnalysisService cvAnalysisService) {
        this.cvAnalysisService = cvAnalysisService;
    }

    @PostMapping(value = "/api/cvs/analyze", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    CandidateProfileResponse analyze(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier CV est vide.");
        }
        if (!MediaType.APPLICATION_PDF_VALUE.equals(file.getContentType())) {
            throw new IllegalArgumentException("Seuls les fichiers PDF sont acceptés.");
        }

        byte[] bytes;
        try {
            bytes = file.getBytes();
        } catch (IOException e) {
            throw new UncheckedIOException("Impossible de lire le fichier envoyé.", e);
        }

        CandidateProfile profile = cvAnalysisService.analyzeCv(bytes);
        return CandidateProfileResponse.from(profile);
    }

    @GetMapping("/api/cvs/{id}")
    CandidateProfileResponse get(@PathVariable String id) {
        return CandidateProfileResponse.from(cvAnalysisService.getProfile(id));
    }
}
