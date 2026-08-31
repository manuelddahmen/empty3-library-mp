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

/***
 * Gemini AI code generation 2025
 * Refactored for geometric point-in-polygon test.
 */
package one.empty3.apps.facedetect.jvm;

import one.empty3.library.Lumiere; // Keep Lumiere if needed elsewhere, but not for testIfIn
import one.empty3.library.Point3D;
import one.empty3.libs.Color;
import one.empty3.libs.Image;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
// Removed unused File I/O imports unless you plan to uncomment that section
// import java.io.File;
// import java.io.IOException;

public class ConvexHull {
    private final List<Point3D> originalScaledPoints; // Renamed for clarity
    private final Image mask;
    private List<Point3D> hullPoints = new ArrayList<>(); // Renamed for clarity

    public ConvexHull(List<Point3D> inputPoints, Dimension dimension) {
        if (inputPoints == null || dimension == null) {
            throw new IllegalArgumentException("Input points list and dimension cannot be null.");
        }
        if (dimension.getWidth() <= 0 || dimension.getHeight() <= 0) {
            throw new IllegalArgumentException("Dimension width and height must be positive.");
        }

        this.mask = new Image((int) dimension.getWidth(), (int) dimension.getHeight());

        // Scale points according to the image dimensions
        this.originalScaledPoints = new ArrayList<>(inputPoints.size());
        double width = dimension.getWidth();
        double height = dimension.getHeight();
        for (Point3D point : inputPoints) {
            // Assuming input points are normalized (0-1)
            originalScaledPoints.add(point.multDot(new Point3D(width, height, 1.0))); // Use 1.0 for Z if needed, or 0.0
        }

        createConvexHullAndMask();
    }

    /**
     * Computes the convex hull of the scaled points using the Gift Wrapping algorithm (Jarvis March).
     * Assumes points are 2D (ignores Z coordinate).
     * @return A list of points representing the convex hull in counter-clockwise order.
     */
    private List<Point3D> computeHull() {
        List<Point3D> hull = new ArrayList<>();
        int n = originalScaledPoints.size();
        if (n < 3) {
            // Convex hull is not well-defined for < 3 points, return the points themselves.
            // Or handle as needed (e.g., return empty list, throw exception)
            return new ArrayList<>(originalScaledPoints);
        }

        // Find the leftmost point (guaranteed to be on the hull)
        int leftmostIndex = 0;
        for (int i = 1; i < n; i++) {
            if (originalScaledPoints.get(i).getX() < originalScaledPoints.get(leftmostIndex).getX()) {
                leftmostIndex = i;
            }
            // Tie-breaking: if x is the same, choose the lowest y
            else if (originalScaledPoints.get(i).getX() == originalScaledPoints.get(leftmostIndex).getX() &&
                    originalScaledPoints.get(i).getY() < originalScaledPoints.get(leftmostIndex).getY()) {
                leftmostIndex = i;
            }
        }

        int currentPointIndex = leftmostIndex;
        int nextPointIndex;
        do {
            hull.add(originalScaledPoints.get(currentPointIndex));
            nextPointIndex = (currentPointIndex + 1) % n; // Initial guess for the next point

            // Find the point that makes the leftmost turn
            for (int i = 0; i < n; i++) {
                // If point i is more counter-clockwise than the current nextPoint
                if (orientation(originalScaledPoints.get(currentPointIndex),
                        originalScaledPoints.get(i),
                        originalScaledPoints.get(nextPointIndex)) == 2) // 2 means counter-clockwise (left turn)
                {
                    nextPointIndex = i;
                }
                // Handle collinear points: choose the farthest one
                else if (orientation(originalScaledPoints.get(currentPointIndex),
                        originalScaledPoints.get(i),
                        originalScaledPoints.get(nextPointIndex)) == 0 && // 0 means collinear
                        distanceSq(originalScaledPoints.get(currentPointIndex), originalScaledPoints.get(i)) >
                                distanceSq(originalScaledPoints.get(currentPointIndex), originalScaledPoints.get(nextPointIndex)))
                {
                    nextPointIndex = i;
                }
            }
            currentPointIndex = nextPointIndex;
        } while (currentPointIndex != leftmostIndex); // Stop when we wrap back to the start

        return hull;
    }

    /**
     * Calculates the orientation of ordered triplet (p, q, r).
     * Uses the cross product to determine the turn direction.
     * @param p First point.
     * @param q Second point (vertex).
     * @param r Third point.
     * @return 0 if points are collinear, 1 if clockwise, 2 if counter-clockwise.
     */
    private int orientation(Point3D p, Point3D q, Point3D r) {
        double val = (q.getY() - p.getY()) * (r.getX() - q.getX()) -
                (q.getX() - p.getX()) * (r.getY() - q.getY());

        if (Math.abs(val) < 1e-9) return 0; // Collinear (use tolerance for floating point)
        return (val > 0) ? 1 : 2; // 1 for Clockwise, 2 for Counter-Clockwise
    }

    /**
     * Calculates the square of the Euclidean distance between two points.
     * Avoids using sqrt for comparison purposes.
     * @param p1 First point.
     * @param p2 Second point.
     * @return Squared distance.
     */
    private double distanceSq(Point3D p1, Point3D p2) {
        double dx = p1.getX() - p2.getX();
        double dy = p1.getY() - p2.getY();
        return dx * dx + dy * dy;
    }


    /**
     * Computes the convex hull and fills the mask image based on the hull.
     */
    private void createConvexHullAndMask() {
        this.hullPoints = computeHull();

        if (this.hullPoints.size() < 3) {
            Logger.getAnonymousLogger().log(Level.WARNING, "Convex hull has less than 3 points, mask might be empty or just points/lines.");
            // Optionally handle drawing points or lines if needed
            // For now, fillPolyMp won't fill anything substantial.
        }

        fillMaskWithHull();

        Logger.getAnonymousLogger().log(Level.INFO, "ConvexHull computed with " + hullPoints.size() + " points.");
    }

    /**
     * Fills the mask image. Pixels inside the convex hull are set to white.
     */
    private void fillMaskWithHull() {
        int whiteRgb = Color.newCol(1f, 1f, 1f).getRGB();
        int blackRgb = Color.newCol(0f, 0f, 0f).getRGB(); // Assuming default is black

        // Optimization: Find bounding box of the hull
        if (hullPoints.isEmpty()) return; // Nothing to fill

        double minX = Double.MAX_VALUE, minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE, maxY = Double.MIN_VALUE;
        for(Point3D p : hullPoints) {
            minX = Math.min(minX, p.getX());
            minY = Math.min(minY, p.getY());
            maxX = Math.max(maxX, p.getX());
            maxY = Math.max(maxY, p.getY());
        }

        // Clamp bounding box to image dimensions
        int startX = Math.max(0, (int) Math.floor(minX));
        int startY = Math.max(0, (int) Math.floor(minY));
        int endX = Math.min(mask.getWidth(), (int) Math.ceil(maxX) + 1); // +1 to include edge cases
        int endY = Math.min(mask.getHeight(), (int) Math.ceil(maxY) + 1);


        for (int j = startY; j < endY; j++) { // Iterate Y (rows) outer loop - often better cache locality
            for (int i = startX; i < endX; i++) { // Iterate X (columns) inner loop
                if (isPointInHull(i + 0.5, j + 0.5)) { // Test center of the pixel
                    mask.setRgb(i, j, whiteRgb);
                } else {
                    // Optional: Explicitly set outside points to black if mask isn't default black
                    // mask.setRgb(i, j, blackRgb);
                }
            }
        }
    }

    /**
     * Geometric test to check if a point (x, y) is inside the computed convex hull.
     * Assumes hullPoints are ordered counter-clockwise.
     * A point is inside if it is always to the left of or on every edge segment of the hull.
     *
     * @param x The x-coordinate of the point to test.
     * @param y The y-coordinate of the point to test.
     * @return true if the point is inside or on the boundary of the hull, false otherwise.
     */
    public boolean isPointInHull(double x, double y) {
        if (hullPoints.size() < 3) {
            // Handle cases with less than 3 points (point, line segment)
            // This basic implementation returns false for simplicity.
            // A more robust version could check if the point lies on the segment or matches a vertex.
            return false;
        }

        Point3D testPoint = new Point3D(x, y, 0.0); // Z doesn't matter for 2D test

        for (int i = 0; i < hullPoints.size(); i++) {
            Point3D p1 = hullPoints.get(i);
            Point3D p2 = hullPoints.get((i + 1) % hullPoints.size()); // Wrap around for the last edge

            // If the test point is not to the left (or collinear) of the edge (p1 -> p2),
            // it's outside the hull.
            // orientation() == 2 means counter-clockwise (left turn)
            // orientation() == 0 means collinear
            if (orientation(p1, p2, testPoint) == 1) { // 1 means clockwise (right turn)
                return false;
            }
        }

        // If the point was to the left of or on all edges, it's inside or on the boundary.
        return true;
    }

    // --- Getters ---

    /**
     * Gets the list of original points after scaling.
     * @return The list of scaled points.
     */
    public List<Point3D> getOriginalScaledPoints() {
        return originalScaledPoints;
    }

    /**
     * Gets the generated mask image where the hull area is white.
     * @return The mask image.
     */
    public Image getMask() {
        return mask;
    }

    /**
     * Gets the points forming the convex hull.
     * @return The list of hull vertices.
     */
    public List<Point3D> getHullPoints() {
        return hullPoints;
    }

    // Removed setP as hullPoints should ideally be computed internally and immutable from outside
    // public void setP(List<Point3D> p) { this.hullPoints = p; }

    // Removed the old testIfIn method that relied on checking mask color
    /*
    public boolean testIfIn(int x, int y) {
        // This version relied on the mask being pre-filled, which is what we wanted to avoid.
        // ... old code checking mask.getRgb() ...
    }
    */
}