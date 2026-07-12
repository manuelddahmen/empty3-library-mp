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
public class SphereCube extends TestObjetSub {
    private final double t0 = -1;
    private final double t1 = 1;
    double d = 90;
    private Sphere s;
    private Cube c;

    public static void main(String[] args) {


        SphereCube sc = new SphereCube();

        sc.setMaxFrames(300);

        sc.setPublish(true);

        sc.loop(true);

        new Thread(sc).start();


    }

    @Override
    public void ginit() {

        //c.texture(new TextureCol(Color.RED));
        c = new Cube(d / 10, Point3D.O0);


        c.texture(new TextureCol(Color.BLUE));

        s = new Sphere(Point3D.X.mult(t0), d / 10);

        s.texture(new ColorTexture(Color.GREEN));

        scene().add(c);
        scene().add(s);

        scene().texture(new ColorTexture(Color.BLACK));
        z().texture(new ColorTexture(Color.BLACK));

        scene().cameraActive(new Camera());
    }

    @Override
    public void testScene() throws Exception {

        double pc = 1.0 * frame() / getMaxFrames();

        double TT;
        TT = t0 + (t1 - t0) * pc;
        s.getCircle().getAxis().getElem().setCenter(Point3D.X.mult(TT * d));

    }

    @Override
    public void finit() {
        Point3D mult = c.getPosition().getElem().plus(s.getCircle().getCenter()).mult(0.5).prodVect(Point3D.Y);
        Point3D zCam = Point3D.Y.mult(mult.norme());
        scene().cameraActive().setEye(zCam);
        scene().cameraActive().setLookat(mult);
    }

}
