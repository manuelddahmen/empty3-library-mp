package one.empty3.apps.facedetect.video;

import com.google.gson.*;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Désérialiseur personnalisé pour les listes d'Animation
 * Utile si vous avez des besoins spécifiques de validation ou transformation
 */
class AnimationListDeserializer implements JsonDeserializer<List<Frame>> {
    private transient Logger logger = Logger.getLogger(AnimationListDeserializer.class.getName());

    @Override
    public List<Frame> deserialize(JsonElement json, Type typeOfT,
                                   JsonDeserializationContext context) throws JsonParseException {
        
        if (!json.isJsonArray()) {
            throw new JsonParseException("Expected JsonArray for animation field");
        }

        JsonArray jsonArray = json.getAsJsonArray();
        List<Frame> animations = new ArrayList<>();

        for (int i = 0; i < jsonArray.size(); i++) {
            JsonElement element = jsonArray.get(i);
            
            try {
                Frame animation = context.deserialize(element, Frame.class);
                
                // Validation post-désérialisation
                if (animation != null) {
                    if (animation.getPoints() == null) {
                        animation.setPoints(new ArrayList<>());
                        logger.warning(String.format("Frame %d: points null, initialisé avec liste vide", i));
                    }
                    if (animation.getGroups() == null) {
                        animation.setGroups(new ArrayList<>());
                        logger.warning(String.format("Frame %d: groups null, initialisé avec liste vide", i));
                    }
                    
                    animations.add(animation);
                    logger.fine(String.format("Frame %d désérialisée: %d points, %d groupes", 
                        i, animation.getPoints().size(), animation.getGroups().size()));
                } else {
                    logger.warning(String.format("Frame %d: Animation null après désérialisation", i));
                }
                
            } catch (Exception e) {
                logger.severe(String.format("Erreur lors de la désérialisation de la frame %d: %s", i, e.getMessage()));
                throw new JsonParseException("Erreur frame " + i, e);
            }
        }

        logger.info(String.format("Désérialisation complète: %d frames d'animation", animations.size()));
        return animations;
    }
}

class ConfigurationJson {
    private List<Point> points = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();
    private List<Transform> transforms = new ArrayList<>();
    private List<Frame> animation = new ArrayList<>();

    /**
     * Instance Gson configurée pour éviter les problèmes de réflexion avec les modules Java
     */
    private static final Gson GSON = new GsonBuilder()
            // Adaptateurs personnalisés
            .registerTypeAdapter(Color.class, new ColorTypeAdapter())
            .registerTypeAdapter(Transform.class, new TransformDeserializer())

            // Exclusion des champs problématiques
            .excludeFieldsWithModifiers(
                    java.lang.reflect.Modifier.TRANSIENT,
                    java.lang.reflect.Modifier.STATIC
            )
            //.registerTypeAdapter(
            //        new TypeToken<List<Animation>>(){}.getType(),
           //         new AnimationListDeserializer()
           // )

            // Stratégie d'exclusion pour éviter les problèmes de Logger
            .setExclusionStrategies(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    // Exclure tous les champs Logger
                    return f.getDeclaredClass() == Logger.class ||
                            f.getName().toLowerCase().contains("logger");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    // Exclure la classe Logger complètement
                    return clazz == Logger.class;
                }
            })

            // Configuration de parsing
            .setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            .setLenient()
            .setPrettyPrinting()
            .create();

    private transient Logger logger = Logger.getLogger(ConfigurationJson.class.getName());

    // Getters and setters...

    public List<Frame> getAnimation() {
        return animation;
    }

    public void setAnimation(List<Frame> animation) {
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
     * Parse une chaîne JSON et crée un objet ConfigurationJson avec gestion d'erreur robuste.
     */
    public static ConfigurationJson parseJson(String jsonString) {
        try {
            if (jsonString == null || jsonString.trim().isEmpty()) {
                throw new IllegalArgumentException("JSON string cannot be null or empty");
            }

            ConfigurationJson result = GSON.fromJson(jsonString, ConfigurationJson.class);

            // Réinitialiser le logger après désérialisation si nécessaire
            if (result != null && result.logger == null) {
                result.logger = Logger.getLogger(ConfigurationJson.class.getName());
            }

            return result;

        } catch (JsonSyntaxException e) {
            Logger.getLogger(ConfigurationJson.class.getName())
                    .severe("Erreur de syntaxe JSON: " + e.getMessage());
            throw new RuntimeException("Invalid JSON syntax", e);
        } catch (Exception e) {
            Logger.getLogger(ConfigurationJson.class.getName())
                    .severe("Erreur lors du parsing JSON: " + e.getMessage());
            throw new RuntimeException("JSON parsing failed", e);
        }
    }

    /**
     * Parse un fichier JSON et crée un objet ConfigurationJson avec gestion d'erreur robuste.
     */
    public static ConfigurationJson parseJson(File jsonFile) {
        if (jsonFile == null || !jsonFile.exists()) {
            throw new IllegalArgumentException("JSON file must exist: " +
                    (jsonFile != null ? jsonFile.getAbsolutePath() : "null"));
        }

        try (FileReader reader = new FileReader(jsonFile, StandardCharsets.UTF_8)) {
            ConfigurationJson result = GSON.fromJson(reader, ConfigurationJson.class);

            // Réinitialiser le logger après désérialisation si nécessaire
            if (result != null && result.logger == null) {
                result.logger = Logger.getLogger(ConfigurationJson.class.getName());
            }

            return result;

        } catch (JsonSyntaxException e) {
            Logger.getLogger(ConfigurationJson.class.getName())
                    .severe("Erreur de syntaxe dans le fichier JSON " + jsonFile.getAbsolutePath() + ": " + e.getMessage());
            throw new RuntimeException("Invalid JSON syntax in file: " + jsonFile.getAbsolutePath(), e);
        } catch (IOException e) {
            Logger.getLogger(ConfigurationJson.class.getName())
                    .severe("Erreur E/S lors de la lecture du fichier JSON " + jsonFile.getAbsolutePath() + ": " + e.getMessage());
            throw new RuntimeException("IO error reading JSON file: " + jsonFile.getAbsolutePath(), e);
        } catch (Exception e) {
            Logger.getLogger(ConfigurationJson.class.getName())
                    .severe("Erreur inattendue lors du parsing du fichier JSON " + jsonFile.getAbsolutePath() + ": " + e.getMessage());
            throw new RuntimeException("Unexpected error parsing JSON file: " + jsonFile.getAbsolutePath(), e);
        }
    }

    /**
     * Récupère une frame d'animation spécifique avec validation
     */
    public Frame getAnimationFrame(int frameIndex) {
        if (animation == null || frameIndex < 0 || frameIndex >= animation.size()) {
            logger.warning(String.format("Frame d'animation %d non trouvée (total: %d)",
                    frameIndex, animation != null ? animation.size() : 0));
            return null;
        }
        return animation.get(frameIndex);
    }

    /**
     * Récupère les points d'une frame spécifique
     */
    public List<Point> getAnimationPoints(int frameIndex) {
        Frame frame = getAnimationFrame(frameIndex);
        return frame != null ? frame.getPoints() : new ArrayList<>();
    }

    /**
     * Récupère les groupes d'une frame spécifique
     */
    public List<Group> getAnimationGroups(int frameIndex) {
        Frame frame = getAnimationFrame(frameIndex);
        return frame != null ? frame.getGroups() : new ArrayList<>();
    }

    /**
     * Nombre total de frames d'animation
     */
    public int getAnimationFrameCount() {
        return animation != null ? animation.size() : 0;
    }

    /**
     * Valide toutes les frames d'animation
     */
    public boolean validateAnimationFrames() {
        if (animation == null || animation.isEmpty()) {
            logger.warning("Aucune frame d'animation définie");
            return false;
        }

        boolean allValid = true;
        for (int i = 0; i < animation.size(); i++) {
            Frame frame = animation.get(i);
            if (frame == null) {
                logger.severe(String.format("Frame %d est null", i));
                allValid = false;
                continue;
            }

            if (!frame.isConsistent()) {
                logger.severe(String.format("Frame %d: incohérence entre points et groupes", i));
                allValid = false;
            }

            // Vérifications additionnelles
            if (frame.getPoints() == null || frame.getPoints().isEmpty()) {
                logger.warning(String.format("Frame %d: aucun point défini", i));
            }

            if (frame.getGroups() == null || frame.getGroups().isEmpty()) {
                logger.warning(String.format("Frame %d: aucun groupe défini", i));
            }
        }

        logger.info(String.format("Validation des frames d'animation: %s (%d frames)",
                allValid ? "SUCCÈS" : "ÉCHEC", animation.size()));
        return allValid;
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
        System.out.println(configurationJson.toJson());
    }

    public void updatePoints(List<Point> groupPoints) {
        getPoints().replaceAll(point -> {
            if (point != null && groupPoints != null) {
                for (Point groupPoint : groupPoints) {
                    if (groupPoint!=null && point.getId().equals(groupPoint.getId())) {
                        point.setName(groupPoint.getName());
                        point.setColor(groupPoint.getColor());
                        point.setVisible(groupPoint.isVisible());
                        point.setImageUrl(groupPoint.getImageUrl());
                        return point;
                    }
                }
            }
            return null;
        });
    }
}

class Frame {
    private List<Point> points = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();

    // Constructeurs
    public Frame() {}

    public Frame(List<Point> points, List<Group> groups) {
        this.points = points != null ? new ArrayList<>(points) : new ArrayList<>();
        this.groups = groups != null ? new ArrayList<>(groups) : new ArrayList<>();
    }

    // Getters et setters
    public List<Point> getPoints() {
        return points;
    }

    public void setPoints(List<Point> points) {
        this.points = points != null ? points : new ArrayList<>();
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups != null ? groups : new ArrayList<>();
    }

    /**
     * Trouve un point par son ID dans cette frame
     */
    public Point findPointById(String id) {
        if (id == null || points == null) return null;
        return points.stream()
                .filter(p -> id.equals(p.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Trouve un groupe par son ID dans cette frame
     */
    public Group findGroupById(String id) {
        if (id == null || groups == null) return null;
        return groups.stream()
                .filter(g -> id.equals(g.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Vérifie la cohérence entre points et groupes de cette frame
     */
    public boolean isConsistent() {
        if (points == null || groups == null) return false;

        Set<String> pointIds = points.stream()
                .map(Point::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return groups.stream()
                .filter(g -> g.getPointIds() != null)
                .flatMap(g -> g.getPointIds().stream())
                .allMatch(pointIds::contains);
    }

    @Override
    public String toString() {
        return String.format("Animation{points=%d, groups=%d}",
                points != null ? points.size() : 0,
                groups != null ? groups.size() : 0);
    }
}


// Classes de base nécessaires (à adapter selon vos besoins)
class Point {
    private double x, y;
    private String name;
    private String id;
    private Color color;
    private boolean visible;
    private String imageUrl; // Ajouté pour supporter les images dans les points d'animation

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "Point{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", visible=" + visible +
                '}';
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
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<String> getPointIds() { return pointIds; }
    public void setPointIds(List<String> pointIds) { this.pointIds = pointIds; }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", name='" + name + '\''
                ;
    }
}

abstract class Transform {
    private String type;
    private boolean visible;
    private String targetId;
    private TargetType targetType;
    protected int frames ;

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