package com.zenika.zenikia.communication.domain;

import java.util.List;

/**
 * French filler expressions ("tics de langage") the POC watches for. A
 * single occurrence is never inherently a problem — see
 * {@link TranscriptMetricsCalculator#isFrequencySignal}, which turns a raw
 * count into a coaching-worthy signal only above a frequency threshold.
 */
public final class FillerWordCatalog {

    public static final List<String> DEFAULT_FRENCH_FILLERS = List.of(
            "euh",
            "bah",
            "voilà",
            "du coup",
            "en fait",
            "genre",
            "donc voilà",
            "comment dire",
            "je dirais",
            "en gros"
    );

    private FillerWordCatalog() {
    }
}
