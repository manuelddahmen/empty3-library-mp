# **Guide utilisateur - empty3-library-mp**

Bienvenue dans le guide officiel de la bibliothèque **empty3-library-mp**. Ce document couvre l'utilisation de la bibliothèque, la création de scènes 3D, le rendu et les bonnes pratiques d'optimisation.

---

## **1. Introduction**
`empty3-library-mp` est une bibliothèque Java / Kotlin open-source dédiée au rendu 3D, à la modélisation paramétrique, à la visualisation de données et au développement d'applications multimédias et de jeux 3D.

---

## **2. Installation et Exécution des Exemples**

### **Prérequis**
- Java SDK 25 (ou compatible)
- Gradle (fourni via le wrapper `./gradlew`)

### **Lancer une application / exemple 3D**
Pour lancer une application de test ou de rendu (par exemple, `FaceDetectApp` ou des scènes de test 3D), utilisez Gradle :
```bash
./gradlew run
```

---

## **3. Concepts Clés**

### **Scène (`Scene`)**
La classe `Scene` est le conteneur principal de votre univers 3D. Elle regroupe :
- Les objets 3D (`Representable`)
- La caméra active (`Camera`)
- Les sources lumineuses (`Lumiere`)
- Les animations et textures

Exemple de création de scène :
```java
Scene scene = new Scene();
Camera camera = new Camera(Point3D.Z0.mult(-10), Point3D.O0, Point3D.Y0);
scene.cameraActive(camera);

Sphere sphere = new Sphere(Point3D.O0, 2.0);
sphere.texture(new ColorTexture(Color.RED));
scene.add(sphere);
```

### **Objets Représentables (`Representable`)**
Tous les objets géométriques héritent de `Representable` :
- **Formes de base :** `Sphere`, `Cube`, `Cylinder`, `Plane`, `Tetraedre`
- **Surfaces paramétriques :** `SurfaceBezier`, `ParametricSurface`, `Tubulaire`
- **Conteneurs :** `RepresentableConteneur`

### **Rendu (`ZBuffer`)**
Le rendu s'effectue via un `ZBuffer` (par exemple `ZBufferImpl` ou `ZBuffer3D`) :
```java
ZBuffer zBuffer = ZBufferFactory.instance(800, 600);
zBuffer.camera(scene.cameraActive());
zBuffer.draw();
```

---

## **4. Optimisation des Performances (Point 2)**

Pour maximiser les performances de rendu :
1. **Résolution des polygones :** Ajustez les incréments (`incrU`, `incrV`) des surfaces paramétriques pour éviter le sur-échantillonnage.
2. **Parallélisation :** Utilisez le traitement parallèle (`parallelStream`) pour le calcul des objets de la scène.
3. **Allocation mémoire :** Minimisez l'instanciation inutile de vecteurs et matrices dans les boucles de rendu critiques.
