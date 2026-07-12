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

import one.empty3.library.*;
import one.empty3.library.core.nurbs.SurfaceParametricPolygonalBezier;
import one.empty3.library.core.testing.TestObjetSub;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class TestThierryJaspart extends TestObjetSub {
    private final Point3D[][] coeff = new Point3D[][]{
            {Point3D.n(2, -2, 0), Point3D.n(2, -1, 0), Point3D.n(2, 0, 0), Point3D.n(2, 1, 0), Point3D.n(2, 2, 0)},
            {Point3D.n(1, -2, 0), Point3D.n(1, -1, 0), Point3D.n(1, 0, 0), Point3D.n(1, 1, 0), Point3D.n(1, 2, 0)},
            {Point3D.n(0, -2, 0), Point3D.n(0, -1, 0), Point3D.n(0, 0, 0), Point3D.n(0, 1, 0), Point3D.n(0, 2, 0)},
            {Point3D.n(-1, -2, 0), Point3D.n(-1, -1, 0), Point3D.n(-1, 0, 0), Point3D.n(-1, 1, 0), Point3D.n(-1, 2, 0)},
            {Point3D.n(-2, -2, 0), Point3D.n(-2, -1, 0), Point3D.n(-2, 0, 0), Point3D.n(-2, 1, 0), Point3D.n(-2, 2, 0)}
    };
    ITexture texture;
    private SurfaceParametricPolygonalBezier s = new SurfaceParametricPolygonalBezier(coeff);

    public TestThierryJaspart() {
        setMaxFrames(25 * 60 * 5);
    }


    @Override
    public void testScene(File f) {
    }

    @Override
    public void ginit() {
        s.texture(texture);
        scene().add(s);
        scene().cameraActive().setEye(Point3D.Z.mult(-5d));
        scene().cameraActive(new Camera(Point3D.Z.mult(-5d), Point3D.O0));
        try {
            texture = new TextureImg(ECBufferedImage.getFromFile(
                    new File("resources/img/2018-03-31 11.51.58.jpg")));
        } catch (Exception ex) {
        }

        s.setIncrU(0.1);
        s.setIncrV(0.1);
        s.texture(texture);
    }


    @Override
    public void testScene() {
        for (int i = 0; i < s.getCoefficients().getData2d().size(); i++)
            for (int j = 0; j < s.getCoefficients().getData2d().get(i).size(); j++) {
                Point3D point3D = Point3D.random2(0.1);
                for (int k = 0; k < 3; k++)
                    s.getCoefficients().getElem(i, j).set(k, s.getCoefficients().getElem(i, j).get(k) + point3D.get(k));
            }
    }


    @Override
    public void finit() throws Exception {
        super.finit();
        scene().texture(new TextureCol(Color.WHITE));

    }

    public static void main(String[] args) {
        new Thread(new TestThierryJaspart()).start();


    }
}
