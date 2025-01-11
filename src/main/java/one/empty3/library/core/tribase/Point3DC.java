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

package one.empty3.library.core.tribase;

import one.empty3.library.Point3D;
import one.empty3.library.StructureMatrix;
import one.empty3.library.core.nurbs.ParametricCurve;

/*__
 * Created by manue on 23-07-19.
 */
public class Point3DC extends ParametricCurve {
    private StructureMatrix<Point3D> o;

    public Point3DC()


    {
        super();
        this.o.setElem(new Point3D(Point3D.O0));
        getDeclaredDataStructure().put("o/Point::Curve", o);
    }

    public Point3DC(Point3D o0) {
        this();
        o.setElem(o0);

    }

    @Override
    public Point3D calculerPoint3D(double t) {
        return o.getElem();
    }
}
