package one.empty3.apps.facedetect.jvm;

public class Dimension2D {
    public double x;
    public double y;

    public Dimension2D(double xMax, double yMax) {
        this.x = xMax;
        this.y = yMax;
    }

    public double getWidth() {
        return x;
    }

    public double getHeight() {
        return y;
    }

    public void setSize(double width, double height) {

    }
}