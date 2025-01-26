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
import java.util.ArrayList;
import java.util.List;

public class DistanceProxLinear42 extends DistanceBezier2 {
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
    public DistanceProxLinear42(List<Point3D> A, List<Point3D> B, Dimension2D aDimReal, Dimension2D bDimReal,
                                boolean opt1, boolean optimizeGrid) {
        super(A, B, aDimReal, bDimReal, opt1, optimizeGrid);
        imageAB = new Point3D[((int) bDimReal.getWidth())][(int) bDimReal.getHeight()];
        init_1();
    }

    int nIteration0 = 7;
    int nNeighbors = 3;

    public void init_1() {

        pointsA = A.subList(0, A.size() - 1);
        pointsB = B.subList(0, B.size() - 1);
        List<Point3D> newA = new ArrayList<>();
        List<Point3D> newB = new ArrayList<>();
        double eps = 1. / Math.min(aDimReal.getWidth(), aDimReal.getHeight());
        boolean[][] checkedList = new boolean[(int) aDimReal.getWidth()][(int) aDimReal.getHeight()];
        Point3D[][] pointAdded = new Point3D[(int) aDimReal.getWidth()][(int) aDimReal.getHeight()];
        int[][] gen = new int[(int) aDimReal.getWidth()][(int) aDimReal.getHeight()];
        ;
        double maxDist = 1. / (1 / eps + 1);
        for (int i = 0; i < checkedList.length; i++) {
            for (int j = 0; j < checkedList[i].length; j++) {
                checkedList[i][j] = false;

            }
        }
        int iteration = 1;
        double N = 1 / eps;
        boolean stepNewPoints = false;
        boolean firstStep = true;
        //while (maxDist > eps && (stepNewPoints || firstStep)) {
        int occ = -1;
        int oldoccc = 0;
        double surfaceOccupied = 0.0;
        double surfaceOccupiedOld = 0.0;
        int step = 0;
        while (surfaceOccupied >= surfaceOccupiedOld * 1.01 && occ != oldoccc) {
            oldoccc = occ;
            stepNewPoints = false;
            firstStep = false;
            maxDist = 0;
            int sizeA = pointsA.size();
            int sizeB = pointsB.size();
            for (int i = 0; i < sizeA; i++) {
                if (iteration == 0) {
                    gen[(int) pointsA.get(i).getX()][(int) pointsA.get(i).getY()] = iteration;
                    pointAdded[(int) pointsA.get(i).getX()][(int) pointsA.get(i).getY()] = pointsA.get(i);
                    checkedList[(int) pointsA.get(i).getX()][(int) pointsA.get(i).getY()] = true;
                    int j1 = (int) (pointsB.get(i).getX() * bDimReal.getWidth());
                    int j2 = (int) (pointsB.get(i).getY() * bDimReal.getHeight());
                    imageAB[j1][j2] = pointsA.get(i);
                    newA.add(pointsA.get(i));
                    newB.add(pointsB.get(i));
                    continue;
                }
                List<Point3D> candidatesA = new ArrayList<>();
                List<Point3D> candidatesB = new ArrayList<>();
                double distCand = Double.MAX_VALUE;
                // Find the nearest mapped point
                double norm = Double.MAX_VALUE;
                for (int k = 0; k < sizeA; k++) {
                    if (k == i)
                        continue;
                    norm = pointsA.get(i).moins(pointsA.get(k)).norme();
                    if (norm < distCand && norm > 0 &&
                            gen[(int) pointsA.get(i).getX()][(int) pointsA.get(i).getY()] == gen[(int) pointsA.get(k).getX()][(int) pointsA.get(k).getY()]) {
                        distCand = norm;
                        candidatesA.add(pointsA.get(k));
                        candidatesB.add(pointsB.get(k));
                    }

                }
                int m = 0;
                int checked = 0;
                int CHECKED_MAX = 2;
                while (checked < CHECKED_MAX && !candidatesA.isEmpty() && candidatesA.size() - m > 0) {
                    int indexA = candidatesA.size() - m - 1;
                    int indexB = candidatesB.size() - m - 1;
                    Point3D candidateA = candidatesA.get(indexA);
                    Point3D candidateB = candidatesB.get(indexB);

                    Point3D pB = (pointsB.get(i).plus(candidateB)).mult(0.5);
                    Point3D pA = (pointsA.get(i).plus(candidateA)).mult(0.5);


                    int i1 = (int) (pA.getX() * aDimReal.getWidth());
                    int i2 = (int) (pA.getY() * aDimReal.getHeight());
                    int j1 = (int) (pB.getX() * bDimReal.getWidth());
                    int j2 = (int) (pB.getY() * bDimReal.getHeight());

                    if (!checkedList[i1][i2]) {
                        checkedList[i1][i2] = true;
                        stepNewPoints = true;
                        newA.add(pA);
                        newB.add(pB);
                        imageAB[j1][j2] = pA;
                        gen[i1][i2] = iteration;
                        pointAdded[i1][i2] = pA;
                        checked++;
                    }
                    m++;
                }


            }
            pointsA.addAll(newA);
            pointsB.addAll(newB);

            newA.clear();
            newB.clear();

            iteration++;
            occ = 0;
            for (int i = 0; i < imageAB.length; i++) {
                for (int j = 0; j < imageAB[i].length; j++) {
                    if (imageAB[i][j] != null) {
                        occ++;
                    }
                }
            }

            step++;

            surfaceOccupiedOld = surfaceOccupied;
            surfaceOccupied = 1.0 * occ / imageAB.length / imageAB[0].length;
            System.out.println("Number of points : " + pointsA.size() + "\n\t" + "Iterations : " + iteration
                    + "\n\toccupied (%) : " + surfaceOccupied);
            System.out.println("Thread n°" + Thread.currentThread().getId());


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
        return findAxPointInBa12(u, v);
    }

    private Point3D findAxPointInBa11(double u, double v) {
        return imageAB[(int) (u * bDimReal.getWidth())][(int) (v * bDimReal.getHeight())] == null ? Point3D.O0
                : imageAB[(int) (u * bDimReal.getWidth())][(int) (v * bDimReal.getHeight())];
    }

    private Point3D findAxPointInBa12(double u, double v) {
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
