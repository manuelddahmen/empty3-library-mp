package one.empty3.apps.facedetect.video;

import com.google.auth.ServiceAccountSigner;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import java.awt.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Logger;

import one.empty3.library.*;
import one.empty3.library.core.nurbs.SurfaceParametriquePolynomialeBezier;
import one.empty3.libs.Image;

import javax.imageio.ImageIO;

import static one.empty3.library.ZBufferImpl.DISPLAY_ALL;

/**
 * Classe qui génère un fichier vidéo MPEG à partir d'un fichier texte et de deux images
 */
public class MovieGenerator2 {
    private static final int RES_AVG = 200;
    private final Storage storage;
    private final HashMap<String, NamedPoint> mapPoint = new HashMap<>();


    private static final Logger logger = Logger.getLogger(MovieGenerator2.class.getName());
    private List<FileType> fileTypes = List.of();
    private int currentFrameIndex = 0;
    private int totalFramesCount = 0;
    private Image currentImageFrame;
    private File outputFile;
    List<Image> images = new ArrayList<>();
    private Transform currentTransform; // Pour stocker la transformation en cours
    Map<String, List<Point>> copyAttachedTimeCordinates = new HashMap<>();
    Map<Integer, List<Image>> imageIds = new HashMap<>();
    Map<String, Map<Integer, Image>> imageGroupIds = new HashMap<>();

    /**
     * Constructeur par défaut
     */
    public MovieGenerator2(List<FileType> types, File outputFile, ConfigurationJson configurationJson, Path tempDir) {
        // Initialiser le client Storage de manière standard.
        // Cela utilise les "Application Default Credentials" de l'environnement (local ou Cloud Function).
        this.storage = StorageOptions.getDefaultInstance().getService();
        this.outputFile = outputFile;
        this.fileTypes = types;
        try {
            outputFile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        if (outputFile.exists()) {
            for (FileType type : types) {
                logger.info("Type : " + type.type());
                logger.info(type.file().getAbsolutePath());
                logger.info(outputFile.getName());


            }
        }

        currentImageFrame = new Image(RES_AVG, RES_AVG);


        for (FileType fileType : types) {
            if (fileType.file().getAbsolutePath().endsWith("txt")) {
                try {/*
                    File f = fileType.file();
                    String content = Files.readString(f.toPath());
                    String[] split = content.split("\n");
                    String currentGroup = "default";
                    for (int lineNumber = 0; lineNumber < split.length; lineNumber++) {
                        String line = split[lineNumber];
                        if (line == null || line.equals(""))
                            continue;
                        logger.info(line);
                        if (line.equalsIgnoreCase("next")) {
                            images.add(currentImageFrame);
                            currentImageFrame = new Image(RES_AVG, RES_AVG);
                            currentGroup = "default";
                            mapPoint.clear();

                        } else if (line.toLowerCase().length() >= "group ".length() && line.toLowerCase().startsWith("group ")) {
                            currentGroup = line.substring("group ".length());

                        } else if (line.toLowerCase().length() >= "endofgroup".length() && line.toLowerCase().startsWith("endofgroup")) {
                            currentGroup = null;
                        } else if (line.toLowerCase().length() > 1) {
                            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                            NamedPoint namedPoint = new NamedPoint();
                            lineNumber = readPoint(namedPoint, List.of(split), lineNumber, atomicBoolean);
                            if (atomicBoolean.get()) {
                                currentGroup = null;
                            }
                        }
                    }*/
                } catch (RuntimeException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        if (configurationJson != null) {
            logger.info(configurationJson.toString());
            logger.info("" + images.size());
        }
        if (configurationJson == null) {
            logger.info("configurationJson is null");
            return;
        }

        final Image[] image = {null};
        int frame = 1;


        int frames = 1;

        final int[] transformIndex = {0};
        // Vérifier les images associées aux groupes avant de commencer le traitement
/*        if (configurationJson != null && configurationJson.getGroups() != null) {
            logger.info("Vérification des images associées aux groupes avant traitement...");
            int validImages = verifyGroupImages(configurationJson, imageIds, transformIndex[0]);
            logger.info(validImages + " images valides trouvées pour " + configurationJson.getGroups().size() + " groupes");

        }
*/
        logger.info(configurationJson.toString());
        currentFrameIndex = 0; // Remplace l'ancienne variable i2

        Image[][] allImagesSets = new Image[configurationJson.getGroups().size()][configurationJson.getTransforms().size()];
        logger.info("groups : " + configurationJson.getGroups().size());
        logger.info("transforms : " + configurationJson.getTransforms().size());
        logger.info("animation total : " + configurationJson.getAnimation().size());
        int countImages = 0;
        for (int indexG = 0; indexG < configurationJson.getGroups().size(); indexG++) {
            for (int indexT = 0; indexT < configurationJson.getTransforms().size(); indexT++) {
                Group g = configurationJson.getGroups().get(indexG);
                Transform transform = configurationJson.getTransforms().get(indexT);
                if (transform instanceof TransformAttachImage transformAttachImage) {
                    String imageUrl = transformAttachImage.getImageUrl();
                    Image image1;
                    if(g.getId().equals(transformAttachImage.getTargetId())) {
                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            try {
                                image1 = readImageFromGcsUrl(transformAttachImage.getImageUrl());
                                allImagesSets[indexT][indexT] = image1;
                                countImages++;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                } else if (transform instanceof TransformDetachImage transformDetachImage && g.getId().equals(transformDetachImage.getTargetId())) {
                    allImagesSets[indexG][indexT] = null;
                } else if (indexT > 0) {
                    allImagesSets[indexG][indexT] = allImagesSets[indexG][indexT - 1];
                } else if(indexT==0) {
                    allImagesSets[indexG][indexT] = null;
                }
            }

        }
        if (countImages == 0) {
            logger.severe("Aucunes image trouvée pour " + configurationJson.getGroups().size() + " groupes");
        } else {
            logger.info(countImages + " images trouvées pour " + configurationJson.getGroups().size() + " groupes");
        }
        // Traiter chaque transformation définie dans le fichier de configuration
        if (configurationJson != null && configurationJson.getTransforms() != null) {
            for (int i = 0; i < configurationJson.getTransforms().size(); i++) {
                Transform transform = configurationJson.getTransforms().get(i);
                int relativeFrameIndex = 0;
                logger.info("Traitement de la transformation " + transform.getClass().getSimpleName());

                // Récupérer le nombre de frames pour cette transformation
                int transformFrames = transform.getFrames();
                int currentFrameInCurrentTransform = 0;
                // Stocker la transformation actuelle pour référence dans les autres méthodes
                currentTransform = transform;

                // Traiter chaque frame de la transformation

                for (int j = 0; j < transformFrames; j++) {
                    currentImageFrame = new Image(RES_AVG, RES_AVG);
                    double progress = 1.0  / transformFrames;

                    List<Group> groups = configurationJson.getGroups();
                    for (int k = 0; k < groups.size(); k++) {
                        List<Point> groupPoints = new ArrayList<>();
                        int finalFrameIndex = currentFrameIndex;
                        String groupId = transform.getTargetId();
                        Group g = groups.get(k);
                        List<Point> finalGroupPoints = groupPoints;
                        configurationJson.getPoints().forEach(
                                point -> {
                                    g.getPointIds().forEach(
                                            s -> {
                                                if (point.getId().equals(s) &&
                                                        !finalGroupPoints.contains(point)) {
                                                    finalGroupPoints.add(point);
                                                }
                                            });
                                });
                        if(transform.getTargetType().equals(Transform.TargetType.Group)&&transform.getTargetId().equals(g.getId())) {
                            if(groupPoints.isEmpty()) {
                                groupPoints = updatePointsFromGroup1(groupPoints, configurationJson, g);
                            } else {
                                groupPoints = transformPointsBasedOnProgress(groupPoints, transform, progress);
                            }
                            List<Point> finalGroupPoints1 = groupPoints;
                            configurationJson.getPoints().forEach(point1 -> finalGroupPoints1.forEach(point2 -> {
                                if (point1.getId().equals(point2.getId())) {
                                    point1.setX(point2.getX());
                                    point1.setY(point2.getY());
                                    point1.setColor(point2.getColor());
                                    point1.setName(point2.getName());
                                    point1.setVisible(point2.isVisible());

                                }
                            }));
                        }
                        Image image1 = null;
                        if(allImagesSets[k][i]!=null ) {
                            image1 = new Image(RES_AVG, RES_AVG);
                            Graphics graphics = image1.getBi().getGraphics();
                            graphics.drawImage(allImagesSets[k][i].getBi(), 0, 0, image1.getBi().getWidth(), image1.getBi().getHeight(), null);
                        } else {

                        }
                        // TRANSFORMS SPECIFICS
                        if (transform instanceof TransformSetVisibility transformSetVisibility) {
                            assert g != null;
                            g.setVisible(transformSetVisibility.isVisible());
                        }

                        drawImageInFrame(groupPoints,  configurationJson, image1, g);
                    }
                    j++;
                    // Passer à la frame suivante
                    frame = frame + 1;
                    totalFramesCount++;
                    currentFrameIndex = frame; // Mettre à jour l'index de frame courant
                    images.add(currentImageFrame);
                }
            }
        }
    }

    public List<Point> updatePointsFromGroup1(List<Point> gp, ConfigurationJson configurationJson, Group g) {
        List<Point> groupPoints = new ArrayList<>();
        for (int p = 0; p < g.getPointIds().size(); p++) {
            int finalP = p;
            boolean anymatch = configurationJson.getPoints().stream().anyMatch(new Predicate<Point>() {
                @Override
                public boolean test(Point point) {
                    if(g.getPointIds().get(finalP).equals(point.getId())) {
                        gp.add(point);
                        return true;
                    }
                    return false;
                }
            });
        }
        return groupPoints;
    }

    /**
     * Dessine une image avec les éléments visuels (points, groupes) configurés
     *
     * @param g                 La groupe à dessiner
     * @param configurationJson La configuration contenant les points et groupes à dessiner
     * @param gp
     * @return
     */
    private Image drawImageInFrame(List<Point> g, ConfigurationJson configurationJson, Image image1, Group gp) {
        if (currentImageFrame == null || image1 == null) {
            return image1;
        }

        Graphics2D g2d = (Graphics2D) currentImageFrame.getBi().getGraphics();

        // Améliorer la qualité du rendu
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Fond blanc pour l'image
        g2d.setColor(Color.WHITE);
        //g2d.fillRect(0, 0, currentImage.getBi().getWidth(), currentImage.getBi().getHeight());

        // Dessiner tous les points visibles
        for (Point point : g) {
                    if (point.isVisible()) {
                        // Convertir les coordonnées normalisées (0-1) en pixels
                        int x = (int) (point.getX() * currentImageFrame.getBi().getWidth());
                        int y = (int) (point.getY() * currentImageFrame.getBi().getHeight());

                        // Définir la couleur du point (utiliser une couleur par défaut si non spécifiée)
                        try {
                            if (point.getColor() != null) {
                                g2d.setColor(point.getColor());
                            } else {
                                g2d.setColor(Color.RED);
                            }
                        } catch (Exception e) {
                            g2d.setColor(Color.RED); // Couleur par défaut en cas d'erreur
                        }

                        // Dessiner le point
                        int pointSize = 5;
                        g2d.fillOval(x - pointSize / 2, y - pointSize / 2, pointSize, pointSize);

                        // Ajouter le nom du point s'il existe
                        if (point.getName() != null && !point.getName().isEmpty()) {
                            g2d.setColor(Color.WHITE);
                            //g2d.drawString(point.getName(), x + pointSize, y);
                        }

                    }

        }
        if(image1!=null) {
            ZBufferImpl zBuffer = new ZBufferImpl(RES_AVG, RES_AVG);
            zBuffer.idzpp();
            zBuffer.setDisplayType(DISPLAY_ALL);
            Camera c = new Camera(new Point3D(0.5, 0.5, -Math.sqrt(3)/2.0),
                    new Point3D(0.5, 0.5, 0.0));
            zBuffer.scene(new Scene());
            zBuffer.camera(c);
            zBuffer.setAngles(Math.PI/3, Math.PI/3);
            c.calculerMatrice(Point3D.X.mult(-1));
            Matrix33 tild = c.getMatrix().tild();
            for(int i=0; i<3; i++) {
                tild.set(0, i, tild.get(0, i) * -1);
            }
            c.setMatrix(tild);
            zBuffer.texture(new ImageTexture(currentImageFrame));

            zBuffer.setIncrementOptimizer(new ZBufferImpl.IncrementOptimizer(0.005, 0.05));

            if (hasAllCornerPoints(gp,g)) {
                // Récupérer les points nommés pour s'assurer que nous utilisons le bon ordre
                Map<String, Point> cornerPoints = getNamedCornerPoints(gp, g);

                // Extraire les points par leur nom
                Point topLeft = cornerPoints.get("topLeft");
                Point topRight = cornerPoints.get("topRight");
                Point bottomLeft = cornerPoints.get("bottomLeft");
                Point bottomRight = cornerPoints.get("bottomRight");

                logger.info("Affichage de l'image avec tracerQuad en utilisant les points nommés");
                logger.info("topLeft: (" + topLeft.getX() + ", " + topLeft.getY() + ")");
                logger.info("topRight: (" + topRight.getX() + ", " + topRight.getY() + ")");
                logger.info("bottomLeft: (" + bottomLeft.getX() + ", " + bottomLeft.getY() + ")");
                logger.info("bottomRight: (" + bottomRight.getX() + ", " + bottomRight.getY() + ")");

                SurfaceParametriquePolynomialeBezier surfaceParametriquePolynomialeBezier = new SurfaceParametriquePolynomialeBezier(
                        new Point3D[][]{
                                {
                                        new Point3D(topLeft.getX(), topLeft.getY(), 0.0),
                                        new Point3D(topRight.getX(), topRight.getY(), 0.0)
                                },
                                {
                                        new Point3D(bottomLeft.getX(), bottomLeft.getY(), 0.0),
                                        new Point3D(bottomRight.getX(), bottomRight.getY(), 0.0)
                                }
                        });
                surfaceParametriquePolynomialeBezier.texture(new ImageTexture(image1));
                // Utiliser tracerQuad avec les points nommés dans le bon ordre
                zBuffer.draw(surfaceParametriquePolynomialeBezier);
                /*zBuffer.tracerQuad(
                        new Point3D(topRight.getX(), topRight.getY(), 0.0),
                        new Point3D(topLeft.getX(), topLeft.getY(), 0.0),
                        new Point3D(bottomLeft.getX(), bottomLeft.getY(), 0.0),
                        new Point3D(bottomRight.getX(), bottomRight.getY(), 0.0),
                        new ImageTexture(image1),
                        0, 1, 0, 1, surfaceParametriquePolynomialeBezier);
                */
                currentImageFrame = zBuffer.image();

            } else {
                logger.info("Le groupe ne contient pas tous les points de coin nommés, affichage simple de l'image");
                g2d.drawImage(image1.getBi(), 0, 0, currentImageFrame.getBi().getWidth(), currentImageFrame.getBi().getHeight(), null);
            }
        }
        return currentImageFrame;
    }

    private boolean isBlank(Image image1) {
        if (image1 == null) {
            return true;
        }
        if (image1.getBi() == null) {
            return true;
        }
        if (image1.getBi().getWidth() == 0) {
            return true;
        }
        if (image1.getBi().getHeight() == 0) {
            return true;
        }
        int refColor = 0;
        for (int i = 0; i < image1.getBi().getWidth(); i++) {
            for (int j = 0; j < image1.getBi().getHeight(); j++) {
                if (refColor == 0) {
                    refColor = image1.getBi().getRGB(i, j);
                }
                if (image1.getBi().getRGB(i, j) != refColor) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Génère un fichier vidéo à partir des images créées
     *
     * @return true si la génération a réussi, false sinon
     */
    public boolean generateMovie() {
        logger.info("Début de la génération de la vidéo: " + (outputFile != null ? outputFile.getAbsolutePath() : "(fichier non défini)"));

        // Vérifier si le fichier de sortie est valide
        if (outputFile == null) {
            logger.severe("Erreur: Le fichier de sortie est null");
            return false;
        }

        // Préparer les images pour l'encodage
        Image[] imagesArray;

        if (!images.isEmpty()) {
            logger.info("Utilisation de " + images.size() + " images générées pour la vidéo");
            imagesArray = new Image[images.size()];
            for (int i = 0; i < images.size(); i++) {
                if (images.get(i) == null) {
                    logger.warning("Image #" + i + " est null, elle sera remplacée par une image vide");
                    imagesArray[i] = new Image(RES_AVG, RES_AVG); // Image vide si null
                } else {
                    imagesArray[i] = images.get(i);
                }
            }
        } else {
            logger.warning("Aucune image générée, création de 50 images par défaut");
            imagesArray = new Image[50];
            for (int i = 0; i < imagesArray.length; i++) {
                imagesArray[i] = new Image(RES_AVG, RES_AVG);

                // Dessiner quelque chose de basique sur l'image par défaut pour qu'elle ne soit pas vide
                Graphics g = imagesArray[i].getBi().getGraphics();
                g.setColor(Color.WHITE);
                g.fillRect(0, 0, RES_AVG, RES_AVG);
                g.setColor(Color.BLACK);
                g.drawString("Frame " + i, 10, RES_AVG / 2);
            }
        }

        try {
            logger.info("Début de l'encodage vidéo avec " + imagesArray.length + " images");

            // Créer le répertoire parent si nécessaire
            if (outputFile.getParentFile() != null && !outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }

            // Encoder les images en vidéo
            new JCodecImageToVideoEncoder().encodeImagesToVideo(outputFile, imagesArray);

            if (outputFile.exists() && outputFile.length() > 0) {
                logger.info("La génération de la vidéo est terminée avec succès: " +
                        outputFile.getAbsolutePath() + " (" + outputFile.length() + " octets)");
                return true;
            } else {
                logger.severe("Le fichier vidéo n'a pas été créé ou est vide");
                return false;
            }
        } catch (IOException e) {
            logger.severe("Erreur lors de l'encodage de la vidéo: " + e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                logger.severe(element.toString());
            }
            return false;
        } catch (Exception e) {
            logger.severe("Exception inattendue lors de la génération de la vidéo: " + e.getMessage());
            for (StackTraceElement element : e.getStackTrace()) {
                logger.severe(element.toString());
            }
            return false;
        }
    }

    /**
     * Lit un point nommé à partir d'un ensemble de lignes de texte
     *
     * @param lines      Liste des lignes de texte
     * @param lineNumber Numéro de ligne où commence la définition du point
     * @return Le point nommé ou null si le format est invalide
     */
    public int readPoint(NamedPoint namedPoint, List<String> lines, int lineNumber, AtomicBoolean endOfGroup) {
        // Vérifier que nous avons assez de lignes pour lire un point complet
        if (lines == null || lineNumber < 0) {
            logger.fine("Pas assez de lignes pour lire un point à la ligne " + lineNumber);
            return lineNumber;
        }
        if (lineNumber + 3 >= lines.size())
            return lines.size();

        // Vérifier que la ligne actuelle n'est pas vide
        String currentLine = lines.get(lineNumber);
        if (currentLine == null || currentLine.isEmpty()) {
            logger.fine("Ligne vide à " + lineNumber);
            return lineNumber + 1;
        }

        // Vérifier que la ligne commence par une lettre et n'est pas un mot-clé réservé
        String lowerCaseLine = currentLine.toLowerCase();
        if (!Character.isAlphabetic(lowerCaseLine.charAt(0)) ||
                lowerCaseLine.startsWith("group ") ||
                lowerCaseLine.startsWith("next")) {
            logger.fine("Ligne " + lineNumber + " n'est pas un début de point: " + currentLine);
            return -1;
        } else if (lowerCaseLine.startsWith("endofgroup")) {
            endOfGroup.set(true);
            return lineNumber + 1;
        }

        try {
            // Lire le nom du point et ses coordonnées
            namedPoint.setName(currentLine.trim());

            // Coordonnée X
            String xLine = lines.get(lineNumber + 1);
            if (xLine == null || xLine.isEmpty()) {
                logger.warning("Coordonnée X manquante pour le point " + currentLine + " à la ligne " + (lineNumber + 1));
                return lineNumber + 2;
            }
            double x = Double.parseDouble(xLine);

            // Coordonnée Y
            String yLine = lines.get(lineNumber + 2);
            if (yLine == null || yLine.isEmpty()) {
                logger.warning("Coordonnée Y manquante pour le point " + currentLine + " à la ligne " + (lineNumber + 2));
                return lineNumber + 1;
            }
            double y = Double.parseDouble(yLine);

            // Vérifier que la ligne suivante est vide (séparateur)
            String separatorLine = lines.get(lineNumber + 3);
            if (separatorLine == null || separatorLine.isBlank()) {
                namedPoint.setX(x);
                namedPoint.setY(y);
                logger.fine("Point " + namedPoint.getName() + " lu avec succès: (" + x + ", " + y + ")");
                return lineNumber + 4;
            } else {
                namedPoint.setX(x);
                namedPoint.setY(y);
                if (!separatorLine.isBlank() && !separatorLine.equalsIgnoreCase("endofgroup")) {
                    endOfGroup.set(true);
                    return lineNumber + 4;
                }
                logger.warning("Format incorrect: la ligne séparatrice après les coordonnées n'est pas vide: " + separatorLine);
                return lineNumber + 3;
            }

        } catch (NumberFormatException ex) {
            logger.warning("Erreur de format des coordonnées pour le point à la ligne " + lineNumber + ": " + ex.getMessage());
            return lineNumber + 1;
        } catch (RuntimeException ex) {
            logger.warning("Erreur lors de la lecture du point à la ligne " + lineNumber + ": " + ex.getMessage());
            return lineNumber + 1;
        }
    }

    public void createVideoFrames(Path imagesDir) {

    }

    /**
     * Convertit une liste de points en format texte utilisable par RunZBuffer
     *
     * @param points Liste de points à convertir
     * @return Représentation textuelle des points
     */
    public String list2string(List points) {
        if (points == null || points.isEmpty()) {
            logger.warning("Liste de points vide ou null dans list2string");
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < points.size(); i++) {
            if (points.get(i) == null) {
                logger.warning("Point null à l'index " + i);
                continue;
            }

            try {
                Point point = (Point) points.get(i);
                sb.append(point.getId() != null ? point.getId() : "point_" + i).append("\n");
                sb.append(point.getX()).append("\n");
                sb.append(point.getY()).append("\n");
                sb.append("\n");
            } catch (ClassCastException e) {
                logger.warning("Impossible de convertir l'objet à l'index " + i + " en Point: " + e.getMessage());
            }
        }
        return sb.toString();
    }

    /**
     * Redimensionne une image pour qu'elle remplisse tout l'écran
     *
     * @param originalImage L'image originale à redimensionner
     * @return Une nouvelle image redimensionnée
     */
    private Image resizeImageToFillScreen(Image originalImage) {
        if (originalImage == null) {
            logger.warning("Impossible de redimensionner une image null");
            return new Image(RES_AVG, RES_AVG);
        }

        try {
            // Déterminer la taille de l'écran ou utiliser une valeur par défaut plus grande que RES_AVG
            int screenWidth = Math.min(RES_AVG, 1920); // Par défaut 1920 ou 4x RES_AVG
            int screenHeight = Math.min(RES_AVG, 1080); // Par défaut 1080 ou 4x RES_AVG

            // Pour un environnement avec interface graphique, on pourrait utiliser:
            // Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            // int screenWidth = (int) screenSize.getWidth();
            // int screenHeight = (int) screenSize.getHeight();

            // Créer une nouvelle image à la taille de l'écran
            Image resizedImage = new Image(screenWidth, screenHeight);
            Graphics2D g2d = (Graphics2D) resizedImage.getBi().getGraphics();

            // Configurer le rendu pour une meilleure qualité
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Calculer les dimensions pour remplir l'écran en conservant les proportions
            double scaleX = (double) screenWidth / originalImage.getWidth();
            double scaleY = (double) screenHeight / originalImage.getHeight();
            double scale = Math.max(scaleX, scaleY); // Prendre le plus grand facteur pour remplir l'écran

            int newWidth = (int) (originalImage.getWidth() * scale);
            int newHeight = (int) (originalImage.getHeight() * scale);

            // Calculer la position pour centrer l'image
            int x = (screenWidth - newWidth) / 2;
            int y = (screenHeight - newHeight) / 2;

            // Dessiner l'image redimensionnée
            g2d.drawImage(originalImage.getBi(), x, y, newWidth, newHeight, null);
            g2d.dispose();

            logger.info("Image redimensionnée à " + screenWidth + "x" + screenHeight +
                    " (scale=" + scale + ", dimensions réelles=" + newWidth + "x" + newHeight + ")");

            return resizedImage;
        } catch (Exception e) {
            logger.severe("Erreur lors du redimensionnement de l'image: " + e.getMessage());
            e.printStackTrace();
            return originalImage; // Retourner l'image originale en cas d'erreur
        }
    }

    /**
     * Crée des points aux quatre coins de l'écran pour afficher une image en plein écran
     *
     * @return Liste de points positionnés aux coins de l'écran
     */
    /**
     * Crée des points aux quatre coins de l'écran pour afficher une image en plein écran
     *
     * @return Liste de points positionnés aux coins de l'écran
     */
    private List<Point> createFullScreenCornerPoints() {
        List<Point> points = new ArrayList<>();

        // Point en haut à gauche (0,0)
        Point topLeft = new Point();
        topLeft.setId("topLeft");
        topLeft.setX(0.0);
        topLeft.setY(0.0);
        topLeft.setVisible(true);
        points.add(topLeft);

        // Point en haut à droite (1,0)
        Point topRight = new Point();
        topRight.setId("topRight");
        topRight.setX(1.0);
        topRight.setY(0.0);
        topRight.setVisible(true);
        points.add(topRight);

        // Point en bas à droite (1,1)
        Point bottomRight = new Point();
        bottomRight.setId("bottomRight");
        bottomRight.setX(1.0);
        bottomRight.setY(1.0);
        bottomRight.setVisible(true);
        points.add(bottomRight);

        // Point en bas à gauche (0,1)
        Point bottomLeft = new Point();
        bottomLeft.setId("bottomLeft");
        bottomLeft.setX(0.0);
        bottomLeft.setY(1.0);
        bottomLeft.setVisible(true);
        points.add(bottomLeft);

        return points;
    }

    /**
     * Transforme des pointsGroup en fonction du type de transformation et de la progression
     *
     * @param pointsGroup    Points d'origine à transformer
     * @param transform Type de transformation à appliquer
     * @param progress  Progression de la transformation (0.0 à 1.0)
     * @return Nouveaux pointsGroup transformés
     */
    private List<Point> transformPointsBasedOnProgress(List<Point> pointsGroup, Transform transform, double progress) {
        if (pointsGroup == null || pointsGroup.isEmpty()) {
            return new ArrayList<>();
        }

        List<Point> transformedPoints = new ArrayList<>();

        // Copier d'abord tous les pointsGroup
        for (Point original : pointsGroup) {
            Point newPoint = new Point();
            newPoint.setId(original.getId());
            newPoint.setX(original.getX());
            newPoint.setY(original.getY());
            newPoint.setVisible(original.isVisible());
            if (original.getName() != null) {
                newPoint.setName(original.getName());
            }
            if (original.getColor() != null) {
                newPoint.setColor(original.getColor());
            }
            transformedPoints.add(newPoint);
        }

        // Puis appliquer les transformations en fonction du type
        if (transform instanceof TransformTranslate translateTransform) {
            double dx = translateTransform.getDx();
            double dy = translateTransform.getDy();

            for (Point point : transformedPoints) {
                point.setX(point.getX() + dx*progress);
                point.setY(point.getY() + dy*progress);
            }
            logger.info("Translation appliquée: dx=" + dx + ", dy=" + dy);
        } else if (transform instanceof TransformRotate rotateTransform) {
            double angle = rotateTransform.getAngle()*progress;

            // Calculer le centre de rotation (moyenne des coordonnées)
            double centerX = 0.5; // Centre de l'écran par défaut
            double centerY = 0.5;

            // Si un point central est spécifié dans la transformation, l'utiliser
            if (rotateTransform.getCx() != 0 || rotateTransform.getCy() != 0) {
                centerX = rotateTransform.getCx();
                centerY = rotateTransform.getCy();
            }

            // Convertir l'angle en radians
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);

            // Appliquer la rotation à chaque point
            for (Point point : transformedPoints) {
                // Translater au centre
                double x = point.getX() - centerX;
                double y = point.getY() - centerY;

                // Appliquer la rotation
                double newX = x * cos - y * sin + centerX;
                double newY = x * sin + y * cos + centerY;

                point.setX(newX);
                point.setY(newY);
            }
            logger.info("Rotation appliquée: angle=" + angle + "° autour de (" + centerX + "," + centerY + ")");
        } else if (transform instanceof TransformScale scaleTransform) {
            double scaleX = 1.0 + (scaleTransform.getCx() - 1.0);
            double scaleY = 1.0 + (scaleTransform.getCy() - 1.0);

            // Calculer le centre de mise à l'échelle (généralement le centre de l'image)
            double centerX = 0.5;
            double centerY = 0.5;

            // Appliquer la mise à l'échelle à chaque point
            for (Point point : transformedPoints) {
                // Calculer la distance par rapport au centre
                double dx = point.getX() - centerX;
                double dy = point.getY() - centerY;

                // Appliquer la mise à l'échelle
                double newX = centerX + dx * scaleX*progress;
                double newY = centerY + dy * scaleY*progress;

                point.setX(newX);
                point.setY(newY);
            }
            logger.info("Mise à l'échelle appliquée: scaleX=" + scaleX + ", scaleY=" + scaleY);
        }
        logger.info("Points transformés: " + transformedPoints.size());
        return transformedPoints;
    }

    /**
     * Vérifie et journalise l'état des images associées aux groupes
     *
     * @param configurationJson Configuration contenant les groupes
     * @param imageIds          Map des images identifiées par URL
     * @return Nombre d'images valides trouvées
     */
    private int verifyTransformImages(ConfigurationJson configurationJson, Map<Integer, List<Image>> imageIds, int transformId) {
        return 0;
    }

    /**
     * Traite une transformation de type TransformDetachImage
     *
     * @param transformDetachImage La transformation à appliquer
     * @param configurationJson    La configuration JSON contenant les groupes et points
     * @return
     */
    private Group processDetachImageTransform(TransformDetachImage transformDetachImage,
                                              ConfigurationJson configurationJson) {
        final Group[] g = {null};
        // Détacher l'image uniquement du groupe cible spécifié
        configurationJson.getGroups().stream()
                .filter(group -> transformDetachImage.getTargetId().equals(group.getId()))
                .forEach(new Consumer<Group>() {
                    @Override
                    public void accept(Group group) {
                        g[0] = group;
                    }
                });
        return g[0];

    }

    /**
     * Traite une transformation de type TransformAttachImage
     * Associe une image téléchargée à un groupe, la fait remplir tout l'écran initialement,
     * et prépare les points correspondants pour les transformations ultérieures
     *
     * @param transformAttachImage La transformation à appliquer
     * @param configurationJson    La configuration JSON contenant les groupes et points
     * @param imageIds             Map des images identifiées par URL
     * @param image1
     */
    private void processAttachImageTransform(TransformAttachImage transformAttachImage,
                                             ConfigurationJson configurationJson,
                                             Map<Integer, List<Image>> imageIds,
                                             int transformNo, Image image1) {

    }

    /**
     * Télécharge un fichier depuis une URL et le sauvegarde localement
     * Version moderne avec try-with-resources
     */
    private File downloadFileFromUrl(String urlString, Path tempDir, String fileName) throws IOException {
        try {
            // Création de l'URL avec encodage des espaces
            String encodedUrl = urlString.replace(" ", "%20");
            URL resource = new URL(encodedUrl);

            logger.info("Téléchargement du fichier depuis: " + encodedUrl);

            // Création du fichier de destination
            File destinationFile = new File(tempDir.toFile(), fileName);

            // Téléchargement avec gestion automatique des ressources
            try (InputStream inputStream = resource.openStream();
                 FileOutputStream outputStream = new FileOutputStream(destinationFile)) {

                // Copie efficace du contenu
                long bytesTransferred = inputStream.transferTo(outputStream);

                logger.info("Fichier téléchargé avec succès: " + fileName +
                        " (" + bytesTransferred + " octets)");

                return destinationFile;
            }

        } catch (IOException e) {
            logger.severe("Erreur lors du téléchargement depuis " + urlString + ": " + e.getMessage());
            throw new IOException("Impossible de télécharger le fichier depuis " + urlString, e);
        }
    }

    /**
     * Lit une image à partir d'une URL GCS, en supportant les URL signées.
     * Utilise une approche similaire à uploadToCloudStorageVideoFile pour la gestion des identifiants.
     *
     * @param urlString L'URL de l'image stockée dans Google Cloud Storage (signée ou non)
     * @return L'image lue depuis l'URL
     * @throws IOException Si l'image ne peut pas être lue
     */
    private Image readImageFromGcsUrl(String urlString) throws IOException {
        Logger logger = Logger.getLogger(getClass().getCanonicalName());

        // Première tentative - lecture directe depuis l'URL fournie
        try {
            URL url = new URL(urlString);
            Image image = new Image(ImageIO.read(url));
            if (image != null) {
                logger.info("Image lue avec succès depuis l'URL directe: " + urlString);
                return image;
            }
        } catch (IOException e) {
            logger.warning("Impossible de lire l'image directement depuis l'URL: " + e.getMessage());
        }

        // Deuxième tentative - si l'URL est un chemin GCS, créer une URL signée
        if (urlString.startsWith("gs://")) {
            try {
                // Initialiser le client Storage
                Storage storage = StorageOptions.newBuilder()
                        .setProjectId("studio-6v2lo")
                        .build()
                        .getService();

                // Extraire le nom du bucket et le chemin du fichier
                String bucketName = urlString.substring(5, urlString.indexOf("/", 5));
                String objectName = urlString.substring(urlString.indexOf("/", 5) + 1);

                // Obtenir le Blob
                BlobId blobId = BlobId.of(bucketName, objectName);
                Blob blob = storage.get(blobId);

                if (blob == null) {
                    throw new IOException("Le fichier n'existe pas dans GCS: " + urlString);
                }

                // Générer une URL signée valide pendant 1 heure
                URL signedUrl;
                try {
                    // Essayer d'abord avec les identifiants par défaut
                    signedUrl = blob.signUrl(1, TimeUnit.HOURS, Storage.SignUrlOption.withV4Signature());
                    logger.info("URL correctement signée pour la lecture de l'image: " + signedUrl);
                } catch (Exception ex) {
                    logger.warning("Impossible de générer l'URL signée avec les identifiants par défaut: " + ex.getMessage());

                    // Chemin vers le fichier JSON de compte de service
                    String credentialPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
                    if (credentialPath == null || credentialPath.isEmpty()) {
                        logger.info("Variable GOOGLE_APPLICATION_CREDENTIALS non définie, utilisation du chemin par défaut");
                        // Utiliser un chemin par défaut si la variable n'est pas définie
                        credentialPath = "c:\\Users\\manue\\AppData\\Local\\gcloud\\application_default_credentials.json";
                    }

                    // Charger les identifiants du compte de service à partir du fichier JSON
                    InputStream credentialsStream = new FileInputStream(credentialPath);
                    GoogleCredentials credentials = ServiceAccountCredentials.fromStream(credentialsStream);

                    // Vérifier que les identifiants implémentent ServiceAccountSigner
                    if (credentials instanceof ServiceAccountSigner signer) {
                        signedUrl = blob.signUrl(1, TimeUnit.HOURS,
                                Storage.SignUrlOption.signWith(signer),
                                Storage.SignUrlOption.withV4Signature());
                        logger.info("URL correctement signée (2ème tentative) pour la lecture de l'image: " + signedUrl);
                    } else {
                        throw new IllegalArgumentException("Les identifiants ne supportent pas la signature");
                    }
                }

                // Lire l'image depuis l'URL signée
                Image image = new Image(ImageIO.read(signedUrl));
                if (image != null) {
                    logger.info("Image lue avec succès depuis l'URL signée");
                    return image;
                } else {
                    throw new IOException("Impossible de lire l'image depuis l'URL signée: " + signedUrl);
                }
            } catch (Exception e) {
                logger.severe("Erreur lors de la génération/utilisation de l'URL signée: " + e.getMessage());
                for (StackTraceElement element : e.getStackTrace()) {
                    logger.severe(element.toString());
                }
                throw new IOException("Erreur lors de la lecture de l'image depuis GCS: " + e.getMessage(), e);
            }
        }

        // Si aucune des tentatives n'a fonctionné, lancer une exception
        throw new IOException("Impossible de lire l'image depuis l'URL: " + urlString);
    }


    /**
     * Récupère tous les points d'un groupe donné
     */
    public static List<Point> getPointsFromGroup(Group group, List<Point> allPoints) {
        List<Point> groupPoints = new ArrayList<>();

        for (String pointId : group.getPointIds()) {
            Point point = findPointById(pointId, allPoints);
            if (point != null) {
                groupPoints.add(point);
            }
        }

        return groupPoints;
    }

    /**
     * Trouve un point par son ID
     */
    private static Point findPointById(String id, List<Point> points) {
        return points.stream()
                .filter(point -> id.equals(point.getId()))
                .findFirst()
                .orElse(null);
    }

    /**
     * Sélectionne spécifiquement les points de coin par leur nom
     */
    public static Map<String, Point> getCornerPoints(Group group, List<Point> allPoints) {
        Map<String, Point> cornerPoints = new HashMap<>();
        String[] cornerNames = {"topLeft", "topRight", "bottomLeft", "bottomRight"};

        List<Point> groupPoints = getPointsFromGroup(group, allPoints);

        for (Point point : groupPoints) {
            if (Arrays.asList(cornerNames).contains(point.getName())) {
                cornerPoints.put(point.getName(), point);
            }
        }

        return cornerPoints;
    }

    /**
     * Vérifie si un groupe contient tous les points de coin
     */
    public static boolean hasAllCornerPoints(Group group, List<Point> allPoints) {
        Map<String, Point> corners = getCornerPoints(group, allPoints);
        return corners.size() == 4 &&
                corners.containsKey("topLeft") &&
                corners.containsKey("topRight") &&
                corners.containsKey("bottomLeft") &&
                corners.containsKey("bottomRight");
    }

    /**
     * Récupère les points de coin par leur nom précis (topLeft, topRight, bottomLeft, bottomRight)
     * Cette méthode garantit l'association correcte des noms aux points
     *
     * @param group Groupe contenant les identifiants de points
     * @param allPoints Liste complète des points disponibles
     * @return Map associant chaque nom de coin à son point correspondant
     */
    public static Map<String, Point> getNamedCornerPoints(Group group, List<Point> allPoints) {
        Map<String, Point> cornerPoints = new HashMap<>();

        // Trouver les points du groupe
        List<Point> groupPoints = getPointsFromGroup(group, allPoints);

        // Parcourir les points et les ajouter à la map par leur nom
        for (Point point : groupPoints) {
            if (point.getName() != null) {
                String name = point.getName();
                // Vérifier si le nom correspond à un des coins
                if (name.equals("topLeft") || name.equals("topRight") ||
                        name.equals("bottomLeft") || name.equals("bottomRight")) {
                    cornerPoints.put(name, point);
                }
            }
        }


        return cornerPoints;
    }
}