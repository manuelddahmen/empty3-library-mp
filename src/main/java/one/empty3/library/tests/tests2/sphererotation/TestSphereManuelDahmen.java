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

/*__
 Global license :

 Microsoft Public Licence

 author Manuel Dahmen _manuel.dahmen@gmx.com_
 ***/


package one.empty3.library.tests.tests2.sphererotation;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.*;
import one.empty3.library.core.move.Trajectoires;
import one.empty3.library.core.tribase.TRISphere;
import one.empty3.libs.Image;

import one.empty3.libs.Image;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/*__
 * cette classe produit une image de sphère avec "Manuel Dahmen" écrit dessus. La sphère tourne
 * puis s'en va et revient à l'écran.
 *
 * Meta Description missing
 * @author Manuel Dahmen dathewolf@gmail.com
 */
public class TestSphereManuelDahmen extends TestObjetSub {
    double distance = 35;
    double rayon = 10;
    TRISphere tp = new TRISphere(Point3D.O0, rayon);

    public static void main(String[] args) {

        TestSphereManuelDahmen ts = new TestSphereManuelDahmen();

        ts.setGenerate(GENERATE_IMAGE | GENERATE_MOVIE);


        ts.loop(true);

        ts.setMaxFrames(500);

        new Thread(ts).start();
    }

    @Override
    public void ginit() {
        scene().add(tp);
        scene().cameraActive(new Camera(Point3D.Z.mult(distance), Point3D.O0));

        //tp.setRotation(tp.new Rotation(new Matrix33(new Point3D[] {Point3D.Z, Point3D.X, Point3D.Y}), Point3D.O0));

        TextureImg imageTexture = new TextureImg(new Image(Image.staticLoadFile(new File(getClass().getResource("map2.png").toString()))));
        imageTexture.setColorMask(ITexture.COLOR_MIROR_XY);
        tp.texture(imageTexture);
    }

    @Override
    public void testScene() throws Exception {

        scene().cameraActive().setEye(Matrix33.rot(90 / 360.0 * 2 * Math.PI, 0).mult(Trajectoires.sphere(0, 1.0 * frame() / 250, distance)));

    }

    @Override
    public void finit() {
    }
}
