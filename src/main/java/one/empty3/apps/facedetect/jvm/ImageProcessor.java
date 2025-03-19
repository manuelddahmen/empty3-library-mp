package one.empty3.apps.facedetect.jvm;


import com.google.cloud.storage.HttpMethod;
import com.google.gson.*;
import com.google.protobuf.ByteString;
import one.empty3.library.objloader.E3Model;
import one.empty3.libs.Image;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ImageProcessor  {

    private final Gson gson = new Gson();
    private final Image result;

    public ImageProcessor(Image image1, E3Model model,Image image3, String txt1, String txt2, String txt3, String hd_texture, String selected_algorithm, String selected_texture_type) {
        HashMap<String, byte[]> data = new HashMap<>();

        byte [] image1Bytes = null;
        byte [] image2Bytes = null;
        byte [] txt1Bytes = null;
        byte [] txt2Bytes = null;
        byte [] txt3Bytes = null;
        byte [] hd_textureBytes = null;
        byte [] selected_algorithmBytes = null;
        byte [] selected_texture_typeBytes = null;

        if (image1 != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new Image(image1).toOutputStream(byteArrayOutputStream);
            image1Bytes = byteArrayOutputStream.toByteArray();
            data.put("image1", image1Bytes);
        }
        if (model != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            data.put("model", byteArrayOutputStream.toByteArray());
        }
        if (image3 != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            new Image(image3).toOutputStream( byteArrayOutputStream);
            byte [] image = byteArrayOutputStream.toByteArray();
            image1Bytes = byteArrayOutputStream.toByteArray();
            data.put("image3", image);
        }
        if (txt1 != null) {
            txt1Bytes = txt1!=null?txt1.getBytes(StandardCharsets.UTF_8):null;
            txt2Bytes = txt2!=null?txt2.getBytes(StandardCharsets.UTF_8):null;
            txt3Bytes = txt3!=null?txt3.getBytes(StandardCharsets.UTF_8):null;
            data.put("textFile1", txt1Bytes);
            data.put("textFile2", txt2Bytes);
            data.put("textFile3", txt3Bytes);

        }
        if (hd_texture != null) {
            hd_textureBytes = hd_texture!=null?hd_texture.getBytes(StandardCharsets.UTF_8):null;
            data.put("hd_texture", hd_textureBytes);
        }
        if (selected_algorithm != null) {
            selected_algorithmBytes = selected_algorithm!=null?selected_algorithm.getBytes(StandardCharsets.UTF_8):null;
            data.put("selected_algorithm", selected_algorithmBytes);
        }
        one.empty3.apps.facedetect.jvm.ProcessData processData = new one.empty3.apps.facedetect.jvm.ProcessData(data);
        Thread thread = new Thread(processData);
        thread.start();
        Image result = null;
        while (processData.isRunning() && result == null) {
            result = processData.getImage();
        }
        this.result = result;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        ImageProcessor that = (ImageProcessor) o;
        return gson.equals(that.gson) && Objects.equals(result, that.result);
    }

    @Override
    public int hashCode() {
        int result1 = gson.hashCode();
        result1 = 31 * result1 + Objects.hashCode(result);
        return result1;
    }

    public Image getResult() {
        return result;
    }
}