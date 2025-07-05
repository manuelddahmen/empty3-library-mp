package one.empty3.apps.facedetect.video;

import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Fonction HTTP qui utilise MovieGenerator pour créer un fichier MPEG à partir
 * d'un fichier texte et de deux images.
 */
public class MovieGeneratorHttpFunction implements HttpFunction {
    private static final Logger logger = Logger.getLogger(MovieGeneratorHttpFunction.class.getName());
    private static final String TEXT_PART_NAME = "text";
    private static final String IMAGE1_PART_NAME = "image1";
    private static final String IMAGE2_PART_NAME = "image2";
    private static final String CONTENT_TYPE_MPEG = "video/mpeg";
    private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    private static final String CONTENT_DISPOSITION_VALUE = "attachment; filename=\"generated-movie.mpeg\"";
    private static final String ALLOWED_ORIGIN = "https://studio--studio-6v2lo.us-central1.hosted.app/";

    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
            String origin = request.getFirstHeader("Origin").orElse(null);

            if (ALLOWED_ORIGIN.equals(origin)) {
                response.appendHeader("Access-Control-Allow-Origin", origin);
            } else {
                // Optionally, you can return an error or a default response for disallowed origins
                response.setStatusCode(403); // Forbidden
                response.getWriter().write("Access denied");
                return;
            }
        // Vérifier la méthode HTTP
        if (!"POST".equals(request.getMethod())) {
            sendErrorResponse(response, 405, "Méthode non autorisée. Veuillez utiliser POST.");
            return;
        }

        logRequestDetails(request);

        Path tempDir = null;
        File textFile = null;
        File image1 = null;
        File image2 = null;
        File outputFile = null;

        try {
            tempDir = Files.createTempDirectory("movie-generator-");
            Map<String, HttpRequest.HttpPart> parts = request.getParts();

            textFile = savePartToFile((HttpRequest) parts.get(TEXT_PART_NAME), tempDir);
            image1 = savePartToFile((HttpRequest) parts.get(IMAGE1_PART_NAME), tempDir);
            image2 = savePartToFile((HttpRequest) parts.get(IMAGE2_PART_NAME), tempDir);

            if (textFile == null || image1 == null || image2 == null) {
                sendErrorResponse(response, 400, "Tous les fichiers requis n'ont pas été fournis. " +
                        "Veuillez fournir un fichier texte (text) et deux images (image1, image2).");
                return;
            }

            String outputFileName = UUID.randomUUID() + ".mpeg";
            outputFile = new File(tempDir.toFile(), outputFileName);

            MovieGenerator generator = new MovieGenerator(textFile, image1, image2, outputFile);
            File generatedFile = generator.generateMovie();

            response.setContentType(CONTENT_TYPE_MPEG);
            response.getHeaders().put(CONTENT_DISPOSITION_HEADER, Collections.singletonList(CONTENT_DISPOSITION_VALUE));

            Files.copy(generatedFile.toPath(), response.getOutputStream());
            logger.info("Film envoyé au client avec succès");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la génération du film", e);
            sendErrorResponse(response, 500, "Erreur lors de la génération du film : " + e.getMessage());
        } finally {
            cleanupFiles(tempDir, textFile, image1, image2, outputFile);
        }
    }

    /**
     * Enregistre une partie de la requête dans un fichier
     */
    private File savePartToFile(HttpRequest part, Path tempDir) throws IOException {
        if (part == null || part.getParts().isEmpty() || part.getParts().get(0).getFileName().isEmpty()) {
            return null;
        }
        File file = new File(tempDir.toFile(), part.getParts().get(0).getFileName().get());
        try (var outputStream = Files.newOutputStream(file.toPath())) {
            part.getInputStream().transferTo(outputStream);
        }
        logger.info("Fichier reçu : " + file.getName());
        return file;
    }

    /**
     * Envoie une réponse d'erreur au client
     */
    private void sendErrorResponse(HttpResponse response, int statusCode, String message) throws IOException {
        response.setStatusCode(statusCode);
        try (BufferedWriter writer = response.getWriter()) {
            writer.write(message);
        }
    }

    /**
     * Journalise tous les détails de la requête HTTP
     */
    private void logRequestDetails(HttpRequest request) {
        try {
            StringBuilder logMessage = new StringBuilder("\n======== DÉTAILS DE LA REQUÊTE HTTP ========\n");

            // Méthode HTTP
            logMessage.append("Méthode: ").append(request.getMethod()).append("\n");

            // URI de la requête
            logMessage.append("URI: ").append(request.getUri()).append("\n");

            // En-têtes
            logMessage.append("\n-- EN-TÊTES --\n");
            request.getHeaders().forEach((name, values) -> {
                logMessage.append(name).append(": ");
                logMessage.append(String.join(", ", values)).append("\n");
            });

            // Paramètres de requête
            logMessage.append("\n-- PARAMÈTRES DE REQUÊTE --\n");
            request.getQueryParameters().forEach((name, values) -> {
                logMessage.append(name).append(": ");
                logMessage.append(String.join(", ", values)).append("\n");
            });

            // Détails des parties (fichiers et champs de formulaire)
            logMessage.append("\n-- PARTIES MULTIPART --\n");
            Map<String, HttpRequest.HttpPart> parts = request.getParts();
            if (parts.isEmpty()) {
                logMessage.append("Aucune partie multipart trouvée\n");
            } else {
                parts.forEach((name, part) -> {
                    logMessage.append("Partie: ").append(name).append("\n");
                    part.getFileName().ifPresent(fileName ->
                        logMessage.append("  Nom du fichier: ").append(fileName).append("\n"));
                    part.getContentType().ifPresent(contentType ->
                        logMessage.append("  Type de contenu: ").append(contentType).append("\n"));
                });
            }

            logMessage.append("\n=======================================\n");

            // Journaliser le message complet
            logger.info(logMessage.toString());

        } catch (Exception e) {
            logger.log(Level.WARNING, "Erreur lors de la journalisation des détails de la requête", e);
        }
    }

    /**
     * Nettoie les fichiers temporaires créés pendant le traitement
     */
    private void cleanupFiles(Path tempDir, File... files) {
        for (File file : files) {
            if (file != null && file.exists()) {
                if (file.delete()) {
                    logger.fine("Fichier supprimé : " + file.getName());
                } else {
                    logger.warning("Impossible de supprimer le fichier : " + file.getName());
                }
            }
        }

        if (tempDir != null) {
            try {
                Files.deleteIfExists(tempDir);
                logger.info("Nettoyage des fichiers temporaires effectué");
            } catch (IOException e) {
                logger.log(Level.WARNING, "Erreur lors du nettoyage du répertoire temporaire", e);
            }
        }
    }
}