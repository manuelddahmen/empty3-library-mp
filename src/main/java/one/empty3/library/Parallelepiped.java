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
import one.empty3.library.TextureCol;

/*__
 * Meta Description missing
 * @author Manuel Dahmen dathewolf@gmail.com
 */
public class Parallelepiped extends RepresentableConteneur {
    Point3D[] p0;
    private double a = 1, b = 1, c = 1;

    public Parallelepiped(Point3D base, Point3D a, Point3D b, Point3D c, ITexture texture) {
        p0 = new Point3D[]{base, a, b, c};
        Point3D[] p1 = new Point3D[]{base};

        for (int face = 0; face < 6; face++) {
            int dim0 = face / 2;
            int dim1 = (dim0 + 1) % 3;
            int dim2 = (dim1 + 1) % 3;
            double[] cof = new double[]{(dim0 == 0) ? 1 : -1,
                    (dim1 == 1) ? 1 : -1, (dim2 == 2) ? 1 : -1};

            add(new Polygon(new Point3D[]{
                    p1[0], p(p1[0], cof[dim0], p0[1]),
                    p(p1[0], cof[dim1], p0[2]),
                    p(p1[0], cof[dim2], p0[2])}, texture()
            ));
            p1[0] = p(p1[0], cof[dim1], p0[2]);
        }
        // add( new Quad(p0[0], p0[1], p0[2], p0[3]));

    }

    public Parallelepiped(double a, double b, double c, TextureCol texture) {
        this.a = a;
        this.b = b;
        this.c = c;
        texture(texture);
        Point3D[] p = new Point3D[4];
        for (int x = -1; x <= 1; x++) {

            p[0] = new Point3D(x * a, -1 * c);
            p[1] = new Point3D(x * a, 1 * b, -1 * c);
            p[2] = new Point3D(x * a, 1 * b, 1 * c);
            p[3] = new Point3D(x * a, -1 * b, 1 * c);

            add(new Polygon(p, texture()));
        }
        for (int y = -1; y <= 1; y++) {
            p[0] = new Point3D(1 * a, y * b, 1 * c);
            p[1] = new Point3D(1 * a, y * b, -1 * c);
            p[2] = new Point3D(-1 * a, y * b, -1 * c);
            p[3] = new Point3D(-1 * a, y * b, 1 * c);

            add(new Polygon(p, texture()));
        }
        for (int z = -1; z <= 1; z++) {
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
}
