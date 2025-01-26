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

package one.empty3.facedetect.model.vecmesh;

import one.empty3.library.Point3D;
import one.empty3.library.StructureMatrix;
import one.empty3.library.core.nurbs.ParametricSurface;
import one.empty3.library1.shader.Vec;

public class Objects3D {
    public static ParametricSurface suv(/*ListInstructions listInstructions,*/ Vec vec3d) {
        return new ParametricSurface() {
            @Override
            public Point3D calculerPoint3D(double u, double v) {
                Point3D p = new Point3D();
                StructureMatrix<Double> vecVal = vec3d.getVecVal();
                double p0 = 0.0;
                for (int i = 0; i < 3; i++) {
                    if (vecVal.getData1d().size() > i) {
                        p0 = vecVal.getElem(i);
                    }
                    p.set(i, p0);
                }
                return p;
            }

        };
    }
}
