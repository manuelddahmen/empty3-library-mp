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

package one.empty3.growth.test;

import one.empty3.feature.snakes.Matrix;
import one.empty3.growth.graphics.Rotation2;
import one.empty3.library.Matrix33;
import one.empty3.library.Point3D;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.awt.*;

import one.empty3.libs.Image;

@RunWith(JUnit4.class)
public class TestRotation2_methodeBis extends TestCaseExtended {

    private Rotation2 rot = new Rotation2();

    /***
     *
     * @param A
     * @param B
     * @param angle
     * @param X
     * @return y rotated by matrix (a,b, a^b)
     */
    private Point3D rotate(Point3D A, Point3D B,
                           double angle, Point3D X) {
        //return rot.rotation(X, A, B, angle);
        Matrix33 mRotate = new Matrix33(A, B, A.prodVect(B));

        //Point3D res = new Point3D(3d, -5d, 0d);

        return mRotate.rotationX(angle).mult(X);
    }

    @Test
    public void testRotationIdent1() {
        Point3D x = rotate(Point3D.Y, Point3D.X,
                2 * Math.PI, Point3D.O0);
        Point3D y = Point3D.Y;

        assertEqualsPoint3D(x, y, 0.1);

    }

    // Fails???
    @Test
    public void testRotationIdent2() {
        Point3D x = rotate(Point3D.O0, Point3D.X,
                2 * Math.PI, Point3D.X);
        Point3D y = Point3D.X;

        assertEqualsNaNPoint3D(x);
    }


    @Test
    public void testRotationIdent3() {
        Point3D x = rotate(Point3D.O0, Point3D.X,
                2 * Math.PI, Point3D.Z);
        Point3D y = Point3D.Z;

        assertEqualsPoint3D(x, y, 0.1);

    }

    @Test
    public void testRotation90() {
        Point3D x = rotate(Point3D.O0, Point3D.X,
                Math.PI, Point3D.Z);
        Point3D y = Point3D.Z.mult(-1);

        assertEqualsPoint3D(x, y, 0.1);


    }

    @Test
    public void testRotationNon180X() {
        Point3D x = Matrix33.rotationX(Math.PI).mult(new Point3D(10d, 3d, 0d));
        Point3D y = new Point3D(10d, -3d, 0d);

        assertEqualsPoint3D(x, y, 0.1);

    }

    @Test
    public void testRotation180Y() {
        Point3D a = new Point3D(11d, 0d, 0d);
        Point3D b = new Point3D(10d, 0d, 0d);
        Point3D x = new Point3D(3d, 5d, 0d);
        Matrix33 mRotate = new Matrix33(a, b, a.prodVect(b));
        Point3D resComputed = mRotate.rotationX(Math.PI).mult(x);

        Point3D res = new Point3D(3d, -5d, 0d);

        assertEqualsPoint3D(res, resComputed, 0.1);

    }

    @Test
    public void testRotation30deg() {
        Point3D x = new Point3D(3d, 5d, 5d);
        Point3D y = x;

        for (int i = 0; i < 12 * 2; i++) {
            x = rotate(Point3D.X, new Point3D(10., 0., 0.),
                    Math.PI / 6, x);
        }


        assertEqualsPoint3D(x, y, 0.1);

    }

    @Test
    public void testRotation30degRandomAxe() {
        Point3D A = Point3D.random(100.);
        Point3D B = Point3D.random(100.);
        Point3D X = Point3D.random(100.);
        Point3D Y = X;

        for (int i = 0; i < 12; i++) {
            X = rotate(A, B, Math.PI / 6, X);
        }


        assertEqualsPoint3D(X, Y, 0.1);

    }

    @Test
    public void testRotation0degRandomPoint() {
        Point3D x = Point3D.random(10.);

        Point3D y = rotate(Point3D.X, new Point3D(10., 0., 0.),
                0., x);

        assertEqualsPoint3D(x, y, 0.1);

    }

    @Test
    public void testRotation0degRandomAxe() {
        Point3D a = Point3D.random(10.);
        Point3D b = Point3D.random(10.);

        Point3D y = rotate(a, b,
                0., Point3D.X);

        assertEqualsPoint3D(Point3D.X, y, 0.1);

    }

    @Test
    public void testRotation360degRandomAxe() {
        Point3D a = Point3D.random(10.);
        Point3D b = Point3D.random(10.);

        Point3D y = rotate(a, b,
                2. * Math.PI, Point3D.X);

        assertEqualsPoint3D(Point3D.X, y, 0.1);

    }

    @Test
    public void testRotation360deg300RandomAxe() {
        Image image = new Image(1600, 1200, Image.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.YELLOW);
        Point3D y = Point3D.O0;
        for (double angle = 0.; angle < 2. * Math.PI; angle += 2. * Math.PI / 1000.) {
            Point3D a = Point3D.random(50.);
            Point3D b = Point3D.random(50.);


            y = rotate(a, b,
                    angle, y);

            Point3D plus = y.plus(new Point3D(image.getWidth() / 2., image.getHeight() / 2., 0.));
            graphics.drawLine((int) (double) plus.getX(), (int) (double) plus.getY(), (int) (double) plus.getX(), (int) (double) plus.getY());

            Point3D y2 = y;

            for (int i = 0; i < 1000; i++) {
                Point3D c = Point3D.random(50.);
                Point3D d = Point3D.random(50.);
                double angleB = 2 * Math.PI / 1000;
                y2 = rotate(c, d,
                        angleB, y2);
                plus = y2.plus(new Point3D(image.getWidth() / 2., image.getHeight() / 2., 0.));
                graphics.drawLine((int) (double) plus.getX(), (int) (double) plus.getY(), (int) (double) plus.getX(), (int) (double) plus.getY());

            }
        }
        writeImage(image);
    }


    @Test
    public void testRotationMethode2() {
        Rotation2 rotation2 = new Rotation2();

        Point3D intersection = rotation2.projection(Point3D.X, Point3D.Y, new Point3D(6., 5., 6.));
        assertEqualsPoint3D(intersection, new Point3D(1., 5., 0.), 0.001);

    }

}
