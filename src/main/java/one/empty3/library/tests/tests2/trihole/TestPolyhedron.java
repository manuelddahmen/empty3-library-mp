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

package one.empty3.library.tests.tests2.trihole;

import one.empty3.library.*;
import one.empty3.library.core.extra.Polyhedron;
import one.empty3.library.core.testing.TestObjetStub;

import java.awt.*;

public class TestPolyhedron extends TestObjetStub {
    private Polyhedron polyhedron;

    @Override
    public void ginit() {
        z().setDisplayType(ZBufferImpl.SURFACE_DISPLAY_COL_TRI);

        z().setFORCE_POSITIVE_NORMALS(true);

        scene().cameraActive(new Camera(Point3D.Z.mult(-5),
                new Point3D(Point3D.O0), new Point3D(Point3D.Y)));


        scene().lumieres().add(new Lumiere() {
            @Override
            public int getCouleur(int base, Point3D p, Point3D n) {
                double[] doubles = Lumiere.getDoubles(base);
                return Lumiere.getInt(Point3D.toArray1d(new Point3D(doubles).prodVect(p.prodVect(n)).norme1(), doubles));
            }
        });


        polyhedron = new Polyhedron();

        polyhedron.texture(new TextureCol(Color.RED));

        scene().add(polyhedron);
    }

    public void finit() {
        polyhedron.clean();

        polyhedron.add(Point3D.random(2.0));

        polyhedron.steps();
    }

    public static void main(String[] args) {
        TestPolyhedron testPolyhedron = new TestPolyhedron();

        testPolyhedron.loop(true);

        testPolyhedron.setMaxFrames(30);

        new Thread(testPolyhedron).start();
    }
}
