package one.empty3.apps.facedetect.video;

import one.empty3.libs.Image;

public class TransformDetachImage extends Transform{
        private TargetType targetType;
        private String targetId;
        public Image fetchImage() {
            return null;
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
}

