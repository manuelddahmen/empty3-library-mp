package one.empty3.apps.facedetect.video;

public class Transform {
    private int frames ;
    public enum Types { ATTACH_IMAGE, DETACH_IMAGE, TRANSLATE, ROTATE, VISIBLE, INVISIBLE};
    private String id;
    private Types type;

    public Types getType() {
        return type;
    }

    public void setType(Types type) {
        this.type = type;
    }

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
