package one.empty3.apps.facedetect.video;

public class TransformRotate extends Transform {
    private TargetType targetType;
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

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTarget(Transform.TargetType targetType) {
        this.targetType = targeType;
    }
}
