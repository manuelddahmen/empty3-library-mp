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

package one.empty3.gui;

import one.empty3.*;
import one.empty3.library.*;
import one.empty3.library.core.nurbs.ParametricCurve;
import one.empty3.library.core.nurbs.ParametricSurface;

import java.awt.*;

/**
 * Created by manue on 17-11-19.
 */

/***
 * Translate around Object's axis. New axis. Re
 */
public class TranslateR  extends ModifyR {

    public TranslateR(ZBufferImpl impl)
    {
        super(impl);
    }
    public LineSegment[] segments()
    {
        Matrix33 elem = representable.getRotation().getElem().getRot().getElem();
        Point3D centreRot = representable.getRotation().getElem().getCentreRot().getElem();
        Point3D[] colVectors = elem.getColVectors();
        axes =  new LineSegment[] {new LineSegment(
                colVectors[0].moins(centreRot ),
                centreRot)
                ,
                new LineSegment(
                        colVectors[1].moins(centreRot ), centreRot),
                new LineSegment(
                        colVectors[2].moins(centreRot ), centreRot) };
        return axes;
    }

    public void startMove() {}
    public void endMove(Point p0) {}

    private void pointAxes(ParametricSurface s)
    {

    }
    private void pointAxes(ParametricCurve s)
    {

    }
}
