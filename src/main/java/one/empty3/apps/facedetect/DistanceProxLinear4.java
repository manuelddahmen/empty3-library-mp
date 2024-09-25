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
import one.empty3.library.TRI;
import one.empty3.library.core.nurbs.SurfaceParametriquePolynomiale;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

public class DistanceProxLinear4 extends DistanceBezier2 {
    private final Point3D[][] imageAB;
    private final List<Point3D> pointsB;
    private final List<Point3D> pointsA;

    private static final int MAX_SUB_ITERE_X = 10;

    public DistanceProxLinear4(List<Point3D> A, List<Point3D> B, Dimension2D aDimReal, Dimension2D bDimReal,
                               boolean opt1, boolean optimizeGrid) {
        super(A, B, aDimReal, bDimReal, opt1, optimizeGrid);
        imageAB = new Point3D[((int) aDimReal.getWidth())][(int) aDimReal.getHeight()];

        pointsA = A.subList(0, A.size() - 1);
        pointsB = B.subList(0, B.size() - 1);
        List<Point3D> newA = new ArrayList<>();
        List<Point3D> newB = new ArrayList<>();
        double eps = 1. / Math.max(bDimReal.getWidth(), bDimReal.getHeight());
        double maxDist = 1. / eps;
        while (maxDist > eps) {
            for (int i = 0; i < pointsB.size(); i++) {
                for (int j = 0; j < pointsA.size(); j++) {
                    maxDist = Point3D.distance(pointsA.get(j), pointsA.get(i));
                    if (maxDist > eps) {
                        Point3D pB = pointsB.get(i).plus(pointsB.get(j)).mult(0.5);
                        Point3D pA = pointsA.get(i).plus(pointsA.get(j)).mult(0.5);

                        newA.add(pA);
                        newB.add(pB);
                    }
                }
            }
            pointsA.addAll(newA);
            pointsB.addAll(newB);
            newA = new ArrayList<>();
            newB = new ArrayList<>();
        }
    }


    @Override
    public Point3D findAxPointInB(double u, double v) {
        return findAxPointInBal5(u, v);
    }

    private Point3D findAxPointInBal5(double u, double v) {
        double distance = Double.MAX_VALUE;
        Point3D currentDist = new Point3D(u, v, 0.0);
        int j = 0;
        double dist = distance;
        for (int i = 0; i < pointsB.size(); i++) {
            Point3D currentB = pointsB.get(i);
            if ((dist = Point3D.distance(currentDist, currentB)) < distance) {
                distance = dist;
                j = i;
            }

        }
        return pointsA.get(j);
    }
}
