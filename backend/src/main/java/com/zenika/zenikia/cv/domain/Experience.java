package com.zenika.zenikia.cv.domain;

/**
 * A professional experience entry as summarized from the CV.
 *
 * @param title    role held (e.g. "Lead Développeur Java")
 * @param company  employer or client name (may be anonymized/blank for confidential missions)
 * @param period   free-text period as written in the CV (e.g. "2022 - 2024")
 * @param summary  short summary of the mission/responsibilities
 */
public record Experience(String title, String company, String period, String summary) {

    public Experience {
        title = title == null ? "" : title;
        company = company == null ? "" : company;
        period = period == null ? "" : period;
        summary = summary == null ? "" : summary;
    }
}
