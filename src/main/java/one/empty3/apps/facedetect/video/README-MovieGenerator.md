# MovieGenerator - Générateur de vidéos à partir d'animations définies en JSON

## Description

La classe `MovieGenerator` permet de générer des vidéos à partir d'animations définies dans un fichier JSON. Elle prend en charge diverses transformations comme le morphing, la rotation, la translation, le changement d'échelle et l'attachement/détachement d'images.

## Fonctionnalités

- Lecture de configurations d'animation à partir de fichiers JSON
- Support de différents types de transformations :
  - Morphing entre deux groupes de points
  - Translation de points
  - Rotation de points
  - Mise à l'échelle de points
  - Attachement d'images à des groupes
  - Détachement d'images
  - Contrôle de la visibilité
- Génération de vidéos MP4 via JCodec

## Format du fichier JSON

Le fichier JSON d'animation doit définir :

- **points** : Liste des points avec leurs coordonnées et propriétés
- **groups** : Groupes de points qui peuvent être manipulés ensemble
- **transforms** : Liste des transformations à appliquer
- **animation** : Données d'animation frame par frame (optionnel)

Exemple simplifié :

```json
{
  "points": [
    { "id": "p1", "name": "Point A", "x": 0.2, "y": 0.2, "color": "#FF6347", "visible": true },
    { "id": "p2", "name": "Point B", "x": 0.4, "y": 0.3, "color": "#4682B4", "visible": true }
  ],
  "groups": [
    { "id": "g1", "name": "Group 1", "pointIds": ["p1", "p2"] }
  ],
  "transforms": [
    { "id": "t1", "type": "translate", "frames": 30, "targetId": "g1", "targetType": "Group", "dx": 0.1, "dy": 0.1 }
  ]
}
```

## Utilisation

```java
// Créer une liste de fichiers à traiter
List<FileType> fileTypes = new ArrayList<>();
fileTypes.add(new FileType("animation.json", "json"));

// Définir le fichier de sortie
File outputFile = new File("output.mp4");

// Créer et exécuter le générateur
MovieGenerator generator = new MovieGenerator(fileTypes, outputFile);
boolean success = generator.generateMovie();
```

## Améliorations récentes

1. **Refactorisation du code** : Organisation plus claire des méthodes
2. **Extraction des traitements** : Séparation des transformations en méthodes dédiées
3. **Amélioration de la gestion d'erreurs** : Messages d'erreur plus détaillés et journalisation améliorée
4. **Documentation** : Ajout de commentaires JavaDoc pour améliorer la compréhension du code
5. **Optimisation des performances** : Réduction des allocations mémoire inutiles

## Dépendances

- JCodec : Pour l'encodage vidéo
- ImageIO : Pour le traitement des images
- Empty3 : Bibliothèque de traitement 3D pour certaines transformations

## Limitations connues

- Certaines transformations complexes peuvent nécessiter des ressources importantes
- La qualité de la vidéo dépend de la résolution des images générées (définie par RES_AVG)
- Les animations complexes avec de nombreuses images peuvent être lentes à générer
