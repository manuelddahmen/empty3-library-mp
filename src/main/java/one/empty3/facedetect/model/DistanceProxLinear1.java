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
import one.empty3.library.core.nurbs.SurfaceParametriquePolynomiale;

import java.awt.geom.Dimension2D;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/***
 * Used for portrait  "Boites imbriquées"
 */
public class DistanceProxLinear1 extends DistanceBezier2 {
    private static final int MAX_SUB_ITERE_X = 10;
    private double right = -1;
    private double bottom = -1;
    private boolean borderNotIinitialized = true;

    public DistanceProxLinear1(List<Point3D> A, List<Point3D> B, Dimension2D aDimReal, Dimension2D bDimReal,
                               boolean opt1, boolean optimizeGrid) {
        super(A, B, aDimReal, bDimReal, opt1, optimizeGrid);

    }


    @Override
    public Point3D findAxPointInB(double u, double v) {
        if (borderNotIinitialized || this.right == -1 || bottom == -1) {
            for (int i = 0, listBXSize = listBX.size(); i < listBXSize; i++) {
                for (int j = 0, listBYSize = listBY.size(); j < listBYSize; j++) {
                    Point3D p2 = findAxPointInBal2(listBX.get(i), listBY.get(j));
                    if (p2.getX() > this.right) {
                        this.right = p2.getX();
                    }
                    if (p2.getY() > bottom) {
                        bottom = p2.getY();
                    }
                }
            }
            borderNotIinitialized = false;
        }
        if (!(borderNotIinitialized || this.right == -1 || bottom == -1)) {
            return findAxPointInBal2(u / this.right, v / bottom)
//                    .multDot(new Point3D(1.0 / right, 1.0 / bottom, 0.0))
                    .multDot(new Point3D(1.0 / aDimReal.getWidth(), 1. / aDimReal.getHeight(), 0.0))
                    .multDot(new Point3D(bDimReal.getWidth(), bDimReal.getHeight(), 0.0));
        } else {
            return findAxPointInBal2(u, v);
        }
    }

    private Point3D findAxPointInBal2z(double u, double v) {
        Point3D pb = nearLandmark(u, v);
        //pb = new Point3D(Math.max(0, Math.min(pb.get(0), listBX.size() - 1)), Math.max(0, Math.min(pb.get(1), listBY.size() - 1)), 0.0);
        Point3D pa = surfaceA.getCoefficients().getElem((int) (double) pb.getX(), (int) (double) pb.get(1));
        return pa;
    }

    private Point3D findAxPointInBal2(double u, double v) throws RuntimeException {
        Point3D pb = nearLandmark(u, v);
        Point3D pa = surfaceA.calculerPoint3D(u, v);
        if (pa == null) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "surfaceA.calculerPoint3D:: null");
            pa = Point3D.O0;
        }
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
                    indexI = (int) u;
                    indexJ = (int) v;
                    distance = Point3D.distance(surfaceB.getCoefficients().getElem(i, j), uv);
                }
            }
        }
        if (indexI == -1 || indexJ == -1) {
            return Point3D.O0;
        }
        Point3D point3Dij = new Point3D((double) indexI, (double) indexJ, 0.0);
        return precision1(point3Dij, u, v);//point3Dij
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

        return new Point3D(((i + (u - i) / sizeBi) / totalBx) / listBX.size(),
                ((j + (v - j) / sizeBj) / totalBy) / listBY.size(), 0.0);
        //return ij;
    }

    Point3D precision1(Point3D ij, double u, double v) {
        double i = ij.getX();
        double j = ij.getY();
        double sizeBi = 0;
        double sizeBj = 0;
        double sizeAi = 0;
        double sizeAj = 0;
        double interCurrAi = 0;
        double interCurrAj = 0;
        double interCurrBi = 0;
        double interCurrBj = 0;
        if (i > listBX.size() - 1) {
            interCurrAi = listAX.get(listAX.size() - 1) - listAX.get(listAX.size() - 2);
            interCurrBi = listBX.get(listBX.size() - 1) - listBX.get(listBX.size() - 2);
            sizeAi = listAX.get(listAX.size() - 1) - listAX.get(0);
            sizeBi = listBX.get(listBX.size() - 1) - listBX.get(0);
        } else if (i < 0) {
            interCurrAi = sizeAi = listAX.get(1) - listAX.get(0);
            interCurrBi = sizeBi = listBX.get(1) - listBX.get(0);

        } else {
            for (int k = 0; k < i; k++) {
                sizeAi += listAX.get((int) (i + 1)) - listAX.get((int) i);
                sizeBi += listBX.get((int) (i + 1)) - listBX.get((int) i);
                interCurrAi += listAX.get((int) (i + 1)) - listAX.get((int) i);
                interCurrBi += listBX.get((int) (i + 1)) - listBX.get((int) i);
            }
        }
        if (j > listBY.size() - 1) {
            interCurrAj += listAY.get(listAX.size() - 1) - listAY.get(listAY.size() - 2);
            interCurrBj += listBY.get(listBY.size() - 1) - listBY.get(listBY.size() - 2);
            sizeAj = listAX.get(listAX.size() - 1) - listAX.get(0);
            sizeBj = listBY.get(listBY.size() - 1) - listBY.get(0);
        } else if (j < 0) {
            interCurrAj = sizeAj = listAY.get(1) - listAY.get(0);
            interCurrBj = sizeBj = listBY.get(1) - listBY.get(0);
        } else {
            for (int k = 0; k < j; k++) {
                sizeAj += listAY.get((int) (j + 1)) - listAY.get((int) j);
                sizeBj += listBY.get((int) (j + 1)) - listBY.get((int) j);
                interCurrAj += listAY.get((int) (j + 1)) - listAY.get((int) j);
                interCurrBj += listBY.get((int) (j + 1)) - listBY.get((int) j);
            }
        }
        double totalBx = listBX.get(listBX.size() - 1) - listBX.get(0);
        double totalBy = listBY.get(listBY.size() - 1) - listBY.get(0);

        return new Point3D((u + (u - interCurrBi) / sizeBi),
                (v + (v - interCurrBj) / sizeBj), 0.0);
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
