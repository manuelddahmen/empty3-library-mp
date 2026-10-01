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
 * *
 * Global license : * GNU GPL v3
 * <p>
 * author Manuel Dahmen <manuel.dahmen@gmx.com>
 * <p>
 * Creation time 25-oct.-2014 SURFACE D'ÉLASTICITÉ DE FRESNEL Fresnel's
 * elasticity surface, Fresnelsche Elastizitätfläche
 * http://www.mathcurve.com/surfaces/elasticite/elasticite.shtml *
 */
package one.empty3.library.tests.tests2.coeur;

import one.empty3.library.Point3D;
import one.empty3.library.core.nurbs.ParametricSurface;

/*__
 *
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
public class SurfaceElasticite extends ParametricSurface {
    private double a;
    private double b;
    private double c;

    public SurfaceElasticite(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }


    @Override
    public Point3D calculerPoint3D(double u, double v) {
        double denom = (Math.pow(Math.sin(2 * Math.PI * v), 2) + c * c * Math.pow(Math.cos(2 * Math.PI * v), 2) * (Math.pow(Math.cos(2 * Math.PI * u), 2) / a / a + Math.pow(Math.sin(2 * Math.PI * u), 2) / b / b));
        double x, y, z;
        x = c * c / a * Math.cos(2 * Math.PI * u) * Math.cos(2 * Math.PI * v) / denom;
        y = c * c / b * Math.sin(2 * Math.PI * u) * Math.cos(2 * Math.PI * v) / denom;
        z = c * c / a * Math.sin(2 * Math.PI * v) / denom;
        return new Point3D(x, y, z);
    }

    @Override
    public Point3D calculerVitesse3D(double u, double v) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
