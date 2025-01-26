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
import one.empty3.library1.shader.Mat;

import java.awt.geom.Dimension2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DistanceBB extends DistanceBezier2 {
    ;

    public DistanceBB(List<Point3D> A, List<Point3D> B, Dimension2D aDimReal, Dimension2D bDimReal) {
        super(A, B, aDimReal, bDimReal, false, false);
    }

    public Point3D findAxPointInB1(double u, double v) {
        Mat matA = new Mat(3, A.size());
        Mat matB = new Mat(B.size(), 3);
        for (int i = 0; i < A.size(); i++) {
            matA.set(0, i, A.get(i).getX());
            matA.set(1, i, A.get(i).getY());
            matA.set(2, i, A.get(i).getZ());
            matA.set(i, 0, B.get(i).getX());
            matA.set(i, 1, B.get(i).getY());
            matA.set(i, 2, B.get(i).getZ());

        }
        Mat product = matA.product(matB);

        Mat mat = new Mat(3, 1);
        mat.set(0, 0, u);
        mat.set(1, 0, v);
        mat.set(2, 0, 1);

        Mat product1 = product.product(mat);

        return new Point3D(product1.get(0, 0), product1.get(0, 1), product1.get(0, 2));
    }

    @Override
    public Point3D findAxPointInB(double u, double v) {
        return findAxPointInB5a2(u, v);
    }

    public Point3D findAxPointInB5a(double u, double v) {
        Point3D searched = null;
        searched = new Point3D(u, v, 0.0);
        double distance = Double.MAX_VALUE;
        Point3D found = searched;//.multDot(new Point3D(bDimReduced.getWidth(), bDimReduced.getHeight(), 0.0));
        if (isInvalidArray()) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "DistanceAB, array is invalid");
            return found;
        }
        for (int i = 0; i < bDimReduced.getWidth(); i++) {
            for (int j = 0; j < bDimReduced.getHeight(); j++) {
                Double dist = Point3D.distance(sBij[i][j], searched);
                if (dist < distance) {
                    distance = dist;
                    found = new Point3D(i / bDimReduced.getWidth(), j / bDimReduced.getHeight(), 0.0);
                    //found = new Point3D(((double) i), ((double) j), 0.0);
                    //found = new Point3D((double) i, (double) j, 0.0);
                }
            }
        }
        return found;
    }

    public Point3D findAxPointInB5a2(double u, double v) {
        Point3D searched = null;
        searched = new Point3D(u, v, 0.0).multDot(new Point3D(1. / bDimReduced.getWidth(), 1. / bDimReduced.getHeight(), 0.0));
        double distance = Double.MAX_VALUE;
        Point3D found = searched;//
        if (isInvalidArray()) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "DistanceAB, array is invalid");
            return found;
        }
        for (int i = 0; i < bDimReduced.getWidth(); i++) {
            for (int j = 0; j < bDimReduced.getHeight(); j++) {
                Double dist = Point3D.distance(sBij[i][j], searched);
                if (dist < distance) {
                    distance = dist;
                    found = new Point3D(i / bDimReduced.getWidth(), j / bDimReduced.getHeight(), 0.0);
                    found = new Point3D(((double) i), ((double) j), 0.0);
                    //found = new Point3D((double) i, (double) j, 0.0);
                }
            }
        }
        return found;
    }

    public Point3D findAxPointInB5b(double u, double v) {
        Point3D searched = null;
        searched = new Point3D(u, v, 0.0);
        double distance = Double.MAX_VALUE;
        Point3D found = searched;//.multDot(new Point3D(bDimReduced.getWidth(), bDimReduced.getHeight(), 0.0));
        if (isInvalidArray()) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "DistanceAB, array is invalid");
            return found;
        }
        for (int i = 0; i < bDimReduced.getWidth(); i++) {
            for (int j = 0; j < bDimReduced.getHeight(); j++) {
                Double dist = Point3D.distance(sBij[i][j], searched);
                if (dist < distance) {
                    distance = dist;
                    found = new Point3D((double) i, (double) j, 0.0);
                    //found = new Point3D(((double) i), ((double) j), 0.0);
                    //found = new Point3D((double) i, (double) j, 0.0);
                }
            }
        }
        int iARef = 0;
        int jARef = 0;
        searched = found;
        distance = Double.MAX_VALUE;
        for (int i = 0; i < aDimReduced.getWidth(); i++) {
            for (int j = 0; j < aDimReduced.getHeight(); j++) {
                Double dist = Point3D.distance(sAij[i][j], searched);
                if (dist < distance) {
                    distance = dist;
                    //found = new Point3D(i / aDimReduced.getWidth(), j / aDimReduced.getHeight(), 0.0);
                    //iARef = i;
                    //jARef = j;
                    //found = new Point3D(((double) i), ((double) j), 0.0);
                    found = new Point3D((double) i, (double) j, 0.0);
                }
            }
        }

        //return sAij[iARef][jARef];
        return found.multDot(new Point3D(aDimReduced.getWidth(), aDimReduced.getHeight(), 0.0));
    }


    public Point3D findAxPointInB5c(double u, double v) {
        Point3D searched = null;
        searched = new Point3D(u, v, 0.0);
        double distance = Double.MAX_VALUE;
        Point3D found = searched;//.multDot(new Point3D(bDimReduced.getWidth(), bDimReduced.getHeight(), 0.0));
        if (isInvalidArray()) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "DistanceAB, array is invalid");
            return found;
        }
        for (int i = 0; i < bDimReduced.getWidth(); i++) {
            for (int j = 0; j < bDimReduced.getHeight(); j++) {
                Double dist = Point3D.distance(/*sBij*/sBij[i][j], searched);
                if (dist < distance) {
                    distance = dist;
                    found = new Point3D((double) (i / bDimReduced.getWidth()),
                            (double) (j / bDimReduced.getHeight()), 0.0);
                }
            }
        }
        double remainderX = 0;
        double remainderY = 0;
        try {
            int ib = 0, jb = 0;
            for (int i = 0; i < listBX.size() - 1; i++) {
                if (listBX.get(i) < found.getX()) {
                    ib = i;
                    remainderX = (listBX.get(i) - found.getX()) / (listBX.get(i + 1) - listBY.get(i + 1));
                }
            }
            for (int i = 0; i < listBY.size() - 1; i++) {
                if (listBY.get(i) < found.getY()) {
                    jb = i;
                    remainderY = (listBX.get(i) - found.getX()) / (listBX.get(i + 1) - listBY.get(i));
                }
            }

            Point3D p2 = new Point3D(listAX.get(ib) + remainderX * (listAX.get(ib + 1) - listAX.get(ib)),
                    listAY.get(ib) + remainderY * (listAY.get(ib + 1) - listAY.get(jb + 1)), 0.0);
            //return p2;
            return surfaceA.calculerPoint3D((int) (p2.getX() * bDimReduced.getWidth()),
                    (int) (p2.getY() * bDimReduced.getHeight()));
            //return sAij[(int) (p2.getX() * bDimReduced.getWidth())]
            //        [(int) (p2.getY() * bDimReduced.getHeight())];
        } catch (RuntimeException ex) {
            Logger.getAnonymousLogger().log(Level.INFO, "Error in DistanceBB");
            return Point3D.O0;
        }
    }


    public Point3D findAxPointInB5d(double u, double v) {
        Point3D searched = new Point3D(u, v, 0.0);
        double distance = Double.MAX_VALUE;
        Point3D found = searched;//.multDot(new Point3D(bDimReduced.getWidth(), bDimReduced.getHeight(), 0.0));
        if (isInvalidArray()) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "DistanceAB, array is invalid");
            return found;
        }
        for (int i = 0; i < bDimReduced.getWidth(); i++) {
            for (int j = 0; j < bDimReduced.getHeight(); j++) {
                Double dist = Point3D.distance(sBij[i][j], searched);
                if (dist < distance) {
                    distance = dist;
                    found = new Point3D((double) (i / bDimReduced.getWidth()),
                            (double) (j / bDimReduced.getHeight()), 0.0);
                }
            }
        }
        distance = Double.MAX_VALUE;
        for (int i = 0; i < aDimReduced.getWidth(); i++) {
            for (int j = 0; j < aDimReduced.getHeight(); j++) {
                Double dist = Point3D.distance(sAij[i][j], searched);
                if (dist < distance) {
                    distance = dist;
                    found = new Point3D((double) (i / aDimReduced.getWidth()),
                            (double) (j / aDimReduced.getHeight()), 0.0);
                }
            }
        }
        return found;
    }
}
