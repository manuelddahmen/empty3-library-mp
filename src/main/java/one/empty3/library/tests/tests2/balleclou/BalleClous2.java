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

package one.empty3.library.tests.tests2.balleclou;

/*
 * 2013 Manuel Dahmen
 */

import one.empty3.library.Point2D;
import one.empty3.library.Point3D;
import one.empty3.library.Sphere;

import java.util.ArrayList;

/*__
 * @author Manuel Dahmen
 */
public class BalleClous2 extends Sphere {
    public static final int METHOD_EXTREMUM = 1;
    public static final int METHOD_SUM = 2;
    private final ArrayList<Point2D> points = new ArrayList<Point2D>();
    private double paramD;
    public static double maxMulStatic1;
    public static double maxMulStatic0;
    protected int method = 1;

    public BalleClous2(Point3D c, double r) {
        super(c, r);
        if (maxMulStatic1 == 0.0)
            maxMulStatic1 = getCircle().getRadius();
        maxMulStatic0 = maxMulStatic1;
    }

    public void addPoint(Point2D p) {
        this.points.add(p);
    }

    public ArrayList<Point2D> points() {
        return points;
    }

    public double formula(Point2D p1, Point2D p2) {
        double d = dmindist(p1, p2);
        return 1 / (d * d + paramD);
    }

    public void param(double paramD, int method) {

        this.paramD = paramD;
        this.method = method;

    }

    public double param() {
        return paramD;
    }

    public double dmindist(Point2D p0, Point2D p1) {

        double[] x = new double[]{-1, 0, 1, -1, 0, 1, -1, 0, 1};
        double[] y = new double[]{-1, -1, -1, 0, 0, 0, 1, 1, 1};
        double min = Double.MAX_VALUE;
        for (int i = 0; i < x.length; i++) {
            double cur;
            cur = Point2D.dist(p0, Point2D.plus(p1, new Point2D(x[i], y[i])));
            if (cur < min)
                min = cur;
        }
        return min;
    }

    public double dmaxdist(Point2D p0, Point2D p1) {

        double[] x = new double[]{-1, 0, 1, -1, 0, 1, -1, 0, 1};
        double[] y = new double[]{-1, -1, -1, 0, 0, 0, 1, 1, 1};
        double max = Double.MIN_VALUE;
        for (int i = 0; i < 9; i++) {
            double cur;
            cur = Point2D.dist(p0, Point2D.plus(p1, new Point2D(x[i], y[i])));
            if (cur > max)
                max = cur;
        }
        return max;
    }

    @Override
    public Point3D calculerPoint3D(double u, double v) {
        Point3D p = super.calculerPoint3D(u, v);

        Point2D p0 = new Point2D((u - .5) * 2, (v - .5) * 2);

        double mult = 0.0;

        for (Point2D point : points) {
            if (method == METHOD_EXTREMUM) {
                mult = Math.max(formula(p0, point), mult);
            } else if (method == METHOD_SUM) {
                mult = formula(p0, point) + mult;
            }
        }
        if (mult > maxMulStatic1) {
            maxMulStatic1 = mult;
        }

        Point3D ret = getCircle().getCenter().plus(p.moins(getCircle().getCenter()).mult(mult / maxMulStatic0 / 2 * 0.7));

        double distance = Point3D.distance(ret, getCircle().getCenter());
        if (distance > maxMulStatic1) {
            maxMulStatic1 = distance;
        }
        return ret;

    }


}
