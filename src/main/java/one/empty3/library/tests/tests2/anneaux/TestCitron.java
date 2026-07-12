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

package one.empty3.library.tests.tests2.anneaux;

import one.empty3.library.*;
import one.empty3.library.core.move.Trajectoires;
import one.empty3.library.core.testing.Resolution;
import one.empty3.library.core.testing.TestObjetSub;

import java.awt.*;
import java.io.IOException;


public class TestCitron extends TestObjetSub {

    public static final int CIRCLES_COUNT = 4;
    public double step = 10000.0;
    private double DIM = 100;
    private Citron[] citrons;

    public static void main(String... args) {
        TestCitron testCitron = new TestCitron();
        testCitron.setMaxFrames(1000);
        testCitron.setDimension(new Resolution(100, 100));
        new Thread(testCitron).start();
    }

    @Override
    public void ginit() {
        Axe axe;
        Point3D pA = Point3D.random(50d);
        Point3D pB = pA.mult(-1d);
        axe = new Axe(pA, pB);
        scene().clear();
        citrons = new Citron[CIRCLES_COUNT];
        for (int i = 0; i < citrons.length; i++) {
            citrons[i] = new Citron(axe,
                    Trajectoires.sphere(
                            1.0 * frame() / getMaxFrames(), 0.0, DIM),
                    DIM * 4);
            citrons[i].texture(new TextureCol(Color.ORANGE));
            try {
                citrons[i].texture(new TextureImg(ECBufferedImage.getFromFile(new java.io.File("samples/img/herbe.jpg"))));
            } catch (IOException e) {
                e.printStackTrace();
            }
            scene().add(citrons[i]);
        }
        scene().cameraActive(new Camera(Point3D.Z.mult(DIM * 2), Point3D.O0));

        //scene().lumieres().add(new LumierePointSimple(Color.BLUE, Point3D.O0, 100));
    }

    public void finit() {
        for (int i = 0; i < CIRCLES_COUNT; i++) {
            Axe axe;
            Point3D sphere = Trajectoires.sphere(
                    1.0 * frame() / getMaxFrames(), 0.0, DIM);
            Point3D pB = sphere.mult(-1d);
            axe = new Axe(sphere, pB);
            citrons[i].getCircle().getAxis().setElem(axe);
        }
    }
}
