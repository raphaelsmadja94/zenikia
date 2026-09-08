# Pattern : États de chargement

## Règle générale

Un texte brut "Chargement…" est un pis-aller, jamais une destination. Utiliser `Skeleton` (`components/data-display.md`) partout où la forme du contenu final est connue à l'avance (une card de profil, une ligne de score) — le texte "Chargement…" reste acceptable uniquement pour une attente courte et imprévisible (ex: bouton en état `loading`).

## Occurrences actuelles à migrer

| Écran | Aujourd'hui | Cible |
|---|---|---|
| `/profile` | `<p>Chargement du profil…</p>` | `Skeleton` reproduisant la forme des cards Compétences/Affirmations |
| `/report` | `<p>Génération du rapport…</p>` | `Skeleton` reproduisant la forme des 3 cards de score + listes |
| `/interview` | états `TRANSCRIBING`/`ANALYZING` : spinner + texte | Conserver tel quel — c'est une attente courte, contextualisée par la machine à états de l'entretien (`READY → AI_SPEAKING → LISTENING → TRANSCRIBING → ANALYZING → FEEDBACK`), le spinner suffit et un skeleton y ajouterait de la confusion (le contenu à venir n'est pas prévisible en forme, c'est un résultat d'IA) |

## Machine à états de l'entretien — cas particulier

L'écran `/interview` a déjà un vrai système d'états explicites (voir `interview.page.ts`). C'est le modèle à suivre pour tout futur flux multi-étapes du produit : chaque état a un libellé humain visible, jamais un simple spinner anonyme.
