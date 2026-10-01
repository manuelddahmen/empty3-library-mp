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

/*__
 Global license :

 Microsoft Public Licence

 author Manuel Dahmen <manuel.dahmen@gmx.com>
 ***/


package one.empty3.library.tests.tests2.cadeau;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.Camera;
import one.empty3.library.Cube;
import one.empty3.library.Point3D;
import one.empty3.library.TextureCol;
import one.empty3.library.core.tribase.TRISphere;

import one.empty3.libs.*;

/*__
 * @author Manuel Dahmen <manuel.dahmen#gmx>
 */
public class SphereCube11 extends TestObjetSub {
    private final double t0 = -1;
    private final double t1 = 1;
    double d = 90;
    private TRISphere s;

    private double F = 3;
    private Camera cam;

    public static void main(String[] args) {


        SphereCube11 sc = new SphereCube11();

        sc.setMaxFrames(500);

        sc.loop(true);

        new Thread(sc).start();


    }

    @Override
    public void ginit() {

        Cube c;

        //c.texture(new TextureCol(one.empty3.libs.Color.newCol(1.0f,0.0f,0f)));
        c = new Cube(d / 10, Point3D.O0);


        c.texture(new TextureCol(one.empty3.libs.Color.newCol(0,0,1)));

        s = new TRISphere(Point3D.X.mult(t0), d / 10);

        s.texture(new TextureCol(one.empty3.libs.Color.newCol(0.0f,0.0f,1f)));

        scene().add(c);
        scene().add(s);

        cam = new Camera(s.getCentre().mult(F), Point3D.O0);

        scene().cameraActive(cam);
    }

    @Override
    public void testScene() throws Exception {

        double pc = 1.0 * frame() / getMaxFrames();

        double TT;
        TT = t0 + (t1 - t0) * pc;

        s.setCentre(Point3D.X.mult(TT * d));

        cam.setEye(s.getCentre().plus(Point3D.Y.mult(d / 9)));
    }

    @Override
    public void finit() {
    }

}
