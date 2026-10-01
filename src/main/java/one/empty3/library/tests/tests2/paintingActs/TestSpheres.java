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

package one.empty3.library.tests.tests2.paintingActs;

import one.empty3.library.Point3D;
import one.empty3.library.TextureCol;
import one.empty3.apps.testobject.TestObjet;
import one.empty3.library.core.tribase.TRISphere;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;

/*__
 * Created by manue on 12-10-15.
 */
public class TestSpheres extends TestObjetSub {
    public static void main(String[] args) {
        TestObjet to = new TestSpheres();

        to.loop(true);
        to.unterminable(false);
        to.setGenerate(GENERATE_IMAGE | GENERATE_MOVIE);
        to.setMaxFrames(2000);

        new Thread(to).start();
    }

    public void ginit() {
        TRISphere sphere = new TRISphere(Point3D.O0, 10);
        sphere.texture(new TextureCol(one.empty3.libs.Color.newCol(0.0f,1.0f,0f)));
        scene().add(sphere);
        //TODO CHECK sphere.setPaintingAct(getZ(), scene(), new SpheresPA());
    }

    public void testScene() {

    }
}
