package one.empty3.apps.facedetect.video;

import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;
import java.awt.image.BufferedImage;
import java.io.File;
import one.empty3.libs.Image;

public class JavaCVEncoder {

    public void encodeImagesToVideo(String outputFile, int frameRate, Image... imageFiles) throws Exception {
        
        // Charger la première image pour obtenir les dimensions
        Image firstImage = imageFiles[0];//ImageIO.read(imageFiles[0]);
        int width = firstImage.getWidth();
        int height = firstImage.getHeight();

        // Initialiser l'enregistreur de frames FFmpeg
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, width, height);
        recorder.setVideoCodecName("libx264"); // Codec H.264
        recorder.setFormat("mp4");
        recorder.setFrameRate(frameRate);
        recorder.setPixelFormat(org.bytedeco.ffmpeg.global.avutil.AV_PIX_FMT_YUV420P);

        recorder.start();

        // Convertisseur pour passer de BufferedImage à la structure de Frame de JavaCV
        Java2DFrameConverter converter = new Java2DFrameConverter();

        for (Image imageFile : imageFiles) {
            BufferedImage bImage = imageFile.getBi();
            recorder.record(converter.getFrame(bImage));
        }

        // Arrêter et libérer les ressources
        recorder.stop();
        recorder.release();
    }
}