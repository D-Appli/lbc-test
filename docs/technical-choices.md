# Choix techniques

Ce document explique ce qui a été fait sur l'application d'albums, et pourquoi. Le travail a été découpé en 17 pull requests sur [D-Appli/lbc-test](https://github.com/D-Appli/lbc-test/pulls?q=is%3Apr), qui donnent plus de détails sur chaque étape.

## En bref

| Exigence | Réponse |
|---|---|
| Données hors ligne | Room est la source de vérité unique ; le réseau ne fait que la mettre à jour |
| Favoris | Stockés localement dans Room, avec un onglet dédié |
| Écran de détails | Destination Navigation 3, restaurée après l'arrêt du processus |
| Injection de dépendances | Hilt |
| Multi-module | 15 modules Gradle, aucune feature ne dépend d'une autre |
| Changements de configuration | L'état vit dans les ViewModels ; la back stack est sauvegardée |
| Tests | 104 tests unitaires et 21 tests de capture d'écran, exécutés par la CI |

## Architecture

```text
app                    Relie les features : navigation, application Hilt
feature/
  albums | favorites | details
    api                Route uniquement, ce que les autres modules peuvent connaître
    presentation       ViewModel et état de l'UI, sans UI Compose
    ui                 Écrans
libs/albums/ui         AlbumItem, AlbumImage, AlbumLabel, partagés par les features
core/
  domain               Modèles, interface du repository, use cases (Kotlin pur)
  data                 Repository, Retrofit, DTOs
  database             Room
  analytics            AnalyticsHelper, accessible depuis n'importe quelle feature
```

Les règles derrière cette organisation :

- **Les dépendances pointent vers `core:domain`.** C'est un module Kotlin pur, donc la logique métier ne dépend pas d'Android.
- **Les features ne se connaissent pas.** Une feature expose sa route et reçoit des callbacks, comme `onItemSelected`. Seul `app` les connecte.
- **`presentation` et `ui` sont séparés**, pour qu'un ViewModel ne puisse pas accéder à l'UI Compose par accident.
- **Les DTOs restent internes à `core:data`.** Leurs champs sont nullables, alors que `Album` et `AlbumEntity` sont toujours valides.

## Patterns

| Pattern | Où | Pourquoi |
|---|---|---|
| MVVM avec un `StateFlow` par écran | ViewModels | Source de vérité unique pour l'UI, qui survit à la rotation |
| Offline-first | `AlbumRepositoryImpl` | L'UI ne lit que Room, donc en ligne et hors ligne suivent le même chemin |
| Use cases | `core:domain` | Points d'entrée clairs, simples à simuler dans les tests |
| Composables `*Content` sans état | Écrans | Les previews et les tests de capture d'écran n'ont pas besoin de ViewModel |
| Interface du repository dans le domaine | `core:domain`, liée avec Hilt | Les couches supérieures ne connaissent ni Retrofit ni Room |

**MVI a été essayé puis abandonné (PR #7, non fusionnée).** Le reducer donnait un flux propre, mais il était lourd pour des écrans qui affichent surtout une liste. L'application a gardé l'essentiel : un état exposé à un seul endroit et mis à jour uniquement par le ViewModel.

## Librairies

| Librairie | Pourquoi |
|---|---|
| Kotlin 2.4, Compose, Spark | Spark était déjà dans le projet et c'est le design system de l'application de l'exercice |
| Hilt | Standard sur Android, vérifications à la compilation, support des ViewModels ; remplace les conteneurs manuels |
| Room | Requêtes observables et intégration directe avec Paging |
| Paging 3 | L'API envoie tout d'un coup (5000 albums), donc seul le cache est paginé |
| Navigation 3 | La back stack est un simple état détenu par l'application, et les routes sont de simples clés |
| Retrofit, OkHttp, kotlinx.serialization | Stack standard, avec le convertisseur officiel |
| Coil 3 | Déjà dans le projet, pensé pour Compose, cross-fade et caches intégrés |
| Timber | Logs uniquement en debug, et remplace `println` |
| LeakCanary | Builds de debug uniquement |
| MockK, Turbine, Robolectric | Tests de coroutines et de Flow, et tests Room et Compose sans appareil |

Certaines versions sont volontairement bloquées :

- **Le BOM Compose reste en 2025.09.00.** Les BOM plus récents embarquent un Material 3 qui a retiré une API utilisée par Spark 1.4, ce qui fait planter `SparkTheme` au démarrage. Ils doivent être mis à jour ensemble.
- **Paging reste en 3.3.6.** `paging-compose` 3.4 a besoin de Compose 1.10, pour la même raison.
- **AGP reste en 9.4.x**, la dernière version prise en charge par la version actuelle d'Android Studio.

## Pièges trouvés et corrigés

| Problème d'origine | Effet | Correction |
|---|---|---|
| Le projet ne compilait pas | Aucun build possible | Versions de `compileSdk` et de Gradle, ajout de la CI |
| `GlobalScope` dans le ViewModel | Travail jamais annulé, ViewModel maintenu en vie | `viewModelScope` |
| `catch (Exception)` | `CancellationException` avalée | Relancée, ou gérée avec le `catch` de Flow |
| `MutableSharedFlow` sans replay | Résultats perdus, et nouvelle requête à chaque rotation | L'état survit dans le ViewModel |
| `AnalyticsHelper` gardait une Activity | Fuite mémoire | Plus de contexte dans le helper |
| `if (!BuildConfig.DEBUG)` sur les logs HTTP | Corps complets loggés en **release** uniquement | Debug uniquement, en-têtes uniquement |
| Deux entrées de lancement | Une seconde icône ouvrait les détails | Une seule entrée, puis `DetailsActivity` supprimée |
| `DetailsActivity` exportée | D'autres applications pouvaient la lancer | Non exportée, puis supprimée |
| Edge-to-edge absent sur les détails | Barre de statut illisible | Géré sur le nouvel écran |
| `modifier` réutilisé sur l'image de `AlbumItem` | Le modifier de l'appelant était appliqué deux fois | Appliqué à la racine uniquement |
| Hauteur de carte fixe | Libellés coupés avec une grande police | La carte s'agrandit et les libellés passent à la ligne |
| Chip `Album #` cliquable sans action | Retour visuel trompeur | Un `AlbumLabel` dédié, qui bloque les clics |
| `coerceInputValues` sans valeurs par défaut | Sans effet : un album invalide faisait échouer toute la liste | Champs de DTO nullables, albums invalides ignorés |
| Ids pouvant être dupliqués | Crash d'une `LazyColumn` avec clés | Dédoublonnés, en gardant le premier |

## Décisions à expliquer

- **Les albums invalides sont ignorés, sans être bloquants.** Un champ manquant n'écarte que cet album. Si *tous* les albums sont invalides, le chargement échoue, pour que le cache et les favoris ne soient pas remplacés par rien. Un JSON malformé fait échouer le chargement et conserve le cache.
- **Le cache n'est écrit que s'il a changé.** `replaceAllIfChanged` s'exécute dans une transaction, donc une réponse identique n'écrit rien et ne recharge pas la liste.
- **Les placeholders de Paging restent activés.** Sans eux, basculer un favori faisait sauter la liste de 31 albums autour de l'album 1440.
- **Être hors ligne n'est pas une erreur quand des albums sont en cache.** La liste reste à l'écran. Un message d'erreur n'apparaît que s'il n'y a rien à afficher.
- **Les connexions refusées sont des erreurs, pas un état « hors ligne ».** `isOfflineError()` est limité à `UnknownHostException` et `NoRouteToHostException`.
- **Albums reste en bas d'une unique back stack**, pour garder son état d'un onglet à l'autre et que l'utilisateur quitte toujours l'application par cet écran.
- **`AlbumLabel` ne consomme que l'appui.** La liste reste défilable depuis ce libellé, et il ne s'annonce pas comme cliquable, ce que ferait un `clickable` vide.
- **Le cœur ne s'anime que lors d'un changement**, pas au premier affichage, pour que revenir sur un album en scrollant ne rejoue pas l'animation.
- **La signature de release utilise la clé de debug**, pour que la CI n'ait besoin d'aucun secret. C'est acceptable pour un test, pas pour une vraie application. Il en va de même pour `mapping.txt`, publié sur un dépôt public.

## Performance

- Seuls 20 albums par page sont lus depuis Room, et les éléments de la liste ont des clés stables.
- `compose_stability.conf` déclare `Album` stable, sans ajouter Compose à `core:domain`. Les rapports du compilateur le confirment.
- Un seul `ImageLoader` Coil est partagé par l'application, avec un cache disque de 100 Mo et un client qui réutilise les connexions du client principal.
- R8 et la réduction des ressources font passer l'APK de release de 16,2 Mo à 2,0 Mo. Aucune règle keep personnalisée n'a été nécessaire.

## Tests

- **Unitaires :** use cases, repository, ViewModels, navigation.
- **Réseau :** `AlbumRepositoryImplNetworkTest` exécute la vraie configuration Retrofit et répond avec des fichiers JSON locaux, y compris invalides et malformés.
- **Base de données :** `AlbumDaoTest`, avec Robolectric.
- **Compose :** `AlbumItemTest` et `AlbumLabelTest`, avec Robolectric, pour qu'ils tournent sur la CI.
- **Captures d'écran :** composants et écrans, dans `src/screenshotTestDebug/reference`.

Plusieurs tests ont été vérifiés en cassant volontairement le code, pour confirmer qu'ils échouent vraiment. Des vérifications manuelles ont été faites sur un Pixel 7a (API 37), y compris avec un build minifié. Chaque PR indique ce qui a été vérifié et ce qui ne l'a pas été.

```bash
./gradlew assembleDebug testDebugUnitTest :core:domain:test validateDebugScreenshotTest
```

## Outils et méthode

- **Pull requests :** les modifications ont été faites par pull requests, un sujet à la fois.
- **Revue de code :** [CodeRabbit](https://www.coderabbit.ai/) a relu les pull requests, à partir de la PR #3.
- **Assistant IA :** Claude Code a été utilisé avec ces skills :
  - [android/skills](https://github.com/android/skills/)
  - [haidrrrry/compose-kotlin-agent-skills](https://github.com/haidrrrry/compose-kotlin-agent-skills)

## Limites connues

- Un rafraîchissement perd les favoris des albums qu'il ne renvoie plus, et une réponse vide `[]` vide le cache.
- Les échecs sont seulement loggés : un échec de bascule de favori n'affiche rien, et le suivi est encore un `TODO`.
- Après `Unavailable`, le flux paginé est terminé, donc les albums ne reviennent qu'après un redémarrage.
- Il n'y a pas de migration de base de données, car l'application n'est pas en production.
- Le mode sombre, les tablettes et TalkBack n'ont pas été vérifiés.
- Les écrans n'ont pas de test de comportement automatisé, et la navigation n'est testée qu'au niveau de la back stack.
- L'animation de retrait du cœur n'est pas visible dans l'onglet Favoris, où la carte disparaît aussitôt.
- `AlbumLabel` copie l'apparence de `ChipTinted`, donc il ne suivra pas les changements de la chip Spark.
- `MainDispatcherRule` existe en trois copies.
- Les ViewModels n'utilisent pas `SavedStateHandle`, donc leur état est perdu quand le système tue l'application en arrière-plan. Seul impact : un utilisateur au milieu des albums ne retrouve pas sa position de scroll à son retour.
- La couverture de tests n'est pas mesurée.

## Plan d'évolution

**Ensuite : fiabilité**

1. Garder les favoris dans leur propre table, pour qu'un rafraîchissement ne puisse jamais les perdre.
2. Remplacer les `TODO` par un vrai suivi (par exemple des non-fatals Crashlytics), et afficher un message quand une bascule échoue.
3. Mesurer le mapping fait sur le thread principal par `loadAlbums()`. S'il a un impact visible, le déplacer vers un `Dispatchers.Default` injecté dans le repository, pas dans les ViewModels.
4. Ajouter les migrations Room, avant toute vraie release.
5. Restaurer la position de scroll après que le système a tué l'application en arrière-plan, avec `SavedStateHandle`.

**Puis : maintenance**

6. Migrer Spark 1.4 vers 4.x en même temps que le BOM Compose, ce qui débloque aussi Paging 3.4+.
7. Passer les tests de capture d'écran sur les test suites d'AGP, avec AGP 9.5, car le plugin autonome n'est plus maintenu.
8. Ajouter des convention plugins `build-logic` pour la configuration Gradle répétée, et un module de test partagé.
9. Utiliser un vrai keystore depuis les secrets de la CI, et arrêter de publier `mapping.txt`.
10. Mesurer la couverture de tests avec le plugin Kover, et la publier dans la CI.
11. Ajouter des tests d'intégration, pour les écrans et la navigation entre eux.

**Plus tard : produit**

12. Synchronisation en arrière-plan avec WorkManager.
13. Mises en page adaptatives : une vue liste-détail avec les scenes de Navigation 3 sur tablettes et pliables.
14. Baseline Profiles pour le démarrage et le scroll.
15. Recherche et filtres, par album par exemple, dans un nouveau module `feature`.

---

# Technical choices

This document explains what was done on the albums app and why. The work was split into 17 pull requests on [D-Appli/lbc-test](https://github.com/D-Appli/lbc-test/pulls?q=is%3Apr), which hold more detail on each step.

## At a glance

| Requirement | Answer |
|---|---|
| Offline data | Room is the single source of truth; the network only updates it |
| Favorites | Stored locally in Room, with a dedicated tab |
| Details screen | Navigation 3 destination, restored after a process kill |
| Dependency injection | Hilt |
| Multi-module | 15 Gradle modules, no feature depends on another one |
| Configuration changes | State lives in ViewModels; the back stack is saved |
| Tests | 104 unit tests and 21 screenshot tests, run by the CI |

## Architecture

```text
app                    Wires the features: navigation, Hilt application
feature/
  albums | favorites | details
    api                Route only, what other modules may know
    presentation       ViewModel and UI state, no Compose UI
    ui                 Screens
libs/albums/ui         AlbumItem, AlbumImage, AlbumLabel, shared by the features
core/
  domain               Models, repository interface, use cases (pure Kotlin)
  data                 Repository, Retrofit, DTOs
  database             Room
  analytics            AnalyticsHelper, reachable from any feature
```

The rules behind this layout:

- **Dependencies point towards `core:domain`.** It is a pure Kotlin module, so the business logic does not depend on Android.
- **Features do not know each other.** A feature exposes its route and takes callbacks, such as `onItemSelected`. Only `app` connects them.
- **`presentation` and `ui` are separate**, so a ViewModel cannot reach Compose UI by accident.
- **DTOs stay internal to `core:data`.** They have nullable fields, while `Album` and `AlbumEntity` are always valid.

## Patterns

| Pattern | Where | Why |
|---|---|---|
| MVVM with one `StateFlow` per screen | ViewModels | Single source of truth for the UI, survives rotation |
| Offline-first | `AlbumRepositoryImpl` | The UI only reads Room, so online and offline follow the same path |
| Use cases | `core:domain` | Clear entry points, simple to fake in tests |
| Stateless `*Content` composables | Screens | Previews and screenshot tests need no ViewModel |
| Repository interface in the domain | `core:domain`, bound with Hilt | The upper layers do not know Retrofit or Room |

**MVI was tried and dropped (PR #7, not merged).** The reducer gave a clean flow, but it was heavy for screens that mostly show one list. The app kept the part that matters: state exposed in one place and updated by the ViewModel only.

## Libraries

| Library | Why |
|---|---|
| Kotlin 2.4, Compose, Spark | Spark was already in the project and is the design system of the assignment's app |
| Hilt | Standard on Android, compile-time checks, ViewModel support; replaces the manual containers |
| Room | Observable queries, and direct integration with Paging |
| Paging 3 | The API sends everything at once (5000 albums), so only the cache is paged |
| Navigation 3 | The back stack is plain state owned by the app, and routes are simple keys |
| Retrofit, OkHttp, kotlinx.serialization | Standard stack, with the official converter |
| Coil 3 | Already in the project, Compose first, cross-fade and caches built in |
| Timber | Logs in debug builds only, and replaces `println` |
| LeakCanary | Debug builds only |
| MockK, Turbine, Robolectric | Coroutine and Flow tests, and Room and Compose tests without a device |

Some versions are held back on purpose:

- **Compose BOM stays at 2025.09.00.** Newer BOMs ship a Material 3 that removed an API used by Spark 1.4, which crashes `SparkTheme` at startup. They must be upgraded together.
- **Paging stays at 3.3.6.** `paging-compose` 3.4 needs Compose 1.10, for the same reason.
- **AGP stays at 9.4.x**, the latest version the current Android Studio supports.

## Traps found and fixed

| Original problem | Effect | Fix |
|---|---|---|
| Project did not compile | No build at all | `compileSdk` and Gradle versions, CI added |
| `GlobalScope` in the ViewModel | Work never cancelled, ViewModel kept alive | `viewModelScope` |
| `catch (Exception)` | Swallowed `CancellationException` | Rethrown, or handled with the Flow `catch` |
| `MutableSharedFlow` without replay | Lost results, and a new request on each rotation | State survives in the ViewModel |
| `AnalyticsHelper` kept an Activity | Memory leak | No more context in the helper |
| `if (!BuildConfig.DEBUG)` on HTTP logging | Full bodies logged in **release** only | Debug only, headers only |
| Two launcher entries | A second app icon opened the details | One entry, then `DetailsActivity` removed |
| `DetailsActivity` exported | Other apps could start it | Not exported, then removed |
| Edge-to-edge missing on details | Unreadable status bar | Handled on the new screen |
| `modifier` reused on the image of `AlbumItem` | The caller's modifier was applied twice | Applied to the root only |
| Fixed card height | Labels cut off with a large font | The card grows, and labels wrap |
| `Album #` chip clickable with no action | Misleading feedback | A dedicated `AlbumLabel`, which blocks clicks |
| `coerceInputValues` with no default values | No effect: one bad album failed the whole list | Nullable DTO fields, invalid albums dropped |
| Ids that could be duplicated | Crash of a keyed `LazyColumn` | Deduplicated, keeping the first one |

## Decisions worth explaining

- **Invalid albums are dropped, not fatal.** A missing field drops that album only. If *all* the albums are invalid, the load fails, so the cache and the favorites are not replaced with nothing. Malformed JSON fails the load and keeps the cache.
- **The cache is written only if it changed.** `replaceAllIfChanged` runs in a transaction, so an identical response neither writes nor reloads the list.
- **Paging placeholders stay enabled.** Without them, toggling a favorite made the list jump by 31 albums around album 1440.
- **Offline is not an error when albums are cached.** The list stays on screen. An error message only shows when there is nothing to display.
- **Refused connections are errors, not "offline".** `isOfflineError()` is limited to `UnknownHostException` and `NoRouteToHostException`.
- **Albums stays at the bottom of a single back stack**, so it keeps its state across tabs and the user always exits through it.
- **`AlbumLabel` consumes only the press.** A list can still be scrolled from it, and it does not announce itself as clickable, which an empty `clickable` would do.
- **The heart animates on a change only**, not on first display, so scrolling back to an album does not replay it.
- **Release signing uses the debug key**, so the CI needs no secret. This is fine for a test, not for a real app. The same goes for `mapping.txt`, published on a public repository.

## Performance

- Only 20 albums per page are read from Room, and the list items have stable keys.
- `compose_stability.conf` declares `Album` stable, without adding Compose to `core:domain`. Compiler reports confirm it.
- A single Coil `ImageLoader` is shared by the app, with a 100 MB disk cache and a client reusing the connections of the main one.
- R8 and resource shrinking take the release APK from 16.2 MB to 2.0 MB. No custom keep rule was needed.

## Tests

- **Unit:** use cases, repository, ViewModels, navigation.
- **Network:** `AlbumRepositoryImplNetworkTest` runs the real Retrofit setup and answers with local JSON files, including invalid and malformed ones.
- **Database:** `AlbumDaoTest`, with Robolectric.
- **Compose:** `AlbumItemTest` and `AlbumLabelTest`, with Robolectric, so that they run on the CI.
- **Screenshots:** components and screens, in `src/screenshotTestDebug/reference`.

Several tests were checked by breaking the code on purpose, to confirm they really fail. Manual checks were done on a Pixel 7a (API 37), including a minified build. Each PR lists what was and was not checked.

```bash
./gradlew assembleDebug testDebugUnitTest :core:domain:test validateDebugScreenshotTest
```

## Tools and process

- **Pull requests:** the changes were made through pull requests, one topic at a time.
- **Code review:** [CodeRabbit](https://www.coderabbit.ai/) reviewed the pull requests, from PR #3 on.
- **AI assistant:** Claude Code was used with these skills:
  - [android/skills](https://github.com/android/skills/)
  - [haidrrrry/compose-kotlin-agent-skills](https://github.com/haidrrrry/compose-kotlin-agent-skills)

## Known limits

- A refresh loses the favorites of the albums it no longer returns, and an empty `[]` response empties the cache.
- Failures are only logged: a failed favorite toggle shows nothing, and tracking is still a `TODO`.
- After `Unavailable`, the paged flow is finished, so the albums only come back with a restart.
- There is no database migration, as the app is not in production.
- Dark mode, tablets and TalkBack were not checked.
- Screens have no automated behaviour test, and navigation is only tested at the back stack level.
- The removal animation of the heart is not visible in the Favorites tab, where the card fades out at once.
- `AlbumLabel` copies the look of `ChipTinted`, so it will not follow changes to the Spark chip.
- `MainDispatcherRule` has three copies.
- ViewModels do not use `SavedStateHandle`, so their state is lost when the system kills the app in the background. The only impact: a user in the middle of the albums does not get the scroll position back on return.
- Test coverage is not measured.

## Evolution plan

**Next: reliability**

1. Keep favorites in their own table, so that a refresh can never lose them.
2. Replace the `TODO`s with real tracking (e.g. Crashlytics non-fatals), and show a message when a toggle fails.
3. Measure the mapping done on the main thread by `loadAlbums()`. If it is visible, move it to an injected `Dispatchers.Default` in the repository, not to the ViewModels.
4. Add Room migrations, before any real release.
5. Restore the scroll position after the system kills the app in the background, with `SavedStateHandle`.

**Then: maintenance**

6. Migrate Spark 1.4 to 4.x together with the Compose BOM, which also unlocks Paging 3.4+.
7. Move screenshot tests to AGP test suites, with AGP 9.5, as the standalone plugin is no longer maintained.
8. Add `build-logic` convention plugins for the repeated Gradle setup, and a shared test module.
9. Use a real keystore from CI secrets, and stop publishing `mapping.txt`.
10. Measure test coverage with the Kover plugin, and report it in the CI.
11. Add integration tests, for the screens and the navigation between them.

**Later: product**

12. Background sync with WorkManager.
13. Adaptive layouts: a list-detail view with Navigation 3 scenes on tablets and foldables.
14. Baseline Profiles for startup and scrolling.
15. Search and filters, by album for example, as a new `feature` module.
