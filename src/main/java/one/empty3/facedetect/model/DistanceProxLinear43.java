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
import java.util.Arrays;
import java.util.List;

public class DistanceProxLinear43 extends DistanceBezier2 {
    private final Point3D[][] imageCB;
    private final List<Point3D> C;
    private final Dimension2D cDimReal;
    private Point3D[][] imageAB;
    private List<Point3D> pointsB;
    private List<Point3D> pointsA;
    boolean[][] checkedListA;
    private static final int MAX_SUB_ITERE_X = 10;
    private List<Point3D> pointsC;
    boolean[][] checkedListC;
    private double computeTimeMax;

    /***
     * Algorithme Chercher le poil dans la tête pressée d'Ariane
     * @param A
     * @param B
     * @param aDimReal
     * @param bDimReal
     * @param opt1
     * @param optimizeGrid
     */
    public DistanceProxLinear43(List<Point3D> A, List<Point3D> B, List<Point3D> C, Dimension2D aDimReal, Dimension2D bDimReal, Dimension2D cDimReal,
                               boolean opt1, boolean optimizeGrid) {
        super(A, B, aDimReal, bDimReal, opt1, optimizeGrid);
        this.C  = C;
        this.cDimReal = cDimReal;
        imageAB = new Point3D[((int) bDimReal.getWidth())][(int) bDimReal.getHeight()];
        imageCB = new Point3D[((int) bDimReal.getWidth())][(int) bDimReal.getHeight()];
        if(cDimReal!=null && C.size()>0)
            init_1();
    }

    public void setComputeMaxTime(double value) {
        this.computeTimeMax = value;
    }
    public double getComputeTimeMax() {
        return computeTimeMax;
    }

    int nIteration0 = 7;
    int nNeighbors = 3;

    public void init_1() {

        pointsA = A.subList(0, A.size() - 1);
        pointsB = B.subList(0, B.size() - 1);
        pointsC = C.subList(0, C.size() - 1);
        List<Point3D> newA = new ArrayList<>();
        List<Point3D> newB = new ArrayList<>();
        List<Point3D> newC = new ArrayList<>();
        double eps = 1. / Math.min(aDimReal.getWidth(), aDimReal.getHeight());
        checkedListA = new boolean[(int) aDimReal.getWidth()][(int) aDimReal.getHeight()];
        checkedListC = new boolean[(int) cDimReal.getWidth()][(int) cDimReal.getHeight()];
       /* if(jpgRight!=null) {
            checkedListRight = new boolean[(int) jpgRight.getWidth()][(int) jpgRight.getHeight()];
            for (int i = 0; i < checkedListRight.length; i++) {
                Arrays.fill(checkedListRight[i], false);
            }
        }*/
        Point3D[][] pointAddedA = new Point3D[(int) aDimReal.getWidth()][(int) aDimReal.getHeight()];
        Point3D[][] pointAddedC = new Point3D[(int) aDimReal.getWidth()][(int) aDimReal.getHeight()];
        int[][] gen = new int[(int) aDimReal.getWidth()][(int) aDimReal.getHeight()];
        ;
        double maxDist = 1. / (1 / eps + 1);
        for (int i = 0; i < checkedListA.length; i++) {
            Arrays.fill(checkedListA[i], false);
        }
        for (int i = 0; i < checkedListC.length; i++) {
            Arrays.fill(checkedListC[i], false);
        }
        int iteration = 1;
        double N = 1 / eps;
        boolean stepNewPoints = false;
        boolean firstStep = true;
        //while (maxDist > eps && (stepNewPoints || firstStep)) {
        int occ = -1;
        int oldoccc = 0;
        double surfaceOccupied = 0.1;
        double surfaceOccupiedOld = 0.01;
        int step = 0;
        int sizeIndexStart = 0;


        long timeStart = System.nanoTime();

        long timeElapsed = 0;

        boolean ended = false;

        while (occ != oldoccc && !ended) {
            oldoccc = occ;
            stepNewPoints = false;
            firstStep = false;
            maxDist = 0;
            int sizeA = pointsA.size();
            int sizeB = pointsB.size();
            int sizeC = pointsB.size();
            for (int i = 0; i < sizeA; i++) {
                if (iteration == 0) {
                    gen[(int) pointsA.get(i).getX()][(int) pointsA.get(i).getY()] = iteration;
                    pointAddedA[(int) pointsA.get(i).getX()][(int) pointsA.get(i).getY()] = pointsA.get(i);
                    pointAddedC[(int) pointsC.get(i).getX()][(int) pointsC.get(i).getY()] = pointsC.get(i);
                    checkedListA[(int) pointsA.get(i).getX()][(int) pointsA.get(i).getY()] = true;
                    int j1 = (int) (pointsB.get(i).getX() * bDimReal.getWidth());
                    int j2 = (int) (pointsB.get(i).getY() * bDimReal.getHeight());
                    imageAB[j1][j2] = pointsA.get(i);
                    imageCB[j1][j2] = pointsC.get(i);
                    newA.add(pointsA.get(i));
                    newB.add(pointsB.get(i));
                    newC.add(pointsC.get(i));
                    continue;
                }
                List<Point3D> candidatesA = new ArrayList<>();
                List<Point3D> candidatesB = new ArrayList<>();
                List<Point3D> candidatesC = new ArrayList<>();
                double distCand = Double.MAX_VALUE;
                // Find the nearest mapped point
                double norm = Double.MAX_VALUE;
                for (int k = sizeIndexStart; k < sizeA; k++) {
                    timeElapsed = System.nanoTime()-timeStart;
                    if(timeElapsed> computeTimeMax) { // 1 min max.
                        ended = true;
                        break;
                    }
                    if (k == i)
                        continue;
                    norm = pointsA.get(i).moins(pointsA.get(k)).norme();
                    if (norm < distCand && norm > 0)// &&
                    //gen[(int) pointsA.get(i).getX()][(int) pointsA.get(i).getY()] == gen[(int) pointsA.get(k).getX()][(int) pointsA.get(k).getY()]) {
                    {
                        distCand = norm;
                        candidatesA.add(pointsA.get(k));
                        candidatesB.add(pointsB.get(k));
                        candidatesC.add(pointsC.get(k));
                    }

                }
                int m = 0;
                int checked = 0;
                int CHECKED_MAX = 3;
                while (checked < CHECKED_MAX && !candidatesA.isEmpty() && candidatesA.size() - m > 0) {
                    int indexA = candidatesA.size() - m - 1;
                    int indexB = candidatesB.size() - m - 1;
                    int indexC = candidatesC.size() - m - 1;
                    Point3D candidateA = candidatesA.get(indexA);
                    Point3D candidateB = candidatesB.get(indexB);
                    Point3D candidateC = candidatesC.get(indexC);

                    Point3D pC = (pointsC.get(i).plus(candidateC)).mult(0.5);
                    Point3D pB = (pointsB.get(i).plus(candidateB)).mult(0.5);
                    Point3D pA = (pointsA.get(i).plus(candidateA)).mult(0.5);


                    int i1 = (int) (pA.getX() * aDimReal.getWidth());
                    int i2 = (int) (pA.getY() * aDimReal.getHeight());
                    int j1 = (int) (pB.getX() * bDimReal.getWidth());
                    int j2 = (int) (pB.getY() * bDimReal.getHeight());
                    int k1 = (int) (pC.getX() * cDimReal.getWidth());
                    int k2 = (int) (pC.getY() * cDimReal.getHeight());

                    if (!checkedListA[i1][i2]) {
                        checkedListA[i1][i2] = true;
                        checkedListC[k1][k2] = true;
                        //checkedListRight[]
                        stepNewPoints = true;
                        newA.add(pA);
                        newB.add(pB);
                        newC.add(pC);
                        imageAB[j1][j2] = pA;
                        imageCB[j1][j2] = pA;
                        gen[i1][i2] = iteration;
                        pointAddedA[i1][i2] = pA;
                        pointAddedC[k1][k2] = pA;
                        checked++;
                    }
                    m++;
                }

            }

            sizeIndexStart = pointsA.size();

            pointsA.addAll(newA);
            pointsB.addAll(newB);
            pointsC.addAll(newC);

            newA.clear();
            newB.clear();
            newC.clear();

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



    @Override
    public Point3D findAxPointInB(double u, double v) {
        return findAxPointInBa12(u, v);
    }

    private Point3D findAxPointInBa11(double u, double v) {
        return imageAB[(int) (u * bDimReal.getWidth())][(int) (v * bDimReal.getHeight())] == null ? null
                : imageAB[(int) (u * bDimReal.getWidth())][(int) (v * bDimReal.getHeight())];
    }
/*
    private Point3D findAxPointInBa12(double u, double v) {
        Point3D searchedB = new Point3D(u*(bDimReal.getWidth()-1), v*(bDimReal.getHeight()-1), 0.0);
        for (int i = 0; i < pointsB.size(); i++) {
            Point3D currentB = pointsB.get(i).multDot(new Point3D(bDimReal.getWidth()-1, bDimReal.getHeight()-1, 0.0));
            if((int)(double)(searchedB.getX())==(int)(double)(currentB.getX())&&(int)(double)(searchedB.getY())==(int)(double)(currentB.getY()))
                return pointsA.get(i);
        }
        return null;
    }
     Point3D findAxPointInBa13(double u, double v) {
        Point3D searchedC = new Point3D(u*(cDimReal.getWidth()-1), v*(cDimReal.getHeight()-1), 0.0);
        for (int i = 0; i < pointsB.size(); i++) {
            Point3D currentB = pointsB.get(i).multDot(new Point3D(bDimReal.getWidth()-1, bDimReal.getHeight()-1, 0.0));
            if((int)(double)(searchedC.getX())==(int)(double)(currentB.getX())&&(int)(double)(searchedC.getY())==(int)(double)(currentB.getY()))
                return pointsC.get(i);
        }
        return null;
    }*/


    private Point3D findAxPointInBa12(double u, double v) {
        Point3D searchedB = new Point3D(u, v, 0.0);
        Point3D found = searchedB;
        if(pointsB!=null&&pointsA!=null) {
            double dist = Double.MAX_VALUE;
            for (int i = 0; i < pointsB.size(); i++) {
                if (pointsB.get(i).moins(searchedB).norme() < dist) {
                    found = pointsA.get(i);
                    dist = pointsB.get(i).moins(searchedB).norme();
                }
            }
        }
        return searchedB;
    }
    Point3D findAxPointInBa13(double u, double v) {
        Point3D searchedB = new Point3D(u, v, 0.0);
        Point3D found = searchedB;
        if(pointsB!=null&&pointsC!=null) {
            double dist = Double.MAX_VALUE;
            for (int i = 0; i < pointsB.size(); i++) {
                if (pointsB.get(i).moins(searchedB).norme() < dist) {
                    found = pointsC.get(i);
                    dist = pointsB.get(i).moins(searchedB).norme();
                }
            }
        }
        return found;
    }
}
