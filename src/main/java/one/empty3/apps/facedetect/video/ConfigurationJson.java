package one.empty3.apps.facedetect.video;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.*;//ERROR
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import java.io.*;
import java.util.List;

/**
 * Classe utilitaire pour la gestion de la sérialisation/désérialisation JSON des configurations.
 * Cette classe centralise la création et l'utilisation de Gson pour le traitement des données.
 */
class GsonUtil {

    /**
     * Crée et configure un adaptateur de type pour les classes Transform.
     * L'adaptateur est configuré une seule fois pour optimiser les performances.
     */
    private static final com.google.gson.typeadapters.RuntimeTypeAdapterFactory<Transform> TRANSFORM_ADAPTER =//ERROR
            com.google.gson.typeadapters.RuntimeTypeAdapterFactory.of(Transform.class, "type")//ERROR
                    .registerSubtype(TransformAttachImage.class, "attachImage")
                    .registerSubtype(TransformDetachImage.class, "detachImage")
                    .registerSubtype(TransformTranslate.class, "translate")
                    .registerSubtype(TransformRotate.class, "rotate")
                    .registerSubtype(TransformSetVisibility.class, "setVisibility")
                    .registerSubtype(TransformMorph.class, "morph")
                    .registerSubtype(TransformScale.class, "scale");

    /**
     * Instance Gson préconfigurée pour la désérialisation des objets ConfigurationJson.
     */
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(TRANSFORM_ADAPTER)//ERROR
            .create();

    /**
     * Convertit une chaîne JSON en objet ConfigurationJson.
     *
     * @param json Chaîne JSON à convertir
     * @return L'objet ConfigurationJson désérialisé
     */
    public static ConfigurationJson fromJson(String json) {
        return GSON.fromJson(json, ConfigurationJson.class);
    }

    /**
     * Convertit un objet ConfigurationJson en chaîne JSON.
     *
     * @param config Objet à convertir
     * @return La représentation JSON de l'objet
     */
    public static String toJson(ConfigurationJson config) {
        return GSON.toJson(config);
    }
}

class ConfigurationJson {
    private List<Point> points;
    private List<Group> groups;
    private List<Transform> transforms;
    private List<List<Point>> animation;

    // Getters et setters...

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
     *
     * @param json Chaîne JSON à parser
     * @return L'objet ConfigurationJson créé
     */
    public static ConfigurationJson parseJson(String json) {
        return GsonUtil.fromJson(json);
    }

    /**
     * Parse un fichier JSON et crée un objet ConfigurationJson.
     *
     * @param jsonFile Fichier JSON à parser
     * @return L'objet ConfigurationJson créé, ou null en cas d'erreur
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

    // Reste de la classe...
    public static void main(String[] args) {
        ConfigurationJson configurationJson = ConfigurationJson.parseJson(
                new File("res/animate-json/animation-data.json"));
        System.out.println(configurationJson);
    }
}