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

import java.awt.Color;
import java.io.File;
import javax.imageio.ImageIO;

public class SphereBleue {
    public static void main(String[] args) {
        // 1. Création de la scène
        Scene scene = new Scene();

        // 2. Création de la sphère
        // Paramètres : centre (Point3D) et rayon (double)
        Point3D centre = new Point3D(0.0, 0.0, 0.0);
        double rayon = 1.0;
        Sphere sphere = new Sphere(centre, rayon);

        // 3. Application de la couleur bleue
        sphere.texture(new ColorTexture(one.empty3.libs.Color.newCol(0f, 0f, 1f)));

        // Ajout de la sphère à la scène
        scene.add(sphere);

        // 4. Configuration de la caméra
        // On place la caméra pour voir la sphère
        Camera camera = new Camera(new Point3D(0.0, 0.0, 4.0), Point3D.O0, Point3D.Y);
        scene.cameraActive(camera);

        // 5. Rendu avec ZBufferImpl
        int largeur = 800;
        int hauteur = 600;
        ZBufferImpl zBuffer = ZBufferFactory.instance(largeur, hauteur, scene);
        zBuffer.camera(camera);

        // Calcul du rendu
        zBuffer.draw();

        // 6. Enregistrement de l'image
        try {
            File output = new File("sphere_bleue.png");
            ImageIO.write(zBuffer.image().getBi(), "png", output);
            System.out.println("Image de la sphère bleue générée : " + output.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}