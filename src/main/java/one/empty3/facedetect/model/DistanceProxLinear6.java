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
import one.empty3.library.TRI;
import one.empty3.library.core.nurbs.SurfaceParametriquePolynomiale;

import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

public class DistanceProxLinear6 extends DistanceBezier2 {
    private final Point3D[][] imageAB;
    private List<Edge> edges = new ArrayList<>();
    // Java program to illustrate Delaunay Triangulation
// algorithm
// structure to store the edge of the triangulation

    class Edge {
        int a, b;

        Edge(int a, int b) {
            this.a = a;
            this.b = b;
        }
    }


// structure to store the point in 2D space

    static
    class
    Point {

        double
                x, y;


        Point(
                double
                        x,
                double
                        y) {

            this
                    .x = x;

            this
                    .y = y;

        }

    }


    //  Driver Class
    public class DelaunayTriangulation {


        private static final double EPSILON = 1e-12;


// function to calculate the cross product of two

// vectors

        private
        static double
        crossProduct(Point A, Point B) {

            return
                    A.x * B.y - A.y * B.x;

        }


// function to check if the point P is inside the circle

// defined by points A, B, and C

        private
        static boolean
        insideCircle(Point3D A, Point3D B,

                     Point3D C, Point3D P) {

            double
                    ax = A.getX() - P.getX();

            double
                    ay = A.getY() - P.getY();

            double
                    bx = B.getX() - P.getX();

            double
                    by = B.getY() - P.getY();

            double
                    cx = C.getX() - P.getX();

            double
                    cy = C.getY() - P.getY();


            double
                    a2 = ax * ax + ay * ay;

            double
                    b2 = bx * bx + by * by;

            double
                    c2 = cx * cx + cy * cy;


            return
                    (ax * (by - cy) + bx * (cy - ay)

                            + cx * (ay - by))

                            >= EPSILON;

        }


// main function to perform Delaunay triangulation

        public List<Edge> triangulate(Point3D[] points) {

            int n = points.length;

            List<Edge> edges = new ArrayList<>();


// sorting the points by x-coordinate

            Point3D[] sorted = new Point3D[n];

            for (int i = 0; i < n; i++) {
                sorted[i] = points[i];
            }

            sorted = sortByX(sorted,
                    0
                    , n -
                            1
            );


// creating the lower hull

            for
            (
                    int
                    i =
                    0
                    ; i < n; i++) {

                while
                (edges.size() >=
                        2
                ) {

                    int
                            j = edges.size() -
                            2;

                    int
                            k = edges.size() -
                            1;

                    Point3D A = sorted[edges.get(j).a];

                    Point3D B = sorted[edges.get(j).b];

                    Point3D C = sorted[edges.get(k).b];


                    if
                    (crossProduct(

                            new
                                    Point(B.getX() - A.getX(), B.getY() - A.getY()),

                            new
                                    Point(C.getX() - B.getX(), C.getY() - B.getY()))

                            >
                            0
                    ) {

                        break
                                ;

                    }


                    edges.remove(edges.size() -
                            1
                    );

                }

                edges.add(new Edge(edges.size(), i));

            }

            int
                    lower = edges.size();

// creating the upper hull

            for
            (
                    int
                    i = n -
                            2, t = lower +
                            1
                    ; i >=
                            0
                    ; i--) {

                while
                (edges.size() >= t) {

                    int
                            j = edges.size() -
                            2;

                    int
                            k = edges.size() -
                            1;

                    Point3D A = sorted[edges.get(j).a];

                    Point3D B = sorted[edges.get(j).b];

                    Point3D C = sorted[edges.get(k).b];


                    if
                    (crossProduct(new Point(B.getX() - A.getX(), B.getY() - A.getY()), new Point(C.getX() - B.getX(), C.getY() - B.getY()))

                            >
                            0
                    ) {

                        break
                                ;

                    }


                    edges.remove(edges.size() -
                            1
                    );

                }

                edges.add(new Edge(i, edges.size()));

            }


// removing the duplicate edges from the hull

            edges.remove(edges.size() -
                    1
            );


// creating the triangulation

            List<Edge> result =
                    new
                            ArrayList<>();

            for
            (
                    int
                    i =
                    0
                    ; i < edges.size(); i++) {

                int
                        a = edges.get(i).a;

                int
                        b = edges.get(i).b;

                Point3D A = sorted[a];

                Point3D B = sorted[b];

                boolean
                        flag =
                        true;


                for
                (
                        int
                        j =
                        0
                        ; j < n; j++) {

                    if
                    (j == a || j == b) {

                        continue
                                ;

                    }

                    Point3D P = sorted[j];

                    if
                    (insideCircle(A, B, P,

                            sorted[a + b >>
                                    1
                                    ])) {

                        flag =
                                false
                        ;

                        break
                                ;

                    }

                }

                if
                (flag) {

                    result.add(
                            new
                                    Edge(a, b));

                }

            }


            return
                    result;

        }


// function to sort the points by x-coordinate

        private
        static Point3D[] sortByX(Point3D[] points,

                                 int
                                         start,
                                 int
                                         end) {

            if
            (start >= end) {

                return
                        points;

            }

            int
                    pivot = partition(points, start, end);

            sortByX(points, start, pivot -
                    1
            );

            sortByX(points, pivot +
                            1
                    , end);

            return
                    points;

        }


// function to partition the points for quick sort

        private
        static int
        partition(Point3D[] points,
                  int
                          start,

                  int
                          end) {

            Point3D pivot = points[end];

            int
                    i = start -
                    1;

            for
            (
                    int
                    j = start; j <= end -
                    1
                    ; j++) {

                if
                (points[j].getX() <= pivot.getX()) {

                    i++;

                    Point3D temp = points[i];

                    points[i] = points[j];

                    points[j] = temp;

                }

            }

            Point3D temp = points[i +
                    1
                    ];

            points[i +
                    1
                    ] = points[end];

            points[end] = temp;

            return
                    i +
                            1
                    ;

        }
    }


    private static final int MAX_SUB_ITERE_X = 10;

    public DistanceProxLinear6(List<Point3D> A, List<Point3D> B, Dimension2D aDimReal, Dimension2D bDimReal,
                               boolean opt1, boolean optimizeGrid) {
        super(A, B, aDimReal, bDimReal, opt1, optimizeGrid);
        List<Edge> triangularize = triangularize(A, B, aDimReal, bDimReal);

        List<TRI> trianglesA = new ArrayList<>();
        List<TRI> trianglesB = new ArrayList<>();


        for (int i = 0; i < triangularize.size() - 2; i += 2) {
            TRI t1A = new TRI(A.get(triangularize.get(i).a), A.get(triangularize.get(i).b), A.get(triangularize.get(i + 1).a));
            TRI t2A = new TRI(A.get(triangularize.get(i + 1).b), A.get(triangularize.get(i + 2).a), A.get(triangularize.get(i + 2).b));
            TRI t1B = new TRI(B.get(triangularize.get(i).a), B.get(triangularize.get(i).b), B.get(triangularize.get(i + 1).a));
            TRI t2B = new TRI(B.get(triangularize.get(i + 1).b), B.get(triangularize.get(i + 2).a), B.get(triangularize.get(i + 2).b));
            trianglesA.add(t1A);
            trianglesA.add(t2A);
            trianglesB.add(t1B);
            trianglesB.add(t2B);
        }

        imageAB = new Point3D[((int) aDimReal.getWidth())][(int) aDimReal.getHeight()];

        double imageABdistMin = (double) imageAB.length / A.size() / 100;

        for (int i = 0; i < trianglesA.size(); i++) {
            TRI currentA = trianglesA.get(i);
            TRI currentB = trianglesB.get(i);
            for (int u = 0; u < 1.0; u += imageABdistMin) {
                for (int v = 0; v < 1.0; v += imageABdistMin) {

                    Point3D plus = currentA.getSommet().getElem(0).plus(
                            currentA.getSommet().getElem(0).moins(currentA.getSommet().getElem(1)).mult(u));
                    Point3D currentPointA = plus.plus(currentA.getSommet().getElem(2).plus(plus.moins(currentA.getSommet().getElem(2)).mult(v)));
                    plus = currentB.getSommet().getElem(0).plus(
                            currentB.getSommet().getElem(0).moins(currentB.getSommet().getElem(1)).mult(u));
                    Point3D currentPointB = plus.plus(currentB.getSommet().getElem(2).plus(plus.moins(currentB.getSommet().getElem(2)).mult(v)));

                    if (currentPointA.getX() >= 0 && currentPointA.getX() < aDimReal.getWidth() && currentPointB.getY() >= 0 && currentPointB.getY() >= 0) {
                        imageAB[(int) currentPointB.getX()][(int) currentPointB.getY()]
                                = new Point3D((double) currentPointA.getX(), (double) currentPointA.getY(), currentPointA.getZ());
                    }
                }

            }
        }
    }

    private List<Edge> triangularize(List<Point3D> a, List<Point3D> b, Dimension2D aDimReal, Dimension2D bDimReal) {

        double x, y;
        int n = 0;
        n = a.size();


// array created

        Point3D[] points = new Point3D[n];


// Input coordinates

        for (int i = 0; i < n; i++) {
            x = A.get(i).getX();
            y = A.get(i).getX();
            points[i] = new Point3D(x, y, 0.0);

        }


// List declared

        List<Edge> edges = new DelaunayTriangulation().triangulate(points);

        System.out.println(
                "Triangulated Edges:"
        );
        return this.edges = edges;
    }


    @Override
    public Point3D findAxPointInB(double u, double v) {
/*
        if (borderNotIinitialized || right == -1 || bottom == -1) {
            for (int i = 0, listBXSize = listBX.size(); i < listBXSize; i++) {
                for (int j = 0, listBYSize = listBY.size(); j < listBYSize; j++) {
                    Point3D p2 = findAxPointInBal2(listBX.get(i), listBY.get(j));
                    if (p2.getX() > right) {
                        right = p2.getX();
                    }
                    if (p2.getY() > bottom) {
                        bottom = p2.getY();
                    }
                }
            }
            borderNotIinitialized = false;
        }
        if (!(borderNotIinitialized || right == -1 || bottom == -1)) {
            return findAxPointInBal1(u / right, v / bottom)
//                    .multDot(new Point3D(1.0 / right, 1.0 / bottom, 0.0))
                    .multDot(new Point3D(1.0 / aDimReal.getWidth(), 1. / aDimReal.getHeight(), 0.0))
                    .multDot(new Point3D(bDimReal.getWidth(), bDimReal.getHeight(), 0.0));
        } else {
   */
        return findAxPointInBal4(u, v);
        //
        //}
    }

    private Point3D findAxPointInBal4(double u, double v) {
        Point3D pos = imageAB[(int) (u * imageAB.length)][(int) (v * imageAB[0].length)];
        if (pos == null)
            return new Point3D(u, v);
        return pos;
    }

    private Point3D findAxPointInBal1(double u, double v) {
        Point3D pb = nearLandmark(u, v);
        //pb = new Point3D(Math.max(0, Math.min(pb.get(0), listBX.size() - 1)), Math.max(0, Math.min(pb.get(1), listBY.size() - 1)), 0.0);
        Point3D pa = surfaceA.getCoefficients().getElem((int) (double) pb.getX(), (int) (double) pb.get(1));
        return pa;
    }

    private Point3D findAxPointInBal2(double u, double v) {
        Point3D pb = nearLandmark(u, v);
        pb = new Point3D(Math.max(0, Math.min(pb.get(0), listBX.size() - 1)), Math.max(0, Math.min(pb.get(1), listBY.size() - 1)), 0.0);
        Point3D pa = surfaceA.calculerPoint3D((double) pb.getX(), (double) pb.getY());
        return pa;
    }

    private Point3D findAxPointInBal3(double u, double v) {
        Point3D pb = nearLandmark3(u, v);
        pb = new Point3D(Math.max(0, Math.min(pb.get(0), listBX.size() - 1)), Math.max(0, Math.min(pb.get(1), listBY.size() - 1)), 0.0);
        Point3D pa = surfaceA.getCoefficients().getElem((int) (double) pb.getX(), (int) (double) pb.get(1));
        return pa;
    }

    /***
     *
     * @param u
     * @param v
     * @return
     */
    private Point3D nearLandmark(double u, double v) {
        Point3D uv = new Point3D(u, v, 0.0);
        double distance = Double.MAX_VALUE;
        int indexI = -1, indexJ = -1;
        for (int i = 0; i < listBX.size(); i++) {
            for (int j = 0; j < listBY.size(); j++) {
                if (Point3D.distance(surfaceB.getCoefficients().getElem(i, j), uv) < distance) {
                    indexI = i;
                    indexJ = j;
                    distance = Point3D.distance(surfaceB.getCoefficients().getElem(i, j), uv);
                }
            }
        }
        Point3D point3Dij = new Point3D((double) indexI, (double) indexJ, 0.0);
        return precision(point3Dij, u, v);//point3Dij
    }

    /***
     *
     * @param u
     * @param v
     * @return
     */
    private Point3D nearLandmark2(double u, double v) {
        Point3D uv = new Point3D(u, v, 0.0);
        double distance = Double.MAX_VALUE;
        int indexI = -1, indexJ = -1, indexK = -1, indexL = -1;
        Point3D u2 = new Point3D(u, v, 0.0);
        Point3D p2 = uv;
        for (int i = 0; i < listBX.size(); i++) {
            for (int j = 0; j < listBY.size(); j++) {
                for (int k = 0; k < MAX_SUB_ITERE_X; k++) {
                    for (int l = 0; l < MAX_SUB_ITERE_X; l++) {
                        Point3D p = precision2(i, j, k, l);
                        p = surfaceB.calculerPoint3D(p.getX(), p.getY());
                        Double dist = Point3D.distance(p, uv);
                        if (dist < distance) {
                            indexI = i;
                            indexJ = j;
                            distance = dist;
                            indexK = k;
                            indexL = l;

                            u2 = new Point3D((double) i + k / 10., (double) j + l / 10., 0.0);
                            p2 = p;
                        }
                    }
                }
            }
        }
        return p2;
    }

    private Point3D nearLandmark3(double u, double v) {
        Point3D uv = new Point3D(u, v, 0.0);
        double distance = Double.MAX_VALUE;
        int indexI = -1, indexJ = -1, indexK = -1, indexL = -1;
        Point3D u2 = new Point3D(u, v, 0.0);
        Point3D p2 = uv;
        for (int i = 0; i < listBX.size(); i++) {
            for (int j = 0; j < listBY.size(); j++) {
                double sizeXb = i > 0 ? (listAX.get(i) - listBX.get(i - 1)) : (listBX.get(1) - listBX.get(0));
                double sizeYb = j > 0 ? (listBY.get(j) - listBY.get(j - 1)) : (listBY.get(1) - listBY.get(0));
                Point3D p = proxima(uv, sizeXb / listBX.size(), sizeYb / listBX.size(), 0.01);
                p = surfaceB.calculerPoint3D(p.getX(), p.getY());
                Double dist = Point3D.distance(p, uv);
                if (dist < distance) {
                    indexI = i;
                    indexJ = j;
                    distance = dist;
                    p2 = p;
                }
            }
        }
        return p2;
    }

    Point3D proxima(Point3D ij, double stepX, double stepY, double eps) {
        Point3D[] testedPoints = {
                ij.plus(Point3D.X.mult(stepX).plus(Point3D.Y.mult(stepY))),
                ij.plus(Point3D.X.mult(-stepX).plus(Point3D.Y.mult(stepY))),
                ij.plus(Point3D.X.mult(stepX).plus(Point3D.Y.mult(-stepY))),
                ij.plus(Point3D.X.mult(-stepX).plus(Point3D.Y.mult(-stepY))),
                ij,
                ij.plus(Point3D.X.mult(stepX / 2).plus(Point3D.Y.mult(stepY / 2))),
                ij.plus(Point3D.X.mult(-stepX / 2).plus(Point3D.Y.mult(stepY / 2))),
                ij.plus(Point3D.X.mult(stepX / 2).plus(Point3D.Y.mult(-stepY / 2))),
                ij.plus(Point3D.X.mult(-stepX / 2).plus(Point3D.Y.mult(-stepY / 2))),

        };
        double distance = Double.MAX_VALUE;
        Point3D res = ij;
        if (res.getX() < 0 || res.getY() < 0)
            System.exit(1);
        for (Point3D ij1 : testedPoints) {
            Point3D aCompare = surfaceB.calculerPoint3D(ij1.getX(), ij1.getY());
            Double dist = Point3D.distance(ij1, aCompare);
            if (dist <= distance) {
                res = aCompare;
                distance = dist;
            }
        }
        if (distance > eps && Point3D.distance(res, ij) > Math.sqrt(stepX * stepX + stepY * stepY)) {
            return proxima(res, stepX / 2, stepY / 2, eps);
        }
        return res;
    }


    private Point3D precision2(int i1, int j1, int k1, int l1) {
        double i = i1;
        double j = j1;
        double sizeBi;
        double sizeBj;
        double sizeAi;
        double sizeAj;
        if (i >= listBX.size() - 1) {
            sizeBi = listBX.get(listBX.size() - 1) - listBX.get(listBX.size() - 2);
            sizeAi = listBX.get(listAX.size() - 1) - listAX.get(listAX.size() - 2);
            //u = listBX.get(listBX.size())-1);
        } else if (i <= 0) {
            sizeBi = listBX.get(1) - listBX.get(0);
            sizeAi = listAX.get(1) - listAX.get(0);
        } else {
            sizeBi = listBX.get((int) (i + 1)) - listBX.get((int) i);
            sizeAi = listAX.get((int) (i + 1)) - listAX.get((int) i);
        }
        if (j >= listBY.size() - 1) {
            sizeBj = listBY.get(listBX.size() - 1) - listBY.get(listBX.size() - 2);
            sizeAj = listAY.get(listAX.size() - 1) - listAY.get(listAX.size() - 2);
        } else if (j <= 0) {
            sizeBj = listBY.get(1) - listBY.get(0);
            sizeAj = listAY.get(1) - listAY.get(0);
        } else {
            sizeBj = listBY.get((int) (j + 1)) - listBY.get((int) j);
            sizeAj = listAY.get((int) (j + 1)) - listAY.get((int) j);
        }

        double totalBx = listBX.get(listBX.size() - 1) - listBX.get(0);
        double totalBy = listBY.get(listBY.size() - 1) - listBY.get(0);

        //return new Point3D(i + (u - i) / sizeBi / totalBx, j + (v - j) / sizeBj / totalBy, 0.0);
        return new Point3D(i + k1 * sizeBj / MAX_SUB_ITERE_X, j + l1 * sizeBj / MAX_SUB_ITERE_X, 0.0);

    }

    Point3D precision(Point3D ij, double u, double v) {
        double i = ij.getX();
        double j = ij.getY();
        double sizeBi;
        double sizeBj;
        double sizeAi;
        double sizeAj;
        if (i >= listBX.size() - 1) {
            sizeBi = listBX.get(listBX.size() - 1) - listBX.get(listBX.size() - 2);
            sizeAi = listBX.get(listAX.size() - 1) - listAX.get(listAX.size() - 2);
            //u = listBX.get(listBX.size())-1);
        } else if (i <= 0) {
            sizeBi = listBX.get(1) - listBX.get(0);
            sizeAi = listAX.get(1) - listAX.get(0);
        } else {
            sizeBi = listBX.get((int) (i + 1)) - listBX.get((int) i);
            sizeAi = listAX.get((int) (i + 1)) - listAX.get((int) i);
        }
        if (j >= listBY.size() - 1) {
            sizeBj = listBY.get(listBX.size() - 1) - listBY.get(listBX.size() - 2);
            sizeAj = listAY.get(listAX.size() - 1) - listAY.get(listAX.size() - 2);
        } else if (j <= 0) {
            sizeBj = listBY.get(1) - listBY.get(0);
            sizeAj = listAY.get(1) - listAY.get(0);
        } else {
            sizeBj = listBY.get((int) (j + 1)) - listBY.get((int) j);
            sizeAj = listAY.get((int) (j + 1)) - listAY.get((int) j);
        }

        double totalBx = listBX.get(listBX.size() - 1) - listBX.get(0);
        double totalBy = listBY.get(listBY.size() - 1) - listBY.get(0);

        //return new Point3D(i + (u - i) / sizeBi / totalBx, j + (v - j) / sizeBj / totalBy, 0.0);
        return ij;
    }

    Point3D precision2(Point3D ij, double k, double l) {
        double i = ij.getX();
        double j = ij.getY();
        double sizeBi;
        double sizeBj;
        double sizeAi;
        double sizeAj;
        if (i >= listBX.size() - 1) {
            sizeBi = listBX.get(listBX.size() - 1) - listBX.get(listBX.size() - 2);
            sizeAi = listBX.get(listAX.size() - 1) - listAX.get(listAX.size() - 2);
            //u = listBX.get(listBX.size())-1);
        } else if (i <= 0) {
            sizeBi = listBX.get(1) - listBX.get(0);
            sizeAi = listAX.get(1) - listAX.get(0);
        } else {
            sizeBi = listBX.get((int) (i + 1)) - listBX.get((int) i);
            sizeAi = listAX.get((int) (i + 1)) - listAX.get((int) i);
        }
        if (j >= listBY.size() - 1) {
            sizeBj = listBY.get(listBX.size() - 1) - listBY.get(listBX.size() - 2);
            sizeAj = listAY.get(listAX.size() - 1) - listAY.get(listAX.size() - 2);
        } else if (j <= 0) {
            sizeBj = listBY.get(1) - listBY.get(0);
            sizeAj = listAY.get(1) - listAY.get(0);
        } else {
            sizeBj = listBY.get((int) (j + 1)) - listBY.get((int) j);
            sizeAj = listAY.get((int) (j + 1)) - listAY.get((int) j);
        }

        double totalBx = listBX.get(listBX.size() - 1) - listBX.get(0);
        double totalBy = listBY.get(listBY.size() - 1) - listBY.get(0);

        //return new Point3D(i + (u - i) / sizeBi / totalBx, j + (v - j) / sizeBj / totalBy, 0.0);
        return ij;
    }

    /***
     *
     * @param u
     * @param v
     * @return
     */
    private Point3D nearLandmark1(double u, double v) {
        Point3D uv = new Point3D(u, v, 0.0);
        Point3D uvFace = model.findUvFace(u, v);
        double distance = Double.MAX_VALUE;
        int indexI = -1, indexJ = -1;
        for (int i = 0; i < listBX.size(); i++) {
            for (int j = 0; j < listBY.size(); j++) {
                Point3D elem = surfaceB.getCoefficients().getElem(i, j);
                if (Point3D.distance(model.findUvFace(elem.getX(), elem.getY()), uvFace) < distance) {
                    indexI = i;
                    indexJ = j;
                    distance = Point3D.distance(surfaceB.getCoefficients().getElem(i, j), uv);
                }
            }
        }
        return new Point3D((double) indexI, (double) indexJ, 0.0);
    }

    /***
     *
     * @param textureCord2D
     * @param listXX
     * @param listXY
     * @param surfaceX
     * @return
     */
    Point3D percentList(Point3D textureCord2D, List<Point3D> listXX, List<Point3D> listXY, SurfaceParametriquePolynomiale surfaceX) {
        return null;
    }
}
