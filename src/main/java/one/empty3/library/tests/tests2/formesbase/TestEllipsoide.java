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
 * Global license :  *
 * Microsoft Public Licence
 * <p>
 * author Manuel Dahmen <manuel.dahmen@gmx.com>
 * <p>
 * *
 */
package one.empty3.library.tests.tests2.formesbase;

import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.core.testing.TestObjetSub;
import one.empty3.library.core.tribase.TRIEllipsoide;

/*__
 *
 * Meta Description missing
 * @author Manuel Dahmen dathewolf@gmail.com
 */
public class TestEllipsoide extends TestObjetSub {

    public static void main(String[] args) {

        new Thread(new TestEllipsoide())

                .start();

    }

    @Override
    public void ginit() {
        setGenerate(GENERATE_IMAGE | GENERATE_MODEL | GENERATE_MOVIE);
        setMaxFrames(1);
        loop(false);
    }

    @Override
    public void testScene() {
        TRIEllipsoide e = new TRIEllipsoide(Point3D.O0, 5.0, 2.0, 1.0);


        scene().add(e);

        scene().cameraActive(new Camera(Point3D.Z.mult(-10d), Point3D.O0));

    }
}
