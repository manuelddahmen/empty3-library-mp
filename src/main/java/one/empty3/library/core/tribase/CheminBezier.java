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

import one.empty3.*;
import one.empty3.library.*;
import one.empty3.*;
import one.empty3.library.*;

public class CheminBezier extends Chemin {

    private int n = 100;
    private BezierCubique sd;

    public CheminBezier(BezierCubique sd) {
        this.sd = sd;
    }

    public double getLength() {
        throw new UnsupportedOperationException("Longueur Bezier non mesurée");
    }


    @Override
    public Point3D calculerPoint3D(double t) {
        return sd.calculerPoint3D(t);
    }

    @Override
    public Point3D calculerVitesse3D(double t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
