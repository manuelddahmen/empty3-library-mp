

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

package one.empty3.library.core.move;

import one.empty3.*;
import one.empty3.library.*;

public class Trajectoires {
    public static Point3D sphere(double longpc, double latpc, double radius) {
        return new Point3D(
                Math.cos(longpc * Math.PI) * Math.cos(Math.PI * (latpc)),
                Math.sin(longpc * Math.PI) * Math.cos(Math.PI * (latpc)),
                Math.sin(Math.PI * (latpc))
        ).mult(radius);

    }
    public static Point3D sphere(Point3D center, Point3D oo00, Point3D axe, double longpc, double latpc, double radius) {
        Point3D base = new Point3D(
                Math.cos(longpc * Math.PI) * Math.cos(Math.PI * (latpc)),
                Math.sin(longpc * Math.PI) * Math.cos(Math.PI * (latpc)),
                Math.sin(Math.PI * (latpc))
        ).mult(radius);
        Point3D x =axe.prodVect(oo00).norme1().prodVect(axe).norme1();
        Matrix33 matrix = new Matrix33(new Point3D[]{
          x, axe.prodVect(x), axe}
           
        );

        return matrix.mult(base).plus(center);
    }
}
 
