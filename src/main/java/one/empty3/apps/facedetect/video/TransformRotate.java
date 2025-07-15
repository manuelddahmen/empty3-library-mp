package one.empty3.apps.facedetect.video;

public class TransformRotate extends Transform {
    private TransformTranslate.Target target;
    private double cx;
    private double cy;
    private double angle;

    public double getCx() {
        return cx;
    }

    public void setCx(double cx) {
        this.cx = cx;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double cxangle) {
        this.angle = cxangle;
    }

    public TransformTranslate.Target getTarget() {
        return target;
    }

    public void setTarget(TransformTranslate.Target target) {
        this.target = target;
    }
}
