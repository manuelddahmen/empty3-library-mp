package one.empty3.apps.facedetect.jvm;


import one.empty3.library.objloader.E3Model;
import one.empty3.libs.Image;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ImageProcessor  implements Runnable {

    Image image1; E3Model model;Image image3; String txt1; String txt2; String txt3; boolean hd_texture; int selected_algorithm;
    boolean isBezier;
    private boolean isRunning;
    EditPolygonsMappings editPolygonsMappings;
    private Image image;

    public ImageProcessor(Image image1, E3Model model,Image image3, String txt1, String txt2, String txt3, boolean hd_texture, int selected_algorithm,
                          boolean isBezier) {
        try {
            this.image1 = image1;
            this.model = model;
            this.image3 = image3;
            this.txt1 = txt1;
            this.txt2 = txt2;
            this.txt3 = txt3;
            this.hd_texture = hd_texture;
            this.selected_algorithm = selected_algorithm;
            this.isBezier = isBezier;
        } catch (RuntimeException e) {
            Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "unknown 1 error", e);
        }
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
            }
            if(editPolygonsMappings.zBufferImage!=null)
                this.setImage(editPolygonsMappings.zBufferImage);
        } catch (RuntimeException e) {
            Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "unknown 2 (run) error", e);
            editPolygonsMappings.isRunning = false;
            this.isRunning = false;
        }
    }

    private void setImage(Image zBufferImage) {
        this.image = zBufferImage;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public Image getImage() {
        return image;
    }
}