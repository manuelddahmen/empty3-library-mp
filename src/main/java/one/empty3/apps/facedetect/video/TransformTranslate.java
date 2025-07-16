package one.empty3.apps.facedetect.video;

public class TransformTranslate extends Transform {
    private TargetType target;
    private String targetId;
        private int dx;
        private double dy;

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public double getDy() {
        return dy;
    }

    public void setDy(double dy) {
        this.dy = dy;
    }

    public TargetType getTargetType() {
        return target;
    }

    public void setTarget(TargetType target) {
        this.target = target;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}
