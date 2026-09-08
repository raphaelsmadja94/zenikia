# Pattern : États vides

Un état vide n'est jamais un écran blanc silencieux. Trois ingrédients obligatoires : une explication de pourquoi c'est vide, une action pour en sortir (si possible), un ton cohérent avec la marque (direct, jamais alarmiste).

## Occurrences actuelles dans le produit

| Écran | État vide | Traitement actuel | Traitement cible |
|---|---|---|---|
| `/profile` | Aucune compétence détectée dans le CV | `<p class="zk-empty">Aucune compétence détectée dans ce CV.</p>` | Conserver le message, styler via un token `--znk-color-text-muted` (déjà correct dans l'esprit, à migrer sur les tokens du DS) |
| `/profile` | Aucune affirmation notable détectée | idem | idem |
| `/report` | Pas assez de signal pour générer top strengths/priorities | `<p class="zk-empty">Pas assez de signal pour cette session.</p>` | Conserver — c'est déjà le bon ton (factuel, pas d'excuse vague) |

## Règle de rédaction

Toujours expliquer la cause probable, jamais juste "Rien à afficher". "Aucune compétence détectée dans ce CV" est correct ; "Aucune donnée" ne le serait pas.
