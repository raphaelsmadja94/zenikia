# Form controls (Input, Textarea, Select, Checkbox, Radio, Switch)

**Statut POC** : aucun de ces composants n'est encore implémenté en Angular — le produit actuel n'a pas de formulaire texte (seul l'upload de fichier existe, géré par le pattern `forms.md` §Dropzone). Cette page documente les tokens et règles à appliquer **dès qu'un premier vrai champ de formulaire sera nécessaire**, pour que Claude n'improvise pas un style à ce moment-là.

## Anatomie commune

`<label>` (toujours visible, jamais un placeholder seul) → champ → texte d'aide optionnel → message d'erreur (remplace le texte d'aide, jamais les deux en même temps).

## Style de base (extrapolé du langage carte du DS, pas observable sur zenika.com — aucun formulaire public)

```css
.zk-field {
  background: var(--znk-color-surface);
  border: 1px solid var(--znk-color-border);
  border-radius: var(--znk-radius-md);
  padding: var(--znk-space-3) var(--znk-space-4);
  font-size: var(--znk-font-size-base);
  color: var(--znk-color-text-primary);
}
.zk-field:hover { border-color: var(--znk-color-border-strong); }
.zk-field:focus-visible { border-color: var(--znk-color-primary); box-shadow: var(--znk-shadow-focus); outline: none; }
.zk-field[aria-invalid="true"] { border-color: var(--znk-color-error); }
.zk-field:disabled { background: var(--znk-color-surface-secondary); color: var(--znk-color-text-muted); cursor: not-allowed; }
```

## Checkbox / Radio / Switch

Taille de cible tactile minimum 24×24px (zone cliquable, même si le visuel est plus petit) — règle d'accessibilité, pas de dérogation. `Switch` utilise `--znk-color-primary` à l'état "on", `--znk-color-border-strong` à l'état "off" — jamais de rouge/vert (ce n'est pas un statut succès/erreur, juste un on/off).

## Messages d'erreur

Toujours sous le champ, `color: var(--znk-color-error)`, `font-size: var(--znk-font-size-sm)`, précédé d'une icône d'alerte. Le message doit dire quoi corriger, jamais juste "Champ invalide" (cohérent avec la règle de feedback actionnable du Coaching Engine backend — même philosophie côté UI).
