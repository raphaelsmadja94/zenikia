# Card

Composant Angular : `frontend/src/app/shared/ui/card/card.component.ts` (sélecteur `zk-card`).

Source directe de zenika.com — c'est le composant le plus fidèlement repris de l'audit (cards "Innovate/Optimize/Transform") :

```css
background: var(--znk-color-surface);
border: 1px solid var(--znk-color-border);
border-radius: var(--znk-radius-lg); /* 16px, valeur exacte relevée */
box-shadow: var(--znk-shadow-sm);
padding: var(--znk-space-6);
```

## Variantes (`[variant]` input)

- `default` — ci-dessus.
- `elevated` — `box-shadow: var(--znk-shadow-md)`, pour une card qui doit se détacher (résultat le plus important d'un rapport).
- `interactive` — `default` + `cursor: pointer` + `box-shadow: var(--znk-shadow-md)` au hover + `border-color: var(--znk-color-primary)` au focus-visible (card sélectionnable, ex: choix de persona).
- `inverse` — fond `--znk-color-surface-inverse`, texte `--znk-color-text-on-inverse` — réservé à un contexte de mise en avant forte (rare dans ce produit).

## Anatomie

Header optionnel (titre + action) → corps → footer optionnel (actions). Le padding ne s'applique jamais deux fois (pas de padding sur le composant ET son contenu direct).

## Do / Don't

✅ Une seule ombre (`--znk-shadow-sm` par défaut) — jamais cumuler plusieurs `box-shadow` à la main.
❌ Redéfinir `border-radius`/`padding` localement par écran (c'est exactement le problème identifié dans l'audit du frontend existant : `.zk-persona-card`, `.zk-score-card`, `.zk-assess-card` réinventaient chacune leur propre variante de card).
✅ Utiliser `variant="interactive"` pour toute card cliquable plutôt que de coder le hover à la main.
