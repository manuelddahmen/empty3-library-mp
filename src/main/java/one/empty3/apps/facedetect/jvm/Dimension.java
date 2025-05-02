package one.empty3.apps.facedetect.jvm;

import one.empty3.library.Point2D;

public class Dimension extends Point2D {

    public Dimension(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public double getWidth() {
        return x;
    }

    public double getHeight() {
        return y;
    }
    public void setWidth(int x) {
        this.x = x;
    }
    public void setHeight(int y) {
        this.y = y;
    }
}
