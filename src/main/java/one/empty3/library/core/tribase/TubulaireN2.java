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

import one.empty3.library.ColorTexture;
import one.empty3.library.Matrix33;
import one.empty3.library.Point3D;
import one.empty3.library.StructureMatrix;
import one.empty3.library.core.nurbs.CourbeParametriquePolynomialeBezier;
import one.empty3.library.core.nurbs.FctXY;
import one.empty3.library.core.nurbs.ParametricCurve;
import one.empty3.library.core.nurbs.ParametricSurface;

import one.empty3.libs.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TubulaireN2 extends ParametricSurface {
    public double TAN_FCT_INCR = 0.00000001;
    public double NORM_FCT_INCR = 0.00001;

    protected StructureMatrix<ParametricCurve> soulCurve = new StructureMatrix<>(0, ParametricCurve.class);
    protected StructureMatrix<FctXY> diameterFunction = new StructureMatrix<>(0, FctXY.class);
    private Point3D lastTan = null;

    public TubulaireN2() {
        super();
        soulCurve.setElem(new CourbeParametriquePolynomialeBezier());
        diameterFunction.setElem(new FctXY());
        declareProperties();
    }

    public TubulaireN2(ParametricCurve curve, double diameter) {
        soulCurve.setElem(curve);
        soulCurve.getElem().texture(new ColorTexture(Color.BLACK.getRGB()));
        FctXY fctXY = new FctXY();
        fctXY.setFormulaX("" + diameter);
        diameterFunction.setElem(fctXY);
        declareProperties();
    }

    public Point3D calculerNormale(double t) {
        Point3D n = calculerTangente(t + NORM_FCT_INCR).moins(calculerTangente(t)).mult(1.0 / NORM_FCT_INCR);
        if (n.equals(Point3D.O0) || n.isAnyNaN()) {
            int i = 0;
            while (i < 3 && (n.equals(Point3D.O0) ||
                    n.isAnyNaN())) {
                n = calculerTangente(t).prodVect(
                        new Point3D(i == 0 ? 1.0 : 0.0, i == 1 ? 1.0 : 0.0, i == 2 ? 1.0 : 0.0));

                i++;
            }

        }
        return n;
    }

    public Point3D[] calculerAxes(double t, Point3D tangent, int iMin) {

        Point3D n = new Point3D(2., 2., 2.).norme1();

        Point3D axe2;
        if (n.norme() != 1.0) {

            int i = 0;
            while (i < 3 && (Math.abs(n.dot(tangent)) > 1E-20 ||
                    n.isAnyNaN() || i < iMin)) {
                n = tangent.prodVect(new Point3D(i == 0 ? 1.0 : 0.0, i == 1 ? 1.0 : 0.0, i == 2 ? 1.0 : 0.0)).norme1();
                i++;
            }
        }

        axe2 = tangent.prodVect(n).norme1();

        return new Point3D[]{n, axe2};
    }

    public Point3D calculerTangente(double t) {
        return soulCurve.getElem().calculerPoint3D(t + TAN_FCT_INCR).moins(
                soulCurve.getElem().calculerPoint3D(t)).mult(1.0 / TAN_FCT_INCR);
    }

    public void nbrAnneaux(int n) {
        setIncrU(1.0 / n);
    }

    public void nbrRotations(int r) {
        setIncrV(1.0 / r);
    }

    @Override
    public String toString() {
        String s = "tubulaire3 (\n\t("
                + soulCurve.getElem().toString();
        s += "\n\n)\n\t" + diameterFunction.toString() + "\n\t" + texture().toString() + "\n)\n";
        return s;
    }

    public Point3D[] vecP(double t, double v) {
        Point3D[][] vecteurs = new Point3D[3][3];

        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                vecteurs[i][j] = new Point3D(0., 0., 0.);
                for (int k = 0; k < 3; k++)
                    vecteurs[i][j].set(j, k == i ? 1. : 0.);
            }

        Point3D tangente = Point3D.O0;
        int k = 0;
        while (tangente.equals(Point3D.O0) && k < 3) {
            tangente = calculerTangente(t).norme1();
            if (tangente.equals(Point3D.O0) || tangente.isAnyNaN() || (tangente.norme() < 1.0E-20)) {
                //TODO
                tangente =
                        lastTan == null ? new Point3D(k == 0 ? 1. : 0,
                                k == 1 ? 1. : 0, k == 2 ? 1. : 0)
                                : lastTan;
            } else {
                lastTan = tangente;
                break;
            }
            k++;
        }
        if (tangente.norme() == 0.0)
            Logger.getAnonymousLogger().log(Level.INFO, "Error in TubulaireN2 tangente==0");

        tangente = tangente.norme1();

        Point3D px = null;
        Point3D py = null;
        int j = 0;
        double min = 0.1d;
        double minI = 1000d; // TODO
        int iMin = 0;
        for (int i = 0; i < 3; i++) {

            Point3D[] perps = calculerAxes(t, tangente, i);
            px = perps[0];//TODO .prodVect(refs[i])).norme1();
            py = perps[1];


            vecteurs[i][0] = tangente.norme1();
            vecteurs[i][1] = px.norme1();
            vecteurs[i][2] = py.norme1();

            minI = Math.abs(px.dot(py));// Math.abs(px.prodVect(py).norme() - 1.);

            if (minI <= min) {
                min = minI;
                j = i;
            }

        }

        if (min >= 1.0)
            Logger.getAnonymousLogger().log(Level.INFO, "Error in TubulaireN2 vectPerp; px.cross.py.norme!=1");


        return vecteurs[j];
    }

    @Override
    public Point3D calculerPoint3D(double u, double v) {
        Point3D[] vecP = vecP(u, v);
        Point3D ret0 = soulCurve.getElem().calculerPoint3D(u).plus(
                vecP[1].mult(diameterFunction.getElem().result(u) * Math.cos(2 * Math.PI * v))).plus(
                vecP[2].mult(diameterFunction.getElem().result(u) * Math.sin(2 * Math.PI * v)));
        return getOrig().plus(getVectX().mult(ret0.get(0))).plus(getVectY().mult(ret0.get(1))).plus(getVectZ().mult(ret0.get(2)));
    }

    /*old
        @Override
        public Point3D calculerPoint3D(double u, double v) {
            Point3D[] vectPerp = vectPerp(u);
            return soulCurve.getElem().calculerPoint3D(u).plus(
                    vectPerp[1].mult(diameterFunction.getElem().result(u) * Math.cos(2 * Math.PI * v))).plus(
                    vectPerp[2].mult(diameterFunction.getElem().result(u) * Math.sin(2 * Math.PI * v)));
        }
    */
    @Override
    public void declareProperties() {
        super.declareProperties();
        soulCurve.getElem().declareProperties();
        diameterFunction.getElem().declareProperties();
        getDeclaredDataStructure().put("soulCurve/Ame de la courbe", soulCurve);
        getDeclaredDataStructure().put("diameterFunction/Fonction de positon sur la courbe - diamètre (var='x')", diameterFunction);

    }

    public StructureMatrix<ParametricCurve> getSoulCurve() {
        return soulCurve;
    }


    public StructureMatrix<FctXY> getDiameterFunction() {
        return diameterFunction;
    }


    public void setSoulCurve(ParametricCurve b) {
        this.soulCurve.setElem(b);
    }

    public void setDiameter(double d) {
        FctXY fctXY = new FctXY();
        fctXY.setFormulaX("" + d);
        this.diameterFunction.setElem(fctXY);
    }
}
