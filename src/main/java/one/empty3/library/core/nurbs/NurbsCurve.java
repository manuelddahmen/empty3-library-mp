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

/*__
 * *
 * Global license : * Microsoft Public Licence
 * <p>
 * author Manuel Dahmen _manuel.dahmen@gmx.com_
 * <p>
 * *
 */
package one.empty3.library.core.nurbs;

import one.empty3.*;
import one.empty3.library.*;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Map.Entry;

/*__
 * Meta Description missing
 * @author Manuel Dahmen dathewolf@gmail.com
 */
public class NurbsCurve extends ParametricCurve {
    ArrayList<Double> knots = new ArrayList<>();
    protected ArrayList<Entry<Point3D, Double>> pointsAndWeights = new ArrayList<Entry<Point3D, Double>>();


    @Override
    public Point3D calculerPoint3D(double t) {
        Point3D S = Point3D.O0;
        double s = 0;
        int m = knots.size();
        int n = pointsAndWeights.size();
        for (int i = 0; i < m - n - 1; i++) {
            S = S.plus(pointsAndWeights.get(i).getKey().mult(b(i, n, t) * pointsAndWeights.get(i).getValue()));
        }
        for (int i = 0; i < m - n - 1; i++) {
            s += b(i, n, t) * pointsAndWeights.get(i).getValue();
        }
        if (s != 0) {
            S = S.mult(1 / s);
        }
        return S;
    }

    @Override
    public Point3D calculerVitesse3D(double t) {
        return null;

    }


    protected double fOOO(double a, double b) {
        if (Double.isInfinite(a / b) || Double.isNaN(a / b)) {
            return 1;
        } else {
            return a / b;
        }
    }

    protected double b(int i, int n, double t) {
        if (n == 0) {
            return (t < knots.get(0) ? knots.get(0) : (t <= knots.get(knots.size() - 1)) ? 1 :
                    knots.get(knots.size() - 1));
        } else {
            return fOOO(t - knots.get(i), knots.get(i + n) - knots.get(i))
                    * b(i, n - 1, t)
                    + fOOO(knots.get(i + n) - t, knots.get(i + n) - knots.get(i))
                    * b(i + 1, n - 1, t);

        }

    }

    public ArrayList<Entry<Point3D, Double>> getPointsAndWeights() {
        return pointsAndWeights;
    }

    @Override
    public String toString() {
        String rep = "nurbscourbe (\n\tknots\n\t(";
        for (int i = 0; i < knots.size(); i++) {
            rep += "\n\t\t" + knots.get(i);
        }
        rep += "\n\tpointsAndWeights\n\t" + "\n\t(";
        for (int i = 0; i < pointsAndWeights.size(); i++) {
            rep += "\n\t\t" + pointsAndWeights.get(i).getKey() + " " + pointsAndWeights.get(i).getValue();
        }
        rep += "\n\t)\n\t" + texture().toString() + "\n\n)";
        return rep;
    }

    public void add(double v, Point3D point) {
        pointsAndWeights.add(new AbstractMap.SimpleImmutableEntry<Point3D, Double>(point, v));
    }

    public void setKnots(double[] knots) {
        for (int i = 0; i < knots.length; i++)
            this.knots.add(knots[i]);
    }
}

