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

package one.empty3.library.tests.tests2.pieuvre;

import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.core.testing.TestObjetSub;

import java.awt.*;


public class TestPieuvre extends TestObjetSub {
    private Pieuvre pieuvre;

    public void ginit() {
        this.pieuvre = new Pieuvre(10,
                Color.YELLOW);
        scene().add(pieuvre);
        scene().cameraActive(new Camera(Point3D.Z.mult(2d), Point3D.O0));
    }

    private double time() {
        return 1.0 * frame() / getMaxFrames() * 25.0;
    }

    public void testScene() {
        pieuvre.setT(time());
    }

    public static void main(String[] args) {
        TestPieuvre testPieuvre = new TestPieuvre();
        testPieuvre.setMaxFrames(10000);
        new Thread(testPieuvre).start();

    }
}
