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

import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.TextureCol;
import one.empty3.library.core.move.Trajectoires;
import one.empty3.library.core.nurbs.CourbeParametriquePolynomialeBezier;
import one.empty3.apps.testobject.TestObjet;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class TestAnneau extends TestObjet {

    private int N = 20;
    List<Point3D> point3D = new ArrayList<>();
    private double latLat = 0.3;
    private CourbeParametriquePolynomialeBezier courbeParametriquePolynomialeBezier;

    @Override
    public void afterRenderFrame() {

    }

    @Override
    public void finit() {

    }


    @Override
    public void ginit() {
    }

    @Override
    public void afterRender() {

    }

    @Override
    public void publishResult() {

    }

    @Override
    public void testScene() throws Exception {
        double lgt = 0;
        for (int i = 0; ; i++) {
            Point3D sphere = Trajectoires.sphere(lgt, Math.random() / latLat, 1);
            point3D.add(sphere);
            lgt += Math.random() / N;
            if (lgt > 1) {
                lgt = 1;
                break;
            }
        }
        Point3D[] ds = new Point3D[this.point3D.size()];
        courbeParametriquePolynomialeBezier = new CourbeParametriquePolynomialeBezier(this.point3D.toArray(ds));
        scene().add(courbeParametriquePolynomialeBezier);

        courbeParametriquePolynomialeBezier.texture(new TextureCol(one.empty3.libs.Color.newCol(0,0,1)));
        courbeParametriquePolynomialeBezier.getParameters().setIncrU(0.0001);
        Camera camera = new Camera(Point3D.O0, Point3D.Z.mult(2d));
        camera.calculerMatrice(Point3D.Y);
        scene().cameraActive(camera);
    }


    public static void main(String... args) {
        TestAnneau testAnneau = new TestAnneau();
        Thread thread = new Thread(testAnneau);
        testAnneau.setMaxFrames(25 * 60 * 5);
        thread.start();
    }
}
