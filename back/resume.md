# Amélioration du découpage des couches (back-end)

## Problème initial

Le code original ne respectait pas la séparation stricte des couches :

- `AuthController` injectait directement `UserRepository` pour vérifier l'existence d'un email et sauvegarder un utilisateur.
- `SessionService` injectait directement `UserRepository` pour récupérer un utilisateur lors des opérations de participation.

## Ce qui a été fait

### 1. `AuthController` → `UserService`

`AuthController` n'appelle plus aucun repository. Les méthodes `findByEmail()`, `existsByEmail()` et `save()` ont été déplacées dans `UserService`, qui devient le seul point d'accès aux données utilisateur pour le controller.

**Avant :**
```java
// AuthController
@Autowired
private UserRepository userRepository;

userRepository.existsByEmail(email);
userRepository.save(user);
userRepository.findByEmail(email);
```

**Après :**
```java
// AuthController
private final UserService userService;

userService.existsByEmail(email);
userService.save(user);
userService.findByEmail(email);
```

### 2. `SessionService` → `UserService`

`SessionService` n'injecte plus `UserRepository` directement. Il délègue désormais la recherche d'un utilisateur à `UserService`, respectant ainsi la règle : un service ne dépend pas du repository d'un autre domaine.

**Avant :**
```java
// SessionService
private final UserRepository userRepository;

User user = this.userRepository.findById(userId).orElse(null);
```

**Après :**
```java
// SessionService
private final UserService userService;

User user = this.userService.findById(userId);
```

## Résultat

Le flux d'appel respecte désormais strictement la séparation en couches :

```
Controller → Service → Repository
```

Aucun controller n'accède à un repository. Aucun service n'accède au repository d'un autre domaine fonctionnel.
