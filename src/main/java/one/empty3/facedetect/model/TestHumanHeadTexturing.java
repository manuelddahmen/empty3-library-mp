/*
 *
 *  *
 *  *  * Copyright (c) 2025. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2024 Manuel Daniel Dahmen
 *  *  *
 *  *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *    you may not use this file except in compliance with the License.
 *  *  *    You may obtain a copy of the License at
 *  *  *
 *  *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  *    Unless required by applicable law or agreed to in writing, software
 *  *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *    See the License for the specific language governing permissions and
 *  *  *    limitations under the License.
 *  *
 *  *
 *
 *
 *
 *  * Created by $user $date
 *
 *
 */

package one.empty3.facedetect.model;

import one.empty3.library.*;
import one.empty3.library.objloader.E3Model;
import one.empty3.libs.Image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestHumanHeadTexturing implements Runnable {
    public static Thread threadTest;
    private static TestHumanHeadTexturing instance = null;
    public double defautZheight = 0;
    public double defautZwidth = 0;
    private Rectangle rectangleFace;
    private BufferedImage trueFace;
    private BufferedImage jpgFile;
    protected BufferedImage jpgFileRight;
    private E3Model objFile;
    private EditPolygonsMappings editPolygonsMappings;
    protected ArrayList<BufferedImage> zBufferImages = new ArrayList<>();
    private Scene scene;
    ZBufferImpl zBufferImpl;

    public TestHumanHeadTexturing() {
        instance = this;
    }


    public void ginit() {
    }

    public void finit() {
        if (editPolygonsMappings == null)
            return;
        if (editPolygonsMappings.model != null) {
            setObj(editPolygonsMappings.model.model);
        }
        if (editPolygonsMappings.model.image != null) {
            setJpg(editPolygonsMappings.model.image);
        }

        z().setDisplayType(ZBufferImpl.DISPLAY_ALL);
        File intPart = new File("faceSkin.txt");
        PrintWriter printWriter;
        try {
            printWriter = new PrintWriter(intPart);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        Camera c = new Camera();


        if (jpgFile != null && objFile != null) {
            printWriter.println("# Face elements without eyes month and nose");
        /*AtomicInteger i = new AtomicInteger(0);
        ((RepresentableConteneur) (scene().getObjets().getElem(0))).
                getListRepresentable().forEach(representable -> {
                    int r = (int) Math.min((i.get() / (256)) % 256, 255);
                    int g = (int) Math.min(i.get() % (256 * 256), 255);
                    int b = i.get() % 256;
                    Color def = new Color(r, g, b);
                    if ((g < 222 && b > 16) || i.get() <= 221) {
                        def = java.awt.Color.BLACK;
                        printWriter.println(i);
                    } else {
                        def = Color.WHITE;
                    }
                    representable.setTexture(new ColorTexzture(def));
                    i.getAndIncrement();
                });*/
            printWriter.flush();
            printWriter.close();

        }
        scene().getObjets().getData1d().clear();
        if (editPolygonsMappings.model != null
                && !scene().getObjets().getData1d().contains(editPolygonsMappings.model.model)) {
            scene.add(editPolygonsMappings.model.model);
            addEyePolygons(scene, editPolygonsMappings.model.model);
        }
        if (editPolygonsMappings!=null&&editPolygonsMappings.model != null && editPolygonsMappings.model.image != null && editPolygonsMappings.model.textureWired) {
            editPolygonsMappings.model.model.texture(new ImageTexture(new one.empty3.libs.Image(editPolygonsMappings.model.image)));
        } else if (editPolygonsMappings!=null&&editPolygonsMappings.model != null && editPolygonsMappings.model.iTextureMorphMove != null) {
            editPolygonsMappings.model.model.texture(editPolygonsMappings.model.iTextureMorphMove);
        } else {
        }
        if (!scene().getObjets().getData1d().isEmpty()) {
            z().scene(scene);
            z().camera(c);
        }
        Point3D minBox = new Point3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        Point3D maxBox = new Point3D(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);

        if (editPolygonsMappings.model != null) {
            editPolygonsMappings.model.model.getBounds(minBox, maxBox);

            if (defautZheight == 0) {
                c.getEye().setX(maxBox.getX() / 2 + minBox.getX() / 2);
                c.getEye().setY(maxBox.getY() / 2 + minBox.getY() / 2);
                //c.getEye().setZ(maxBox.getZ() + Math.sqrt(Math.pow(maxBox.getX() - minBox.getX(), 2) + Math.pow(maxBox.getY() - minBox.getY(), 2)));
                c.getEye().setZ(maxBox.getZ()*2 + Math.sqrt(Math.pow(maxBox.getX() - minBox.getX(), 2) + Math.pow(maxBox.getY() - minBox.getY(), 2)));
                c.calculerMatrice(Point3D.Y.mult(-1));
                //c.setAngleYr(60, 1.0 * z().ha() / z().la());
            } else {
                c.getEye().setZ(-Math.max(defautZheight, defautZwidth));
                c.getEye().setX(0.0);
                c.setLookat(Point3D.O0);
                c.calculerMatrice(Point3D.Y.mult(-1));
                //c.setAngleYr(60, 1.0 * z().ha() / z().la());
            }
        }
        camera(c);
        scene().cameraActive(c);
    }

    private void camera(Camera c) {
        scene().cameraActive(c);
    }

    private ZBufferImpl z() {
        return zBufferImpl;
    }

    private void addEyePolygons(Scene scene, E3Model model) {
        E3Model.FaceWithUv[] quads = new E3Model.FaceWithUv[2];
        HashMap<String, Point3D> modp = editPolygonsMappings.model.pointsInModel;
        if(model!=null && !modp.isEmpty()) {
            quads = new E3Model.FaceWithUv[]{
                    model.new FaceWithUv(new Polygon(
                            new Point3D[]{modp.get("RIGHT_EYE_RIGHT_CORNER"), modp.get("RIGHT_EYE_TOP_BOUNDARY"), modp.get("RIGHT_EYE_BOTTOM_BOUNDARY"),
                                    modp.get("RIGHT_EYE_LEFT_CORNER")}, model.texture()), new double[]{0, 0, 0, 0, 0, 0, 0, 0}),//???
                    model.new FaceWithUv(new Polygon(
                            new Point3D[]{modp.get("LEFT_EYE_LEFT_CORNER"), modp.get("LEFT_EYE_TOP_BOUNDARY"), modp.get("LEFT_EYE_BOTTOM_BOUNDARY"),
                                    modp.get("LEFT_EYE_RIGHT_CORNER")}, model.texture()), new double[]{0, 0, 0, 0, 0, 0, 0, 0})};//???
            for (int i = 0; i < quads.length; i++) {
                boolean fail = false;
                for (int j = 0; j < 4; j++) {
                    if (quads[i].getPolygon().getPoints().getElem(j) == null) {
                        fail = true;
                    }
                }
                if (!fail) {
                    scene.add(quads[i]);
                }
            }
        }
    }

    public void afterRender() {
        if (getPicture() != null) {
            zBufferImages.add(getPicture());
        }
    }

    private BufferedImage getPicture() {
        return zBufferImages.size()>0?zBufferImages.get(zBufferImages.size()-1):null;
    }


    public static TestHumanHeadTexturing startAll(EditPolygonsMappings editPolygonsMappings, BufferedImage jpg, BufferedImage jpgRight, E3Model obj) {
        Logger.getAnonymousLogger().log(Level.INFO, "Jpg Obj Mapping...");
        if (instance != null) {
            instance.stop();
        }
        if (threadTest != null) {
            threadTest.interrupt();
            threadTest = null;
        }
        if (editPolygonsMappings.model.iTextureMorphMove != null) {
            if (editPolygonsMappings.model.iTextureMorphMove.distanceAB != null) {
                editPolygonsMappings.model.iTextureMorphMove.distanceAB = null;
            }
        }
        if (instance != null && instance.editPolygonsMappings != null) {
            if (instance.editPolygonsMappings.model.iTextureMorphMove != null) {
                instance.editPolygonsMappings.model.iTextureMorphMove = null;
            }
            instance.editPolygonsMappings = null;
        }
        if (instance != null) {
            instance.editPolygonsMappings = null;
        }
        TestHumanHeadTexturing testHumanHeadTexturing = new TestHumanHeadTexturing();
        TestHumanHeadTexturing.instance = testHumanHeadTexturing;
        testHumanHeadTexturing.editPolygonsMappings = editPolygonsMappings;
        if (editPolygonsMappings.model.distanceABClass != null) {
            editPolygonsMappings.model.iTextureMorphMove = new TextureMorphMove(editPolygonsMappings, editPolygonsMappings.model.distanceABClass);
            if (editPolygonsMappings.model.iTextureMorphMove.distanceAB != null) {
                editPolygonsMappings.model.iTextureMorphMove.distanceAB.opt1 = editPolygonsMappings.model.opt1;
                editPolygonsMappings.model.iTextureMorphMove.distanceAB.optimizeGrid = editPolygonsMappings.model.optimizeGrid;
                editPolygonsMappings.model.iTextureMorphMove.distanceAB.typeShape = editPolygonsMappings.model.typeShape;
                editPolygonsMappings.model.iTextureMorphMove.distanceAB.refineMatrix = editPolygonsMappings.model.refineMatrix;
                editPolygonsMappings.model.iTextureMorphMove.distanceAB.aDimReduced = editPolygonsMappings.model.aDimReduced;
                editPolygonsMappings.model.iTextureMorphMove.distanceAB.bDimReduced = editPolygonsMappings.model.bDimReduced;
                if(editPolygonsMappings.model.iTextureMorphMove.distanceAB instanceof DistanceProxLinear4 &&jpgRight!=null)
                    editPolygonsMappings.model.iTextureMorphMove.distanceAB.jpgRight = editPolygonsMappings.model.imageFileRight;

            }
            editPolygonsMappings.model.testHumanHeadTexturing = testHumanHeadTexturing;
            testHumanHeadTexturing.setJpg(jpg);
            testHumanHeadTexturing.setJpgRight(jpgRight);
            testHumanHeadTexturing.setObj(obj);
        }
        threadTest = new Thread(testHumanHeadTexturing);
        threadTest.start();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        return testHumanHeadTexturing;
    }

    void stop() {
        // ???
    }

    protected void setJpg(BufferedImage jpgFile) {
        this.jpgFile = jpgFile;
    }
    public void setJpgRight(BufferedImage image) {
        this.jpgFileRight = image;
        if(editPolygonsMappings.model.iTextureMorphMove!=null && editPolygonsMappings.model.iTextureMorphMove.distanceAB instanceof DistanceProxLinear4)
            editPolygonsMappings.model.iTextureMorphMove.distanceAB.jpgRight = image;
    }

    public BufferedImage getJpgFile() {
        int i = zBufferImages.size() - 1;
        if (i < 0) return getPicture();
        BufferedImage current = zBufferImages.get(i);
        zBufferImages.clear();
        return current;

    }

    void setObj(E3Model objFile) {
        if (objFile != null) {
            this.objFile = objFile;
            scene().getObjets().setElem(objFile, 0);
        }
    }

    Scene scene() {
        return scene;
    }

    public Rectangle getRectangleFace() {
        return rectangleFace;
    }

    public void setRectangleFace(Rectangle rectangleFace) {
        this.rectangleFace = rectangleFace;
    }

    public BufferedImage getTrueFace() {
        return trueFace;
    }

    public void setTrueFace(BufferedImage trueFace) {
        this.trueFace = trueFace;
    }

    public BufferedImage zBufferImage() {
        return getJpgFile();
    }

    @Override
    public void run() {

    }

    public boolean isRunning() {
        return false;
    }

    public void loop(boolean b) {

    }
}
