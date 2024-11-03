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
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package one.empty3.library;


import one.empty3.ECImage;

/*__
 * @author Atelier
 */
public class POINT3D_RIO extends Point3D {

    public static final Point3D X = new Point3D(1d, 0d, 0d);
    public static final Point3D Y = new Point3D(0d, 1d, 0d);
    public static final Point3D Z = new Point3D(0d, 0d, 1d);
    public static final Point3D O0 = new Point3D(0d, 0d, 0d);
    /*__
     *
     */
    private static final long serialVersionUID = -5729435529487300122L;
    private double[] x;

    public POINT3D_RIO(double x0, double y0, double z0) {
        super(x0, y0, z0);
    }

    public POINT3D_RIO(Point3D p) {
        super();
        x[0] = p.get(0);
        x[1] = p.get(1);
        x[2] = p.get(2);
    }

    public POINT3D_RIO(POINT3D_RIO p0) {
        x = new double[3];
        x[0] = p0.getX();
        x[1] = p0.getY();
        x[2] = p0.getZ();
    }

    @Override
    public Object clone() {
        return new POINT3D_RIO(x[0], x[1], x[2]);
    }

    @Override
    public Point3D moins(Point3D p) {
        setX(getX() - p.getX());
        setY(getY() - p.getY());
        setZ(getZ() - p.getZ());
        return this;
    }

    public Point3D mult(double xFactor) {
        setX(getX() * xFactor);
        setY(getY() * xFactor);
        setZ(getZ() * xFactor);
        return this;
    }

    public Point3D plus(double i) {
        setX(getX() + i);
        setY(getY() + i);
        setZ(getZ() + i);
        return this;
    }

    @Override
    public Point3D plus(Point3D p) {
        setX(getX() + p.getX());
        setY(getY() + p.getY());
        setZ(getZ() + p.getZ());
        return this;
    }

}
