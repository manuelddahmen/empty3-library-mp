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

/*
 * 2013 Manuel Dahmen
 */
package one.empty3.library.tests.tests2.dossierimages;

import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.TextureCol;
import one.empty3.library.TextureImg;
import one.empty3.library.core.testing.TestObjetSub;
import one.empty3.library.core.tribase.Plan3D;

import java.awt.*;
import java.io.File;

/*__
 * @author Se7en
 */
public class TestDossier extends TestObjetSub {

    private Double[][] coordCube = new Double[][]{{-1d, -1d, -1d, 1d}, {-1d, 1d, 1d, 1d}, {1d, 1d, 1d, -1d}, {1d, -1d, -1d, -1d}};
    private Double[][] coordCube2 = new Double[][]{{-1d, -1d, -1d, 1d}, {-1d, 1d, 1d, 1d}, {1d, 1d, 1d, -1d}, {1d, -1d, -1d, -1d}};
    private Dossier dos = new Dossier();

    public static void main(String[] args) {

        TestDossier td = new TestDossier();

        td.dos.limite = 12;
        if (args != null
                && args.length > 0
                && args[0] != null
                && !args[0].equals("")
                && new File(args[0]).exists()) {
            td.addDossier(new File(args[0]));
        } else {
            td.addDossier(new File("C:\\Users\\Se7en\\Pictures\\Textures\\873-8-tileable-metal-textures\\square-tiles"));
        }
        td.setResx(1000);

        td.setResy(1000);

        td.loop(false);

        td.setFileExtension("png");

        td.setMaxFrames(1);

        //td.setGenerate(GENERATE_OPENGL);
        td.run();
    }

    @Override
    public void ginit() {
        int size = dos.getImages().size();

        for (int i = 0; i < size; i++) {
            Plan3D plan3D = new Plan3D();


            plan3D.texture(new TextureImg(dos.getImages().get(i)));

            plan3D.pointOrigine(
                    Point3D.O0.plus(Point3D.Z.mult(i / 4d)
                            .plus(Point3D.X.mult(coordCube[(i % 4)][0]))
                            .plus(Point3D.Y.mult(coordCube[(i % 4)][1]))));
            plan3D.pointXExtremite(
                    Point3D.O0.plus(Point3D.Z.mult(i / 4d)
                            .plus(Point3D.X.mult(coordCube[(i % 4)][2]))
                            .plus(Point3D.Y.mult(coordCube[(i % 4)][3]))));

            plan3D.pointYExtremite(
                    plan3D.pointOrigine().plus(Point3D.Z)
            );

            scene().add(plan3D);

        }
    }

    public void addDossier(File f) {
        dos.addDossier(f);
    }

    @Override
    public void testScene() throws Exception {
        setCouleurFond(new TextureCol(Color.white));
        scene().cameraActive(new Camera(
                //Deplace.deplace(1, getMaxFrames(), Point3D.Z.mult(-30), Point3D.Z.mult(30), frame, getMaxFrames()),
                Point3D.O0,
                Point3D.Z.mult(20d)
        ));
    }

    @Override
    public void finit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

class Deplace {

    public static Point3D deplace(int framemin, int framemax, Point3D p0, Point3D p1, int framecurr, int nof) {

        return framecurr < framemin
                ? p0
                : (framecurr > framemax
                   ? p1
                   : p0.plus(p1.moins(p0).mult((1.0 * framecurr - framemin) / nof)));
    }
}
