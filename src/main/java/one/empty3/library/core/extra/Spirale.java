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

/*

 Vous êtes libre de :

 */
package one.empty3.library.core.extra;

import one.empty3.*;
import one.empty3.library.*;

import java.util.ArrayList;
import java.util.Iterator;

public class Spirale implements ISpirale {

    private ArrayList<TRI> triangles;
    private Axe axe;
    private double angle;
    private double radius;
    private TRIObject obj;

    public Spirale(Axe axe, double radius) {
        this.axe = axe;
        this.setRadius(radius);

        /*
         * Tour tour = new Tour(axe.getP1(), axe.getP2(), new
         * Tour.IColorFunction() {
         * 
         * @Override public Color getColor(double axeCoordinate, double theta) {
         * return new Color(255, 0, 0); } }, new Tour.IPoint3DFunction() {
         * 
         * @Override public double getDiameter(double axeCoordinate, double
         * theta) {
         * 
         * return 100; }
         * 
         * @Override public int getNbrPoints() { return 20; }
         * 
         * @Override public void setNbrPoints() {
         * 
         * 
         * }
         * 
         * @Override public int getNbrRotations() { return 360; }
         * 
         * @Override public void setNbrRotation() { } });
         * 
         * FObjet o = tour.generate(); this.obj = o;
         */
    }

    @Override
    public void addToScene(Scene sc) {
        Iterator<TRI> it = triangles.iterator();
        TRIObject o = new TRIObject();
        while (it.hasNext()) {
            TRI t = it.next();
            o.add(t);
        }
        sc.add(o);
    }

    public TRIObject getObj() {
        return obj;
    }

    public void setObj(TRIObject obj) {
        this.obj = obj;
    }

    @Override
    public Point3D getObjectDeviation(Point3D position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Point3D getObjectDeviation(Point3D position, Point3D speed,
                                      Point3D rotation) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Point3D getObjectRotation(Point3D position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Point3D getObjectRotation(Point3D position, Point3D speed,
                                     Point3D rotation) {
        // TODO Auto-generated method stub
        return null;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public void rotate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public void rotate(double deg) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
