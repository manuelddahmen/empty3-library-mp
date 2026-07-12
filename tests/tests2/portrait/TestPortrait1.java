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
 * *
 * Global license : * Microsoft Public Licence
 * <p>
 * author Manuel Dahmen <manuel.dahmen@gmx.com>
 * <p>
 * *
 */
package one.empty3.library.tests.tests2.portrait;

import one.empty3.library.*;
import one.empty3.library.core.tribase.TRISphere;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;

/*__
 *
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
public class TestPortrait1 extends TestObjetSub {

    public static void main(String[] args) {
        TestPortrait1 tp = new TestPortrait1();
        tp.loop(false);
        new Thread(tp).start();
    }

    @Override
    public void testScene() throws Exception {

        for (int i = -2; i <= 2; i++) {
            for (int j = -2; j <= 2; j++) {
                Cube c1 = new Cube(1.0, new Point3D(i * 2d, 0.5, j * 2d), new TextureCol(one.empty3.libs.Color.newCol(1.0f,1.0f,1f)));
                scene().add(c1);
            }
        }

        TRISphere ts = new TRISphere(new Point3D(0d, 1.5, 0d), 2d);

        ts.texture(new TextureCol(one.empty3.libs.Color.newCol(1.0f,1.0f,1f)));

        scene().add(ts);

        Camera c = new Camera(new Point3D(0d, 3d, -20d), Point3D.O0);

        scene().cameraActive(c);
        scene().lumieres()
                .add(
                        new LumierePonctuelle(new Point3D(0d, 20d, 20d),  one.empty3.libs.Color.newCol(255, 153, 28)));
        scene().lumieres()
                .add(
                        new LumierePonctuelle(new Point3D(20d, 20d, 20d), Color.magenta));
    }

    @Override
    public void finit() {
    }

    @Override
    public void ginit() {
    }
}
