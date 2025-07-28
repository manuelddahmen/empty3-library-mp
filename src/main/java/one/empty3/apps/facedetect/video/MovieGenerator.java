package one.empty3.apps.facedetect.video;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;

import one.empty3.library.objloader.E3Model;
import one.empty3.libs.Image;

import javax.imageio.ImageIO;

/**
 * Classe qui génère un fichier vidéo MPEG à partir d'un fichier texte et de deux images
 */
public class MovieGenerator {
    private static final int RES_AVG = 100;
    private final HashMap<String, NamedPoint> mapPoint = new HashMap<>();


    private static final Logger logger = Logger.getLogger(MovieGenerator.class.getName());
    private final List<FileType> fileTypes;
    private File outputFile;
    List<Image> images = new ArrayList<>();
    private Transform currentTransform; // Pour stocker la transformation en cours

    /**
     * Constructeur par défaut
     */
    public MovieGenerator(List<FileType> types, File outputFile, ConfigurationJson configurationJson, Path tempDir) {
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

        Image currentImage = new Image(RES_AVG, RES_AVG);


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
                            images.add(currentImage);
                            currentImage = new Image(RES_AVG, RES_AVG);
                            drawImage(currentImage, configurationJson);
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


        Map<String, Image> imageGroups = new HashMap<>();
        List<Image> framesImage = new ArrayList<>();
        int frames = 1;
        Map<String, List<Point>> copyAttachedTimeCordinates = new HashMap<>();
        Map<String, Image> imageIds = new HashMap<>();

        configurationJson.getTransforms().stream().filter(new Predicate<Transform>() {
            @Override
            public boolean test(Transform transform) {
                if (transform instanceof TransformAttachImage transformAttachImage) {
                    try {
                        imageGroups.put(transformAttachImage.getImageUrl(), new Image(ImageIO.read(new URL(transformAttachImage.getImageUrl()))));
                        return true;
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                return false;
            }
        });

        int currentFrameIndex = 0; // Remplace l'ancienne variable i2

        // Vérifier les images associées aux groupes avant de commencer le traitement
        if (configurationJson != null && configurationJson.getGroups() != null) {
            logger.info("Vérification des images associées aux groupes avant traitement...");
            int validImages = verifyGroupImages(configurationJson, imageIds);
            logger.info(validImages + " images valides trouvées pour " + configurationJson.getGroups().size() + " groupes");
        }

        // Traiter chaque transformation définie dans le fichier de configuration
        if (configurationJson != null && configurationJson.getTransforms() != null) {
            for (int i = 0; i < configurationJson.getTransforms().size(); i++) {
                Transform transform = configurationJson.getTransforms().get(i);
                logger.info("Traitement de la transformation " + transform.getClass().getSimpleName());
                currentImage = new Image(RES_AVG, RES_AVG);

                // Récupérer le nombre de frames pour cette transformation
                int transformFrames = transform.getFrames();

                // Stocker la transformation actuelle pour référence dans les autres méthodes
                currentTransform = transform;

                // Traiter chaque frame de la transformation
                for (int j = 0; j < transformFrames; j++) {
                    // Calculer le pourcentage de progression de cette transformation
                    double transformProgress = (double) j / Math.max(1, transformFrames - 1);
                    logger.info("Transformation " + transform.getClass().getSimpleName() + " - frame " + j + "/" + transformFrames +
                            " (" + Math.round(transformProgress * 100) + "%)");

                    // Déterminer le type de transformation et appliquer l'effet approprié
                    if (transform instanceof TransformAttachImage transformAttachImage) {
                        processAttachImageTransform(transformAttachImage, configurationJson, imageIds, copyAttachedTimeCordinates);
                    } else if (transform instanceof TransformDetachImage transformDetachImage) {
                        processDetachImageTransform(transformDetachImage, configurationJson);
                    } else if (transform instanceof TransformTranslate transformTranslate) {
                        if (transformTranslate.getTargetType().equals(Transform.TargetType.All)) {
                            for (int i1 = 0; i1 < configurationJson.getPoints().size(); i1++) {
                                ConfigurationJson finalConfigurationJson1 = configurationJson;
                                int finalI = currentFrameIndex;
                                configurationJson.getPoints().replaceAll(new UnaryOperator<Point>() {
                                    @Override
                                    public Point apply(Point point) {
                                        List<Point> points = finalConfigurationJson1.getAnimation().get(finalI);
                                        for (int i3 = 0; i3 < points.size(); i3++) {
                                            if (points.get(i3).getId().equals(point.getId())) {
                                                return points.get(i3);
                                            }
                                        }
                                        return point;
                                    }
                                });
                            }
                        } else if (transformTranslate.getTargetType().equals(Transform.TargetType.Group)) {
                            ConfigurationJson finalConfigurationJson = configurationJson;
                            int finalFrameIndex = currentFrameIndex;
                            configurationJson.getPoints().replaceAll((UnaryOperator<Point>) point -> {
                                finalConfigurationJson.getAnimation().stream().filter(points -> points.get(finalFrameIndex).getId().equals(point.getId()));
                                return point;
                            });
                        }
                    } else if (transform instanceof TransformRotate transformRotate) { // TODO: implement
                        if (transformRotate.getTargetType().equals(Transform.TargetType.All)) {
                            for (int i1 = 0; i1 < configurationJson.getPoints().size(); i1++) {
                                ConfigurationJson finalConfigurationJson1 = configurationJson;
                                int finalFrameIndex = currentFrameIndex;
                                configurationJson.getPoints().replaceAll(new UnaryOperator<Point>() {
                                    @Override
                                    public Point apply(Point point) {
                                        List<Point> points = finalConfigurationJson1.getAnimation().get(finalFrameIndex);
                                        for (int i3 = 0; i3 < points.size(); i3++) {
                                            if (points.get(i3).getId().equals(point.getId())) {
                                                return points.get(i3);
                                            }
                                        }
                                        return point;
                                    }
                                });
                            }
                        } else if (transformRotate.getTargetType().equals(Transform.TargetType.Group)) {
                            ConfigurationJson finalConfigurationJson = configurationJson;
                            int finalFrameIndex = currentFrameIndex;
                            configurationJson.getPoints().replaceAll((UnaryOperator<Point>) point -> {
                                finalConfigurationJson.getAnimation().stream().filter(points -> points.get(finalFrameIndex).getId().equals(point.getId()));
                                return point;
                            });
                        }
                    } else if (transform instanceof TransformSetVisibility transformSetVisibility) { // TODO: implement
                        if (transformSetVisibility.getTargetType().equals(Transform.TargetType.All)) {
                            configurationJson.getGroups().stream().filter(new Predicate<Group>() {
                                @Override
                                public boolean test(Group group) {
                                    return group.getId().equals("default");
                                }
                            }).forEach(new Consumer<Group>() {
                                @Override
                                public void accept(Group group) {
                                    return;

                                }
                            });
                        } else {
                            configurationJson.getGroups().stream().filter(new Predicate<Group>() {
                                @Override
                                public boolean test(Group group) {
                                    return transformSetVisibility.getTargetId().equals(group.getId());
                                }
                            }).forEach(new Consumer<Group>() {
                                @Override
                                public void accept(Group group) {
                                    return;

                                }
                            });

                        }

                    } else if (transform instanceof TransformScale transformScale) {
                        if (transformScale.getTargetType().equals(Transform.TargetType.All)) {
                            for (int i1 = 0; i1 < configurationJson.getPoints().size(); i1++) {
                                ConfigurationJson finalConfigurationJson1 = configurationJson;
                                int finalFrame = frame;
                                configurationJson.getPoints().replaceAll(new UnaryOperator<Point>() {
                                    @Override
                                    public Point apply(Point point) {
                                        List<Point> points = finalConfigurationJson1.getAnimation().get(finalFrame);
                                        for (int i3 = 0; i3 < points.size(); i3++) {
                                            if (points.get(i3).getId().equals(point.getId())) {
                                                return points.get(i3);
                                            }
                                        }
                                        return point;
                                    }
                                });
                            }
                        } else if (transformScale.getTargetType().equals(Transform.TargetType.Group)) {
                            ConfigurationJson finalConfigurationJson = configurationJson;
                            int finalFrame2 = frame;
                            configurationJson.getPoints().replaceAll((UnaryOperator<Point>) point -> {
                                finalConfigurationJson.getAnimation().stream().filter(points -> points.get(finalFrame2).getId().equals(point.getId()));
                                return point;
                            });
                        }
                    } else if (transform instanceof TransformMorph transformMorph) {
                        Group sourceGroup = configurationJson.getGroups().stream().filter(
                                group ->  group.getId().equals(transformMorph.getSourceGroupId())
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
                    Graphics graphics = currentImage.getBi().getGraphics();

                    ConfigurationJson finalConfigurationJson3 = configurationJson;
                    Image finalCurrentImage = currentImage;
                    copyAttachedTimeCordinates.forEach(new BiConsumer<String, List<Point>>() {
                        @Override
                        public void accept(String groupId, List<Point> points) {
                            logger.info("Traitement du groupe " + groupId + " avec " + points.size() + " points");

                            // Récupérer le groupe correspondant
                            List<Group> matchingGroups = finalConfigurationJson3.getGroups().stream()
                                    .filter(group -> group.getId().equals(groupId))
                                    .toList();

                            if (matchingGroups.isEmpty()) {
                                logger.warning("Groupe " + groupId + " non trouvé dans la configuration");
                                return;
                            }

                            Group currentGroup = matchingGroups.get(0);
                            String imageUrl = currentGroup.getImageId();

                            // Vérifier si une URL d'image est définie pour ce groupe
                            if (imageUrl == null || imageUrl.isEmpty()) {
                                logger.warning("Aucune URL d'image définie pour le groupe " + groupId);
                                return;
                            }

                            // Charger l'image associée au groupe
                            final Image[] groupImage = {null};
                            try {
                                // Vérifier d'abord si l'image est déjà dans imageIds
                                if (imageIds.containsKey(imageUrl)) {
                                    logger.info("Utilisation de l'image en cache pour " + imageUrl);
                                    groupImage[0] = imageIds.get(imageUrl);
                                } else {
                                    // Sinon, essayer de la charger depuis l'URL
                                    logger.info("Chargement de l'image depuis l'URL: " + imageUrl);
                                    groupImage[0] = new Image(ImageIO.read(new URL(imageUrl)));
                                    // Mettre en cache l'image pour une utilisation future
                                    imageIds.put(imageUrl, groupImage[0]);
                                }

                                if (groupImage[0] != null) {
                                    logger.info("Image chargée pour le groupe " + groupId + ": " +
                                            groupImage[0].getWidth() + "x" + groupImage[0].getHeight());
                                }
                            } catch (IOException e) {
                                logger.severe("Erreur lors du chargement de l'image " + imageUrl + ": " + e.getMessage());
                                e.printStackTrace();
                                return;
                            }

                            RunZBuffer runZBuffer = null;
                            try {
                                List<Point> points31 = new ArrayList<>();
                                for (int a = 0; a < points31.size(); a++)
                                    points31.add((Point) points31.get(a));
                                URL resource = URI.create("https://empty3.one/apps/plane blender2.obj").toURL();
                                String contentStr = resource.getContent().toString();
                                logger.info("URL CONTENT :\n"+contentStr);
                                File file = new File(tempDir.toFile(), "model-plane.obj");
                                runZBuffer = new RunZBuffer(image[0], new E3Model(new BufferedReader(new FileReader(file)), true, file.getAbsolutePath()), finalCurrentImage, list2string(points31), list2string(points31), list2string(points), true, 6, true, true, new HashMap<>());
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            image[0] = runZBuffer.processImage();

                            graphics.drawImage(image[0], 0, 0, image[0].getWidth(), image[0].getHeight(), null);
                        }
                    });
                    // Ajouter l'image traitée à la liste des frames
                    if (image[0] == null) {
                        if(currentImage!=null) {
                            image[0] = currentImage;
                        } else {
                            logger.warning("Frame " + frame + " non ajoutée car l'image est null");
                            // Ajouter une image vide pour maintenir la continuité
                            Image emptyImage = new Image(RES_AVG, RES_AVG);
                            Graphics g = emptyImage.getBi().getGraphics();
                            g.setColor(Color.WHITE);
                            g.fillRect(0, 0, RES_AVG, RES_AVG);
                            g.setColor(Color.RED);
                            g.drawString("Erreur frame " + frame, 10, RES_AVG / 2);
                            images.add(emptyImage);
                        }
                    }
                    if (image[0] != null) {
                        images.add(image[0]);
                        logger.info("Frame " + frame + " ajoutée: " +
                                (image[0] != null ? image[0].getWidth() + "x" + image[0].getHeight() : "null"));

                        // Optionnel: enregistrer chaque frame pour débogage
                        if (logger.isLoggable(java.util.logging.Level.FINE)) {
                            try {
                                File debugDir = new File("debug_frames");
                                if (!debugDir.exists()) {
                                    debugDir.mkdirs();
                                }

                                File frameFile = new File(debugDir, "frame_" + frame + ".png");
                                ImageIO.write(image[0].getBi(), "png", frameFile);
                                logger.fine("Frame " + frame + " enregistrée pour débogage: " + frameFile.getAbsolutePath());
                            } catch (Exception e) {
                                logger.warning("Impossible d'enregistrer la frame pour débogage: " + e.getMessage());
                            }
                        }
                    }

                    // Passer à la frame suivante
                    frame = frame + 1;
                    currentFrameIndex = frame - 1; // Mettre à jour l'index de frame courant
                }
            }

        }
    }


    /**
     * Dessine une image avec les éléments visuels (points, groupes) configurés
     *
     * @param currentImage      L'image sur laquelle dessiner
     * @param configurationJson La configuration contenant les points et groupes à dessiner
     */
    private void drawImage(Image currentImage, ConfigurationJson configurationJson) {
        if (currentImage == null || configurationJson == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) currentImage.getBi().getGraphics();

        // Améliorer la qualité du rendu
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // Fond blanc pour l'image
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, currentImage.getWidth(), currentImage.getHeight());

        // Dessiner tous les points visibles
        for (Point point : configurationJson.getPoints()) {
            if (point.isVisible()) {
                // Convertir les coordonnées normalisées (0-1) en pixels
                int x = (int) (point.getX() * currentImage.getWidth());
                int y = (int) (point.getY() * currentImage.getHeight());

                // Définir la couleur du point (utiliser une couleur par défaut si non spécifiée)
                try {
                    if (point.getColor() != null ) {
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

        // Dessiner les connexions entre les points des groupes
        for (Group group : configurationJson.getGroups()) {
            if (group.isVisible() && group.getPointIds() != null && group.getPointIds().size() > 1) {
                // Utiliser une couleur distincte pour chaque groupe
                g2d.setColor(new Color(group.getId().hashCode() & 0x00FFFFFF | 0xFF000000));
                g2d.setStroke(new BasicStroke(2));

                // Trouver les points du groupe
                List<Point> groupPoints = new ArrayList<>();
                for (String pointRefStr : group.getPointIds()) {
                    Point pointRef = null;
                    for (Point point : configurationJson.getPoints()) {
                        if (point.getId().equals(pointRefStr)) {
                            pointRef = point;
                            groupPoints.add(point);
                            break;
                        }
                    }
                    if (pointRef == null) {
                        logger.warning("Point non trouvé dans la configuration: " + pointRefStr);
                    }
                }

                // Dessiner les lignes entre les points consécutifs
                if (groupPoints.size() >= 2) {
                    for (int i = 0; i < groupPoints.size() - 1; i++) {
                        Point p1 = groupPoints.get(i);
                        Point p2 = groupPoints.get(i + 1);

                        int x1 = (int) (p1.getX() * currentImage.getWidth());
                        int y1 = (int) (p1.getY() * currentImage.getHeight());
                        int x2 = (int) (p2.getX() * currentImage.getWidth());
                        int y2 = (int) (p2.getY() * currentImage.getHeight());

                        g2d.drawLine(x1, y1, x2, y2);
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
            int screenWidth = Math.max(RES_AVG * 4, 1920); // Par défaut 1920 ou 4x RES_AVG
            int screenHeight = Math.max(RES_AVG * 4, 1080); // Par défaut 1080 ou 4x RES_AVG

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
     * @return Nouveaux points transformés
     */
    private List<Point> transformPointsBasedOnProgress(List<Point> points, Transform transform, double progress) {
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
    private int verifyGroupImages(ConfigurationJson configurationJson, Map<String, Image> imageIds) {
        if (configurationJson == null || configurationJson.getGroups() == null) {
            logger.warning("Configuration ou groupes null dans verifyGroupImages");
            return 0;
        }

        int validImages = 0;

        logger.info("=== Vérification des images associées aux groupes ===");

        for (Group group : configurationJson.getGroups()) {
            String imageUrl = group.getImageId();
            if (imageUrl == null || imageUrl.isEmpty()) {
                logger.info("Groupe " + group.getId() + ": aucune image associée");
                continue;
            }

            if (imageIds.containsKey(imageUrl)) {
                Image image = imageIds.get(imageUrl);
                if (image != null) {
                    validImages++;
                    logger.info("Groupe " + group.getId() + ": image valide trouvée (" +
                            image.getWidth() + "x" + image.getHeight() + ")");
                } else {
                    logger.warning("Groupe " + group.getId() + ": référence d'image null pour l'URL " + imageUrl);
                }
            } else {
                logger.warning("Groupe " + group.getId() + ": URL d'image non trouvée dans la map: " + imageUrl);

                // Tenter de charger l'image manquante
                try {
                    URL url = new URL(imageUrl);
                    Image newImage = new Image(ImageIO.read(url));
                    imageIds.put(imageUrl, newImage);
                    validImages++;
                    logger.info("Groupe " + group.getId() + ": image chargée avec succès: " +
                            newImage.getWidth() + "x" + newImage.getHeight());
                } catch (Exception e) {
                    logger.severe("Impossible de charger l'image pour le groupe " +
                            group.getId() + ": " + e.getMessage());
                }
            }
        }

        logger.info(validImages + " images valides trouvées sur " + configurationJson.getGroups().size() + " groupes");
        return validImages;
    }

    /**
     * Traite une transformation de type TransformDetachImage
     *
     * @param transformDetachImage La transformation à appliquer
     * @param configurationJson    La configuration JSON contenant les groupes et points
     */
    private void processDetachImageTransform(TransformDetachImage transformDetachImage,
                                             ConfigurationJson configurationJson) {
        if (transformDetachImage.getTargetType().equals(Transform.TargetType.All)) {
            // Détacher l'image de tous les groupes par défaut
            configurationJson.getGroups().stream()
                    .filter(group -> group.getId().equals("default"))
                    .forEach(group -> group.setImageId(null));
        } else {
            // Détacher l'image uniquement du groupe cible spécifié
            configurationJson.getGroups().stream()
                    .filter(group -> transformDetachImage.getTargetId().equals(group.getId()))
                    .forEach(group -> group.setImageId(null));
        }
    }

    /**
     * Traite une transformation de type TransformAttachImage
     * Associe une image téléchargée à un groupe, la fait remplir tout l'écran initialement,
     * et prépare les points correspondants pour les transformations ultérieures
     *
     * @param transformAttachImage       La transformation à appliquer
     * @param configurationJson          La configuration JSON contenant les groupes et points
     * @param imageIds                   Map des images identifiées par URL
     * @param copyAttachedTimeCordinates Map pour stocker les coordonnées temporelles attachées
     */
    private void processAttachImageTransform(TransformAttachImage transformAttachImage,
                                             ConfigurationJson configurationJson,
                                             Map<String, Image> imageIds,
                                             Map<String, List<Point>> copyAttachedTimeCordinates) {
        // Vérifier si l'URL de l'image est valide
        String imageUrl = transformAttachImage.getImageUrl();
        if (imageUrl == null || imageUrl.isEmpty()) {
            logger.warning("URL d'image non spécifiée dans la transformation AttachImage");
            return;
        }

        // Vérifier si l'image est disponible dans la map des images
        boolean imageAvailable = imageIds.containsKey(imageUrl);
        Image originalImage = null;

        if (!imageAvailable) {
            logger.warning("Image " + imageUrl + " non trouvée dans les images disponibles");

            // Tenter de charger l'image si elle n'est pas dans la map
            try {
                URL url = new URL(imageUrl);
                BufferedImage bufferedImage = ImageIO.read(url);
                if (bufferedImage == null) {
                    logger.severe("L'image chargée est null: " + imageUrl);
                    return;
                }

                // Sauvegarder l'image originale
                originalImage = new Image(bufferedImage);

                // Redimensionner l'image pour qu'elle remplisse l'écran
                Image resizedImage = resizeImageToFillScreen(originalImage);

                // Stocker à la fois l'image originale et l'image redimensionnée
                imageIds.put(imageUrl, resizedImage);
                imageIds.put(imageUrl + "_original", originalImage);

                imageAvailable = true;
                logger.info("Image " + imageUrl + " chargée et redimensionnée: " +
                        resizedImage.getWidth() + "x" + resizedImage.getHeight() +
                        " (originale: " + originalImage.getWidth() + "x" + originalImage.getHeight() + ")");
            } catch (Exception e) {
                logger.severe("Impossible de charger l'image " + imageUrl + ": " + e.getMessage());
                e.printStackTrace();
                return;
            }
        } else {
            logger.info("Image " + imageUrl + " déjà disponible dans la map des images");
            // Si l'image a déjà été chargée mais pas encore redimensionnée
            if (!imageIds.containsKey(imageUrl + "_original")) {
                originalImage = imageIds.get(imageUrl);
                imageIds.put(imageUrl + "_original", originalImage);
                Image resizedImage = resizeImageToFillScreen(originalImage);
                imageIds.put(imageUrl, resizedImage);
                logger.info("Image " + imageUrl + " redimensionnée: " +
                        resizedImage.getWidth() + "x" + resizedImage.getHeight());
            }
        }

        // Traiter les groupes selon le type de cible
        if (transformAttachImage.getTargetType() == null
                || transformAttachImage.getTargetType().equals(Transform.TargetType.All)) {
            // Appliquer la transformation à tous les groupes par défaut
            List<Group> defaultGroups = configurationJson.getGroups().stream()
                    .filter(group -> group.getId().equals("default"))
                    .toList();

            if (defaultGroups.isEmpty()) {
                logger.warning("Aucun groupe 'default' trouvé pour la transformation AttachImage");
                // Si aucun groupe par défaut n'existe, en créer un
                Group newDefaultGroup = new Group();
                newDefaultGroup.setId("default");
                newDefaultGroup.setVisible(true);
                newDefaultGroup.setImageId(imageUrl);

                // Créer des points aux 4 coins pour couvrir tout l'écran
                List<Point> cornerPoints = createFullScreenCornerPoints();
                List<String> pointIds = new ArrayList<>();

                for (Point point : cornerPoints) {
                    pointIds.add(point.getId());
                }
                newDefaultGroup.setPointIds(pointIds);

                for (Point point : cornerPoints) {
                    configurationJson.getPoints().add(point);

                }
                configurationJson.getGroups().add(newDefaultGroup);
                defaultGroups = List.of(newDefaultGroup);

                logger.info("Création d'un nouveau groupe 'default' avec l'image " + imageUrl);
            }

            for (Group group : defaultGroups) {
                // Sauvegarder l'ancienne URL pour journalisation
                String oldUrl = group.getImageId();

                // Mettre à jour l'URL de l'image du groupe
                group.setImageId(imageUrl);
                logger.info("Groupe " + group.getId() + ": URL d'image modifiée de " +
                        (oldUrl != null ? oldUrl : "<aucune>") + " à " + imageUrl);

                // Si le groupe n'a pas de points, ajouter des points aux coins pour couvrir tout l'écran
                if (group.getPointIds() == null || group.getPointIds().isEmpty()) {
                    List<Point> fullScreenCornerPoints = createFullScreenCornerPoints();
                    configurationJson.getPoints().addAll(createFullScreenCornerPoints());
                    List<String > pointsIds = new ArrayList<>();
                    for (int i = 0; i < fullScreenCornerPoints.size(); i++) {
                        pointsIds.add(fullScreenCornerPoints.get(i).getId());
                    }
                    group.getPointIds().addAll(pointsIds);
                    logger.info("Ajout de points aux coins pour le groupe " + group.getId());
                }

                // Collecter tous les points pour ce groupe
                List<Point> groupPoints = new ArrayList<>();

                // Vérifier que le groupe a des points
                if (group.getPointIds() == null || group.getPointIds().isEmpty()) {
                    logger.warning("Le groupe " + group.getId() + " n'a pas de points définis");
                    continue;
                }

                logger.info("Traitement de " + group.getPointIds().size() + " points pour le groupe " + group.getId());

                // Pour chaque point ID dans le groupe
                for (String pointIdStr : group.getPointIds()) {
                    Point pointId = null;
                    for (Point point : configurationJson.getPoints()) {
                        if (point.getId().equals(pointIdStr)) {
                            pointId = point;
                            break;
                        }
                    }
                    if (pointId == null || pointId.getId() == null) {
                        logger.warning("Point ID null détecté dans le groupe " + group.getId());
                        continue;
                    }

                    // Trouver le point correspondant dans la liste des points
                    Point matchingConfigPoint = null;
                    for (Point configPoint : configurationJson.getPoints()) {
                        if (configPoint.getId().equals(pointId.getId())) {
                            matchingConfigPoint = configPoint;
                            break;
                        }
                    }

                    if (matchingConfigPoint == null) {
                        logger.warning("Point " + pointId.getId() + " référencé dans le groupe " +
                                group.getId() + " non trouvé dans la configuration");
                        continue;
                    }

                    // Créer un nouveau point avec les propriétés du groupe et les coordonnées du point de configuration
                    Point newPoint = new Point();
                    newPoint.setId(group.getId() + "_" + pointId.getId()); // ID unique
                    newPoint.setVisible(group.isVisible() && matchingConfigPoint.isVisible());
                    newPoint.setX(pointId.getX() != 0 ? pointId.getX() : matchingConfigPoint.getX());
                    newPoint.setY(pointId.getY() != 0 ? pointId.getY() : matchingConfigPoint.getY());

                    // Ajouter les autres propriétés du point d'origine si elles existent
                    if (matchingConfigPoint.getName() != null) {
                        newPoint.setName(matchingConfigPoint.getName());
                    }
                    if (matchingConfigPoint.getColor() != null) {
                        newPoint.setColor(matchingConfigPoint.getColor());
                    }

                    groupPoints.add(newPoint);
                    logger.fine("Point ajouté: " + newPoint.getId() + " à " + newPoint.getX() + "," + newPoint.getY());
                }

                // Stocker les points collectés pour ce groupe
                if (!groupPoints.isEmpty()) {
                    copyAttachedTimeCordinates.put(group.getId(), groupPoints);
                    logger.info(group.getId() + ": " + groupPoints.size() + " points associés à l'image " + imageUrl);
                } else {
                    logger.warning("Aucun point valide trouvé pour le groupe " + group.getId());
                }
            }
        } else if (transformAttachImage.getTargetType() != null && transformAttachImage.getTargetType().equals(Transform.TargetType.Group)) {
            // Appliquer la transformation uniquement au groupe cible spécifié
            String targetGroupId = transformAttachImage.getTargetId();
            if (targetGroupId == null || targetGroupId.isEmpty()) {
                logger.warning("ID de groupe cible non spécifié pour la transformation AttachImage");
                return;
            }

            List<Group> targetGroups = configurationJson.getGroups().stream()
                    .filter(group -> targetGroupId.equals(group.getId()))
                    .toList();

            if (targetGroups.isEmpty()) {
                logger.warning("Groupe cible '" + targetGroupId + "' non trouvé pour la transformation AttachImage");
                return;
            }

            for (Group group : targetGroups) {
                String oldUrl = group.getImageId();
                group.setImageId(imageUrl);
                logger.info("Groupe cible " + group.getId() + ": URL d'image modifiée de " +
                        (oldUrl != null ? oldUrl : "<aucune>") + " à " + imageUrl);

                // Même traitement des points que pour le cas All, mais uniquement pour ce groupe cible
                List<Point> groupPoints = new ArrayList<>();

                if (group.getPointIds() != null && !group.getPointIds().isEmpty()) {
                    for (String pointIdStr : group.getPointIds()) {
                        Point pointId = null;
                        for (Point configPoint : configurationJson.getPoints()) {
                            if (configPoint.getId().equals(pointIdStr)) {
                                pointId = configPoint;
                                break;
                            }
                            if (pointId != null && pointId.getId() != null) {
                                for (Point configPoint1 : configurationJson.getPoints()) {
                                    if (configPoint1.getId().equals(pointId.getId())) {
                                        Point newPoint = new Point();
                                        newPoint.setId(group.getId() + "_" + pointId.getId());
                                        newPoint.setVisible(group.isVisible() && configPoint1.isVisible());
                                        newPoint.setX(pointId.getX() != 0 ? pointId.getX() : configPoint.getX());
                                        newPoint.setY(pointId.getY() != 0 ? pointId.getY() : configPoint.getY());

                                        if (configPoint1.getName() != null) {
                                            newPoint.setName(configPoint1.getName());
                                        }
                                        if (configPoint1.getColor() != null) {
                                            newPoint.setColor(configPoint1.getColor());
                                        }

                                        groupPoints.add(newPoint);
                                    }
                                }
                            }
                        }

                        if (!groupPoints.isEmpty()) {
                            copyAttachedTimeCordinates.put(group.getId(), groupPoints);
                            logger.info(group.getId() + ": " + groupPoints.size() + " points associés à l'image " + imageUrl);
                        }
                    }
                }
            }
        }
    }
}