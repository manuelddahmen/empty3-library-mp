/*
 *
 *  * Copyright (c) 2024. Manuel Daniel Dahmen
 *  *
 *  *
 *  *    Copyright 2024 Manuel Daniel Dahmen
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 *
 */

package one.empty3.library.tests;


import one.empty3.library.*;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestPlanets extends TestObjetSub {
    public static final int SECONDS = 8;
    public static final int FPS = 50;
    private static final int TURNS = 2;
    private final File planets = new File("res\\img\\planets2");
    private File[] planetsImagesFiles;
    private int i = -1;
    private BufferedImage image;
    private Sphere sphere;
    private Logger logger;
    private Point3D axeVerticalVideo = Point3D.Y;
    private Point3D[] axeViseeVideo = new Point3D[]{Point3D.X, Point3D.Z};
    private Point3D[] axesSphereHorizontaux = new Point3D[]{Point3D.X, Point3D.Z};
    private TextureImg textureImg;

    private static double getaDouble() {
        return TURNS * FPS * SECONDS;
    }

    public static void main(String[] args) {
        TestPlanets testPlanets = new TestPlanets();
        testPlanets.loop(true);
        testPlanets.setResolution(320, 240);
        Thread thread = new Thread(testPlanets);
        thread.start();
    }

    @Override
    public void ginit() {
        logger = Logger.getLogger(this.getClass().getCanonicalName());
        planetsImagesFiles = planets.listFiles();

        setMaxFrames(planetsImagesFiles.length * FPS * SECONDS);

        z().ratioVerticalAngle();


        z().setDisplayType(ZBufferImpl.SURFACE_DISPLAY_TEXT_QUADS);

        z().texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f)));
        scene().texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f)));


        Camera c = new Camera(axeViseeVideo[1].mult(10), Point3D.O0, axeVerticalVideo);
        c.calculerMatrice(axeVerticalVideo);
        z().scene().cameraActive(c);
        scene().cameraActive(c);
        z().camera(c);
        camera(c);
        i = -1;

        // frame = 3990;

        incr();

    }

    public void incr() {
        int i1 = (frame() / (FPS * SECONDS));
        if (i1 != i) {
            i = i1;
            if (i1 < planetsImagesFiles.length) {
                image = Image.staticLoadFile(planetsImagesFiles[i1]);
                textureImg = new TextureImg(new  one.empty3.libs.Image(image));
            } else {
                i1 = 0;
            }

            Logger.getAnonymousLogger().log(Level.INFO, "Planets:" + i1 + "/" + planetsImagesFiles.length);
        }
    }

    public void vecDirRotate(Point3D vecOrigX, Point3D vecOrigY, double ratio,
                             Point3D outX, Point3D outY) {
        outX.changeTo(vecOrigX.mult(Math.cos(2 * Math.PI * ratio)).plus(
                vecOrigY.mult(Math.sin(2 * Math.PI * ratio))));
        outY.changeTo(vecOrigX.mult(-Math.sin(2 * Math.PI * ratio)).plus(
                vecOrigY.mult(Math.cos(2 * Math.PI * ratio))));
    }

    @Override
    public void finit() throws Exception {
        incr();

        sphere = new Sphere(new Axe(axeVerticalVideo.mult(1.0), axeVerticalVideo.mult(-1.0)), 2.0);

        sphere.setIncrU(.003);
        sphere.setIncrV(.003);
        scene().clear();
        scene().add(sphere);


        sphere.texture(textureImg);

        double v = (frame() % (FPS * SECONDS)) / getaDouble();


        //setZ(new ZBufferImpl(getResx(), getResy()));

        Circle circle = sphere.getCircle();
        circle.setVectZ(axeVerticalVideo);
        circle.getAxis().getElem().getP1().setElem(axeVerticalVideo.mult(1.0));
        circle.getAxis().getElem().getP2().setElem(axeVerticalVideo.mult(-1.0));
        circle.setVectX(axesSphereHorizontaux[0].mult(Math.cos(2 * Math.PI * v))
                .plus(axesSphereHorizontaux[1].mult(Math.sin(2 * Math.PI * v))));
        circle.setVectY(axesSphereHorizontaux[0].mult(-Math.sin(2 * Math.PI * v))
                .plus(axesSphereHorizontaux[1].mult(Math.cos(2 * Math.PI * v))));
        circle.setCalculerRepere1(true);
        sphere.setCircle(circle);
        Logger.getAnonymousLogger().log(Level.INFO, "Camera t : " + v);
    }

    @Override
    public void afterRender() {

    }
}
