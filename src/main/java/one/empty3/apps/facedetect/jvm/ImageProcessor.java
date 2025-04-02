package one.empty3.apps.facedetect.jvm;


import one.empty3.library.objloader.E3Model;
import one.empty3.libs.Image;

import java.io.ByteArrayOutputStream;
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
                case 0:
                    editPolygonsMappings.distanceABClass = DistanceProxLinear1.class;
                    break;
                case 1:
                    editPolygonsMappings.distanceABClass = DistanceProxLinear2.class;
                    break;
                case 2:
                    editPolygonsMappings.distanceABClass = DistanceProxLinear3.class;
                    break;
                case 3:
                    editPolygonsMappings.distanceABClass = DistanceProxLinear4.class;
                    break;
                case 4:
                    editPolygonsMappings.distanceABClass = DistanceProxLinear5.class;
                    break;
                case 5:
                    editPolygonsMappings.distanceABClass = DistanceProxLinear43.class;
                    break;
                case 6:
                    editPolygonsMappings.distanceABClass = DistanceProxLinear44.class;
                    break;
                case 7:
                    editPolygonsMappings.distanceABClass = DistanceIdent.class;
                    break;
                default:
                    return;
            }
            editPolygonsMappings.typeShape = isBezier?DistanceAB.TYPE_SHAPE_BEZIER:DistanceAB.TYPE_SHAPE_QUADR;//!Objects.equals(data.get("selected_texture_type"), "Bezier texture") ?  : DistanceAB.TYPE_SHAPE_BEZIER;
            editPolygonsMappings.testHumanHeadTexturing.setMaxFrames(200);

            Thread runApp = new Thread(editPolygonsMappings);


            final int[] phase = {0};


            runApp.start();


            while(editPolygonsMappings.iTextureMorphMove.distanceAB==null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }


            editPolygonsMappings.iTextureMorphMove.distanceAB.addFinishInitListener(new FinishInitListener() {
                @Override
                public void fire() {
                    phase[0] = 1;
                }
            });

            while(phase[0]==0) {
                Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Compute texture ...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Compute texture ... DONE");

            while ((editPolygonsMappings.testHumanHeadTexturing.zBufferImage()==null
                    && editPolygonsMappings.isRunning
                    && isBlankImage(editPolygonsMappings.testHumanHeadTexturing.zBufferImage()))
                    || editPolygonsMappings.testHumanHeadTexturing.frame()<=3) {
                Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Running ImageProcessor wait loop ...");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {
                    ignored.printStackTrace();
                }

                if(editPolygonsMappings.testHumanHeadTexturing.zBufferImage()!=null) {
                    Logger.getLogger(this.getClass().getCanonicalName()).log(Level.INFO, "Running ImageProcessor wait loop ... DONE");
                    setImage(editPolygonsMappings.testHumanHeadTexturing.zBufferImage());
                    editPolygonsMappings.testHumanHeadTexturing.loop(false);
                    editPolygonsMappings.testHumanHeadTexturing.stop();
                }
                }
            if((editPolygonsMappings.testHumanHeadTexturing.zBufferImage())!=(null)) {
                setImage(editPolygonsMappings.testHumanHeadTexturing.zBufferImage());
                editPolygonsMappings.stopThreadDisplay();
            }
            editPolygonsMappings.testHumanHeadTexturing.stop();
            editPolygonsMappings.testHumanHeadTexturing.setMaxFrames(0);
            editPolygonsMappings.stopRenderer();
            editPolygonsMappings.isRunning = false;


        } catch (RuntimeException e) {
            Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "unknown 2 (run) error", e);
        }
        editPolygonsMappings.isRunning = false;
        this.isRunning = false;
    }
    public void stopAll() {

        editPolygonsMappings.stopRenderer();
        editPolygonsMappings.stopThreadDisplay();
        Image image2 = editPolygonsMappings.testHumanHeadTexturing.zBufferImage();
        if(image2!=(null)) {
            setImage(editPolygonsMappings.testHumanHeadTexturing.zBufferImage());
        }
        editPolygonsMappings.testHumanHeadTexturing.stop();
        editPolygonsMappings.testHumanHeadTexturing.setMaxFrames(0);
        editPolygonsMappings.stopRenderer();
        editPolygonsMappings.isRunning = false;

    }
    private boolean isBlankImage(Image zBufferImage) {
        if(image==null) return true;

        int c = image.getRgb(0, 0);
        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if(image.getRgb(i,j)!=c)
                    return false;
            }
        }
        return true;
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
    public byte[] getResultMapImage() {
        Image image2 = getImage();
        if(image2!=null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (image2.toOutputStream(byteArrayOutputStream)) {
                return byteArrayOutputStream.toByteArray();
            }
        }
        return null;
        }
    }