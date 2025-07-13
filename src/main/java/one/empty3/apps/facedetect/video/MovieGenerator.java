package one.empty3.apps.facedetect.video;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

import one.empty3.library.Point2D;
import one.empty3.library.Point3D;
import one.empty3.library.core.tribase.Config;
import one.empty3.library.core.tribase.PointWire;
import one.empty3.libs.Image;
/**
 * Classe qui génère un fichier vidéo MPEG à partir d'un fichier texte et de deux images
 */
public class MovieGenerator {
    private final HashMap<String, NamedPoint> mapPoint = new HashMap<>();

    public class NamedPoint extends Point3D {
        protected String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
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

        if(outputFile.exists()) {
            for (FileType type : types) {
                logger.info("Type : " + type.type());
                logger.info(type.filename());
                logger.info(outputFile.getName());


            }
        }
        types.forEach(fileType -> {
            if(fileType.type().equals("txt")) {
                Image currentImage = new one.empty3.libs.Image(1000, 1000);
                try {
                    File f = new File(fileType.filename());
                    String content = Files.readString(f.toPath());
                    String[] split = content.split("\n");
                    String currentGroup = "default";
                    for(int lineNumber=0; lineNumber<split.length; lineNumber++) {
                        String line = split[lineNumber];
                        logger.info(line);
                        if(content.toLowerCase().equals("next")) {
                            images.add(currentImage);
                            currentImage = new one.empty3.libs.Image(1000, 1000);
                            drawImage(currentImage);
                            currentGroup = "default";
                            mapPoint.clear();

                        } else if(content.toLowerCase().startsWith("group ")) {
                                currentGroup = line.substring("group ".length());

                        } else {
                            NamedPoint namedPoint = readPoint(List.of(split), lineNumber);
                            if(namedPoint != null) {
                                mapPoint.put(currentGroup, namedPoint);
                                lineNumber+=3;
                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
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
        if(!(lines.size()>lineNumber-3 && !lines.get(lineNumber).isEmpty())) {
            return null;
        }
        if(Character.isAlphabetic(lines.get(lineNumber).toLowerCase().charAt(lineNumber))
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
}