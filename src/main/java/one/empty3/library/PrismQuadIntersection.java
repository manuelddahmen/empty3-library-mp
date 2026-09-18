/*
 *
 *  *
 *  *  * Copyright (c) 2026. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2026 Manuel Daniel Dahmen
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

import one.empty3.library.Point3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrismQuadIntersection {

    private static final double EPS = 1e-9;

    // =====================================================
    // VECTOR OPERATIONS
    // =====================================================

    static Point3D add(Point3D a, Point3D b) {
        return new Point3D(
                a.getX() + b.getX(),
                a.getY() + b.getY(),
                a.getZ() + b.getZ()
        );
    }

    static Point3D sub(Point3D a, Point3D b) {
        return new Point3D(
                a.getX() - b.getX(),
                a.getY() - b.getY(),
                a.getZ() - b.getZ()
        );
    }

    static Point3D mul(Point3D a, double s) {
        return new Point3D(
                a.getX() * s,
                a.getY() * s,
                a.getZ() * s
        );
    }

    static double dot(Point3D a, Point3D b) {
        return a.getX() * b.getX()
                + a.getY() * b.getY()
                + a.getZ() * b.getZ();
    }

    static Point3D cross(Point3D a, Point3D b) {
        return new Point3D(
                a.getY() * b.getZ() - a.getZ() * b.getY(),
                a.getZ() * b.getX() - a.getX() * b.getZ(),
                a.getX() * b.getY() - a.getY() * b.getX()
        );
    }

    static double norm(Point3D a) {
        return Math.sqrt(dot(a, a));
    }

    static Point3D normalize(Point3D a) {
        double n = norm(a);

        if (n < EPS) {
            throw new IllegalArgumentException(
                    "Vecteur de longueur nulle"
            );
        }

        return mul(a, 1.0 / n);
    }

    static double distance(Point3D a, Point3D b) {
        return norm(sub(a, b));
    }

    // =====================================================
    // PLANE
    // =====================================================

    static class Plane {

        Point3D normal;
        double d;

        Plane(Point3D normal, double d) {
            this.normal = normal;
            this.d = d;
        }

        double evaluate(Point3D p) {
            return dot(normal, p) + d;
        }

        void reverse() {
            normal = mul(normal, -1.0);
            d = -d;
        }
    }

    // =====================================================
    // PLANE FROM 3 POINTS
    // =====================================================

    static Plane makePlane(
            Point3D a,
            Point3D b,
            Point3D c
    ) {
        Point3D ab = sub(b, a);
        Point3D ac = sub(c, a);

        Point3D n = normalize(cross(ab, ac));

        double d = -dot(n, a);

        return new Plane(n, d);
    }

    // =====================================================
    // PRISM CENTER
    // =====================================================

    static Point3D prismCenter(
            Point3D[] qNear,
            Point3D[] qFar
    ) {
        Point3D sum = new Point3D(0., 0., 0.);

        for (int i = 0; i < 4; i++) {
            sum = add(sum, qNear[i]);
            sum = add(sum, qFar[i]);
        }

        return mul(sum, 1.0 / 8.0);
    }

    // =====================================================
    // BUILD PRISM PLANES
    // =====================================================

    static List<Plane> buildPrismPlanes(
            Point3D[] qNear,
            Point3D[] qFar
    ) {
        List<Plane> planes = new ArrayList<>();

        Point3D center = prismCenter(qNear, qFar);

        // Near face
        planes.add(makePlane(
                qNear[0],
                qNear[1],
                qNear[2]
        ));

        // Far face
        planes.add(makePlane(
                qFar[0],
                qFar[2],
                qFar[1]
        ));

        // Four lateral faces
        for (int i = 0; i < 4; i++) {

            int j = (i + 1) % 4;

            planes.add(makePlane(
                    qNear[i],
                    qNear[j],
                    qFar[j]
            ));
        }

        // Orient all planes toward the outside.
        // The center must be inside each half-space.
        for (Plane plane : planes) {

            if (plane.evaluate(center) < 0.0) {
                plane.reverse();
            }
        }

        return planes;
    }

    // =====================================================
    // CLIP POLYGON AGAINST PLANE
    // =====================================================

    static List<Point3D> clipPolygonAgainstPlane(
            List<Point3D> polygon,
            Plane plane
    ) {
        List<Point3D> result = new ArrayList<>();

        if (polygon.isEmpty()) {
            return result;
        }

        for (int i = 0; i < polygon.size(); i++) {

            Point3D a = polygon.get(i);

            Point3D b = polygon.get(
                    (i + 1) % polygon.size()
            );

            double da = plane.evaluate(a);
            double db = plane.evaluate(b);

            boolean insideA = da >= -EPS;
            boolean insideB = db >= -EPS;

            if (insideA && insideB) {

                // Both inside
                result.add(b);

            } else if (insideA && !insideB) {

                // Leaving the volume
                result.add(intersection(a, b, da, db));

            } else if (!insideA && insideB) {

                // Entering the volume
                result.add(intersection(a, b, da, db));
                result.add(b);
            }
        }

        return removeDuplicatePoints(result);
    }

    // =====================================================
    // SEGMENT / PLANE INTERSECTION
    // =====================================================

    static Point3D intersection(
            Point3D a,
            Point3D b,
            double da,
            double db
    ) {
        double denominator = da - db;

        if (Math.abs(denominator) < EPS) {
            return a;
        }

        double t = da / denominator;

        return add(
                a,
                mul(sub(b, a), t)
        );
    }

    // =====================================================
    // REMOVE DUPLICATES
    // =====================================================

    static List<Point3D> removeDuplicatePoints(
            List<Point3D> points
    ) {
        List<Point3D> result = new ArrayList<>();

        for (Point3D p : points) {

            boolean duplicate = false;

            for (Point3D q : result) {

                if (distance(p, q) < EPS) {
                    duplicate = true;
                    break;
                }
            }

            if (!duplicate) {
                result.add(p);
            }
        }

        return result;
    }

    // =====================================================
    // MAIN INTERSECTION FUNCTION
    // =====================================================

    public static List<Point3D> intersect(
            Point3D[] qNear,
            Point3D[] qFar,
            Point3D[] quad
    ) {
        if (qNear.length != 4
                || qFar.length != 4
                || quad.length != 4) {

            throw new IllegalArgumentException(
                    "Les tableaux doivent contenir 4 points"
            );
        }

        List<Point3D> polygon = new ArrayList<>(
                Arrays.asList(quad)
        );

        List<Plane> planes = buildPrismPlanes(
                qNear,
                qFar
        );

        for (Plane plane : planes) {

            polygon = clipPolygonAgainstPlane(
                    polygon,
                    plane
            );

            if (polygon.isEmpty()) {
                break;
            }
        }

        return polygon;
    }
}