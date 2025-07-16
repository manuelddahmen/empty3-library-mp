package one.empty3.apps.facedetect.video;

import java.util.List;

public class Group {
    private String id;
    private List<Point> pointsId;
    private String imageUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Point> getPointsId() {
        return pointsId;
    }

    public void setPointsId(List<Point> pointsId) {
        this.pointsId = pointsId;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
