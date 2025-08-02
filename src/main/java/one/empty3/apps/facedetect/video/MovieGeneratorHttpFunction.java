package one.empty3.apps.facedetect.video;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.MultipartContent;
import com.google.auth.ServiceAccountSigner;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.auth.ServiceAccountSigner;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.cloud.storage.*;
import one.empty3.apps.testobject.TestCollection;
import one.empty3.apps.facedetect.video.ConfigurationJson.*;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static com.google.cloud.functions.HttpRequest.*;
import one.empty3.apps.facedetect.video.ConfigurationJson.*;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;



/**
 * Fonction HTTP qui utilise MovieGenerator pour créer un fichier mp4 à partir
 * d'un fichier texte et de deux images.
 */
public class MovieGeneratorHttpFunction implements HttpFunction {
    private static final String BUCKET_NAME = "gs://studio-6v2lo.firebasestorage.app";
    private static final Logger logger = Logger.getLogger(MovieGeneratorHttpFunction.class.getCanonicalName());
    private static final String TEXT_PART_NAME = ".txt";
    private static final String IMAGE1_PART_NAME = ".jpg";
    private static final String IMAGE2_PART_NAME = ".png";
    private static final String JSONFILE_EXT = ".json";
    private static final String CONTENT_TYPE_mp4 = "video/mp4";
    private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    private static final String CONTENT_DISPOSITION_VALUE = "attachment; filename=\"generated-movie.mp4\"";
    private static final String ALLOWED_ORIGIN = "https://us-central1-studio-6v2lo.cloudfunctions.net/motion-weaver-render";


    private String uploadToCloudStorage(File outputFile, String userId) throws IOException {
        if (outputFile == null || !outputFile.exists() || outputFile.length() == 0) {
            logger.severe("Le fichier à uploader est invalide ou vide");
            throw new IOException("Le fichier vidéo est invalide ou vide");
        }
        if (userId == null || userId.trim().isEmpty()) {
            logger.severe("L'identifiant utilisateur (userId) est manquant pour l'upload.");
            throw new IllegalArgumentException("L'identifiant utilisateur (userId) ne peut pas être vide pour l'upload.");
        }



        try {
            // Initialiser le client Storage
            com.google.cloud.storage.Storage storage = StorageOptions.newBuilder()
                    .setProjectId("studio-6v2lo")
                    .build()
                    .getService();

            // Générer un nom unique pour le fichier dans le dossier de l'utilisateur
            String fileName = "users/" + userId + "/generated_video/" + System.currentTimeMillis() + "-" + outputFile.getName();

            // Créer les informations du blob avec le bon Content-Type
            BlobId blobId = BlobId.of(BUCKET_NAME.replace("gs://", ""), fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType("video/mp4")
                    .build();

            // Upload le fichier
            logger.info("Upload du fichier vers GCS: " + fileName + " (taille: " + outputFile.length() + " octets)");
            Blob blob = storage.createFrom(blobInfo, outputFile.toPath());

            // Générer une URL signée valide pendant 7 jours
            URL signedUrl;

            try {
                // Essayer d'abord avec les identifiants par défaut
                signedUrl = blob.signUrl(7, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature());
            } catch (Exception e) {
                logger.warning("Impossible de générer l'URL signée avec les identifiants par défaut: " + e.getMessage());

                // Chemin vers le fichier JSON de compte de service
                String credentialPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
                if (credentialPath == null || credentialPath.isEmpty()) {
                    logger.info("Variable GOOGLE_APPLICATION_CREDENTIALS non définie, utilisation du chemin par défaut");
                    // Utiliser un chemin par défaut si la variable n'est pas définie
                    credentialPath = "c:\\Users\\manue\\AppData\\Local\\gcloud\\application_default_credentials.json";
                }

                try {
                    // Charger les identifiants du compte de service à partir du fichier JSON
                    InputStream credentialsStream = new FileInputStream(credentialPath);
                    GoogleCredentials credentials = ServiceAccountCredentials.fromStream(credentialsStream);

                    // Vérifier que les identifiants implémentent ServiceAccountSigner
                    if (credentials instanceof ServiceAccountSigner) {
                        ServiceAccountSigner signer = (ServiceAccountSigner) credentials;
                        signedUrl = blob.signUrl(7, TimeUnit.DAYS,
                                Storage.SignUrlOption.signWith(signer),
                                Storage.SignUrlOption.withV4Signature());
                    } else {
                        throw new IllegalArgumentException("Les identifiants ne supportent pas la signature");
                    }


                } catch (Exception ex) {
                    logger.severe("Erreur lors de la génération de l'URL signée avec les identifiants personnalisés: " + ex.getMessage());
                    // Fallback à l'URL non signée si tout échoue
                    signedUrl = new URL(String.format("https://storage.googleapis.com/%s/%s",
                            BUCKET_NAME.replace("gs://", ""), fileName));
                }
            }
            logger.info("Fichier uploadé avec succès. URL: " + signedUrl.toString());
            return signedUrl.toString();

        } catch (Exception e) {
            logger.severe("Erreur lors de l'upload vers GCS: " + e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                logger.severe(element.toString());
            }
            throw new IOException("Erreur lors de l'upload vers Google Cloud Storage: " + e.getMessage(), e);
        }
    }


    @Override
    public void service(HttpRequest request, HttpResponse response) throws IOException {
        // Configurer les en-têtes CORS pour toutes les requêtes
        response.appendHeader("Access-Control-Allow-Origin", "*");
        response.appendHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        response.appendHeader("Access-Control-Allow-Headers", "Content-Type, Accept, Origin");
        response.appendHeader("Access-Control-Max-Age", "3600");

        // Traiter la méthode OPTIONS pour les requêtes préliminaires CORS
        if ("OPTIONS".equals(request.getMethod())) {
            response.setStatusCode(204); // No Content
            return;
        }

        // Première étape : vérifier l'identité de l'appelant
        String callerEmail = getCallerIdentity(request);
        if (callerEmail == null) {
            // Si l'identité ne peut pas être vérifiée et que vous exigez une authentification
            sendErrorResponse(response, 401, "Unauthorized: Jeton d'authentification invalide ou manquant.");
            return;
        }
        logger.info("Requête reçue de l'appelant authentifié : " + callerEmail);

        // Vérifier la méthode HTTP
        if (!"POST".equals(request.getMethod())) {
            sendErrorResponse(response, 405, "Méthode non autorisée. Veuillez utiliser POST.");
            return;
        }
        // Extraire l'identifiant utilisateur de la requête. C'est le {hash}.
        String userId = request.getFirstQueryParameter("userId").orElse(null);
        if (userId == null || userId.trim().isEmpty()) {
        }

        String jobId = request.getFirstQueryParameter("jobId")
                .orElse(generateJobId());

        logger.info("Job ID reçu/généré: " + jobId);

        // Ou depuis les headers HTTP
        String jobIdFromHeader = request.getHeaders()
                .getOrDefault("X-Job-ID", Collections.emptyList())
                .stream()
                .findFirst()
                .orElse(generateJobId());

        // Extraire l'identifiant utilisateur de la requête. C'est le {hash}.
        userId = request.getFirstQueryParameter("userId").orElse(null);
        if (userId == null || userId.trim().isEmpty()) {
            sendErrorResponse(response, 400, "Bad Request: Le paramètre de requête 'userId' est manquant ou vide.");
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

            ConfigurationJson configurationJson = null;

            try {
                for (String key : parts.keySet()) {
                    HttpPart httpPart = parts.get(key);
                    if (httpPart != null) {
                        logger.info("Partie " + key + " : " + httpPart.getFileName().orElse("null"));
                        try {
                            boolean added = false;
                            if (httpPart.getFileName() != null && httpPart.getFileName().isPresent()) {
                                String s1 = httpPart.getFileName().get();
                                String s2 = null;
                                if (s1.equals("animation.txt")) {
                                    File f = savePartToFileTxt(httpPart, tempDir);
                                    s1 = f.getAbsolutePath();
                                    s2 = "txt";
                                    added = true;
                                } else if (s1.endsWith("jpg") || s1.endsWith("png")) {
                                    File f = savePartToFile(httpPart, tempDir);
                                    s1 = f.getAbsolutePath();
                                    s2 = s1.substring(s1.lastIndexOf('.') + 1);
                                    added = true;
                                } else if (s1.equals("project.json")) {
                                    File f = savePartToFileTxt(httpPart, tempDir);
                                    configurationJson = ConfigurationJson.parseJson(f);
                                    s1 = f.getAbsolutePath();
                                    s2 = "json";
                                    added = true;
                                }
                                if (added) {
                                    types.add(new FileType(new File(s1), s2));
                                    if(!new File(s1).exists()) {
                                        long length = new File(s1).length();
                                        logger.info("ERREUR Fichier non enregistré "+s1+" --- "+length);
                                    } else {
                                        logger.info("Fichier enregistré "+s1+" -- " );
                                    }
                                } else {
                                    logger.info("ERREUR Fichier non enregistré "+s1);
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
            } catch (IOException e) {
                sendErrorResponse(response, 500, "Erreur lors de la lecture des parties du message HTTP : " + exceptionToString(e));
                throw new RuntimeException(e);
            }
            Logger.getLogger(getClass().getCanonicalName()).info("Main code in function");
            /*
            for (Entry<String, HttpPart> part : parts.entrySet()) {
                Logger.getLogger(getClass().getCanonicalName()).info(part.getKey());
                if(part.getKey().endsWith(".json") && part.getValue().getFileName().isPresent()) {
                    configurationJson = ConfigurationJson.parseJson(part.getValue().getFileName().get());
                }
            }
*/

            for ( FileType fp : types) {
                Logger.getLogger(getClass().getCanonicalName()).info(fp.file().getAbsolutePath());
                if(fp.file().getAbsolutePath().endsWith(".json") && fp.file().exists()) {
                    Scanner scanner = new Scanner(fp.file());
                    StringBuilder stringBuilder = new StringBuilder();
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        stringBuilder.append(line).append("\n");
                        logger.info(line);
                    }
                    configurationJson = ConfigurationJson.parseJson(stringBuilder.toString());
                }
            }


    // After processing all parts, validate that the configuration was found.
// This is a robust check that works even if assertions are disabled.
            if (configurationJson == null) {
                logger.severe("Le fichier de configuration 'project.json' est manquant dans la requête.");
                // Clean up any files that might have been created before failing.
                cleanupFiles(tempDir.toFile());
                sendErrorResponse(response, 400, "Bad Request: Le fichier de configuration 'project.json' est manquant.");
                return; // Stop execution immediately
            }

            Logger.getLogger(getClass().getCanonicalName()).info("Main code in function");

            String outputFileName = UUID.randomUUID() + ".mp4";
            outputFile = new File(tempDir.toString(), outputFileName);


            MovieGenerator2 generator = null;
            try {
                generator = new MovieGenerator2(types, outputFile, configurationJson, tempDir);
            } catch (RuntimeException ex) {
                exceptionToString(ex);
                sendErrorResponse(response, 500, "new MovieGenerator(): " + exceptionToString(ex));
                return;
            }
            boolean b = false;
            try {
                if (generator != null)
                    b = generator.generateMovie();
            } catch (RuntimeException ex) {
                exceptionToString(ex);
                sendErrorResponse(response, 500, "movieGenerator.generateMovie(): " + exceptionToString(ex));
                return;
            } /*finally {
                cleanupFiles(tempDir.toFile());
            }*/

            response.setContentType("video/mp4");
            // Définir les en-têtes CORS si nécessaire
            response.appendHeader("Access-Control-Allow-Origin", "*");
            response.appendHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.appendHeader("Access-Control-Allow-Headers", "Content-Type");

            String uploadToCloudStorage = uploadToCloudStorage(outputFile, userId);
//            response.getWriter().write(uploadToCloudStorage);

            // Optionnel: définir le nom du fichier pour le téléchargement
            response.appendHeader("Content-Disposition", "attachment; filename=\"generated-movie.mpg\"");

// Encoder le fichier en Base64 et le retourner dans une réponse JSON
            byte[] fileContent = Files.readAllBytes(outputFile.toPath());
            String base64Content = Base64.getEncoder().encodeToString(fileContent);

            OutputStream o = response.getOutputStream();

            response.setContentType("application/json");
            o.write(("{"+/*"\"video\":\"" + base64Content
                    + "\","+*/"\"mimeType\":\"video/mp4\", \"url:\":\"" + uploadToCloudStorage + "\"}").getBytes(StandardCharsets.UTF_8));

            if (outputFile.exists()) {
                Files.copy(outputFile.toPath(), o);
            } else {
                int size = -1;
                if (generator != null && generator.images != null)
                    size = generator.images.size();
                sendErrorResponse(response, 500, "Erreur lors de la génération du film : le fichier mp4 n'a pas été trouvé" + outputFile.getAbsolutePath() + "--- longueur  " + size);
            }
            logger.info("Film envoyé au client avec succès");


            cleanupFiles(tempDir.toFile());


        } catch (RuntimeException | IOException e) {
            logger.log(Level.SEVERE, "Erreur lors de la génération du film", e);
            sendErrorResponse(response, 500, "Erreur lors de la génération du film : " + exceptionToString(e));
        } finally {
            cleanupFiles(tempDir.toFile());
        }
    }

    /**
     * Génère un identifiant unique pour le job
     */
    private String generateJobId() {
        // Option 1: UUID simple
        return UUID.randomUUID().toString();

        // Option 2: UUID avec timestamp
        // return System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);

        // Option 3: Format personnalisé
        // return "job-" + System.currentTimeMillis() + "-" +
        //        Integer.toHexString(new Random().nextInt(0xFFFF));
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
            throw new RuntimeException("Le nom du fichier est invalide");
        }
        String s = part.getFileName().get();
        if (!s.isEmpty()) {
            File file = new File(tempDir.toFile(), s);
            try (var outputStream = Files.newOutputStream(file.toPath())) {
                part.getInputStream().transferTo(outputStream);
            }
            logger.info("Fichiers reçu : " + file.getName());
            return file;
        }
        throw new RuntimeException("Le nom du fichier est invalide");
    }

    private File savePartToFileTxt(HttpPart httpPart, Path tempDir) {
        // Lit le contenu
        String txt;
        File file = null;
        try (InputStream inputStream = httpPart.getInputStream()) {
            txt = new String(inputStream.readAllBytes(), "UTF-8");
            file = new File(tempDir.toFile(), httpPart.getFileName().get());
            PrintWriter printWriter = new PrintWriter(file);
            printWriter.print(txt);
            printWriter.close();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file!=null?file:null;
    }
    /**
     * Envoie une réponse d'erreur au client au format JSON
     */
    private void sendErrorResponse(HttpResponse response, int statusCode, String message) throws IOException {
        StringBuilder sb = new StringBuilder("\nERROR 500");
        // Échapper les guillemets pour éviter les problèmes dans le JSON
        String escapedMessage = message.replace("\"", "\\\"");
        sb.append("{\"error\":true,\"code\":" + statusCode + ",\"message\":\"" + escapedMessage + "\"}");
        Logger.getLogger(getClass().getCanonicalName()).severe(sb.toString());
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
     * Vérifie le jeton d'identification (ID Token) de la requête pour identifier l'appelant.
     *
     * @param request La requête HTTP entrante.
     * @return L'adresse e-mail du compte de service ou de l'utilisateur appelant,
     *         ou null si la vérification échoue ou si le jeton est absent.
     */
    private String getCallerIdentity(HttpRequest request) {
        // L'identité est dans l'en-tête "Authorization: Bearer <ID_TOKEN>"
        Optional<String> authHeader = request.getFirstHeader("Authorization");
        if (authHeader.isEmpty() || !authHeader.get().startsWith("Bearer ")) {
            logger.info("Aucun en-tête d'autorisation Bearer trouvé. L'appelant est peut-être anonyme.");
            // Retourner null pour indiquer un échec d'authentification.
            // Si votre fonction autorise les appels non authentifiés, vous pourriez retourner une valeur par défaut.
            return null;
        }

        String idTokenString = authHeader.get().substring(7); // Enlever "Bearer "

        // L'audience doit correspondre à l'URL de votre fonction.
        // C'est une mesure de sécurité cruciale pour éviter les attaques de type "confused deputy".
        String audience = request.getUri();

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(audience))
                .build();

        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                return idToken.getPayload().getEmail();
            } else {
                logger.warning("Jeton d'identification invalide. La vérification a échoué.");
                return null;
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors de la vérification du jeton d'identification", e);
            return null;
        }
    }

    /**
     * Nettoie les fichiers temporaires créés pendant le traitement
     */
    private void cleanupFiles(File tempDir) {
        try {
            if (tempDir != null && tempDir.exists()) {
                for (File file : Objects.requireNonNull(tempDir.listFiles())) {
                    if (file != null && !"..".equals(file.getName()) && !".".equals(file.getName())) {
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

    /**
     * Publishes a message to a Pub/Sub topic.
     *
     * @param projectId The ID of your Google Cloud project (e.g., "studio-6v2lo").
     * @param topicId The ID of your Pub/Sub topic (e.g., "render-completed").
     * @param jobId The Job ID of the completed render task.
     */
    public void publishJobCompletion(String projectId, String topicId, String jobId)
            throws IOException, ExecutionException, InterruptedException {
/*

        TopicName topicName = TopicName.of(projectId, topicId);
        Publisher publisher = null;
        try {
            // Create a publisher instance.
            publisher = Publisher.newBuilder(topicName).build();

            // The message payload should be the jobId.
            // You can also use JSON for more complex data.
            ByteString data = ByteString.copyFromUtf8(jobId);
            PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();

            // Once published, returns a server-assigned message id (unique within the topic)
            ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
            String messageId = messageIdFuture.get();

            System.out.println("Published message ID: " + messageId + " for job ID: " + jobId);

        } finally {
            if (publisher != null) {
                // When finished with the publisher, shutdown to free up resources.
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }*/
    }
/*
    // Example of how to call this method from your function
    public void handleRenderRequest(String jobId) {
        // ... your existing video rendering logic ...

        // ---- WHEN RENDERING IS COMPLETE ----
        try {
            // Call the publisher method
            String projectId = "studio-6v2lo";
            String topicId = "render-completed";
            publishJobCompletion(projectId, topicId, jobId);
            System.out.println("Successfully published completion for job: " + jobId);
        } catch (Exception e) {
            System.err.println("Error publishing Pub/Sub message for job " + jobId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }*/
}
