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

/**
 * RU( ) =
 * 2
 * 64
 * cos   sin   0
 * 􀀀sin   cos   0
 * 0 0 1
 * 3
 * 75
 * RL( ) =
 * 2
 * 64
 * cos   0 􀀀sin
 * 0 1 0
 * sin   0 cos
 * 3
 * 75
 * RH( ) =
 * 2
 * 64
 * 1 0 0
 * 0 cos   􀀀sin
 * 0 sin   cos
 * 3
 * 75
 * The following symbols control turtle orientation in space (Figure 2.12):
 * + Turn left by angle . The rotation matrix equal to RU().
 * 􀀀 Turn right by angle . The rotation matrix is equal to RU(􀀀).
 * & Pitch down by angle . The rotation matrix is equal to RL().
 * ^ Pitch up by angle . The rotation matrix is equal to RL(􀀀).
 * n Roll left by angle . The rotation matrix is equal to RH().
 * = Roll right by angle . The rotation matrix is equal to RH(􀀀).
 * j Turn around. The rotation matrix is equal to RU(180).
 */
package one.empty3.growth.graphics;

import one.empty3.*;
import one.empty3.library.*;

import one.empty3.libs.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Turtle3D_4 {
    private Point3D position;
    private Scene scene;
    private Color color;
    private ZBuffer zBuffer;
    private List<Character> alphabet = new ArrayList<>();
    private Matrix33 R;

    public Turtle3D_4(ZBuffer z) {
        this.zBuffer = z;
        scene = new Scene();
        position = Point3D.O0;
        R = Matrix33.I;
    }

    public void transform(double angleU, double angleL, double angleH) {
        Matrix33[] ulhMatrices =
                new Matrix33[]{
                        new Matrix33(new double[]{
                                Math.cos(angleU), Math.sin(angleU), 0,
                                -Math.sin(angleU), Math.cos(angleU), 0,
                                0, 0, 1

                        }),
                        new Matrix33(new double[]{
                                Math.cos(angleL), 0, -Math.sin(angleL),
                                0, 1, 0,
                                Math.sin(angleL), 0, Math.cos(angleL)
                        }),
                        new Matrix33(new double[]{
                                1, 0, 0,
                                0, Math.cos(angleH), -Math.sin(angleH),
                                0, Math.sin(angleH), Math.cos(angleH)
                        })
                };
        R = ulhMatrices[0].
                mult(ulhMatrices[1])
                .mult(ulhMatrices[2])
                .mult(R);
    }

    public List<Character> populateAlphabet() {
        return alphabet;
    }


    public void rotU(double a) {
        transform(a, 0, 0);
    }


    public void rotH(double a) {
        transform(0, a, 0);
    }


    public void rotL(double a) {
        transform(0, 0, a);
    }


    public void line(double dist) {
        Double[][] doubleArray = R.getDoubleArray();
        Point3D d = new Point3D(R.get(0, 0), R.get(0, 1), R.get(0, 2));
        Point3D newPosition = getPosition().plus(d);
        LineSegment seg = new LineSegment(getPosition(), newPosition);
        seg.texture(new ColorTexture(this.color));
        scene().add(seg);
        setPosition(newPosition);
        Logger.getAnonymousLogger().log(Level.INFO, "" + newPosition);
    }

    private Scene scene() {
        return scene;
    }

    public Point3D getPosition() {
        return position;
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public ZBuffer getzBuffer() {
        return zBuffer;
    }

    public void setzBuffer(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
        zBuffer.scene(this.scene = new Scene());
    }

}
