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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Intersection entre un frustum de caméra perspective
 * et un quadrilatère 3D.
 * <p>
 * Utilise le repère de Camera :
 * X horizontal
 * Y vertical
 * Z profondeur positive
 *
 * @author Manuel Daniel Dahmen
 */
public class CameraFrustumQuadIntersection {

    private static final double EPS = 1e-9;

    private CameraFrustumQuadIntersection() {
    }

    public enum IntersectionType {
        NONE,
        PARTIAL,
        FULL
    }

    public static class Result {

        public final List<Point3D> polygon;
        public final double area;
        public final IntersectionType type;

        public Result(
                List<Point3D> polygon,
                double area,
                IntersectionType type
        ) {
            this.polygon = polygon;
            this.area = area;
            this.type = type;
        }

        public boolean isEmpty() {
            return polygon.isEmpty();
        }

        public boolean isFull() {
            return type == IntersectionType.FULL;
        }

        public boolean isPartial() {
            return type == IntersectionType.PARTIAL;
        }
    }

    // =========================================================
    // VECTOR OPERATIONS
    // =========================================================

    private static Point3D add(Point3D a, Point3D b) {
        return new Point3D(
                a.getX() + b.getX(),
                a.getY() + b.getY(),
                a.getZ() + b.getZ()
        );
    }

    private static Point3D sub(Point3D a, Point3D b) {
        return new Point3D(
                a.getX() - b.getX(),
                a.getY() - b.getY(),
                a.getZ() - b.getZ()
        );
    }

    private static Point3D mul(Point3D a, double s) {
        return new Point3D(
                a.getX() * s,
                a.getY() * s,
                a.getZ() * s
        );
    }

    private static double dot(Point3D a, Point3D b) {
        return a.getX() * b.getX()
                + a.getY() * b.getY()
                + a.getZ() * b.getZ();
    }

    private static Point3D cross(Point3D a, Point3D b) {
        return new Point3D(
                a.getY() * b.getZ() - a.getZ() * b.getY(),
                a.getZ() * b.getX() - a.getX() * b.getZ(),
                a.getX() * b.getY() - a.getY() * b.getX()
        );
    }

    private static double norm(Point3D a) {
        return Math.sqrt(dot(a, a));
    }

    private static Point3D normalize(Point3D a) {
        double n = norm(a);

        if (n < EPS) {
            throw new IllegalArgumentException(
                    "Vecteur de longueur nulle"
            );
        }

        return mul(a, 1.0 / n);
    }

    private static double distance(Point3D a, Point3D b) {
        return norm(sub(a, b));
    }

    // =========================================================
    // PLANE
    // =========================================================

    private static class Plane {

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

    private static Plane makePlane(
            Point3D a,
            Point3D b,
            Point3D c
    ) {
        Point3D ab = sub(b, a);
        Point3D ac = sub(c, a);

        Point3D normal = normalize(cross(ab, ac));

        double d = -dot(normal, a);

        return new Plane(normal, d);
    }

    // =========================================================
    // CAMERA SPACE -> WORLD SPACE
    // =========================================================

    /**
     * Convertit un point du repère caméra vers le repère monde.
     * <p>
     * Camera.calculerPointDansRepere() utilise :
     * <p>
     * M * (world - eye)
     * <p>
     * La matrice M est supposée orthonormale.
     * L'inverse est donc sa transposée.
     */
    private static Point3D cameraToWorld(
            Camera camera,
            Point3D local
    ) {
        Matrix33 m = camera.getMatrice();

        double x =
                m.get(0, 0) * local.getX()
                        + m.get(1, 0) * local.getY()
                        + m.get(2, 0) * local.getZ();

        double y =
                m.get(0, 1) * local.getX()
                        + m.get(1, 1) * local.getY()
                        + m.get(2, 1) * local.getZ();

        double z =
                m.get(0, 2) * local.getX()
                        + m.get(1, 2) * local.getY()
                        + m.get(2, 2) * local.getZ();

        return add(
                camera.getEye(),
                new Point3D(x, y, z)
        );
    }

    // =========================================================
    // FRUSTUM VERTICES
    // =========================================================

    /**
     * Construit les 8 sommets du frustum.
     * <p>
     * Ordre des sommets de chaque plan :
     * <p>
     * 0 -------- 1
     * |          |
     * 3 -------- 2
     * <p>
     * La base Near est suivie de la base Far.
     */
    private static Point3D[] buildFrustumVertices(
            Camera camera,
            double near,
            double far
    ) {
        if (near <= 0.0 || far <= near) {
            throw new IllegalArgumentException(
                    "Distances near/far invalides"
            );
        }

        double tanX = Math.tan(camera.getAngleX());
        double tanY = Math.tan(camera.getAngleY());

        Point3D[] vertices = new Point3D[8];

        // Near
        vertices[0] = new Point3D(
                -near * tanX,
                -near * tanY,
                near
        );

        vertices[1] = new Point3D(
                near * tanX,
                -near * tanY,
                near
        );

        vertices[2] = new Point3D(
                near * tanX,
                near * tanY,
                near
        );

        vertices[3] = new Point3D(
                -near * tanX,
                near * tanY,
                near
        );

        // Far
        vertices[4] = new Point3D(
                -far * tanX,
                -far * tanY,
                far
        );

        vertices[5] = new Point3D(
                far * tanX,
                -far * tanY,
                far
        );

        vertices[6] = new Point3D(
                far * tanX,
                far * tanY,
                far
        );

        vertices[7] = new Point3D(
                -far * tanX,
                far * tanY,
                far
        );

        for (int i = 0; i < vertices.length; i++) {
            vertices[i] = cameraToWorld(
                    camera,
                    vertices[i]
            );
        }

        return vertices;
    }

    // =========================================================
    // FRUSTUM PLANES
    // =========================================================

    private static List<Plane> buildFrustumPlanes(
            Point3D[] v
    ) {
        List<Plane> planes = new ArrayList<>();

        Point3D center = new Point3D(
                (v[0].getX() + v[6].getX()) / 2.0,
                (v[0].getY() + v[6].getY()) / 2.0,
                (v[0].getZ() + v[6].getZ()) / 2.0
        );

        // Near
        planes.add(makePlane(
                v[0], v[1], v[2]
        ));

        // Far
        planes.add(makePlane(
                v[5], v[4], v[7]
        ));

        // Bottom
        planes.add(makePlane(
                v[0], v[4], v[5]
        ));

        // Right
        planes.add(makePlane(
                v[1], v[5], v[6]
        ));

        // Top
        planes.add(makePlane(
                v[2], v[6], v[7]
        ));

        // Left
        planes.add(makePlane(
                v[3], v[7], v[4]
        ));

        /*
         * Orientation des demi-espaces.
         * Le centre doit rester à l'intérieur.
         */
        for (Plane plane : planes) {
            if (plane.evaluate(center) < 0.0) {
                plane.reverse();
            }
        }

        return planes;
    }

    // =========================================================
    // POLYGON CLIPPING
    // =========================================================

    private static List<Point3D> clipPolygonAgainstPlane(
            List<Point3D> polygon,
            Plane plane
    ) {
        if (polygon.isEmpty()) {
            return Collections.emptyList();
        }

        List<Point3D> result = new ArrayList<>();

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

                result.add(b);

            } else if (insideA && !insideB) {

                result.add(
                        intersection(a, b, da, db)
                );

            } else if (!insideA && insideB) {

                result.add(
                        intersection(a, b, da, db)
                );

                result.add(b);
            }
        }

        return removeDuplicatePoints(result);
    }

    private static Point3D intersection(
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

    private static List<Point3D> removeDuplicatePoints(
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

    // =========================================================
    // POINT INSIDE FRUSTUM
    // =========================================================

    private static boolean isInside(
            Point3D p,
            List<Plane> planes
    ) {
        for (Plane plane : planes) {

            if (plane.evaluate(p) < -EPS) {
                return false;
            }
        }

        return true;
    }

    // =========================================================
    // POLYGON AREA
    // =========================================================

    public static double polygonArea(
            List<Point3D> polygon
    ) {
        if (polygon.size() < 3) {
            return 0.0;
        }

        Point3D origin = polygon.get(0);

        double area = 0.0;

        for (int i = 1; i < polygon.size() - 1; i++) {

            Point3D b = polygon.get(i);
            Point3D c = polygon.get(i + 1);

            area += 0.5 * norm(
                    cross(
                            sub(b, origin),
                            sub(c, origin)
                    )
            );
        }

        return area;
    }

    // =========================================================
    // INTERSECTION
    // =========================================================

    public static Result intersect(
            Camera camera,
            double near,
            double far,
            Point3D p1,
            Point3D p2,
            Point3D p3,
            Point3D p4
    ) {
        Point3D[] quad = {
                p1, p2, p3, p4
        };

        return intersect(
                camera,
                near,
                far,
                quad
        );
    }

    public static Result intersect(
            Camera camera,
            double near,
            double far,
            Point3D[] quad
    ) {
        if (quad == null || quad.length != 4) {
            throw new IllegalArgumentException(
                    "Le quadrilatère doit contenir 4 points"
            );
        }

        Point3D[] vertices =
                buildFrustumVertices(
                        camera,
                        near,
                        far
                );

        List<Plane> planes =
                buildFrustumPlanes(vertices);

        boolean allInside = true;

        for (Point3D p : quad) {
            if (!isInside(p, planes)) {
                allInside = false;
                break;
            }
        }

        List<Point3D> polygon =
                new ArrayList<>(
                        Arrays.asList(quad)
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

        double area = polygonArea(polygon);

        IntersectionType type;

        if (polygon.isEmpty()) {
            type = IntersectionType.NONE;

        } else if (allInside) {
            type = IntersectionType.FULL;

        } else {
            type = IntersectionType.PARTIAL;
        }

        return new Result(
                polygon,
                area,
                type
        );
    }
}