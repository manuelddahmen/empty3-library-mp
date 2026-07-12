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
import one.empty3.library.core.testing.*;
import one.empty3.library.core.tribase.Tubulaire3;

public class PerforageVertical extends TestObjetSub {
    public PerforageVertical() {
    }

    public void ginit() {
        TRI chatte = new TRI(Point3D.O0, Point3D.Z, Point3D.X);
        scene().add(chatte);
        Tubulaire3 queue = new Tubulaire3();
        queue.getSoulCurve().getElem().getCoefficients().getData1d().add(new Point3D(Math.sqrt(2), 0., Math.sqrt(2)));
        queue.getSoulCurve().getElem().getCoefficients().getData1d().add(new Point3D(Math.sqrt(2), 0., Math.sqrt(2)).plus(Point3D.Y));
        queue.getDiameterFunction().getElem().setFormulaX("0.4");
        scene().add(queue);
        Tubulaire3 jambe1 = new Tubulaire3();
        Tubulaire3 jambe2 = new Tubulaire3();
        jambe1.getSoulCurve().getElem().getCoefficients().getData1d().add(Point3D.O0);
        jambe1.getSoulCurve().getElem().getCoefficients().getData1d().add(Point3D.X);
        jambe1.getSoulCurve().getElem().getCoefficients().getData1d().add(Point3D.X.moins(Point3D.Y));
        jambe2.getSoulCurve().getElem().getCoefficients().getData1d().add(Point3D.O0);
        jambe2.getSoulCurve().getElem().getCoefficients().getData1d().add(Point3D.Z);
        jambe2.getSoulCurve().getElem().getCoefficients().getData1d().add(Point3D.Z.moins(Point3D.Y));
        scene().add(jambe1);
        scene().add(jambe2);
        Sphere couille1 = new Sphere(P.n(0.5, 1.0, 0.3), 0.3);
        Sphere couille2 = new Sphere(P.n(0.3, 1.0, 0.5), 0.3);
        scene().add(couille1);
        scene().add(couille2);
        Camera camera = new Camera(Point3D.Z.mult(-4.), Point3D.O0, Point3D.Y/*.plus(Point3D.X)*/);
        scene().cameras().add(camera);
        camera.declareProperties();
        scene().cameraActive(camera);

    }

    public static void main(String[] args) {
        PerforageVertical perforageVertical = new PerforageVertical();
        perforageVertical.setMaxFrames(100);
        perforageVertical.setDimension(TestObjet.VGA200);
        perforageVertical.setPublish(true);
        perforageVertical.setGenerate(GENERATE_MODEL | GENERATE_IMAGE | GENERATE_MOVIE);
        Thread thread = new Thread(perforageVertical);
        thread.start();

    }
}
