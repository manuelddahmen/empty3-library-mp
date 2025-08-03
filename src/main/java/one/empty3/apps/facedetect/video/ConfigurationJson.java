package one.empty3.apps.facedetect.video;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ConfigurationJson {
    private List<Point> points = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();
    private List<Transform> transforms = new ArrayList<>();
    private List<List<Point>> animation = new ArrayList<>();

    /**
     * A single, reusable Gson instance configured with all necessary TypeAdapters.
     * This prevents reflection issues with JDK classes like java.awt.Color.
     */
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(Color.class, new ColorTypeAdapter())
            .registerTypeAdapter(Transform.class, new TransformDeserializer())
            .create();

    // Getters and setters...

    public List<List<Point>> getAnimation() {
        return animation;
    }

    public void setAnimation(List<List<Point>> animation) {
        this.animation = animation;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points;
    }

    public List<Transform> getTransforms() {
        return transforms;
    }

    public void setTransforms(List<Transform> transforms) {
        this.transforms = transforms;
    }

    /**
     * Parse une chaîne JSON et crée un objet ConfigurationJson.
     */
    public static ConfigurationJson parseJson(String jsonString) {
        return GSON.fromJson(jsonString, ConfigurationJson.class);
    }


    /**
     * Parse un fichier JSON et crée un objet ConfigurationJson.
     */
    public static ConfigurationJson parseJson(File jsonFile) {
        try (FileReader reader = new FileReader(jsonFile)) {
            return GSON.fromJson(reader, ConfigurationJson.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Converts this object to a JSON string.
     */
    public String toJson() {
        return GSON.toJson(this);
    }


    public static void main(String[] args) {
        ConfigurationJson configurationJson = ConfigurationJson.parseJson(
                new File("res/animate-json/animation-data.json"));
        System.out.println(configurationJson);
    }
}

// Classes de base nécessaires (à adapter selon vos besoins)
class Point {
    private double x, y;
    private String name;
    private String id;
    private Color color;
    private boolean visible;
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

class Group {
    private String id;
    private String name;
    private List<String> pointIds = new ArrayList<>();
    private boolean visible = true;
    public boolean isVisible() {
        return visible;
    }


    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public List<String> getPointIds() { return pointIds; }
    public void setPointIds(List<String> pointIds) { this.pointIds = pointIds; }
}

abstract class Transform {
    private String type;
    private boolean visible;
    private String targetId;
    private TargetType targetType;
    private int frames ;

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public enum  Type{ ATTACH_IMAGE, DETACH_IMAGE, TRANSLATE, ROTATE, VISIBLE, INVISIBLE};
    public enum TargetType { All, Group };
    public TargetType getTargetType() {
        return targetType;
    }
    public void setTargetType(TargetType targetType) {
       this.targetType = targetType;
    }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getFrames() {
        return frames;
    }

    public void setFrames(int frames) {
        this.frames = frames;
    }
}

class TransformAttachImage extends Transform {
    private String imageUrl;

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}

class TransformDetachImage extends Transform {
}

class TransformTranslate extends Transform {
    private double dx, dy;
    
    public double getDx() { return dx; }
    public void setDx(double dx) { this.dx = dx; }
    public double getDy() { return dy; }
    public void setDy(double dy) { this.dy = dy; }
}

class TransformRotate extends Transform {
    private double angle;
    private double cx;
    private double cy;
    public double getAngle() { return angle; }
    public void setAngle(double angle) { this.angle = angle; }

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
}

class TransformSetVisibility extends Transform {
    private boolean visible; // Nouvelle propriété pour définir la visibilité

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}

class TransformMorph extends Transform {
    private String sourceGroupId;
    private String targetGroupId;

    public String getTargetGroupId() { return targetGroupId; }
    public void setTargetGroupId(String targetGroupId) { this.targetGroupId = targetGroupId; }

    public String getSourceGroupId() { return sourceGroupId; }
    public void setSourceGroupId(String source) { this.sourceGroupId = source; }

}

class TransformScale extends Transform {
    private double scaleX, scaleY;
    private double cx, cy;
    public double getScaleX() { return scaleX; }
    public void setScaleX(double scaleX) { this.scaleX = scaleX; }
    public double getScaleY() { return scaleY; }
    public void setScaleY(double scaleY) { this.scaleY = scaleY; }

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
}