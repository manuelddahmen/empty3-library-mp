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

package one.empty3.library.tests.tests2.anneaux;

import one.empty3.library.*;
import one.empty3.library.core.lighting.Colors;
import one.empty3.library.core.move.Trajectoires;
import one.empty3.library.core.testing.TestObjetSub;

import java.util.logging.Level;
import java.util.logging.Logger;

/*__
 * Created by Win on 28-08-18.
 */
public class TestCircles extends TestObjetSub {

    public static final double CIRCLES_COUNT = 100;
    private static final double MAX_SIZE = 100;
    private static int INCR_MAX;

    private Circle[] circles;

    public TestCircles() {
        INCR_MAX = (int) ((getResx() * getResy())
                / MAX_SIZE / MAX_SIZE / 25.0
                /
                camera().getEye().moins(camera().getLookat()).norme());
    }


    @Override
    public void afterRenderFrame() {

    }

    @Override
    public void finit() {
        scene().cameras().clear();
        int mod = getMaxFrames() / 4;
        double longpc = (frame() % mod) / 1.0 / mod;
        Logger.getAnonymousLogger().log(Level.INFO, "Longitude= " + longpc);
        scene().cameraActive(new Camera(
                Trajectoires.sphere(
                        longpc,
                        0,
                        200
                ), Point3D.O0));
    }

    @Override
    public void ginit() {
        scene().getObjets().getData1d().clear();
        circles = new Circle[(int) CIRCLES_COUNT];
        for (int i = 0; i < circles.length; i++) {
            circles[i] = new Circle(new Axe(Point3D.random(MAX_SIZE), Point3D.random(MAX_SIZE)),
                    MAX_SIZE);
            circles[i].texture(new TextureCol(Colors.random()));
            circles[i].getParameters().setIncrU(0.01);
            scene().add(circles[i]);
            Logger.getAnonymousLogger().log(Level.INFO, "Center: " + circles[i].getCenter());
        }
    }

    @Override
    public void testScene() throws Exception {
        for (int i = 0; i < circles.length; i++) {
            Point3D axe = circles[i].getAxis().getElem().getP1().getElem();
            axe.changeTo(axe.plus(Point3D.random(MAX_SIZE / 10)));

            axe = circles[i].getAxis().getElem().getP2().getElem();
            axe.changeTo(axe.plus(Point3D.random(MAX_SIZE / 10)));
        }
    }

    public static void main(String... args) {
        TestCircles testCircles = new TestCircles();
        testCircles.setMaxFrames(4000);
        testCircles.setResolution(1600, 1200);
        new Thread(testCircles).start();
    }
}
