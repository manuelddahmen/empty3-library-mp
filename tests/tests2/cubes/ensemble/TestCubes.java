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
 * 2013 Manuel Dahmen
 */
package one.empty3.library.tests.tests2.cubes.ensemble;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.*;
import one.empty3.library.core.lighting.Colors;

public class TestCubes extends TestObjetSub {
    private static final int M = 10;
    private static final int N = 10;
    private static final double r = 0.33;
    private static final double g = 0.33;
    private static final double b = 0.33;
    private static final double D = 100;
    private double[][] dim = new double[M][N];
    private Point3D[][] s = new Point3D[M][N];
    private Point3D[][] v = new Point3D[M][N];

    public static void main(String[] args) {
        TestCubes tc = new TestCubes();

        tc.loop(true);

        tc.setMaxFrames(10000);

        tc.setResx(HD1080.x());
        tc.setResy(HD1080.y());

        tc.run();
    }

    public void bounce(Point3D s, Point3D v) {
        s.set(0, s.get(0) + v.get(0));
        s.set(1, s.get(1) + v.get(1));
        s.set(2, s.get(2) + v.get(2));

        if (s.getX() > D


                && v.getX() > 0) {
            v.setX(-v.getX());
        }
        if (s.getX() < -D && v.getX() < 0) {
            v.setX(-v.getX());
        }
        if (s.getY() > D && v.getY() > 0) {
            v.setY(-v.getY());
        }
        if (s.getY() < -D && v.getY() < 0) {
            v.setY(-v.getY());
        }
        if (s.getZ() > D && v.getZ() > 0) {
            v.setZ(-v.getZ());
        }
        if (s.getZ() < -D && v.getZ() < 0) {
            v.setZ(-v.getZ());
        }
    }

    @Override
    public void ginit() {
        for (int i = 0; i < s.length; i++)
            for (int j = 0; j < s[i].length; j++)
                s[i][j] = new Point3D();
        for (int i = 0; i < s.length; i++)
            for (int j = 0; j < s[i].length; j++) {
                int vfactor = 25;
                v[i][j] = new Point3D(Math.random() * D / vfactor,
                        Math.random() * D / vfactor,
                        Math.random() * D / vfactor);
                dim[i][j] = Math.random() * D / 25;
            }

        for (int i = 0; i < s.length; i++)
            for (int j = 0; j < s[i].length; j++)
                scene().add(new Cube(dim[i][j], s[i][j], new TextureCol(one.empty3.libs.Color.newCol((float)Math.random(),(float)Math.random(),(float)Math.random()))));


    }

    @Override
    public void finit() {
        for (int i = 0; i < s.length; i++)
            for (int j = 0; j < s[i].length; j++)
                bounce(s[i][j], v[i][j]);
    }

    private double z(double min, double max, int framemin, int nof) {
        return min + (max - min) * (1.0 * (frame() - framemin) / nof);
    }

    @Override
    public void testScene() {
        scene().cameras().clear();

        scene().cameraActive(new Camera(
                new Point3D(0d, 0d, -D * 2),
                new Point3D(0d, 0d, 1000d)
        ));
    }

    protected Matrix33 spin(Matrix33 rot) {
        return null;
    }
}
/*
class Cube extends RepresentableConteneur {
    private ArrayList<Representable> cube = new ArrayList<Representable>();

    public Cube(double dim, int pas, Color c) {
        if (dim < 0)
            return;
        for (double i = -dim / 2; i < dim / 2 + pas; i += pas)
            for (double j = -dim / 2; j < dim / 2 + pas; j += pas)
                for (double k = -dim / 2; k < dim / 2 + pas; k += pas) {
                    if (1.0 * i + 1.0 * dim / pas < dim / 2 + pas) {
                        cube.add(new LineSegment(
                                        new Point3D(1.0 * i, 1.0 * j, 1.0 * k),
                                        new Point3D(1.0 * i + 1.0 * dim / pas, 1.0 * j, 1.0 * k),
                                        new TextureCol(c)
                                )
                        );
                    }
                    if (1.0 * j + 1.0 * dim / pas < dim / 2 + pas)
                        cube.add(new LineSegment(
                                        new Point3D(1.0 * i, 1.0 * j, 1.0 * k),
                                        new Point3D(1.0 * i, 1.0 * j + 1.0 * dim / pas, 1.0 * k),
                                        new TextureCol(c)
                                )
                        );
                    if (1.0 * k + 1.0 * dim / pas < dim / 2 + pas)
                        cube.add(new LineSegment(
                                        new Point3D(1.0 * i, 1.0 * j, 1.0 * k),
                                        new Point3D(1.0 * i, 1.0 * j, 1.0 * k + 1.0 * dim / pas),
                                        new TextureCol(c)
                                )
                        );
                }
    }

    public void deforme(Point3D p) {
        throw new UnsupportedOperationException("Pas encore implémenté");
    }

    @Override
    public List<Representable> getListRepresentable() {
        return cube;
    }

}*/