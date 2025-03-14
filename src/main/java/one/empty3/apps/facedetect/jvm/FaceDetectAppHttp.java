package one.empty3.apps.facedetect.jvm;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionScopes;
import com.google.api.services.vision.v1.model.*;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;

import one.empty3.libs.Image;
import java.io.*;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.*;

public class FaceDetectAppHttp {
    private static final String APPLICATION_NAME = "MeshMask";
    private static final int MAX_RESULTS = 10;
    private final Vision vision;
    private final Gson gson = new Gson();
    private String[][][] landmarks;
    private PrintWriter dataWriter;

    public FaceDetectAppHttp() {
        try {
            this.vision = getVisionService();
        } catch (IOException | GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public FaceDetectAppHttp(Vision visionService) {
        this.vision = visionService;
    }

    // ... (Other methods: initStructurePolygons, frontal, getVisionService, detectFaces, writeFaceData) ...

    private void writeFaceData(Image img, FaceAnnotation faceAnnotation) {

        for (int i = 0; i < landmarks.length; i++) {
            for (int j = 0; j < landmarks[i].length; j++) {
                for (int k = 0; k < landmarks[i][j].length; k++) {
                    int finalI = i;
                    int finalJ = j;
                    int finalK = k;
                    Optional<Landmark> landmark1 = faceAnnotation.getLandmarks().stream().filter(landmark ->
                            landmark.getType() != null && landmark.getType().equals(landmarks[finalI][finalJ][finalK])).findFirst();
                    if (!landmark1.isEmpty()) {
                        Landmark landmark2 = landmark1.get();
                        dataWriter.println(landmark2.getType());
                        dataWriter.println((double) landmark2.getPosition().getX() / img.getWidth());
                        dataWriter.println((double) landmark2.getPosition().getY() / img.getHeight());
                        dataWriter.println();
                    }
                }
            }
        }
    }

    /**
     * Connects to the Vision API using Application Default Credentials.
     */
    public static Vision getVisionService() throws IOException, GeneralSecurityException {
        GoogleCredentials credential =
                GoogleCredentials.getApplicationDefault().createScoped(VisionScopes.all());
        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        return new Vision.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                jsonFactory,
                new HttpCredentialsAdapter(credential))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public List<FaceAnnotation> detectFaces(byte[] read, int maxResults) throws IOException {
        AnnotateImageRequest request =
                new AnnotateImageRequest()
                        .setImage(new com.google.api.services.vision.v1.model.Image().encodeContent(read))
                        .setFeatures(
                                com.google.common.collect.ImmutableList.of(
                                        new Feature().setType("FACE_DETECTION").setMaxResults(maxResults)));
        Vision.Images.Annotate annotate =
                vision
                        .images()
                        .annotate(new BatchAnnotateImagesRequest().setRequests(com.google.common.collect.ImmutableList.of(request)));
        // Due to a bug: requests to Vision API containing large images fail when GZipped.
        annotate.setDisableGZipContent(true);

        BatchAnnotateImagesResponse batchResponse = annotate.execute();
        assert batchResponse.getResponses().size() == 1;
        AnnotateImageResponse response = batchResponse.getResponses().get(0);
        if (response.getFaceAnnotations() == null) {
            throw new IOException(
                    response.getError() != null
                            ? response.getError().getMessage()
                            : "Unknown error getting image annotations");
        }
        return response.getFaceAnnotations();
    }

    public void initStructurePolygons() {
        landmarks = new String[][][]{
                {
                        {"LEFT_EAR_TRAGION", "CHIN_LEFT_GONION", "CHIN_GNATHION", "LEFT_CHEEK_CENTER"},
                        {"MOUTH_LEFT", "UPPER_LIP", "MOUTH_RIGHT", "MOUTH_CENTER"},
                        {"LEFT_EYE_LEFT_CORNER", "LEFT_EYE_TOP_BOUNDARY", "LEFT_EYE_RIGHT_CORNER", "LEFT_EYE_BOTTOM_BOUNDARY"},
                        {"LEFT_OF_LEFT_EYEBROW", "LEFT_EYEBROW_UPPER_MIDPOINT", "RIGHT_OF_LEFT_EYEBROW"},
                        {"MIDPOINT_BETWEEN_EYES", "NOSE_TIP", "NOSE_BOTTOM_LEFT"},
                },
                {
                        {"RIGHT_EAR_TRAGION", "CHIN_RIGHT_GONION", "CHIN_GNATHION", "RIGHT_CHEEK_CENTER"},
                        {"MOUTH_LEFT", "LOWER_LIP", "MOUTH_RIGHT", "MOUTH_CENTER"},
                        {"RIGHT_EYE_LEFT_CORNER", "RIGHT_EYE_TOP_BOUNDARY", "RIGHT_EYE_RIGHT_CORNER", "RIGHT_EYE_BOTTOM_BOUNDARY"},
                        {"LEFT_OF_RIGHT_EYEBROW", "RIGHT_EYEBROW_UPPER_MIDPOINT", "RIGHT_OF_RIGHT_EYEBROW"},
                        {"MIDPOINT_BETWEEN_EYES", "NOSE_TIP", "NOSE_BOTTOM_RIGHT"},
                }, {
                {"NOSE_TIP", "NOSE_BOTTOM_RIGHT", "NOSE_BOTTOM_CENTER", "NOSE_BOTTOM_LEFT"}

        }
        };
    }

    public String run(File imgFile) {
        ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
        try {
            /*
            FileInputStream fileInputStream = new FileInputStream(imgFile);
            int b = 0;
            while((b=fileInputStream.read())!=-1) {
                byteArrayOutputStream1.write(b);
            }
*/
            one.empty3.libs.Image img = (one.empty3.libs.Image) one.empty3.libs.Image.getFromFile(imgFile);

            byte[] decode = byteArrayOutputStream1.toByteArray();
            FaceDetectAppHttp app = new FaceDetectAppHttp(getVisionService());
            List<FaceAnnotation> faces = detectFaces(decode, MAX_RESULTS);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            dataWriter = new PrintWriter(byteArrayOutputStream);
            initStructurePolygons();
            faces.forEach(faceAnnotation -> {
                writeFaceData(img, faceAnnotation);
            });

            return byteArrayOutputStream.toString(Charset.defaultCharset());
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();

        }
    }
}