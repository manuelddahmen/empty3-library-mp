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

package one.empty3.library.tests.tests2.borromeanRings;

import one.empty3.library.Point3D;
import one.empty3.library.core.nurbs.ParametricCurve;

/*__
 * Created by Win on 11-09-18.
 */
public class BorromeanRings extends ParametricCurve {
    private double a, b;

    public BorromeanRings() {
        a = 1;
        b = 2;
    }

    @Override
    public Point3D calculerPoint3D(double t) {
        return fct1(t);
    }

    @Override
    public Point3D calculerVitesse3D(double v) {
        return null;
    }

    Point3D fct1(double t) {
        assert b > a;
        Point3D p1 = new Point3D(a * Math.cos(t), b * Math.sin(t), 0d);
        Point3D p2 = new Point3D(0 * Math.cos(t), a * Math.cos(t), b * Math.sin((t)));
        Point3D p3 = new Point3D(b * Math.sin(t), 0d, a * Math.cos(t));

        return p1;
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }
}
