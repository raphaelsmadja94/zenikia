# Feedback (Badge, Alert, Toast)

## Badge

Composant Angular : `frontend/src/app/shared/ui/badge/badge.component.ts` (sélecteur `zk-badge`).

Pilule (`--znk-radius-full`), `padding: var(--znk-space-1) var(--znk-space-3)`, `font-size: var(--znk-font-size-xs)`, `font-weight: var(--znk-font-weight-medium)`.

Variantes sémantiques : `neutral` (défaut, `--znk-color-surface-secondary`/`--znk-color-text-secondary` — persona, catégorie de compétence), `primary` (mise en avant), `success`/`warning`/`error`/`info` (`--znk-color-{x}-bg`/`--znk-color-{x}`) — **jamais** une couleur de statut choisie à l'œil, toujours une de ces 4.

## Alert

Composant Angular : `frontend/src/app/shared/ui/alert/alert.component.ts` (sélecteur `zk-alert`).

Bandeau plein-largeur dans son conteneur, icône + message (+ action optionnelle). Mêmes 4 variantes sémantiques que Badge, fond `--znk-color-{x}-bg`, texte/icône `--znk-color-{x}`, `border-radius: var(--znk-radius-md)`, `padding: var(--znk-space-4)`.

Utilisé pour toute erreur applicative (échec d'appel IA, micro inaccessible) — remplace les `.zk-error` actuels dispersés dans chaque page. `role="alert"` obligatoire (annonce automatique aux lecteurs d'écran).

## Toast

**Statut POC** : non implémenté (le produit affiche ses erreurs inline via `Alert`, ce qui suffit pour un flux mono-écran comme l'entretien). À documenter en détail si un besoin de notification transverse apparaît (ex: sauvegarde en arrière-plan). Si implémenté un jour : `position: fixed`, `z-index: var(--znk-z-toast)`, empilement en bas à droite, auto-dismiss 5s sauf variante `error`.

## Do / Don't

✅ `Alert` pour toute erreur qui bloque l'action en cours (échec d'analyse IA).
❌ Un `<p>` rouge en dur à la place d'un `Alert` — perd le `role="alert"` et l'icône de contexte.
