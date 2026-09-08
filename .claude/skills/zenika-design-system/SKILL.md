---
name: zenika-design-system
description: Charte UX/UI et Design System du projet ZenikIA (tokens, composants, patterns dérivés de l'identité Zenika). À utiliser pour TOUTE tâche touchant l'UI, l'UX, le frontend, un composant, une page, un dashboard, un formulaire, le styling, le responsive ou l'accessibilité du projet — création d'un nouvel écran, modification d'un existant, ajout d'un composant, retouche de style, correction d'un bug visuel ou d'accessibilité inclus.
---

# Zenika Design System

Ce projet a un Design System (`design-system/`) qui est la **source de vérité unique** pour toute décision visuelle. Ce fichier explique comment l'appliquer.

## Quand cette skill s'applique

Dès qu'une tâche touche à : un écran/page Angular, un composant, du CSS, la mise en page, le responsive, l'accessibilité, un formulaire, un état de chargement/vide/erreur — même une "petite" retouche visuelle.

## Workflow obligatoire

1. **Lire le Design System avant toute modification UI.** Commencer par `design-system/DESIGN_SYSTEM.md`, puis le fichier de composant concerné dans `design-system/components/*.md` s'il existe. Ne jamais écrire de CSS "à l'instinct" sans avoir vérifié ce qui existe déjà.
2. **Identifier les composants existants réutilisables** dans `frontend/src/app/shared/ui/` avant d'en écrire un nouveau. Un composant à 90% équivalent + une variante (`[variant]`) est presque toujours préférable à un nouveau composant.
3. **Utiliser les design tokens** (`var(--znk-*)`, importés via `design-system/tokens/index.css` → `frontend/src/styles.css`). Toute couleur, taille de police, spacing, radius, ombre ou z-index vient d'un token.
4. **Ne pas inventer de style arbitraire.** Une valeur qui ne correspond à aucun token (`padding: 13px`, `#ff6644`, `border-radius: 7px`...) est un signal d'erreur, pas une liberté créative. Si le besoin est réel et qu'aucun token ne convient, l'ajouter au Design System (voir §5) plutôt que de le coder en dur.
5. **Vérifier la hiérarchie visuelle.** Un seul bouton `primary` par section, un seul `h1` par page, un gradient de marque réservé à 1-2 actions signature maximum (voir `components/button.md`).
6. **Vérifier responsive et accessibilité avant de considérer une tâche terminée** — pas après coup si le temps le permet. Checklist dans `design-system/foundations/accessibility.md`.
7. **Prévoir les états loading / empty / error** pour tout contenu asynchrone — jamais un écran qui reste silencieusement vide ou bloqué. Voir `design-system/patterns/loading-states.md` et `empty-states.md`.
8. **Respecter les états focus / hover / disabled** sur tout élément interactif — définis dans la doc de chaque composant (`components/button.md`, `form-controls.md`...). Ne jamais retirer un `outline`/`focus-visible` sans le remplacer par `--znk-shadow-focus`.
9. **Éviter les duplications CSS.** Si un style copie/adapte celui d'un autre composant, c'est le signal qu'il fallait réutiliser le composant partagé plutôt que le redupliquer localement — c'est exactement le problème identifié dans l'audit initial du frontend (plusieurs variantes de "card" quasi identiques par écran).
10. **Documenter un nouveau pattern dans le Design System uniquement lorsqu'il est réellement nécessaire** — c'est-à-dire quand aucun composant/pattern existant ne couvre le besoin ET que la composition risque de se répéter. Suivre la procédure de `DESIGN_SYSTEM.md` §Règles de contribution : documenter d'abord, implémenter ensuite, mettre à jour le tableau des composants.

## Ce qu'il ne faut jamais faire

- Copier un écran de zenika.com pixel pour pixel — c'est une référence de marque à traduire pour un produit SaaS dense, pas un site à répliquer (voir `DESIGN_SYSTEM.md` §Philosophie visuelle).
- Ajouter une dépendance UI externe (Material, Tailwind, Bootstrap...) — le Design System est natif Angular + CSS custom properties, volontairement sans framework UI tiers.
- Créer un composant `shared/ui/` pour un besoin qui n'existe qu'une fois dans le produit — dans ce cas, du CSS local au composant de page suffit (le Design System documente déjà comment, via les tokens).
- Implémenter un composant listé "⏳ documenté, non implémenté" dans `DESIGN_SYSTEM.md` (Input, Modal, Tabs...) sans qu'un écran réel en ait besoin maintenant — c'est de la spéculation, contraire aux règles du projet.

## Références rapides

- Source de vérité : [`design-system/DESIGN_SYSTEM.md`](../../../design-system/DESIGN_SYSTEM.md)
- Tokens : `design-system/tokens/*.css`
- Composants Angular : `frontend/src/app/shared/ui/`
- Règles permanentes projet : [`CLAUDE.md`](../../../CLAUDE.md) §Frontend & Design System
