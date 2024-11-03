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

package one.empty3.growth.graphics.test;

import one.empty3.growth.test.TestCaseExtended;
import one.empty3.growth.graphics.Turtle3D;
import one.empty3.*;
import one.empty3.library.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.awt.*;

@RunWith(JUnit4.class)

public class Turtle3D_2Test extends TestCaseExtended {

    @Override
    public void setUp() throws Exception {
        super.setUp();

    }

    public ZBuffer fct() {
        ZBuffer z = ZBufferFactory.instance(1600, 1200);
        z.backgroundTexture(new ColorTexture(new Color(90, 160, 50)));
        z.scene(new Scene());
        Turtle3D turtle3D;
        Camera camera = new Camera(new Point3D(0., 0., -200.), new Point3D(0., 0., 0.));
        z.scene().cameraActive(camera);
        turtle3D = new Turtle3D(z);

        turtle3D.line(100);
        turtle3D.rotationTete(Math.PI / 2);
        turtle3D.setColor(Color.BLACK);
        turtle3D.rotationLargeur(Math.PI / 2);
        turtle3D.line(100);
        turtle3D.rotationTete(Math.PI / 2);
        turtle3D.line(100);
        turtle3D.rotationTete(Math.PI / 2);
        turtle3D.line(100);
        return z;
    }

    public void testSquaresXYZaxis() {
        writeImage(fct());

    }

    public void testSquaresXYZaxis_3() {
        ZBuffer z = ZBufferFactory.instance(1600, 1200);
        ColorTexture colorTexture = new ColorTexture(new Color(140, 50, 100));
        z.backgroundTexture(colorTexture);
        z.scene(new Scene());
        z.scene().cameraActive(new Camera(new Point3D(0., 0., -200.), new Point3D(0., 0., 0.)));
        Turtle3D turtle3D = new Turtle3D(z);

        turtle3D.setzBuffer(z);


        turtle3D.setColor(Color.BLACK);
        turtle3D.line(100);
        turtle3D.rotationLargeur(Math.PI / 2);
        turtle3D.line(100);
        turtle3D.rotationLargeur(Math.PI / 2);
        turtle3D.line(100);
        turtle3D.rotationLargeur(Math.PI / 2);
        turtle3D.line(100);
        turtle3D.rotationLargeur(Math.PI / 2);


        writeImage(turtle3D.getzBuffer());

    }

}
