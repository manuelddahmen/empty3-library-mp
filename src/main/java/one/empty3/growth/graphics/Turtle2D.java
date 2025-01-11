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

package one.empty3.growth.graphics;

import one.empty3.library.Matrix33;
import one.empty3.library.Point3D;

import one.empty3.library.Rectangle;
import one.empty3.libs.*;
import one.empty3.libs.Color;
import one.empty3.libs.Image;

import java.awt.*;

public class Turtle2D {
    private Color zeColor = new Color(Color.GRAY.getRGB());

    private Matrix33 theRot(double angle) {
        return new Matrix33(new double[]
                {
                        Math.cos(angle),
                        Math.sin(angle),
                        0.,
                        Math.sin(angle),
                        Math.cos(angle),
                        0d,
                        0d, 0d, 0d
                }
        );
    }

    private Point3D moveTo(double length) {
        return new Point3D(Math.cos(rads), Math.sin(rads), 0.).mult(length);
    }

    private Rectangle dimImage;
    private Image zeImage;
    private Point3D position;
    private double rads = 0;

    public Turtle2D(Image image) {
        zeImage = image;
        position = new Point3D(image.getWidth(null) / 2., image.getHeight(null) / 2., 0.);
    }

    public Rectangle getDimImage() {
        return dimImage;
    }

    public Color getZeColor() {
        return zeColor;
    }

    public void setZeColor(Color zeColor) {
        this.zeColor = zeColor;
    }
    public void setZeColor(int zeColor) {
        this.zeColor = new Color(zeColor);
    }

    public void setPosition(Point3D position) {
        this.position = position;
    }

    public void setDimImage(Rectangle dimImage) {
        this.dimImage = dimImage;
    }

    public Image getZeImage() {
        return zeImage;
    }

    public void setZeImage(Image zeImage) {
        this.zeImage = zeImage;
    }

    public Point3D getPosition() {
        return position;
    }


    public double getRads() {
        return rads;
    }

    public void setRads(double rads) {
        this.rads = rads;
    }

    public void line(double length) {
        Point3D extremite = position.plus(moveTo(length));
        Graphics graphics = getZeImage().getGraphics();
        graphics.setColor(zeColor);
        graphics.drawLine((int)(double ) position.getX(),
                (int)(double ) position.getY(),
                (int)(double ) extremite.getX(),
                (int)(double ) extremite.getY());
        position = extremite;
    }

    public void move(double length) {
        this.position = position.plus(moveTo(length));
    }

    public void right(double rads) {
        setRads(this.getRads() - rads);
    }

    public void left(double rads) {
        setRads(this.getRads() + rads);
    }

}
