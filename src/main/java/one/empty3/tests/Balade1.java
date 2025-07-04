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

package one.empty3.tests;

import one.empty3.apps.testobject.Resolution;
import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.*;
import one.empty3.library.core.nurbs.CourbeParametriquePolynomialeBezier;
import one.empty3.library.core.nurbs.FctXY;
import one.empty3.library.core.tribase.Tubulaire3refined;

import one.empty3.libs.Image;

import java.io.File;
import java.util.logging.Logger;

public class Balade1 extends TestObjetSub {

    private static final int VUE_1 = 75;
    private static final int FPS = 25;
    Tubulaire3refined tube = new Tubulaire3refined();

    public static void main(String[] args) {
        Balade1 balade1 = new Balade1();
        balade1.loop(true);
        balade1.setMaxFrames(VUE_1 * FPS);
        //balade1.setDimension(new Resolution(1920 / 8, 1080 / 8));
        //balade1.setDimension(new Resolution(320, 200));
        //balade1.setDimension(new Resolution(640, 480));
        balade1.setDimension(new Resolution(320, 240));
        balade1.setGenerate(GENERATE_IMAGE | GENERATE_SAVE_IMAGE|GENERATE_MOVIE|GENERATE_LOG|GENERATE_SAVE_ZIP);
        balade1.setPublish(true);
        new Thread(balade1).start();
    }

    @Override
    public void ginit() {

        super.ginit();
        ImageTexture sol_sableux;

        File f = new File(".\\res\\img\\planets\\carte-monde-vue-satellite.jpg");

        if(f.exists()) {
            Image i = null;
            i = new Image(f);
            sol_sableux = new ImageTexture(i);
        } else {
            throw new RuntimeException("file not exists or can't read");
        }

        tube = new Tubulaire3refined();
        tube.getSoulCurve().setElem(
                new CourbeParametriquePolynomialeBezier());

        for (int i = 0; i < 5; i++) {
            tube.getSoulCurve().getElem().getCoefficients().setElem(Point3D.random(10.0), i);
        }
        tube.getDiameterFunction().setElem(new FctXY() {
            @Override
            public double result(double input) {
                return 2.0;
            }
        });
        tube.setIncrU(0.01);
        tube.setIncrV(0.01);


        tube.texture(sol_sableux);

        Logger.getLogger(getClass().getCanonicalName()).info("texture at 0.5,0.5 : " + tube.texture().getColorAt(0.5,0.5));

        scene().add(tube);

        frame = 0;

        z().scene(scene());
        z().setDisplayType(ZBufferImpl.DISPLAY_ALL);
        //z().texture(new ColorTexture(0x00FF0000));
        int numFaces = 1;
        //double v = 1.0/Math.sqrt(1.0/(64.0 *z().la()*z().ha() / numFaces/Math.pow(surfaceBoundingCube, 2./3.)));
        double v = 2.0 * Math.pow(1.0 * z().la() * z().ha() * tube.getIncrU()* tube.getIncrV(), .5) + 1.0;
        if (v == Double.POSITIVE_INFINITY || v == Double.NEGATIVE_INFINITY || Double.isNaN(v) || v == 0.0) {
            v = ((double) (z().la() * z().ha())) / numFaces + 1;
        }
        z().setIncrementOptimizer(
                new ZBufferImpl.IncrementOptimizer(ZBufferImpl.IncrementOptimizer.Strategy.ENSURE_MAXIMUM_PERFORMANCE, .05)
        );
        /*z().setIncrementOptimizer(
                new ZBufferImpl.IncrementOptimizer(
                        ZBufferImpl..Strategy.IncrementOptimizer.Strategy.NONE, 1000.0
                )
        );*/
    }

    @Override
    public void finit() throws Exception {
        super.finit();

        if (frame() < VUE_1 * FPS) {
            Point3D a = tube.getSoulCurve().getElem().calculerPoint3D((frame() * 1.0) / getMaxFrames());
            Point3D b = tube.getSoulCurve().getElem().calculerPoint3D((frame() + 1.0) / getMaxFrames());

            Point3D y = tube.calculerPoint3D(0.25, 1.0 * frame() / getMaxFrames());
            Point3D ym = tube.calculerPoint3D(0.75, 1.0 * frame() / getMaxFrames());

            double ca = Math.max(1.0, 3.0 * (getMaxFrames() - frame() * 3) / getMaxFrames());
            ca = 1;
            Camera camera = new Camera(a.mult(ca), a.plus(b.moins(a).mult(1.0 / Point3D.distance(a, b))), y.moins(ym).mult(1.0 / Point3D.distance(y, ym)));

            //camera.getScale().setElem(100.0);
            scene().cameraActive(camera);

            //z().setDisplayType(Representable.DISPLAY_ALL);
            //z().texture(new ColorTexture(java.awt.Color.newCol(0f,0f,0f).getRGB()));
            StructureMatrix<Point3D> mat = new StructureMatrix<>(2, Point.class);
            mat.setElem(new Point3D(-10d, 0d, -10d), 0, 0);
            mat.setElem(new Point3D(10d, 0d, -10d), 1, 0);
            mat.setElem(new Point3D(10d, 0d, 10d), 1, 1);
            mat.setElem(new Point3D(-10d, 0d, 10d), 0, 1);

            Point3D[] vectors = new Point3D[]{mat.getElem(0, 0), mat.getElem(0, 1), mat.getElem(1, 0)};

            StructureMatrix<Point3D>[] v = new StructureMatrix[]{
                    new StructureMatrix<Point3D>(0, Point3D.class),
                    new StructureMatrix<Point3D>(0, Point3D.class),
                    new StructureMatrix<Point3D>(0, Point3D.class)};


            v[0].setElem(vectors[0]);
            v[1].setElem(vectors[1]);
            v[2].setElem(vectors[2]);

            StructureMatrix<Point3D>[] v1 = new StructureMatrix[]{
                    new StructureMatrix<Point3D>(0, Point3D.class),
                    new StructureMatrix<Point3D>(0, Point3D.class),
                    new StructureMatrix<Point3D>(0, Point3D.class)};


            v1[0].setElem(vectors[0].plus(Point3D.Y));
            v1[1].setElem(vectors[1].plus(Point3D.Y));
            v1[2].setElem(vectors[2].plus(Point3D.Y));


        }
    }

}
