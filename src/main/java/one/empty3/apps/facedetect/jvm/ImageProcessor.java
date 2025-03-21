package one.empty3.apps.facedetect.jvm;


import com.google.gson.*;
import one.empty3.library.objloader.E3Model;
import one.empty3.libs.Image;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ImageProcessor  {

    private final Gson gson = new Gson();
    private Image result;
    Image image1; E3Model model;Image image3; String txt1; String txt2; String txt3; boolean hd_texture; int selected_algorithm;
    boolean isBezier;
    private boolean isRunning;
    private int count;
EditPolygonsMappings editPolygonsMappings;
    public ImageProcessor(Image image1, E3Model model,Image image3, String txt1, String txt2, String txt3, boolean hd_texture, int selected_algorithm,
                          boolean isBezier) {

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

    public void run() {
        try {
            editPolygonsMappings = new EditPolygonsMappings();
            editPolygonsMappings.loadImage1(image1);
            editPolygonsMappings.loadImage3(image3);
            editPolygonsMappings.model = model;
            editPolygonsMappings.loadTxtData(txt1, 0);
            editPolygonsMappings.loadTxtData(txt2, 1);
            editPolygonsMappings.loadTxtData(txt3, 2);
            editPolygonsMappings.hdTextures = hd_texture;
            switch (selected_algorithm) {
                case 1:
                    editPolygonsMappings.distanceABClass = DistanceProxLinear1.class;
                    break;
                case 2:
                    editPolygonsMappings.distanceABClass = DistanceProxLinear2.class;
                    break;
                case 3:
                    editPolygonsMappings.distanceABClass = DistanceProxLinear3.class;
                    break;
                case 4:
                    editPolygonsMappings.distanceABClass = DistanceProxLinear4.class;
                    break;
                case 5:
                    editPolygonsMappings.distanceABClass = DistanceProxLinear5.class;
                    break;
                case 43:
                    editPolygonsMappings.distanceABClass = DistanceProxLinear43.class;
                    break;
                case 44:
                    editPolygonsMappings.distanceABClass = DistanceProxLinear44.class;
                    break;
                default:
                    editPolygonsMappings.distanceABClass = DistanceIdent.class;
                    break;
            }
            editPolygonsMappings.typeShape = DistanceAB.TYPE_SHAPE_QUADR;//!Objects.equals(data.get("selected_texture_type"), "Bezier texture") ?  : DistanceAB.TYPE_SHAPE_BEZIER;

            Thread runApp = new Thread(editPolygonsMappings);
            runApp.start();

            while (editPolygonsMappings.zBufferImage == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    editPolygonsMappings.isRunning = false;
                    this.isRunning = false;
                }
            }
        } catch (RuntimeException e) {
            editPolygonsMappings.isRunning = false;
            this.isRunning = false;
            throw e;
        }
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Image getImage() {
        if(editPolygonsMappings!=null && editPolygonsMappings.zBufferImage!=null)
            count++;
        if(count ==2)
            return new Image(editPolygonsMappings.zBufferImage);
        return null;
    }
}