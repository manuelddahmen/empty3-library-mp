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

import one.empty3.apps.testobject.Resolution;
import one.empty3.apps.testobject.TestObjetSub;

import one.empty3.library.*;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestPlanets2 extends TestObjetSub {
    static class TextureInvertU extends ImageTexture {
        public TextureInvertU( one.empty3.libs.Image  image) {
            super( image);
        }

        @Override
        public Point2D getCoord(double x, double y) {
            return super.getCoord(1.0 - x, y);
        }
    }

    public static final int SECONDS = 35;
    public static final int FPS = 25;
    private static final int TURNS = 1;
    private static final int REAL_DAYS = 1;
    private final File planets = new File("res\\img\\planets2");
    private final File earthFilename = new File(planets.getAbsolutePath() +
            File.separator + "_earth.jpg");
    private final File moonFilename = new File(planets.getAbsolutePath() +
            File.separator + "8k_moon.jpg");
    private final File sunFilename = new File(planets.getAbsolutePath() +
            File.separator + "8k_sun.jpg");
    private final double radius = 2.0;
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
    private final Point3D axeVerticalVideo = Point3D.Y;
    private final Point3D[] axeViseeVideo = new Point3D[]{Point3D.Z, Point3D.X};
    private final Point3D[] axesSphereHorizontaux = new Point3D[]{Point3D.Z, Point3D.X};
    private final int nBalles = 1;
    private Point3D[][] s;
    private Point3D[][] v;
    private final int N = 20;
    private final double V = 10;
    private final double D = 10.0;
    private Sphere sun;
    private Sphere earth;
    private Sphere moon;

    private static double getaDouble() {
        return FPS * SECONDS * REAL_DAYS;
    }

    public static void main(String[] args) {
        one.empty3.library.tests.TestPlanetEtLune1
                testPlanets = new one.empty3.library.tests.TestPlanetEtLune1();
        testPlanets.loop(true);
        testPlanets.setResolution(Resolution.HD1080RESOLUTION.x(), Resolution.HD1080RESOLUTION.y());
        //testPlanets.setResolution(320, 240);
        //testPlanets.setResolution(160, 90);
        Thread thread = new Thread(testPlanets);
        thread.start();
    }

    @Override
    public void ginit() {
        sun = new Sphere(new Point3D(0., 0., sunDistance), sunRealSize);
        earth = new Sphere(new Point3D(0., 0., 0.), radius);
        moon = new Sphere(new Point3D(0., moonDistance, 0.0), moonRealSize);

        sun.texture(new TextureInvertU(new  one.empty3.libs.Image(ImageIO.read(sunFilename))));
        earth.texture(new TextureInvertU(new  one.empty3.libs.Image(ImageIO.read(earthFilename))));
        moon.texture(new TextureInvertU(new  one.empty3.libs.Image(ImageIO.read(moonFilename))));

        logger = Logger.getLogger(this.getClass().getCanonicalName());
        setMaxFrames(FPS * SECONDS * REAL_DAYS);

        z().ratioVerticalAngle();


        z().setDisplayType(ZBufferImpl.DISPLAY_ALL);

        z().texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f)));
        scene().texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f)));


/*
        Camera c = new Camera(axeViseeVideo[1].mult((double)  earthRealSize*1.4),
                axeViseeVideo[1].mult((double)  earthRealSize*1.4)
                        .prodVect(axeVerticalVideo.mult(earthRealSize))
                        .mult(1./earthRealSize), axeVerticalVideo);
*/
        i = -1;

        LumierePonctuelle lumierePointSimple = new LumierePonctuelle(Point3D.X.mult(1000.0), one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f));

        //scene().lumieres().add(lumierePointSimple);

        frame = 0;
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


        double angleCamera = 0; //2.0 * Math.PI * frame() / getMaxFrames();
        c = new Camera(axeViseeVideo[0].mult(Math.cos(angleCamera))
                .plus(axeViseeVideo[0].mult(Math.sin(angleCamera))).mult(radius * 3), Point3D.O0);
        c.calculerMatrice(axeVerticalVideo);
        c.setAngleX(Math.PI / 3 * z().la() / z().ha());
        c.setAngleY(Math.PI / 3);
        z().scene().cameraActive(c);
        scene().cameraActive(c);
        z().camera(c);
        camera(c);


        for (Sphere sphere : new Sphere[]{earth}) {
            sphere.setIncrU(.05);
            sphere.setIncrV(.05);
            scene().clear();
            scene().add(sphere);
        }
        final int i1 = FPS * SECONDS * REAL_DAYS;
        double u = ((double) getMaxFrames() - frame()) / getMaxFrames();

        Circle circle = earth.getCircle();
        circle.getAxis().getElem().getP1().setElem(axeVerticalVideo.mult(radius));
        circle.getAxis().getElem().getP2().setElem(axeVerticalVideo.mult(-radius));
        earth.setVectZ(axeVerticalVideo);
        earth.setVectX(axesSphereHorizontaux[0].mult(Math.cos(2 * Math.PI * u))
                .plus(axesSphereHorizontaux[1].mult(Math.sin(2 * Math.PI * u))));
        earth.setVectY(axesSphereHorizontaux[0].mult(-Math.sin(2 * Math.PI * u))
                .plus(axesSphereHorizontaux[1].mult(Math.cos(2 * Math.PI * u))));
        /*
        Matrix33 matrixB = Matrix33.ZXY.mult(Matrix33.rotationZ(2.0*Math.PI*u))
                .mult(Matrix33.rotationX(Math.PI));
        Point3D[] colVectors = matrixB.getColVectors();//Matrix33.ZXY.mult(matriceB).getRowVectors();
        earth.setVectX(colVectors[0]);
        earth.setVectY(colVectors[1]);
        earth.setVectZ(colVectors[2]);
        int i = 0;
        for(Point3D v : colVectors) {
            Logger.getAnonymousLogger().log(Level.INFO, "P3 ["+i+"] = " + v);
            i++;
        }
        Logger.getAnonymousLogger().log(Level.INFO, matrixB);
*/

        earth.setCircle(circle);
        circle.setCalculerRepere1(true);
        earth.setQuad_not_computed(0);
        scene().add(circle);
        Logger.getAnonymousLogger().log(Level.INFO, "Camera u : " + u);
    }
}
