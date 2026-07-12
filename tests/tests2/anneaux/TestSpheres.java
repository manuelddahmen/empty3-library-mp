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
import one.empty3.library.core.nurbs.CameraInPath;

import javax.imageio.ImageIO;
import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TestSpheres extends TestObjetSub {

    public static final int CIRCLES_COUNT = 10;
    public double step = 10000.0;

    public static void main(String... args) {
        TestSpheres testSpheres = new TestSpheres();
        testSpheres.setResolution(800, 800);
        testSpheres.setMaxFrames(24 * 30);
        new Thread(testSpheres).start();
    }

    @Override
    public void afterRenderFrame() {

    }

    @Override
    public void finit() {
        CameraInPath camera = new CameraInPath(new Circle(
                new Axe(Point3D.O0.plus(Point3D.X), Point3D.O0.moins(Point3D.X)), 400d));
        scene().add(camera);
        scene().cameraActive(camera);
        double t = 1.0 * frame() / (getMaxFrames());
        camera.setT(t);

        Point3D z = Point3D.O0.moins(camera.getCurve().calculerPoint3D(t)).norme1();
        Point3D x = camera.getCurve().tangente(t).norme1().mult(-1d);
        Point3D y = x.prodVect(z).norme1();
        camera.setMatrix(x, y, z);


    }

    @Override
    public void ginit() {
        scene().getObjets().data1d = new ArrayList<>();
        Sphere[] spheres = new Sphere[TestSpheres.CIRCLES_COUNT];
        for (int i = 0; i < spheres.length; i++) {
            Axe axe = new Axe(Point3D.random(100d), Point3D.random(100d));
            spheres[i] = new Sphere(axe,
                    100);
            spheres[i].texture(new TextureCol( one.empty3.libs.Color.newCol(255, 153, 28)));
            spheres[i].setIncrU(.01);
            spheres[i].setIncrV(.01);
            try {
                TextureImg imageTexture = new TextureImg(
                        new  one.empty3.libs.Image(
                                ImageIO.read(new File("./samples/img/herbe.jpg"))));

                spheres[i].texture(imageTexture);
            } catch (IOException e) {
                e.printStackTrace();
            }
            scene().add(spheres[i]);
        }
    }

    @Override
    public void afterRender() {

    }

    @Override
    public void testScene() throws Exception {

    }


    public void gc() {
        Runnable gc = () -> {
            while (true) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.gc();
            }
        };
        new Thread(gc).start();
    }
}
