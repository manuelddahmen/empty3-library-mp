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
 * author Manuel Dahmen <manuel.dahmen@gmx.com>
 * <p>
 * Creation time 17-sept.-2014
 * <p>
 * *
 */
package one.empty3.library.tests.tests2.nurbs;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.core.nurbs.SurfaceParametricPolygonalBezier;

/*__
 *
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
public class SurfaceBezier extends TestObjetSub {

    public static void main(String[] arg) {
        SurfaceBezier t1 = new SurfaceBezier();
        t1.loop(false);
        new Thread(t1).start();
    }

    @Override
    public void testScene() {
        Point3D[][] p = new Point3D[9][9];
        int m = 0;
        for (int i = -4; i <= 4; i++) {
            int n = 0;
            for (int j = -4; j <= 4; j++) {
                p[m][n] = new Point3D((double) i, (double) j, 0d);
                n++;
            }
            m++;
        }
        SurfaceParametricPolygonalBezier surfaceParametricPolygonalBezier = new SurfaceParametricPolygonalBezier(p);
        boolean add = scene().add(surfaceParametricPolygonalBezier);
        scene().cameraActive(new Camera(Point3D.Z.mult(-10d), Point3D.O0));

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
