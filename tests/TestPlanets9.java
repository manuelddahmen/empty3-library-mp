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

import javax.imageio.ImageIO;
import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestPlanets9 extends TestObjetSub {
    private final File planets = new File("res\\img\\planets2");
    int moveIndex = 0;

    static class TextureInvertU extends ImageTexture {
        public TextureInvertU( one.empty3.libs.Image  image) {
            super( image);
        }

        @Override
        public Point2D getCoord(double x, double y) {
            return super.getCoord(1.0 - x, y);
        }
    }

    public static final int SECONDS = 8;
    public static final int FPS = 35;
    private static final int TURNS = 3;
    private static final int REAL_DAYS = 365;
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
    ArrayList<File> images = new ArrayList<>();

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
        TestPlanets9 testPlanets9 = new TestPlanets9();
        testPlanets9.loop(true);
        testPlanets9.setResolution(Resolution.XVGARESOLUTION.x(), Resolution.XVGARESOLUTION.y());
        Thread thread = new Thread(testPlanets9);
        thread.start();
    }

    public void list() {
        File[] files;
        files = planets.listFiles();
        if (files != null) {
            for (int i1 = files.length - 1; i1 >= 0; i1--) {
                if (!(files[i1].getName().startsWith(".") || !files[i1].getName().endsWith("jpg"))) {
                    images.add(files[i1]);
                }
            }
        }
    }


    @Override
    public void ginit() throws IOException {
        sun = new Sphere(new Point3D(0., 0., sunDistance), sunRealSize);
        earth = new Sphere(new Point3D(0., 0., 0.), radius);
        moon = new Sphere(new Point3D(0., moonDistance, 0.0), moonRealSize);

        sun.texture(new TextureInvertU(new  one.empty3.libs.Image(javax.imageio.ImageIO
                .read(sunFilename))));
        earth.texture(new TextureInvertU(new  one.empty3.libs.Image(javax.imageio.ImageIO.read(earthFilename))));
        moon.texture(new TextureInvertU(new  one.empty3.libs.Image(javax.imageio.ImageIO
                .read(moonFilename))));

        logger = Logger.getLogger(this.getClass().getCanonicalName());

        list();

        setMaxFrames(FPS * SECONDS * REAL_DAYS * 1);//images.size());

        z().ratioVerticalAngle();

        z().setFORCE_POSITIVE_NORMALS(false);

        z().setDisplayType(ZBufferImpl.DISPLAY_ALL);

        ImageTexture imageTexture = new ImageTexture(new  one.empty3.libs.Image(Objects.requireNonNull(ImageIO.read(
                new File("res/img/planets2/others/8k_stars_milky_way.jpg")))));

        z().texture(imageTexture);
        scene().texture(imageTexture);


/*
        Camera c = new Camera(axeViseeVideo[1].mult((double)  earthRealSize*1.4),
                axeViseeVideo[1].mult((double)  earthRealSize*1.4)
                        .prodVect(axeVerticalVideo.mult(earthRealSize))
                        .mult(1./earthRealSize), axeVerticalVideo);
*/
        i = -1;

        LumierePonctuelle lumierePointSimple = new LumierePonctuelle(Point3D.X.mult(1000.0), one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f)) {

            @Override
            public int getCouleur(int base, Point3D p, Point3D n) {
                if (n == null)
                    return base;
                //double x = Math.asin(p.moins(position.getElem()).norme1().dot(n.norme1())) / 2 / Math.PI;
                Point3D moins = p.moins(position.getElem());
                Point3D l = moins.norme1();

                Double dot = l.dot(n.norme1());


                double angle = dot > 0 ? 0.0 : 1.0;


                double[] couleurObjet = getDoubles(base);
                double[] res = new double[3];
                double[] Lsa = getRgb(Ls);
                double[] Laa = getRgb(La);
                for (int i = 0; i < 3; i++) {
                    double x = (1 - angle) * couleurObjet[i]
                            + (angle) * Lsa[i];
                    res[i] = x;

                }
                return getInt(res);
            }
        };

        scene().lumieres().add(lumierePointSimple);

        //frame = 472;


    }

    public Point3D positions() {
        Point3D p = new Point3D();
        p.set(0, 1.0 * (frame() / (FPS * SECONDS * TURNS)));
        p.set(1, 1.0 * (frame() % (FPS * SECONDS * TURNS)));
        p.set(2, 1.0 * FPS * SECONDS * TURNS);

        return p;
    }

    public void vecDirRotate(Point3D vecOrigX, Point3D vecOrigY, Point3D vecOrigZ, double ratio,
                             Point3D outX, Point3D outY, Point3D outZ) {
        outX.changeTo(vecOrigX.mult(Math.cos(2 * Math.PI * ratio)).plus(
                vecOrigY.mult(Math.sin(2 * Math.PI * ratio))));
        outY.changeTo(vecOrigX.mult(-Math.sin(2 * Math.PI * ratio)).plus(
                vecOrigY.mult(Math.cos(2 * Math.PI * ratio))));
        outZ.changeTo(vecOrigZ.mult(1.0));
    }

    public Matrix33 vecDirRotate2(Matrix33 matrixOrigRotation, double angleRad,
                                  Point3D vecAxesX, Point3D vecAxesY, Point3D vecAxesZ) {
        Point3D[] rowVectors = matrixOrigRotation.getRowVectors();
        vecAxesX = (rowVectors[0].mult(Math.cos(2 * Math.PI * angleRad)).plus(
                rowVectors[1].mult(Math.sin(2 * Math.PI * angleRad))));
        vecAxesY = (rowVectors[0].mult(-Math.sin(2 * Math.PI * angleRad)).plus(
                rowVectors[1].mult(Math.cos(2 * Math.PI * angleRad))));
        vecAxesZ = rowVectors[2];
        Matrix33 mult = matrixOrigRotation.mult(new Matrix33(new Point3D[
                ]{vecAxesX, vecAxesY, vecAxesZ}));
        return mult;
    }

    private Matrix33 vecDirRotate3(Matrix33 matrix33, double angleRad) {
        Point3D[] rowVectors = matrix33.getRowVectors();
        Point3D vecAxesX = (rowVectors[0].mult(Math.cos(2 * Math.PI * angleRad)).plus(
                rowVectors[1].mult(Math.sin(2 * Math.PI * angleRad))));
        Point3D vecAxesY = (rowVectors[0].mult(-Math.sin(2 * Math.PI * angleRad)).plus(
                rowVectors[1].mult(Math.cos(2 * Math.PI * angleRad))));
        Point3D vecAxesZ = rowVectors[2];
        Matrix33 mult = new Matrix33(new Point3D[]{vecAxesX, vecAxesY, vecAxesZ});
        return mult;
    }

    @Override
    public void finit() throws Exception {
        int palier = (frame() / (int) (getaDouble())) % images.size();
        palier = 0;
        earth.texture(new TextureInvertU(
                new  one.empty3.libs.Image(Objects.requireNonNull(ImageIO.read(images
                        .get(palier))))));

        Logger.getAnonymousLogger().log(Level.INFO, "Palier:  " + palier + " " + images.get(palier).getName());

        double angleCamera = 0.0; //2.0 * Math.PI * frame() / FPS / SECONDS * 3.156E7;
        c = new Camera(axeViseeVideo[0].mult(Math.cos(angleCamera))
                .plus(axeViseeVideo[0].mult(Math.sin(angleCamera))).mult(radius * 4), Point3D.O0);
        c.calculerMatrice(axeVerticalVideo);
        c.setAngleX(Math.PI / 3 * z().la() / z().ha());
        c.setAngleY(Math.PI / 3);
        z().scene().cameraActive(c);
        scene().cameraActive(c);
        z().camera(c);
        camera(c);


        //scene().add(cheval);

        final int i1 = FPS * SECONDS;
        double u = ((1.0 * frame() / i1) * TURNS);


        Point3D[] origins = new Point3D[3];

        Matrix33 matrixSphereVert = new Matrix33(new Point3D[]{origins[0], origins[1], origins[2]});

        Circle circle = earth.getCircle();
        circle.getAxis().getElem().getP1().setElem(axeVerticalVideo.mult(radius));
        circle.getAxis().getElem().getP2().setElem(axeVerticalVideo.mult(-radius));

        origins[0] = (axesSphereHorizontaux[0].mult(Math.cos(2 * Math.PI * u))
                .plus(axesSphereHorizontaux[1].mult(Math.sin(2 * Math.PI * u))));
        origins[1] = (axesSphereHorizontaux[0].mult(-Math.sin(2 * Math.PI * u))
                .plus(axesSphereHorizontaux[1].mult(Math.cos(2 * Math.PI * u))));
        origins[2] = (axeVerticalVideo);

        Matrix33 matrixRotationTerre = new Matrix33(new Point3D[]{origins[0], origins[1], origins[2]})
                .tild();


        Matrix33 formule2 = Matrix33.rotationX(2 * Math.PI * 23.4 / 360.0).mult(Matrix33.XYZ);

        Matrix33 formule3 = Matrix33.YZX.mult(Matrix33.rotationY(2 * Math.PI * frame() / getMaxFrames()));

        Point3D[] rowVectors = matrixRotationTerre.mult(formule2).mult(formule3).getColVectors();

        earth.setVectX(rowVectors[0]);
        earth.setVectY(rowVectors[1]);
        earth.setVectZ(rowVectors[2]);

        earth.setIncrU(0.003);
        earth.setIncrV(0.003);
        scene().clear();
        scene().add(earth);
        earth.setCircle(circle);
        circle.setCalculerRepere1(true);
        earth.setQuad_not_computed(0);
        Logger.getAnonymousLogger().log(Level.INFO, "Camera u : " + u);
    }


}
