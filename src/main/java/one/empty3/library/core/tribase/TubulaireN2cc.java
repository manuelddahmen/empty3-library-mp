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

/*__
 * *
 * Global license : * GNU GPL v3
 * <p>
 * author Manuel Dahmen _manuel.dahmen@gmx.com_
 * <p>
 * Creation time 2015-03-25
 * <p>
 * *
 */
package one.empty3.library.core.tribase;

import one.empty3.library.Point3D;
import one.empty3.library.core.nurbs.CourbeParametriquePolynomialeBezier;
import one.empty3.library.core.nurbs.Fct1D_1D;
import one.empty3.library.core.nurbs.ParametricCurve;
import one.empty3.library.core.nurbs.ParametricSurface;

public class TubulaireN2cc extends ParametricSurface {
    public  double TAN_FCT_INCR = 0.000001;
    public double NORM_FCT_INCR = 0.000001;

    private ParametricCurve soulCurve;
    private Fct1D_1D diameterFunction;

    public TubulaireN2cc()
    {
        super();
        soulCurve = new CourbeParametriquePolynomialeBezier();
        diameterFunction = new Fct1D_1D() {
            @Override
            public double result(double input) {
                return input;
            }
        };
    }
    public ParametricCurve getSoulCurve() {
        return soulCurve;
   }
    public TubulaireN2cc(ParametricCurve soulCurve, Fct1D_1D diameterCurve) {
        this();
        this.soulCurve = soulCurve;
        this.diameterFunction = diameterCurve;
    }

    public Point3D calculerNormale(double t) {
        return calculerTangente(t + NORM_FCT_INCR).moins(calculerTangente(t));
    }

    public Point3D calculerTangente(double t) {
        return soulCurve.calculerPoint3D(t + TAN_FCT_INCR).moins(soulCurve.calculerPoint3D(t));
    }

    public void nbrAnneaux(int n) {
        setIncrU(1.0 / n);
    }

    public void nbrRotations(int r) {
        setIncrV(1.0/r);
    }

    @Override
    public String toString() {
        String s = "tubulaireN2cc (\n\t("
                + soulCurve.toString();
        s += "\n\n)\n\t" + diameterFunction.toString() + "\n\t" + texture().toString() + "\n)\n";
        return s;
    }


    private Point3D[] vectPerp(double t) {
        Point3D[] vecteurs = new Point3D[3];

        Point3D p = soulCurve.calculerPoint3D(t);
        Point3D tangente = calculerTangente(t);

        Point3D ref1 = new Point3D(0d, 0d, 1d);
        Point3D ref2 = new Point3D(1d, 0d, 0d);
        Point3D ref3 = new Point3D(0d, 1d, 0d);

        tangente = tangente.norme1();

        if (tangente != null) {
            Point3D px = calculerNormale(t);///tangente.prodVect(ref1);

            if (px.norme() == 0) {
                px = tangente.prodVect(ref2);
            }
            if (px.norme() == 0) {
                px = tangente.prodVect(ref3);
            }

            Point3D py = px.prodVect(tangente);

            px = px.norme1();
            py = py.norme1();

            vecteurs[0] = tangente;
            vecteurs[1] = px;
            vecteurs[2] = py;

        }
        return vecteurs;
    }

    @Override
    public Point3D calculerPoint3D(double u, double v) {
        Point3D[] vectPerp = vectPerp(u);
        return soulCurve.calculerPoint3D(u).plus(
                vectPerp[1].mult(diameterFunction.result(u)*Math.cos(2 * Math.PI * v)).plus(
                        vectPerp[2].mult(diameterFunction.result(u)*Math.sin(2 * Math.PI * v))));
    }

    @Override
    public Point3D calculerVitesse3D(double u, double v) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void declareProperties() {
        super.declareProperties();
  throw new UnsupportedOperationException("Tubulaire N2 CC unsupported");
    }
}
