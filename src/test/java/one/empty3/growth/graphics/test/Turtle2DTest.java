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

package one.empty3.growth.graphics.test;

import junit.framework.TestCase;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Before;
import org.junit.Test;

import one.empty3.growth.graphics.Turtle2D;
import one.empty3.library.Point2D;
import one.empty3.library.Point3D;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.imageio.ImageIO;

import one.empty3.libs.Image;

import java.io.IOException;

@RunWith(JUnit4.class)

public class Turtle2DTest extends TestCase {

    @Test
    public void testLineRotate() {
        Image bufferedImage = new Image(1024, 768, Image.TYPE_INT_RGB);
        Turtle2D turtle2D = new Turtle2D(bufferedImage);
        Point2D o = new Point2D(turtle2D.getPosition().getX(),
                turtle2D.getPosition().getY());
        assertEqualsPoint3D(turtle2D.getPosition()
                , new Point2D(0.0, 0.).plus(o).get3D());
        turtle2D.line(100.0);
        turtle2D.right(Math.PI / 2);
        assertEqualsPoint3D(turtle2D.getPosition()
                , new Point2D(100.0, 0.).plus(o).get3D());
        turtle2D.line(100.0);
        turtle2D.right(Math.PI / 2);
        assertEqualsPoint3D(turtle2D.getPosition()
                , new Point2D(100.0, -100.).plus(o).get3D());
        turtle2D.line(100.0);
        turtle2D.right(Math.PI / 2);
        assertEqualsPoint3D(turtle2D.getPosition()
                , new Point2D(0.0, -100.).plus(o).get3D());
        turtle2D.line(100.0);
        turtle2D.right(Math.PI / 2);
        assertEqualsPoint3D(turtle2D.getPosition()
                , new Point2D(0.0, 0.0).plus(o).get3D());
        try {
            ImageIO.write(bufferedImage, "jpg", new java.io.File("testResults/testLineRotate.jpg"));
        } catch (Exception ex) {
            ex.printStackTrace();

        }


    }

    public void assertEqualsPoint3D(Point3D x, Point3D y) {
        for (int i = 0; i < 3; i++) {
            assertEquals(y.get(i), x.get(i), 0.01);
        }
    }
}