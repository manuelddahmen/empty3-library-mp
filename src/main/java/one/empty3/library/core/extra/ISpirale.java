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

/*

 Vous êtes libre de :

 */
package one.empty3.library.core.extra;

import one.empty3.*;
import one.empty3.library.*;
import one.empty3.*;
import one.empty3.library.*;

public interface ISpirale {

    void addToScene(Scene sc);

    Point3D getObjectDeviation(Point3D position);

    Point3D getObjectDeviation(Point3D position, Point3D speed,
                               Point3D rotation);

    Point3D getObjectRotation(Point3D position);

    Point3D getObjectRotation(Point3D position, Point3D speed,
                              Point3D rotation);

    void rotate();

    void rotate(double deg);
}
