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

package one.empty3.library.tests;

import one.empty3.library.*;
import one.empty3.library.core.nurbs.CourbeParametriquePolynomiale;
import one.empty3.library.core.nurbs.ExtrusionB1B1;
import one.empty3.library.core.testing.TestObjetSub;
import one.empty3.testscopy.tests.Hemisphere;

import java.awt.*;

public class TeteBonhomme extends RepresentableConteneur {
    public TeteBonhomme() {
        init();
    }

    public static void main(String[] args) {
        TestBonhomme testBonhomme = new TestBonhomme();
        testBonhomme.setMaxFrames(1);
        testBonhomme.camera(new Camera(P.n(-10., 0., 0.),
                P.n(0., 0., 0.)));
        testBonhomme.setPublish(true);
        new Thread(testBonhomme).start();
    }

    public void init() {
        Hemisphere hemisphere = new Hemisphere(Point3D.Z, 1.0, Point3D.Z);
        Hemisphere hemisphere2 = new Hemisphere(Point3D.Z.mult(-1.), 1.0, Point3D.Z.mult(-1.));
        ExtrusionB1B1 tubeExtrusion = new ExtrusionB1B1();
        hemisphere.texture(new ColorTexture(Color.YELLOW));
        hemisphere2.texture(new ColorTexture(Color.RED));
        tubeExtrusion.getBase().add(new Circle(Point3D.Z.mult(-1), Point3D.Z, 1.0));
        tubeExtrusion.getPath().add(new CourbeParametriquePolynomiale(new Point3D[]
                {Point3D.Z.mult(-1.), Point3D.Z}));
        tubeExtrusion.texture(new ColorTexture(Color.BLUE));

        add(hemisphere);
        add(tubeExtrusion);
        add(hemisphere2);

        assert Point3D.Z.equals(new Point3D(0d, 0d, 1d));
        assert Point3D.Y.equals(new Point3D(0d, 1d, 0d));
        assert Point3D.X.equals(new Point3D(1d, 0d, 0d));
    }

    public static class TestBonhomme extends TestObjetSub {
        TeteBonhomme teteBonhomme = new TeteBonhomme();

        @Override
        public void ginit() {
            scene().add(teteBonhomme);
            setMaxFrames(1);
        }
    }
}
