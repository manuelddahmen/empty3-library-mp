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
import one.empty3.libs.Color;

import java.io.File;

import one.empty3.libs.Image;

public class CubeRougeImage {
    public static void main(String[] args) {
        // 1. Création de la scène
        Scene scene = new Scene();

        // 2. Création d'un cube (Box) de dimensions 1x1x1
        // On centre le cube en utilisant une translation ou en ajustant ses paramètres
        Box cube = new Box(1.0, 1.0, 1.0);

        // 3. Application de la texture rouge
        cube.texture(new ColorTexture(Color.newCol(1f, 0f, 0f)));

        // Ajout du cube à la scène
        scene.add(cube);

        // 4. Configuration de la caméra
        // Positionnée en (2, 2, 2) pour une vue en perspective, regardant l'origine (0, 0, 0)
        Point3D positionCamera = new Point3D(2.0, 2.0, 2.0);
        Point3D pointCible = Point3D.O0;
        Camera camera = new Camera(positionCamera, pointCible);
        scene.cameraActive(camera);

        // 5. Rendu de l'image avec ZBufferImpl
        int largeur = 800;
        int hauteur = 600;
        ZBufferImpl zBuffer = ZBufferFactory.instance(largeur, hauteur, scene);
        zBuffer.camera(camera);

        // Calcul du rendu des objets de la scène
        zBuffer.draw();

        // 6. Enregistrement du résultat dans un fichier image
        try {
            File fichierSortie = new File("cube_rouge.png");
            zBuffer.image().saveFile(fichierSortie);
            System.out.println("Image du cube rouge générée avec succès : " + fichierSortie.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}