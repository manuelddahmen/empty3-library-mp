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

package one.empty3.library;


import one.empty3.libs.Image;/*
 * Copyright (c) 2017. Tous les fichiers dans ce programme sont soumis à la License Publique Générale GNU créée par la Free Softxware Association, Boston.
 * La plupart des licenses de parties tièrces sont compatibles avec la license principale.
 * Les parties tierces peuvent être soumises à d'autres licenses.
 * Montemedia : Creative Commons
 * ECT : Tests à valeur artistique ou technique.
 * La partie RayTacer a été honteusement copiée sur le Net. Puis traduite en Java et améliorée.
 * Java est une marque de la société Oracle.
 *
 * Pour le moment le programme est entièrement accessible sans frais supplémentaire. Get the sources, build it, use it, like it, share it.
 */

/*__
 * *
 * Global license : * Microsoft Public Licence
 * <p>
 * author Manuel Dahmen _manuel.dahmen@gmx.com_
 * <p>
 * *
 */

import one.empty3.library.Point3D;
import one.empty3.library.Polygon;
import one.empty3.library.RepresentableConteneur;
import one.empty3.library.ColorTexture;

/**
 * Represents a 3D parallelepiped geometric object. A parallelepiped is a
 * six-faced figure (also called a hexahedron) with opposite faces that are
 * parallel and congruent. The class supports defining the dimensions and
 * texture of the parallelepiped.
 *
 * This class extends {@link RepresentableConteneur}, allowing the parallelepiped
 * to contain and manage its sub-elements, such as polygons that make up its faces.
 *
 * @author Manuel Dahmen _manuel.dahmen@gmx.com_
 */
public class Parallelepiped extends RepresentableConteneur {
    Point3D[] p0;
    private double a = 1, b = 1, c = 1;

    public Parallelepiped(Point3D base, Point3D a, Point3D b, Point3D c, ITexture texture) {
        p0 = new Point3D[]{base, a, b, c};

        Point3D v_a = a.moins(base);
        Point3D v_b = b.moins(base);
        Point3D v_c = c.moins(base);

        Point3D p000 = base;
        Point3D p100 = base.plus(v_a);
        Point3D p010 = base.plus(v_b);
        Point3D p001 = base.plus(v_c);
        Point3D p110 = base.plus(v_a).plus(v_b);
        Point3D p101 = base.plus(v_a).plus(v_c);
        Point3D p011 = base.plus(v_b).plus(v_c);
        Point3D p111 = base.plus(v_a).plus(v_b).plus(v_c);

        // Face 1: Bottom (Z=0)
        add(new Polygon(new Point3D[]{p000, p100, p110, p010}, texture));
        // Face 2: Top (Z=1)
        add(new Polygon(new Point3D[]{p001, p101, p111, p011}, texture));
        // Face 3: Front (Y=0)
        add(new Polygon(new Point3D[]{p000, p100, p101, p001}, texture));
        // Face 4: Back (Y=1)
        add(new Polygon(new Point3D[]{p010, p110, p111, p011}, texture));
        // Face 5: Left (X=0)
        add(new Polygon(new Point3D[]{p000, p010, p011, p001}, texture));
        // Face 6: Right (X=1)
        add(new Polygon(new Point3D[]{p100, p110, p111, p101}, texture));
    }

    public Parallelepiped(double a, double b, double c, ColorTexture texture) {
        this.a = a;
        this.b = b;
        this.c = c;
        texture(texture);
        Point3D[] p = new Point3D[4];
        for (int x = -1; x <= 1; x += 2) {

            p[0] = new Point3D(x * a, -1 * b, -1 * c);
            p[1] = new Point3D(x * a, 1 * b, -1 * c);
            p[2] = new Point3D(x * a, 1 * b, 1 * c);
            p[3] = new Point3D(x * a, -1 * b, 1 * c);

            add(new Polygon(p, texture()));
        }
        for (int y = -1; y <= 1; y += 2) {
            p[0] = new Point3D(1 * a, y * b, 1 * c);
            p[1] = new Point3D(1 * a, y * b, -1 * c);
            p[2] = new Point3D(-1 * a, y * b, -1 * c);
            p[3] = new Point3D(-1 * a, y * b, 1 * c);

            add(new Polygon(p, texture()));
        }
        for (int z = -1; z <= 1; z += 2) {
            p[0] = new Point3D(-1 * a, -1 * b, z * c);
            p[1] = new Point3D(-1 * a, 1 * b, z * c);
            p[2] = new Point3D(1 * a, 1 * b, z * c);
            p[3] = new Point3D(1 * a, -1 * b, z * c);

            add(new Polygon(p, texture()));
        }
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }

    Point3D p(Point3D p0, double a, Point3D p1) {
        return p0.plus(p1.moins(p0).mult(a));
    }

    @Override
    public void setOrig(Point3D orig) {
        getListRepresentable().forEach(r -> r.setOrig(orig));
    }
}
