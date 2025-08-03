//package one.empty3.apps.facedetect.video;
//
//import com.google.gson.Gson;
//import java.io.File;
//import java.io.FileReader;
//import java.io.IOException;
//
//// ... (keep your data model classes: Point, Group, Transform, etc.)
//
//class ConfigurationJson1 {
//    private List<Point> points = new ArrayList<>();
//    private List<Group> groups = new ArrayList<>();
//    private List<Transform> transforms = new ArrayList<>();
//    private List<List<Point>> animation = new ArrayList<>();
//
//    // Getters and setters...
//
//    /**
//     * Parse une chaîne JSON et crée un objet ConfigurationJson.
//     */
//    public static ConfigurationJson parseJson(String jsonString) {
//        Gson gson = new Gson();
//        // This one line replaces the entire JsonUtil.fromJson() method
//        return gson.fromJson(jsonString, ConfigurationJson.class);
//    }
//
//    /**
//     * Parse un fichier JSON et crée un objet ConfigurationJson.
//     */
//    public static ConfigurationJson parseJson(File jsonFile) {
//        Gson gson = new Gson();
//        try (FileReader reader = new FileReader(jsonFile)) {
//            return gson.fromJson(reader, ConfigurationJson.class);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
//
//    /**
//     * Converts this object to a JSON string.
//     */
//    public String toJson() {
//        Gson gson = new Gson();
//        // This one line replaces the entire JsonUtil.toJson() method
//        return gson.toJson(this);
//    }
//}