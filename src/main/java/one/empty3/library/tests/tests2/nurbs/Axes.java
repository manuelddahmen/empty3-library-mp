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

/*__
 Global license :

 CC Attribution

 author Manuel Dahmen <manuel.dahmen@gmx.com>
 ***/


package one.empty3.library.tests.tests2.nurbs;

import one.empty3.library.LineSegment;
import one.empty3.library.Point3D;
import one.empty3.library.RepresentableConteneur;
import one.empty3.library.TextureCol;

import java.awt.*;

/*__
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
class Axes extends RepresentableConteneur {
    public Axes(TextureCol a1, TextureCol a2, TextureCol a3) {
        add(new LineSegment(Point3D.O0, Point3D.X, a1));
        add(new LineSegment(Point3D.O0, Point3D.Y, a2));
        add(new LineSegment(Point3D.O0, Point3D.Z, a3));

    }

    public Axes() {
        this(new TextureCol(Color.RED), new TextureCol(Color.GREEN), new TextureCol(Color.BLUE));
    }
}
