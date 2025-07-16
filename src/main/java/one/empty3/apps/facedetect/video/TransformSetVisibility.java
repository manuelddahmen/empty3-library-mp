package one.empty3.apps.facedetect.video;

import one.empty3.libs.Image;

public class TransformSetVisibility extends Transform{
    private TargetType targetType;
    private String targetId;
    private boolean visible;

    public boolean isVisibility() {
        return visible;
    }

    public void setVisibility(boolean visibility) {
        this.visible = visibility;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public TargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TargetType targetType) {
        this.targetType = targetType;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
