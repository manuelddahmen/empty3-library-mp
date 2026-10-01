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

 Creation time 05-nov.-2014
 ***/


package one.empty3.library.tests.tests2.getobjectat;

import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.TRI;
import one.empty3.library.TextureCol;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;

/*__
 * Future: this should be.manudahmen.empty3.tests.tests2.be very low level: like :
 * Representable.setSelectionZoneId(long szid);
 * ZBuffer.getSelectionZoneId(int coordArr, int y);
 *
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
public class TestGetObjectAt extends TestObjetSub {

    @Override
    public void testScene() throws Exception {
        scene().getObjets().getData1d().clear();
        scene().add(new TRI(Point3D.O0, Point3D.X, Point3D.Y, new TextureCol(one.empty3.libs.Color.newCol(0,0,1))));
        scene().cameraActive(new Camera(Point3D.Z.mult(-1d), Point3D.O0));

    }

    @Override
    public void finit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void ginit() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
