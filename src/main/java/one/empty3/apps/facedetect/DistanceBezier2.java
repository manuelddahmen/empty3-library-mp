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

package one.empty3.apps.facedetect;

import one.empty3.library.Point3D;
import one.empty3.library.Polygons;
import one.empty3.library.core.nurbs.ParametricSurface;
import one.empty3.library.core.nurbs.SurfaceParametriquePolynomiale;
import one.empty3.library.core.nurbs.SurfaceParametriquePolynomialeBezier;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

public abstract class DistanceBezier2 extends DistanceAB {


    public DistanceBezier2(List<Point3D> A, List<Point3D> B, Dimension2D aDimReal, Dimension2D bDimReal, boolean opt1, boolean optimizeGrid) {
        super();
        this.opt1 = opt1;
        this.optimizeGrid = optimizeGrid;
        class Dimension2D extends java.awt.geom.Dimension2D {
            private double x;
            private double y;

            public Dimension2D(double xMax, double yMax) {
                this.x = xMax;
                this.y = yMax;
            }

            @Override
            public double getWidth() {
                return x;
            }

            @Override
            public double getHeight() {
                return y;
            }

            @Override
            public void setSize(double width, double height) {

            }
        }

        aDimReduced = new Dimension2D(distanceABdimSize, distanceABdimSize);
        bDimReduced = new Dimension2D(distanceABdimSize, distanceABdimSize);
        System.out.println("\nStart properties\n\tOpt1 : " + opt1 + "\n\tOptimizeGrid: " + optimizeGrid + "\n\tTypeShape : " + typeShape + "\n\t" + "refineGrid : " + refineMatrix + "\n" + getClass().getCanonicalName() + "\nEnd of properties");

        if (A != null && B != null && A.size() > 0 && B.size() > 0) {

        } else {
            System.out.println("A or B are null or empty");
            return;
        }

        this.A = A;
        this.B = B;
        this.aDimReal = aDimReal;
        this.bDimReal = bDimReal;

        rectA = new Rectangle2(1000000, 1000000, 0, 0);
        rectB = new Rectangle2(1000000, 1000000, 0, 0);


        for (int i = 0; i < A.size(); i++) {
            listAX.add(A.get(i).getX());
            listAY.add(A.get(i).getY());
            listBX.add(B.get(i).getX());
            listBY.add(B.get(i).getY());

        }

        for (int i = 0; i < A.size(); i++) {
            if (rectA.getX1() > A.get(i).getX())
                rectA.setX1(A.get(i).getX());
            if (rectB.getX1() > B.get(i).getX())
                rectB.setX1(B.get(i).getX());
            if (rectA.getY1() > A.get(i).getY())
                rectA.setY1(A.get(i).getY());
            if (rectB.getY1() > B.get(i).getY())
                rectB.setY1(B.get(i).getY());
            if (rectA.getX2() < A.get(i).getX())
                rectA.setX2(A.get(i).getX());
            if (rectB.getX2() < B.get(i).getX())
                rectB.setX2(B.get(i).getX());
            if (rectA.getY2() < A.get(i).getY())
                rectA.setY2(A.get(i).getY());
            if (rectB.getY2() < B.get(i).getY())
                rectB.setY2(B.get(i).getY());
        }

        if (opt1) {
            for (int i = 0; i < A.size(); i++) {
                listAX.set(i, (listAX.get(i) - rectA.getX1()) / rectA.getWidth());
                listAY.set(i, (listAY.get(i) - rectA.getY1()) / rectA.getHeight());
                listBX.set(i, (listBX.get(i) - rectB.getX1()) / rectB.getWidth());
                listBY.set(i, (listBY.get(i) - rectB.getY1()) / rectB.getHeight());
/*
                rectA.setX1(0);
                rectA.setY1(0);
                rectB.setX1(0);
                rectB.setY1(0);

                rectA.setX2(rectA.getX2() - rectA.getX1());
                rectA.setY2(rectA.getY2() - rectA.getY1());
                rectB.setX2(rectB.getX2() - rectB.getX1());
                rectB.setY2(rectB.getY2() - rectB.getY1());
  */
            }
        }
        listAX.sort(Double::compare);
        listAY.sort(Double::compare);
        listBX.sort(Double::compare);
        listBY.sort(Double::compare);


        switch (typeShape) {
            case TYPE_SHAPE_BEZIER -> {
                surfaceA = new SurfaceParametriquePolynomialeBezier();
                surfaceB = new SurfaceParametriquePolynomialeBezier();
            }
            case TYPE_SHAPE_QUADR -> {
                surfaceA = new Polygons();
                surfaceB = new Polygons();
            }
        }

        if (optimizeGrid) {
            final double listAXmin = listAX.get(0);
            final double listAXmax = listAX.get(listAX.size() - 1);
            final double listAYmin = listAY.get(0);
            final double listAYmax = listAY.get(listAY.size() - 1);
            final double listBXmin = listBX.get(0);
            final double listBXmax = listBX.get(listBX.size() - 1);
            final double listBYmin = listBY.get(0);
            final double listBYmax = listBY.get(listBY.size() - 1);
            final List<Double> listAXopt = new ArrayList<>();
            final List<Double> listAYopt = new ArrayList<>();
            final List<Double> listBXopt = new ArrayList<>();
            final List<Double> listBYopt = new ArrayList<>();
            for (int i = 0; i < OPTIMIZED_GRID_SIZE; i++) {
                listAXopt.add(listAXmin + i * (listAXmax - listAXmin) / OPTIMIZED_GRID_SIZE);
                listAYopt.add(listAYmin + i * (listAYmax - listAYmin) / OPTIMIZED_GRID_SIZE);
                listBXopt.add(listBXmin + i * (listBXmax - listBXmin) / OPTIMIZED_GRID_SIZE);
                listBYopt.add(listBYmin + i * (listBYmax - listBYmin) / OPTIMIZED_GRID_SIZE);
            }

            listAX = listAXopt;
            listAY = listAYopt;
            listBX = listBXopt;
            listBY = listBYopt;
        }

        if (!optimizeGrid) {
            for (int i = 0; i < A.size(); i++) {
                for (int j = 0; j < B.size(); j++) {
//                int i1 = (int) Math.min((double) (i % ((int) Math.sqrt(A.size() )+ 1)) * (Math.sqrt(A.size() )+ 1), A.size() - 1);
//                int j1 = (int) Math.min((double) (j / ((int) Math.sqrt(B.size() )+ 1)) * (Math.sqrt(A.size() )+ 1), B.size() - 1);
                    if (refineMatrix) {
                        if (i + 1 < A.size() && i + 1 < B.size() && j + 1 < A.size() && j + 1 < B.size()) {
                            double uA = listAX.get(i + 1) - listAX.get(i) / REFINE_MATRIX_FACTOR;
                            double uB = listBX.get(i + 1) - listBX.get(i) / REFINE_MATRIX_FACTOR;
                            double vA = listAY.get(j + 1) - listAY.get(j) / REFINE_MATRIX_FACTOR;
                            double vB = listBY.get(j + 1) - listBY.get(j) / REFINE_MATRIX_FACTOR;
                            for (int k = 0; k < REFINE_MATRIX_FACTOR; k++) {
                                for (int l = 0; l < REFINE_MATRIX_FACTOR; l++) {
                                    ((SurfaceParametriquePolynomiale) surfaceA).getCoefficients().setElem(new Point3D(listAX.get(i) + uA * k, listAY.get(j) + vA * l, 0.0), i * REFINE_MATRIX_FACTOR + k, j * REFINE_MATRIX_FACTOR + l);
                                    ((SurfaceParametriquePolynomiale) surfaceB).getCoefficients().setElem(new Point3D(listBX.get(i) + uB * k, listBY.get(j) * vB * l, 0.0), i * REFINE_MATRIX_FACTOR + k, j * REFINE_MATRIX_FACTOR + l);

                                }
                            }
                        }
                    } else {
                        ((SurfaceParametriquePolynomiale) surfaceA).getCoefficients().setElem(new Point3D(listAX.get(i), listAY.get(j), 0.0), i, j);
                        ((SurfaceParametriquePolynomiale) surfaceB).getCoefficients().setElem(new Point3D(listBX.get(i), listBY.get(j), 0.0), i, j);
                    }
                }
            }
        } else {
            for (int i = 0; i < OPTIMIZED_GRID_SIZE; i++) {
                for (int j = 0; j < OPTIMIZED_GRID_SIZE; j++) {
                    ((SurfaceParametriquePolynomiale) surfaceA).getCoefficients().setElem(new Point3D(listAX.get(i), listAY.get(j), 0.0), i, j);
                    ((SurfaceParametriquePolynomiale) surfaceB).getCoefficients().setElem(new Point3D(listBX.get(i), listBY.get(j), 0.0), i, j);
                }
            }
        }

        sAij = new Point3D[(int) this.aDimReduced.getWidth()][(int) this.aDimReduced.getHeight()];
        sBij = new Point3D[(int) this.bDimReduced.getWidth()][(int) this.bDimReduced.getHeight()];

        if (sAij.length == 0 || sAij[0].length == 0 || sBij.length == 0 || sBij[0].length == 0)
            setInvalidArray(true);

        precomputeX2(aDimReal, aDimReduced, sAij, surfaceA, rectA);
        precomputeX2(bDimReal, bDimReduced, sBij, surfaceB, rectB);
    }

    public double maxBox(double v, double min, double max) {
        return Math.max(min, Math.min(v, max));
    }


    public Point3D findAxPointInB2(double u, double v) {
        Point3D searched = new Point3D(u, v, 0.0);
        double distance = Double.MAX_VALUE;
        Point3D found = searched;
        if (isInvalidArray())
            return found;
        //searched = sAij[(int) Math.min((u * aDimReduced.getWidth())
        //        , aDimReduced.getWidth() - 1)][(int) Math.min((v * aDimReduced.getHeight())
        //        , aDimReduced.getHeight() - 1)];
        for (int i = 0; i < aDimReduced.getWidth(); i++)
            for (int j = 0; j < aDimReduced.getHeight(); j++) {
                Double dist = Point3D.distance(sBij[i][j], searched);
                if (dist < distance) {
                    distance = dist;
                    found = new Point3D(i / aDimReduced.getWidth(), j / aDimReduced.getHeight(), 0.0);
                }
            }
        return found;
    }

    public abstract Point3D findAxPointInB(double u, double v);

    // Assez-good version
    public Point3D findAxPointInB2a(double u, double v) {
        Point3D point3D = surfaceB.calculerPoint3D(u, v);
        return surfaceA.calculerPoint3D(point3D.getX(), point3D.getY());
    }

    public Point3D findAxPointInB2a1(double u, double v) {
        Point3D point3D = surfaceB.calculerPoint3D(u, v);
        return point3D.mult(Math.sqrt(A.size()));//surfaceA.calculerPoint3D(point3D.getX(), point3D.getY());
    }

    public Point3D findAxPointInB2b(double u, double v) {
        return surfaceB.calculerPoint3D(u, v);
    }

    public Point3D findAxPointInB1(double u, double v) {
        Point3D searched = new Point3D(u, v, 0.0);
        double distance = Double.MAX_VALUE;
        Point3D found = searched;
        if (isInvalidArray())
            return found;
        //searched = sAij[(int) Math.min((u * aDimReduced.getWidth())
        //        , aDimReduced.getWidth() - 1)][(int) Math.min((v * aDimReduced.getHeight())
        //        , aDimReduced.getHeight() - 1)];
        for (int i = 0; i < bDimReduced.getWidth(); i++)
            for (int j = 0; j < bDimReduced.getHeight(); j++) {
                Double dist = Point3D.distance(sBij[i][j], searched);
                if (dist < distance) {
                    distance = dist;
                    found = new Point3D(i / bDimReduced.getWidth(), j / bDimReduced.getHeight(), 0.0);
                }
            }
        return found;

        //return sAij[(int) (found.getX() * aDim.getWidth())]
        //        [(int) (found.getX() * aDim.getHeight())];
        //return sAij[(int) Math.min((found.getX() * aDimReduced.getWidth()), aDimReduced.getWidth() - 1)][(int) Math.min((found.getY() * aDimReduced.getHeight()), aDimReduced.getHeight() - 1)];
    }

    public void precomputeX(Dimension2D xDimReal, Dimension2D xDimReduced, Point3D[][] sXij, ParametricSurface surfaceX) {
        for (int i = 0; i < xDimReduced.getWidth(); i++) {
            for (int j = 0; j < xDimReduced.getHeight(); j++) {
                Point3D tried = new Point3D(1.0 * i / xDimReduced.getWidth() * xDimReal.getWidth(),
                        1.0 * j / xDimReduced.getHeight() * xDimReal.getHeight(), 0.0);
                sXij[i][j] = surfaceX.calculerPoint3D(tried.getX(), tried.getY());

            }

        }
    }

    public void precomputeX2(Dimension2D xDimReal, Dimension2D xDimReduced, Point3D[][] sXij, ParametricSurface surfaceX, Rectangle2 rectX) {
        for (int i = 0; i < xDimReduced.getWidth(); i++) {
            for (int j = 0; j < xDimReduced.getHeight(); j++) {
                if (opt1) {
                    try {
                        // CHECK : why reversed?
                        Point3D tried = new Point3D(rectX.getX1(), rectX.getY1(), 0.0)
                                .plus(new Point3D((rectX.x2 - 1.0 * i / xDimReduced.getWidth()
                                        / rectX.getWidth()),
                                        (rectX.y2 - 1.0 * j / xDimReduced.getHeight()
                                                / rectX.getHeight()), 0.0));
                        sXij[i][j] = surfaceX.calculerPoint3D(tried.getX(), tried.getY());
        /*                sXij[i][j] = surfaceX.calculerPoint3D(tried.getX(), tried.getY())
                                .multDot(new Point3D(1. / xDimReal.getWidth(), 1. / xDimReal.getHeight(), 0.0));
         */
                    } catch (IndexOutOfBoundsException ex) {
                        ex.printStackTrace();
                    }

                } else {
                    Point3D tried = new Point3D(1.0 * i / xDimReduced.getWidth() * xDimReal.getWidth(),
                            1.0 * j / xDimReduced.getHeight() * xDimReal.getHeight(), 0.0);
                    int i1 = (int) (double) (tried.getX());
                    int j1 = (int) (double) (tried.getY());
                    sXij[i][j] = surfaceX.calculerPoint3D(tried.getX(), tried.getY())
                            .multDot(new Point3D(1. / xDimReal.getWidth(), 1. / xDimReal.getHeight(), 0.0));

                }

            }
        }
    }
}
