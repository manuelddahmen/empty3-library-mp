package one.empty3.apps.facedetect.video;

import com.google.api.client.http.MultipartContent;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import one.empty3.apps.testobject.TestCollection;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.cloud.functions.HttpRequest.*;

/**
 * Fonction HTTP qui utilise MovieGenerator pour créer un fichier MPEG à partir
 * d'un fichier texte et de deux images.
 */
public class MovieGeneratorHttpFunction implements HttpFunction {
    private static final Logger logger = Logger.getLogger(MovieGeneratorHttpFunction.class.getCanonicalName());
    private static final String TEXT_PART_NAME = ".txt";
    private static final String IMAGE1_PART_NAME = ".jpg";
    private static final String IMAGE2_PART_NAME = ".png";
    private static final String JSONFILE_EXT = ".json";
    private static final String CONTENT_TYPE_MPEG = "video/mp4";
    private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    private static final String CONTENT_DISPOSITION_VALUE = "attachment; filename=\"generated-movie.mp4\"";
    private static final String ALLOWED_ORIGIN = "https://us-central1-studio-6v2lo.cloudfunctions.net/motion-weaver-render";
    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
            String origin = request.getFirstHeader("Origin").orElse(null);

            /**if (ALLOWED_ORIGIN.equals(origin)) {
                response.appendHeader("Access-Control-Allow-Origin", origin);
            } else {
                // Optionally, you can return an error or a default response for disallowed origins
                response.setStatusCode(403); // Forbidden
                response.getWriter().write("Access denied\n");
                response.getWriter().write(ALLOWED_ORIGIN+"\n");
                response.getWriter().write(origin+"\n");
                return;
            }*/
        // Vérifier la méthode HTTP
        if (!"POST".equals(request.getMethod())) {
            sendErrorResponse(response, 405, "Méthode non autorisée. Veuillez utiliser POST.");
            return;
        }

        logRequestDetails(request);

        Path tempDir = Files.createTempDirectory("movie-generator-");
        File textFile = null;
        File image1 = null;
        File image2 = null;
        File outputFile = null;

        List<FileType> types = new ArrayList<>();
        try {
            Map<String, HttpPart> parts = request.getParts();

            for (Entry<String, HttpPart> entry : parts.entrySet()) {
                String s = entry.getKey();
                HttpPart httpPart = entry.getValue();
                if(httpPart!=null) {
                    try {
                        boolean added = false;
                        if(httpPart.getFileName()!=null&&httpPart.getFileName().isPresent()) {
                            String s1 = httpPart.getFileName().get();
                            String s2 = null;
                            if (s1 != null) {
                                switch (s1.substring(s1.lastIndexOf('.'))) {
                                    case TEXT_PART_NAME -> {
                                        File f = savePartToFile(httpPart, tempDir);
                                        if(f==null)
                                            break;
                                        s1 = f.getName();
                                        s2 = "txt";
                                        added = true;
                                    }
                                    case IMAGE1_PART_NAME -> {
                                        File f = savePartToFile(httpPart, tempDir);
                                        if(f==null)
                                            break;
                                        s1 = f.getName();
                                        s2 = "jpg";
                                        added = true;
                                    }
                                    case IMAGE2_PART_NAME -> {
                                        File f = savePartToFile(httpPart, tempDir);
                                        if(f==null)
                                            break;
                                        s1 = f.getName();
                                        s2 = "png";
                                        added = true;
                                    }
                                    case JSONFILE_EXT -> {
                                        File f = savePartToFile(httpPart, tempDir);
                                        if(f==null)
                                            break;
                                        s1 = f.getName();
                                        s2 = "json";
                                        added = true;
                                    }
                                }
                                if (added) {
                                    types.add(new FileType(s1, s2));
                                }
                            }
                        }
                    } catch (IOException e) {
                        cleanupFiles(tempDir.toFile());
                        sendErrorResponse(response, 500, "Erreur lors de l'enregistrement du fichier :");
                        response.setStatusCode(500);
                        return;
                    }
                }
            }

            Logger.getLogger(getClass().getCanonicalName()).info("Main code in function");
            for(Entry<String, HttpPart> part : parts.entrySet()) {
                Logger.getLogger(getClass().getCanonicalName()).info(part.getKey());

            }
            Logger.getLogger(getClass().getCanonicalName()).info("Main code in function");

            String outputFileName = UUID.randomUUID() + ".mp4";
            outputFile = new File(tempDir.toString(), outputFileName);

            MovieGenerator generator = null;
            try {
                generator = new MovieGenerator(tempDir.toFile(), types, outputFile);
            } catch (RuntimeException ex) {
                exceptionToString(ex);
                sendErrorResponse(response, 500, "new MovieGenerator(): " + exceptionToString(ex));
                return;
            }
            boolean b = false;
            try {
                if(generator!=null)
                    b = generator.generateMovie();
            } catch (RuntimeException ex) {
                exceptionToString(ex);
                sendErrorResponse(response, 500, "movieGenerator.generateMovie(): " + exceptionToString(ex));
                return;
            } finally {
                cleanupFiles(tempDir.toFile());
            }

            response.setContentType(CONTENT_TYPE_MPEG);
            response.getHeaders().put(CONTENT_DISPOSITION_HEADER, Collections.singletonList(CONTENT_DISPOSITION_VALUE));

            if(outputFile.exists()) {
                Files.copy(outputFile.toPath(), response.getOutputStream());
            } else {
                sendErrorResponse(response, 500, "Erreur lors de la génération du film : le fichier mp4 n'a pas été trouvé");
                return;
            }
            logger.info("Film envoyé au client avec succès");

        } catch (RuntimeException | IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la génération du film", e);
            sendErrorResponse(response, 500, "Erreur lors de la génération du film : " + exceptionToString(e));
        } finally {
            cleanupFiles(tempDir.toFile());
        }
    }

    private String exceptionToString(Exception e) {
        StringBuilder s = new StringBuilder();
        s.append(e.getMessage()).append("\n");
        for (int i = 0; i < e.getStackTrace().length; i++) {
            s.append(e.getStackTrace()[i]).append("\n");
        }
        return s.toString();
    }

    /**
     * Enregistre une partie de la requête dans un fichier
     */
    private File savePartToFile(HttpPart part, Path tempDir) throws IOException {
        if (part == null || part.getFileName().isEmpty() || part.getFileName().isEmpty()) {
            return null;
        }
        String s = part.getFileName().get();
        if(s!=null && s.length()>0) {
            File file = new File(tempDir.toFile(), s);
            if (file != null) {
                try (var outputStream = Files.newOutputStream(file.toPath())) {
                    part.getInputStream().transferTo(outputStream);
                }
                logger.info("Fichiers reçu : " + file.getName());
            }
            return file;
        }
        return null;
    }

    /**
     * Envoie une réponse d'erreur au client
     */
    private void sendErrorResponse(HttpResponse response, int statusCode, String message) throws IOException {
        response.setStatusCode(statusCode);
        try (BufferedWriter writer = response.getWriter()) {
            writer.write(message);
        }
        Logger.getLogger(getClass().getCanonicalName()).severe(message);
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
            Map<String, HttpPart> parts = request.getParts();
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
    private void cleanupFiles(File tempDir) {
        try {
            if (tempDir != null && tempDir.exists()) {
                for (File file : Objects.requireNonNull(tempDir.listFiles())) {
                    if (file != null  && !"..".equals(file.getName()) && !".".equals(file.getName())) {
                        if ((file != null && file.exists()) && (!file.isDirectory() || (file.isDirectory() && Objects.requireNonNull(file.listFiles()).length == 0))) {
                            if (file.delete()) {
                                logger.fine("Fichier supprimé : " + file.getName());
                            }
                            if (file.isDirectory() && file.listFiles() != null && file.listFiles().length > 0) {
                                cleanupFiles(file);
                            }
                            if (file.exists() && file.isDirectory() && file.listFiles() != null && file.listFiles().length == 0)
                                file.delete();
                        }
                    }

                    if (tempDir.exists() && tempDir.isDirectory()) {
                        tempDir.delete();
                        logger.info("Nettoyage des fichiers temporaires effectué");
                    }
                }
            }
        } catch (SecurityException ex) {
            logger.log(Level.WARNING, "Erreur lors du nettoyage des fichiers temporaires", ex);
            ex.printStackTrace();
        }
    }
}