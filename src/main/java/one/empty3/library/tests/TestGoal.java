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
import one.empty3.library.core.nurbs.ParametricSurface;
import one.empty3.library.core.testing.TestObjetSub;
import one.empty3.library.core.tribase.Tubulaire3;

import java.awt.*;

public class TestGoal extends TestObjetSub {
    int fps = 25;
    final Point3D pa = new Point3D(-20., 0., 0.);
    final Point3D pb = new Point3D(20., 0., 0.);
    private final int tempsTotal = 10;
    final Point3D v = pb.moins(pa).mult(1. / fps / tempsTotal);
    private Point3D current = pa;
    private Cube cubeA;
    private Cube cubeB;
    private final Color ca = Color.PINK;
    private final Color cb = Color.ORANGE;


    public void tubeAddPoint(Tubulaire3 tube, Point3D p) {
        tube.getSoulCurve().getElem().getCoefficients().getData1d().add(p);
    }

    public void ginit() {
        setMaxFrames(tempsTotal * 25);
        //z.setDisplayType(ZBufferImpl.SURFACE_DISPLAY_TEXT_TRI);
        z.setDisplayType(ZBufferImpl.DISPLAY_ALL);
        scene().lumieres().add(new Lumiere() {
            Point3D lp = new Point3D(0., 10., 100.);

            @Override
            public int getCouleur(int base, Point3D p, Point3D n) {
                Point3D baseCol = new Point3D(Lumiere.getDoubles(base));
                StructureMatrix<Double> coordArr = lp.moins(p).prodVect(n).norme1().prodVect(baseCol).getCoordArr();
                return Lumiere.getInt(new double[]{
                                (coordArr.getElem(0) * 0.5 + 0.5),
                                (coordArr.getElem(1) * 0.5 + 0.5),
                                (coordArr.getElem(2) * 0.5 + 0.5)
                        }
                );
            }
        });
    }

    public void finit() {
        scene().texture(new ColorTexture(Color.BLACK));
        z().texture(scene().texture());
        z().idzpp();
        scene().cameras().clear();
        scene().clear();

        cubeA = new Cube(20, pa);
        cubeA.texture(new ColorTexture(ca));
        TRIObject cubeA1 = cubeA.generate();
        cubeB = new Cube(20, pb);
        cubeB.texture(new ColorTexture(cb));
        TRIObject cubeB1 = cubeA.generate();


        scene().add(cubeA);
        scene().add(cubeB);

        scene().add(new LineSegment(pa, pb, new ColorTexture(Color.BLUE)));

        // Plus ligne
        current = pa.plus(pb.moins(pa).mult(1.0 * frame() / getMaxFrames()));

        Axe axe = new Axe(current.plus(Point3D.Y.mult(10.)),
                current.moins(Point3D.Y.mult(10.)));

        Sphere sphere = new Sphere(axe, 10.);
        sphere.texture(new ColorTexture(Color.WHITE));
        scene().add(sphere);

        Point3D eye = current.plus(Point3D.Z.mult(100));
        Camera camera = new Camera(eye, axe.getCenter(), Point3D.Y);
        camera.declareProperties();
        scene().cameraActive(camera);
        //camera.setMatrice(camera.getMatrice().tild());
    }

    public static void main(String[] args) {
        TestGoal testGoal = new TestGoal();
        testGoal.setResolution(Resolution.HD1080RESOLUTION.x(), Resolution.HD1080RESOLUTION.y());
        testGoal.setPublish(true);
        testGoal.setGenerate(testGoal.getGenerate() | GENERATE_MODEL);
        new Thread(testGoal).start();
    }

}

