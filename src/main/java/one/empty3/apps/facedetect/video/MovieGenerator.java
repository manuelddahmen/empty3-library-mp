package one.empty3.apps.facedetect.video;

import one.empty3.apps.facedetect.jvm.TextureMorphMove;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.logging.Logger;

/**
 * Classe qui génère un fichier vidéo MPEG à partir d'un fichier texte et de deux images
 */
public class MovieGenerator {
    private static final Logger logger = Logger.getLogger(MovieGenerator.class.getName());
    private final List<FileType> fileTypes;
    private File outputFile;

    /**
     * Constructeur par défaut
     */
    public MovieGenerator(List<FileType> types, File outputFile) {
        this.outputFile = outputFile;
        this.fileTypes = types;

        for (FileType type : types) {
            logger.info("Type : " + type.type());
            logger.info(type.filename());
            logger.info(outputFile.getName());


        }
    }

    public boolean generateMovie() {
        return true;
    }
}
