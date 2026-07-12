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

package one.empty3.library.tests.tests2.tihange_reac_cendar;

import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.core.move.Trajectoires;
import one.empty3.library.core.testing.TestObjetSub;

public class TestTihange extends TestObjetSub {
    public static void main(String... args) {
        TestTihange testTihange = new TestTihange();
        testTihange.setMaxFrames(1);
        testTihange.setGenerate(GENERATE_MOVIE | GENERATE_IMAGE | GENERATE_MODEL);
        new Thread(testTihange).start();
    }

    public void finit() {
        scene().cameraActive(new Camera(
                Trajectoires.sphere(Math.random(), Math.random()
                        , 8),
                new Point3D(0d, 0d, 1d)));
        scene().add(new Tihange());
    }

    @Override
    public void testScene() throws Exception {
    }
}
