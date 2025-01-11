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

package one.empty3.pointset;

import one.empty3.library.Point3D;

/*__
 * Created by manue on 16-07-19.
 */
public class ComposanteForceAttraction {


    public Point3D fun(Gravity t1, Gravity t2, double G, double powerD, double powerM1, double powerM2)
    {
        Point3D plus = t2.dv1.plus(t1.moins(t2).mult(G * Math.pow(t1.m, powerM1) * Math.pow(t2.m, powerM2) / (
                Math.pow(t1.moins(t2).norme(), powerD))));
        return plus;
    }
}
