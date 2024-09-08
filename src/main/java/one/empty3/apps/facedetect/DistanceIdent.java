package one.empty3.apps.facedetect;

import one.empty3.library.Point3D;

import java.awt.geom.Dimension2D;
import java.util.List;

public class DistanceIdent extends DistanceAB {


    @Override
    public Point3D findAxPointInB(double u, double v) {
        return new Point3D(u, v, 0.0);
    }
}
