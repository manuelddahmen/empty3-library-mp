/*
 *
 *  *
 *  *  * Copyright (c) 2025. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2024 Manuel Daniel Dahmen
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

package one.empty3.library.core.nurbs;

import one.empty3.*;
import one.empty3.library.*;


/*__
 * Created by manuel on 29-06-17.
 */
public class ParametricLine extends ParametricCurve implements CurveElem{
    private final LineSegment lineSegment;

    public ParametricLine(LineSegment sd) {
        this.lineSegment = sd;
    }

    @Override
    public Point3D calculerPoint3D(double t) {
        return lineSegment.getOrigine().plus(lineSegment.getExtremite().moins(lineSegment.getOrigine().mult(t)));
    }

    @Override
    public Point3D calculerVitesse3D(double t) {
        return lineSegment.getOrigine().plus(lineSegment.getExtremite().moins(lineSegment.getOrigine())).norme1();
    }
}
