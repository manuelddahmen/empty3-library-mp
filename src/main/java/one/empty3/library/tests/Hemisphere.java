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

package one.empty3.library.tests;

import one.empty3.library.Axe;
import one.empty3.library.Point3D;
import one.empty3.library.Sphere;

public class Hemisphere extends Sphere {
    private Point3D north;

    public Hemisphere(Point3D center, double radius, Point3D north) {
        this(center, radius);
        this.north = north;
    }

    @Override
    public Point3D calculerPoint3D(double u, double v) {
        return super.calculerPoint3D(u, v / 2);
    }

    public Hemisphere() {
    }

    public Hemisphere(Axe axis, double radius) {
        super(axis, radius);
    }

    public Hemisphere(Point3D center, double radius) {
        super(center, radius);
    }
}
