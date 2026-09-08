# Navigation (Header, Sidebar)

## Header

Composant actuel : `frontend/src/app/app.html`/`app.css` (à migrer sur les tokens, voir audit). Repris de zenika.com : hauteur fixe **72px**, fond identique au fond de page (pas de nav flottante contrastée), logo + nom de marque à gauche.

```css
height: 72px;
background: var(--znk-color-surface);
border-bottom: 1px solid var(--znk-color-border);
```

## Sidebar

**Statut POC** : non implémenté — le produit est un parcours linéaire (landing → upload → profile → interview → report), pas un dashboard à navigation persistante. À documenter si un jour une navigation latérale devient nécessaire (ex: historique de sessions).

## Do / Don't

✅ Header identique sur toutes les pages (déjà le cas via `app.html`).
❌ Réintroduire une nav secondaire par page tant que le parcours reste linéaire — ajouter de la navigation sans besoin réel complexifie sans bénéfice utilisateur.
