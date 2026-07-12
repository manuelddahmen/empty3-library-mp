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

/*
 * 2013 Manuel Dahmen
 */
package one.empty3.library.tests.tests2.triangles;

import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.TRI;
import one.empty3.library.core.testing.TestObjetSub;

import java.awt.*;

public class TestTriangles extends TestObjetSub {

    private SiPiKi3D si;

    public TestTriangles() {
    }

    /*__
     * @param args
     */
    public static void main(String[] args) {
        TestTriangles tt = new TestTriangles();
        tt.loop(true);
        tt.setMaxFrames(10);
        new Thread(tt).start();
    }

    @Override
    public void ginit() {
        scene().getObjets().getData1d().clear();


        si = new SiPiKi3D(frame());


        si.add(new TRI(Point3D.O0, Point3D.X, Point3D.Y, Color.BLUE), frame);

        scene().add(si);

        scene().cameraActive(new Camera(new Point3D(0d, 0d, 2d), new Point3D(0d, 0d, 0d)));
    }

}
