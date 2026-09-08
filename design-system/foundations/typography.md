# Typographie

## Familles

- **Titres** : `--znk-font-heading` → Nunito, 700. C'est la police de marque Zenika (relevée sur zenika.com) — elle porte l'identité, on ne la change jamais pour un titre.
- **Corps de texte** : `--znk-font-body` → Open Sans, 400. Plus lisible en petit corps qu'une police display comme Nunito.
- **Code / valeurs techniques** : `--znk-font-mono` (transcript, identifiants) — jamais Nunito/Open Sans pour du contenu à alignement fixe.

Charger via Google Fonts (déjà fait dans `frontend/src/styles.css`) :
```
@import url('https://fonts.googleapis.com/css2?family=Nunito:wght@600;700;800&family=Open+Sans:wght@400;600&display=swap');
```

## Échelle

| Token | Taille | Usage |
|---|---|---|
| `--znk-font-size-xs` | 12px | légendes, disclaimers, timestamps |
| `--znk-font-size-sm` | 13px | labels de formulaire, métadonnées |
| `--znk-font-size-base` | 15px | corps de texte par défaut |
| `--znk-font-size-md` | 16px | texte mis en avant dans une card |
| `--znk-font-size-lg` | 19px | sous-titre de section, titre de card |
| `--znk-font-size-xl` | 22px | titre de section |
| `--znk-font-size-2xl` | 28px | titre de page |
| `--znk-font-size-3xl` | 36px | hero (landing uniquement — jamais dans l'app produit) |

**Règle** : l'échelle produit est volontairement plus resserrée que zenika.com (qui monte à 42px en H1). Un outil de travail affiche du contenu dense ; le site marketing peut se permettre de grands espaces vides.

## Poids et hiérarchie

- `--znk-font-weight-bold` (700) : titres (H1-H3), valeurs chiffrées mises en avant (scores).
- `--znk-font-weight-medium` (600) : labels de bouton, sous-titres, emphase courte.
- `--znk-font-weight-regular` (400) : tout le reste.

Un titre `<h1>`/`<h2>` utilise toujours `--znk-font-heading` + `--znk-font-weight-bold` + `--znk-letter-spacing-tight`. Un `<h4>` ou plus petit reste en `--znk-font-weight-medium` sans letter-spacing serré (ça devient illisible en dessous de 19px).

## Do / Don't

✅ Utiliser `--znk-font-size-lg` pour un titre de card.
❌ Ne jamais écrire `font-size: 18px` en dur — pas de valeur intermédiaire hors échelle.
✅ Nunito uniquement sur les titres et les CTA.
❌ Ne jamais utiliser Nunito pour un paragraphe long (fatigue de lecture).
