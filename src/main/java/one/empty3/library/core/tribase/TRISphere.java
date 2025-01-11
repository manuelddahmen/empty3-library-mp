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

/*

 Vous êtes libre de :

 */
package one.empty3.library.core.tribase;

import one.empty3.*;
import one.empty3.library.*;

/*__
 * @author DAHMEN Manuel
 *         <p>
 *         dev
 * @date 22-mars-2012
 */
public class TRISphere extends Sphere {
    public TRISphere(Point3D center, double radius) {
        super(center, radius);
    }

    public Point3D getCentre() {
        return circle.getElem().getCenter();
    }

    public void setCentre(Point3D centre) {
        Axe axe2 = new Axe(getCircle().getAxis().getElem().getP1().getElem(), getCircle().getAxis().getElem().getP2().getElem());
        Point3D center2 = axe2.getCenter();
        axe2.getP1().getElem().changeTo(getCircle().getAxis().getElem().getP1().getElem().moins(center2).plus(centre));
        axe2.getP2().getElem().changeTo(getCircle().getAxis().getElem().getP2().getElem().moins(center2).plus(centre));
        circle.getElem().getAxis().setElem(axe2);
    }

    public void setRadius(double radius) {
        circle.getElem().setRadius(radius);
    }
}/*ParametricSurface {

    private Point3D centre = new Point3D(0, 0, 0);
    private double radius = 1.0;

    public TRISphere(Point3D c, double r) {
        rotation = new Rotation(Matrix33.I, c);
        this.centre = c;
        this.radius = r;
        setCirculaireY(true);
        setCirculaireX(false);
        setIncrU(0.01);
        setIncrV(0.01);
    }

    @Override
    public Point3D calculerPoint3D(double u1, double v1) {

        Point3D centre = this.centre;


        double u = 2*Math.PI*u1;
        double v = Math.PI*v1;

        Point3D p = rotation(
                new Point3D(centre.getX() + Math.sin(u) * Math.sin(v)
                * radius, centre.getY() + Math.sin(u) * Math.cos(v) * radius,
                centre.getZ() + Math.cos(u) * radius));
        return p;
    }

    @Override
    public Point3D calculerVitesse3D(double u, double v) {
        return null;
    }

    @Override
    public Point3D coordPoint3D(int coordArr, int y) {
        double a = 1.0 * coordArr / getMaxX() * 2 * Math.PI - Math.PI;
        double b = 1.0 * y / getMaxY() * 2 * Math.PI - Math.PI;

        return calculerPoint3D(a, b);
    }

    public Point3D getCentre() {
        return centre;
    }

    public void setCentre(Point3D centre) {

        rotation = new Rotation(Matrix33.I, centre);
        this.centre = centre;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    @Override
    public String toString() {
        return "Sphere (\n\t" + centre.toString() + "\n\t" + radius + "\n\t"
                + texture.toString() + "\n)\n";
    }

}
*/