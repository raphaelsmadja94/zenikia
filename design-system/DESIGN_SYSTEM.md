# ZenikIA Design System

> Challenge ce que tu sais. Améliore la manière dont tu le défends.

Ce document est la **source de vérité unique** pour toute décision visuelle du produit ZenikIA. Un développeur (ou Claude Code) doit pouvoir y travailler sans jamais avoir besoin de retourner analyser zenika.com.

## Philosophie visuelle

**"Si Zenika avait conçu ce produit, il pourrait ressembler à ça"** — pas une copie de zenika.com. zenika.com est un site de marque : sombre, spacieux, pensé pour convaincre en quelques secondes de scroll. ZenikIA est un outil de travail qu'on utilise 20-30 minutes d'affilée en préparation d'un entretien client. Trois traductions volontaires :

1. **Densité plus élevée.** Le site marketing peut se permettre un H1 à 42px et des kilomètres d'espace vide. Un outil de travail affiche plus d'information utile par écran (voir `foundations/typography.md` pour l'échelle resserrée).
2. **Le gradient de marque devient une exception, pas un fond.** Sur zenika.com, le gradient rouge→magenta (`#EE2238 → #BF1D67`) porte un seul CTA ("Contactez-nous"). Dans le produit, il ne doit jamais dépasser 1-2 usages par écran (l'action la plus importante) — sinon il perd sa force de signal. Voir `components/button.md` variante `brand`.
3. **Contraste renforcé.** Le site source utilise des transparences (`rgba(255,255,255,.87)`) qui passent sur un fond noir profond, mais un produit clair (choix par défaut de ZenikIA, voir plus bas) doit rester lisible longtemps sans fatigue — chaque token de texte est vérifié WCAG AA (`foundations/accessibility.md`).

## Brand principles

- **Direct** : jamais de jargon marketing vide, jamais de superlatif non justifié. Cohérent avec la philosophie produit ("Indicateur de coaching", jamais "Certifié").
- **Sobre** : une seule action mise en avant par écran, une seule ombre visible, une seule police d'accent.
- **Crédible** : le produit doit ressembler à un vrai outil interne, pas à une maquette d'étudiant (rappel du brief produit initial).

## Audit de référence (zenika.com)

Valeurs extraites via DevTools (`getComputedStyle`), pas des approximations visuelles. zenika.com est construit en React + Material-UI — ça explique la cohérence structurelle qu'on retrouve ci-dessous (une seule élévation de card, deux paliers de radius, grille aux breakpoints MUI standards).

| Élément | Valeur relevée |
|---|---|
| Fond | `rgb(18,18,18)` |
| Surface de card | `rgba(30,30,30,.5)`, bordure `rgba(255,255,255,.1)` |
| Texte primaire | `rgba(255,255,255,.87)` |
| Gradient CTA | `linear-gradient(51deg, #EE2238 -57%, #BF1D67 157%)` |
| Titres | Nunito, 700 |
| Corps | Open Sans, 400 |
| Radius carte | 16px |
| Radius bouton | 25-50px (quasi-pilule) |
| Ombre de carte | `0 4px 4px rgba(0,0,0,.25)` |
| Hauteur de nav | 72px |
| Breakpoints | 600 / 900 / 1200 / 1536 (défauts MUI) |

Ce qui n'a **pas** pu être observé (aucun formulaire sur la home publique) est documenté comme extrapolation explicite dans `components/form-controls.md`, jamais présenté comme une valeur "officielle" Zenika.

## Palette

Voir `tokens/colors.css` pour les valeurs exactes. Toujours utiliser les **tokens sémantiques** (`--znk-color-primary`, `--znk-color-text-secondary`...), jamais les primitives (`--znk-palette-*`) directement dans un composant — les primitives ne sont référencées que par `colors.css` lui-même.

| Rôle | Token | Note |
|---|---|---|
| Action principale | `--znk-color-primary` | dérivé du gradient CTA, densifié pour un usage répété |
| Action signature (rare) | `--znk-gradient-brand` | le gradient lui-même, réservé à 1-2 CTA par produit |
| Fond de page | `--znk-color-background` | |
| Surface (card) | `--znk-color-surface` | |
| Texte | `--znk-color-text-primary` / `-secondary` / `-muted` | 3 paliers, jamais une 4ᵉ valeur de gris ad hoc |
| Bordure | `--znk-color-border` / `-strong` | |
| Statuts | `--znk-color-success/warning/error/info` (+ `-bg`) | jamais une autre couleur pour un statut |

Le produit reste **clair par défaut** (cohérent avec l'app existante) ; un thème sombre est prévu (`prefers-color-scheme` + `data-theme="dark"`) et reprend plus fidèlement les valeurs sombres de zenika.com.

## Typographie, spacing, radius, shadows, breakpoints

Voir respectivement `foundations/typography.md`, `foundations/layout.md`, `tokens/radius.css`, `tokens/shadows.css`, `foundations/responsive.md`. Règle transverse : **toute valeur doit venir d'un token**. Aucune couleur, taille, ombre, radius ou spacing ne doit être introduite arbitrairement dans un composant — c'est la règle n°1 de ce Design System.

## Accessibilité

`foundations/accessibility.md` — non négociable, voir la checklist qui y figure.

## Composants

| Composant | Doc | Implémenté (Angular) |
|---|---|---|
| Button | `components/button.md` | ✅ `shared/ui/button` |
| Card | `components/card.md` | ✅ `shared/ui/card` |
| Badge | `components/feedback.md` | ✅ `shared/ui/badge` |
| Alert | `components/feedback.md` | ✅ `shared/ui/alert` |
| Progress / Meter | `components/data-display.md` | ✅ `shared/ui/progress-meter` |
| Skeleton | `components/data-display.md` | ✅ `shared/ui/skeleton` |
| Toast | `components/feedback.md` | ⏳ documenté, non implémenté (pas de besoin produit actuel) |
| Input / Select / Checkbox / Switch | `components/form-controls.md` | ⏳ documenté, non implémenté (aucun formulaire texte dans le POC) |
| Modal / Dropdown / Tooltip | `components/overlay.md` | ⏳ documenté, non implémenté |
| Header | `components/navigation.md` | ✅ `app.html` (à migrer sur tokens) |
| Sidebar / Tabs | `components/navigation.md` / `data-display.md` | ⏳ documenté, aucun besoin produit actuel |

Un composant marqué ⏳ a volontairement une doc mais pas de code — l'implémenter avant qu'un écran en ait réellement besoin serait de la spéculation, contraire à la règle "pas d'overengineering" du projet.

## Patterns

`patterns/forms.md`, `patterns/empty-states.md`, `patterns/loading-states.md`.

## Do / Don't (règles transverses)

✅ Toujours importer les tokens via `design-system/tokens/index.css` (un seul point d'entrée).
✅ Réutiliser un composant existant avant d'en écrire un nouveau.
✅ Documenter un nouveau pattern **dans ce Design System** au moment où il est créé, pas après coup.
❌ Aucune couleur hex, taille px, ombre ou radius en dur dans un fichier `*.page.css` ou un composant.
❌ Dupliquer une variante de card/bouton au lieu d'utiliser `[variant]` sur le composant partagé.
❌ Copier un écran de zenika.com pixel pour pixel — ce n'est pas l'objectif (voir Philosophie visuelle).

## Règles de contribution

**Avant de créer un nouveau token** : vérifier qu'aucun token existant ne couvre déjà le besoin (`grep -r "znk-" design-system/tokens/`). Un token n'est ajouté que si un composant en a un besoin réel, jamais par anticipation.

**Avant de créer un nouveau composant** : vérifier qu'un composant existant + une variante ne suffit pas. Si un vrai nouveau composant est nécessaire :
1. Écrire d'abord sa doc dans `components/*.md` (anatomie, variantes, états, tokens utilisés, do/don't) — même schéma que les fichiers existants.
2. L'implémenter dans `frontend/src/app/shared/ui/<nom>/`.
3. Mettre à jour le tableau "Composants" ci-dessus.

**Avant de créer un nouveau pattern** (`patterns/*.md`) : uniquement si une composition de composants existants est amenée à se répéter sur plusieurs écrans. Un pattern utilisé une seule fois ne mérite pas sa propre doc — c'est juste un écran.

## Limites connues de ce Design System (POC)

- Pas de vrai formulaire texte observé sur zenika.com ni implémenté dans le produit — `form-controls.md` est une extrapolation assumée, à valider dès qu'un premier champ réel apparaît.
- Pas de dashboard multi-widgets dans ce POC — pas de grille à colonnes formalisée au-delà de `flex`/`grid` simples.
- Le thème sombre est préparé (tokens définis) mais pas activement testé sur tous les écrans — le produit reste livré en thème clair par défaut.
