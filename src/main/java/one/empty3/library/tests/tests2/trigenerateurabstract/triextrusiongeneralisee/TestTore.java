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


package one.empty3.library.tests.tests2.trigenerateurabstract.triextrusiongeneralisee;

import one.empty3.library.TextureCol;
import one.empty3.library.core.tribase.*;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;

/*__
 * Meta Description missing
 * @author Manuel Dahmen dathewolf@gmail.com
 */
public class TestTore extends TestObjetSub {
    public static void main(String[] args) {
        TestTore tp = new TestTore();
        tp.setGenerate(GENERATE_IMAGE | GENERATE_MODEL);
        tp.loop(false);
        new Thread(tp).start();
    }

    @Override
    public void ginit() {
        Surface s = new SurfaceCercle(1);
        Chemin c = new CheminCercle(10);

        TRIExtrusionGeneralisee tri = new TRIExtrusionGeneralisee();


        tri.texture(new TextureCol(one.empty3.libs.Color.newCol(1.0f,1.0f,1f)));

        scene().add(tri);
    }

    @Override
    public void finit() {
    }

    @Override
    public void testScene() throws Exception {
    }

}
