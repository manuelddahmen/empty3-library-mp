package one.empty3.apps.facedetect.video;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.UUID;

/**
 * Fonction HTTP qui utilise MovieGenerator pour créer un fichier MPEG à partir
 * d'un fichier texte et de deux images.
 */
public class MovieGeneratorHttpFunction implements HttpFunction {
    private static final Logger logger = Logger.getLogger(MovieGeneratorHttpFunction.class.getName());

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        // Vérifier la méthode HTTP
        if (!"POST".equals(request.getMethod())) {
            response.setStatusCode(405);
            try (BufferedWriter writer = response.getWriter()) {
                writer.write("Méthode non autorisée. Veuillez utiliser POST.");
            }
            return;
        }

        // Créer un répertoire temporaire pour les fichiers
        Path tempDir = Files.createTempDirectory("movie-generator-");
        File textFile = null;
        File image1 = null;
        File image2 = null;
        File outputFile = null;

        try {
            // Récupérer les fichiers depuis la requête multipart
            Map<String, HttpRequest.HttpPart> parts = request.getParts();

            // Recherche du fichier texte
            HttpRequest.HttpPart textPart = parts.get("text");
            if (textPart != null && textPart.getFileName().isPresent()) {
                textFile = new File(tempDir.toFile(), textPart.getFileName().get());
                try (var outputStream = Files.newOutputStream(textFile.toPath())) {
                    textPart.getInputStream().transferTo(outputStream);
                }
                logger.info("Fichier texte reçu : " + textFile.getName());
            }

            // Recherche de la première image
            HttpRequest.HttpPart image1Part = parts.get("image1");
            if (image1Part != null && image1Part.getFileName().isPresent()) {
                image1 = new File(tempDir.toFile(), image1Part.getFileName().get());
                try (var outputStream = Files.newOutputStream(image1.toPath())) {
                    image1Part.getInputStream().transferTo(outputStream);
                }
                logger.info("Image 1 reçue : " + image1.getName());
            }

            // Recherche de la deuxième image
            HttpRequest.HttpPart image2Part = parts.get("image2");
            if (image2Part != null && image2Part.getFileName().isPresent()) {
                image2 = new File(tempDir.toFile(), image2Part.getFileName().get());
                try (var outputStream = Files.newOutputStream(image2.toPath())) {
                    image2Part.getInputStream().transferTo(outputStream);
                }
                logger.info("Image 2 reçue : " + image2.getName());
            }

            // Vérifier que tous les fichiers requis sont présents
            if (textFile == null || image1 == null || image2 == null) {
                response.setStatusCode(400);
                try (BufferedWriter writer = response.getWriter()) {
                    writer.write("Tous les fichiers requis n'ont pas été fournis. " +
                                 "Veuillez fournir un fichier texte (text) et deux images (image1, image2).");
                }
                return;
            }

            // Créer le fichier de sortie
            String outputFileName = UUID.randomUUID().toString() + ".mpeg";
            outputFile = new File(tempDir.toFile(), outputFileName);

            // Utiliser MovieGenerator pour générer le film
            MovieGenerator generator = new MovieGenerator(textFile, image1, image2, outputFile);
            File generatedFile = generator.generateMovie();

            // Définir les en-têtes pour le téléchargement
            response.setContentType("video/mpeg");
            response.getHeaders().set("Content-Disposition", "attachment; filename=\"generated-movie.mpeg\"");

            // Envoyer le fichier généré
            Files.copy(generatedFile.toPath(), response.getOutputStream());

            logger.info("Film envoyé au client avec succès");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la génération du film", e);
            response.setStatusCode(500);
            try (BufferedWriter writer = response.getWriter()) {
                writer.write("Erreur lors de la génération du film : " + e.getMessage());
            }
        } finally {
            // Nettoyer les fichiers temporaires
            cleanupTempFiles(textFile, image1, image2, outputFile, tempDir);
        }
    }

    /**
     * Nettoie les fichiers temporaires créés pendant le traitement
     */
    private void cleanupTempFiles(File textFile, File image1, File image2, File outputFile, Path tempDir) {
        try {
            if (textFile != null && textFile.exists()) textFile.delete();
            if (image1 != null && image1.exists()) image1.delete();
            if (image2 != null && image2.exists()) image2.delete();
            if (outputFile != null && outputFile.exists()) outputFile.delete();

            // Supprimer le répertoire temporaire
            Files.deleteIfExists(tempDir);

            logger.info("Nettoyage des fichiers temporaires effectué");
        } catch (Exception e) {
            logger.log(Level.WARNING, "Erreur lors du nettoyage des fichiers temporaires", e);
        }
    }
}
