/*
 *
 *  *
 *  *  * Copyright (c) 2026. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2026 Manuel Daniel Dahmen
 *  *  *
 *  *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *    you may not use this file except in compliance with the License.
 *  *  *    You may obtain a copy of the License at
 *  *  *
 *  *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  *    Unless required by applicable law or agreed to in writing, software
 *  *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *    See the License for the specific language governing permissions and
 *  *  *    limitations under the License.
 *  *
 *  *
 *
 *
 *
 *  * Created by $user $date
 *
 *
 */

package one.empty3;

import one.empty3.library.Line;
import one.empty3.library.Point3D;
import one.empty3.library.core.nurbs.FctXY;
import one.empty3.library.core.tribase.T3D;

public class Cylinder extends T3D {
    public Cylinder(Point3D base, Point3D top, double radius) {
        getSoulCurve().setElem(new Line(base, top));
        getDiameterFunction().setElem(new FctXY() {
            public double result(double x) {
                return radius;
            }
        });
    }
}
