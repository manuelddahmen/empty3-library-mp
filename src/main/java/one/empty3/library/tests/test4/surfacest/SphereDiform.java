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

package one.empty3.library.tests.test4.surfacest;

import one.empty3.library.Axe;
import one.empty3.library.Circle;
import one.empty3.library.Point3D;
import one.empty3.library.Sphere;

public class SphereDiform extends Sphere {
    public SphereDiform() {
    }

    public SphereDiform(Axe axis, double radius) {
        super(axis, radius);
    }

    public SphereDiform(Point3D center, double radius) {
        super(center, radius);
    }

    @Override
    public Point3D calculerPoint3D(double u, double v) {
        for (Point3D point3D : getCircle().getVectors().data1d) {
            if (point3D == null)
                return Point3D.O0;
        }
        Circle c = getCircle();
        if (!c.isCalculerRepere1()) {
            c.calculerRepere1();
        }
        double cos = Math.cos(-Math.PI / 2 + Math.PI * v);
        Point3D multi = getVectX().mult(
                        Math.cos(2.0 * Math.PI * u) * cos).plus(
                        getVectY().mult(
                                Math.sin(2.0 * Math.PI * u) * cos))
                .plus(getVectZ().mult(Math.sin(-Math.PI / 2 + Math.PI * v) * Math.sin(2 * Math.PI * 6 * u)));
        if (multi.norme() <= Double.MIN_VALUE) {
            return c.getCenter();
        } else
            return c.getCenter().plus(multi.norme1().mult(c.getRadius() * (1 + u + v)));
    }
}
