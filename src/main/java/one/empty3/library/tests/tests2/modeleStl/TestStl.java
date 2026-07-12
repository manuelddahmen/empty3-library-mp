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

package one.empty3.library.tests.tests2.modeleStl;

import one.empty3.library.*;
import one.empty3.library.core.lighting.Colors;
import one.empty3.library.core.testing.TestObjetSub;
import one.empty3.library.stl_loader.IncorrectFormatException;
import one.empty3.library.stl_loader.ParsingErrorException;
import one.empty3.library.stl_loader.StlFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestStl extends TestObjetSub {
    private BufferedReader reader;

    @Override
    public void ginit() {
        super.ginit();


        z().setDisplayType(ZBufferImpl.DISPLAY_ALL);

        StlFile file = new StlFile();
        Scene load = new Scene();
        try {
            File file1 = new File("samples/stl/another_nude_girl-ascii.stl");
            load = file.load(file1.getAbsolutePath());
        } catch (IncorrectFormatException | IOException | ParsingErrorException e) {
            e.printStackTrace();
        }


        scene().add(load.getObjets().getElem(0));

        Logger.getAnonymousLogger().log(Level.INFO, "scene objets= " + load.getObjets().data1d.size());

        TextureCol colorTexture0 = new TextureCol(Colors.random());
        for (int i = 0; i < ((RepresentableConteneur) scene().getObjets().getElem(0)).getListRepresentable().size(); i++) {
            TRI t = (TRI) ((RepresentableConteneur) scene().getObjets().getElem(0)).getListRepresentable().get(i);
            t.texture(colorTexture0);
        }
        load.getObjets().getElem(0).texture(colorTexture0);
        //Logger.getAnonymousLogger().log(Level.INFO, ((RepresentableConteneur) scene().getObjets().getElem(0)).getListRepresentable().size());

        Sphere s = new Sphere(new Axe(new Point3D(0., -1., 0.),
                new Point3D(0., 1., 0.)), 1.0);
        s.texture(new TextureCol(Colors.random()));

        scene().add(s);

        scene().cameraActive(new Camera());

        Camera camera = new Camera(Point3D.Y.mult(-50), Point3D.O0, Point3D.Z);

        //camera.calculerMatrice(Point3D.Y);

        scene().cameraActive(camera);

        scene().lumieres().add(new Lumiere() {
            @Override
            public int getCouleur(int base, Point3D p, Point3D n) {
                double dot = (n.norme1().dot(p.norme1()));
                double[] pc = Lumiere.getDoubles(base);
                return Lumiere.getInt(
                        new double[]{dot * pc[0], dot * pc[1], dot * pc[2]});
            }
        });
    }


    public static void main(String[] args) {
        TestStl stl = new TestStl();
        stl.setMaxFrames(1);
        stl.setResolution(1000, 1000);
        new Thread(stl).start();
    }

}
