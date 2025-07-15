package one.empty3.apps.facedetect.video;

public class TransformTranslate extends Transform {
        public class Target {
            public enum Type { All, Group };
        }
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
}
