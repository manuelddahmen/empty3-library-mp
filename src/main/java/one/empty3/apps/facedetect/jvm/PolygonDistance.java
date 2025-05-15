package one.empty3.apps.facedetect.jvm;

import one.empty3.library.Point3D;

import java.util.List;

public class PolygonDistance {

    public static double distanceToPolygon(Point3D point, List<Point3D> polygon) {
        double minDistance = Double.POSITIVE_INFINITY;

        // Iterate through all edges of the polygon
        for (int i = 0; i < polygon.size(); i++) {
            Point3D p1 = polygon.get(i);
            Point3D p2 = polygon.get((i + 1) % polygon.size()); // Wrap around for the last edge

            double distance = distanceToLineSegment(point, p1, p2);
            minDistance = Math.min(minDistance, distance);
        }

        return minDistance;
    }


    public static double distanceToLineSegment(Point3D point, Point3D p1, Point3D p2) {

        double segmentLengthSquared = p1.moins(p2).NormeCarree();

        if (segmentLengthSquared == 0) {
            // p1 and p2 are the same point
            return point.distance(p1);
        }

        // Project point onto the line (p1, p2)
        double t = ((point.getX() - p1.getX()) * (p2.getX() - p1.getX()) +
                (point.getY() - p1.getY()) * (p2.getY() - p1.getY())) / segmentLengthSquared;

        t = Math.max(0, Math.min(1, t));  // Clamp t to the segment bounds [0, 1]

        // Calculate the closest point on the segment
        double closestX = p1.getX() + t * (p2.getX() - p1.getX());
        double closestY = p1.getY() + t * (p2.getY() - p1.getY());

        return point.distance(new Point3D(closestX, closestY, 0.0));
    }


    public static void main(String[] args) {
        List<Point3D> polygon = List.of(
                new Point3D(1., 1.,0.0),
                new Point3D(4., 1.,0.0),
                new Point3D(4., 4.,0.0),
                new Point3D(1., 4.,0.0)
        );

        Point3D point = new Point3D(2.0, 0.0,0.0);

        double distance = distanceToPolygon(point, polygon);

        System.out.println("Distance: " + distance); // Expected: 1.0
    }
}