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

package one.empty3.library.tests.tests2.mammaires;

import one.empty3.library.Point3D;
import one.empty3.library.core.nurbs.ParametricSurface;

/*__
 * Created by manuel on 23-05-17.
 */
public class mammaires {
    static class Sein1 extends ParametricSurface {
        @Override
        public Point3D calculerPoint3D(double u, double v) {
            Point3D p = new Point3D(
                    Math.cos(Math.PI * 2 * u) * Math.sin(Math.PI + Math.PI * v),
                    v,
                    Math.sin(Math.PI * 2 * u) * Math.sin(Math.PI + Math.PI * v));
            p.texture(this.texture());
            return p;
        }

        @Override
        public Point3D calculerVitesse3D(double v, double v1) {
            return null;
        }
    }

    /*__
     * Spheric Ball
     */
    static class Sein2 extends ParametricSurface {
        @Override
        public Point3D calculerPoint3D(double u, double v) {
            Point3D p = new Point3D(
                    Math.cos(Math.PI * 2 * u) * Math.sin(Math.PI + Math.PI * v),
                    Math.cos(Math.PI * v),
                    Math.sin(Math.PI * 2 * u) * Math.sin(Math.PI + Math.PI * v));
            p.texture(this.texture());
            return p;
        }

        @Override
        public Point3D calculerVitesse3D(double v, double v1) {
            return null;
        }
    }

    /*__
     *
     */
    static class Sein3 extends ParametricSurface {
        @Override
        public Point3D calculerPoint3D(double u, double v) {
            Point3D p = new Point3D(
                    Math.cos(Math.PI * 2 * u) * Math.sin(Math.PI + Math.PI * v),
                    v * v,
                    Math.sin(Math.PI * 2 * u) * Math.sin(Math.PI + Math.PI * v));
            p.texture(this.texture());
            return p;
        }

        @Override
        public Point3D calculerVitesse3D(double v, double v1) {
            return null;
        }
    }

}
