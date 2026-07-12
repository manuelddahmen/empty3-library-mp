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

package one.empty3.library.tests.tests2.borromeanRings;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.core.move.Trajectoires;

/*__
 * Created by Win on 11-09-18.
 */
public class TestBorromeanRings extends TestObjetSub {
    @Override
    public void finit() {
        camera(new Camera(Trajectoires.sphere(
                1.0 * frame() / getMaxFrames(), 0, 0.0001),
                Point3D.O0));
    }

    @Override
    public void ginit() {
        BorromeanRings br = new BorromeanRings();
        scene().add(br);
    }

    public static void main(String... args) {
        TestBorromeanRings tbr = new TestBorromeanRings();
        tbr.setMaxFrames(100);
        new Thread(tbr).start();
    }
}
