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

import one.empty3.library.Camera;
import one.empty3.library.LumierePonctuelle;
import one.empty3.library.Point3D;
import one.empty3.library.TextureCol;
import one.empty3.library.core.extra.Polyhedron;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;

/*__
 * @author Se7en
 */
public class TestTriHole extends TestObjetSub {
    private Polyhedron th;
    private Polyhedron th2;

    public TestTriHole() {
    }

    public static void main(String[] ar) {
        TestTriHole tth = new TestTriHole();

        tth.loop(true);
        tth.setResx(2000);
        tth.setResy(1500);

        tth.setMaxFrames(25 * 10);

        new Thread(tth).start();
    }

    @Override
    public void ginit() {
        th = new Polyhedron();

        th.texture(new TextureCol(one.empty3.libs.Color.newCol(0.0f,1.0f,0f)));

        scene().add(th);

        scene().cameraActive(new Camera(Point3D.Z.mult(-2d), Point3D.O0));
        LumierePonctuelle lumierePonctuelle = new LumierePonctuelle(Point3D.O0, one.empty3.libs.Color.newCol(0.0,0.0,1));
        lumierePonctuelle.setR0(1);

        scene().lumieres().add(lumierePonctuelle);

        lumierePonctuelle = new LumierePonctuelle(Point3D.Y, one.empty3.libs.Color.newCol(0,0,1));
        lumierePonctuelle.setR0(1);

        scene().lumieres().add(lumierePonctuelle);


        th2 = new Polyhedron();

        th2.texture(new TextureCol(one.empty3.libs.Color.newCol(1.0f,0.0f,0f)));

        scene().add(th2);


    }

    @Override
    public void testScene() throws Exception {
        th.add(new Point3D(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5));
        th2.add(new Point3D(Math.random() - 0.5, Math.random() - 0.5, Math.random() - 0.5));

        //exportFrame("stl", "Polyhedre"+frame()+".stl");

    }

    @Override
    public void finit() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
