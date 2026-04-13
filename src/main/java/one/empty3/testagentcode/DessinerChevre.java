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

public class DessinerChevre {

    public static void main(String[] args) {
        Scene scene = new Scene();

        // Couleurs
        Color couleurCorps = Color.newCol(0.8f, 0.7f, 0.5f); // Brun clair / Beige
        Color couleurCornes = Color.newCol(0.3f, 0.2f, 0.1f); // Brun foncé
        Color couleurPeau = Color.newCol(0.1f, 0.1f, 0.1f);   // Noir pour les sabots/museau

        // 1. Le corps (plus fin que le mouton)
        Sphere corps = new Sphere(new Point3D(0d, 0d, 0d), 1.0);
        corps.texture(new TextureCol(couleurCorps));
        // Allongement du corps
        corps.setVectX(new Point3D(1.6, 0d, 0d));
        scene.add(corps);

        // 2. Le cou (Cylindre oblique)
        Point3D baseCou = new Point3D(1.3, 0.3, 0d);
        Point3D hautCou = new Point3D(1.8, 1.2, 0d);
        Cylinder cou = new Cylinder(baseCou, hautCou, 0.3);
        cou.texture(new TextureCol(couleurCorps));
        scene.add(cou);

        // 3. La tête
        Sphere tete = new Sphere(hautCou.plus(new Point3D(0.2, 0.2, 0d)), 0.4);
        tete.texture(new TextureCol(couleurCorps));
        scene.add(tete);

        // 4. Les cornes (deux petits cylindres incurvés vers l'arrière)
        Point3D front = hautCou.plus(new Point3D(0.1, 0.4, 0d));

        Cylinder corneD = new Cylinder(front.plus(new Point3D(0d, 0d, 0.15)),
                front.plus(new Point3D(-0.3, 0.6, 0.2)), 0.08);
        corneD.texture(new TextureCol(couleurCornes));
        scene.add(corneD);

        Cylinder corneG = new Cylinder(front.plus(new Point3D(0d, 0d, -0.15)),
                front.plus(new Point3D(-0.3, 0.6, -0.2)), 0.08);
        corneG.texture(new TextureCol(couleurCornes));
        scene.add(corneG);

        // 5. Les pattes (plus fines et longues)
        double hauteurPatte = 1.8;
        double rayonPatte = 0.1;
        Point3D[] posPattes = {
                new Point3D(1.0, -0.7, 0.4),
                new Point3D(1.0, -0.7, -0.4),
                new Point3D(-1.0, -0.7, 0.4),
                new Point3D(-1.0, -0.7, -0.4)
        };

        for (Point3D pos : posPattes) {
            Cylinder patte = new Cylinder(pos, pos.plus(new Point3D(0d, -hauteurPatte, 0d)), rayonPatte);
            patte.texture(new TextureCol(couleurCorps));
            scene.add(patte);

            // Sabots
            Sphere sabot = new Sphere(pos.plus(new Point3D(0d, -hauteurPatte, 0d)), 0.12);
            sabot.texture(new TextureCol(couleurPeau));
            scene.add(sabot);
        }

        // Configuration Caméra
        Camera camera = new Camera(new Point3D(5d, 3d, 8d), Point3D.O0, Point3D.Y);
        scene.cameraActive(camera);

        // Rendu
        int width = 1200;
        int height = 900;
        ZBufferImpl zBuffer = new ZBufferImpl(width, height);
        zBuffer.scene(scene);
        zBuffer.camera(camera);

        // Lumière
        scene.getLumieres().add(new LumierePonctuelle(new Point3D(10d, 10d, 10d), Color.newCol(1f, 1f, 1f)));

        zBuffer.draw(scene);

        // Sauvegarde
        Image image = zBuffer.image();
        File output = new File("chevre.png");
        if (image.saveFile(output)) {
            System.out.println("Image de la chèvre générée : " + output.getAbsolutePath());
        }
    }
}