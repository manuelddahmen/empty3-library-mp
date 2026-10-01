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
import java.util.Collections;
import java.util.List;

public class FrustumPolygonIntersection {

    private static final double EPSILON = 1.0e-9;

    /**
     * Point 3D associé à des coordonnées de texture.
     */
    public static class TexturedPoint {

        public final Point3D p;
        public final double u;
        public final double v;

        public TexturedPoint(Point3D p, double u, double v) {
            this.p = p;
            this.u = u;
            this.v = v;
        }

        @Override
        public String toString() {
            return "TexturedPoint{" +
                    "p=" + p +
                    ", u=" + u +
                    ", v=" + v +
                    '}';
        }
    }

    /**
     * Plan défini par :
     * <p>
     * a*x + b*y + c*z + d >= 0
     * <p>
     * Un point est à l'intérieur si evaluate(point) >= 0.
     */
    public static class Plane {

        public final double a;
        public final double b;
        public final double c;
        public final double d;

        public Plane(double a, double b, double c, double d) {
            this.a = a;
            this.b = b;
            this.c = c;
            this.d = d;
        }

        public double evaluate(Point3D p) {
            return a * p.get(0)
                    + b * p.get(1)
                    + c * p.get(2)
                    + d;
        }

        public boolean inside(Point3D p) {
            return evaluate(p) >= -EPSILON;
        }
    }

    public enum IntersectionType {
        NONE,
        PARTIAL,
        FULL
    }

    public static class Result {

        private final List<TexturedPoint> polygon;
        private final IntersectionType type;

        public Result(
                List<TexturedPoint> polygon,
                IntersectionType type
        ) {
            this.polygon = polygon;
            this.type = type;
        }

        public List<TexturedPoint> getPolygon() {
            return polygon;
        }

        public IntersectionType getType() {
            return type;
        }

        public boolean isEmpty() {
            return polygon == null || polygon.size() < 3;
        }
    }

    /**
     * Calcule l'intersection d'un polygone avec plusieurs plans.
     *
     * @param polygon polygone initial
     * @param planes  plans du frustum
     * @return résultat de l'intersection
     */
    public static Result intersects(
            List<TexturedPoint> polygon,
            List<Plane> planes
    ) {
        if (polygon == null || polygon.size() < 3) {
            return new Result(
                    Collections.emptyList(),
                    IntersectionType.NONE
            );
        }

        if (planes == null || planes.isEmpty()) {
            return new Result(
                    new ArrayList<>(polygon),
                    IntersectionType.FULL
            );
        }

        boolean completelyInside = true;

        for (TexturedPoint point : polygon) {
            for (Plane plane : planes) {
                if (!plane.inside(point.p)) {
                    completelyInside = false;
                    break;
                }
            }
        }

        List<TexturedPoint> result = new ArrayList<>(polygon);

        for (Plane plane : planes) {
            result = clipPolygon(result, plane);

            if (result.size() < 3) {
                return new Result(
                        Collections.emptyList(),
                        IntersectionType.NONE
                );
            }
        }

        IntersectionType type = completelyInside
                ? IntersectionType.FULL
                : IntersectionType.PARTIAL;

        return new Result(result, type);
    }

    /**
     * Effectue le clipping d'un polygone par un plan.
     * <p>
     * Algorithme de Sutherland-Hodgman.
     */
    private static List<TexturedPoint> clipPolygon(
            List<TexturedPoint> polygon,
            Plane plane
    ) {
        if (polygon.isEmpty()) {
            return Collections.emptyList();
        }

        List<TexturedPoint> output = new ArrayList<>();

        for (int i = 0; i < polygon.size(); i++) {

            TexturedPoint current = polygon.get(i);
            TexturedPoint next =
                    polygon.get((i + 1) % polygon.size());

            double dc = plane.evaluate(current.p);
            double dn = plane.evaluate(next.p);

            boolean currentInside = dc >= -EPSILON;
            boolean nextInside = dn >= -EPSILON;

            if (currentInside && nextInside) {

                // Les deux sommets sont à l'intérieur.
                output.add(next);

            } else if (currentInside && !nextInside) {

                // Sortie du frustum.
                output.add(intersection(
                        current,
                        next,
                        dc,
                        dn
                ));

            } else if (!currentInside && nextInside) {

                // Entrée dans le frustum.
                output.add(intersection(
                        current,
                        next,
                        dc,
                        dn
                ));

                output.add(next);
            }

            // Si les deux sommets sont à l'extérieur :
            // aucun point n'est ajouté.
        }

        return removeDuplicatePoints(output);
    }

    /**
     * Calcule l'intersection d'un segment avec un plan.
     * <p>
     * Le même paramètre t est appliqué aux coordonnées 3D et UV.
     */
    private static TexturedPoint intersection(
            TexturedPoint a,
            TexturedPoint b,
            double da,
            double db
    ) {
        double denominator = da - db;

        double t;

        if (Math.abs(denominator) < EPSILON) {
            t = 0.0;
        } else {
            t = da / denominator;
        }

        t = Math.max(0.0, Math.min(1.0, t));

        Point3D direction = b.p.moins(a.p);

        Point3D point = a.p.plus(
                direction.mult(t)
        );

        double u = a.u + t * (b.u - a.u);
        double v = a.v + t * (b.v - a.v);

        return new TexturedPoint(point, u, v);
    }

    /**
     * Supprime les sommets consécutifs identiques.
     */
    private static List<TexturedPoint> removeDuplicatePoints(
            List<TexturedPoint> points
    ) {
        if (points.size() < 2) {
            return points;
        }

        List<TexturedPoint> result = new ArrayList<>();

        for (TexturedPoint point : points) {

            if (result.isEmpty()) {
                result.add(point);
                continue;
            }

            TexturedPoint previous =
                    result.get(result.size() - 1);

            if (!samePoint(previous.p, point.p)) {
                result.add(point);
            }
        }

        if (result.size() > 1) {

            TexturedPoint first = result.get(0);
            TexturedPoint last =
                    result.get(result.size() - 1);

            if (samePoint(first.p, last.p)) {
                result.remove(result.size() - 1);
            }
        }

        return result;
    }

    private static boolean samePoint(
            Point3D a,
            Point3D b
    ) {
        return Math.abs(a.get(0) - b.get(0)) < EPSILON
                && Math.abs(a.get(1) - b.get(1)) < EPSILON
                && Math.abs(a.get(2) - b.get(2)) < EPSILON;
    }

    /**
     * Vérifie si tous les sommets sont à l'intérieur du frustum.
     */
    public static boolean isFullyInside(
            List<TexturedPoint> polygon,
            List<Plane> planes
    ) {
        if (polygon == null || polygon.isEmpty()) {
            return false;
        }

        for (TexturedPoint point : polygon) {
            for (Plane plane : planes) {
                if (!plane.inside(point.p)) {
                    return false;
                }
            }
        }

        return true;
    }
}