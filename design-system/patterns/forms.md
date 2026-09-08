# Pattern : Formulaires

## Dropzone (upload de fichier)

Le seul "formulaire" du POC actuel (`/upload`). Style déjà cohérent avec les tokens (`upload.page.css`) :

```css
border: 2px dashed var(--znk-color-border);
border-radius: var(--znk-radius-md);
background: var(--znk-color-surface-secondary);
```

États : `default` → `--over` (survol de drag, `border-color: var(--znk-color-primary)`, fond `--znk-color-primary-subtle`) → `--filled` (fichier sélectionné, bordure pleine `--znk-color-primary`). Toujours donner un moyen de sélection au clavier (input file natif visuellement caché, jamais un `<div onclick>` seul — c'est déjà le cas actuellement, à conserver).

## Validation

- Erreur affichée sous le champ concerné (jamais seulement en haut de page) via le pattern `feedback.md` §messages d'erreur.
- Un formulaire ne se soumet jamais silencieusement en échec : le bouton de soumission passe en état `loading` (voir `button.md`), jamais juste désactivé sans indication.

## Prochain vrai formulaire de ce produit

Aucun champ texte n'existe encore. Le jour où l'un apparaît (ex: renommer une session), utiliser `components/form-controls.md` sans réinventer un style.
