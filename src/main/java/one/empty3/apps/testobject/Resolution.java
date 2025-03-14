package one.empty3.apps.testobject;

public class Resolution {
    public static final Resolution HD1080RESOLUTION = new Resolution(1920, 1080);
    public static final Resolution HD1080  = new Resolution(1920, 1080);
    private final int y;
    private final int x;

    public Resolution(int i, int i1) {
        this.x = i;
        this.y = i1;
    }

    public int y() {
        return y;
    }
    public int x() {
        return x;
    }


}
