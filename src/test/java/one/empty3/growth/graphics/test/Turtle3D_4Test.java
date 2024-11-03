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

import one.empty3.growth.graphics.Turtle3D_4;
import one.empty3.*;
import one.empty3.library.*;
import one.empty3.growth.test.TestCaseExtended;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@RunWith(JUnit4.class)

public class Turtle3D_4Test extends TestCaseExtended {
    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public ZBuffer fct() {

        ZBuffer z = ZBufferFactory.instance(1600, 1200);

        z.backgroundTexture(new ColorTexture(new Color(90, 160, 50)));
        z.scene(new Scene());
        z.scene().cameraActive(new Camera(new Point3D(0., 0., -200.), new Point3D(0., 0., 0.)));
        //z.suivante();

        //  z.scene().cameraActive(z.camera());
        Turtle3D_4 turtle3D_4;
        turtle3D_4 = new Turtle3D_4(z);
        turtle3D_4.setColor(Color.BLACK);
        turtle3D_4.rotL(Math.PI / 2);
        turtle3D_4.line(100);
        turtle3D_4.rotU(Math.PI / 2);
        turtle3D_4.rotU(Math.PI / 2);
        turtle3D_4.line(100);
        turtle3D_4.rotU(Math.PI / 2);
        turtle3D_4.line(100);
        turtle3D_4.rotU(Math.PI / 2);
        turtle3D_4.line(100);


        return z;
    }

    public void test1() {
        ZBuffer z = fct();
        writeImage(z);
    }

    public void writeImage(ZBuffer z) {
        z.draw();
        try {
            java.io.File imageFile = getUniqueFilenameForProduction("testResults", getClass().getCanonicalName() + "___test1", "jpg");
            ImageIO.write(z.image(), "jpg", imageFile);
            Logger.getAnonymousLogger().log(Level.INFO, imageFile + " written");
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }
}
