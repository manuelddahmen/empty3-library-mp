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

import one.empty3.library.core.nurbs.CourbeParametriquePolynomialeBezier;
import one.empty3.library.core.nurbs.FctXY;
import one.empty3.library.core.nurbs.ParametricCurve;
import one.empty3.library.core.nurbs.ParametricSurface;

import java.util.logging.Level;
import java.util.logging.Logger;

public class T4 extends ParametricSurface {
    public double TAN_FCT_INCR = 0.000001;
    public double NORM_FCT_INCR = 0.000001;

    protected StructureMatrix<ParametricCurve> soulCurve = new StructureMatrix<>(0, ParametricCurve.class);
    protected StructureMatrix<BezierMap> diameterFunctionZ = new StructureMatrix<>(0, FctXY.class);
    protected Point3D lastNorm;
    protected Point3D lastTan = Point3D.Z;
    Point3D[][] vecteurs = new Point3D[3][3];
    private ITexture texture2;

    {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++) {
                vecteurs[i][j] = new Point3D(0., 0., 0.);
                for (int k = 0; k < 3; k++)
                    vecteurs[i][j].set(j, k == i ? 1. : 0.);
            }
    }

    public T4() {
        super();
        soulCurve.setElem(new CourbeParametriquePolynomialeBezier());
        diameterFunctionZ.setElem(new BezierMap(new Bezier2D()));
        declareProperties();
        this.quad_not_computed = 0;
    }

    public T4(ParametricCurve baseCurve, double rayonMembres) {
        this();
        this.soulCurve.setElem(baseCurve);
        this.diameterFunctionZ.setElem(new BezierMap(new ParametricSurface() {
            @Override
            public Point3D calculerPoint3D(double u, double v) {
                return Point3D.Z.mult(rayonMembres);
            }
        }));
        this.quad_not_computed = QUAD_NOT_COMPUTE_U2 | QUAD_NOT_COMPUTE_V2;
    }

    public Point3D calculerNormale(double t) {
        return calculerTangente(t + NORM_FCT_INCR).moins(calculerTangente(t)).mult(1.0 / NORM_FCT_INCR);
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
        String s = "T4 (\n\t("
                + soulCurve.getElem().toString();
        s += "\n\n)\n\t" + diameterFunctionZ.toString() + "\n\t" + texture().toString() + "\n)\n";
        return s;
    }

    private Point3D calculerTangenteUpart(double u, double v) {
        return soulCurve.data0d.calculerTangente(u);
    }

    private Object calculerTangenteVpart(double u, double v) {
        return calculerTangenteUpart(u, v).prodVect(calculerTangenteUpart(u + TAN_FCT_INCR, v)).norme1();//?????
    }

    public Point3D[] vectPerp(double t, double v) {
        int j = -1;
        double min = Double.POSITIVE_INFINITY;
        double minI = Double.POSITIVE_INFINITY; // TODO
        for (int i = 0; i < 3; i++) {
            Point3D tangente = calculerTangente(t);
            if (tangente.equals(Point3D.O0) || tangente.isAnyNaN()) {
                //TODO
                tangente = lastTan == null ? Point3D.X : lastTan;
            } else {
                lastTan = tangente;
            }


            Point3D[] refs = new Point3D[3];

            refs[0] = new Point3D(0d, 0d, 1d);
            refs[1] = new Point3D(1d, 0d, 0d);
            refs[2] = new Point3D(0d, 1d, 0d);

            tangente = tangente.norme1();

            Point3D px;
            Point3D normal;

            //normal = lastNorm;
            Point3D tangente1 = tangente;
            Point3D tangente2 = tangente.prodVect(refs[i]);
            normal = tangente1.prodVect(tangente2);
            if (normal != null) {
                if (Math.abs(normal.prodScalaire(tangente)) >= 0.00001) {
                    normal = calculerNormale(t);
                    if (normal.equals(Point3D.O0) || normal.isAnyNaN() || normal.norme() < 0.8) {
                        normal = tangente.prodVect(refs[i]);//TODO .prodVect(refs[i])).norme1();
                    }
                }
            } else {
                normal = lastNorm;
            }
            if (!normal.equals(Point3D.O0) && !normal.isAnyNaN() && !(normal.norme() < 0.8)) {
                lastNorm = normal;
            }
            normal = normal.norme1();
            px = tangente.prodVect(normal);//TODO .prodVect(refs[i])).norme1();

            Point3D py = tangente.prodVect(px).norme1();


            vecteurs[i][0] = tangente.norme1();
            vecteurs[i][1] = px.norme1();
            vecteurs[i][2] = py.norme1();

            minI = (px.prodVect(py).norme() - 1.0) * (px.prodVect(py).norme() - 1.0);

            if (minI < min) {
                min = minI;
                j = i;
            }
        }
        if (j == -1) {
            Logger.getAnonymousLogger().log(Level.INFO, "Error j==-1");
            j = 0;
        }

        return new Matrix33(vecteurs[j]).mult(new Matrix33(new Point3D[]{
                        getVectors().data1d.get(0),
                        getVectors().data1d.get(1),
                        getVectors().data1d.get(2)}))
                .getColVectors();
    }

    @Override
    public Point3D calculerPoint3D(double v, double u) {
        if (level == 0 && quad_not_computed > 0) {
            super.calculerPoint3D(v, u);
        }
        Point3D[] vectPerp = vectPerp(u, v);
        // Offsets point by cosine of scaled diameter
        Point3D z = diameterFunctionZ.getElem().calculerPoint3D(u, v);
        return transformVec(

                soulCurve.getElem().calculerPoint3D(u).plus(
                        vectPerp[1].mult(z.getZ() * Math.cos(2 * Math.PI * v))).plus(
                        vectPerp[2].mult(z.getZ() * Math.sin(2 * Math.PI * v)))

        );
    }

    /**
     * Declares properties; registers curve and diameter function
     */
    @Override
    public void declareProperties() {
        super.declareProperties();
        soulCurve.getElem().declareProperties();
        diameterFunctionZ.getElem().declareProperties();
        getDeclaredDataStructure().put("soulCurve/ame de la courbe", soulCurve);
        getDeclaredDataStructure().put("diameterFunctionZ/fonction de la longueur du diamètre", diameterFunctionZ);

    }

    public StructureMatrix<ParametricCurve> getSoulCurve() {
        return soulCurve;
    }


    public StructureMatrix<BezierMap> getDiameterFunctionZ() {
        return diameterFunctionZ;
    }


    @Override
    public void texture(ITexture tc) {
        super.texture(tc);
        this.texture2 = new ITexture() {
            @Override
            public int getColorAt(double u, double v) {
                return texture.getColorAt(v, u);
            }

            @Override
            public MatrixPropertiesObject copy() throws CopyRepresentableError, IllegalAccessException, InstantiationException {
                return texture2.copy();
            }
        };
        return;
    }

    /**
     * Calculates 3D point using curve, diameter, and surface
     */
    /*
    @Override
    public Point3D calculerPoint3D(double u, double v) {
        if (level == 0 && quad_not_computed > 0) {
            super.calculerPoint3D(v, u);
        }
        Point3D[] vectPerp = vectPerp(v, u);

        // Computes point offset by cosine scaled by height
        return transformVec(soulCurve.getElem().calculerPoint3D(u).plus(
                vectPerp[1].mult(diameterFunction.getElem().result(u) * Math.cos(2 * Math.PI * v)*
                        surfaceUV.getElem().heightDouble(u, v)
                )).plus(
                vectPerp[2].mult(diameterFunction.getElem().result(u) * Math.sin(2 * Math.PI * v)*
                        surfaceUV.getElem().heightDouble(u, v)
                )));
    }*/
    @Override
    public ITexture texture() {
        return texture2;
    }
}
