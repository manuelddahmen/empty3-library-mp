package one.empty3.apps.facedetect;

import one.empty3.library.Point3D;
import one.empty3.library.core.nurbs.SurfaceParametriquePolynomiale;

import java.awt.geom.Dimension2D;
import java.util.List;

public class DistanceProxLinear4 extends DistanceBezier2 {

    private static final int MAX_SUB_ITERE_X = 10;

    public DistanceProxLinear4(List<Point3D> A, List<Point3D> B, Dimension2D aDimReal, Dimension2D bDimReal,
                               boolean opt1, boolean optimizeGrid) {
        super(A, B, aDimReal, bDimReal, opt1, optimizeGrid);
    }


    @Override
    public Point3D findAxPointInB(double u, double v) {
        return findAxPointInBal2z(u, v);
    }

    private Point3D findAxPointInBal2z(double u, double v) {
        Point3D pb = nearLandmark(u, v);
        //pb = new Point3D(Math.max(0, Math.min(pb.get(0), listBX.size() - 1)), Math.max(0, Math.min(pb.get(1), listBY.size() - 1)), 0.0);
        Point3D pa = surfaceB.calculerPoint3D(pb.getX(), pb.get(1));
        return pa;
    }

    private Point3D findAxPointInBal2(double u, double v) {
        Point3D pb = nearLandmark(u, v);
        pb = new Point3D(Math.max(0, Math.min(pb.get(0) / listBX.size() * 5, 1.0)), Math.max(0.0, Math.min(pb.get(1) / listBY.size() * 5, 1.0)), 1.0, 0.0);
        pb = new Point3D(Math.max(0, Math.min(pb.get(0) / Math.sqrt(listBX.size()) / 3.0 * 3, 1.0)),
                Math.max(0.0, Math.min(pb.get(1) / Math.sqrt(listBY.size()) / 2.0 * 3.0, 1.0)), 0.0);
        Point3D pa = surfaceB.calculerPoint3D((double) pb.getX(), (double) pb.getY()).multDot(new Point3D(1.0 / bDimReal.getWidth(), 1.0 / bDimReal.getHeight(), 0.0));
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
        if (i >= listBX.size() - 1) {
            interCurrAi = listAX.get(listAX.size() - 1) - listAX.get(listAX.size() - 2);
            interCurrBi = listBX.get(listBX.size() - 1) - listBX.get(listBX.size() - 2);
            sizeAi = listAX.get(listAX.size() - 1) - listAX.get(0);
            sizeBi = listBX.get(listBX.size() - 1) - listBX.get(0);
        } else if (i <= 0) {
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
        if (j >= listBY.size() - 1) {
            interCurrAj += listAY.get(listAX.size() - 1) - listAY.get(listAY.size() - 2);
            interCurrBj += listBY.get(listBY.size() - 1) - listBY.get(listBY.size() - 2);
            sizeAj = listAX.get(listAX.size() - 1) - listAX.get(0);
            sizeBj = listBY.get(listBY.size() - 1) - listBY.get(0);
        } else if (j <= 0) {
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

        return new Point3D((u + (u - interCurrBi) / sizeBi) / totalBx / listBX.size() / Math.sqrt(7) * 10,
                (v + (v - interCurrBj) / sizeBj) / totalBy / listBY.size() / Math.sqrt(7), 0.0);
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
