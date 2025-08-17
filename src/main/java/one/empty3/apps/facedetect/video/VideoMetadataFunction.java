package one.empty3.apps.facedetect.video;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.auth.ServiceAccountSigner;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.functions.HttpFunction;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.functions.HttpResponse;
import com.google.cloud.storage.*;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.jcodec.api.JCodecException;
import org.jcodec.common.DemuxerTrack;
import org.jcodec.common.io.FileChannelWrapper;
import org.jcodec.common.io.NIOUtils;
import org.jcodec.containers.mp4.demuxer.MP4Demuxer;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An HTTP Cloud Function to extract technical metadata from a video file stored in Firebase Storage.
 * The function is secured and requires a Firebase Authentication ID token.
 */
public class VideoMetadataFunction {
    private static final int MAX_PROCESSING_TIME_MS = 600000; // 9 minutes
    private static final int HEARTBEAT_INTERVAL_MS = 5000; // 30 secondes
    private static final String BUCKET_NAME = "gs://studio-6v2lo.firebasestorage.app";
    private static final Logger logger = Logger.getLogger(VideoMetadataFunction.class.getCanonicalName());
    private static final String TEXT_PART_NAME = ".txt";
    private static final String IMAGE1_PART_NAME = ".jpg";
    private static final String IMAGE2_PART_NAME = ".png";
    private static final String JSONFILE_EXT = ".json";
    private static final String CONTENT_TYPE_mp4 = "video/mp4";
    private static final String CONTENT_DISPOSITION_HEADER = "Content-Disposition";
    private static final String CONTENT_DISPOSITION_VALUE = "attachment; filename=\"generated-movie.mp4\"";
    private static final String ALLOWED_ORIGIN = "https://us-central1-studio-6v2lo.cloudfunctions.net/motion-weaver-render";
    private static final String ALLOWED_METHODS = "GET, POST, OPTIONS";
    private static final String ALLOWED_HEADERS = "Content-Type";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";
    private static final String ACCESS_CONTROL_ALLOW_METHODS_HEADER = "Access-Control-Allow-Methods";
    private static final String ACCESS_CONTROL_ALLOW_HEADERS_HEADER = "Access-Control-Allow-Headers";
    private static final String ACCESS_CONTROL_MAX_AGE_HEADER = "Access-Control-Max-Age";
    private static final String SERVICE_ACCOUNT = "firebase-app-hosting-compute@studio-6v2lo.iam.gserviceaccount.com";

    public static String uploadToCloudStorageMetadata(File outputFile, String userId, String date) throws IOException {
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
            String fileName = "users/" + userId + "/generated_video/metadata/" + date + "-" + outputFile.getName().replace(".mp4", ".json");
            // Créer les informations du blob avec le bon Content-Type
            BlobId blobId = BlobId.of(BUCKET_NAME.replace("gs://", ""), fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType("application/json")
                    .build();

            Map<String, Object> stringObjectMap = extractVideoMetadata(outputFile);
            stringObjectMap.put("date", Long.valueOf(date));
            stringObjectMap.put("createdAt", Long.valueOf(date));
            stringObjectMap.put("name", fileName.replace(".mp4", ""));
            stringObjectMap.put("userId", userId);
            String json = new Gson().toJson(stringObjectMap);
            logger.info("JSON: " + json);

            // Upload le fichier
            logger.info("Upload du fichier vers GCS: " + fileName.replace(".mp4", ".json") + " (taille: " + outputFile.length() + " octets)");
            Blob blob = storage.createFrom(blobInfo, new ByteArrayInputStream(json.getBytes()));

            // Générer une URL signée valide pendant 7 jours
            URL signedUrl;

            try {
                // Essayer d'abord avec les identifiants par défaut
                signedUrl = blob.signUrl(7, TimeUnit.DAYS, Storage.SignUrlOption.withV4Signature());
                logger.info("URL correctly signed first attempts");
                return signedUrl.toString();
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
                        logger.info("URL correctly signed 2 attempts");
                        return signedUrl.toString();
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

    /**
     * Extracts technical metadata from a video file using JCodec.
     */
    public static Map<String, Object> extractVideoMetadata(File videoFile) throws IOException, JCodecException {
        Map<String, Object> metadata = new HashMap<>();
        try (FileChannelWrapper channel = NIOUtils.readableChannel(videoFile)) {
            MP4Demuxer demuxer = MP4Demuxer.createMP4Demuxer(channel);
            DemuxerTrack videoTrack = demuxer.getVideoTrack();

            if (videoTrack != null) {
                org.jcodec.common.model.Size dimensions = videoTrack.getMeta().getVideoCodecMeta().getSize();;
                double duration = videoTrack.getMeta().getTotalDuration();
                int totalFrames = videoTrack.getMeta().getTotalFrames();

                metadata.put("durationSeconds", duration);
                metadata.put("frameCount", totalFrames);
                metadata.put("width", dimensions.getWidth());
                metadata.put("height", dimensions.getHeight());
                metadata.put("codec", videoTrack.getMeta().getCodec()); // e.g., "avc1" for H.264
                if (duration > 0) {
                    metadata.put("frameRate", totalFrames / duration);
                }
            } else {
                throw new JCodecException("No video track found in the file.");
            }
            return metadata;
        }
    }

}