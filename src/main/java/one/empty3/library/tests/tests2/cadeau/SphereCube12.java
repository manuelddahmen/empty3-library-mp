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

import one.empty3.library.*;
import one.empty3.library.core.testing.TestObjetSub;

import java.awt.*;

/*__
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
public class SphereCube12 extends TestObjetSub {
    private final double t0 = -1;
    private final double t1 = 1;
    double d = 90;
    private Sphere s;

    private Camera cam;

    public static void main(String[] args) {


        SphereCube12 sc = new SphereCube12();

        sc.setMaxFrames(500);

        sc.loop(true);

        new Thread(sc).start();


    }

    @Override
    public void ginit() {

        Cube c;

        //c.texture(new TextureCol(Color.RED));
        c = new Cube(d / 10, Point3D.O0);


        c.texture(new TextureCol(Color.BLUE));

        s = new Sphere(Point3D.X.mult(t0), d / 10);

        s.texture(new TextureCol(Color.YELLOW));

        scene().add(c);
        scene().add(s);

        double f = 3;
        cam = new Camera(s.getCircle().getCenter().mult(f), Point3D.O0);

        scene().cameraActive(cam);

        scene().lumieres().add(new LumierePonctuelle(Point3D.O0, Color.GREEN));
    }

    @Override
    public void finit() {

        double pc = 1.0 * frame() / getMaxFrames();

        double TT;
        TT = t0 + (t1 - t0) * pc;

        s.getCircle().getAxis().getElem().setCenter(Point3D.X.mult(TT * d));

        cam.setLookat(s.getCircle().getCenter());
        cam.setEye(Point3D.Z.mult(d / 3));
        cam.calculerMatrice(Point3D.Y);
    }

}
