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
import one.empty3.library.lang.Node;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

public class DistanceProxLinear4 extends DistanceBezier2 {
    private Point3D[][] imageAB;
    private List<Point3D> pointsB;
    private List<Point3D> pointsA;

    private static final int MAX_SUB_ITERE_X = 10;

    /***
     * Algorithme Chercher le poil dans la tête pressée d'Ariane
     * @param A
     * @param B
     * @param aDimReal
     * @param bDimReal
     * @param opt1
     * @param optimizeGrid
     */
    public DistanceProxLinear4(List<Point3D> A, List<Point3D> B, Dimension2D aDimReal, Dimension2D bDimReal,
                               boolean opt1, boolean optimizeGrid) {
        super(A, B, aDimReal, bDimReal, opt1, optimizeGrid);
        imageAB = new Point3D[((int) aDimReal.getWidth())][(int) aDimReal.getHeight()];
        init_1();
    }

    public void init_1() {

        pointsA = A.subList(0, A.size() - 1);
        pointsB = B.subList(0, B.size() - 1);
        List<Point3D> newA = new ArrayList<>();
        List<Point3D> newB = new ArrayList<>();
        double eps = 1. / Math.min(aDimReal.getWidth(), aDimReal.getHeight());
        boolean[][] checkedList = new boolean[(int) aDimReal.getWidth()][(int) aDimReal.getHeight()];
        double maxDist = 1. / (1 / eps + 1);
        for (int i = 0; i < checkedList.length; i++) {
            for (int j = 0; j < checkedList[i].length; j++) {
                checkedList[i][j] = false;

            }
        }
        boolean stepNewPoints = false;
        boolean firstStep = true;
        while (maxDist > 0 && (stepNewPoints || firstStep)) {
            stepNewPoints = false;
            firstStep = false;
            maxDist = 0;
            int sizeA = pointsA.size();
            int sizeB = pointsB.size();
            for (int i = 0; i < sizeA; i++) {
                List<Point3D> candidatesA = new ArrayList<>();
                List<Point3D> candidatesB = new ArrayList<>();
                double distCand = Double.MAX_VALUE;
                // Find the nearest mapped point
                for (int k = 0; k < sizeA; k++) {
                    if (k == i)
                        continue;
                    double norm = pointsA.get(i).moins(pointsA.get(k)).norme();

                    if (norm < distCand && norm > 0) {
                        distCand = norm;
                        candidatesA.add(pointsA.get(k));
                        candidatesB.add(pointsB.get(k));
                    }
                }
                if (maxDist < distCand) {
                    maxDist = distCand;
                }
                if (!candidatesA.isEmpty()) {
                    for (int m = 0; m < 1 && candidatesA.size() - m > 0; m++) {
                        int indexA = candidatesA.size() - m - 1;
                        int indexB = candidatesB.size() - m - 1;
                        Point3D candidateA = candidatesA.get(indexA);
                        Point3D candidateB = candidatesB.get(indexB);
                        double dist = Point3D.distance(pointsA.get(i), candidateA);

                        Point3D pB = (pointsB.get(i).plus(candidateB)).mult(0.5);
                        Point3D pA = (pointsA.get(i).plus(candidateA)).mult(0.5);

                        int i1 = (int) (pA.getX() * aDimReal.getWidth());
                        int i2 = (int) (pA.getY() * aDimReal.getHeight());

                        if (!checkedList[i1][i2]) {
                            checkedList[i1][i2] = true;
                            stepNewPoints = true;
                            newA.add(pA);
                            newB.add(pB);

                        }

                    }
                }
            }
            pointsA.addAll(newA);
            pointsB.addAll(newB);

            System.out.println("Number of points : " + pointsA.size());
        }

        System.out.println("Compute texturing ended");
    }

    public void init_2() {
        imageAB = new Point3D[((int) aDimReal.getWidth())][(int) aDimReal.getHeight()];

        pointsA = A.subList(0, A.size() - 1);
        pointsB = B.subList(0, B.size() - 1);
        List<Point3D> newA = new ArrayList<>();
        List<Point3D> newB = new ArrayList<>();
        double eps = 1. / Math.max(bDimReal.getWidth(), bDimReal.getHeight());

        double maxDist = 1. / eps;
        double globalLocalMaxDist = maxDist;
        double[][] localMaxDist = new double[(int) aDimReal.getWidth()][(int) aDimReal.getHeight()];
        while (maxDist > eps) {
            for (int i = 0; i < pointsB.size(); i++) {
                for (int j = 0; j < pointsA.size(); j++) {
                    if (i != j) {
                        Point3D pi = pointsA.get(i);
                        Point3D pj = pointsA.get(i);
                        double dist = Point3D.distance(pointsA.get(j), pointsA.get(i));
                        if (dist < localMaxDist[i][j] && dist < maxDist) {
                            localMaxDist[i][j] = dist;
                            Point3D pB = pointsB.get(i).plus(pointsB.get(j)).mult(0.5);
                            Point3D pA = pointsA.get(i).plus(pointsA.get(j)).mult(0.5);

                            newA.add(pA);
                            newB.add(pB);

                            if (localMaxDist[i][j] < maxDist && globalLocalMaxDist < maxDist
                                    && localMaxDist[i][j] < globalLocalMaxDist) {
                                globalLocalMaxDist = localMaxDist[i][j];
                            }
                        }
                    }
                }
            }
            for (int i = 0; i < localMaxDist.length; i++) {
                for (int j = 0; j < localMaxDist[i].length; j++) {
                    if (maxDist > localMaxDist[i][j]) {
                        maxDist = localMaxDist[i][j];
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
