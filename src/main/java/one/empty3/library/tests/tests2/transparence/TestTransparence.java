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

 author Manuel Dahmen <manuel.dahmen@gmx.com>
 ***/


package one.empty3.library.tests.tests2.transparence;

import one.empty3.library.Camera;
import one.empty3.library.ECBufferedImage;
import one.empty3.library.Point3D;
import one.empty3.library.TextureImg;
import one.empty3.library.core.testing.TestObjetSub;
import one.empty3.library.core.tribase.Plan3D;

import javax.imageio.ImageIO;

/*__
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
public class TestTransparence extends TestObjetSub {

    public static void main(String[] ar) {
        TestTransparence tth = new TestTransparence();

        tth.loop(false);
        tth.setResx(2000);
        tth.setResy(1500);

        new Thread(tth).start();
    }

    @Override
    public void testScene() throws Exception {
        Plan3D plan3D = new Plan3D();
        plan3D.pointOrigine(new Point3D(-1d, -1d, 0d));
        plan3D.pointXExtremite(new Point3D(1d, 0d, 0d));
        plan3D.pointYExtremite(new Point3D(0d, 1d, 0d));
        TextureImg TextureCol = new TextureImg(new ECBufferedImage(ImageIO.read(getClass().getResourceAsStream("be.manudahmen.empty3.tests.tests2.cubes-be.manudahmen.empty3.tests.tests2.transparence.png"))));
        //TextureCol.setTransparent(Color.GREEN);
        plan3D.texture(TextureCol);

        scene().add(plan3D);
        scene().cameraActive(new Camera(Point3D.Z.mult(3d), Point3D.O0));

    }
}
