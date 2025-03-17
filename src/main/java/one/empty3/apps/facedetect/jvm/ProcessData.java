package one.empty3.apps.facedetect.jvm;

import one.empty3.library.objloader.E3Model;
import one.empty3.libs.Image;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

public class ProcessData implements Runnable {
    boolean isRunning = true;
    EditPolygonsMappings editPolygonsMappings;
    Map<String, byte[]> data;
    private int compte = 0;

    /***
     * Constructor
     * @param data POST data encoded as String and Base64 for files
     */
    public ProcessData(Map<String, byte[]> data) {
        this.data = data;
    }
    public void run() {
        try {
            editPolygonsMappings = new EditPolygonsMappings();
            editPolygonsMappings.loadImageData1(Base64.getDecoder().decode(data.get("image1")));
            editPolygonsMappings.loadImageData3(Base64.getDecoder().decode(data.get("image3")));
            String modelDecoded = new String(Base64.getDecoder().decode(data.get("model")));
            editPolygonsMappings.model = new E3Model(new BufferedReader(new StringReader(modelDecoded)), false, null);
            editPolygonsMappings.loadTxtData(Arrays.toString(Base64.getDecoder().decode(data.get("textFile1"))), 0);
            editPolygonsMappings.loadTxtData(Arrays.toString(Base64.getDecoder().decode(data.get("textFile2"))), 1);
            editPolygonsMappings.loadTxtData(Arrays.toString(Base64.getDecoder().decode(data.get("textFile3"))), 2);
            editPolygonsMappings.hdTextures = Objects.equals(data.get("hd_texture"), "true");
            switch (Integer.parseInt(data.get("selected_algorithm").toString())+1) {
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
            data.get("token");

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
            compte++;
        if(compte==2)
            return new Image(editPolygonsMappings.zBufferImage);
        return null;
    }
}
