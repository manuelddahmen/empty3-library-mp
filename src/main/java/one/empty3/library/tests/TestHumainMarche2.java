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
import one.empty3.library.core.tribase.Tubulaire3;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;

public class TestHumainMarche2 extends TestObjetSub {
    private HumainMarche humainMarche;

    public void tubeAddPoint(Tubulaire3 tube, Point3D p) {
        tube.getSoulCurve().getElem().getCoefficients().getData1d().add(p);
    }

    public void ginit() {
        setMaxFrames(180);
        z.setDisplayType(ZBufferImpl.DISPLAY_ALL);
        scene().lumieres().add(new LumierePonctuelle(new Point3D(10., 10., 2.), one.empty3.libs.Color.newCol(0,0,1)));
        c = new Camera(new Point3D(0.0, 10.0, 30.0), Point3D.O0, Point3D.Y);
        humainMarche = new HumainMarche();
        scene().add(humainMarche);
    }

    public void finit() {
        z().idzpp();
        scene().cameras().clear();
        scene().cameras().add(c);
        c.declareProperties();
        scene().cameraActive(c);
        humainMarche.setT(frame() / 25.);
        humainMarche.init();
    }

    public static void main(String[] args) {
        TestHumainMarche2 testHumain = new TestHumainMarche2();
        testHumain.setPublish(true);
        testHumain.setGenerate(testHumain.getGenerate() | GENERATE_MODEL);
        new Thread(testHumain).start();
    }

}

