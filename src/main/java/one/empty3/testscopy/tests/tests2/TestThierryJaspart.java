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

package one.empty3.testscopy.tests.tests2;

import one.empty3.library.*;
import one.empty3.library.core.nurbs.SurfaceParametricPolygonalBezier;
import one.empty3.library.core.nurbs.SurfaceParametriquePolynomialeBezier;
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
    private SurfaceParametriquePolynomialeBezier s = new SurfaceParametriquePolynomialeBezier(coeff);

    public TestThierryJaspart() {
        setMaxFrames(25 * 60 * 5);
    }


    @Override
    public void testScene(File f) {
    }

    @Override
    public void ginit() {
        z.setDisplayType(ZBufferImpl.DISPLAY_ALL);
        s.texture(texture);
        scene().add(s);
        Camera camera = new Camera(Point3D.Z.mult(10d), Point3D.O0, Point3D.Y);
        scene().cameraActive(camera);
        scene().cameraActive().setEye(Point3D.Z.mult(10d));
        try {
            texture = new ImageTexture(ECBufferedImage.getFromFile(
                    new File("res/img/IMG_20240510_152936.jpg")));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        s.setIncrU(0.1);
        s.setIncrV(0.1);
        s.texture(texture);
        setGenerate(getGenerate() | GENERATE_IMAGE | GENERATE_MOVIE);
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
