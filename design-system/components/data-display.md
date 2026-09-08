# Data display (Progress/Meter, Skeleton, Tabs)

## Progress / Meter

Composant Angular : `frontend/src/app/shared/ui/progress-meter/progress-meter.component.ts` (sélecteur `zk-progress-meter`).

Utilisé pour tous les scores 0-5 ou 0-10 du produit (technique, communication, rapport final) — remplace les multiples implémentations locales de `.zk-meter` dispersées dans `interview.page.css`/`report.page.css` (identifié dans l'audit).

```css
.zk-progress-meter { height: 8px; border-radius: var(--znk-radius-full); background: var(--znk-color-surface-secondary); overflow: hidden; }
.zk-progress-meter__fill { height: 100%; border-radius: var(--znk-radius-full); background: var(--znk-color-primary); transition: width 0.4s ease; }
```

`role="progressbar"` + `aria-valuenow`/`aria-valuemin`/`aria-valuemax` obligatoires — ces barres portent une vraie information (un score), pas juste décoratives.

## Skeleton

Composant Angular : `frontend/src/app/shared/ui/skeleton/skeleton.component.ts` (sélecteur `zk-skeleton`).

Remplace les textes bruts "Chargement…"/"Génération du rapport…" actuellement utilisés sur `/profile` et `/report` (identifié dans l'audit comme un vrai manque — le pattern loading-states.md l'impose désormais). Rectangle `background: var(--znk-color-surface-secondary)` avec une animation de balayage douce (`prefers-reduced-motion` respecté : pas d'animation si l'utilisateur l'a désactivée, juste la couleur statique).

## Tabs

**Statut POC** : non implémenté — aucun écran actuel n'a besoin de contenu à onglets. À documenter si nécessaire plus tard (ex: rapport avec plusieurs vues technique/communication/historique).
