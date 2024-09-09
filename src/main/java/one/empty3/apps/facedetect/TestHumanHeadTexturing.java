/*
 *
 *  * Copyright (c) 2024. Manuel Daniel Dahmen
 *  *
 *  *
 *  *    Copyright 2024 Manuel Daniel Dahmen
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 *
 */

package one.empty3.apps.facedetect;

import one.empty3.feature.PixM;
import one.empty3.library.*;
import one.empty3.library.core.testing.Resolution;
import one.empty3.library.core.testing.TestObjetStub;
import one.empty3.library.objloader.E3Model;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestHumanHeadTexturing extends TestObjetStub {
    public static Thread threadTest;
    private static TestHumanHeadTexturing instance = null;
    public double defautZheight = 0;
    public double defautZwidth = 0;
    private Rectangle rectangleFace;
    private BufferedImage trueFace;
    private BufferedImage jpgFile;
    private E3Model objFile;
    private EditPolygonsMappings editPolygonsMappings;
    protected ArrayList<BufferedImage> zBufferImages = new ArrayList<BufferedImage>();

    public TestHumanHeadTexturing() {
        instance = this;
    }


    public void setImageIn(PixM face) {
        this.trueFace = face.getImage();
    }

    @Override
    public void ginit() {
/*
        super.ginit();
        if (objFile != null) {
            z().scene().getObjets().getData1d().clear();
            z().scene().getObjets().setElem(objFile, 0);
        }
*/
    }

    @Override
    public void finit() {
        super.finit();
        if (editPolygonsMappings.model != null) {
            setObj(editPolygonsMappings.model);
        }
        if (editPolygonsMappings.image != null) {
            setJpg(editPolygonsMappings.image);
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

        if (defautZheight == 0) {
            c.getEye().setZ(c.getEye().getX() / 8);
            c.getEye().setX(0.0);
            c.calculerMatrice(Point3D.Y.mult(-1));
            //c.setAngleYr(60, 1.0 * z().ha() / z().la());
        } else {
            c.getEye().setZ(-Math.max(defautZheight, defautZwidth));
            c.getEye().setX(0.0);
            c.setLookat(Point3D.O0);
            c.calculerMatrice(Point3D.Y.mult(-1));
            //c.setAngleYr(60, 1.0 * z().ha() / z().la());
        }
        camera(c);
        scene().cameraActive(c);

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
                        def = Color.BLACK;
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
                && !scene().getObjets().getData1d().contains(editPolygonsMappings.model)) {
            scene.add(editPolygonsMappings.model);
        }
        if (editPolygonsMappings.model != null && editPolygonsMappings.image != null && editPolygonsMappings.textureWired) {
            editPolygonsMappings.model.texture(new ImageTexture(new ECBufferedImage(editPolygonsMappings.image)));
        } else if (editPolygonsMappings.model != null && editPolygonsMappings.iTextureMorphMove != null) {
            editPolygonsMappings.model.texture(editPolygonsMappings.iTextureMorphMove);
            editPolygonsMappings.iTextureMorphMove.setConvHullAB();
        } else {
            Logger.getAnonymousLogger().log(Level.WARNING, "setConvHullAB:: model or texture null on render thread");
        }
        if (!scene().getObjets().getData1d().isEmpty()) {
            z().scene(scene);
            z().camera(c);
        }
    }

    @Override
    public void afterRender() {
        if (getPicture() != null) {
            zBufferImages.add(getPicture());
        }
    }


    public static TestHumanHeadTexturing startAll(EditPolygonsMappings editPolygonsMappings, BufferedImage jpg, E3Model obj) {
        Logger.getAnonymousLogger().log(Level.INFO, "Jpg Obj Mapping...");
        if (instance != null) {
            instance.stop();
            if (threadTest != null && threadTest.isAlive()) {
                while (threadTest != null && threadTest.isAlive()) {
                    threadTest.interrupt();
                }
                if (threadTest != null && !threadTest.isAlive()) {
                    threadTest = null;
                }
            }
            editPolygonsMappings.iTextureMorphMove.distanceAB = null;
            instance.editPolygonsMappings.iTextureMorphMove = null;
            instance.editPolygonsMappings = null;
        }
        TestHumanHeadTexturing testHumanHeadTexturing = new TestHumanHeadTexturing();
        testHumanHeadTexturing.setDimension(new Resolution(editPolygonsMappings.panelModelView.getWidth(), editPolygonsMappings.panelModelView.getHeight()));
        TestHumanHeadTexturing.instance = testHumanHeadTexturing;
        testHumanHeadTexturing.editPolygonsMappings = editPolygonsMappings;
        editPolygonsMappings.iTextureMorphMove = new TextureMorphMove(editPolygonsMappings, editPolygonsMappings.distanceABClass);
        editPolygonsMappings.iTextureMorphMove.setDistanceABclass(editPolygonsMappings.distanceABClass);
        editPolygonsMappings.iTextureMorphMove.distanceAB.opt1 = editPolygonsMappings.opt1;
        editPolygonsMappings.iTextureMorphMove.distanceAB.optimizeGrid = editPolygonsMappings.optimizeGrid;
        editPolygonsMappings.iTextureMorphMove.distanceAB.typeShape = editPolygonsMappings.typeShape;
        editPolygonsMappings.iTextureMorphMove.distanceAB.refineMatrix = editPolygonsMappings.refineMatrix;
        editPolygonsMappings.iTextureMorphMove.setConvHullAB();
        editPolygonsMappings.iTextureMorphMove.distanceAB.aDimReduced = editPolygonsMappings.aDimReduced;
        editPolygonsMappings.iTextureMorphMove.distanceAB.bDimReduced = editPolygonsMappings.bDimReduced;
        testHumanHeadTexturing.setGenerate(GENERATE_IMAGE);
        testHumanHeadTexturing.setJpg(jpg);
        testHumanHeadTexturing.setObj(obj);
        testHumanHeadTexturing.loop(true);
        testHumanHeadTexturing.setMaxFrames(Integer.MAX_VALUE);
        testHumanHeadTexturing.setPublish(false);
        testHumanHeadTexturing.setDimension(new Resolution(editPolygonsMappings.panelModelView.getWidth(), editPolygonsMappings.panelModelView.getHeight()));

        threadTest = new Thread(testHumanHeadTexturing);
        threadTest.start();

        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        return testHumanHeadTexturing;
    }

    protected void setJpg(BufferedImage jpgFile) {
        this.jpgFile = jpgFile;
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
}
