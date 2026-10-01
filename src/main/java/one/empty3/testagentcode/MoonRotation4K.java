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
 *  * Created by Manuel D Dahmen -2026
 *
 *
 */

package one.empty3.testagentcode;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.*;
import one.empty3.libs.Image;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

/**
 * Animation d'une sphère Moon en 4K pendant 20 secondes.
 */
public class MoonRotation4K extends TestObjetSub {
    private Sphere moon;
    private static final int FPS = 25;
    private static final int DURATION_SECONDS = 50;
    private static final String STARS_TEXTURE_PATH = "res/img/planets3/stars.jpg";
    private ImageTexture imageTexture;
    private ArrayList<File> planetsImagesFile;
    private String[] planetsImages;
    private int oldI;
    private String currentImageName;

    @Override
    public void ginit() {
        frame = 0;
        // 1. Création de la scène
        scene = new Scene();


        File file1 = new File("res/img/planets3");
        if (file1.exists()) {
            Object[] sorted = Arrays.stream(Objects.requireNonNull(new File("res/img/planets3/").list())).sorted().toArray();
            planetsImagesFile = new ArrayList<>();
            for (Object o : sorted) {
                if (o != null && !o.equals("others") && !o.equals("stars.jpg")) {
                    assert planetsImages != null;
                    System.out.println("File exists: " + o);
                    planetsImagesFile.add(new File("res/img/planets3/" + o.toString()));
                }
            }
            setMaxFrames(planetsImagesFile.size() * DURATION_SECONDS * FPS);
        }


        imageTexture = new ImageTexture(new File(STARS_TEXTURE_PATH));

        // 2. Création de la sphère (Centre 0,0,0, Rayon 1.0)
        moon = new Sphere(new Axe(Point3D.Y.mult(-1), Point3D.Y), 1.0);
        moon.calculerPoint3D(0, 0);
        // Ajout de la sphère à la scène
        scene.add(moon);

        // 4. Configuration de la caméra (Rule 7 & 8: Vecteur UP explicite pour éviter matrice nulle)
        // Positionnée à z=3 pour voir la sphère de rayon 1.0
        Camera camera = new Camera(new Point3D(0.0, 0.0, 2.0), Point3D.O0, Point3D.Y);
        camera.angleXY(((ZBufferImpl) z()).getDimx(), ((ZBufferImpl) z()).getDimy(), Math.PI / 3, Axis.Y);
        scene.cameraActive(camera);
        oldI = -1;
    }

    @Override
    public void finit() {

        int planetI = (frame() - 1) / (FPS * DURATION_SECONDS);

        // 3. Application de la texture Moon (Rule 4 & 11)
        try {
            if (oldI != planetI) {
                ImageTexture imageTexture = new ImageTexture(planetsImagesFile.get(planetI));
                moon.setTexture(imageTexture);
                currentImageName = planetsImagesFile.get(planetI).getName();
                oldI = planetI;
            }
        } catch (Exception e) {
            moon.texture(new ColorTexture(one.empty3.libs.Color.newCol(0.8f, 0.4f, 0.2f)));
            e.printStackTrace();
        }


        z().texture(imageTexture);
        // 5. Animation de la rotation (Rule 2 & 5)
        // Calcul de l'angle en fonction de l'image actuelle (frame)
        double totalFrames = (DURATION_SECONDS * FPS);
        double angle = 2.0 * Math.PI * (double) ((frame % ((int) totalFrames)) / totalFrames);

        // Rotation autour de l'axe Y : mise à jour des vecteurs d'orientation
        double cosA = Math.cos(angle);
        double sinA = Math.sin(angle);


        // Modification des axes de la sphère pour la faire tourner sur elle-même
        moon.getCircle().setVectX(new Point3D(sinA, 0.0, cosA));
        moon.getCircle().setVectY(new Point3D(cosA, 0.0, -sinA));
        moon.getCircle().setVectZ(Point3D.Y);
        //moon.setVectZ(Point3D.Y);
        moon.setOrig(new Point3D(0.0, 0.0, 0.0)); // Centre de rotation
    }

    public static void main(String[] args) {
        MoonRotation4K animation = new MoonRotation4K();
        animation.setGenerate(GENERATE_IMAGE | GENERATE_MOVIE | GENERATE_SAVE_IMAGE);
        // Configuration du rendu
        animation.setResX(3840); // 4K UHD
        animation.setResY(2160);
        //animation.setResX(300); // 4K UHD
        //animation.setResY(200);
        animation.setFps(FPS);
        animation.setPublish(true);
        // Nombre total d'images (20s * 25fps = 500 frames)
        animation.setMaxFrames(DURATION_SECONDS * FPS);

        // Lancement du processus de rendu
        Thread thread = new Thread(animation);
        thread.start();
    }

    @Override
    public void afterRender() {
        super.afterRender();
        Image graphe = getPicture();
        Graphics graphics = graphe.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.drawString(currentImageName, 10, 10);
        graphe.getGraphics();
    }
}