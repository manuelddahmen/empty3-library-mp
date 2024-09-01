package one.empty3.feature;// Java program to find convex hull of a set of Point3Ds. Refer
// https://www.geeksforgeeks.org/orientation-3-ordered-Point3Ds/
// for explanation of orientation()

import one.empty3.library.Point3D;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import java.util.*;


public class ConvHull {

    // To find orientation of ordered triplet (p, q, r).
    // The function returns following values
    // 0 --> p, q and r are collinear
    // 1 --> Clockwise
    // 2 --> Counterclockwise
    public static int orientation(Point3D p, Point3D q, Point3D r) {
        int val = (int) ((q.getY() - p.getY()) * (r.getX() - q.getX()) -
                (q.getX() - p.getX()) * (r.getY() - q.getY()));

        if (val == 0) return 0; // collinear
        return (val > 0) ? 1 : 2; // clock or counterclock wise
    }

    // Prints convex hull of a set of n hull0.
    public static List<Point3D> convexHull(List<Point3D> hull0, int n) {
        // There must be at least 3 hull0
        if (n < 3) return null;

        // Initialize Result
        List<Point3D> hull = new ArrayList<>();

        // Find the leftmost Point3D
        int l = 0;
        for (int i = 1; i < n; i++)
            if (hull0.get(i).getX() < hull0.get(l).getX())
                l = i;

        // Start from leftmost Point3D, keep moving
        // counterclockwise until reach the start Point3D
        // again. This loop runs O(h) times where h is
        // number of hull0 in result or output.
        int p = l, q;
        do {
            // Add current Point3D to result
            hull.add(hull0.get(p));

            // Search for a Point3D 'q' such that
            // orientation(p, q, x) is counterclockwise
            // for all hull0 'x'. The idea is to keep
            // track of last visited most counterclock-
            // wise Point3D in q. If any Point3D 'i' is more
            // counterclock-wise than q, then update q.
            q = (p + 1) % n;

            for (int i = 0; i < n; i++) {
                // If i is more counterclockwise than
                // current q, then update q
                if (orientation(hull0.get(p), hull0.get(i), hull0.get(q))
                        == 2)
                    q = i;
            }

            // Now q is the most counterclockwise with
            // respect to p. Set p as q for next iteration,
            // so that q is added to result 'hull'
            p = q;

        } while (p != l); // While we don't come to first
        // Point3D
        // Print Result
        List<Point3D> listRet = hull;
        return listRet;
    }

    /* Driver program to test above function */
    public static void main(String[] args) {

        Point3D[] Point3Ds = new Point3D[7];
        Point3Ds[0] = new Point3D(0., 3., 0.);
        Point3Ds[1] = new Point3D(2., 3., 0.);
        Point3Ds[2] = new Point3D(1., 1., 0.);
        Point3Ds[3] = new Point3D(2., 1., 0.);
        Point3Ds[4] = new Point3D(3., 0., 0.);
        Point3Ds[5] = new Point3D(0., 0., 0.);
        Point3Ds[6] = new Point3D(3., 3., 0.);

        int n = Point3Ds.length;
        List<Point3D> hull = convexHull(Arrays.stream(Point3Ds).toList(), n);

        for (Point3D temp : hull)
            System.out.println("(" + temp.getX() + ", " +
                    temp.getY() + ")");
    }


    public static boolean convexHullTestPointIsInside(List<Point3D> points_list, Point3D testPoint) {
        Path2D path = new Path2D.Double();

        // Move to the first point in the polygon
        path.moveTo(points_list.get(0).getX(), points_list.get(0).getY());

        // Connect the points in the polygon
        for (int i = 1; i < points_list.size(); i++) {
            path.lineTo(points_list.get(i).getX(), points_list.get(i).getY());
        }

        // Close the path
        path.closePath();

        // Create a Point2D object for the test point

        // Check if the test point is inside the polygon
        return path.contains(

                new Point2D() {
                    @Override
                    public double getX() {
                        return testPoint.getX();
                    }

                    @Override
                    public double getY() {
                        return testPoint.getY();
                    }

                    @Override
                    public void setLocation(double x, double y) {
                        testPoint.setX(x);
                        testPoint.setY(y);
                    }
                });


    }

// This code is contributed by Arnav Kr. Mandal.
}