# Yoga App — Frontend

Angular 19 · port `4200`

---

## Prérequis

| Outil   | Version minimale |
| ------- | ---------------- |
| Node.js | 18+              |
| npm     | 9+               |

---

## Installation

```bash
npm install
```

---

## Démarrage

```bash
npm run start
```

L'application est disponible sur `http://localhost:4200`.

> Le backend doit être démarré pour que le frontend fonctionne correctement.

---

## Tests unitaires (Jest)

```bash
# Lancer les tests une fois
npm test

# Mode watch
npm run test:watch

# Avec rapport de couverture
npm test -- --coverage
```

Rapport de couverture :

```
coverage/jest/lcov-report/index.html
```

Seuil configuré : **80 %** sur statements, branches, functions et lines.

---

## Tests E2E (Cypress)

Les tests mockent toutes les requêtes API avec `cy.intercept` : **le backend n'est pas nécessaire**. La commande `e2e:ci` démarre le serveur Angular automatiquement.

```bash
# Lancer les tests en mode headless
npm run e2e:ci

# Ouvrir Cypress en mode interactif
npm run cypress:open
```

### Rapport de couverture E2E

```bash
npm run e2e:coverage
```

Rapport disponible dans :

```
coverage/e2e/lcov-report/index.html
```

### Rapport JUnit XML (artefact CI)

```bash
./node_modules/.bin/cypress run --reporter junit --reporter-options "mochaFile=cypress/reports/results-[hash].xml"
```

Rapport consolidé dans `cypress/reports/`.
