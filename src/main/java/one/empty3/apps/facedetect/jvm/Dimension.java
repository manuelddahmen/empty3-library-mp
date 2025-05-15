package one.empty3.apps.facedetect.jvm;

import one.empty3.library.Point2D;

public class Dimension extends Point2D {

    public Dimension(int width, int height) {
        super(width, height);
    }

    public double getWidth() {
        return getX();
    }

    public double getHeight() {
        return getY();
    }
    
    public void setWidth(int width) {
        setX(width);
    }
    
    public void setHeight(int height) {
        setY(height);
    }
}
