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

package one.empty3.library.tests.tests2.simula;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.*;
import one.empty3.library.core.physics.Bille;
import one.empty3.library.core.physics.Force;
import one.empty3.libs.Color;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestGravity2 extends TestObjetSub {
    int X = 3;
    int Y = 3;
    int Z = 3;
    Bille[] billes = new Bille[X * Y * Z];
    Force f = new Force();
    double vOriginal = 0.001;

    public static void main(String[] args) {

        TestGravity2 ttn = new TestGravity2();

        ttn.loop(true);
        ttn.setMaxFrames(10000);
        ttn.publishResult(true);

        new Thread(ttn).start();

    }

    public void ginit() {
        billes = new Bille[X * Y * Z];

        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                for (int k = 0; k < Z; k++) {

                    billes[k * Y * X + j * X + i] = new Bille();
                    billes[k * Y * X + j * X + i].position = new Point3D(
                            (i - X / 2.), (j - Y / 2.),
                            (k - Z / 2.)).mult(Math.random() * vOriginal);
                    billes[k * Y * X + j * X + i].vitesse = new Point3D(
                            (i - X / 2.), (j - Y / 2.) / 1d,
                            (k - Z / 2.) / 1d).mult((Math.random() - 0.5) * 10.0);
                    billes[k * Y * X + j * X + i].color = Color.newCol(1f,0f,0f);
                    billes[k * Y * X + j * X + i].masse = 100;
                    billes[k * Y * X + j * X + i].attraction = 1e10;
                    billes[k * Y * X + j * X + i].repulsion = 0.0;
                    billes[k * Y * X + j * X + i].amortissement = 0.0;
                }
            }

        }
        f = new Force();
        f.setDt(100);
        f.setG(1000.0);

        ArrayList<Bille> billes1 = new ArrayList<>();
        for (int i = 0; i < billes.length; i++) {
            billes1.add(billes[i]);
        }

        f.configurer(billes1);
    }

    public void testScene() {
        scene().clear();


        f.calculer();


        RepresentableConteneur rc = new RepresentableConteneur();

        //Polyhedron polyhedron = new Polyhedron();
        for (int i = 0; i < X * Y * Z; i++) {
            Point3D position = f.getCourant().get(i).position;
            Point3D pA = position.plus(Point3D.X.mult(position.norme()));
            Point3D pB = position.moins(Point3D.X.mult(-position.norme()));
            Axe axe = new Axe(pA, pB);
            Representable r = new Sphere(axe, f.dMin(i) / 5);

//            ((TRISphere) r).setMaxX(7);
//            ((TRISphere) r).setMaxY(7);

            r.texture(new TextureCol(billes[i].color));

            rc.add(r);

            //polyhedron.add(billes[i].position);
        }

        Camera camera = new Camera(f.centreMasse().plus(
                new Point3D(0d, 0d, f.getDistMax())), f.centreMasse(), Point3D.Y);

        Logger.getAnonymousLogger().log(Level.INFO, "" + rc.getListRepresentable().size());

        scene().cameraActive(camera);

        scene().add(rc);

    }

    @Override
    public void finit() {
    }

}
