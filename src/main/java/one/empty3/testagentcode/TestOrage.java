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

import one.empty3.library.core.tribase.Tubulaire4;
import one.empty3.libs.Color;
import one.empty3.library.*;
import one.empty3.apps.testobject.*;

import java.io.File;
import java.util.Random;

public class TestOrage extends TestObjetSub {
    private Random random = new Random();
    private Point3D[] lightningPoints;
    private int lightningTimer = 0;

    @Override
    public void ginit() {
        // Initialisation de la scène
        scene().clear();

        // Caméra orientée vers le centre, avec le vecteur haut (Point3D.Y) pour éviter la matrice dégénérée
        Camera camera = new Camera(new Point3D(0.0, 0.0, 15.0), new Point3D(0.0, 0.0, 0.0), Point3D.Y);
        scene().cameraActive(camera);

        // Générer la pluie statique/dynamique en tâche de fond
        for (int i = 0; i < 150; i++) {
            double x = (random.nextDouble() - 0.5) * 20.0;
            double y = (random.nextDouble() - 0.5) * 20.0;
            double z = (random.nextDouble() - 0.5) * 10.0;

            // Une goutte de pluie est représentée par un petit segment vertical légèrement incliné
            LineSegment drop = new LineSegment(
                    new Point3D(x, y, z),
                    new Point3D(x - 0.1, y - 0.8, z)
            );
            drop.texture(new ColorTexture(Color.newCol(0.6f, 0.7f, 0.8f))); // Couleur bleuâtre translucide
            scene().add(drop);
        }
    }

    @Override
    public void finit() {
        // Cette méthode est appelée à chaque image (frame) de l'animation
        // On va animer l'apparition aléatoire des éclairs

        // Nettoyer les anciens éclairs de l'image précédente
        scene().getObjets().getData1d().removeIf(obj -> obj instanceof Tubulaire4 || obj instanceof LineSegment && ((LineSegment) obj).texture() instanceof ColorTexture && ((ColorTexture) ((LineSegment) obj).texture()).getColor().equals(Color.newCol(1.0f, 1.0f, 1.0f)));

        lightningTimer--;

        if (lightningTimer <= 0) {
            // Chance de déclencher un nouvel éclair
            if (random.nextDouble() < 0.3) {
                // Création d'un éclair (ligne brisée descendant du ciel)
                double startX = (random.nextDouble() - 0.5) * 10.0;
                double startY = 8.0;
                double startZ = (random.nextDouble() - 0.5) * 5.0;

                Point3D current = new Point3D(startX, startY, startZ);

                // On génère 6 à 10 segments pour l'éclair
                int segments = 6 + random.nextInt(5);
                for (int i = 0; i < segments; i++) {
                    double nextX = current.getX() + (random.nextDouble() - 0.5) * 3.0;
                    double nextY = current.getY() - (1.5 + random.nextDouble() * 1.5);
                    double nextZ = current.getZ() + (random.nextDouble() - 0.5) * 1.5;

                    Point3D next = new Point3D(nextX, nextY, nextZ);

                    // On crée un cylindre fin (Tubulaire3) ou un segment épais pour l'éclair lumineux
                    LineSegment lightningSegment = new LineSegment(current, next);
                    lightningSegment.texture(new ColorTexture(Color.newCol(1.0f, 1.0f, 1.0f))); // Blanc pur brillant

                    scene().add(lightningSegment);

                    current = next;
                }

                // L'éclair reste visible pendant 1 à 3 frames
                lightningTimer = 1 + random.nextInt(3);

                // Changement temporaire de la couleur de fond pour simuler le flash
                scene().texture(new ColorTexture(Color.newCol(0.2f, 0.2f, 0.3f)));
            } else {
                // Ciel d'orage sombre par défaut
                scene().texture(new ColorTexture(Color.newCol(0.05f, 0.05f, 0.08f)));
            }
        }

        // Faire tomber la pluie légèrement à chaque frame
        for (Representable obj : scene().getObjets().getData1d()) {
            if (obj instanceof LineSegment && !obj.texture().toString().contains("255")) { // si ce n'est pas l'éclair blanc
                LineSegment drop = (LineSegment) obj;
                // Déplacement vers le bas
                drop.getOrigine().setY(drop.getOrigine().getY() - 0.5);
                drop.getExtremite().setY(drop.getExtremite().getY() - 0.5);

                // Réinitialiser la goutte en haut si elle sort de l'écran
                if (drop.getOrigine().getY() < -10.0) {
                    double x = (random.nextDouble() - 0.5) * 20.0;
                    double y = 10.0;
                    double z = (random.nextDouble() - 0.5) * 10.0;
                    drop.getOrigine().setX(x);
                    drop.getOrigine().setY(y);
                    drop.getOrigine().setZ(z);
                    drop.getExtremite().setX(x - 0.1);
                    drop.getExtremite().setY(y - 0.8);
                    drop.getExtremite().setZ(z);
                }
            }
        }
    }

    public static void main(String[] args) {
        TestOrage orage = new TestOrage();
        orage.setPublish(true);
        orage.setMaxFrames(100); // Génère une animation de 100 images
        new Thread(orage).start();
    }
}