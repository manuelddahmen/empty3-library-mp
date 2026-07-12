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
 * *
 * Global license :  *
 * Microsoft Public Licence
 * <p>
 * author Manuel Dahmen _manuel.dahmen@gmx.com_
 * <p>
 * *
 */
package one.empty3.library.tests.tests2.trigenerateurabstract.paraboloidehyperbolique;

import one.empty3.library.Camera;
import one.empty3.library.LumierePointSimple;
import one.empty3.library.Point3D;
import one.empty3.library.TextureCol;
import one.empty3.library.core.testing.TestObjet;
import one.empty3.library.core.tribase.ParaboloideHyperbolique;

import java.awt.*;

/*__
 *
 * Meta Description missing
 * @author Manuel Dahmen dathewolf@gmail.com
 */
public class TestPH1 extends TestObjet {

    private ParaboloideHyperbolique ph = null;

    public static void main(String[] argd) {
        TestPH1 tth = new TestPH1();

        tth.loop(true);
        tth.setMaxFrames(250);
        tth.setGenerate(GENERATE_IMAGE | GENERATE_MOVIE);
        new Thread(tth).start();
    }

    @Override
    public void afterRenderFrame() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void finit() {
        double a = Math.sqrt(frame() + 1);
        double b = Math.sqrt(frame() + 1);
        ph = new ParaboloideHyperbolique(a, b, 2);
        scene().add(ph);
        ph.texture(new TextureCol(Color.RED));
    }

    @Override
    public void ginit() {

    }

    @Override
    public void testScene() throws Exception {
        scene().cameraActive(new Camera(Point3D.Z.mult(4d).mult(new Point3D(1.0 * frame() / getMaxFrames() * 2 * Math.PI, 0d, 0d)), Point3D.O0));
        scene().lumieres().add(new LumierePointSimple(Color.WHITE, Point3D.X.plus(Point3D.Y), 1));
    }

    public void afterRender() {

    }
}
