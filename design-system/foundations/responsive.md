# Responsive

## Breakpoints (repris de zenika.com / MUI, voir `tokens/breakpoints.css`)

| Palier | Largeur | Contexte |
|---|---|---|
| (défaut, mobile-first) | 0 | mobile |
| `sm` | 600px | tablette portrait |
| `md` | 900px | tablette paysage / petit desktop |
| `lg` | 1200px | desktop |
| `xl` | 1536px | grand écran |

CSS ne permettant pas `var()` dans une media query, écrire directement la valeur mais **toujours l'un de ces 4 paliers** — jamais une valeur ad hoc type `@media (max-width: 860px)` choisie à l'œil (c'est pourtant ce que fait le frontend actuel, à corriger progressivement — voir `DESIGN_SYSTEM.md` §Audit).

```css
/* mobile-first : le style par défaut est mobile, on ajoute pour les écrans plus grands */
.zk-grid { grid-template-columns: 1fr; }

@media (min-width: 900px) {
  .zk-grid { grid-template-columns: repeat(2, 1fr); }
}
```

## Règles produit

- L'écran d'entretien (`/interview/:id`) doit rester pleinement utilisable sur tablette — c'est l'écran qu'un consultant est le plus susceptible d'utiliser en dehors d'un poste fixe.
- Aucun tableau ou contenu large ne doit provoquer un scroll horizontal de la page entière : l'isoler dans son propre conteneur `overflow-x: auto`.
- Les grilles de cards (compétences, résultats du rapport) passent systématiquement à une seule colonne sous `md` (900px).
