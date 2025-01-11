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

package one.empty3.library.objloader;

import one.empty3.library.Point3D;
import one.empty3.library.StructureMatrix;
import one.empty3.library.core.nurbs.ParametricSurface;

public class PolygonalSurface extends ParametricSurface {
    private StructureMatrix<Point3D> controls = new StructureMatrix<Point3D>();

    public PolygonalSurface(StructureMatrix<Point3D> s) {
        this.controls = s;
    }


    public StructureMatrix<Point3D> getControls() {
        return controls;
    }

    public void setControls(StructureMatrix<Point3D> controls) {
        this.controls = controls;
    }

    @Override
    public Point3D calculerPoint3D(double u, double v) {
        return null;
    }

    @Override
    public void declareProperties() {
        super.declareProperties();
        getDeclaredDataStructure().put("controls/Points de contrôles (délimatiation des polygônes)", controls);
    }


}
