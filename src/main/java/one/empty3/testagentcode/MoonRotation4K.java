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
import one.empty3.libs.Image;

import java.io.File;

/**
 * Animation d'une sphère Moon en 4K pendant 20 secondes.
 */
public class MoonRotation4K extends TestObjetSub {
    private Sphere moon;
    private static final int FPS = 25;
    private static final int DURATION_SECONDS = 20;

    @Override
    public void ginit() {
        frame = 0;
        scene = new Scene();

        // 1. Create Axis strictly aligned with the Y-axis.
        // The North Pole (P1) is UP (0, 1, 0), the South Pole (P2) is DOWN (0, -1, 0).
        Axe yAxis = new Axe(new Point3D(0.0, 1.0, 0.0), new Point3D(0.0, -1.0, 0.0));

        // 2. Initialize Sphere with this axis.
        moon = new Sphere(yAxis, 1.0);

        // IMPORTANT: Let the sphere compute its own basis.
        // Do NOT manually setVectX, setVectY, setVectZ here.
        // If you applied the fix to Circle.calculerRepere1(),
        // it will now correctly build the basis aligned with this Y-axis.

        // 3. Texture setup
        try {
            File textureFile = new File("d:\\current\\moon2.jpg");
            if (textureFile.exists()) {
                Image image = new Image(textureFile);
                moon.texture(new ImageTexture(textureFile));
            } else {
                moon.texture(new ColorTexture(one.empty3.libs.Color.newCol(0.8f, 0.4f, 0.2f)));
                System.out.println("Texture file not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        scene.add(moon);

        // 4. Camera setup looking at the center
        Camera camera = new Camera(new Point3D(0.0, 0.0, 3.0), Point3D.O0, Point3D.Y);
        scene.cameraActive(camera);

    }

    @Override
    public void finit() {
        ((ZBufferImpl) z()).setIncrementOptimizer(new ZBufferImpl.IncrementOptimizer(ZBufferImpl.IncrementOptimizer.Strategy.ENSURE_MINIMUM_DETAIL, 0.01));
        ((ZBufferImpl) z()).setDisplayType(ZBufferImpl.DISPLAY_ALL);
        // 5. Animation de la rotation autour de l'axe Y
        double totalFrames = (double) (DURATION_SECONDS * FPS);
        double angle = 2.0 * Math.PI * (double) frame / totalFrames;

        // Rotation matrix around Y axis
        Matrix33 rotY = Matrix33.rotationY(angle);

        // Apply rotation to the sphere's orientation vectors
        moon.setVectX(rotY.mult(Point3D.X));
        moon.setVectY(rotY.mult(Point3D.Z));
        moon.setVectZ(Point3D.Y);

        moon.setOrig(new Point3D(0.0, 0.0, 0.0));
    }

    public static void main(String[] args) {
        MoonRotation4K animation = new MoonRotation4K();
        animation.setGenerate(GENERATE_IMAGE | GENERATE_MOVIE | GENERATE_SAVE_IMAGE);
        // Configuration du rendu
        animation.setResX(3840 / 8); // 4K UHD
        animation.setResY(2160 / 8);
        animation.setFps(FPS);
        animation.setPublish(false);
        // Nombre total d'images (20s * 25fps = 500 frames)
        animation.setMaxFrames(DURATION_SECONDS * FPS);

        // Lancement du processus de rendu
        Thread thread = new Thread(animation);
        thread.start();
    }
}