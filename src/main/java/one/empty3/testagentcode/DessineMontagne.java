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

import one.empty3.library.*;
import one.empty3.library.core.nurbs.ParametricSurface;
import one.empty3.libs.*;

import java.io.File;

public class DessineMontagne {
    public static void main(String[] args) {
        // 1. Création de la scène (Règle 1)
        Scene scene = new Scene();

        // 2. Définition de la montagne comme une surface paramétrique
        ParametricSurface montagne = new ParametricSurface() {
            @Override
            public Point3D calculerPoint3D(double u, double v) {
                // u et v varient de 0.0 à 1.0
                double x = (u - 0.5) * 40.0;
                double z = (v - 0.5) * 40.0;

                // Calcul de la distance au centre pour créer une forme de pic
                double dist = Math.sqrt(x * x + z * z);

                // Forme de base (cloche)
                double hauteur = 15.0 * Math.exp(-dist / 8.0);

                // Ajout de "bruit" pour le relief rocheux
                hauteur += 1.5 * Math.sin(x * 0.8) * Math.cos(z * 0.7);
                hauteur += 0.8 * Math.sin(x * 2.5) * Math.cos(z * 2.2);

                // On retourne le point (X, Hauteur, Z)
                return new Point3D(x, hauteur, z);
            }
        };

        // Paramètres de maillage de la surface
        montagne.setIncrU(0.01);
        montagne.setIncrV(0.01);

        // Application d'une texture de couleur (Brun/Gris rocheux)
        montagne.texture(new ColorTexture(Color.newCol(0.46f, 0.35f, 0.3f)));

        // Ajout de la montagne à la scène
        scene.add(montagne);

        // 3. Configuration de la Caméra (Règles 7 & 8 : Vecteur "UP" explicite)
        // Positionnée en (20, 20, 20), regarde le centre (0, 5, 0)
        Point3D positionCamera = new Point3D(25.0, 20.0, 25.0);
        Point3D pointCible = new Point3D(0.0, 5.0, 0.0);
        Camera camera = new Camera(positionCamera, pointCible, Point3D.Y);
        scene.cameraActive(camera);

        // 4. Rendu de l'image (Règle 3)
        int largeur = 1920;
        int hauteurImg = 1080;
        ZBufferImpl zBuffer = new ZBufferImpl(largeur, hauteurImg);
        zBuffer.scene(scene);

        // Couleur de fond (Ciel bleu clair)
        zBuffer.texture(new ColorTexture(Color.newCol(0.5f, 0.7f, 1.0f)));

        zBuffer.draw();

        // 5. Sauvegarde de l'image (Règle 4 & 11)
        File out = new File("montagne.png");
        zBuffer.image().saveFile(out);

        System.out.println("L'image de la montagne a été générée : " + out.getAbsolutePath());
    }
}