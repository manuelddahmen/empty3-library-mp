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
import one.empty3.apps.testobject.TestObjet;

import java.awt.Color;
import java.io.File;
import javax.imageio.ImageIO;

public class RedCubeExample {
    public static void main(String[] args) {
        // 1. Création de la scène
        Scene scene = new Scene();

        // 2. Création du cube
        // Par défaut, le cube est centré ou défini par ses dimensions
        double size = 1.0;
        Box cube = new Box(size, size, size);

        // 3. Définition de la couleur rouge
        cube.texture(new ColorTexture(new one.empty3.libs.Color(Color.RED.getRGB())));

        // Ajout du cube à la scène
        scene.add(cube);

        // 4. Configuration de la caméra
        // On place la caméra un peu en retrait pour voir le cube
        Camera camera = new Camera(new Point3D(2.0, 2.0, 2.0), Point3D.O0);
        scene.cameraActive(camera);

        // 5. Rendu de l'image avec ZBufferImpl
        int width = 800;
        int height = 600;
        ZBufferImpl zBuffer = new ZBufferImpl(width, height);
        zBuffer.scene(scene);
        zBuffer.camera(camera);

        // Calcul du rendu
        zBuffer.draw();

        // 6. Enregistrement de l'image
        try {
            File output = new File("cube_rouge.png");
            ImageIO.write(zBuffer.image(), "png", output);
            System.out.println("Image enregistrée : " + output.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}