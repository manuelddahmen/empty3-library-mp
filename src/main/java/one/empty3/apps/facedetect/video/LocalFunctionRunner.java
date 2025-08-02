package one.empty3.apps.facedetect.video;

import com.google.cloud.functions.invoker.runner.Invoker;

public class LocalFunctionRunner {
    public static void main(String[] args) throws Exception {
        // Démarrer le serveur local sur le port 8080
        Invoker.main(new String[]{
            "--target", "one.empty3.apps.facedetect.video.MovieGeneratorHttpFunction",
            "--port", "8080"
        });
    }
}