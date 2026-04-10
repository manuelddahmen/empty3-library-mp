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

public class Box extends Parallelepiped {
    public Box(Point3D base, Point3D a, Point3D b, Point3D c, ITexture texture) {
        super(base, a, b, c, texture);
    }

    public Box(double a, double b, double c, ColorTexture texture) {
        super(a, b, c, texture);
    }

    public Box(double cubeSize, double cubeSize1, double cubeSize2) {
        this(cubeSize, cubeSize1, cubeSize2, null);
    }
}
