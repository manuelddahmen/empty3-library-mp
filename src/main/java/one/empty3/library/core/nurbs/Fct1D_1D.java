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

package one.empty3.library.core.nurbs;

import one.empty3.library.Point3D;

/*__
 * Created by manue on 28-05-19.
 */
public abstract class Fct1D_1D extends ParametricCurve {
    public class I extends Fct1D_1D {

        public I() {

        }

        @Override
        public double result(double input) {
            return input;
        }
    }
    public class Fx extends Fct1D_1D {

        private final double x;

        public Fx(double x) {
            this.x = x;
        }

        @Override
        public double result(double input) {
            return x;
        }
    }
/*
    StructureMatrix<Fct1D_1D> predifinedFunctions = new StructureMatrix<>(1);
    {
        predifinedFunctions.add(1, new I());
        predifinedFunctions.add(1, new Fx(0.0));
    }
*/
public abstract double result(double input);

    @Override
    public Point3D calculerPoint3D(double t) {
        return new Point3D(t, result(t), 0d);
    }

    @Override
    public void declareProperties() {
        super.declareProperties();
    }
}
