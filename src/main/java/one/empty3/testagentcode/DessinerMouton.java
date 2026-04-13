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
import one.empty3.library.ZBufferImpl;
import one.empty3.libs.Color;
import one.empty3.libs.Image;

import java.io.File;

public class DessinerMouton {

    public static void main(String[] args) {
        Scene scene = new Scene();

        // Couleurs
        Color couleurLaine = Color.newCol(0.9f, 0.9f, 0.9f); // Blanc cassé
        Color couleurPeau = Color.newCol(0.1f, 0.1f, 0.1f);  // Noir/Gris foncé

        // 1. Le corps (une grosse sphère étirée ou plusieurs sphères)
        Sphere corps = new Sphere(new Point3D(0d, 0d, 0d), 1.5);
        corps.texture(new TextureCol(couleurLaine));
        // On l'allonge un peu sur l'axe X pour faire un corps ovale
        corps.setVectX(new Point3D(1.5, 0d, 0d));
        scene.add(corps);

        // 2. La tête
        Sphere tete = new Sphere(new Point3D(1.8, 0.8, 0d), 0.6);
        tete.texture(new TextureCol(couleurPeau));
        scene.add(tete);

        // 3. Les pattes (4 cylindres)
        double hauteurPatte = 1.5;
        double rayonPatte = 0.15;

        Point3D[] positionsPattes = {
                new Point3D(0.8, -1.0, 0.6),
                new Point3D(0.8, -1.0, -0.6),
                new Point3D(-0.8, -1.0, 0.6),
                new Point3D(-0.8, -1.0, -0.6)
        };

        for (Point3D pos : positionsPattes) {
            // Un cylindre de la base vers le bas
            Cylinder patte = new Cylinder(pos, pos.plus(new Point3D(0d, -hauteurPatte, 0d)), rayonPatte);
            patte.texture(new TextureCol(couleurPeau));
            scene.add(patte);
        }

        // 4. Les oreilles (petites sphères)
        Sphere oreilleD = new Sphere(new Point3D(2.0, 1.2, 0.4), 0.15);
        oreilleD.texture(new TextureCol(couleurPeau));
        scene.add(oreilleD);

        Sphere oreilleG = new Sphere(new Point3D(2.0, 1.2, -0.4), 0.15);
        oreilleG.texture(new TextureCol(couleurPeau));
        scene.add(oreilleG);

        // Configuration de la Caméra
        // On se place sur le côté pour voir la forme
        Camera camera = new Camera(new Point3D(6d, 3d, 6d), Point3D.O0, Point3D.Y);
        scene.cameraActive(camera);

        // Rendu
        int width = 1024;
        int height = 768;
        ZBufferImpl zBuffer = ZBufferFactory.instance(width, height);
        zBuffer.scene(scene);
        zBuffer.camera(camera);

        // Optionnel : ajouter une lumière simple
        scene.getLumieres().add(new LumierePonctuelle(new Point3D(10d, 10d, 10d), Color.newCol(1f, 1f, 1f)));
        zBuffer.draw();

        // Sauvegarde
        Image image = zBuffer.image();
        File output = new File("mouton.png");
        if (image.saveFile(output)) {
            System.out.println("L'image du mouton a été sauvegardée : " + output.getAbsolutePath());
        }
    }
}