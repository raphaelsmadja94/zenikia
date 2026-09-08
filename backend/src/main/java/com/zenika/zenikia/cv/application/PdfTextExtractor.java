package com.zenika.zenikia.cv.application;

/**
 * Port: extracts raw text from a PDF file. Implemented in infrastructure
 * (PDFBox for the POC) — the application layer never touches a PDF library
 * directly.
 */
public interface PdfTextExtractor {

    /**
     * @param pdfBytes raw bytes of the uploaded PDF
     * @return the extracted plain text, never null (empty if nothing could be read)
     */
    String extractText(byte[] pdfBytes);
}
