# Button

Composant Angular : `frontend/src/app/shared/ui/button/button.component.ts` (sélecteur `zk-button`).

## Anatomie

Icône optionnelle (avant ou après le label) + label. Jamais un bouton icône-seul sans `aria-label`.

## Variantes

| Variante | Usage | Fond | Texte |
|---|---|---|---|
| `primary` | UNE action principale par écran/section (Analyser mon CV, Commencer l'entretien) | `--znk-color-primary` | `--znk-color-on-primary` |
| `brand` | Action la plus signature du produit UNIQUEMENT (ex : lancer l'entretien) — utilise `--znk-gradient-brand`, l'équivalent du CTA "Contactez-nous" de zenika.com. Jamais plus d'un par page. | `--znk-gradient-brand` | `--znk-color-on-primary` |
| `ghost` | Actions secondaires (Réessayer, Annuler, navigation) | transparent, bordure `--znk-color-border` | `--znk-color-text-primary` |
| `danger` | Action destructive ou état d'erreur actionnable | `--znk-color-error-bg` | `--znk-color-error` |

## Tailles

`sm` (32px de haut, contexte compact type card), `md` (40px, défaut), `lg` (48px, CTA de page).

## États obligatoires

- `:hover` → assombrit le fond de 1 palier (`--znk-color-primary-hover`).
- `:active` → `--znk-color-primary-active`.
- `:focus-visible` → `box-shadow: var(--znk-shadow-focus)`, jamais de `outline: none` sans ce remplacement.
- `:disabled` → `opacity: 0.5`, `cursor: not-allowed`, aucune interaction hover/active.
- `loading` (état applicatif, pas pseudo-classe CSS) → spinner à la place de l'icône, label conservé ou remplacé par un texte d'état ("Analyse en cours…"), bouton désactivé pendant ce temps.

## Do / Don't

✅ Un seul bouton `primary` visible par section.
❌ Deux boutons `primary` côte à côte (aucune hiérarchie claire pour l'utilisateur).
✅ `brand` réservé aux 1-2 actions les plus importantes du produit.
❌ `brand` sur un bouton "Annuler" ou une action secondaire — dilue le signal.
