# Layout

## Grille et largeur de page

- Largeur de contenu max : `1080px` (`.zk-page`), centrée, `padding-inline: 24px`.
- Variante étroite (formulaires, upload, texte long) : `720px` (`.zk-page--narrow`).
- Pas de grille à colonnes fixes (12 col) pour ce POC — le contenu s'organise en `flex`/`grid` CSS natif par composant, avec un `gap` toujours pris dans `--znk-space-*`. Une vraie grille à colonnes ne sera introduite que si un pattern (ex: dashboard multi-widgets) l'exige réellement.

## Espacement

Toujours utiliser les tokens `--znk-space-*` (base 4px). Repères d'usage :

| Contexte | Token |
|---|---|
| Gap entre icône et texte | `--znk-space-2` |
| Gap entre éléments d'un formulaire | `--znk-space-4` |
| Padding interne d'une card standard | `--znk-space-6` |
| Padding interne d'une card mise en avant | `--znk-space-8` |
| Séparation entre sections d'une page | `--znk-space-12` |

## Cards et surfaces

Une card = `background: var(--znk-color-surface)` + `border: 1px solid var(--znk-color-border)` + `border-radius: var(--znk-radius-lg)` + `box-shadow: var(--znk-shadow-sm)` par défaut, `--znk-shadow-md` au survol si la card est cliquable. Voir `components/card.md`.

## Empilement (z-index)

Utiliser uniquement les tokens `--znk-z-*` (`tokens/z-index.css`). Ne jamais écrire une valeur de z-index en dur — c'est systématiquement le symptôme d'un conflit d'empilement mal diagnostiqué.
