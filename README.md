# SpotView

**Application Android de visite virtuelle : reconstituer un lieu en 3D simplifiée à partir de photos prises sur place.**

---

## Le concept

Prendre des photos d'une pièce dans quatre directions cardinales (Nord, Sud, Est, Ouest), les relier par des accès, et se déplacer virtuellement de pièce en pièce comme si on y était.

Ce n'est pas une reconstruction 3D : c'est un **assemblage d'images orientées**, reliées par des passages. Une approche légère, qui tient sur un téléphone et ne demande aucun serveur.

## Fonctionnalités

- **Créer, supprimer et sauvegarder** un modèle de lieu.
- **Créer des pièces** virtuelles, chacune composée de quatre images prises dans les quatre orientations.
- **Définir des accès** sur ces images, en dessinant la zone de passage et en indiquant vers quelle pièce elle mène.
- **Se déplacer** dans le modèle chargé, en empruntant les accès d'une pièce à l'autre.
- **Orientation assistée** par le capteur boussole du téléphone, pour savoir dans quelle direction la photo a été prise.

## Architecture

```
app/src/main/java/fr/ul/spotview/
├── donnees/                  Modèle de données
│   ├── Modele.java           Un lieu : un ensemble de pièces
│   ├── Piece.java            Une pièce : quatre photos orientées
│   ├── Photo.java            Une image et son orientation
│   └── Acces.java            Une zone de passage vers une autre pièce
├── MainActivity.java         Point d'entrée, menu principal
├── SelectionModele.java      Chargement d'un modèle existant
├── GestionModele.java        Création et suppression de modèles
├── GestionPieces.java        Gestion des pièces d'un modèle
├── GestionPhotosPiece.java   Les quatre photos d'une pièce
├── GestionPhoto.java         Prise de vue et orientation
├── Visualisation.java        Navigation dans le modèle
├── DrawingView.java          Dessin des accès sur une image
└── SingletonOpenFolder.java  Accès persistant au dossier de sauvegarde
```

Sept activités, un modèle de données séparé de l'interface, et une vue personnalisée pour le tracé des accès.

## Stack

| Élément | Choix |
|---|---|
| Langage | Java 11 |
| Plateforme | Android (SDK 24 minimum, compilation en API 34) |
| Interface | AppCompat, ConstraintLayout, Material 3 |
| Capteurs | `SensorManager` — boussole |
| Appareil photo | `ActivityResultContracts`, `FileProvider` |
| Stockage | `DocumentFile` (Storage Access Framework) |
| Sérialisation | Gson |
| Build | Gradle 8, Android Gradle Plugin 8.1 |

## Compiler le projet

Prérequis : **JDK 17**, Android SDK (API 34), Gradle 8 (ou le wrapper).

```bash
git clone https://github.com/BooFenyx/SpotView.git
cd SpotView
./gradlew assembleDebug
```

L'APK est produit dans `app/build/outputs/apk/debug/`.

Pour installer directement sur un appareil connecté :

```bash
./gradlew installDebug
```

## Ce qu'il faut savoir sur ce dépôt

Ce projet a été développé dans le cadre du module **Développement d'Application Mobile** du Master Informatique à l'Université de Lorraine. Le document de conception complet est inclus : [`app/Conception_SpotView.pdf`](app/Conception_SpotView.pdf).

**Les fichiers de build Gradle ont été reconstitués après coup.** Le dépôt d'origine ne contenait que les sources et les ressources ; `build.gradle`, `settings.gradle` et `gradle.properties` ont été réécrits à partir des dépendances réellement importées dans le code. Ils n'ont pas pu être testés sur une machine disposant du SDK Android complet, et peuvent nécessiter un ajustement de version.

## Auteur

Mathys Bertrand — [@BooFenyx](https://github.com/BooFenyx)
