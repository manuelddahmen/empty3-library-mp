package one.empty3.apps.facedetect.video;


import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.logging.Logger;
import java.util.stream.Stream;

import one.empty3.library.Point2D;
import one.empty3.library.Point3D;
import one.empty3.library.core.tribase.Config;
import one.empty3.library.core.tribase.PointWire;
import one.empty3.library.objloader.E3Model;
import one.empty3.libs.Image;

import javax.imageio.ImageIO;

/**
 * Classe qui génère un fichier vidéo MPEG à partir d'un fichier texte et de deux images
 */
public class MovieGenerator {
    private final HashMap<String, NamedPoint> mapPoint = new HashMap<>();


    private static final Logger logger = Logger.getLogger(MovieGenerator.class.getName());
    private final List<FileType> fileTypes;
    private File outputFile;
    List<Image> images = new ArrayList<>();

    /**
     * Constructeur par défaut
     */
    public MovieGenerator(List<FileType> types, File outputFile) {
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
                logger.info(type.filename());
                logger.info(outputFile.getName());


            }
        }
        ConfigurationJson configurationJson = null;
        for (FileType fileType : types) {
            if (fileType.type().equals("txt")) {
                Image currentImage = new Image(1000, 1000);
                try {
                    File f = new File(fileType.filename());
                    String content = Files.readString(f.toPath());
                    String[] split = content.split("\n");
                    String currentGroup = "default";
                    for (int lineNumber = 0; lineNumber < split.length; lineNumber++) {
                        String line = split[lineNumber];
                        logger.info(line);
                        if (content.toLowerCase().equals("next")) {
                            images.add(currentImage);
                            currentImage = new Image(1000, 1000);
                            drawImage(currentImage);
                            currentGroup = "default";
                            mapPoint.clear();

                        } else if (content.toLowerCase().startsWith("group ")) {
                            currentGroup = line.substring("group ".length());

                        } else {
                            NamedPoint namedPoint = readPoint(List.of(split), lineNumber);
                            if (namedPoint != null) {
                                mapPoint.put(currentGroup, namedPoint);
                                lineNumber += 3;
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            if (fileType.type().equals("json")) {
                configurationJson = ConfigurationJson.parseJson(new File(fileType.filename()));
            }

        }

        int frame = 1;

        final Image[] currentImage = {new Image(1000, 1000)};

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

        int i2 = 0;


        if (configurationJson.getTransforms() != null) {
            for (int i = 0; i < configurationJson.getTransforms().size(); i++) {
                Transform transform = configurationJson.getTransforms().get(i);
                currentImage[0] = new Image(1000, 1000);
                frames = transform.getFrames();
                boolean b = true;
                for (int j = 0; j < frames || b; j++) {
                    b = false;
                    if (transform instanceof TransformAttachImage transformAttachImage) {
                        if (transformAttachImage.getTargetType().equals(Transform.TargetType.All)) {
                            ConfigurationJson finalConfigurationJson2 = configurationJson;
                            configurationJson.getGroups().stream().filter(new Predicate<Group>() {
                                @Override
                                public boolean test(Group group) {
                                    return group.getId().equals("default");
                                }
                            }).forEach(new Consumer<Group>() {
                                @Override
                                public void accept(Group group) {
                                    group.setImageUrl(transformAttachImage.getImageUrl());
                                    Image attachedNow;
                                    if (group.isVisible()) {
                                        attachedNow = new Image(imageIds.get(group.getImageUrl()));
                                    }
                                    List<Point> d2 = new ArrayList<>();
                                    for (int k = 0; k < finalConfigurationJson2.getGroups().size(); k++) {
                                        Group kg = finalConfigurationJson2.getGroups().get(k);
                                        if (kg.getId().equals(group.getId())) {
                                            for (int p = 0; p < group.getPointsId().size(); p++) {
                                                for (int p1 = 0; p1 < finalConfigurationJson2.getPoints().size(); p1++) {
                                                    if (finalConfigurationJson2.getPoints().get(p1).getId().equals(group.getPointsId().get(p).getId())) {
                                                        Point p2 = new Point();
                                                        p2.setId(group.getId());
                                                        p2.setImageUrl(group.getImageUrl());
                                                        p2.setVisible(group.isVisible());
                                                        p2.setX(group.getPointsId().get(p).getX());
                                                        p2.setY(group.getPointsId().get(p).getY());
                                                        d2.add(p2);

                                                    }
                                                }
                                                copyAttachedTimeCordinates.put(kg.getId(), d2);
                                            }
                                        }
                                    }
                                }
                            });
                        } else {
                            configurationJson.getGroups().stream().filter(new Predicate<Group>() {
                                @Override
                                public boolean test(Group group) {
                                    return transformAttachImage.getTargetId().equals(group.getId());
                                }
                            }).forEach(new Consumer<Group>() {
                                @Override
                                public void accept(Group group) {
                                    group.setImageUrl(transformAttachImage.getImageUrl());

                                }
                            });

                        }
                    } else if (transform instanceof TransformDetachImage transformDetachImage) {
                        if (transformDetachImage.getTargetType().equals(Transform.TargetType.All)) {
                            configurationJson.getGroups().stream().filter(new Predicate<Group>() {
                                @Override
                                public boolean test(Group group) {
                                    return group.getId().equals("default");
                                }
                            }).forEach(new Consumer<Group>() {
                                @Override
                                public void accept(Group group) {
                                    group.setImageUrl(null);

                                }
                            });
                        } else {
                            configurationJson.getGroups().stream().filter(new Predicate<Group>() {
                                @Override
                                public boolean test(Group group) {
                                    return transformDetachImage.getTargetId().equals(group.getId());
                                }
                            }).forEach(new Consumer<Group>() {
                                @Override
                                public void accept(Group group) {
                                    group.setImageUrl(null);

                                }
                            });

                        }
                    } else if (transform instanceof TransformTranslate transformTranslate) {
                            if (transformTranslate.getTargetType().equals(Transform.TargetType.All)) {
                                for (int i1 = 0; i1 < configurationJson.getPoints().size(); i1++) {
                                    ConfigurationJson finalConfigurationJson1 = configurationJson;
                                    int finalI = i2;
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
                                int finalFrame1 = frame;
                                configurationJson.getPoints().replaceAll((UnaryOperator<Point>) point -> {
                                    finalConfigurationJson.getAnimation().stream().filter(points -> points.get(finalFrame1).getId().equals(point.getId()));
                                    return point;
                                });
                            }
                    } else if (transform instanceof TransformRotate transformRotate) { // TODO: implement
                            if (transformRotate.getTargetType().equals(Transform.TargetType.All)) {
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
                            } else if (transformRotate.getTargetType().equals(Transform.TargetType.Group)) {
                                ConfigurationJson finalConfigurationJson = configurationJson;
                                int finalFrame2 = frame;
                                configurationJson.getPoints().replaceAll((UnaryOperator<Point>) point -> {
                                    finalConfigurationJson.getAnimation().stream().filter(points -> points.get(finalFrame2).getId().equals(point.getId()));
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

                    }

                }
                Graphics graphics = currentImage[0].getGraphics();

                ConfigurationJson finalConfigurationJson3 = configurationJson;
                copyAttachedTimeCordinates.forEach(new BiConsumer<String, List<Point>>() {
                    @Override
                    public void accept(String s, List<Point> points) {
                        List<Point> points1 = points;
                        final Image[] image1 = {null};
                        Stream<Group> groupStream1 = finalConfigurationJson3.getGroups().stream().filter(group -> group.getId().equals(s));
                        List points3 = groupStream1.toList();
                        List<Group> point2 = finalConfigurationJson3.getGroups().stream().filter(new Predicate<Group>() {
                            @Override
                            public boolean test(Group group) {
                                if (group.getId().equals(s)) {

                                    try {
                                        image1[0] = new Image(ImageIO.read(new URL(s)));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                        return false;
                                    }
                                    return true;
                                }
                                return false;
                            }
                        }).toList();
                        List<Point> points2 = points3;
                        Image image3 = image1[0];

                        RunZBuffer runZBuffer = null;
                        try {
                            List<Point> points31 = new ArrayList<>();
                            for(int a = 0; a < points3.size(); a++)
                                points31.add((Point) points3.get(a));
                            runZBuffer = new RunZBuffer(image1[0],new E3Model(new BufferedReader(new FileReader(new File("resources/models/plane blender2.obj"))), true, "resources/models/plane blender2.obj"), image3, list2string(points1), list2string(points2),  list2string(points31), true, 6, true, true, new HashMap<>());
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                        Image image = runZBuffer.processImage();

                        graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), null);
                    }
                });
                frame = frame + 1;
            }

        }
    }


    private void drawImage(Image currentImage) {


    }

    public boolean generateMovie() {
        // 1. Générer les images de la vidéo (cette partie dépend de votre logique métier)
        // Supposons que les images sont générées dans un répertoire temporaire
        Path imagesDir;

        Image[] array = new Image[images.size()];
        for (int i = 0; i < images.size(); i++) {
            array[i] = images.get(i);
        }
        try {
            imagesDir = Files.createTempDirectory("movie-images-");
            // ... Votre logique pour créer les images ici ...
            // Par exemple: createVideoFrames(imagesDir);
            new JCodecImageToVideoEncoder().encodeImagesToVideo(outputFile, array);
        } catch (IOException e) {
            logger.severe("Erreur lors de la création du répertoire d'images: " + e.getMessage());
            return false;
        }

        // 2. Utiliser FFMpeg pour assembler les images en vidéo


        logger.info("La génération de la vidéo est terminée.");

        return true;
    }

    public NamedPoint readPoint(List<String> lines, int lineNumber) {
        if (!(lines.size() > lineNumber - 3 && !lines.get(lineNumber).isEmpty())) {
            return null;
        }
        if (Character.isAlphabetic(lines.get(lineNumber).toLowerCase().charAt(lineNumber))
                && !lines.get(lineNumber).toLowerCase().startsWith("group ")
                && !lines.get(lineNumber).toLowerCase().startsWith("next")
        ) {
            try {
                NamedPoint namedPoint = new NamedPoint();
                namedPoint.setName(lines.get(lineNumber).trim());
                double x = Double.parseDouble(lines.get(lineNumber + 1));
                double y = Double.parseDouble(lines.get(lineNumber + 2));
                String ret = lines.get(lineNumber + 3);
                if (ret.isBlank()) {
                    namedPoint.setX(x);
                    namedPoint.setY(y);
                    return namedPoint;
                } else {
                    return null;
                }
            } catch (RuntimeException ex) {

            }
        }
        return null;
    }

    public void createVideoFrames(Path imagesDir) {

    }

    public String list2string(List points) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < points.size(); i++) {
            Point point = (Point) points.get(i);
            sb.append(((Point) points.get(i)).getId() + "\n");
            sb.append(((Point) points.get(i)).getX() + "\n");
            sb.append(((Point) points.get(i)).getY() + "\n");
            sb.append("\n");

        }
        return sb.toString();
    }
}