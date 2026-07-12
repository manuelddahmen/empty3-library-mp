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

package one.empty3.library.tests.test3;

import one.empty3.library.Point3D;
import org.junit.Test;

import static org.junit.Assert.assertTrue;


/*__
 * Created by manue on 11-09-19.
 */
public class PointAlgebra {
    @Test
    public void testPoint3DInit() {
        //Double [] ds2 = new Double [] {1.4, 2.3, 3.1};
        double[] ds1 = new double[]{1.4, 2.3, 3.1};
        //assertTrue(new Point3D(ds1).equals(new Point3D(ds2)));
        assertTrue(new Point3D(ds1).equals(new Point3D(1.4, 2.3, 3.1)));
        assertTrue(!new Point3D(ds1).equals(Point3D.O0));
        assertTrue(new Point3D().equals(Point3D.O0));
        assertTrue(new Point3D(3).equals(Point3D.O0));
        assertTrue(new Point3D(1.0, 2.0, 3.0).mult(2.0).equals(new Point3D(2.0, 4.0, 6.0)));

    }

    @Test
    public void testPoint3D() {
        new Point3D();
    }
}
