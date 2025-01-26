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

import java.awt.geom.Dimension2D;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DistanceBezier3 extends DistanceBezier2 {
    float[][][] doubles;
    final double DIM_MAX = Double.MAX_VALUE;
    final int dim = 1000;

    public DistanceBezier3(List<Point3D> A, List<Point3D> B, Dimension2D aDimReal, Dimension2D bDimReal, boolean opt1, boolean optimizeGrid) {
        super(A, B, aDimReal, bDimReal, opt1, optimizeGrid);
        if (A == null || B == null || A.size() == 0 || B.size() == 0 || A.size() != B.size()) {
            setInvalidArray(true);
            return;
        }
        Logger.getAnonymousLogger().log(Level.INFO, "DistanceBezier3() constructor started");
        float[][][] doubles = new float[7][dim][dim];
        for (int u = 0; u < dim; u++) {
            // Closer A point
            for (int v = 0; v < dim; v++) {
                //[0][u][v] =
                final double finalU = 1.0 * u / dim;
                final double finalV = 1.0 * v / dim;

                Point3D pab = new Point3D(finalU, finalV, 0.0);

                double[] dist_current_min = new double[A.size()];
                Arrays.fill(dist_current_min, DIM_MAX);
                double distMin = DIM_MAX;
                for (int i = 0; i < B.size(); i++) {
                    Point3D b = B.get(i);
                    distMin = Point3D.distance(b, pab);
                    if (distMin < dist_current_min[i]) {
                        dist_current_min[i] = distMin;
                        // closer b point for B(u,v)
                        doubles[0][u][v] = (float) (double) b.getX();
                        doubles[1][u][v] = (float) (double) b.getY();
                        doubles[2][u][v] = i;
                    }
                }
                double ub = doubles[0][u][v];
                double vb = doubles[1][u][v];
                Point3D paa = new Point3D(ub, vb, 0.0);
                double distance = DIM_MAX;
                for (int i = 0; i < A.size(); i++) {
                    Point3D a = A.get(i);
                    distMin = Point3D.distance(paa, a);
                    if (distMin < distance) {
                        doubles[3][u][v] = (float) (double) a.getX();
                        doubles[4][u][v] = (float) (double) a.getY();
                        distance = distMin;
                    }
                }
                for (int i = 0; i < A.size(); i++) {
                    Point3D b = new Point3D((double) doubles[0][(int) (u * dim)][(int) (v * dim)], (double) doubles[1][(int) (u * dim)][(int) (v * dim)], 0.0);
                    Point3D a = new Point3D((double) doubles[3][(int) (u * dim)][(int) (v * dim)], (double) doubles[4][(int) (u * dim)][(int) (v * dim)], 0.0);
                    Point3D bMoved = a.moins(b);
                    doubles[5][u][v] = (float) (double) a.getX();
                    doubles[6][u][v] = (float) (double) a.getY();
                }
            }
        }
        Logger.getAnonymousLogger().log(Level.INFO, "DistanceBezier3() constructor ended");
    }


    @Override
    public Point3D findAxPointInB(double u, double v) {
        int iX = (int) (u * dim);
        int iY = (int) (v * dim);
        if (iX >= dim) System.exit(1);
        if (iY >= dim) System.exit(1);
        Point3D a = A.get((int) doubles[2][iX][iY]);
        Point3D b = new Point3D((double) doubles[0][(int) (u * dim)][(int) (v * dim)], (double) doubles[1][(int) (u * dim)][(int) (v * dim)], 0.0);
        Point3D a2 = new Point3D((double) doubles[3][(int) (u * dim)][(int) (v * dim)], (double) doubles[4][(int) (u * dim)][(int) (v * dim)], 0.0);
        Point3D ba = new Point3D((double) doubles[5][(int) (u * dim)][(int) (v * dim)], (double) doubles[5][(int) (u * dim)][(int) (v * dim)], 0.0);
        return ba;
    }


}