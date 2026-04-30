# Yoga App

Application full-stack de gestion de sessions de yoga.

- **Backend** : Spring Boot 3 · Java 21 · MySQL (Docker) · port `8080`
- **Frontend** : Angular 19 · port `4200`

---

## Prérequis

| Outil          | Version minimale      |
| -------------- | --------------------- |
| Java (JDK)     | 21                    |
| Maven          | 3.9+                  |
| Node.js        | 18+                   |
| npm            | 9+                    |
| Docker Desktop | toute version récente |

---

## Backend

### Variables d'environnement

L'application lit sa configuration depuis des variables d'environnement. Crée un fichier `.env` à la racine de `back/` (ou exporte-les dans ton shell) :

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=test
DB_USER=user_test
DB_PASSWORD=test_password
TOKEN_SECRET=unSecretJWTDe64CaracteresMinimumPourHS512Algorithm000000000000
```

> `TOKEN_SECRET` doit faire **au minimum 64 caractères** (512 bits requis par HS512).

### Installation et démarrage

```bash
cd back

# Démarrer le backend (lance aussi le container MySQL via Docker Compose)
mvn spring-boot:run
```

Spring Boot démarre Docker Compose automatiquement. L'API est disponible sur `http://localhost:8080`.

### Initialiser la base de données

Au premier démarrage, insère l'utilisateur admin en exécutant le script SQL dans le container :

```bash
docker exec -it back_mysql mysql -u user_test -ptest_password test
```

Puis dans le prompt MySQL :

```sql
source /chemin/vers/back/src/main/resources/sql/insert_user.sql
```

Ou colle directement :

```sql
INSERT INTO users(first_name, last_name, admin, email, password)
VALUES ('Admin', 'Admin', true, 'yoga@studio.com',
        '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq');
```

Compte admin par défaut :

- **Email** : `yoga@studio.com`
- **Mot de passe** : `test!1234`

> Ne pas hésiter à ajoute des teachers via le SQL pour tester les fonctionnalités de création de sessions.

---

## Frontend

### Installation

```bash
cd front
npm install
```

### Démarrage

```bash
npm run start
```

L'application est disponible sur `http://localhost:4200`.

> Le backend doit être démarré pour que le frontend fonctionne correctement.

---

## Tests

### Backend — tests unitaires et d'intégration

Les tests backend utilisent une base **H2 en mémoire** : Docker n'est **pas nécessaire** pour les lancer.

```bash
cd back

# Lancer tous les tests
mvn test

# Lancer un test spécifique
mvn test -Dtest=SessionServiceTest

# Lancer une classe de test avec un pattern
mvn test -Dtest="*Controller*"
```

### Frontend — tests unitaires (Jest)

```bash
cd front

# Lancer les tests une fois
npm test

# Mode watch (relance à chaque modification)
npm run test:watch
```

---

## Rapports de couverture

### Backend — JaCoCo

Le rapport est généré automatiquement lors de `mvn test`.

```bash
cd back
mvn test
```

Ouvrir le rapport :

```
back/target/site/jacoco/index.html
```

Seuil configuré : **80 %** sur instructions, branches, lignes et méthodes. Le build échoue si le seuil n'est pas atteint.

---

### Frontend — Jest (tests unitaires)

```bash
cd front

# Générer le rapport de couverture
npm test -- --coverage
```

Ouvrir le rapport :

```
front/coverage/jest/lcov-report/index.html
```

Seuil configuré : **80 %** sur statements, branches, functions et lines.

---

### Frontend — Cypress (tests E2E)

Les tests E2E mockent toutes les requêtes API avec `cy.intercept` : **le backend n'est pas nécessaire**. La commande `e2e:ci` démarre le serveur Angular automatiquement.

```bash
cd front

# Lancer les tests E2E (démarre le serveur Angular automatiquement)
npm run e2e:ci
```

Générer le rapport de couverture E2E (après l'exécution des tests) :

```bash
cd front
npm run e2e:coverage
```

Ouvrir le rapport :

```
front/coverage/e2e/lcov-report/index.html
```

Générer le rapport JUnit XML (artefact CI) :

```bash
cd front && ./node_modules/.bin/cypress run --reporter junit --reporter-options "mochaFile=cypress/reports/results-[hash].xml"
```

Le rapport consolidé est disponible dans `front/cypress/reports/`.

Ouvrir Cypress en mode interactif (pour déboguer) :

```bash
cd front
npm run cypress:open
```

---

## Récapitulatif des commandes

### Backend

| Action           | Commande                                     |
| ---------------- | -------------------------------------------- |
| Démarrer l'app   | `mvn spring-boot:run`                        |
| Lancer les tests | `mvn test`                                   |
| Rapport JaCoCo   | `mvn test` → `target/site/jacoco/index.html` |
| Build complet    | `mvn clean install`                          |

### Frontend

| Action                       | Commande                                                   |
| ---------------------------- | ---------------------------------------------------------- |
| Démarrer l'app               | `npm run start`                                            |
| Tests unitaires              | `npm test`                                                 |
| Tests unitaires + couverture | `npm test -- --coverage`                                   |
| Rapport Jest                 | `coverage/jest/lcov-report/index.html`                     |
| Tests E2E (headless)         | `npm run e2e:ci`                                           |
| Tests E2E (interactif)       | `npm run cypress:open`                                     |
| Rapport E2E                  | `npm run e2e:coverage` → `coverage/e2e/lcov-report/index.html` |
