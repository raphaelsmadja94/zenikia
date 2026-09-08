# ZenikIA Design System

Source de vérité unique pour toute décision UX/UI du projet. Voir [DESIGN_SYSTEM.md](DESIGN_SYSTEM.md) pour la documentation complète (philosophie, palette, composants, patterns, règles de contribution).

## Structure

```
design-system/
├── DESIGN_SYSTEM.md     ← documentation complète, à lire en premier
├── tokens/               ← valeurs CSS (source de vérité technique)
│   ├── index.css         ← point d'entrée, importé par frontend/src/styles.css
│   ├── colors.css / typography.css / spacing.css
│   ├── radius.css / shadows.css / z-index.css / breakpoints.css
├── foundations/          ← principes transverses
│   ├── typography.md / layout.md / accessibility.md / responsive.md
├── components/           ← spec de chaque composant (anatomie, variantes, états, do/don't)
│   ├── button.md / form-controls.md / card.md
│   ├── feedback.md / overlay.md / navigation.md / data-display.md
└── patterns/              ← compositions de plusieurs composants
    ├── forms.md / empty-states.md / loading-states.md
```

## Qui utilise quoi

- **Les tokens CSS** (`tokens/*.css`) sont importés une seule fois par `frontend/src/styles.css` et disponibles partout dans l'app via `var(--znk-*)`.
- **Les composants Angular réels** vivent dans `frontend/src/app/shared/ui/` — un fichier `.md` ici documente le composant, le code Angular l'implémente.
- **Claude Code** suit la skill `.claude/skills/zenika-design-system/SKILL.md`, qui impose de consulter ce Design System avant toute modification frontend. Ce n'est pas une lecture facultative pour un humain non plus.

## Mettre à jour ce Design System

Un token ou un composant ne s'ajoute que lorsqu'un besoin réel du produit ne peut pas être résolu par l'existant — voir `DESIGN_SYSTEM.md` §Règles de contribution.
