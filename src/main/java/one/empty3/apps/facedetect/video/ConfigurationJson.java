package one.empty3.apps.facedetect.video;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe utilitaire pour la gestion de la sérialisation/désérialisation JSON des configurations.
 * Cette classe utilise une approche native Java sans dépendances externes.
 */
class JsonUtil {
    
    /**
     * Parse simple d'un fichier JSON pour extraire les données de base
     */
    public static ConfigurationJson fromJson(String json) {
        ConfigurationJson config = new ConfigurationJson();
        
        // Extraction des points
        config.setPoints(extractPoints(json));
        
        // Extraction des groupes
        config.setGroups(extractGroups(json));
        
        // Extraction des transformations
        config.setTransforms(extractTransforms(json));
        
        // Extraction de l'animation
        config.setAnimation(extractAnimation(json));
        
        return config;
    }
    
    private static List<Point> extractPoints(String json) {
        List<Point> points = new ArrayList<>();
        Pattern pointPattern = Pattern.compile("\"points\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher matcher = pointPattern.matcher(json);
        
        if (matcher.find()) {
            String pointsJson = matcher.group(1);
            // Parsing simplifié - vous devrez adapter selon votre format JSON exact
            Pattern individualPoint = Pattern.compile("\\{[^}]*\\}");
            Matcher pointMatcher = individualPoint.matcher(pointsJson);
            
            while (pointMatcher.find()) {
                String pointData = pointMatcher.group();
                Point point = parsePoint(pointData);
                if (point != null) {
                    points.add(point);
                }
            }
        }
        
        return points;
    }
    
    private static Point parsePoint(String pointJson) {
        // Parsing basique d'un point - adaptez selon votre structure
        Pattern xPattern = Pattern.compile("\"x\"\\s*:\\s*([\\d.-]+)");
        Pattern yPattern = Pattern.compile("\"y\"\\s*:\\s*([\\d.-]+)");
        Pattern idPattern = Pattern.compile("\"id\"\\s*:\\s*\"([^\"]+)\"");
        
        Matcher xMatcher = xPattern.matcher(pointJson);
        Matcher yMatcher = yPattern.matcher(pointJson);
        Matcher idMatcher = idPattern.matcher(pointJson);
        
        if (xMatcher.find() && yMatcher.find()) {
            Point point = new Point();
            point.setX(Double.parseDouble(xMatcher.group(1)));
            point.setY(Double.parseDouble(yMatcher.group(1)));
            
            if (idMatcher.find()) {
                point.setId(idMatcher.group(1));
            }
            
            return point;
        }
        
        return null;
    }

    private static List<Group> extractGroups(String json) {
        List<Group> groups = new ArrayList<>();
        Pattern groupPattern = Pattern.compile("\"groups\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher matcher = groupPattern.matcher(json);

        if (matcher.find()) {
            String groupsJson = matcher.group(1);
            groups.addAll(parseJsonObjects(groupsJson, JsonUtil::parseGroup));
        }

        return groups;
    }


    private static Group parseGroup(String groupJson) {
        Group group = new Group();

        group.setId(extractStringValue(groupJson, "id"));
        group.setVisible(extractBooleanValue(groupJson, "visible", true));
        group.setImageId(extractStringValue(groupJson, "imageId"));

        // Extraction des IDs de points
        List<String> pointIds = extractStringArray(groupJson, "pointIds");
        group.setPointIds(pointIds);

        return group;
    }
    private static List<Transform> extractTransforms(String json) {
        List<Transform> transforms = new ArrayList<>();
        Pattern transformPattern = Pattern.compile("\"transforms\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher matcher = transformPattern.matcher(json);

        if (matcher.find()) {
            String transformsJson = matcher.group(1);
            transforms.addAll(parseJsonObjects(transformsJson, JsonUtil::parseTransform));
        }

        return transforms;
    }

    private static Transform parseTransform(String transformJson) {
        String type = extractStringValue(transformJson, "type");
        if (type == null) return null;

        Transform transform = null;

        switch (type.toLowerCase()) {
            case "attachimage":
            case "attach_image":
                transform = parseTransformAttachImage(transformJson);
                break;
            case "detachimage":
            case "detach_image":
                transform = parseTransformDetachImage(transformJson);
                break;
            case "translate":
                transform = parseTransformTranslate(transformJson);
                break;
            case "rotate":
                transform = parseTransformRotate(transformJson);
                break;
            case "setvisibility":
            case "visibility":
                transform = parseTransformSetVisibility(transformJson);
                break;
            case "morph":
                transform = parseTransformMorph(transformJson);
                break;
            case "scale":
                transform = parseTransformScale(transformJson);
                break;
            default:
                System.err.println("Type de transformation non reconnu: " + type);
                return null;
        }

        if (transform != null) {
            // Propriétés communes à toutes les transformations
            transform.setType(type);
            transform.setVisible(extractBooleanValue(transformJson, "visible", true));
            transform.setFrames((int) extractDoubleValue(transformJson, "frames"));

            String targetType = extractStringValue(transformJson, "targetType");
            if (targetType != null) {
                try {
                    transform.setTargetType(Transform.TargetType.valueOf(targetType.toUpperCase()));
                } catch (IllegalArgumentException e) {
                    transform.setTargetType(Transform.TargetType.Group);
                }
            }
        }

        return transform;
    }

    /**
     * Convertit un objet ConfigurationJson en chaîne JSON.
     */
    public static String toJson(ConfigurationJson config) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");

        boolean first = true;

        // Sérialisation des points
        if (config.getPoints() != null && !config.getPoints().isEmpty()) {
            if (!first) json.append(",\n");
            json.append("  \"points\": [\n");
            for (int i = 0; i < config.getPoints().size(); i++) {
                Point point = config.getPoints().get(i);
                json.append("    {\n");
                json.append("      \"x\": ").append(point.getX()).append(",\n");
                json.append("      \"y\": ").append(point.getY()).append(",\n");
                json.append("      \"visible\": ").append(point.isVisible());
                if (point.getId() != null) {
                    json.append(",\n      \"id\": \"").append(point.getId()).append("\"");
                }
                if (point.getName() != null) {
                    json.append(",\n      \"name\": \"").append(point.getName()).append("\"");
                }
                json.append("\n    }");
                if (i < config.getPoints().size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            json.append("  ]");
            first = false;
        }

        // Sérialisation des groupes
        if (config.getGroups() != null && !config.getGroups().isEmpty()) {
            if (!first) json.append(",\n");
            json.append("  \"groups\": [\n");
            for (int i = 0; i < config.getGroups().size(); i++) {
                Group group = config.getGroups().get(i);
                json.append("    {\n");
                json.append("      \"id\": \"").append(group.getId()).append("\",\n");
                json.append("      \"visible\": ").append(group.isVisible());
                if (group.getImageId() != null) {
                    json.append(",\n      \"imageId\": \"").append(group.getImageId()).append("\"");
                }
                if (group.getPointIds() != null && !group.getPointIds().isEmpty()) {
                    json.append(",\n      \"pointIds\": [");
                    for (int j = 0; j < group.getPointIds().size(); j++) {
                        json.append("\"").append(group.getPointIds().get(j)).append("\"");
                        if (j < group.getPointIds().size() - 1) {
                            json.append(", ");
                        }
                    }
                    json.append("]");
                }
                json.append("\n    }");
                if (i < config.getGroups().size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            json.append("  ]");
            first = false;
        }

        // Sérialisation des transformations
        if (config.getTransforms() != null && !config.getTransforms().isEmpty()) {
            if (!first) json.append(",\n");
            json.append("  \"transforms\": [\n");
            for (int i = 0; i < config.getTransforms().size(); i++) {
                Transform transform = config.getTransforms().get(i);
                json.append("    {\n");
                json.append("      \"type\": \"").append(transform.getType()).append("\",\n");
                json.append("      \"visible\": ").append(transform.isVisible()).append(",\n");
                json.append("      \"frames\": ").append(transform.getFrames());

                // Propriétés spécifiques selon le type
                if (transform instanceof TransformAttachImage) {
                    TransformAttachImage tai = (TransformAttachImage) transform;
                    if (tai.getImageUrl() != null) {
                        json.append(",\n      \"imageUrl\": \"").append(tai.getImageUrl()).append("\"");
                    }
                    if (tai.getGroupId() != null) {
                        json.append(",\n      \"groupId\": \"").append(tai.getGroupId()).append("\"");
                    }
                } else if (transform instanceof TransformTranslate) {
                    TransformTranslate tt = (TransformTranslate) transform;
                    json.append(",\n      \"dx\": ").append(tt.getDx());
                    json.append(",\n      \"dy\": ").append(tt.getDy());
                } else if (transform instanceof TransformRotate) {
                    TransformRotate tr = (TransformRotate) transform;
                    json.append(",\n      \"angle\": ").append(tr.getAngle());
                    json.append(",\n      \"cx\": ").append(tr.getCx());
                    json.append(",\n      \"cy\": ").append(tr.getCy());
                } else if (transform instanceof TransformScale) {
                    TransformScale ts = (TransformScale) transform;
                    json.append(",\n      \"scaleX\": ").append(ts.getScaleX());
                    json.append(",\n      \"scaleY\": ").append(ts.getScaleY());
                    json.append(",\n      \"cx\": ").append(ts.getCx());
                    json.append(",\n      \"cy\": ").append(ts.getCy());
                }
                // Ajoutez d'autres types selon vos besoins

                json.append("\n    }");
                if (i < config.getTransforms().size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            json.append("  ]");
            first = false;
        }

        // Sérialisation de l'animation
        if (config.getAnimation() != null && !config.getAnimation().isEmpty()) {
            if (!first) json.append(",\n");
            json.append("  \"animation\": [\n");
            for (int i = 0; i < config.getAnimation().size(); i++) {
                List<Point> frame = config.getAnimation().get(i);
                json.append("    [\n");
                for (int j = 0; j < frame.size(); j++) {
                    Point point = frame.get(j);
                    json.append("      {\"x\": ").append(point.getX())
                            .append(", \"y\": ").append(point.getY()).append("}");
                    if (j < frame.size() - 1) {
                        json.append(",");
                    }
                    json.append("\n");
                }
                json.append("    ]");
                if (i < config.getAnimation().size() - 1) {
                    json.append(",");
                }
                json.append("\n");
            }
            json.append("  ]");
        }

        json.append("\n}");
        return json.toString();
    }


    private static TransformAttachImage parseTransformAttachImage(String json) {
        TransformAttachImage transform = new TransformAttachImage();
        transform.setImageUrl(extractStringValue(json, "imageUrl"));
        transform.setGroupId(extractStringValue(json, "groupId"));
        return transform;
    }

    private static TransformDetachImage parseTransformDetachImage(String json) {
        TransformDetachImage transform = new TransformDetachImage();
        transform.setGroupId(extractStringValue(json, "groupId"));
        return transform;
    }

    private static TransformTranslate parseTransformTranslate(String json) {
        TransformTranslate transform = new TransformTranslate();
        transform.setDx(extractDoubleValue(json, "dx"));
        transform.setDy(extractDoubleValue(json, "dy"));
        return transform;
    }

    private static TransformRotate parseTransformRotate(String json) {
        TransformRotate transform = new TransformRotate();
        transform.setAngle(extractDoubleValue(json, "angle"));
        transform.setCx(extractDoubleValue(json, "cx"));
        transform.setCy(extractDoubleValue(json, "cy"));
        return transform;
    }

    private static TransformSetVisibility parseTransformSetVisibility(String json) {
        return new TransformSetVisibility();
    }

    private static TransformMorph parseTransformMorph(String json) {
        TransformMorph transform = new TransformMorph();
        transform.setTargetGroupId(extractStringValue(json, "targetGroupId"));
        transform.setSourceGroupId(extractStringValue(json, "sourceGroupId"));
        transform.setCx(extractDoubleValue(json, "cx"));
        transform.setCy(extractDoubleValue(json, "cy"));
        return transform;
    }

    private static TransformScale parseTransformScale(String json) {
        TransformScale transform = new TransformScale();
        transform.setScaleX(extractDoubleValue(json, "scaleX"));
        transform.setScaleY(extractDoubleValue(json, "scaleY"));
        transform.setCx(extractDoubleValue(json, "cx"));
        transform.setCy(extractDoubleValue(json, "cy"));
        return transform;
    }

    private static List<List<Point>> extractAnimation(String json) {
        List<List<Point>> animation = new ArrayList<>();
        Pattern animationPattern = Pattern.compile("\"animation\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher matcher = animationPattern.matcher(json);

        if (matcher.find()) {
            String animationJson = matcher.group(1);

            // Extraction des frames d'animation (tableaux de points)
            Pattern framePattern = Pattern.compile("\\[(.*?)\\]", Pattern.DOTALL);
            Matcher frameMatcher = framePattern.matcher(animationJson);

            while (frameMatcher.find()) {
                String frameJson = frameMatcher.group(1);
                List<Point> frame = new ArrayList<>();

                // Parse chaque point dans la frame
                frame.addAll(parseJsonObjects(frameJson, JsonUtil::parsePoint));

                if (!frame.isEmpty()) {
                    animation.add(frame);
                }
            }
        }

        return animation;
    }

    // Méthodes utilitaires pour l'extraction de valeurs
    private static String extractStringValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*?)\"");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static double extractDoubleValue(String json, String key) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*([\\d.-]+)");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? Double.parseDouble(matcher.group(1)) : 0.0;
    }

    private static boolean extractBooleanValue(String json, String key, boolean defaultValue) {
        Pattern pattern = Pattern.compile("\"" + key + "\"\\s*:\\s*(true|false)");
        Matcher matcher = pattern.matcher(json);
        return matcher.find() ? Boolean.parseBoolean(matcher.group(1)) : defaultValue;
    }

    private static List<String> extractStringArray(String json, String key) {
        List<String> result = new ArrayList<>();
        Pattern arrayPattern = Pattern.compile("\"" + key + "\"\\s*:\\s*\\[(.*?)\\]", Pattern.DOTALL);
        Matcher arrayMatcher = arrayPattern.matcher(json);

        if (arrayMatcher.find()) {
            String arrayContent = arrayMatcher.group(1);
            Pattern stringPattern = Pattern.compile("\"([^\"]*?)\"");
            Matcher stringMatcher = stringPattern.matcher(arrayContent);

            while (stringMatcher.find()) {
                result.add(stringMatcher.group(1));
            }
        }

        return result;
    }

    private static Color parseColor(String colorStr) {
        try {
            if (colorStr.startsWith("#")) {
                return Color.decode(colorStr);
            } else if (colorStr.startsWith("rgb(")) {
                // Parse format rgb(r,g,b)
                Pattern rgbPattern = Pattern.compile("rgb\\((\\d+),\\s*(\\d+),\\s*(\\d+)\\)");
                Matcher matcher = rgbPattern.matcher(colorStr);
                if (matcher.find()) {
                    int r = Integer.parseInt(matcher.group(1));
                    int g = Integer.parseInt(matcher.group(2));
                    int b = Integer.parseInt(matcher.group(3));
                    return new Color(r, g, b);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du parsing de la couleur: " + colorStr);
        }
        return Color.BLACK; // Couleur par défaut
    }

    // Interface fonctionnelle pour le parsing d'objets
    @FunctionalInterface
    private interface JsonObjectParser<T> {
        T parse(String json);
    }

    // Méthode générique pour parser des objets JSON dans un tableau
    private static <T> List<T> parseJsonObjects(String jsonArray, JsonObjectParser<T> parser) {
        List<T> results = new ArrayList<>();

        // Suppression des espaces et retours à la ligne en début et fin
        jsonArray = jsonArray.trim();

        if (jsonArray.isEmpty()) {
            return results;
        }

        int braceCount = 0;
        int start = -1;

        for (int i = 0; i < jsonArray.length(); i++) {
            char c = jsonArray.charAt(i);

            if (c == '{') {
                if (braceCount == 0) {
                    start = i;
                }
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0 && start != -1) {
                    String objectJson = jsonArray.substring(start, i + 1);
                    T parsed = parser.parse(objectJson);
                    if (parsed != null) {
                        results.add(parsed);
                    }
                    start = -1;
                }
            }
        }

        return results;
    }

}

class ConfigurationJson {
    private List<Point> points = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();
    private List<Transform> transforms = new ArrayList<>();
    private List<List<Point>> animation = new ArrayList<>();

    // Getters et setters
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
    public static ConfigurationJson parseJson(String json) {
        return JsonUtil.fromJson(json);
    }

    /**
     * Parse un fichier JSON et crée un objet ConfigurationJson.
     */
    public static ConfigurationJson parseJson(File jsonFile) {
        try (FileReader reader = new FileReader(jsonFile)) {
            StringBuilder json = new StringBuilder();
            int c;
            while ((c = reader.read()) != -1) {
                json.append((char) c);
            }
            return parseJson(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
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
    private List<String> pointIds = new ArrayList<>();
    private boolean visible;
    private String imageId;
    public boolean isVisible() {
        return visible;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public void setVisible(boolean visible) {
        visible = visible;
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
    private String groupId;
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
}

class TransformDetachImage extends Transform {
    private String groupId;
    
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
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
}

class TransformMorph extends Transform {
    private String targetGroupId;
    private double cx;
    private double cy;

    public String getTargetGroupId() { return targetGroupId; }
    public void setTargetGroupId(String targetGroupId) { this.targetGroupId = targetGroupId; }
    private String sourceGroupId;

    public String getSourceGroupId() { return sourceGroupId; }
    public void setSourceGroupId(String source) { this.sourceGroupId = source; }

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