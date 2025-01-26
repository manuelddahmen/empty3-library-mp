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

package one.empty3.facedetect.model.sol1;

import one.empty3.library.Point3D;
import one.empty3.library.core.nurbs.SurfaceParametriquePolynomiale;
import one.empty3.library.objloader.E3Model;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.util.ArrayList;
import java.util.List;

public abstract class DistanceAB {
    SurfaceParametriquePolynomiale surfaceA;
    SurfaceParametriquePolynomiale surfaceB;
    Dimension2D aDimReduced = new Dimension(40, 40);
    Dimension2D bDimReduced = new Dimension(40, 40);
    static final int TYPE_SHAPE_BEZIER = 1;
    static final int TYPE_SHAPE_QUADR = 2;
    int typeShape = 1;
    boolean opt1 = false;
    DistanceBezier2.Rectangle2 rectA;
    DistanceBezier2.Rectangle2 rectB;
    Dimension2D aDimReal;
    Dimension2D bDimReal;
    List<Point3D> A;
    List<Point3D> B;
    Point3D[][] sAij;
    Point3D[][] sBij;
    double arrayHeight = 80;
    double arrayWidth = 80;

    List<Double> listAX = new ArrayList<>();
    List<Double> listAY = new ArrayList<>();
    List<Double> listBX = new ArrayList<>();
    List<Double> listBY = new ArrayList<>();

    protected E3Model model;
    protected boolean isInvalidArray = false;

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
}
