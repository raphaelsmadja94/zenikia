package com.zenika.zenikia.speech.domain;

/** Result of a speech-to-text run. Kept separate from audio metrics on purpose (see CLAUDE.md §12). */
public record Transcript(String text) {

    public Transcript {
        text = text == null ? "" : text.trim();
    }

    public boolean isBlank() {
        return text.isBlank();
    }
}
