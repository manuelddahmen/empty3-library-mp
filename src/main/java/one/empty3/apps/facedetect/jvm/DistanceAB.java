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

package one.empty3.apps.facedetect.jvm;

import one.empty3.library.Point3D;
import one.empty3.library.core.nurbs.SurfaceParametriquePolynomiale;
import one.empty3.library.objloader.E3Model;



import one.empty3.libs.Image;

import javax.print.attribute.standard.Finishings;
import java.util.ArrayList;
import java.util.List;

public abstract class DistanceAB {

    public boolean refineMatrix = false;
    public int REFINE_MATRIX_FACTOR = 5;
    public boolean borderNotIinitialized;
    public double right;
    public double bottom;
    public int distanceABdimSize = 25;
    public Image jpgRight;
    public FinishInitListener finishInitListener;

    public void setJpgRight(Image jpgFileRight) {
        this.jpgRight = jpgFileRight;
    }

    public static class Rectangle2 {
        public double getX1() {
            return x1;
        }

        public void setX1(double x1) {
            this.x1 = x1;
        }

        public double getY1() {
            return y1;
        }

        public void setY1(double y1) {
            this.y1 = y1;
        }

        public double getX2() {
            return x2;
        }

        public void setX2(double x2) {
            this.x2 = x2;
        }

        public double getY2() {
            return y2;
        }

        public void setY2(double y2) {
            this.y2 = y2;
        }

        private double x1;
        private double y1;
        double x2;
        double y2;

        public Rectangle2(double x1, double y1, double x2, double y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }

        public double getWidth() {
            return x2 - x1;
        }

        public double getHeight() {
            return y2 - y1;
        }
    }

    public boolean optimizeGrid = false;
    public int OPTIMIZED_GRID_SIZE = 5;
    public SurfaceParametriquePolynomiale surfaceA;
    public SurfaceParametriquePolynomiale surfaceB;
    public Dimension aDimReduced = new Dimension(20, 20);
    public Dimension bDimReduced = new Dimension(20, 20);
    public static final int TYPE_SHAPE_BEZIER = 1;
    public static final int TYPE_SHAPE_QUADR = 2;
    public int typeShape = TYPE_SHAPE_QUADR;
    public static final boolean BAL_GET_CCNTROLS_A = true;
    public static final boolean BAL_GET_MULTPLY_BDIM_A = true;
    public boolean opt1 = false;
    public DistanceBezier2.Rectangle2 rectA;
    public DistanceBezier2.Rectangle2 rectB;
    public Dimension aDimReal;
    public Dimension bDimReal;
    public List<Point3D> A;
    public List<Point3D> B;
    public Point3D[][] sAij;
    public Point3D[][] sBij;

    public List<Double> listAX = new ArrayList<>();
    public List<Double> listAY = new ArrayList<>();
    public List<Double> listBX = new ArrayList<>();
    public List<Double> listBY = new ArrayList<>();

    public E3Model model;
    public boolean isInvalidArray = false;

    public abstract Point3D findAxPointInB(double u, double v);

    public boolean isInvalidArray() {
        return isInvalidArray;
    }

    public void setInvalidArray(boolean invalidArray) {
        isInvalidArray = invalidArray;
    }

    public E3Model getModel() {
        return model;
    }

    public void setModel(E3Model model) {
        this.model = model;
    }

    public void PuvFromPoly4(Point3D P, Point3D P1, Point3D P2, Point3D P3, Point3D P4) {
        double u, v;

        //Point3D P1+(u*(P2-P1)+v*(P3+(1-u)*(P4-P3)-P1+u*(P2-P1))-(P2+v*(P3-P2)+u*(P4+(1-v)(P1-P4)-P2+v*(P3-P2))))

    }

    public void addFinishInitListener(FinishInitListener finishInitListener) {
        this.finishInitListener = finishInitListener;
    }
}
