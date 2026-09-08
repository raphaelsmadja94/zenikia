# Accessibilité

Non négociable pour ce projet : ZenikIA s'adresse à des consultants qui préparent un entretien, souvent au clavier, parfois avec des besoins d'accessibilité variés. Ce n'est pas une option "si le temps le permet".

## Contraste

Tous les tokens de couleur texte/fond de ce Design System ont été choisis pour respecter **WCAG AA** (contraste ≥ 4.5:1 pour le texte normal, ≥ 3:1 pour le grand texte/UI). C'est précisément pourquoi les couleurs produit (`design-system/tokens/colors.css`) ne sont pas des copier-coller directs de zenika.com : le site marketing utilise `rgba(255,255,255,.87)` sur fond `#121212` qui passe, mais certains textes secondaires du site n'atteindraient pas AA sur un fond clair — on a resserré le contraste des tokens `--znk-color-text-secondary` / `--znk-color-text-muted` en conséquence.

**Règle** : toute nouvelle couleur de texte doit être vérifiée au contraste avant d'être ajoutée aux tokens (outil : contrast checker WCAG). Ne jamais ajouter une couleur de texte "parce qu'elle est jolie".

## États clavier

- **Tout élément interactif** (bouton, lien, input, card cliquable) doit avoir un état `:focus-visible` visible — jamais `outline: none` sans remplacement. Le Design System fournit `--znk-shadow-focus` pour ça.
- Ordre de tabulation logique (DOM = ordre visuel, pas de `tabindex` positif).
- Les icônes seules (bouton "voir/cacher", fermeture de modale) ont toujours un `aria-label`.

## Structure sémantique

- Un seul `<h1>` par page. Hiérarchie de titres continue (pas de saut h1→h3).
- Les états de chargement/erreur/vide sont annoncés (`aria-live="polite"` sur la zone qui change — ex: l'état de l'entretien qui passe de LISTENING à ANALYZING).
- Formulaires : chaque champ a un `<label>` associé (`for`/`id`), jamais un placeholder utilisé comme seul label.

## Mouvement

Respecter `prefers-reduced-motion` : toute animation décorative (pulse du bouton micro, transition de carte) doit avoir une version réduite ou nulle.

```css
@media (prefers-reduced-motion: reduce) {
  * { animation-duration: 0.01ms !important; transition-duration: 0.01ms !important; }
}
```

## Checklist rapide avant de livrer un écran

- [ ] Contraste texte/fond vérifié (tokens du DS = déjà conformes, mais vérifier les combinaisons custom)
- [ ] Navigable entièrement au clavier (Tab, Shift+Tab, Entrée, Échap pour fermer une modale)
- [ ] `:focus-visible` présent sur chaque élément interactif
- [ ] Un seul `<h1>`, hiérarchie de titres cohérente
- [ ] États loading/empty/error annoncés, pas seulement visuels
