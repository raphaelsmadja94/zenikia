package com.zenika.zenikia.interview.domain;

/**
 * The interlocutor ZenikIA plays during the interview. Each persona
 * challenges a different angle — see {@link #briefing()} for the short,
 * prompt-ready description of what it focuses on.
 */
public enum InterviewPersona {

    RECRUITER("Recruteur RH",
            "Challenge la présentation, le parcours, la motivation, la cohérence, le storytelling, "
                    + "les exemples concrets et la capacité de synthèse. Attend une communication claire et accessible, "
                    + "pas nécessairement un vocabulaire très technique."),

    DEVELOPER("Développeur pair technique",
            "Challenge les concepts techniques, leur fonctionnement réel, l'expérience de code, le debugging "
                    + "et les bonnes pratiques. Pose des questions précises et concrètes, va au fond des choses."),

    TECH_LEAD("Tech Lead",
            "Challenge l'architecture, la qualité, la mise en production, les décisions prises, les compromis, "
                    + "les tests, l'observabilité et le leadership technique. Attend des réponses qui montrent une "
                    + "responsabilité réelle sur les choix, pas seulement leur exécution."),

    CTO("CTO",
            "Challenge fortement les affirmations vagues : scalable, résilient, performant, architecture "
                    + "microservices, haute disponibilité, event-driven. Demande systématiquement la volumétrie réelle, "
                    + "les alternatives envisagées et pourquoi une solution plus simple n'aurait pas suffi.");

    private final String displayName;
    private final String briefing;

    InterviewPersona(String displayName, String briefing) {
        this.displayName = displayName;
        this.briefing = briefing;
    }

    public String displayName() {
        return displayName;
    }

    /** Short, prompt-ready description of what this persona challenges — fed to the question-generation prompt. */
    public String briefing() {
        return briefing;
    }
}
