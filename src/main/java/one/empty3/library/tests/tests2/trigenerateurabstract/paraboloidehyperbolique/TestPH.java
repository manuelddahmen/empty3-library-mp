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
import one.empty3.apps.testobject.TestObjet;
import one.empty3.library.core.tribase.ParaboloideHyperbolique;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;

/*__
 *
 * Meta Description missing
 * @author Manuel Dahmen dathewolf@gmail.com
 */
public class TestPH extends TestObjet {

    private ParaboloideHyperbolique ph = null;

    public static void main(String[] argd) {
        TestPH tth = new TestPH();

        tth.loop(true);
        tth.setMaxFrames(250);

        new Thread(tth).start();
    }

    @Override
    public void afterRenderFrame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void finit() {
        double angleU = Math.abs(Math.cos(2.0 * Math.PI * frame() / getMaxFrames() * 6));
        double angleV = Math.abs(Math.sin(2.0 * Math.PI * frame() / getMaxFrames() * 6));
        ph = new ParaboloideHyperbolique(angleU, angleV, 1);
        scene().add(ph);
        ph.texture(new TextureCol(one.empty3.libs.Color.newCol(1.0f,0.0f,0f)));
    }

    @Override
    public void ginit() {

    }

    @Override
    public void testScene() throws Exception {
        scene().cameraActive(new Camera(Point3D.Z.mult(-5d), Point3D.O0));
        scene().lumieres().add(new LumierePointSimple(one.empty3.libs.Color.newCol(1.0f,1.0f,1f), Point3D.X.plus(Point3D.Y), 1));
    }

    public void afterRender() {

    }

    @Override
    public void publishResult() {

    }
}
