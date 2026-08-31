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
import one.empty3.library.core.nurbs.CourbeParametriquePolynomialeBezier;
import one.empty3.library.core.nurbs.FctXY;
import one.empty3.apps.testobject.Resolution;
import one.empty3.library.core.tribase.Tubulaire3;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;

public class TestBezierSans0 extends TestObjetSub {

    public void ginit() {
        setMaxFrames(18);
        z.setDisplayType(ZBufferImpl.DISPLAY_ALL);
        setDimension(HD1080);
        CourbeParametriquePolynomialeBezier patte = new CourbeParametriquePolynomialeBezier();

        patte.getCoefficients().setElem(new Point3D(0., -1., 0.), 0);
        patte.getCoefficients().setElem(new Point3D(0., 1., -1.), 2);
        patte.getCoefficients().setElem(new Point3D(0., 1., 0.), 1); //patte avant
        patte.getCoefficients().setElem(new Point3D(0., -1., -1.), 3);

        scene().add(patte);

        scene().lumieres().add(new LumierePonctuelle(new Point3D(0., 0., 2.), one.empty3.libs.Color.newCol(0,0,1)/*.YELLOW*/));
    }

    public void finit() {
        scene().cameras().clear();
        Camera c = new Camera(Point3D.X.mult(4.), Point3D.O0, Point3D.Z);
        scene().cameras().add(c);
        c.declareProperties();
        scene().cameraActive(c);
    }

    public static void main(String[] args) {
        TestBezierSans0 testBezierSans0 = new TestBezierSans0();
        testBezierSans0.setPublish(true);
        new Thread(testBezierSans0).start();
    }

}
