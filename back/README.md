# Yoga App — Backend

Spring Boot 3 · Java 21 · MySQL (Docker) · port `8080`

---

## Prérequis

| Outil          | Version minimale |
| -------------- | ---------------- |
| Java (JDK)     | 21               |
| Maven          | 3.9+             |
| Docker Desktop | toute version    |

---

## Variables d'environnement

Crée un fichier `.env` à la racine de `back/` :

```env
DB_HOST=localhost
DB_PORT=3306
DB_NAME=test
DB_USER=user_test
DB_PASSWORD=test_password
TOKEN_SECRET=unSecretJWTDe64CaracteresMinimumPourHS512Algorithm000000000000
```

> `TOKEN_SECRET` doit faire **au minimum 64 caractères** (requis par HS512).

---

## Démarrage

```bash
# Démarrer Docker Desktop, puis :
mvn spring-boot:run
```

Spring Boot démarre le container MySQL automatiquement via Docker Compose. L'API est disponible sur `http://localhost:8080`.

Sur Docker Desktop, tu devrais voir apparaître un container `back_mysql`.

---

## Initialiser la base de données

Au premier démarrage, insère l'utilisateur admin via le container :

```bash
docker exec -it back_mysql mysql -u user_test -ptest_password test
```

Puis dans le prompt MySQL :

```sql
INSERT INTO users(first_name, last_name, admin, email, password)
VALUES ('Admin', 'Admin', true, 'yoga@studio.com',
        '$2a$10$.Hsa/ZjUVaHqi0tp9xieMeewrnZxrZ5pQRzddUXE/WjDu2ZThe6Iq');
```

Compte admin par défaut :

- **Email** : `yoga@studio.com`
- **Mot de passe** : `test!1234`

---

## Tests

Les tests utilisent une base **H2 en mémoire** : Docker n'est **pas nécessaire**.

```bash
# Lancer tous les tests + rapport JaCoCo
mvn test

# Lancer un test spécifique
mvn test -Dtest=SessionServiceTest

# Lancer une classe de test avec un pattern
mvn test -Dtest="*Controller*"
```

### Rapport de couverture JaCoCo

Généré automatiquement lors de `mvn test` :

```
target/site/jacoco/index.html
```

Seuil configuré : **80 %** sur instructions, branches, lignes et méthodes. Le build échoue si le seuil n'est pas atteint.

---

## Collection Postman

Importe la collection :

```
postman/yoga.postman_collection.json
```
