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

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.*;
import one.empty3.library.core.tribase.*;
import one.empty3.library.core.nurbs.*;
import one.empty3.library.core.testing.*;

import java.awt.Color;

import one.empty3.library.core.move.Trajectoires;

public class TestChien extends TestObjetSub {

    public void ginit() {
        setMaxFrames(18);
        z.setDisplayType(ZBufferImpl.DISPLAY_ALL);
        Tubulaire3[] patte = new Tubulaire3[4];
        Point3D tete = new Point3D(0., 1., 0.); //tête
        Point3D queue = new Point3D(1., 0., 1.); // queue
        Sphere tetes = new Sphere(tete, 0.8); //sphère
        tetes.texture(new TextureCol(one.empty3.libs.Color.newCol(1.0f,0.0f,0f)));
        queue.texture(new TextureCol(one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f)));

        for (int i = 0; i < 4; i++) {
            patte[i] = new Tubulaire3();
            patte[i].getSoulCurve().getElem().getCoefficients().getData1d().clear();
            patte[i].texture(new TextureCol( one.empty3.libs.Color.newCol(255, 153, 28)));
            ((FctXY) (patte[i].getDiameterFunction().getElem())).setFormulaX("0.6");
        }
        Tubulaire3 corps;
        corps = new Tubulaire3();
        corps.getSoulCurve().getElem().getCoefficients().getData1d().clear();
        corps.getSoulCurve().getElem().getCoefficients().setElem(P.n(0., 1., 0.));
        corps.getSoulCurve().getElem().getCoefficients().setElem(P.n(1., 1., 0.));
        corps.texture(new TextureCol( one.empty3.libs.Color.newCol(255, 153, 28)));
        ((FctXY) (corps.getDiameterFunction().getElem())).setFormulaX("1.5");
// patte AVANT
// §1
        ((CourbeParametriquePolynomialeBezier) (patte[0].getSoulCurve().getElem())).getCoefficients().setElem(new Point3D(0., -1., 0.), 0);
        ((CourbeParametriquePolynomialeBezier) (patte[0].getSoulCurve().getElem())).getCoefficients().setElem(new Point3D(0., 1., 0.), 1); //patte avant
        ((CourbeParametriquePolynomialeBezier) (patte[0].getSoulCurve().getElem())).getCoefficients().setElem(new Point3D(0., 1., -1.), 2);
        ((CourbeParametriquePolynomialeBezier) (patte[0].getSoulCurve().getElem())).getCoefficients().setElem(new Point3D(0., -1., -1.), 3);
// patte arrière
// §2
        ((CourbeParametriquePolynomialeBezier) (patte[2].getSoulCurve().getElem())).getCoefficients().setElem(new Point3D(1., -1., 0.), 0);
        ((CourbeParametriquePolynomialeBezier) (patte[2].getSoulCurve().getElem())).getCoefficients().setElem(new Point3D(1., 1., 0.), 1); //patte arrière #1
        ((CourbeParametriquePolynomialeBezier) (patte[2].getSoulCurve().getElem())).getCoefficients().setElem(new Point3D(1., 1., -1.), 2);// patte avant #2
        ((CourbeParametriquePolynomialeBezier) (patte[2].getSoulCurve().getElem())).getCoefficients().setElem(new Point3D(1., -1., -1.), 3);
        //1,0,0 //etx queue.

        scene().add(corps);
        scene().add(tetes);
        for (int i = 0; i < 4; i += 2) {
            scene().add(patte[i]);

        }
        scene().lumieres().add(new LumierePonctuelle(new Point3D(0., 0., 2.), one.empty3.libs.Color.newCol(0,0,1)/*.YELLOW*/));
    }

    public void finit() {
        scene().cameras().clear();
        Camera c = new Camera(Point3D.Z.mult(-4.), Point3D.O0, Point3D.Y);
        scene().cameras().add(c);
        c.declareProperties();
        scene().cameraActive(c);
    }

    public static void main(String[] args) {
        new Thread(new TestChien()).start();
    }

}
