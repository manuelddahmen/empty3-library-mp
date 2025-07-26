package one.empty3.apps.facedetect.video;

import org.jcodec.api.awt.AWTSequenceEncoder;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import one.empty3.libs.Image;

public class JCodecImageToVideoEncoder {

    public void encodeImagesToVideo(File outputFile, Image... imageFiles) throws IOException {
        // Crée l'encodeur de séquence vidéo
        // La vidéo sera créée à 25 images par seconde
        AWTSequenceEncoder encoder = AWTSequenceEncoder.createSequenceEncoder(outputFile, 25);

        for (int i = 0; i < 25; i++) {
            encoder.encodeImage(new Image(100, 100).getBi());
        }

        // Boucle sur chaque fichier image
        for (Image imageFile : imageFiles) {
            //System.out.println("Encodage de l'image : ");
            
            // Charge l'image dans un BufferedImage
            BufferedImage image = imageFile.getBi();
            
            // Encode l'image dans la vidéo
            encoder.encodeImage(image);
        }

        // Finalise l'écriture du fichier vidéo
        encoder.finish();
        
        System.out.println("Vidéo sauvegardée dans : " + outputFile.getAbsolutePath()+ " Nombre de frames : "+imageFiles.length);
    }

    public static void main(String[] args) {
        // Un exemple d'utilisation
        try {
            // Assurez-vous d'avoir des images nommées frame-0.png, frame-1.png, etc.
            // dans un dossier 'test_images' à la racine de votre projet.
            Image imageObj = new Image(new File("resources/img/2018-03-31 11.51_edited.jpg"));

            JCodecImageToVideoEncoder encoder = new JCodecImageToVideoEncoder();
            encoder.encodeImagesToVideo(new File("output.mp4"), imageObj, imageObj, imageObj);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
