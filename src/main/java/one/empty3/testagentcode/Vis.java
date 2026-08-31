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

package one.empty3.testagentcode;

import one.empty3.library.Bezier;
import one.empty3.library.BezierMap;
import one.empty3.library.Point3D;
import one.empty3.library.core.tribase.Tubulaire5;
import one.empty3.library.core.nurbs.ParametricSurface;

import static java.lang.Math.cos;

public class Vis extends Tubulaire5 {
    private final double min;
    private final double max;
    private final int nSpires;

    public Vis(double min, double max, int nSpires, double hauteur) {
        this.min = min;
        this.max = max;
        this.nSpires = nSpires;
        this.getDiameterFunctionZ().setElem(new BezierMap(
                new ParametricSurface() {
                    public Point3D calculerPoint3D(double u, double v) {
                        return new Point3D(u, v, max - (u / nSpires *
                                Math.sqrt(Math.abs(cos(Math.PI * 2 * v)))) * (max - min));
                    }
                }
        ));
        this.getSoulCurve().setElem(new Bezier(new Point3D[]{Point3D.O0, Point3D.O0.add(new Point3D(1.0, 0.1, 0.0)), Point3D.O0.add(new Point3D(2.0, 0.0, 0.0))}));
    }

}
