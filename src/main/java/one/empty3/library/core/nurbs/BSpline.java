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

package one.empty3.library.core.nurbs;

import one.empty3.library.Point3D;
import one.empty3.library.StructureMatrix;

import java.util.ArrayList;
import java.util.Iterator;

/*__
 * @author Manuel Dahmen
 *
 * BSpline
 *
 * n = number of controls points controls.size()
 * m = number of knots
 * degree = degree of the curve
 * n+1=m-d
 *
 * example=http://www.cgeo.ulg.ac.be/CAO/CAD_04.pdf
 *
 */

public class BSpline extends ParametricCurve {

    private final StructureMatrix<Point3D> controls = new StructureMatrix<>(1, Point3D.class);
    private final StructureMatrix<Double> T = new StructureMatrix<>(1, Double.class);
    private final StructureMatrix<Integer> degree = new StructureMatrix<>(0, Integer.class);

    public BSpline() {

        degree.setElem(3);
        add(new Point3D(20.0, 10d, 0d));
        add(new Point3D(20d, 20d, 0d));
        add(new Point3D(10.0, 20d, 0d));
        add(new Point3D(10d, 10d, 0d));
        add(new Point3D(20d, 10d, 0d));

        add1();
    }

    public void add1() {
        T.getData1d().clear();
        int endI = controls.getData1d().size() + 1 + 2 * degree.getElem();
        for (int i = 0; i < endI; i++)
            if (i < degree.getElem()) {
                T.add(1, 0.0);
            } else if (i >= endI - degree.getElem()) {
                T.add(1, 1.0);
            } else {
                T.add(1, 1.0 * i / (controls.getData1d().size()));
            }


    }

    public void add(Point3D point) {
        controls.add(1, point);
    }

    public double boor(double t, int i, int d) {
        if (d <= 0) {
            if (i >= 0 && i < T.getData1d().size())
                return t < get(i + 1) && t > get(i) ? 1.0 : 0.0;
            else
                return 0.0;
        }
        return avoidNaN((t - get(i)) * boor(t, i, d - 1), get(i + d) - t)
                +
                avoidNaN((get(i + d + 1) - t) * boor(t, i + 1, d - 1), get(i + d + 1) - get(i + 1));
    }

    private double avoidNaN(double a, double b) {
        if (Double.isFinite(a / b))
            return a / b;
        return 0.0;
    }

    public Point3D calculerPoint3D(double t) {
        Point3D p = Point3D.O0;
        double boor = 0d;
        for (int i = 0; i < controls.getData1d().size() - degree.getElem(); i++) {
            boor += boor(t, i, degree.getElem());
            p = p.plus(controls.getElem(i).mult(boor));
        }
        //Logger.getAnonymousLogger().log(Level.INFO, "p = " + p.toString() + "\tt = " + t);
        return p;//.mult(1/boor);
    }

    public double get(int i) {
        if (i < 0) {
            return 0.0;
        }
        if (i >= T.getData1d().size()) {
            return 0.0;
        }
        return T.getElem(i);
    }


    public Integer getDegree() {
        return controls.getData1d().size();
    }

    public void setDegree(Integer degree) {
        this.degree.setElem(degree);
    }

    public Iterator<Point3D> iterator() {
        return controls.getData1d().iterator();
    }

    public Point3D remove(int arg0) {
        return controls.getData1d().remove(arg0);
    }

    public Point3D set(int arg0, Point3D arg1) {
        return controls.getData1d().set(arg0, arg1);
    }

    public int size() {
        return controls.getData1d().size();
    }

    public String toString() {
        StringBuilder s = new StringBuilder("bspline \n(\n\n");
        Iterator<Point3D> ps = iterator();
        s.append("\n controls (");
        while (ps.hasNext()) {
            s.append("\n").append(ps.next().toString()).append("\n");
        }
        Iterator<Double> iterator = T.getData1d().iterator();

        s.append("\n) knots (");
        while (iterator.hasNext()) {
            s.append("\n").append(iterator.next().toString()).append("\n");
        }
        s.append("\n) \n)");

        return s.toString();
    }

    public StructureMatrix<Point3D> getControls() {
        return controls;
    }

    public void setControls(ArrayList<Point3D> controls) {
        this.controls.setAll(controls);
    }

    public StructureMatrix<Double> getT() {
        return T;
    }

    public void setT(ArrayList<Double> t) {

        T.setAll(t);
    }

    @Override
    public void declareProperties() {
        super.declareProperties();
        getDeclaredDataStructure().put("controls/Points de contrôle", controls);
        getDeclaredDataStructure().put("T/poids des points de contrôle", T);
        getDeclaredDataStructure().put("degree/Degree of curve", degree);
    }

    public void add(Double d, Point3D p) {
        controls.add(1, p);
        T.add(1, d);
    }


}
