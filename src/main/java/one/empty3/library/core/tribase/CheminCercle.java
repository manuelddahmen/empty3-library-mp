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
 * Global license :
 * <p>
 * Microsoft Public Licence
 * <p>
 * author Manuel Dahmen _manuel.dahmen@gmx.com_
 ***/


package one.empty3.library.core.tribase;

import one.empty3.*;
import one.empty3.library.*;

/*__
 * Meta Description missing
 * @author Manuel Dahmen dathewolf@gmail.com
 */
public class CheminCercle extends Chemin {
    private final double rayon;

    public CheminCercle(double r) {
        this.rayon = r;
    }

    @Override
    public double getLength() {
        return 2 * Math.PI * rayon;
    }

    @Override
    public Point3D calculerPoint3D(double t) {
        return new Point3D(
                Math.cos(2 * Math.PI * t),
                Math.sin(2 * Math.PI * t),
                0d)
                .mult(rayon);

    }

    @Override
    public Point3D calculerVitesse3D(double t) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
