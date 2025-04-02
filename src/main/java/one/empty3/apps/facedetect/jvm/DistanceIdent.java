package one.empty3.apps.facedetect.jvm;

import one.empty3.library.Point3D;

public class DistanceIdent extends DistanceAB {

    public DistanceIdent() {
        super();
        if(finishInitListener!=null)
            finishInitListener.fire();
    }
    @Override
    public Point3D findAxPointInB(double u, double v) {
        return new Point3D(u, v, 0.0);
    }
}
