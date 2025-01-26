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

package one.empty3.facedetect.model.sol1;

import one.empty3.library.Point3D;
import one.empty3.library.Polygons;
import one.empty3.library.core.nurbs.ParametricSurface;
import one.empty3.library.core.nurbs.SurfaceParametriquePolynomiale;
import one.empty3.library.core.nurbs.SurfaceParametriquePolynomialeBezier;

import java.awt.geom.Dimension2D;
import java.util.List;

public class DistanceBezier2 extends DistanceAB {

    static class Rectangle2 {
        public double getX1() {
            return x1;
        }

        public void setX1(double x1) {
            this.x1 = x1;
        }

        public double getY1() {
            return y1;
        }

        public void setY1(double y1) {
            this.y1 = y1;
        }

        public double getX2() {
            return x2;
        }

        public void setX2(double x2) {
            this.x2 = x2;
        }

        public double getY2() {
            return y2;
        }

        public void setY2(double y2) {
            this.y2 = y2;
        }

        private double x1;
        private double y1;
        private double x2;
        private double y2;

        public Rectangle2(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public double getWidth() {
            return x2 - x1;
        }

        public double getHeight() {
            return y2 - y1;
        }
    }


    public DistanceBezier2(List<Point3D> A, List<Point3D> B, Dimension2D aDimReal, Dimension2D bDimReal) {
        try {
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
                    listAX.set(i, listAX.get(i) - rectA.getX1());
                    listAY.set(i, listAY.get(i) - rectA.getY1());
                    listBX.set(i, listBX.get(i) - rectB.getX1());
                    listBY.set(i, listBY.get(i) - rectB.getY1());

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
            for (int i = 0; i < A.size(); i++) {
                for (int j = 0; j < B.size(); j++) {
//                int i1 = (int) Math.min((double) (i % ((int) Math.sqrt(A.size() )+ 1)) * (Math.sqrt(A.size() )+ 1), A.size() - 1);
//                int j1 = (int) Math.min((double) (j / ((int) Math.sqrt(B.size() )+ 1)) * (Math.sqrt(A.size() )+ 1), B.size() - 1);


                    ((SurfaceParametriquePolynomiale) surfaceA).getCoefficients().setElem(new Point3D(listAX.get(i), listAY.get(j), 0.0), i, j);
                    ((SurfaceParametriquePolynomiale) surfaceB).getCoefficients().setElem(new Point3D(listBX.get(i), listBY.get(j), 0.0), i, j);
                }
            }

            sAij = new Point3D[(int) this.aDimReduced.getWidth()][(int) this.aDimReduced.getHeight()];
            sBij = new Point3D[(int) this.bDimReduced.getWidth()][(int) this.bDimReduced.getHeight()];

            if (sAij.length == 0 || sAij[0].length == 0 || sBij.length == 0 || sBij[0].length == 0)
                setInvalidArray(true);

            precomputeX2(aDimReal, aDimReduced, sAij, surfaceA);
            precomputeX2(bDimReal, bDimReduced, sBij, surfaceB);
        } catch (RuntimeException ex) {
            setInvalidArray(true);
        }
    }


    public Point3D findAxPointInB2(double u, double v) {
        Point3D point3D = surfaceB.calculerPoint3D(u, v);
        return surfaceA.calculerPoint3D(point3D.getX(), point3D.getY());
    }

    public Point3D findAxPointInB(double u, double v) {
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
        for (int i = 0; i < xDimReduced.getWidth(); i++)
            for (int j = 0; j < xDimReduced.getHeight(); j++) {
                Point3D tried = new Point3D(1.0 * i / xDimReduced.getWidth() * xDimReal.getWidth(),
                        1.0 * j / xDimReduced.getHeight() * xDimReal.getHeight(), 0.0);
                int i1 = (int) (double) (tried.getX() / xDimReal.getWidth());
                int j1 = (int) (double) (tried.getY() / xDimReal.getHeight());
                sXij[i][j] = surfaceX.calculerPoint3D(tried.getX(), tried.getY());

            }
    }

    public void precomputeX2(Dimension2D xDimReal, Dimension2D xDimReduced, Point3D[][] sXij, ParametricSurface surfaceX) {
        for (int i = 0; i < xDimReduced.getWidth(); i++)
            for (int j = 0; j < xDimReduced.getHeight(); j++) {
                Point3D tried = new Point3D(1.0 * i / xDimReduced.getWidth() * xDimReal.getWidth(),
                        1.0 * j / xDimReduced.getHeight() * xDimReal.getHeight(), 0.0);
                int i1 = (int) (double) (tried.getX());
                int j1 = (int) (double) (tried.getY());
                sXij[i][j] = surfaceX.calculerPoint3D(tried.getX(), tried.getY())
                        .multDot(new Point3D(1. / xDimReal.getWidth(), 1. / xDimReal.getHeight(), 0.0));

            }
    }
}
