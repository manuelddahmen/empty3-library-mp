package one.empty3.apps.facedetect.video;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import java.awt.*;
import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

import one.empty3.libs.Image;

import javax.imageio.ImageIO;

/**
 * Classe qui génère un fichier vidéo MPEG à partir d'un fichier texte et de deux images
 */
public class MovieGenerator2 {
    private static final int RES_AVG = 100;
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
                try {
                    File f = fileType.file();
                    String content = Files.readString(f.toPath());
                    String[] split = content.split("\n");
                    String currentGroup = "default";
                    for (int lineNumber = 0; lineNumber < split.length; lineNumber++) {
                        String line = split[lineNumber];
                        logger.info(line);
                        if (content.toLowerCase().equals("next")) {
                            images.add(currentImageFrame);
                            currentImageFrame = new Image(RES_AVG, RES_AVG);
                            currentGroup = "default";
                            mapPoint.clear();

                        } else if (content.toLowerCase().startsWith("group ")) {
                            currentGroup = line.substring("group ".length());

                        } else if (content.toLowerCase().startsWith("point ")) {
                            AtomicBoolean atomicBoolean = new AtomicBoolean(false);
                            NamedPoint namedPoint = readPoint(List.of(split), lineNumber, atomicBoolean);
                            if (namedPoint != null) {
                                mapPoint.put(currentGroup, namedPoint);
                                lineNumber += 3;
                                if (atomicBoolean.get()) {
                                    currentGroup = null;
                                }
                            }
                        } else if (content.toLowerCase().startsWith("endofgroup")) {
                            currentGroup = null;
                        }
                    }
                } catch (IOException e) {
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
        configurationJson.getTransforms().forEach(
                transform -> {
                    try {
                        if (transform instanceof TransformAttachImage transformAttachImage &&  transformAttachImage.getImageUrl()!=null && !transformAttachImage.getImageUrl().isEmpty()) {
                            Image image1 = readImageFromGcsUrl(transformAttachImage.getImageUrl());
                            if (image1 != null) {
                                if (imageIds.containsKey(transformIndex[0])) {
                                    imageIds.computeIfAbsent(transformIndex[0], k -> new ArrayList<>());
                                    imageIds.get(transformIndex[0]).add(image1);
                                }
                                imageGroupIds.computeIfAbsent(transform.getTargetId(), k -> new HashMap<>());
                                imageGroupIds.get(transform.getTargetId()).put(transformIndex[0], image1);
                            }

                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    transformIndex[0]++;
                });
        // Vérifier les images associées aux groupes avant de commencer le traitement
/*        if (configurationJson != null && configurationJson.getGroups() != null) {
            logger.info("Vérification des images associées aux groupes avant traitement...");
            int validImages = verifyGroupImages(configurationJson, imageIds, transformIndex[0]);
            logger.info(validImages + " images valides trouvées pour " + configurationJson.getGroups().size() + " groupes");

        }
*/
        logger.info(configurationJson.toString());
        currentFrameIndex = 0; // Remplace l'ancienne variable i2

        Image[][]  allImagesSets = new Image[configurationJson.getGroups().size()][configurationJson.getTransforms().size()];
        logger.info("groups : "+configurationJson.getGroups().size());
        logger.info("transforms : "+configurationJson.getTransforms().size());
        int countImages = 0;
        for (int i = 0; i < configurationJson.getGroups().size(); i++) {
            for (int j = 0; j < configurationJson.getTransforms().size(); j++) {
                Group g = configurationJson.getGroups().get(i);
                Transform t = configurationJson.getTransforms().get(j);
                if (t instanceof TransformAttachImage transformAttachImage && transformAttachImage.getTargetId().equals(g.getId())) {
                    String imageUrl = transformAttachImage.getImageUrl();
                    Image image1;
                    if(imageUrl!=null && !imageUrl.isEmpty()) {
                        try {
                            image1 = readImageFromGcsUrl(transformAttachImage.getImageUrl());
                            allImagesSets[i][j] = image1;
                            countImages++;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }else if (t instanceof TransformDetachImage transformDetachImage  && transformDetachImage.getTargetId().equals(g.getId())) {
                    allImagesSets[i][j] = null;
                } else if(j>0) {
                    allImagesSets[i][j] = allImagesSets[i][j-1];
                } else
                    allImagesSets[i][j] = null;
            }

        }
        logger.severe("Aucunes images trouvées pour " + configurationJson.getGroups().size() + " groupes");

        transformIndex[0] = 0;
        // Traiter chaque transformation définie dans le fichier de configuration
        if (configurationJson != null && configurationJson.getTransforms() != null) {
            for (int i = 0; i < configurationJson.getTransforms().size(); i++) {
                Transform transform = configurationJson.getTransforms().get(i);

                logger.info("Traitement de la transformation " + transform.getClass().getSimpleName());

                // Récupérer le nombre de frames pour cette transformation
                int transformFrames = transform.getFrames();
                int currentTransformFrame = 0;
                // Stocker la transformation actuelle pour référence dans les autres méthodes
                currentTransform = transform;

                // Traiter chaque frame de la transformation
                for (int j = 0; j < transformFrames; j++) {
                    currentImageFrame = new Image(RES_AVG, RES_AVG);
                    double progress = 1.0*currentTransformFrame/transformFrames;
                    // Calculer le pourcentage de progression de cette transformation
                    double transformProgress = (double) j / Math.max(1, transformFrames - 1);
                     /*
                   logger.info("Transformation " + transform.getClass().getSimpleName() + " - frame " + j + "/" + transformFrames +
                            " (" + Math.round(transformProgress * 100) + "%)");
                     */

                    List<Group> groups = configurationJson.getGroups();
                    for (int k = 0; k < groups.size(); k++) {
                        Group g = groups.get(k);
                        Image image1 = allImagesSets[k][i];
                        // Déterminer le type de transformation et appliquer l'effet approprié
                             if (transform instanceof TransformTranslate || transform instanceof TransformScale || transform instanceof TransformScale || transform instanceof TransformRotate) {
                            ConfigurationJson finalConfigurationJson = configurationJson;
                            int finalFrameIndex = currentFrameIndex;
                            String groupId = transform.getTargetId();

                            List<Point> points = finalConfigurationJson.getAnimation().get(finalFrameIndex);
                            if (points != null && points.size() > 0) {
                                for (int p = 0; p < configurationJson.getPoints().size(); p++) {
                                    Point pointPoint = configurationJson.getPoints().get(p);
                                    if (configurationJson.getAnimation().get(finalFrameIndex).get(p).getId() == pointPoint.getId()) {
                                        pointPoint.setX(points.get(p).getX());
                                        pointPoint.setY(points.get(p).getY());
                                    }
                                }
                            }
                            for (Point point : configurationJson.getPoints()) {

                            }
                            transformPointsBasedOnProgress(points, transform, progress, image1);
                        } else if (transform instanceof TransformSetVisibility transformSetVisibility) { // TODO: implement
                            g.setVisible(transformSetVisibility.isVisible());
                        } else if (transform instanceof TransformMorph transformMorph) {
                            Group sourceGroup = configurationJson.getGroups().stream().filter(
                                    group -> group.getId().equals(transformMorph.getSourceGroupId())
                            ).findFirst().get();
                            Group destinationGroup = configurationJson.getGroups().stream().filter(
                                    group -> group.getId().equals(transformMorph.getTargetGroupId())
                            ).findFirst().get();

                            ConfigurationJson finalConfigurationJson = configurationJson;
                            int finalFrame2 = frame;
                            configurationJson.getPoints().replaceAll((UnaryOperator<Point>) point -> {
                                finalConfigurationJson.getAnimation().stream().filter(points -> points.get(finalFrame2).getId().equals(point.getId()));
                                return point;
                            });
                        }
                        if (image1 != null && image1.getBi()!=null) {
                            if (g.isVisible()) {
                                drawImage(g, configurationJson);
                                try {
                                    Graphics g2d = currentImageFrame.getBi().getGraphics();
                                    Image image2 = resizeImageToFillScreen(image1);
                                    g2d.drawImage(image2.getBi(), 0, 0, image2.getBi().getWidth(), image2.getBi().getHeight(), null);
                                } catch (RuntimeException ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                logger.info("Image " + g.getId() + " invisible, ne pas dessiner");
                            }
                        }
                    }
                    // Passer à la frame suivante
                    frame = frame + 1;
                    totalFramesCount ++;
                    currentFrameIndex = frame - 1; // Mettre à jour l'index de frame courant
                    images.add(currentImageFrame);
                }
                transformIndex[0]++;
            }
        }
    }


    /**
     * Dessine une image avec les éléments visuels (points, groupes) configurés
     *
     * @param g La groupe à dessiner
     * @param configurationJson La configuration contenant les points et groupes à dessiner
     */
    private void drawImage(Group g, ConfigurationJson configurationJson) {
        if (currentImageFrame == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) currentImageFrame.getBi().getGraphics();

        // Améliorer la qualité du rendu
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Fond blanc pour l'image
        g2d.setColor(Color.WHITE);
        //g2d.fillRect(0, 0, currentImage.getBi().getWidth(), currentImage.getBi().getHeight());

        // Dessiner tous les points visibles
        for (String pointId : g.getPointIds()) {
            for (int i = 0; i < configurationJson.getPoints().size(); i++) {
                Point point = configurationJson.getPoints().get(i);
                if (point.getId().equals(pointId)) {
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
                            g2d.setColor(Color.BLACK);
                            g2d.drawString(point.getName(), x + pointSize, y);
                        }

                    }
                }
            }

        }
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
    public NamedPoint readPoint(List<String> lines, int lineNumber, AtomicBoolean endOfGroup) {
        // Vérifier que nous avons assez de lignes pour lire un point complet
        if (lines == null || lineNumber < 0 || lineNumber + 3 >= lines.size()) {
            logger.fine("Pas assez de lignes pour lire un point à la ligne " + lineNumber);
            return null;
        }

        // Vérifier que la ligne actuelle n'est pas vide
        String currentLine = lines.get(lineNumber);
        if (currentLine == null || currentLine.isEmpty()) {
            logger.fine("Ligne vide à " + lineNumber);
            return null;
        }

        // Vérifier que la ligne commence par une lettre et n'est pas un mot-clé réservé
        String lowerCaseLine = currentLine.toLowerCase();
        if (!Character.isAlphabetic(lowerCaseLine.charAt(0)) ||
                lowerCaseLine.startsWith("group ") ||
                lowerCaseLine.startsWith("next")) {
            logger.fine("Ligne " + lineNumber + " n'est pas un début de point: " + currentLine);
            return null;
        }

        try {
            // Lire le nom du point et ses coordonnées
            NamedPoint namedPoint = new NamedPoint();
            namedPoint.setName(currentLine.trim());

            // Coordonnée X
            String xLine = lines.get(lineNumber + 1);
            if (xLine == null || xLine.isEmpty()) {
                logger.warning("Coordonnée X manquante pour le point " + currentLine + " à la ligne " + (lineNumber + 1));
                return null;
            }
            double x = Double.parseDouble(xLine);

            // Coordonnée Y
            String yLine = lines.get(lineNumber + 2);
            if (yLine == null || yLine.isEmpty()) {
                logger.warning("Coordonnée Y manquante pour le point " + currentLine + " à la ligne " + (lineNumber + 2));
                return null;
            }
            double y = Double.parseDouble(yLine);

            // Vérifier que la ligne suivante est vide (séparateur)
            String separatorLine = lines.get(lineNumber + 3);
            if (separatorLine == null || separatorLine.isBlank()) {
                namedPoint.setX(x);
                namedPoint.setY(y);
                logger.fine("Point " + namedPoint.getName() + " lu avec succès: (" + x + ", " + y + ")");
                return namedPoint;
            } else {
                if (separatorLine.toLowerCase().equals("endofgroup")) {
                    namedPoint.setX(x);
                    namedPoint.setY(y);
                    endOfGroup.set(true);
                    return namedPoint;
                }
                logger.warning("Format incorrect: la ligne séparatrice après les coordonnées n'est pas vide: " + separatorLine);
                return null;
            }
        } catch (NumberFormatException ex) {
            logger.warning("Erreur de format des coordonnées pour le point à la ligne " + lineNumber + ": " + ex.getMessage());
            return null;
        } catch (RuntimeException ex) {
            logger.warning("Erreur lors de la lecture du point à la ligne " + lineNumber + ": " + ex.getMessage());
            return null;
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
            int screenHeight = Math.min(RES_AVG , 1080); // Par défaut 1080 ou 4x RES_AVG

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
     * Transforme des points en fonction du type de transformation et de la progression
     *
     * @param points    Points d'origine à transformer
     * @param transform Type de transformation à appliquer
     * @param progress  Progression de la transformation (0.0 à 1.0)
     * @param image1
     * @return Nouveaux points transformés
     */
    private List<Point> transformPointsBasedOnProgress(List<Point> points, Transform transform, double progress, Image image1) {
        if (points == null || points.isEmpty()) {
            return new ArrayList<>();
        }

        List<Point> transformedPoints = new ArrayList<>();

        // Copier d'abord tous les points
        for (Point original : points) {
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
        if (transform instanceof TransformTranslate) {
            TransformTranslate translateTransform = (TransformTranslate) transform;
            double dx = translateTransform.getDx() * progress;
            double dy = translateTransform.getDy() * progress;

            for (Point point : transformedPoints) {
                point.setX(point.getX() + dx);
                point.setY(point.getY() + dy);
            }
            logger.fine("Translation appliquée: dx=" + dx + ", dy=" + dy);
        } else if (transform instanceof TransformRotate) {
            TransformRotate rotateTransform = (TransformRotate) transform;
            double angle = rotateTransform.getAngle() * progress;

            // Calculer le centre de rotation (moyenne des coordonnées)
            double centerX = 0.5; // Centre de l'écran par défaut
            double centerY = 0.5;

            // Si un point central est spécifié dans la transformation, l'utiliser
            if (rotateTransform.getCx() != 0 || rotateTransform.getCy() != 0) {
                centerX = rotateTransform.getCx();
                centerY = rotateTransform.getCy();
            }

            // Convertir l'angle en radians
            double angleRad = Math.toRadians(angle);
            double cos = Math.cos(angleRad);
            double sin = Math.sin(angleRad);

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
            logger.fine("Rotation appliquée: angle=" + angle + "° autour de (" + centerX + "," + centerY + ")");
        } else if (transform instanceof TransformScale) {
            TransformScale scaleTransform = (TransformScale) transform;
            double scaleX = 1.0 + (scaleTransform.getCx() - 1.0) * progress;
            double scaleY = 1.0 + (scaleTransform.getCy() - 1.0) * progress;

            // Calculer le centre de mise à l'échelle (généralement le centre de l'image)
            double centerX = 0.5;
            double centerY = 0.5;

            // Appliquer la mise à l'échelle à chaque point
            for (Point point : transformedPoints) {
                // Calculer la distance par rapport au centre
                double dx = point.getX() - centerX;
                double dy = point.getY() - centerY;

                // Appliquer la mise à l'échelle
                double newX = centerX + dx * scaleX;
                double newY = centerY + dy * scaleY;

                point.setX(newX);
                point.setY(newY);
            }
            logger.fine("Mise à l'échelle appliquée: scaleX=" + scaleX + ", scaleY=" + scaleY);
        }

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
     * @param transformAttachImage       La transformation à appliquer
     * @param configurationJson          La configuration JSON contenant les groupes et points
     * @param imageIds                   Map des images identifiées par URL
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
     * Reads an image from a URL, handling authenticated access for private Google Cloud Storage objects.
     * @param urlString The URL of the image. Can be a public URL or a GCS URL.
     * @return An Image object, or null if the URL is empty.
     * @throws IOException If the image cannot be read or downloaded.
     */
    private Image readImageFromGcsUrl(String urlString) throws IOException {
        if (urlString == null || urlString.trim().isEmpty()) {
            return null;
        }

        // Ensure URL is properly encoded before creating a URI
        URI uri = URI.create(urlString.replace(" ", "%20"));
        String host = uri.getHost();

        // Check if this is a Google Cloud Storage URL that requires authenticated access
        if (host != null && (host.equals("storage.googleapis.com") || host.equals("firebasestorage.googleapis.com"))) {
            logger.info("Reading private image from GCS: " + urlString);

            String path = uri.getPath(); // e.g., /my-bucket/my-folder/image.jpg
            if (path.startsWith("/")) {
                path = path.substring(1);
            }

            String bucketName;
            String objectName;

            // Handle different GCS URL formats
            if (host.equals("storage.googleapis.com")) {
                // Format: storage.googleapis.com/BUCKET_NAME/OBJECT_NAME
                int firstSlash = path.indexOf('/');
                if (firstSlash == -1) {
                    throw new IllegalArgumentException("Invalid GCS URL format. Expected https://storage.googleapis.com/bucket/object. Got: " + urlString);
                }
                bucketName = path.substring(0, firstSlash);
                objectName = path.substring(firstSlash + 1);
            } else { // firebasestorage.googleapis.com
                // Format: firebasestorage.googleapis.com/v0/b/BUCKET_NAME/o/OBJECT_NAME...
                String prefix = "v0/b/";
                if (!path.startsWith(prefix)) {
                    throw new IllegalArgumentException("Invalid Firebase Storage URL format. Got: " + urlString);
                }
                path = path.substring(prefix.length());
                int bucketEnd = path.indexOf('/');
                bucketName = path.substring(0, bucketEnd);

                String objectPrefix = "/o/";
                int objectStart = path.indexOf(objectPrefix);
                if (objectStart == -1) {
                    throw new IllegalArgumentException("Invalid Firebase Storage URL format. Could not find object part. Got: " + urlString);
                }
                objectName = path.substring(objectStart + objectPrefix.length());
            }

            BlobId blobId = BlobId.of(bucketName, objectName);
            byte[] content = storage.readAllBytes(blobId);

            try (InputStream is = new ByteArrayInputStream(content)) {
                return new Image(ImageIO.read(is));
            }
        } else {
            // Fallback for standard public URLs
            logger.info("Reading public image from URL: " + urlString);
            return new Image(ImageIO.read(uri.toURL()));
        }
    }
}