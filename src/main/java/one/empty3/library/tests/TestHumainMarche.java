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

import one.empty3.library.Polygon;
import one.empty3.library.*;
import one.empty3.library.core.nurbs.CourbeParametriquePolynomialeBezier;
import one.empty3.library.core.testing.TestObjetSub;
import one.empty3.library.core.tribase.Tubulaire3;

import java.awt.*;

public class TestHumainMarche extends TestObjetSub {
    public void tubeAddPoint(Tubulaire3 tube, Point3D p) {
        ((CourbeParametriquePolynomialeBezier) tube.getSoulCurve().getElem()).getCoefficients().getData1d().add(p);
    }

    public void ginit() {
        setMaxFrames(180);
        z.setDisplayType(ZBufferImpl.DISPLAY_ALL);
        scene().lumieres().add(new LumierePonctuelle(new Point3D(10., 10., 2.), Color.BLUE));
    }

    public void finit() {
        scene().cameras().clear();
        scene().getObjets().getData1d().clear();
        Camera c = new Camera2Quad(
                z(), new Polygon(new Point3D[]{
                new Point3D(-5., -5., -80.),
                new Point3D(5., -5., -80.),
                new Point3D(5., 5., -80.),
                new Point3D(-5., 5., -80.)},
                Color.BLUE),
                new Polygon(new Point3D[]{
                        new Point3D(-50., -50., 0.),
                        new Point3D(50., -50., 0.),
                        new Point3D(50., 50., 0.),
                        new Point3D(-50., 50., 0.)},
                        Color.BLUE)
        );
        scene().cameras().add(c);
        c.declareProperties();
        scene().cameraActive(c);

        HumainMarche humainMarche = new HumainMarche();
        scene().add(humainMarche);
        humainMarche.setT(frame() / 25.);
        humainMarche.init();
    }

    public static void main(String[] args) {
        TestHumainMarche testHumain = new TestHumainMarche();
        testHumain.setPublish(true);
        new Thread(testHumain).start();
    }

}

