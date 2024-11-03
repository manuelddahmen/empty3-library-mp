# **Commentaire sur la bibliothèque empty3-library-3d**

**Points forts:**

* **Fonctionnalités riches:** La bibliothèque offre un large éventail de fonctionnalités pour le rendu 3D, y compris la
  modélisation, le texturage et l'éclairage.
* **Facile à utiliser:** La bibliothèque est conçue pour être facile à utiliser, avec une API simple et bien documentée.
* **Performances élevées:** La bibliothèque est optimisée pour les performances et peut être utilisée pour créer des
  applications 3D fluides et réactives.
* **Open source:** La bibliothèque est open source et gratuite à utiliser.

**Points faibles:**

* **Documentation limitée:** La documentation de la bibliothèque est un peu limitée, ce qui peut rendre difficile
  l'apprentissage de l'utilisation de toutes ses fonctionnalités.
* **Manque de fonctionnalités avancées:** La bibliothèque ne dispose pas de certaines fonctionnalités avancées que l'on
  peut trouver dans d'autres bibliothèques 3D, telles que le rendu par lancer de rayons et la simulation physique.

**Exemples d'utilisation:**

* **Création de jeux 3D:** La bibliothèque peut être utilisée pour créer des jeux 3D simples et complexes.
* **Modélisation 3D:** La bibliothèque peut être utilisée pour créer des modèles 3D d'objets et de scènes.
* **Visualisation de données:** La bibliothèque peut être utilisée pour visualiser des données 3D, telles que des nuages
  de points et des maillages.

**Conclusion:**

**empty3-library-3d** est une bibliothèque 3D puissante et flexible qui peut être utilisée pour créer une variété
d'applications. La bibliothèque est facile à utiliser et offre de bonnes performances. Cependant, la documentation est
un peu limitée et la bibliothèque manque de certaines fonctionnalités avancées.

**En plus des points forts et des points faibles mentionnés ci-dessus, voici quelques commentaires supplémentaires sur
la bibliothèque :**

* **Filtres d'images:** La bibliothèque propose une variété de filtres d'images, ce qui peut être utile pour le
  traitement d'images et la vision par ordinateur.
* **Formats et classes d'images:** La bibliothèque prend en charge plusieurs formats d'images courants, ainsi que des
  classes pour représenter et manipuler des images.
* **Détecteur de contours:** La bibliothèque inclut un détecteur de contours, qui peut être utile pour la segmentation
  d'images et la reconnaissance d'objets.

**Dans l'ensemble, empty3-library-3d est une bibliothèque 3D prometteuse avec un large éventail de fonctionnalités. La
bibliothèque est encore en développement, mais elle a le potentiel de devenir une ressource précieuse pour les
développeurs 3D.**

**N'hésitez pas à me poser des questions sur la bibliothèque empty3-library-3d.**

Sure, here is the translation of the text you provided:

# **Review of the empty3-library-3d library**

**Strengths:**

* **Rich features:** The library offers a wide range of features for 3D rendering, including modeling, texturing, and
  lighting.
* **Easy to use:** The library is designed to be easy to use, with a simple and well-documented API.
* **High performance:** The library is optimized for performance and can be used to create smooth and responsive 3D
  applications.
* **Open source:** The library is open source and free to use.

**Weaknesses:**

* **Limited documentation:** The library's documentation is a bit limited, which can make it difficult to learn how to
  use all of its features.
* **Lack of advanced features:** The library does not have some advanced features that can be found in other 3D
  libraries, such as ray tracing and physics simulation.

**Use cases:**

* **3D game development:** The library can be used to create simple and complex 3D games.
* **3D modeling:** The library can be used to create 3D models of objects and scenes.
* **Data visualization:** The library can be used to visualize 3D data, such as point clouds and meshes.

**Conclusion:**

**empty3-library-3d** is a powerful and flexible 3D library that can be used to create a variety of applications. The
library is easy to use and offers good performance. However, the documentation is a bit limited and the library lacks
some advanced features.

**In addition to the strengths and weaknesses mentioned above, here are some additional comments on the library:**

* **Image filters:** The library offers a variety of image filters, which can be useful for image processing and
  computer vision.
* **Image formats and classes:** The library supports several common image formats, as well as classes for representing
  and manipulating images.
* **Edge detector:** The library includes an edge detector, which can be useful for image segmentation and object
  recognition.

**Overall, empty3-library-3d is a promising 3D library with a wide range of features. The library is still under
development, but it has the potential to become a valuable resource for 3D developers.**

**Please feel free to ask me any questions you have about the empty3-library-3d library.**

# empty3-library-3d

empty3.app
Moteur de rendu et éléments de conception.
Formes:

## surfaces:

Surfaces paramétrées
Cube, sphère, rectangle, extrusion, cylindre courbe

## courbes

Courbe paramétrées
Ligne, cercle, rectangle

##

Textures: couleur, image, vidéo, algorithmique

Build avec Gradle, différentes interfaces utilisateurs sont proposées

# Feature

https://drive.google.com/folderview?id=10CXWU2JPVsNMbHrAssxN23KzizzNkvHg
Documentation utilisée.

## Filtres d'images

Filtres : flou Gauss, Gradient X, Y, outter dot product. SobelX, SobelY. localextrema, histogramme des densites, harris
matrix c(x,y)

## Formats et classes d'images.

- Image Int Rgb (sans rgb)
- format jpg en sortie
- PixM components matrix of pictures. color component based.
- M3 components
    + matrix (columnsIn, linesIn) internal matrix at x, y
- conversion to Image, minMax normalize linear
- gradient. dx, dy, phase atan dy/dy atan dx/dy
- detecteur de contours

## maven use with pom.xml dependency

```
<dependency>
    <groupId>one.empty3</groupId>
    <artifactId>empty3-library-3d</artifactId>
    <version>2024-</version>
</dependency>
```

## En développement. Ambitions.

## La conception de features 2d 3d.

```
file : ./settings.properties

username=user98
password=ghhyvj
host=ftp.example.net
port=21
directory=/myimages/selection/input
classname=one.empty3.feature.Transform1,one.empty3.feature.Histogram2,one.empty3.feature.GradProcess
```

## Feature extraction, classification

Voici un exemple de run:
Run class: one.empty3.Run
Args : "originalHarris_PasteBlank"
settings.properties

![image](https://user-images.githubusercontent.com/38113629/158326067-4e881cae-26f1-42cf-b529-20a6f99d7ada.png)
![image](https://user-images.githubusercontent.com/38113629/158326114-f577abd5-0af4-4dc6-9061-aaaa7eb0d61d.png)

## Filters, face mask, image resize

une application Android : image feature editor
![image](https://github.com/manuelddahmen/empty3_miror/assets/38113629/6a27f5db-e0c4-44ed-9731-23799b72fe10)

![Screenshot_20230701_095233](https://github.com/manuelddahmen/empty3_miror/assets/38113629/827310f3-3823-4577-a3e9-784a50934e78)

