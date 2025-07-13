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

        // Boucle sur chaque fichier image
        for (Image imageFile : imageFiles) {
            System.out.println("Encodage de l'image : ");
            
            // Charge l'image dans un BufferedImage
            BufferedImage image = imageFile.getBi();
            
            // Encode l'image dans la vidéo
            encoder.encodeImage(image);
        }

        // Finalise l'écriture du fichier vidéo
        encoder.finish();
        
        System.out.println("Vidéo sauvegardée dans : " + outputFile.getAbsolutePath());
    }

    public static void main(String[] args) {
        // Un exemple d'utilisation
        try {
            // Assurez-vous d'avoir des images nommées frame-0.png, frame-1.png, etc.
            // dans un dossier 'test_images' à la racine de votre projet.
            File image1 = new File("test_images/frame-0.png");
            File image2 = new File("test_images/frame-1.png");
            File image3 = new File("test_images/frame-2.png");

            Image image1Obj = new Image(image1);
            Image image2Obj = new Image(image2);
            Image image3Obj = new Image(image3);

            if(!image1.exists() || !image2.exists() || !image3.exists()) {
                 System.err.println("Veuillez créer des images de test (ex: test_images/frame-0.png)");
                 return;
            }

            JCodecImageToVideoEncoder encoder = new JCodecImageToVideoEncoder();
            encoder.encodeImagesToVideo(new File("output.mp4"), image1Obj, image2Obj, image3Obj);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
