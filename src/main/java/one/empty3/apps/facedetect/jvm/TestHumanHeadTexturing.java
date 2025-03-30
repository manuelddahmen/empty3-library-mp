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

package one.empty3.apps.facedetect.jvm;

import one.empty3.library.*;
import one.empty3.library.core.lighting.Colors;
import one.empty3.apps.testobject.*;
import one.empty3.library.objloader.E3Model;
import one.empty3.libs.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestHumanHeadTexturing extends TestObjetStub {
    public static Thread threadTest;
    private static TestHumanHeadTexturing instance = null;
    public double defautZheight = 0;
    public double defautZwidth = 0;
    private Rectangle rectangleFace;
    private Image trueFace;
    private Image jpgFile;
    protected Image jpgFileRight;
    private E3Model objFile;
    public EditPolygonsMappings editPolygonsMappings;
    protected ArrayList<Image> zBufferImages = new ArrayList<>();

    public TestHumanHeadTexturing() {
        instance = this;
    }


    @Override
    public void ginit() {
        z().texture(new ColorTexture(new Colors().random()));
        z().setMinMaxOptimium(z().new MinMaxOptimium(ZBufferImpl.MinMaxOptimium.MinMaxIncr.Min, 2000));
        z().setDisplayType(ZBufferImpl.DISPLAY_ALL);
    }

    @Override
    public void finit() {
        super.finit();

        if (editPolygonsMappings == null)
            return;
        if (editPolygonsMappings.model != null) {
            setObj(editPolygonsMappings.model);
            setObj(editPolygonsMappings.model);
        }
        if (editPolygonsMappings.image != null) {
            setJpg(editPolygonsMappings.image);
        }

        z().setDisplayType(ZBufferImpl.DISPLAY_ALL);
        File intPart = new File("faceSkin.txt");

        Camera c = new Camera();

        scene = new Scene();

        if (editPolygonsMappings != null && editPolygonsMappings.model != null) {
            scene().add(editPolygonsMappings.model);
            //addEyePolygons(scene, editPolygonsMappings.model);
        }
        if (editPolygonsMappings != null && editPolygonsMappings.model != null && editPolygonsMappings.image != null && editPolygonsMappings.textureWired) {
            editPolygonsMappings.model.texture(new ImageTexture(new Image(editPolygonsMappings.image)));
        } else if (editPolygonsMappings != null && editPolygonsMappings.model != null && editPolygonsMappings.iTextureMorphMove != null) {
            editPolygonsMappings.model.texture(editPolygonsMappings.iTextureMorphMove);
        } else {
        }
        if (!scene().getObjets().getData1d().isEmpty()) {
            z().scene(scene());
            z().camera(c);
        }
        Point3D minBox = new Point3D(Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);
        Point3D maxBox = new Point3D(Double.MIN_VALUE, Double.MIN_VALUE, Double.MIN_VALUE);

        if (editPolygonsMappings.model != null) {
            editPolygonsMappings.model.getBounds(minBox, maxBox);

            if (defautZheight == 0) {
                c.getEye().setX(maxBox.getX() / 2 + minBox.getX() / 2);
                c.getEye().setY(maxBox.getY() / 2 + minBox.getY() / 2);
                //c.getEye().setZ(maxBox.getZ() + Math.sqrt(Math.pow(maxBox.getX() - minBox.getX(), 2) + Math.pow(maxBox.getY() - minBox.getY(), 2)));
                c.getEye().setZ(maxBox.getZ() * 2 + Math.sqrt(Math.pow(maxBox.getX() - minBox.getX(), 2) + Math.pow(maxBox.getY() - minBox.getY(), 2)));
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
/*
        if(editPolygonsMappings!=null &&editPolygonsMappings.model instanceof E3Model e3Model) {
            for (int i = 0; i < e3Model.getListRepresentable().size(); i++) {
                Representable representable = e3Model.getListRepresentable().get(i);
                if(representable instanceof E3Model.FaceWithUv faceWithUv) {
                    faceWithUv.setIncrU(0.2);
                    faceWithUv.setIncrV(0.1);
                }
            }
        }*/
        if (z().scene().getObjets().getData1d().size() > 1)
            Logger.getAnonymousLogger().log(Level.SEVERE, "Only one model in scene allowed here");
    }

    private void addEyePolygons(Scene scene, E3Model model) {
        E3Model.FaceWithUv[] quads = new E3Model.FaceWithUv[2];
        HashMap<String, Point3D> modp = editPolygonsMappings.pointsInModel;
        if (model != null && !modp.isEmpty()) {
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

    @Override
    public void afterRender() {
        if (getPicture() != null && getPicture().getBi() != null) {
            zBufferImages.add(getPicture());
        }
    }


    public static TestHumanHeadTexturing startAll(EditPolygonsMappings editPolygonsMappings, Image jpg, Image jpgRight, E3Model obj, Resolution resolution) {
        Logger.getAnonymousLogger().log(Level.INFO, "Jpg Obj Mapping...");
        if (instance != null) {
            instance.stop();
        }
        if (threadTest != null) {
            threadTest.interrupt();
            threadTest = null;
        }
        if (editPolygonsMappings.iTextureMorphMove != null) {
            if (editPolygonsMappings.iTextureMorphMove.distanceAB != null) {
                editPolygonsMappings.iTextureMorphMove.distanceAB = null;
            }
        }
        if (instance != null && instance.editPolygonsMappings != null) {
            if (instance.editPolygonsMappings.iTextureMorphMove != null) {
                instance.editPolygonsMappings.iTextureMorphMove = null;
            }
            instance.editPolygonsMappings = null;
        }
        TestHumanHeadTexturing testHumanHeadTexturing = new TestHumanHeadTexturing();

        TestHumanHeadTexturing.instance = testHumanHeadTexturing;
        testHumanHeadTexturing.editPolygonsMappings = editPolygonsMappings;
        if (editPolygonsMappings.distanceABClass != null) {
            editPolygonsMappings.iTextureMorphMove = new TextureMorphMove(editPolygonsMappings, editPolygonsMappings.distanceABClass);
            if (editPolygonsMappings.iTextureMorphMove.distanceAB != null) {
                editPolygonsMappings.iTextureMorphMove.distanceAB.opt1 = editPolygonsMappings.opt1;
                editPolygonsMappings.iTextureMorphMove.distanceAB.optimizeGrid = editPolygonsMappings.optimizeGrid;
                editPolygonsMappings.iTextureMorphMove.distanceAB.typeShape = editPolygonsMappings.typeShape;
                editPolygonsMappings.iTextureMorphMove.distanceAB.refineMatrix = editPolygonsMappings.refineMatrix;
                editPolygonsMappings.iTextureMorphMove.distanceAB.aDimReduced = editPolygonsMappings.aDimReduced;
                editPolygonsMappings.iTextureMorphMove.distanceAB.bDimReduced = editPolygonsMappings.bDimReduced;
                if (editPolygonsMappings.iTextureMorphMove.distanceAB instanceof DistanceProxLinear4 && jpgRight != null)
                    editPolygonsMappings.iTextureMorphMove.distanceAB.jpgRight = editPolygonsMappings.imageFileRight;

            }
            editPolygonsMappings.testHumanHeadTexturing = testHumanHeadTexturing;

            if (resolution == null || !resolution.equals(Resolution.HD1080RESOLUTION)) {
                testHumanHeadTexturing.setResx((int) editPolygonsMappings.dimModelBox.getWidth());
                testHumanHeadTexturing.setResy((int) editPolygonsMappings.dimModelBox.getHeight());
                testHumanHeadTexturing.setDimension(
                        new Resolution((int) editPolygonsMappings.dimModelBox.getWidth(), (int) editPolygonsMappings.dimModelBox.getHeight()));

            } else {
                testHumanHeadTexturing.setResx(resolution.x());
                testHumanHeadTexturing.setResy(resolution.y());
                testHumanHeadTexturing.setDimension(TestObjet.HD1080);
            }
            testHumanHeadTexturing.setGenerate(GENERATE_IMAGE);
            testHumanHeadTexturing.setJpg(jpg);
            testHumanHeadTexturing.setJpgRight(jpgRight);
            testHumanHeadTexturing.setObj(obj);
            testHumanHeadTexturing.loop(true);
            testHumanHeadTexturing.setMaxFrames(Integer.MAX_VALUE);
            testHumanHeadTexturing.setPublish(false);
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

    protected void setJpg(Image jpgFile) {
        this.jpgFile = jpgFile;
    }

    public void setJpgRight(Image image) {
        this.jpgFileRight = image;
        if (editPolygonsMappings != null && editPolygonsMappings.iTextureMorphMove != null && editPolygonsMappings.iTextureMorphMove.distanceAB instanceof DistanceProxLinear4)
            editPolygonsMappings.iTextureMorphMove.distanceAB.jpgRight = image;
    }

    public Image getJpgFile() {
        int i = zBufferImages.size() - 1;
        if (i > 10) {
            for (int j = 0; j < 9; j++) {
                zBufferImages.remove(0);
            }
        }
        i = zBufferImages.size() - 1;
        if (i < 0) return getPicture();
        Image current = zBufferImages.get(i);
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

    public Image getTrueFace() {
        return trueFace;
    }

    public void setTrueFace(Image trueFace) {
        this.trueFace = trueFace;
    }

    public Image zBufferImage() {
        return getPicture()!=null?getPicture():null;
    }
}