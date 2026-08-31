/*
 *
 *  *
 *  *  * Copyright (c) 2026. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2026 Manuel Daniel Dahmen
 *  *  *
 *  *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *    you may not use this file except in compliance with the License.
 *  *  *    You may obtain a copy of the License at
 *  *  *
 *  *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  *    Unless required by applicable law or agreed to in writing, software
 *  *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *    See the License for the specific language governing permissions and
 *  *  *    limitations under the License.
 *  *
 *  *
 *
 *
 *
 *  * Created by $user $date
 *
 *
 */

package one.empty3.testagentcode;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.*;
import one.empty3.libs.Color;
import one.empty3.library.Point3D;

/**
 * Modélisation d'une abeille avec la bibliothèque Empty3.
 */
public class BeeAnimation extends TestObjetSub {

    @Override
    public void ginit() {
        frame = 130;
        // Effacer la scène précédente
        scene().clear();

        // Couleurs
        Color yellow = Color.newCol(1.0f, 0.9f, 0.0f);
        Color black = Color.newCol(0.05f, 0.05f, 0.05f);
        Color wingColor = Color.newCol(0.8f, 0.8f, 1.0f); // Légèrement bleuté et clair

        // --- Abdomen (Modélisé par des segments pour créer les rayures) ---
        for (int i = 0; i < 7; i++) {
            double zPos = -0.4 - (i * 0.35);
            // On réduit le rayon vers le dard
            double radius = 0.6 * (1.0 - (i * 0.12));
            Sphere segment = new Sphere(new Point3D(0.0, 0.0, zPos), radius);
            // Alternance noir et jaune
            segment.texture(new ColorTexture(i % 2 == 0 ? black : yellow));
            scene().add(segment);
        }

        // --- Thorax (Partie centrale) ---
        Sphere thorax = new Sphere(new Point3D(0.0, 0.0, 0.2), 0.7);
        thorax.texture(new ColorTexture(black));
        scene().add(thorax);

        // --- Tête ---
        Sphere head = new Sphere(new Point3D(0.0, 0.0, 1.0), 0.45);
        head.texture(new ColorTexture(black));
        scene().add(head);

        // --- Yeux ---
        Sphere leftEye = new Sphere(new Point3D(0.25, 0.25, 1.3), 0.18);
        leftEye.texture(new ColorTexture(Color.newCol(0.1f, 0.1f, 0.2f)));
        scene().add(leftEye);

        Sphere rightEye = new Sphere(new Point3D(-0.25, 0.25, 1.3), 0.18);
        rightEye.texture(new ColorTexture(Color.newCol(0.1f, 0.1f, 0.2f)));
        scene().add(rightEye);

        // --- Ailes (Utilisation de sphères déformées en ellipsoïdes plats) ---
        // Ailes droites
        addWing(0.6, 0.5, 0.3, 1.4, 0.7, true);   // Aile avant
        addWing(0.5, 0.4, -0.1, 1.1, 0.5, true);  // Aile arrière

        // Ailes gauches
        addWing(-0.6, 0.5, 0.3, 1.4, 0.7, false);
        addWing(-0.5, 0.4, -0.1, 1.1, 0.5, false);

        // --- Caméra (Configuration avec vecteur "haut" pour éviter la matrice nulle) ---
        Point3D eye = new Point3D(5.0, 4.0, 5.0);
        Point3D lookAt = Point3D.O0; // Vise l'origine
        scene().cameraActive(new Camera(eye, lookAt, Point3D.Y));
    }

    /**
     * Ajoute une aile à la scène en utilisant une sphère étirée.
     */
    private void addWing(double x, double y, double z, double length, double width, boolean isRight) {
        Sphere wing = new Sphere(new Point3D(x, y, z), 0.1);
        // On étire la sphère sur l'axe X pour la longueur et Z pour la largeur, Y reste très fin
        wing.setVectX(new Point3D(isRight ? length : -length, 0.0, 0.0));
        wing.setVectY(new Point3D(0.0, 0.02, 0.0)); // Épaisseur très fine
        wing.setVectZ(new Point3D(0.0, 0.0, width));

        wing.texture(new ColorTexture(Color.newCol(0.9f, 0.9f, 1.0f)));
        scene().add(wing);
    }

    @Override
    public void finit() {
        // Animation : rotation de la caméra autour de l'abeille
        double angle = frame * 0.05;
        double distance = 7.0;
        double x = distance * Math.cos(angle);
        double z = distance * Math.sin(angle);

        scene().cameraActive().setEye(new Point3D(x, 3.0, z));
    }

    public static void main(String[] args) {
        BeeAnimation bee = new BeeAnimation();
        bee.loop(true);           // Activer le rendu en boucle
        bee.setMaxFrames(300);    // Durée de l'animation
        new Thread(bee).start();  // Lancer le rendu
    }
}