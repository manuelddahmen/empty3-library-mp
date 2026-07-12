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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestPlanetEtLune extends TestObjetSub {
    public static final int SECONDS = 45;
    public static final int FPS = 25;
    private static final int TURNS = 1;
    private final File planets = new File("res\\img\\planets2");
    private File earthFilename = new File(planets.getAbsolutePath() +
            File.separator + "_earth.jpg");
    private File moonFilename = new File(planets.getAbsolutePath() +
            File.separator + "8k_moon.jpg");
    private File sunFilename = new File(planets.getAbsolutePath() +
            File.separator + "8k_sun.jpg");
    private final double sunDistance = 150;
    private final double moonDistance = 0.384;
    private final double sunRealSize = 13927E6 / 2;
    private final double earthRealSize = (double) 12756 / 2;
    private final double moonRealSize = 3475;
    private final double earthPeriod = 365.256;
    private final double moonPeriod = 655.7 / 24;
    private final double earthMass = 5.97;//E24
    private final double sunMass = 1.98847E6;
    private final double moonMass = 0.073;

    private int i = -1;
    private BufferedImage image;
    private Logger logger;
    private Point3D axeVerticalVideo = Point3D.Y;
    private Point3D[] axeViseeVideo = new Point3D[]{Point3D.X, Point3D.Z};
    private Point3D[] axesSphereHorizontaux = new Point3D[]{Point3D.X, Point3D.Z};
    private final int nBalles = 1;
    private Point3D[][] s;
    private Point3D[][] v;
    private int N = 20;
    private double V = 10;
    private double D = 10.0;
    private Sphere sun;
    private Sphere earth;
    private Sphere moon;

    private static double getaDouble() {
        return TURNS * FPS * SECONDS;
    }

    public static void main(String[] args) {
        TestPlanetEtLune testPlanets = new TestPlanetEtLune();
        testPlanets.loop(true);
        testPlanets.setResolution(1920, 1080);
        Thread thread = new Thread(testPlanets);
        thread.start();
    }

    @Override
    public void ginit() throws IOException {
        sun = new Sphere(new Point3D(0., 0., sunDistance), sunRealSize);
        earth = new Sphere(new Point3D(0., 0., 0.), earthRealSize);
        moon = new Sphere(new Point3D(0., moonDistance, 0.0), moonRealSize);

        sun.texture(new ImageTexture(new  one.empty3.libs.Image(ImageIO.read(sunFilename))));
        earth.texture(new ImageTexture(new  one.empty3.libs.Image(ImageIO.read(earthFilename))));
        moon.texture(new ImageTexture(new  one.empty3.libs.Image(ImageIO.read(moonFilename))));

        logger = Logger.getLogger(this.getClass().getCanonicalName());
        setMaxFrames(FPS * SECONDS);

        z().ratioVerticalAngle();


        z().setDisplayType(ZBufferImpl.SURFACE_DISPLAY_TEXT_QUADS);

        z().texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f)));
        scene().texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f)));


        Camera c = new Camera(axeViseeVideo[1].mult((double) earthRealSize * 1.4), Point3D.O0, axeVerticalVideo);
        c.calculerMatrice(axeVerticalVideo);
        z().scene().cameraActive(c);
        scene().cameraActive(c);
        z().camera(c);
        camera(c);
        i = -1;

        frame = 909;
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

        for (Sphere sphere : new Sphere[]{moon, sun, earth}) {
            sphere.setIncrU(.003);
            sphere.setIncrV(.003);
            scene().clear();
            scene().add(sphere);
        }

        double v = (frame() % (FPS * SECONDS)) / getaDouble();

        Circle circle = earth.getCircle();
        circle.setVectZ(axeVerticalVideo);
        double lat = 51. / 90;
        circle.getAxis().getElem().getP1().setElem(axeVerticalVideo.mult(1.0));
        circle.getAxis().getElem().getP2().setElem(axeVerticalVideo.mult(-1.0));
        circle.setVectX(axesSphereHorizontaux[0].mult(Math.cos(2 * Math.PI * v)
                        * Math.cos(Math.PI / 2 * lat))
                .plus(axesSphereHorizontaux[1].mult(-Math.sin(2 * Math.PI * v)
                        * Math.cos(Math.PI / 2 * lat))).norme1());
        circle.setVectY(axesSphereHorizontaux[0].mult(Math.sin(2 * Math.PI * v)
                        * Math.cos(Math.PI / 2 * lat))
                .plus(axesSphereHorizontaux[1].mult(Math.cos(2 * Math.PI * v)
                        * Math.cos(Math.PI / 2 * lat))).norme1());
        circle.setCalculerRepere1(true);
        earth.setCircle(circle);
        Logger.getAnonymousLogger().log(Level.INFO, "Camera t : " + v);

    }


}
