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
import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.libs.Color;

public class GreenCubeAnimation extends TestObjetSub {
    double startX = -2.5;
    double endX = 2.5;
    private Box cube;
    private double cubeSize = 0.5; // Correspond au quart d'une hauteur d'écran de 2.0 unités

    @Override
    public void ginit() {
        // Initialisation de la scène
        scene = new Scene();

        // Création du cube vert
        cube = new Box(cubeSize, cubeSize, cubeSize);
        cube.texture(new ColorTexture(Color.newCol(0f, 1f, 0f)));

        cube.setOrig(new Point3D(startX, 0d, 0d));

        // Ajout du cube à la scène
        scene.add(cube);

        // Positionnement de la caméra (vue de face)
        Camera camera = new Camera(new Point3D(0d, 0d, 5d), Point3D.O0, Point3D.Y);
        scene.cameraActive(camera);
    }

    @Override
    public void finit() {
        // Calcul de la progression du film (de 0.0 à 1.0)
        double progress = (double) frame() / getMaxFrames();


        // Animation : de gauche (x = -2.5) à droite (x = 2.5)

        double currentX = startX + (endX - startX) * progress;
        currentX = (endX - startX) / getMaxFrames();
        // Mise à jour de la position du cube
        // On définit la position du centre du cube
        cube.setOrig(new Point3D(currentX, 0d, 0d));

    }

    public static void main(String[] args) {
        GreenCubeAnimation animation = new GreenCubeAnimation();
        // Configuration de la résolution et du nombre d'images
        animation.setResolution(800, 600);
        animation.setMaxFrames(100);
        new Thread(animation).start();
    }
}