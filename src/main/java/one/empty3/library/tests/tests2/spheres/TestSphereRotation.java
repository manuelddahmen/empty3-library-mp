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


 author Manuel Dahmen _manuel.dahmen@gmx.com_

 Creation time 06-nov.-2014

 Updated 08/09/2015
 ***/


package one.empty3.library.tests.tests2.spheres;

import one.empty3.library.*;
import one.empty3.library.core.lighting.Colors;
import one.empty3.library.core.move.Trajectoires;
import one.empty3.library.core.testing.TestObjetSub;


/*__
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
public class TestSphereRotation extends TestObjetSub {
    Sphere ts;
    private static int N = 10;
    private Sphere[] spheres;

    public static void main(String[] args) {
        TestSphereRotation tsr = new TestSphereRotation();

        tsr.loop(true);
        tsr.setMaxFrames(300);
        tsr.setGenerate(GENERATE_IMAGE | GENERATE_MOVIE);
        tsr.unterminable(false);
        new Thread(tsr).start();


    }

    @Override
    public void ginit() {
        spheres = new Sphere[N];
        for (int i = 0; i < spheres.length; i++) {
            spheres[i] = new Sphere(Point3D.O0.plus(Point3D.random(1.0)), 10.0);
            Cube c = new Cube(0.7, spheres[i].getCircle().getCenter());

            spheres[i].texture(new TextureCol(Colors.random()));
            c.texture(new TextureCol(Colors.random()));


            scene().add(spheres[i]);
            scene().add(c);
        }
    }

    @Override
    public void testScene() throws Exception {
        Point3D sphere = Trajectoires.sphere(1.0 * frame() / getMaxFrames(),
                0.0, 100.0);
        scene().cameras().clear();
        scene().cameraActive(new Camera(sphere, Point3D.O0));

    }

    @Override
    public void finit() {

    }


}
