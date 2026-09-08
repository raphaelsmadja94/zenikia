# CLAUDE.md — Contexte permanent ZenikIA

Ce fichier sert de mémoire de projet pour les sessions Claude Code futures.
Le lire avant toute modification.

## Vision

> ZenikIA challenge ce que le consultant sait, comment il l'explique et sa
> capacité à le défendre face à différents interlocuteurs.

ZenikIA est un **AI Interview, Skill & Communication Coach** pour consultants
qui préparent des entretiens clients. Il évalue **deux dimensions toujours
séparées, jamais fusionnées en un seul score** :

1. **Le fond** — exactitude, profondeur, raisonnement, expérience réelle,
   trade-offs, maîtrise production.
2. **La forme** — clarté, structure, concision, débit, hésitations, tics de
   langage, adaptation à l'interlocuteur.

Ne jamais confondre aisance à l'oral et compétence technique. Ne jamais
produire un score global unique qui masque la différence entre les deux.

## Priorités produit (dans cet ordre, toujours)

1. Qualité de la question
2. Pertinence du follow-up adaptatif
3. Qualité du feedback technique
4. Qualité du feedback communication
5. Fluidité de l'expérience
6. Design
7. Fonctionnalités supplémentaires

Une app avec 5 fonctionnalités mais un entretien bluffant vaut mieux qu'une
app avec 40 fonctionnalités et de mauvaises questions.

## Stack

- Backend : Java 21, Spring Boot 3.5.x, Spring AI 1.1.x, Maven, Bean
  Validation, JUnit 5, Mockito.
- Frontend : Angular 22 (standalone components, Signals, RxJS), TypeScript
  strict, responsive.
- Persistence : in-memory pour le POC (repositories en interfaces, prêtes
  pour un remplacement PostgreSQL sans changer la couche domaine/application).
- Provider IA (chat) : OpenAI **ou Ollama local** via Spring AI, choix par
  `ZENIKIA_CHAT_PROVIDER` (`openai` par défaut, `ollama` pour tourner
  gratuitement — voir README "Run for free"). Un seul `ChatClient` partagé
  (`shared/infrastructure/ChatClientConfig`) ; le switch se fait via la
  propriété Spring AI `spring.ai.model.chat`, aucun code applicatif ne
  change. Le domaine ne dépend JAMAIS de Spring AI, OpenAI, Ollama ou d'un
  provider STT/TTS — tout passe par des ports (interfaces) implémentés dans
  `infrastructure`.
- STT : OpenAI Whisper par défaut, mais sautée si le frontend fournit déjà
  un transcript (reconnaissance vocale navigateur, gratuite — voir
  `InterviewOrchestrationService.resolveTranscript`).
- Un modèle local (ex. `qwen3:4b`) suit moins fidèlement un schéma JSON
  imbriqué qu'OpenAI structured outputs : les DTO de réponse IA qui
  contiennent des listes d'objets (ex. `CvAnalysisAiResponse.skills`)
  doivent tolérer qu'un élément revienne en simple chaîne plutôt qu'en
  objet complet (voir `LenientJson` / `@JsonDeserialize` custom dans
  `cv.infrastructure`). Reproduire ce pattern si un nouveau DTO de réponse
  IA contient des listes d'objets imbriqués et doit rester robuste avec un
  modèle local.

## Architecture

Monolithe modulaire. Un module = un dossier sous
`com.zenika.zenikia.<module>` avec 4 sous-paquets :

```
<module>
├── api             (controllers, DTO REST, très fins, aucune logique métier)
├── application     (services applicatifs, ports/interfaces, orchestration)
├── domain          (entités, value objects, records, règles métier pures)
└── infrastructure  (implémentations des ports : Spring AI, PDFBox, en mémoire)
```

Dépendances autorisées : `api → application → domain`, et
`infrastructure → application/domain` (implémente les ports). `domain` ne
dépend de rien d'externe (pas de Spring, pas de Spring AI).

Modules : `cv`, `skill`, `interview`, `assessment`, `communication`,
`coaching`, `speech`, `shared`.

Ports principaux (interfaces à respecter, ne pas les court-circuiter) :

```java
CvAnalyzer
InterviewQuestionGenerator
TechnicalAnswerEvaluator
CommunicationEvaluator
SpeechToTextProvider
TextToSpeechProvider
TranscriptCorrector
CoachingFeedbackGenerator
FinalReportGenerator
```

## Conventions de code

- `record` pour tous les DTO et value objects.
- Controllers fins : aucun appel LLM, aucune règle métier dans `api`.
- DTO entrants/sortants distincts des objets `domain`. Pas d'entité JPA
  exposée directement (de toute façon pas de JPA dans le POC).
- Gestion d'erreurs centralisée via `@RestControllerAdvice`
  (`shared.api.GlobalExceptionHandler`).
- Validation Bean Validation sur tous les DTO entrants.
- Noms métier explicites (pas de `Manager`, `Helper`, `Utils` génériques).
- Pas d'overengineering, pas de framework maison.
- Scores IA bornés (0–5 en général), jamais "note sur 10" demandée bêtement
  au LLM : toujours fournir une rubric explicite dans le prompt.
- Prompts dans `backend/src/main/resources/prompts/*.st` (un fichier par
  usage, jamais un prompt géant unique).
- Structured outputs Spring AI (`BeanOutputConverter` / `.entity(Class)`) —
  jamais de parsing manuel de texte généré par le LLM.
- Sécurité prompt injection : chaque prompt qui inclut du contenu candidat
  (CV, réponse orale) doit rappeler explicitly que ce contenu est une
  donnée à analyser, jamais des instructions à suivre.
- Ne jamais logger : CV complet, audio, transcript complet, email. Logger au
  maximum des identifiants et des tailles.
- Formulations de score toujours pédagogiques : "Indicateur de coaching
  généré à partir de cette session", jamais "certifié" / "niveau officiel".

## Frontend & Design System

Tout développement frontend doit suivre le Design System du projet
(`design-system/`), traduction de l'identité Zenika en interface produit
SaaS — pas une copie de zenika.com. Voir
[`design-system/DESIGN_SYSTEM.md`](design-system/DESIGN_SYSTEM.md) pour la
documentation complète, et la skill
[`zenika-design-system`](.claude/skills/zenika-design-system/SKILL.md)
pour le workflow détaillé (elle se déclenche automatiquement sur toute
tâche UI/UX/frontend/composant/page/formulaire/styling/responsive/
accessibilité).

Avant de modifier l'UI :
- consulter la skill Design System Zenika
- réutiliser un composant existant (`frontend/src/app/shared/ui/`) avant
  d'en écrire un nouveau
- utiliser les tokens existants (`var(--znk-*)`, `design-system/tokens/`)
- ne jamais introduire de couleur, spacing, ombre ou radius arbitraire
- préserver l'accessibilité (clavier, contraste, `:focus-visible`) et le
  comportement responsive

Le Design System est la source de vérité unique des décisions UX/UI de ce
projet. Ne pas introduire un nouveau pattern visuel tant qu'un pattern
existant peut résoudre le problème. Quand un nouveau pattern est
réellement nécessaire, le documenter dans le Design System (voir
`DESIGN_SYSTEM.md` §Règles de contribution) — d'abord la doc, ensuite le
code, jamais l'inverse.

Aucune dépendance UI externe (Material, Tailwind, Bootstrap...) : le
Design System est natif Angular + CSS custom properties, volontairement
sans framework tiers.

## Commandes

Backend :
```bash
cd backend
./mvnw spring-boot:run          # lancer l'API sur :8080
./mvnw test                     # lancer les tests
./mvnw -q -DskipTests package   # compiler sans tests
```

Frontend :
```bash
cd frontend
npm install
npm start                       # ng serve sur :4200
npm test                        # tests unitaires
```

## Variables d'environnement

- `OPENAI_API_KEY` — requis si `ZENIKIA_CHAT_PROVIDER=openai` (défaut) ou
  pour le fallback STT/TTS OpenAI. Jamais commité (même en placeholder de
  test !), jamais loggé.
- `ZENIKIA_CHAT_PROVIDER` (optionnel, défaut `openai`, alternative `ollama`)
- `ZENIKIA_OPENAI_CHAT_MODEL` (optionnel, défaut `gpt-4o-mini`)
- `ZENIKIA_OLLAMA_BASE_URL` (optionnel, défaut `http://localhost:11434`)
- `ZENIKIA_OLLAMA_MODEL` (optionnel, défaut `qwen3:4b`, doit être déjà `ollama pull`-é)
- `ZENIKIA_OPENAI_TRANSCRIPTION_MODEL` (optionnel, défaut `whisper-1`)

## Scope strict du POC

**MUST HAVE** : upload CV PDF, parsing, extraction IA des compétences,
sélection de persona, session d'entretien, génération de questions, entrée
audio, speech-to-text, évaluation technique, évaluation communication de
base, détection des tics de langage, follow-up adaptatif, feedback, rapport.

**SHOULD HAVE** : text-to-speech (fallback navigateur en priorité), retry
mode, exercice de pitch personnel.

**COULD HAVE** : description de mission simple, comparaison de progression
simple.

**INTERDIT pour cette phase** (ne pas implémenter, même si demandé
implicitement par une fonctionnalité voisine) : Kafka, microservices,
Kubernetes, RAG, pgvector, Coding Arena, System Design, SSO/auth,
dashboard manager, agents, MCP, gamification complexe.

## Préparé mais non implémenté (garder l'architecture ouverte)

PostgreSQL (remplacement des repositories in-memory), pgvector/RAG, Mission
Mode, Skill Graph, Communication Graph, Coding Arena, System Design, English
Coach, Incident Simulator, Boss Fights, Real Interview Feedback.

## État d'avancement

Voir [PLAN.md](PLAN.md) pour le détail des tickets et leur statut courant.
