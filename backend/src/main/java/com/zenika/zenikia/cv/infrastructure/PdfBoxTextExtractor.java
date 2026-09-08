package com.zenika.zenikia.cv.infrastructure;

import com.zenika.zenikia.cv.application.PdfTextExtractor;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/** PDFBox-backed implementation of {@link PdfTextExtractor}. */
@Component
class PdfBoxTextExtractor implements PdfTextExtractor {

    private static final Logger log = LoggerFactory.getLogger(PdfBoxTextExtractor.class);

    @Override
    public String extractText(byte[] pdfBytes) {
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            return stripper.getText(document);
        } catch (IOException e) {
            // Never log the PDF content itself, only that extraction failed.
            log.warn("PDF text extraction failed: {}", e.getMessage());
            throw new IllegalArgumentException("Le fichier fourni n'est pas un PDF lisible.", e);
        }
    }
}
