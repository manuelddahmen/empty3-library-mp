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

/*
 * Created by JFormDesigner on Sat May 18 10:59:33 CEST 2024
 */

package one.empty3.apps.facedetect.jvm;


import one.empty3.apps.testobject.Resolution;
import one.empty3.library.Point3D;
import one.empty3.library.Point;
import one.empty3.library.Representable;
import one.empty3.library.RepresentableConteneur;
import one.empty3.library.objloader.E3Model;

import one.empty3.libs.Image;

import java.io.*;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author manue
 */
public class EditPolygonsMappings implements Runnable {
    public static final int EDIT_POINT_POSITION = 1;
    public  static final int SELECT_POINT_POSITION = 2;
    public static final int SELECT_POINT_VERTEX = 4;
    public static final int EDIT_OBJECT_MODE_ROTATE = 8;
    public static final int EDIT_OBJECT_MODE_TRANSLATE = 16;
    public static final int EDIT_OBJECT_MODE_INT_TRANSLATE_VECTOR = 32;
    public static final int EDIT_OBJECT_MODE_INT_UNTRANSLATE_VECTOR = 64;
    public static final int EDIT_OBJECT_MODE_INT_UNROTATE_VECTOR = 128;
    public static final int EDIT_OBJECT_MODE_INT_RESET_VIEW = 256;
    public static final int MULTIPLE = 1;
    public static final int SINGLE = 2;
    public one.empty3.libs.Image zBufferImage = new one.empty3.libs.Image(200, 200);
    public int typeShape = DistanceAB.TYPE_SHAPE_QUADR;
    public boolean refineMatrix = false;
    public Dimension2D aDimReduced = new Dimension(20, 20);
    public Dimension2D bDimReduced = new Dimension(20, 20);
    public int durationMilliS = 30000;
    public File imageFile;
    public File txtFile;
    public boolean hdTextures = true;
    public boolean textureWired = false;
    public  final int mode = EDIT_POINT_POSITION;
    public ConvexHull convexHull1;
    public ConvexHull convexHull2;
    public int selectedPointNo = -1;
    public E3Model model;
    public TestHumanHeadTexturing testHumanHeadTexturing;
    public boolean threadDistanceIsNotRunning = true;
    public boolean isRunning = true;
    public Point3D pFound = null;
    public String landmarkType = "";
    public double u;
    public double v;
    public int selectedPointNoOut = -1;
    public Point3D selectedPointOutUv = null;
    public Point3D selectedPointVertexOut;
    public TextureMorphMove iTextureMorphMove;
    public boolean hasChangedAorB = true;
    public boolean notMenuOpen = true;
    public HashMap<String, Point3D> pointsInModel = new HashMap<>();
    public HashMap<String, Point3D> pointsInImage = new HashMap<>();
    public HashMap<String, Point3D> points3 = new HashMap<>();
    public Image image = new one.empty3.libs.Image(200, 200);
    public Image imageFileRight = new one.empty3.libs.Image(200, 200);
    public int distanceABdimSize = 25;
    public Class<? extends DistanceAB> distanceABClass = DistanceProxLinear2.class;
    public boolean opt1 = false;
    public boolean optimizeGrid = false;
    public boolean renderingStarted = false;
    public boolean renderingStopped = true;
    public int inImageType;
    public int outTxtType;
    public int inTxtType;
    public File imagesDirectory;
    public File txtInDirectory;
    public File txtOutDirectory;
    public File modelFile;
    public Thread threadDisplay;
    public Thread threadTextureCreation;
    public ConvexHull convexHull3;
    public double computeTimeMax = 3600;
    public Dimension2D dimPictureBox = new Dimension(200, 200);
    public Dimension2D dimModelBox = new Dimension(200, 200);
    public Image image1;


    public EditPolygonsMappings() {
        distanceABClass = DistanceProxLinear2.class;
        testHumanHeadTexturing = new TestHumanHeadTexturing();
        testHumanHeadTexturing.editPolygonsMappings = this;

    }

    public void setComputeMaxTime(double value) {
        this.computeTimeMax = value;
    }
    public double getComputeTimeMax() {
        return computeTimeMax;
    }





    public void loadImage(File selectedFile) {
        image = (Image) Image.getFromFile(selectedFile);
        if (image != null && testHumanHeadTexturing != null) {
            testHumanHeadTexturing.setJpg(image);
            imageFile = selectedFile;
        }
        Logger.getAnonymousLogger().log(Level.INFO, "Loaded image");
    }

    public void loadImageRight(File selectedFile) {
        imageFileRight = (Image)Image.getFromFile(selectedFile);
        if (imageFileRight != null && testHumanHeadTexturing != null) {
            testHumanHeadTexturing.setJpgRight(imageFileRight);
        }

        Logger.getAnonymousLogger().log(Level.INFO, "Loaded image");
    }


    public void run() {
        testHumanHeadTexturing = TestHumanHeadTexturing.startAll(this, image, imageFileRight, model, hdTextures?Resolution.HD1080RESOLUTION:new Resolution((int) dimModelBox.getWidth(), (int) dimModelBox.getHeight()));
        hasChangedAorB = true;
        boolean firstTime = true;
        AtomicBoolean oneMore = new AtomicBoolean(true);
        while (isRunning) {
            try {
                threadDisplay = new Thread(() -> {
                    while (isRunning) {
                        //if (isNotMenuOpen()) {
                        zBufferImage = testHumanHeadTexturing.zBufferImage();
                        // Display 3D scene
                        if (zBufferImage != null) {
                            if(zBufferImage==null) {
                                zBufferImage =  new Image((int) dimModelBox.getWidth(), (int) dimModelBox.getHeight());
                            }
                        }
                        if (image == null) {
                            image = new Image((int) dimPictureBox.getWidth(), (int) dimPictureBox.getHeight());
                        }

                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                if (firstTime || !isRunning) {
                    threadDisplay.start();
                    firstTime = false;
                }
                if(isRunning) {
                    if (pointsInImage != null && dimModelBox != null && !pointsInImage.isEmpty()
                            && !pointsInModel.isEmpty() && model != null && image != null && distanceABClass != null
                            && threadDistanceIsNotRunning && iTextureMorphMove != null) {
                        if (oneMore.get() || hasChangedAorB() && threadTextureCreation == null) {
                            threadDistanceIsNotRunning = false;
                            hasChangedAorB = false;
                            threadTextureCreation = new Thread(() -> {
                                try {
                                    if (hasChangedAorB())
                                        oneMore.set(true);
                                    else
                                        oneMore.set(false);
                                    long l = System.nanoTime();
                                    Logger.getAnonymousLogger().log(Level.INFO, "All loaded resources finished. Starts distance calculation");
                                    if (iTextureMorphMove == null) {
                                        iTextureMorphMove = new TextureMorphMove(this, distanceABClass);
                                    }
                                    if (!distanceABClass.getClass().equals(iTextureMorphMove.distanceAB)) {
                                        if (pointsInModel != null && pointsInImage != null && !pointsInImage.isEmpty() && !pointsInModel.isEmpty()) {

                                            if (pointsInImage != null && pointsInImage.size() >= 3 && pointsInModel != null && pointsInModel.size() >= 3) {
                                                //iTextureMorphMove.setConvHullAB();
                                            }
                                            if (iTextureMorphMove.distanceAB != null && !iTextureMorphMove.distanceAB.isInvalidArray()) {
                                                // Display 3D scene
                                                if (model != null) {
                                                    iTextureMorphMove.distanceAB.setModel(model);
                                                    model.texture(iTextureMorphMove);
                                                }
                                                if (iTextureMorphMove.distanceAB instanceof DistanceProxLinear4 d4 && imageFileRight != null) {
                                                    d4.jpgRight = imageFileRight;
                                                }
                                                if (iTextureMorphMove.distanceAB instanceof DistanceProxLinear43 d43 && imageFileRight != null) {
                                                    d43.jpgRight = imageFileRight;
                                                }
                                                if (iTextureMorphMove.distanceAB instanceof DistanceProxLinear44 d44 && imageFileRight != null) {
                                                    d44.jpgRight = imageFileRight;
                                                }
                                            } else {
                                                Logger.getAnonymousLogger().log(Level.INFO, "Invalid array in DistanceAB");
                                            }
                                            l = System.nanoTime() - l;
                                            Logger.getAnonymousLogger().log(Level.INFO, "Distance calculation finished" + (l / 1000000.0));
                                        }
                                    }
                                } catch (RuntimeException ex) {
                                    ex.printStackTrace();
                                } finally {

                                }
                                threadTextureCreation = null;
                                hasChangedAorB = false;
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                            threadTextureCreation.start();
                            Thread.sleep(1000);
                            threadDistanceIsNotRunning = true;
                            Logger.getAnonymousLogger().log(Level.INFO, "Thread texture creation started");
                            //Logger.getAnonymousLogger().log(Level.INFO, "Pause because no changes, and texture updated");
                        }
                    }
                    if (!threadDistanceIsNotRunning)
                        Thread.sleep(10);// Logger.getAnonymousLogger().log(Level.INFO, "Thread 'Texture creation' still in progress...");
                    //}
                }
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                hasChangedAorB = true;
            } catch (InterruptedException e) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Interrupts ", e);
            }

            if ((testHumanHeadTexturing == null || !testHumanHeadTexturing.isRunning())
                    && (image != null && model != null)) {
                Logger.getAnonymousLogger().log(Level.INFO, "Le thread :TestObjet est arrêté ou non attribute");
                testHumanHeadTexturing = TestHumanHeadTexturing.startAll(this, image,imageFileRight,  model,
                        hdTextures?Resolution.HD1080RESOLUTION:new Resolution((int) dimModelBox.getWidth(), (int) dimModelBox.getHeight()));
                Logger.getAnonymousLogger().log(Level.INFO, "Une nouvelle instance a été démarrée");
            }

        }
    }

    public  boolean isNotMenuOpen() {
        return notMenuOpen;
    }


    public  boolean hasChangedAorB() {
        return hasChangedAorB;
    }

    public  void displayPointsIn(HashMap<String, Point3D> points) {
        if (points == null) return;
        Dimension2D panelDraw = dimPictureBox;
        try {
            Thread.sleep(200);
            if (image != null && panelDraw != null) {
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(EditPolygonsMappings.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public  void displayPointsOut(HashMap<String, Point3D> points) {
        Image panelDraw = image1;
        if (image != null && panelDraw != null && testHumanHeadTexturing != null && testHumanHeadTexturing.z().la() == dimModelBox.getWidth()
                && testHumanHeadTexturing.z().ha() == dimModelBox.getHeight()) {
            // Display image
            if (points != null) {
                try {
                    points.forEach((s, uvCoordinates) -> {
                        if (testHumanHeadTexturing.camera() != null && uvCoordinates != null) {
                            // +++ Model 3DObj : calculerPoint3D(u,v) +++
                            Point3D uvFace = model.findUvFace(
                                    uvCoordinates.getX(),
                                    uvCoordinates.getY());
                            if (uvFace != null) {
                                if (testHumanHeadTexturing.scene() != null && testHumanHeadTexturing.scene().cameraActive() != null) {
                                    Point point = testHumanHeadTexturing.scene().cameraActive().coordonneesPoint2D(uvFace, testHumanHeadTexturing.z());
                                    Point point2 = testHumanHeadTexturing.scene().cameraActive().coordonneesPoint2D(uvFace, testHumanHeadTexturing.z());
                                    if (point != null && point2 != null) {
                                        point.setLocation(point.getX() / testHumanHeadTexturing.z().la() * panelDraw.getWidth(),
                                                point.getY() / testHumanHeadTexturing.z().ha() * panelDraw.getHeight());
                                        point2.setLocation(point2.getX() * dimModelBox.getWidth(), point2.getX() * dimModelBox.getWidth());
                                    } else {
                                    }
                                }
                            }
                        }
                    });
                } catch (ConcurrentModificationException ex) {
                    Logger.getAnonymousLogger().log(Level.SEVERE, "Concurrent exception while drawing");
                }
            }
/*
            if (mode == SELECT_POINT_POSITION && selectedPointOutUv != null) {
                Point3D uvFace = model.findUvFace(selectedPointOutUv.getX(), selectedPointOutUv.getY());
                Point point = testHumanHeadTexturing.scene().cameraActive().coordonneesPoint2D(uvFace, testHumanHeadTexturing.getZ());
                point.setLocation(point.getX() / testHumanHeadTexturing.getZ().la() * panelDraw.getWidth(),
                        point.getY() / testHumanHeadTexturing.getZ().ha() * panelDraw.getHeight());
                Graphics graphics = panelDraw.getGraphics();
                graphics.setColor(Color.YELLOW);
                graphics.fillOval((int) (point.getX() - 3),
                        (int) ((point.getY()) - 3),
                        7, 7);

            } else if (mode == SELECT_POINT_POSITION && selectedPointVertexOut != null && selectedPointNoOut >= 0) {
                Point point = testHumanHeadTexturing.scene().cameraActive().coordonneesPoint2D(selectedPointVertexOut, testHumanHeadTexturing.getZ());
                point.setLocation(point.getX() / testHumanHeadTexturing.getZ().la() * panelDraw.getWidth(),
                        point.getY() / testHumanHeadTexturing.getZ().ha() * panelDraw.getHeight());
                Graphics graphics = panelDraw.getGraphics();
                graphics.setColor(Color.YELLOW);
                graphics.fillOval((int) (point.getX() - 3),
                        (int) ((point.getY()) - 3),
                        7, 7);

            }
  */
        }
    }

    public void add3DModel(File selectedFile) {
        try {
            testHumanHeadTexturing.defautZheight = 0;
            testHumanHeadTexturing.defautZwidth = 0;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(selectedFile));
            model = new E3Model(bufferedReader, true, selectedFile.getAbsolutePath());
            model.texture(iTextureMorphMove);
            testHumanHeadTexturing.setObj(model);
            Logger.getAnonymousLogger().log(Level.INFO, "Loaded model");
            testHumanHeadTexturing.defautZheight = 0;
            this.modelFile = selectedFile;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        hasChangedAorB = true;
    }

    public void add3DModelFillPanel(byte[] selectedFile) {
        BufferedReader bufferedReader = new BufferedReader(new StringReader(selectedFile.toString()));
        model = new E3Model(bufferedReader, true, null);
        model.texture(iTextureMorphMove);
        testHumanHeadTexturing.setObj(model);
        Logger.getAnonymousLogger().log(Level.INFO, "Loaded model");

        testHumanHeadTexturing.defautZwidth = (dimModelBox.getWidth() * Math.sqrt(2) / 2) * 2;
        testHumanHeadTexturing.defautZheight = (dimModelBox.getHeight() * Math.sqrt(2) / 2) * 2;

        Point3D minWidthPanel = new Point3D((double) dimModelBox.getWidth(),
                (double) dimModelBox.getHeight() * (1.0 * dimModelBox.getWidth() / dimModelBox.getHeight()), 0.0).mult(Math.sqrt(2));
        Point3D[] plane;


        plane = new Point3D[]{
                new Point3D(-minWidthPanel.getX() / 2, -minWidthPanel.getY() / 2, 0.0).mult(-1),
                new Point3D(minWidthPanel.getX() / 2, -minWidthPanel.getY() / 2, 0.0).mult(-1),
                new Point3D(minWidthPanel.getX() / 2, minWidthPanel.getY() / 2, 0.0).mult(-1),
                new Point3D(-minWidthPanel.getX() / 2, minWidthPanel.getY() / 2, 0.0).mult(-1)
        };
        // Adapt uv textures
        double[] textUv = new double[]{0, 0, 1, 0, 1, 1, 0, 1};

        for (int i = 0; i < textUv.length; i += 2) {
            textUv[i] = textUv[i] /* * minWidthPanel.getX()*/;
            textUv[i + 1] = textUv[i + 1] /* * minWidthPanel.getY()*/;
        }

        boolean a = false;
        for (Representable representable : model.getListRepresentable()) {
            if (representable instanceof E3Model.FaceWithUv face) {
                if (!a) {
                    face.getPolygon().setPoints(plane);
                    face.setTextUv(textUv);
                }
                a = true;
            } else if (representable instanceof RepresentableConteneur rc) {
                for (Representable representable1 : rc.getListRepresentable()) {
                    if (representable1 instanceof E3Model.FaceWithUv face1) {
                        if (!a) {
                            face1.getPolygon().setPoints(plane);
                            face1.setTextUv(textUv);
                        }
                        a = true;
                    }
                }
            }

        }

        hasChangedAorB = true;
    }

    public void loadTxt(byte[] selectedFile) {
        inTxtType = SINGLE;
        if (image != null && model != null) {
            pointsInImage = new HashMap<String, Point3D>();
            try {
                Scanner bufferedReader = new Scanner(new ByteArrayInputStream(selectedFile));
                String line = "";
                while (bufferedReader.hasNextLine()) {
                    line = bufferedReader.nextLine().trim();
                    Point3D point = new Point3D();
                    String landmarkType;
                    double x;
                    double y;
                    if (!line.isEmpty()) {
                            landmarkType = line;
                            // X
                            line = bufferedReader.nextLine().trim();
                            x = Double.parseDouble(line);
                            // Y
                            line = bufferedReader.nextLine().trim();
                            y = Double.parseDouble(line);
                            // Blank line
                            line = bufferedReader.nextLine().trim();

                            pointsInImage.put(landmarkType, new Point3D(x, y, 0.0));
                    }
                }
                Logger.getAnonymousLogger().log(Level.INFO, "Loaded {0} points in image", pointsInImage.size());
                bufferedReader.close();

                // Initialize surface bezier

                if (pointsInModel.size() == 0) {
                    pointsInModel = new HashMap<>();
                    if (!testHumanHeadTexturing.scene().getObjets().getData1d().isEmpty() && testHumanHeadTexturing.scene().getObjets().getElem(0) instanceof E3Model e3Model) {
                        pointsInImage.forEach((s, point3D) -> {
                            Point3D copy = new Point3D(point3D);
                            pointsInModel.put(s, copy);
                        });
                    }
                }
                pointsInModel.forEach((s, point3D) -> {
                    if (!pointsInImage.containsKey(s)) {
                        pointsInImage.put(s, point3D);
                    }
                });
                pointsInImage.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        if (!pointsInModel.containsKey(s)) {
                            pointsInModel.put(s, point3D);
                        }
                    }
                });


                hasChangedAorB = true;

                Logger.getAnonymousLogger().log(Level.INFO, "Loaded {0} points in model view", pointsInImage.size());
            } catch ( RuntimeException ex) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Seems file is not good ", ex);
            }
        } else {
            Logger.getAnonymousLogger().log(Level.INFO, "Loaded image first before points", pointsInImage.size());
        }


    }

    public void editPointPosition() {
        pointsInImage.forEach(new BiConsumer<String, Point3D>() {
            @Override
            public void accept(String s, Point3D point3D) {
                if (!pointsInModel.containsKey(s)) {
                    pointsInModel.put(s, new Point3D(Point3D.O0));
                }
            }
        });
        pointsInModel.forEach(new BiConsumer<String, Point3D>() {
            @Override
            public void accept(String s, Point3D point3D) {
                if (!pointsInImage.containsKey(s)) {
                    pointsInImage.put(s, new Point3D(Point3D.O0));
                }
            }
        });
    }

    public void selectPointPosition() {
        //mode = SELECT_POINT_POSITION;
    }

    public void loadTxtOut(byte[] selectedFile) {
        outTxtType = SINGLE;
        if (image != null && model != null) {
            pointsInModel = new HashMap<>();
            try {
                Scanner bufferedReader = new Scanner(new ByteArrayInputStream(selectedFile));
                String line = "";
                while (bufferedReader.hasNextLine()) {
                    line = bufferedReader.nextLine().trim();
                    String landmarkType;
                    double x;
                    double y;
                    if (!line.isEmpty()) {
                            landmarkType = line;
                            // X
                            line = bufferedReader.nextLine().trim();
                            x = Double.parseDouble(line);
                            // Y
                            line = bufferedReader.nextLine().trim();
                            y = Double.parseDouble(line);
                            // Blank line
                            line = bufferedReader.nextLine().trim();

                            pointsInModel.put(landmarkType, new Point3D(x, y, 0.0));
                        }
                }
                pointsInImage.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        if (!pointsInModel.containsKey(s)) {
                            pointsInModel.put(s, point3D);
                        }
                    }
                });
                pointsInModel.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        if (!pointsInImage.containsKey(s)) {
                            pointsInImage.put(s, point3D);
                        }
                    }
                });

                Logger.getAnonymousLogger().log(Level.INFO, "Loaded {0} points in image", pointsInModel.size());
                bufferedReader.close();

                hasChangedAorB = true;

            } catch ( RuntimeException ex) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Seems file is not good ", ex);
            }
        } else {
            Logger.getAnonymousLogger().log(Level.INFO, "Load image and model first before points", pointsInModel.size());
        }

    }

    public void saveTxtOutRightMddel(File selectedFile) {
        PrintWriter dataWriter = null;
        try {
            dataWriter = new PrintWriter(selectedFile);
            PrintWriter finalDataWriter = dataWriter;
            pointsInModel.forEach((s, point3D) -> {
                finalDataWriter.println(s);
                finalDataWriter.println(point3D.getX());
                finalDataWriter.println(point3D.getY());
                finalDataWriter.println();
            });
            dataWriter.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void saveTxtOutLeftPicture(File selectedFile) {
        PrintWriter dataWriter = null;
        try {
            dataWriter = new PrintWriter(selectedFile);
            PrintWriter finalDataWriter = dataWriter;
            pointsInImage.forEach((s, point3D) -> {
                finalDataWriter.println(s);
                finalDataWriter.println(point3D.getX());
                finalDataWriter.println(point3D.getY());
                finalDataWriter.println();
            });
            dataWriter.close();
        } catch (FileNotFoundException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Seems file is not found ", e);
        }
    }

    public void loadTxtOutVideoDirectory(File selectedFile) {
        inTxtType = MULTIPLE;
        this.txtOutDirectory = selectedFile;
    }

    public void loadTxtVideoDirectory(File selectedFile) {
        if (selectedFile.exists() && selectedFile.isDirectory()) {
            outTxtType = MULTIPLE;
            this.txtInDirectory = selectedFile;
        }

    }

    public void loadImages(File selectedFile) {
        if (selectedFile.exists() && selectedFile.isDirectory()) {
            inImageType = MULTIPLE;

            this.imagesDirectory = selectedFile;
        }

    }

    public void stopRenderer() {
        hasChangedAorB = false;
        testHumanHeadTexturing.stop();
        testHumanHeadTexturing.setMaxFrames(0);
        //while (TestHumanHeadTexturing.threadTest.isAlive()) {
        //     TestHumanHeadTexturing.threadTest.interrupt();
        //}
        while (threadTextureCreation != null && threadTextureCreation.isAlive()) {
            threadTextureCreation.interrupt();
        }
        iTextureMorphMove = null;
        threadTextureCreation = null;
        threadDistanceIsNotRunning = true;
        if(testHumanHeadTexturing!=null&&testHumanHeadTexturing.isRunning()) {
            testHumanHeadTexturing.stop();
        }
        renderingStopped = true;
        hasChangedAorB = true;
    }


    public Representable getModel() {
        return model;
    }

    public void loadTxt3(byte[] selectedFile) {
        outTxtType = SINGLE;
        if (image != null && model != null) {
            points3 = new HashMap<>();
            try {
                Scanner bufferedReader = new Scanner(new ByteArrayInputStream(selectedFile));
                String line = "";
                while (bufferedReader.hasNextLine()) {
                    line = bufferedReader.nextLine().trim();
                    String landmarkType;
                    double x;
                    double y;
                    if (!line.isEmpty()) {
                            landmarkType = line;
                            // X
                            line = bufferedReader.nextLine().trim();
                            x = Double.parseDouble(line);
                            // Y
                            line = bufferedReader.nextLine().trim();
                            y = Double.parseDouble(line);
                            // Blank line
                            line = bufferedReader.nextLine().trim();

                            points3.put(landmarkType, new Point3D(x, y, 0.0));
                    }
                }
                pointsInImage.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        if (!points3.containsKey(s)) {
                            points3.put(s, point3D);
                        }
                    }
                });
                pointsInModel.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        if (!points3.containsKey(s)) {
                            points3.put(s, point3D);
                        }
                    }
                });
                points3.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        if (!pointsInImage.containsKey(s)) {
                            pointsInImage.put(s, point3D);
                        }
                    }
                });
                points3.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        if (!pointsInModel.containsKey(s)) {
                            pointsInModel.put(s, point3D);
                        }
                    }
                });

                Logger.getAnonymousLogger().log(Level.INFO, "Loaded {0} points in image", pointsInModel.size());
                bufferedReader.close();



                hasChangedAorB = true;

            } catch (RuntimeException ex) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Seems file is not good ", ex);
            }
        } else {
            Logger.getAnonymousLogger().log(Level.INFO, "Load image and model first before points", pointsInModel.size());
        }

    }

    public void loadImageData1(byte[] image1s) {
        Image image0 = (Image)(Image.getFromInputStream(new ByteArrayInputStream(image1s)));
        if(image0!=null) {
            image = image0;
            dimPictureBox = new Dimension(image.getWidth(), image.getHeight());
        }
    }
    public void loadImageData3(byte[] image1s) {

        Image image0 = (Image)(Image.getFromInputStream(new ByteArrayInputStream(image1s)));
        if(image0!=null) {
            imageFileRight = image0;
        }
    }

    public void loadTxtData(String model, int i) {

        switch (i) {
            case 1:
                loadImageData1(model.getBytes());
                break;
            case 2:
                loadTxtOut(model.getBytes());
                break;
            case 3:
                loadImageData1(model.getBytes());
                break;
        }

    }

    public void loadImage3(Image image3) {
        imageFileRight = image3;

    }

    public void loadImage1(Image image1) {
        image = image1;

    }

    public void stopThreadDisplay() {
        isRunning = false;
    }
}
