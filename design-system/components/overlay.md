# Overlay (Modal / Dialog, Dropdown, Tooltip)

**Statut POC** : aucun n'est implémenté aujourd'hui (aucun écran actuel n'en a besoin — pas de confirmation destructive, pas de menu déroulant). Documenté à l'avance pour que Claude ait une base cohérente le jour où l'un de ces patterns devient nécessaire (ex : confirmation avant d'abandonner un entretien en cours).

## Modal / Dialog

- `--znk-z-overlay` pour le fond assombri (`rgba(0,0,0,.4)`), `--znk-z-modal` pour la boîte.
- Boîte : `background: var(--znk-color-surface)`, `border-radius: var(--znk-radius-lg)`, `box-shadow: var(--znk-shadow-lg)`, `padding: var(--znk-space-8)`, largeur max `480px` (confirmation) ou `720px` (contenu riche).
- Focus trap obligatoire (le focus clavier ne doit pas pouvoir sortir de la modale tant qu'elle est ouverte) ; `Échap` ferme ; le focus revient à l'élément qui a ouvert la modale à la fermeture.
- `role="dialog"` + `aria-modal="true"` + `aria-labelledby` pointant le titre.

## Dropdown

`box-shadow: var(--znk-shadow-md)`, `border-radius: var(--znk-radius-md)`, `z-index: var(--znk-z-dropdown)`. Fermeture au clic extérieur et à `Échap`. Navigation clavier (flèches haut/bas, Entrée pour sélectionner) si le contenu est une liste d'options.

## Tooltip

Réservé à un complément d'info non essentiel (jamais une information nécessaire à l'usage — sinon elle doit être visible directement). `z-index: var(--znk-z-tooltip)`, apparition au hover ET au focus clavier (pas hover seul, sinon inaccessible au clavier), délai d'apparition ~400ms pour éviter le clignotement au survol rapide.
