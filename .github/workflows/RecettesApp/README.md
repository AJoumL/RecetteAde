
# RecettesApp (Android)

Application Android (Jetpack Compose) qui charge vos recettes depuis `app/src/main/assets/recettes_fusionnees.json`, permet d'afficher chaque recette (photo, ingrédients, étapes), d'ajuster le nombre de portions et de générer une **liste de courses** multi-recettes avec quantités agrégées.

## Pré-requis
- Android Studio Jellyfish (ou plus récent)
- JDK 17 (installé avec Android Studio)

## Lancer l'application
1. Ouvrez Android Studio → *Open an Existing Project* → sélectionnez le dossier `RecettesApp`.
2. Laissez Gradle télécharger les dépendances.
3. Lancez l'exécution sur un smartphone Android ou un émulateur.

## Intégrer vos photos de recettes
- Déposez vos images dans `app/src/main/assets/images/`.
- Nommage attendu : à partir du **titre** de la recette, en minuscules, sans accents ni caractères spéciaux, remplacés par `_`.
  - Exemple : `"Poulet Grillé au Balsamique avec Mozzarella et Tomate"` → `poulet_grille_au_balsamique_avec_mozzarella_et_tomate.jpg`
- Formats acceptés : `.jpg`, `.jpeg`, `.png`, `.webp`.
- Si aucune image correspondante n'est trouvée, un **placeholder** est affiché.

## Changer le nombre de personnes
- Sur l'écran d'une recette, modifiez la valeur de **Portions**. Les quantités d'ingrédients sont recalculées en direct (quantité et champ `grams` quand il est fourni).
- Bouton **Ajouter à la liste de courses** : ajoute la recette courante avec le nombre de portions choisi.

## Liste de courses
- Accessible depuis la page d'accueil (bouton *Liste de courses*) ou après ajout depuis une fiche recette.
- Les ingrédients sont **agrégés** par *nom + unité*. Si une unité est absente mais `grams` est présent dans le JSON, la quantité en grammes est utilisée.

## Personnalisation rapide
- Nom du package : `com.example.recettes` (modifiez-le si besoin dans les fichiers Gradle & AndroidManifest).
- Thème : Material 3 (clair). Ajustez `values/colors.xml` et `values/themes.xml`.

---
*Projet généré automatiquement par M365 Copilot.*
