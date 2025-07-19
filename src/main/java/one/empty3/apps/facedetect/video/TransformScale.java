package one.empty3.apps.facedetect.video;

public class TransformScale  extends Transform {
    private TargetType targetType;
    private double cx;
    private double cy;
    private double factor;

    public double getCx() {
        return cx;
    }

    public void setCx(double cx) {
        this.cx = cx;
    }

    public double getCy() {
        return cy;
    }

    public void setCy(double cy) {
        this.cy = cy;
    }

    public double getFactor() {
        return factor;
    }

    public void setFactor(double factor) {
        this.factor = factor;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }
}
