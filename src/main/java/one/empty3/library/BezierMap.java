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

package one.empty3.library;

import one.empty3.library.core.nurbs.ParametricSurface;

public class BezierMap extends ParametricSurface {
    private StructureMatrix<ParametricSurface> surface;

    public BezierMap(ParametricSurface uvz) {
        surface = new StructureMatrix<>(0, ParametricSurface.class);
        surface.setElem(uvz);
    }

    @Override
    public Point3D calculerPoint3D(double u, double v) {
        //return super.calculerPoint3D(u, v);
        return surface.getElem().calculerPoint3D(u, v);
    }
}
