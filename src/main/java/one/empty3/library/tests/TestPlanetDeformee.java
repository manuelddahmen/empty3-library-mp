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

import one.empty3.feature.app.replace.javax.imageio.ImageIO;
import one.empty3.library.*;
import one.empty3.library.core.testing.TestObjetSub;
import one.empty3.testscopy.tests.tests2.balleclou.BalleClous2;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestPlanetDeformee extends TestObjetSub {
    public static final int SECONDS = 8;
    public static final int FPS = 25;
    private static final int TURNS = 1;
    private final File planets = new File("res\\img\\planets2");
    private File[] planetsImagesFiles;
    private int i = -1;
    private BufferedImage image;
    private Sphere sphere;
    private Logger logger;
    private Point3D axeVerticalVideo = Point3D.Y;
    private Point3D[] axeViseeVideo = new Point3D[]{Point3D.X, Point3D.Z};
    private Point3D[] axesSphereHorizontaux = new Point3D[]{Point3D.X, Point3D.Z};
    private final int nBalles = 1;
    private Point3D[][] s;
    private Point3D[][] v;
    private int N = 20;
    private double V = 10;
    private BalleClous2[] balles = new BalleClous2[nBalles];
    private double D = 10.0;

    private static double getaDouble() {
        return TURNS * FPS * SECONDS;
    }

    public static void main(String[] args) {
        TestPlanetDeformee testPlanets = new TestPlanetDeformee();
        testPlanets.loop(true);
        testPlanets.setResolution(1920, 1080);
        Thread thread = new Thread(testPlanets);
        thread.start();
    }

    @Override
    public void ginit() {
        s = new Point3D[nBalles][N];
        v = new Point3D[nBalles][N];

        for (int b = 0; b < nBalles; b++) {
            for (int i = 0; i < N; i++) {
                s[b][i] = new Point3D(Point3D.O0);
                v[b][i] = new Point3D(Math.random() * (V / 2 - V), Math.random() * (V / 2 - V), Math.random() * (V / 2 - V));

            }

        }

        logger = Logger.getLogger(this.getClass().getCanonicalName());
        planetsImagesFiles = planets.listFiles();

        setMaxFrames(planetsImagesFiles.length * FPS * SECONDS);

        z().ratioVerticalAngle();


        z().setDisplayType(ZBufferImpl.SURFACE_DISPLAY_TEXT_QUADS);

        z().texture(new ColorTexture(Color.BLACK));
        scene().texture(new ColorTexture(Color.BLACK));


        Camera c = new Camera(axeViseeVideo[1].mult((double) 0.333), Point3D.O0, axeVerticalVideo);
        c.calculerMatrice(axeVerticalVideo);
        z().scene().cameraActive(c);
        scene().cameraActive(c);
        z().camera(c);
        camera(c);
        i = -1;

        //frame = 3990;
        sphere = new BalleClous2(
                new Axe(axeVerticalVideo.mult(1.0), axeVerticalVideo.mult(-1.0)).getCenter(), 2.0);

        incr();
    }

    public void incr() {
        int i1 = (frame() / (FPS * SECONDS * TURNS));
        if (i1 != i) {
            i = i1;
            if (i1 < planetsImagesFiles.length)
                image = ImageIO.read(planetsImagesFiles[i1]);
            else {
                i1 = 0;
            }

            Logger.getAnonymousLogger().log(Level.INFO, "Planets:" + i1 + "/" + planetsImagesFiles.length);
        }
    }

    public Point3D positions() {
        Point3D p = new Point3D();
        p.set(0, 1.0 * (frame() / (FPS * SECONDS * TURNS)));
        p.set(1, 1.0 * (frame() % (FPS * SECONDS * TURNS)));
        p.set(2, 1.0 * FPS * SECONDS * TURNS);

        return p;
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


        for (int i = 0; i < balles.length; i++
        ) {

            sphere.setIncrU(0.01);
            sphere.setIncrV(0.01);

            balles[i] = (BalleClous2) sphere;

            //textureMov = new TextureMov("C:\\Emptycanvas\\Resources\\BigFloEtOlie.mp4");
            //textureMov.setTransparent(Color.BLACK);
            //ballec.texture(textureMov);
            scene().add(balles[i]);


        }
        sphere.setIncrU(.003);
        sphere.setIncrV(.003);
        scene().clear();
        scene().add(sphere);


        TextureImg textureImg = new TextureImg(new ECBufferedImage(image));
        sphere.texture(textureImg);

        double v = (frame() % (FPS * SECONDS)) / getaDouble();

        Circle circle = sphere.getCircle();
        circle.setVectZ(axeVerticalVideo);
        circle.getAxis().getElem().getP1().setElem(axeVerticalVideo.mult(1.0));
        circle.getAxis().getElem().getP2().setElem(axeVerticalVideo.mult(-1.0));
        circle.setVectX(axesSphereHorizontaux[0].mult(Math.cos(2 * Math.PI * v))
                .plus(axesSphereHorizontaux[1].mult(-Math.sin(2 * Math.PI * v))).norme1());
        circle.setVectY(axesSphereHorizontaux[0].mult(Math.sin(2 * Math.PI * v))
                .plus(axesSphereHorizontaux[1].mult(Math.cos(2 * Math.PI * v))).norme1());
        circle.setCalculerRepere1(true);
        sphere.setCircle(circle);
        Logger.getAnonymousLogger().log(Level.INFO, "Camera t : " + v);


        deforme();
    }

    public void deforme() throws Exception {
        for (int b = 0; b < balles.length; b++) {
            BalleClous2 ballec = balles[b];

            for (int i = 0; i < s[b].length; i++) {
                bounce(b, i);
            }


            ballec.points().clear();
            double totalA = 0;
            double totalB = 0;

            for (int j = 0; j < s[b].length; j++) {
                if (s[b][j].getX() < 0) {
                    s[b][j].setX(s[b][j].getX() + D);
                }
                if (s[b][j].getY() < 0) {
                    s[b][j].setY(s[b][j].getY() + D);
                }
                if (s[b][j].getX() > D) {
                    s[b][j].setX(s[b][j].getX() - D);
                }
                if (s[b][j].getY() > D) {
                    s[b][j].setY(s[b][j].getY() - D);
                }

                totalA += s[b][j].getX();
                totalB += s[b][j].getY();

                ballec.addPoint(new Point2D(s[b][j].getX(), s[b][j].getY()));

            }

        }
    }

    @Override
    public void afterRender() {

    }

    public void bounce(int numBalle, int i) {
        s[numBalle][i] = s[numBalle][i].plus(v[numBalle][i]);


        if (s[numBalle][i].getX() > D && v[numBalle][i].getX() > 0) {
            v[numBalle][i].setX(-v[numBalle][i].getX());
        }
        if (s[numBalle][i].getX() < -D && v[numBalle][i].getX() < 0) {
            v[numBalle][i].setX(-v[numBalle][i].getX());
        }
        if (s[numBalle][i].getY() > D && v[numBalle][i].getY() > 0) {
            v[numBalle][i].setY(-v[numBalle][i].getY());
        }
        if (s[numBalle][i].getY() < -D && v[numBalle][i].getY() < 0) {
            v[numBalle][i].setY(-v[numBalle][i].getY());
        }
        if (s[numBalle][i].getZ() > D && v[numBalle][i].getZ() > 0) {
            v[numBalle][i].setZ(-v[numBalle][i].getZ());
        }
        if (s[numBalle][i].getZ() < -D && v[numBalle][i].getZ() < 0) {
            v[numBalle][i].setZ(-v[numBalle][i].getZ());
        }
    }
}
