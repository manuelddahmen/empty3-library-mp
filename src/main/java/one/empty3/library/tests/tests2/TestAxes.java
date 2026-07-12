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

package one.empty3.library.tests.tests2;

import one.empty3.library.Camera;
import one.empty3.library.Matrix33;
import one.empty3.library.Point3D;
import one.empty3.library.TextureCol;
import one.empty3.library.core.testing.TestObjetSub;
import one.empty3.library.core.tribase.Plan3D;

import java.awt.*;

public class TestAxes extends TestObjetSub {

    public static void main(String[] args) {
        TestAxes testAxes = new TestAxes();
        testAxes.loop(true);
        testAxes.setMaxFrames(3);
        new Thread(testAxes).start();

    }

    @Override
    public void testScene() {
        Plan3D planX = new Plan3D();
        Plan3D planY = new Plan3D();
        Plan3D planZ = new Plan3D();
        planX.pointOrigine(Point3D.O0);
        planX.pointXExtremite(Point3D.X);
        planX.pointYExtremite(Point3D.Y.mult(0.3));
        planX.texture(new TextureCol(Color.RED));
        planY.pointOrigine(Point3D.O0);
        planY.pointXExtremite(Point3D.Y);
        planY.pointYExtremite(Point3D.Z.mult(0.3));
        planY.texture(new TextureCol(Color.GREEN));
        planZ.pointOrigine(Point3D.O0);
        planZ.pointXExtremite(Point3D.Z);
        planZ.pointYExtremite(Point3D.X.mult(0.3));
        planZ.texture(new TextureCol(Color.BLUE));
        scene().add(planX);
        scene().add(planY);
        scene().add(planZ);
        Camera camera = new Camera();
        camera.setEye(Point3D.Z.mult(-1d));
        if (frame() == 0) {
            camera.imposerMatrice(new Matrix33(
                    new Double[]
                            {
                                    1d, 0d, 0d,
                                    0d, 1d, 0d,
                                    0d, 0d, 1d
                            }
            ));
        } else if (frame() == 1) {
            camera.imposerMatrice(new Matrix33(
                    new Double[]
                            {
                                    0d, 0d, 1d,
                                    1d, 0d, 0d,
                                    0d, 1d, 0d
                            }
            ));
        } else if (frame() == 2) {
            camera.imposerMatrice(new Matrix33(
                    new Double[]
                            {
                                    0d, 1d, 0d,
                                    0d, 0d, 1d,
                                    1d, 0d, 0d
                            }
            ));
        }

        camera.calculerMatrice(null);
        scene().cameraActive(camera);
    }

}
