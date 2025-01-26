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

package one.empty3.facedetect.model;

import one.empty3.library.Point3D;
import one.empty3.library.core.nurbs.SurfaceParametriquePolynomialeBezier;
import one.empty3.library.objloader.E3Model;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

public class DistanceBezier extends DistanceAB {
    private static final int MAX_OCCURENCES = 2000;
    private boolean opt1 = false;
    private double distance0a = 10000;

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

    private final Dimension2D aDimReal;
    private final Dimension2D bDimReal;
    final List<Point3D> A;
    final List<Point3D> B;
    private final SurfaceParametriquePolynomialeBezier surfaceA;
    private final SurfaceParametriquePolynomialeBezier surfaceB;
    private final Point3D[][] sAij;
    protected final Point3D[][] sBij;
    private Dimension2D aDimReduced = new Dimension(80, 80);
    protected Dimension2D bDimReduced = new Dimension(80, 80);
    private Rectangle2 rectA = new Rectangle2(1000000, 1000000, 0, 0);
    private Rectangle2 rectB = new Rectangle2(1000000, 1000000, 0, 0);

    public DistanceBezier(List<Point3D> A, List<Point3D> B, Dimension2D aDimReal, Dimension2D bDimReal) {
        this.A = A;
        this.B = B;
        this.aDimReal = aDimReal;
        this.bDimReal = bDimReal;

//        Rectangle2 rectA = new Rectangle2(1000000, 1000000, 0, 0);
//        Rectangle2 rectB = new Rectangle2(1000000, 1000000, 0, 0);


        List<Double> listAX = new ArrayList<>();
        List<Double> listAY = new ArrayList<>();
        List<Double> listBX = new ArrayList<>();
        List<Double> listBY = new ArrayList<>();

        for (int i = 0; i < A.size(); i++) {
            listAX.add(A.get(i).getX());
            listAY.add(A.get(i).getY());
            listBX.add(B.get(i).getX());
            listBY.add(B.get(i).getY());

        }
        if (opt1) {
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

            for (int i = 0; i < A.size(); i++) {
                listAX.set(i, (listAX.get(i) - rectA.getX1()) / rectA.getWidth());
                listAY.set(i, (listAY.get(i) - rectA.getY1()) / rectA.getHeight());
                listBX.set(i, (listBX.get(i) - rectB.getX1()) / rectB.getWidth());
                listBY.set(i, (listBY.get(i) - rectB.getY1()) / rectB.getHeight());

            }
        }

        listAX.sort(Double::compare);
        listAY.sort(Double::compare);
        listBX.sort(Double::compare);
        listBY.sort(Double::compare);
/*
        aDimReduced.setSize(new Dimension((int) (double) (listAX.get(listAX.size() - 1) - listAX.get(0)),
                (int) (double) (listAY.get(listAY.size() - 1) - listAY.get(0))));
        bDimReduced.setSize(new Dimension((int) (double) (listBX.get(listBX.size() - 1) - listBX.get(0)),
                (int) (double) (listBY.get(listBY.size() - 1) - listBY.get(0))));
*/

        surfaceA = new SurfaceParametriquePolynomialeBezier();
        surfaceB = new SurfaceParametriquePolynomialeBezier();

        for (int j = 0; j < A.size(); j++) {
            //     surfaceA.getCoefficients().setElem(new Point3D(1.0 * j / A.size(), 0.0, 0.0), j, 0);
            //     surfaceB.getCoefficients().setElem(new Point3D(1.0 * j / A.size(), 0.0, 0.0), j, 0);
        }
        for (int i = 0; i < A.size(); i++) {
            //    surfaceA.getCoefficients().setElem(new Point3D(0.0, 1.0 * i / A.size(), 0.0), 0, i+1);
            //    surfaceB.getCoefficients().setElem(new Point3D(0.0, 1.0 * i / A.size(), 0.0), 0, i+1);
            for (int j = 0; j < B.size(); j++) {
                surfaceA.getCoefficients().setElem(new Point3D(listAX.get(i), listAY.get(j), 0.0), i, j);
                surfaceB.getCoefficients().setElem(new Point3D(listBX.get(i), listBY.get(j), 0.0), i, j);
            }
            //    surfaceA.getCoefficients().setElem(new Point3D(1.0, 1.0, 0.0), A.size(), i);
            //    surfaceB.getCoefficients().setElem(new Point3D(1.0, 1.0, 0.0), A.size(), i);
        }
        for (int i = 0; i < A.size(); i++) {
            //    surfaceA.getCoefficients().setElem(new Point3D((double) i / A.size(), 1.0, 0.0), i, A.size() + 1);
            //    surfaceB.getCoefficients().setElem(new Point3D((double) i / A.size(), 1.0, 0.0), i, A.size() + 1);
        }
/*
        this.aDimReduced = new Dimension((int) (rectA.getWidth() * aDimReduced.getWidth()), (int) (rectA.getHeight() * aDimReduced.getHeight()));

        this.bDimReduced = new Dimension((int) (rectB.getWidth() * bDimReduced.getWidth()), (int) (rectB.getHeight() * bDimReduced.getHeight()));
*/

        sAij = new Point3D[(int) this.aDimReduced.getWidth()][(int) this.aDimReduced.getHeight()];
        sBij = new Point3D[(int) this.bDimReduced.getWidth()][(int) this.bDimReduced.getHeight()];

        if (sAij.length == 0 || sAij[0].length == 0 || sBij.length == 0 || sBij[0].length == 0)
            setInvalidArray(true);

        precomputeX(aDimReal, aDimReduced, sAij, surfaceA);
        precomputeX(bDimReal, bDimReduced, sBij, surfaceB);

    }


    public Point3D findAxPointInB2(double u, double v) {
        Point3D point3D = surfaceB.calculerPoint3D(u, v);
        return surfaceA.calculerPoint3D(point3D.getX(), point3D.getY());
    }

    public Point3D findAxPointInB(double u, double v) {
        return findAxPointInB3(u, v);
    }

    public Point3D findAxPointInB5(double u, double v) {
        Point3D uvFace = ((E3Model) getModel()).findUvFace(u, v);
        if (uvFace == null)
            return null;
        return surfaceA.calculerPoint3D(uvFace.getX(), uvFace.getY());
    }

    public Point3D findAxPointInB4(double u, double v) {
        int i1 = (int) (u * aDimReduced.getWidth());
        int j1 = (int) (v * aDimReduced.getHeight());
        double u1 = u * aDimReduced.getWidth() - i1;
        double v1 = v * aDimReduced.getHeight() - j1;
        return surfaceA.calculerPoint3D(u, v);
    }

    public Point3D findAxPointInB3(double u, double v) {
        Point3D searched = null;
        if (opt1) {
            searched = new Point3D((u - rectB.getX1()) / rectB.getWidth(), (v - rectB.getY1()) / rectB.getHeight(), 0.0);
        } else {
            searched = new Point3D(u, v, 0.0);
        }
        double distance = Double.MAX_VALUE;
        Point3D found = searched;
        if (isInvalidArray())
            return found;
        searched = sBij[(int) Math.min((u * aDimReduced.getWidth())
                , aDimReduced.getWidth() - 1)][(int) Math.min((v * aDimReduced.getHeight())
                , aDimReduced.getHeight() - 1)];
        for (int i = 0; i < bDimReduced.getWidth(); i++) {
            for (int j = 0; j < bDimReduced.getHeight(); j++) {
                Double dist = Point3D.distance(sBij[i][j], searched);
                if (dist < distance) {
                    distance = dist;
                    found = new Point3D(i / bDimReduced.getWidth(), j / bDimReduced.getHeight(), 0.0);
                }
            }
        }
        Point3D aSearch = sAij[(int) (found.getX() * aDimReduced.getWidth())][(int) (found.getY() * aDimReduced.getHeight())];
        Point3D aFound = found;
        for (int i = 0; i < aDimReduced.getWidth(); i++) {
            for (int j = 0; j < aDimReduced.getHeight(); j++) {
                Double dist = Point3D.distance(sAij[i][j], aSearch);
                if (dist < distance) {
                    distance = dist;
                    //aFound = new Point3D(i / aDimReduced.getWidth(), j / aDimReduced.getHeight(), 0.0);
                    aFound = new Point3D((double) i, (double) j, 0.0);
                }
            }
        }

        return aFound;

        //return sAij[(int) (found.getX() * aDim.getWidth())]
        //        [(int) (found.getX() * aDim.getHeight())];
        //return sAij[(int) Math.min((found.getX() * aDimReduced.getWidth()), aDimReduced.getWidth() - 1)][(int) Math.min((found.getY() * aDimReduced.getHeight()), aDimReduced.getHeight() - 1)];
    }


    public Point3D findAxPointInB31(double u, double v) {
        Point3D searched = null;
        if (opt1) {
            searched = new Point3D((u - rectB.getX1()) / rectB.getWidth(), (v - rectB.getY1()) / rectB.getHeight(), 0.0);
        } else {
            searched = new Point3D(u, v, 0.0);
        }
        double distance = Double.MAX_VALUE;
        Point3D found = searched;
        if (isInvalidArray())
            return found;
        searched = sBij[(int) Math.min((u * aDimReduced.getWidth())
                , aDimReduced.getWidth() - 1)][(int) Math.min((v * aDimReduced.getHeight())
                , aDimReduced.getHeight() - 1)];
        for (int i = 0; i < bDimReduced.getWidth(); i++) {
            for (int j = 0; j < bDimReduced.getHeight(); j++) {
                Double dist = Point3D.distance(sBij[i][j], searched);
                if (dist < distance) {
                    distance = dist;
                    found = new Point3D(i / bDimReduced.getWidth(), j / bDimReduced.getHeight(), 0.0);
                }
            }
        }
        Point3D aSearch = sAij[(int) (found.getX() * aDimReduced.getWidth())][(int) (found.getY() * aDimReduced.getHeight())];
        Point3D aFound = found;
        for (int i = 0; i < aDimReduced.getWidth(); i++) {
            for (int j = 0; j < aDimReduced.getHeight(); j++) {
                Double dist = Point3D.distance(sAij[i][j], aSearch);
                if (dist < distance) {
                    distance = dist;
                    //aFound = new Point3D(i / aDimReduced.getWidth(), j / aDimReduced.getHeight(), 0.0);
                    aFound = new Point3D((double) i, (double) j, 0.0);
                }
            }
        }

        return sAij[(int) Math.min((found.getX()), aDimReduced.getWidth() - 1)][(int) Math.min((found.getY()), aDimReduced.getHeight() - 1)];
    }

    public Point3D findAxPointInB31inv(double u, double v) {
        Point3D searched = null;
        if (opt1) {
            searched = new Point3D((u - rectA.getX1()) / rectA.getWidth(), (v - rectA.getY1()) / rectA.getHeight(), 0.0);
        } else {
            searched = new Point3D(u, v, 0.0);
        }
        double distance = Double.MAX_VALUE;
        Point3D found = searched;
        if (isInvalidArray())
            return found;
        searched = sAij[(int) Math.min((u * aDimReduced.getWidth())
                , aDimReduced.getWidth() - 1)][(int) Math.min((v * aDimReduced.getHeight())
                , aDimReduced.getHeight() - 1)];
        for (int i = 0; i < aDimReduced.getWidth(); i++) {
            for (int j = 0; j < aDimReduced.getHeight(); j++) {
                Double dist = Point3D.distance(sAij[i][j], searched);
                if (dist < distance) {
                    distance = dist;
                    found = new Point3D(i / aDimReduced.getWidth(), j / aDimReduced.getHeight(), 0.0);
                }
            }
        }
        Point3D bSearch = sBij[(int) (found.getX() * bDimReduced.getWidth())][(int) (found.getY() * bDimReduced.getHeight())];
        Point3D bFound = found;
        for (int i = 0; i < aDimReduced.getWidth(); i++) {
            for (int j = 0; j < aDimReduced.getHeight(); j++) {
                Double dist = Point3D.distance(sBij[i][j], bSearch);
                if (dist < distance) {
                    distance = dist;
                    //aFound = new Point3D(i / aDimReduced.getWidth(), j / aDimReduced.getHeight(), 0.0);
                    bFound = new Point3D((double) i, (double) j, 0.0);
                }
            }
        }

        return sBij[(int) Math.min((found.getX()), bDimReduced.getWidth() - 1)][(int) Math.min((found.getY()), bDimReduced.getHeight() - 1)];
    }

    public Point3D findAxPointInB31invOptimized1(double u, double v) {
        double distance1a = distance0a;
        Point3D searched = null;
        if (opt1) {
            searched = new Point3D((u - rectA.getX1()) / rectA.getWidth(), (v - rectA.getY1()) / rectA.getHeight(), 0.0);
        } else {
            searched = new Point3D(u, v, 0.0);
        }
        double distance = Double.MAX_VALUE;
        Point3D aFound = searched;
        if (isInvalidArray())
            return aFound;
        searched = returnGetOrDefault(sAij, (int) Math.min((u * aDimReduced.getWidth())
                , aDimReduced.getWidth() - 1), (int) Math.min((v * aDimReduced.getHeight())
                , aDimReduced.getHeight() - 1));
        aFound = fromNearToFar(aDimReduced, surfaceA, sAij, searched, distance);


        Point3D bSearch = returnGetOrDefault(sBij, (int) (double) (aFound.getX()), (int) (double) (aFound.getY()));

        Point3D bFound;

        bFound = fromNearToFar(aDimReduced, surfaceB, sBij, bSearch, distance);

        return returnGetOrDefault(sBij, (int) (double) bFound.getX(), (int) (double) bFound.getY());
    }

    boolean isInArrayBounds(double[] array, int index) {
        return index >= 0 && index < array.length;
    }

    boolean isInArrayBounds(double[][] array, int index1, int index2) {
        return index1 >= 0 && index1 < array.length && index2 >= 0 && index2 < array[index1].length;
    }

    public double returnGetOrDefault(double[] array, int index) {
        index = Math.max(0, Math.min(array.length - 1, index));
        return array[index];
    }

    public double returnGetOrDefault(double[][] array, int index1, int index2) {
        index1 = Math.max(0, Math.min(array.length - 1, index1));
        index2 = Math.max(0, Math.min(array[index1].length - 1, index2));
        return array[index1][index2];
    }

    public Point3D returnGetOrDefault(Point3D[][] array, int index1, int index2) {
        index1 = Math.max(0, Math.min(array.length - 1, index1));
        index2 = Math.max(0, Math.min(array[index1].length - 1, index2));
        return array[index1][index2];
    }

    public void precomputeX(Dimension2D xDimReal, Dimension2D xDimReduced, Point3D[][] sXij, SurfaceParametriquePolynomialeBezier surfaceX) {
        for (int i = 0; i < xDimReal.getWidth(); i++)
            for (int j = 0; j < xDimReal.getHeight(); j++) {
                double u = (double) i / xDimReal.getWidth();
                double v = (double) j / xDimReal.getHeight();
                int i1 = (int) (u * xDimReduced.getWidth());
                int j1 = (int) (v * xDimReduced.getHeight());
                sXij[i1][j1] = surfaceX.calculerPoint3D(u, v);

            }
    }

    protected Point3D fromNearToFar(Dimension2D xDimReduced, SurfaceParametriquePolynomialeBezier surfaceX, Point3D[][] sXij, Point3D searched, double distance0) {
        Point3D xFound = searched;
        double distance = distance0;
        int i0 = (int) (double) (searched.getX());
        int j0 = (int) (double) (searched.getY());
        int cLength = 0;
        int xIncr = 1;
        int yIncr = 0;

        int occurencesFound = 0;

        int maxIterations = (int) (xDimReduced.getWidth() * xDimReduced.getHeight() / 2);
        int iterations = 0;
        while (occurencesFound < MAX_OCCURENCES && cLength < Math.min(xDimReduced.getHeight(), xDimReduced.getWidth()) && iterations < maxIterations) {

            for (int c = 0; c < cLength; c++) {
                switch (c) {
                    case 0:
                        xIncr = 1;
                        yIncr = 0;
                        break;
                    case 1:
                        xIncr = 0;
                        yIncr = 1;
                        break;
                    case 2:
                        xIncr = -1;
                        yIncr = 0;
                        break;
                    case 3:
                        xIncr = 0;
                        yIncr = -1;
                        break;
                }
                i0 += xIncr;
                j0 += yIncr;
                if (i0 < 0 || j0 < 0 || i0 >= xDimReduced.getWidth() || j0 >= xDimReduced.getHeight()) {
                    continue;
                }
                Double dist = Point3D.distance(sBij[i0][j0], searched);
                if (dist < distance0) {
                    distance = dist;
                    //aFound = new Point3D(i / aDimReduced.getWidth(), j / aDimReduced.getHeight(), 0.0);
                    xFound = new Point3D((double) i0, (double) j0, 0.0);
                    occurencesFound++;
                }
            }
            iterations++;
            cLength += 2;
        }
        return xFound;
    }
}
