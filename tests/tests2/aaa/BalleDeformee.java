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

package one.empty3.library.tests.tests2.aaa;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.*;
import one.empty3.library.core.nurbs.CameraInPath;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/*__
 * Created by manue on 22-06-19.
 */
public class BalleDeformee extends TestObjetSub {
    private CameraInPath camera;
    private Balle balle;

    public static void main(String[] args) {
        BalleDeformee ba = new BalleDeformee();
        ba.setMaxFrames(25 * 60);
        ba.loop(true);
        new Thread(ba).start();
    }

    @Override
    public void finit() throws Exception {
        double t = 1.0 * (frame() % (25 * 4));
        camera.setT(t);

        Point3D z = Point3D.O0.moins(camera.getCurve().calculerPoint3D(t)).norme1();
        Point3D x = camera.getCurve().tangente(t).norme1().mult(-1d);
        Point3D y = x.prodVect(z).norme1();
        camera.setMatrix(x, y, z);
    }

    @Override
    public void ginit() {
        balle = new Balle(new Axe(Point3D.Y.mult(1d), Point3D.Y.mult(-1d)), 100);
        balle.setFormula(Balle.formula3);
        balle.setIncrU(0.001);
        balle.setIncrV(0.001);
        try {
            balle.texture(
                    new TextureOpSphere(
                            new TextureImg(
                                    new  one.empty3.libs.Image(
                                            ImageIO.read(
                                                    new File("samples/img/pink-watercolor-texture.jpg"))))));
        } catch (Exception ex) {
        }


        scene().add(balle);
        camera = new CameraInPath(new Circle(
                new Axe(Point3D.O0.plus(Point3D.Y), Point3D.O0.moins(Point3D.Y)), 800 * 35));
        scene().cameraActive(camera);
    }

    public void testScene() throws Exception {
        balle.bounce();
    }


}
