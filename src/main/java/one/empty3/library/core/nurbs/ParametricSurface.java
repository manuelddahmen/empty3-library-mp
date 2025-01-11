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
 * Global license : * Microsoft Public Licence
 * <p>
 * author Manuel Dahmen _manuel.dahmen@gmx.com_
 * <p>
 * *
 */
package one.empty3.library.core.nurbs;

import one.empty3.library.Point3D;
import one.empty3.library.Polygon;
import one.empty3.library.Representable;
import one.empty3.library.StructureMatrix;

/*__
 * Meta Description missing
 * @author Manuel Dahmen dathewolf@gmail.com
 */
public abstract class ParametricSurface extends Representable {

    public static final int QUAD_NOT_COMPUTE_U2 = 1;
    public static final int QUAD_NOT_COMPUTE_V2 = 2;
    private static final double MIN_NORMGT0 = 0.000000001;
    private static final double TANGENT_INCR = 0.00000001;
    protected int quad_not_computed = 3;
    //    private static Globals globals;
    protected StructureMatrix<Point2Point> terminalU = new StructureMatrix<>(0, Point2Point.class);
    protected StructureMatrix<Point2Point> terminalV = new StructureMatrix<>(0, Point2Point.class);
    protected Point3D[] vectorsBak;
    protected int level = 0;
    private StructureMatrix<Double> incrU = new StructureMatrix<>(0, Double.class);
    private StructureMatrix<Double> incrV = new StructureMatrix<>(0, Double.class);
    private StructureMatrix<Double> incrVitesse = new StructureMatrix<>(0, Double.class);
    private StructureMatrix<Double> incrNormale = new StructureMatrix<>(0, Double.class);
    private StructureMatrix<Double> startU = new StructureMatrix<>(0, Double.class);
    private StructureMatrix<Double> endU = new StructureMatrix<>(0, Double.class);
    private StructureMatrix<Double> startV = new StructureMatrix<>(0, Double.class);
    private StructureMatrix<Double> endV = new StructureMatrix<>(0, Double.class);

    {
        terminalU.setElem(new Point2Point() {
            @Override
            public Point3D result(Point3D p) {
                return new Point3D(p.get(0), p.get(1), p.get(2));
            }
        });
        terminalV.setElem(new Point2Point() {
            @Override
            public Point3D result(Point3D p) {
                return new Point3D(p.get(0), p.get(1), p.get(2));
            }
        });

    }

    //    private ParametricSurface.Parameters parameters = new ParametricSurface.Parameters(true);

  /*  static {
        if(globals==null)

        {
            Globals globals1 = new Globals();
            ParametricSurface.setGlobals(globals1);
            globals1.setIncrU(0.1);
            globals1.setIncrV(0.1);
        }

    }



    public static Globals getGlobals() {
        return globals;
    }

    public static void setGlobals(Globals globals) {
        ParametricSurface.globals = globals;
    }
*/


    public ParametricSurface() {
        startU.setElem(0.0);
        startV.setElem(0.0);
        incrU.setElem(0.1);
        incrV.setElem(0.1);
        endU.setElem(1.0);
        endV.setElem(1.0);
        incrVitesse.setElem(0.0001);
        incrNormale.setElem(0.0001);
        terminalU.setElem(new Point2Point.I());
        terminalV.setElem(new Point2Point.I());
    }

    public Double getIncrU() {
        return incrU.getElem();
    }

    public void setIncrU(Double incr1) {
//        if (parameters.isGlobal()) {
//            parameters.setIncrU(incr1);
//        } else {
//            globals.setIncrU(incr1);
//        }
        this.incrU.setElem(incr1);
    }

    public Double getIncrV() {
        return incrV.getElem();
    }

    public void setIncrV(Double incr2) {
//        if (parameters.isGlobal()) {
//            parameters.setIncrV(incr2);
//        } else {
//            globals.setIncrV(incr2);
//        }
        this.incrV.setElem(incr2);
    }

    public Point3D calculerPoint3D(double u, double v) {
        if (getQuad_not_computed() > 0) {
            if (level == 0) {
                level++;
                vectorsBak = new Point3D[]{calculerPoint3D(u + getIncrU(), v),
                        calculerPoint3D(u + getIncrU(), v + getIncrV()),
                        calculerPoint3D(u, v + getIncrV())};
            }
            if (level > 0)
                level = 0;
        }
        return Point3D.O0;
    }

    public Point3D calculerVitesse3D(double u, double v) {
        Point3D moins = calculerPoint3D(u + incrVitesse.getElem(), v).moins(calculerPoint3D(u, v));
        Point3D moins1 = calculerPoint3D(u, v + incrVitesse.getElem()).moins(calculerPoint3D(u, v));
        return moins.plus(moins1).mult(0.5 / incrVitesse.getElem() / incrVitesse.getElem()).norme1();
    }

    public Point3D calculerNormale3D(double u, double v) {
        Point3D moins0 = calculerPoint3D(u + incrNormale.getElem(), v).moins(calculerPoint3D(u, v)).norme1();
        Point3D moins1 = calculerPoint3D(u, v + incrNormale.getElem()).moins(calculerPoint3D(u, v)).norme1();
        return moins0.prodVect(moins1).mult(0.5 / incrNormale.getElem() / incrNormale.getElem()).norme1();
    }

    public Point3D calculerTangenteU(double u, double v) {
        Point3D moins = calculerPoint3D(u + incrVitesse.getElem(), v).moins(calculerPoint3D(u, v));
        return moins.mult(1.0 / incrVitesse.getElem() / incrVitesse.getElem()).norme1();
    }

    public Point3D calculerTangenteV(double u, double v) {
        Point3D moins = calculerPoint3D(u, v + incrVitesse.getElem()).moins(calculerPoint3D(u, v));
        return moins.mult(1.0 / incrVitesse.getElem()).norme1();
    }

    public Point3D calculerNormalePerp(double u, double v) {

        Point3D mult = calculerTangenteU(u + TANGENT_INCR, v).prodVect(calculerTangenteV(u, v + TANGENT_INCR)).mult(1.0);
        if (mult.norme() <= MIN_NORMGT0) {
            return mult;
        } else {
            return mult;
        }
    }

    public Double incr1() {
        return incrU.getElem();
    }

    public Double incr2() {
        return incrV.getElem();
    }

    public Double getStartU() {
        return startU.getElem();
    }

    public void setStartU(Double s1) {
        this.startU.setElem(s1);
    }

    public Double getStartV() {
        return startV.getElem();
    }

    public void setStartV(Double s2) {
        this.startV.setElem(s2);
    }

    public Double getEndU() {
        return endU.getElem();
    }

    public void setEndU(Double e1) {
        this.endU.setElem(e1);
    }

    public Double getEndV() {
        return endV.getElem();
    }

    public void setEndV(Double e2) {
        this.endV.setElem(e2);
    }

    public Point3D velocity(Double u1, Double v1, Double u2, Double v2) {
        return calculerPoint3D(u2, v2).moins(calculerPoint3D(u1, v1));
    }

    public Polygon getElementSurface(Double u, Double incrU, Double v, Double incrV) {
        Double[][] uvIncr = new Double[][]{
                {u, v},
                {u + incrU, v},
                {u + incrU, v + incrV},
                {u, v + incrV}
        };
        Polygon polygon = new Polygon(new Point3D[]{
                calculerPoint3D(uvIncr[0][0], uvIncr[0][1]),
                calculerPoint3D(uvIncr[1][0], uvIncr[1][1]),
                calculerPoint3D(uvIncr[2][0], uvIncr[2][1]),
                calculerPoint3D(uvIncr[3][0], uvIncr[3][1])},
                texture());
        return polygon;
    }

    public int getNormale3D(double v, double v1) {
        return 0;
    }

    public Point3D getNextU(double u2, double v) {
        return vectorsBak != null ? vectorsBak[0] : null;

    }

    public Point3D getNextUV(double u2, double v) {
        return vectorsBak != null ? vectorsBak[1] : null;

    }

    public Point3D getNextV(double u, double v) {
        return vectorsBak != null ? vectorsBak[2] : null;
    }

    @Override
    public void declareProperties() {
        super.declareProperties();
        getDeclaredDataStructure().put("startU/startU", startU);
        getDeclaredDataStructure().put("startV/startV", startV);
        getDeclaredDataStructure().put("incrU/incrU", incrU);
        getDeclaredDataStructure().put("incrV/incrV", incrV);
        getDeclaredDataStructure().put("endU/endU", endU);
        getDeclaredDataStructure().put("endV/endV", endV);
    }

    @Override
    public String toString() {
        return "ParametricSurface()\n";
    }

    @Override
    public Point3D calculerSurfaceT(double u, double v, double t) {
        return calculerPoint3D(u, v);
    }

//    public Parameters getParameters() {
//        return parameters;
//    }
//
//    public void setParameters(Parameters parameters) {
//        this.parameters = parameters;
//    }

    public StructureMatrix<Point2Point> getTerminalU() {
        level++;
        return terminalU;
    }

    public void setTerminalU(StructureMatrix<Point2Point> terminalU) {
        level++;
        this.terminalU = terminalU;
    }

    public StructureMatrix<Point2Point> getTerminalV() {
        level++;
        return terminalV;
    }

    public void setTerminalV(StructureMatrix<Point2Point> terminalV) {
        level++;
        this.terminalV = terminalV;
    }

    public int getQuad_not_computed() {
        return quad_not_computed;
    }

    public void setQuad_not_computed(int quad_not_computed) {
        this.quad_not_computed = quad_not_computed;
    }

    public StructureMatrix<Double> getIncrNormale() {
        return incrNormale;
    }


    public static class Globals {
        private Double incrU;
        private Double incrV;

        public Double getIncrU() {
            return incrU;
        }

        public void setIncrU(Double incrU) {
            this.incrU = incrU;
        }

        public Double getIncrV() {
            return incrV;
        }

        public void setIncrV(Double incrV) {
            this.incrV = incrV;
        }
    }

    public class Parameters {

        private boolean isGlobal = true;
        private Double incrV = 0.1;
        private Double incrU = 0.1;

        public Parameters(Double incrU, Double incrV) {
            this.setIncrU(incrU);
            this.setIncrV(incrV);
        }

        public Parameters(boolean isGlobal) {
            setGlobal(isGlobal);
        }

        public Double getIncrU() {
            return incrU;
        }

        public void setIncrU(Double incrU) {
            this.incrU = incrU;
        }

        public Double getIncrV() {
            return incrV;
        }

        public void setIncrV(Double incrV) {
            this.incrV = incrV;
        }

        public boolean isGlobal() {
            return isGlobal;
        }

        public void setGlobal(boolean global) {
            this.isGlobal = global;
        }
    }
}
