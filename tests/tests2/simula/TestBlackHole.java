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

package one.empty3.library.tests.tests2.simula;/*
 * Copyright (c) 2016. Tous les fichiers dans ce programme sont soumis à la License Publique Générale GNU créée par la Free Softxware Association, Boston.
 * La plupart des licenses de parties tièrces sont compatibles avec la license principale.
 * Les parties tierces peuvent être soumises à d'autres licenses.
 * Montemedia : Creative Commons
 * ECT : Tests à valeur artistique ou technique.
 * La partie RayTacer a été honteusement copiée sur le Net. Puis traduite en Java et améliorée.
 * Java est une marque de la société Oracle.
 *
 * Pour le moment le programme est entièrement accessible sans frais supplémentaire. Get the sources, build it, use it, like it, share it.
 */
/*
package tests.tests2.simula;

import one.empty3.library.*;
import one.empty3.library.core.physics.Bille;
import one.empty3.library.core.physics.Move;
import one.empty3.library.core.tribase.TRISphere;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;

public class TestBlackHole extends TestObjetSub {
    int X = 10;
    int Y = 10;
    int Z = 10;
    Bille[] billes = new Bille[X * Y * Z];
    Move f = new Move();

    public static void main(String[] args) {

        TestBlackHole ttn = new TestBlackHole();

        ttn.setResx(640);
        ttn.setResy(480);
        ttn.loop(true);
        ttn.setMaxFrames(100000);
        ttn.publishResult(true);
        ttn.setFileExtension("png");

        ttn.run();

    }

    public void ginit() {

        for (int i = 0; i < X; i++) {
            for (int j = 0; j < Y; j++) {
                for (int k = 0; k < Z; k++) {

                    billes[k * Y * X + j * X + i] = new Bille();
                    billes[k * Y * X + j * X + i].position =
                            /*
                            new Point3D(
                            (i - X / 2) / 1f, (j - Y / 2) / 1f,
                            (k - Z / 2) / 1f).mult(Math.random()*1000);
                            Point3D.random(1000.0);
                    billes[k * Y * X + j * X + i].color = new Color(1.0f * i
                            / X, 1.0f * j / Y, 1.0f * k / Z);
                    billes[k * Y * X + j * X + i].masse = 100000;
                    billes[k * Y * X + j * X + i].attraction = 10;
                    billes[k * Y * X + j * X + i].repulsion = 0.1;
                    billes[k * Y * X + j * X + i].amortissement = 0.2;
                    billes[k * Y * X + j * X + i].vitesse = Point3D.O0;
                }
            }

        }
        f.setFusion(true);
        f.configurer(billes);

    }

    public void testScene() {
        scene().clear();

        f.calculer();
        f.setDistMinFusion(5 * f.getDistMin() / 4);


        RepresentableConteneur rc = new RepresentableConteneur();

        double distMin = f.getDistMin();

        for (int i = 0; i < X * Y; i++) {
            Representable r = new TRISphere(billes[i].position, distMin);

            ((TRISphere) r).setMaxX(5);
            ((TRISphere) r).setMaxY(5);

            r.texture(new TextureCol(billes[i].color));

            rc.add(r);
        }

        Camera camera = new Camera(
                f.centreMasse().plus(f.getDistMax() * 2), f.centreMasse());

        scene().cameraActive(camera);
        scene().add(rc);
    }

}
*/