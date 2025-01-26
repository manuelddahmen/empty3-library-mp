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

/*
 * Created by JFormDesigner on Sat May 18 10:59:33 CEST 2024
 */

package one.empty3.facedetect.model;

import one.empty3.library.*;
import one.empty3.library.objloader.E3Model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author manue
 */
public class EditPolygonsMappings  implements Runnable {
    FaceDetectModel model ;
    public EditPolygonsMappings(Window owner) {
        this();
    }

    EditPolygonsMappings() {
        model = new FaceDetectModel();
        model.distanceABClass = DistanceProxLinear2.class;
        model.testHumanHeadTexturing = new TestHumanHeadTexturing();
    }

    public void setComputeMaxTime(double value) {
        model.computeTimeMax = value;
    }
    public double getComputeTimeMax() {
        return model.computeTimeMax;
    }

    public void voidPanelModelViewRotate(MouseEvent mouseEvent) {
        if (model.mode == model.EDIT_OBJECT_MODE_ROTATE) {

        } else if (model.mode == model.EDIT_OBJECT_MODE_TRANSLATE) {

        } else if (model.mode == model.EDIT_OBJECT_MODE_INT_UNROTATE_VECTOR) {

        } else if (model.mode == model.EDIT_OBJECT_MODE_INT_TRANSLATE_VECTOR) {

        } else if (model.mode == model.EDIT_OBJECT_MODE_INT_UNTRANSLATE_VECTOR) {

        } else if (model.mode == model.EDIT_OBJECT_MODE_INT_RESET_VIEW) {

        }
    }
/*
    private void panelModelViewMouseDragged(MouseEvent e) {
        java.awt.Point point = e.getPoint();
        if (model.image != null && model != null && model.selectedPointNo > -1) {
            int x = point.x;
            int y = point.y;
            ZBufferImpl.ImageMapElement ime = ((ZBufferImpl) model.testHumanHeadTexturing.zBufferImpl).ime;
            Point3D pointIme = null;
            if (ime.checkCoordinates(x, y)) {
                Representable elementRepresentable = ime.getrMap()[x][y];
                System.out.println(elementRepresentable);
                if (elementRepresentable instanceof E3Model.FaceWithUv
                        && ((E3Model.FaceWithUv) elementRepresentable).model.equals(model)) {
                    u = ime.getuMap()[x][y];
                    v = ime.getvMap()[x][y];
                    pointIme = new Point3D(u, v, 0.0);//ime.getElementPoint(x, y);


                    final Point3D finalPointIme = pointIme;
                    Logger.getAnonymousLogger().log(Level.INFO, "Point final ime : " + finalPointIme);
                    model.pointsInModel.forEach((landmarkTypeItem, point3D) -> {
                        if (landmarkTypeItem.equals(landmarkType)) {
                            model.pointsInModel.put(landmarkTypeItem, finalPointIme);
                        }
                    });
                    model.hasChangedAorB = true;
                } else {
                    Logger.getAnonymousLogger().log(Level.INFO, "Representable null : " + elementRepresentable);
                }
            } else {
                Logger.getAnonymousLogger().log(Level.INFO, "Point out of bounds : " + pointIme);
            }
        }
    }


 */
/*
    private void panelPictureMouseClicked(MouseEvent e) {
        java.awt.Point point = e.getPoint();
        if (model.image != null && model != null) {
            Point3D[] pNear = new Point3D[]{new Point3D(point.getX() / panelPicture.getWidth(),
                    point.getY() / panelPicture.getHeight(), 0.)};
            AtomicDouble distanceMin = new AtomicDouble(Double.MAX_VALUE);
            model.pointsInImage.forEach((s, point3D) -> {
                if (Point3D.distance(pNear[0], point3D) < distanceMin.get()) {
                    distanceMin.set(Point3D.distance(pNear[0], point3D));
                    model.pFound = point3D;
                    model.landmarkType = s;
                    model.selectedPointNo = 0;
                }

            });
        }
    }
*/
    private void panelModelViewMouseClicked(MouseEvent e) {
/*        Point point = e.getPoint();
        if (model != null) {
            int x = point.x;
            int y = point.y;
            ZBufferImpl.ImageMapElement ime = ((ZBufferImpl) testHumanHeadTexturing.getZ()).ime;
            Point3D pointIme = null;
            if (ime.checkCoordinates(x, y)) {
                u = ime.getuMap()[x][y];
                v = ime.getvMap()[x][y];
                pointIme = ime.getElementPoint(x, y);
            }
            Point3D finalPointIme = pointIme;
            int[] i = new int[]{0};
            selectedPointNoOut = -1;
            AtomicReference<Double> dist = new AtomicReference<>(Double.MAX_VALUE);
            pointsInModel.forEach((s, point3D) -> {
                if (Point3D.distance(finalPointIme, point3D) < dist.get()) {
                    dist.set(Point3D.distance(finalPointIme, point3D));
                    pointsInModel.put(s, finalPointIme);
                    selectedPointNoOut = i[0];
                    selectedPointVertexOut = point3D;
                    i[0]++;
                }
            });

        } else if (model != null && mode == SELECT_POINT_POSITION) {
            int x = point.x;
            int y = point.y;
            ZBufferImpl.ImageMapElement ime = ((ZBufferImpl) testHumanHeadTexturing.getZ()).ime;
            Point3D pointIme = null;
            if (ime.checkCoordinates(x, y)) {
                u = ime.getuMap()[x][y];
                v = ime.getvMap()[x][y];
                pointIme = ime.getElementPoint(x, y);
            }
            selectedPointOutUv = new Point3D(u, v);
        }
*/
    }

    private void panelModelViewComponentResized(ComponentEvent e) {
        int w = e.getComponent().getWidth();
        int h = e.getComponent().getHeight();
        if (model.testHumanHeadTexturing != null) {
            model.testHumanHeadTexturing.loop(false);
            if (model.testHumanHeadTexturing.threadTest != null)
                TestHumanHeadTexturing.threadTest.interrupt();
        }
        model.testHumanHeadTexturing = TestHumanHeadTexturing.startAll(this, model.image, model.imageFileRight, model, null);
        model.hasChangedAorB = true;
    }
/*
    private void panelPictureMouseDragged(MouseEvent e) {
        java.awt.Point point = e.getPoint();
        if (model.image != null && model != null && model.selectedPointNo > -1) {
            int x = point.x;
            int y = point.y;
            //ime.getElementPoint(x, y);
            final Point3D finalPointIme = new Point3D((double) (1.0 * x / panelPicture.getWidth()), (double) (1.0 * y / panelPicture.getHeight()), 0.0);
            model.pointsInImage.forEach((landmarkTypeItem, point3D) -> {
                if (landmarkTypeItem.equals(model.landmarkType)) {
                    model.pointsInImage.put(landmarkTypeItem, finalPointIme);
                }
            });
            model.hasChangedAorB = true;

        }

    }
*/
    public void loadImage(File selectedFile) {
        try {
            model.image = new one.empty3.libs.Image(selectedFile);
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Seems file is not good ", e);
        }
        if (model.image != null && model.testHumanHeadTexturing != null) {
            model.testHumanHeadTexturing.setJpg(model.image);
            model.imageFile = selectedFile;
        }
        Logger.getAnonymousLogger().log(Level.INFO, "Loaded image");
    }

    public void loadImageRight(File selectedFile) {
        try {
            model.imageFileRight = new one.empty3.libs.Image(selectedFile);
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Seems file is not good ", e);
        }
        if (model.imageFileRight != null && model.testHumanHeadTexturing != null) {
            model.testHumanHeadTexturing.setJpgRight(model.imageFileRight);
        }

        Logger.getAnonymousLogger().log(Level.INFO, "Loaded image");
    }


    public void run() {
        model.testHumanHeadTexturing = TestHumanHeadTexturing.startAll(this, model.image, model.imageFileRight, model.model, TestObjet.HD1080);
        model.hasChangedAorB = true;
        boolean firstTime = true;
        AtomicBoolean oneMore = new AtomicBoolean(true);
        while (model.isRunning) {
            try {
                model.threadDisplay = new Thread(() -> {
                    while (model.isRunning) {
                        //if (isNotMenuOpen()) {
                        model.zBufferImage = (one.empty3.libs.Image) model.testHumanHeadTexturing.zBufferImage();
                        // Display 3D scene
                        /*
                        if (model.zBufferImage != null && model.zBufferImage.getWidth() == panelModelView.getWidth() && zBufferImage.getHeight() == panelModelView.getHeight()) {
                            Graphics graphics = panelModelView.getGraphics();
                            if (graphics != null) {
                                graphics.drawImage(zBufferImage, 0, 0, panelModelView.getWidth(), panelModelView.getHeight(), null);
                                displayPointsOut(pointsInModel);
                            }
                        }
                        if (image != null) {
                            Graphics graphics = panelPicture.getGraphics();
                            if (graphics != null) {
                                graphics.drawImage(image, 0, 0, panelPicture.getWidth(), panelPicture.getHeight(), null);
                                displayPointsIn(pointsInImage);
                            }
                        }

                         */
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                if (firstTime || !model.isRunning) {
                    model.threadDisplay.start();
                    firstTime = false;
                }
                if (model.pointsInImage != null && !model.pointsInImage.isEmpty()
                        && !model.pointsInModel.isEmpty() && model != null && model.image != null && model.distanceABClass != null
                        && model.threadDistanceIsNotRunning && model.iTextureMorphMove != null) {
                    if (oneMore.get() || hasChangedAorB() && model.threadTextureCreation == null) {
                        model.threadDistanceIsNotRunning = false;
                        model.hasChangedAorB = false;
                        model.threadTextureCreation = new Thread(() -> {
                            try {
                                if (hasChangedAorB())
                                    oneMore.set(true);
                                else
                                    oneMore.set(false);
                                long l = System.nanoTime();
                                Logger.getAnonymousLogger().log(Level.INFO, "All loaded resources finished. Starts distance calculation");
                                if (model.iTextureMorphMove == null) {
                                    model.iTextureMorphMove = new TextureMorphMove(this, model.distanceABClass);
                                }
                                if(  !model.distanceABClass.getClass().equals(model.iTextureMorphMove.distanceAB)) {
                                    if (model.pointsInModel != null && model.pointsInImage != null && !model.pointsInImage.isEmpty() && !model.pointsInModel.isEmpty()) {

                                        if (model.pointsInImage != null && model.pointsInImage.size() >= 3 && model.pointsInModel != null && model.pointsInModel.size() >= 3) {
                                            //iTextureMorphMove.setConvHullAB();
                                        }
                                        if (model.iTextureMorphMove.distanceAB != null && !model.iTextureMorphMove.distanceAB.isInvalidArray()) {
                                            // Display 3D scene
                                            if (model != null) {
                                                model.iTextureMorphMove.distanceAB.setModel(model.model);
                                                model.model.texture(model.iTextureMorphMove);
                                            }
                                            if (model.iTextureMorphMove.distanceAB instanceof DistanceProxLinear4 d4 && model.imageFileRight != null) {
                                                d4.jpgRight = model.imageFileRight;
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
                            model.threadTextureCreation = null;
                            model.hasChangedAorB = false;
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        model.threadTextureCreation.start();
                        Thread.sleep(1000);
                        model.threadDistanceIsNotRunning = true;
                        Logger.getAnonymousLogger().log(Level.INFO, "Thread texture creation started");
                        //Logger.getAnonymousLogger().log(Level.INFO, "Pause because no changes, and texture updated");
                    }
                }
                if (!model.threadDistanceIsNotRunning)
                    Thread.sleep(10);// Logger.getAnonymousLogger().log(Level.INFO, "Thread 'Texture creation' still in progress...");
                //}
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                model.hasChangedAorB = true;
            } catch (InterruptedException e) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Interrupts ", e);
            }

            if (model.testHumanHeadTexturing == null || !model.testHumanHeadTexturing.isRunning()
                    &&model. image != null && model != null) {
                //testHumanHeadTexturing = TestHumanHeadTexturing.startAll(this, image, model);
                Logger.getAnonymousLogger().log(Level.INFO, "Le thread :TestObjet est arrêté ou non attribute");
                Logger.getAnonymousLogger().log(Level.INFO, "Il y a (pas nécessairement exact) %d instances de classes dérivées de TsestObjet");
                Logger.getAnonymousLogger().log(Level.INFO, "Une nouvelle instance a été démarrée");
            }

        }
    }

    private boolean isNotMenuOpen() {
        return model.notMenuOpen;
    }


    private boolean hasChangedAorB() {
        return model.hasChangedAorB;
    }
/*
    private void displayPointsIn(HashMap<String, Point3D> points) {
        if (points == null) return;
        JPanel panelDraw = model.panelPicture;
        try {
            Thread.sleep(200);
            if (model.image != null && panelDraw != null) {
                Graphics graphics = panelDraw.getGraphics();
                if (graphics != null) {
                    try {
                        points.forEach((s, point3D) -> {
                            Graphics graphics1 = panelDraw.getGraphics();
                            if (model.landmarkType != null && model.landmarkType.equals(s))
                                graphics1.setColor(Color.ORANGE);
                            else
                                graphics1.setColor(Color.GREEN);
                            graphics1.fillOval((int) (double) (point3D.getX() * panelDraw.getWidth()) - 3,
                                    (int) (double) (point3D.getY() * panelDraw.getHeight()) - 3, 7, 7);
                        });
                    } catch (ConcurrentModificationException ex) {

                    }
                    // Display 3D scene
                }
            }
        } catch (InterruptedException ex) {
            Logger.getAnonymousLogger().log(Level.SEVERE, null, ex);
        }

    }
*/
/*
  private void displayPointsOut(HashMap<String, Point3D> points) {
        JPanel panelDraw = model.panelModelView;
        if (model.image != null && panelDraw != null && model.testHumanHeadTexturing != null && model.testHumanHeadTexturing.getZ().la() == panelModelView.getWidth()
                && testHumanHeadTexturing.getZ().ha() == panelModelView.getHeight()) {
            // Display image
            if (points != null) {
                try {
                    points.forEach((s, uvCoordinates) -> {
                        if (model.testHumanHeadTexturing.camera() != null && uvCoordinates != null) {
                            // +++ Model 3DObj : calculerPoint3D(u,v) +++
                            Point3D uvFace = model.model.findUvFace(
                                    uvCoordinates.getX(),
                                    uvCoordinates.getY());
                            if (uvFace != null) {
                                Point point = model.testHumanHeadTexturing.scene().cameraActive().coordonneesPoint2D(uvFace, model.testHumanHeadTexturing.getZ());
                                Point point2 = model.testHumanHeadTexturing.scene().cameraActive().coordonneesPoint2D(uvFace, model.testHumanHeadTexturing.getZ());
                                if (point != null && point2 != null) {
                                    point.setLocation(point.getX() / model.testHumanHeadTexturing.getZ().la() * panelDraw.getWidth(),
                                            point.getY() / model.testHumanHeadTexturing.getZ().ha() * panelDraw.getHeight());
                                    point2.setLocation(point2.getX() * model.panelModelView.getWidth(), point2.getX() * model.panelModelView.getWidth());
                                    Graphics graphics = panelDraw.getGraphics();
                                    // point.setLocation(point.getX(), point.getY());
                                    if (model.testHumanHeadTexturing.getZ().checkScreen(point)) {
                                        if (model.landmarkType != null && model.landmarkType.equals(s)) {
                                            graphics.setColor(Color.PINK);
                                        } else {
                                            graphics.setColor(Color.GREEN);
                                        }
                                        graphics.fillOval((int) (point.getX() - 3),
                                                (int) ((point.getY()) - 3),
                                                7, 7);
                                    }
                                } else {
                                    Graphics graphics = panelDraw.getGraphics();
                                    graphics.setColor(Color.GREEN);
                                    graphics.fillRect(0, 0, 10, 10);

                                }
                            }
                        }

                    });
                } catch (ConcurrentModificationException ex) {
                    Logger.getAnonymousLogger().log(Level.SEVERE, "Concurrent exception while drawing");
                }
            }
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
        }
    }
*/

    public void add3DModel(File selectedFile) {
        try {
            model.testHumanHeadTexturing.defautZheight = 0;
            model.testHumanHeadTexturing.defautZwidth = 0;
            BufferedReader bufferedReader = new BufferedReader(new FileReader(selectedFile));
            model.model = new E3Model(bufferedReader, true, selectedFile.getAbsolutePath());
            model.model.texture(model.iTextureMorphMove);
            model.testHumanHeadTexturing.setObj(model.model);
            Logger.getAnonymousLogger().log(Level.INFO, "Loaded model");
            model.testHumanHeadTexturing.defautZheight = 0;
            model.modelFile = selectedFile;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        model.hasChangedAorB = true;
    }
/*
    public void add3DModelFillPanel(File selectedFile) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(selectedFile));
            model.model = new E3Model(bufferedReader, true, selectedFile.getAbsolutePath());
            model.model.texture(model.iTextureMorphMove);
            model.testHumanHeadTexturing.setObj(model);
            Logger.getAnonymousLogger().log(Level.INFO, "Loaded model");

            model.testHumanHeadTexturing.defautZwidth = (model.panelModelView.getWidth() * Math.sqrt(2) / 2) * 2;
            model.testHumanHeadTexturing.defautZheight = (model.panelModelView.getHeight() * Math.sqrt(2) / 2) * 2;

            Point3D minWidthPanel = new Point3D((double) model.panelModelView.getWidth(),
                    (double) model.panelModelView.getHeight() * (1.0 * model.panelModelView.getWidth() / model.panelModelView.getHeight()), 0.0).mult(Math.sqrt(2));
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
                textUv[i] = textUv[i];
                textUv[i + 1] = textUv[i + 1];
            }

            boolean a = false;
            for (Representable representable : model.model.getListRepresentable()) {
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

            model.hasChangedAorB = true;
        } catch (FileNotFoundException e) {
            Logger.getAnonymousLogger().log(Level.SEVERE, "Seems file is not found ", e);
        }
    }
*/
    public void loadTxt(File selectedFile) {
        model.inTxtType = model.SINGLE;
        if (model.image != null && model != null) {
            model.pointsInImage = new HashMap<String, Point3D>();
            try {
                Scanner bufferedReader = new Scanner(new FileReader(selectedFile));
                String line = "";
                while (bufferedReader.hasNextLine()) {
                    line = bufferedReader.nextLine().trim();
                    Point3D point = new Point3D();
                    String landmarkType;
                    double x;
                    double y;
                    if (!line.isEmpty()) {
                        if (Character.isLetter(line.charAt(0))) {
                            landmarkType = line;
                            // X
                            line = bufferedReader.nextLine().trim();
                            x = Double.parseDouble(line);
                            // Y
                            line = bufferedReader.nextLine().trim();
                            y = Double.parseDouble(line);
                            // Blank line
                            line = bufferedReader.nextLine().trim();

                            model.pointsInImage.put(landmarkType, new Point3D(x, y, 0.0));
                        }
                    }
                }
                Logger.getAnonymousLogger().log(Level.INFO, "Loaded {0} points in image", model.pointsInImage.size());
                bufferedReader.close();

                // Initialize surface bezier

                if (model.pointsInModel.size() == 0) {
                    model.pointsInModel = new HashMap<>();
                    if (!model.testHumanHeadTexturing.scene().getObjets().getData1d().isEmpty() && model.testHumanHeadTexturing.scene().getObjets().getElem(0) instanceof E3Model e3Model) {
                        model.pointsInImage.forEach((s, point3D) -> {
                            Point3D copy = new Point3D(point3D);
                            model.pointsInModel.put(s, copy);
                        });
                    }
                }
                model.pointsInModel.forEach((s, point3D) -> {
                    if (!model.pointsInImage.containsKey(s)) {
                        model.pointsInImage.put(s, point3D);
                    }
                });
                model.pointsInImage.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        if (!model.pointsInModel.containsKey(s)) {
                            model.pointsInModel.put(s, point3D);
                        }
                    }
                });


                this.model.txtFile = selectedFile;
                model.hasChangedAorB = true;

                Logger.getAnonymousLogger().log(Level.INFO, "Loaded {0} points in model view", model.pointsInImage.size());
            } catch (IOException | RuntimeException ex) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Seems file is not good ", ex);
            }
        } else {
            Logger.getAnonymousLogger().log(Level.INFO, "Loaded image first before points", model.pointsInImage.size());
        }


    }

    public void editPointPosition() {
        model.pointsInImage.forEach(new BiConsumer<String, Point3D>() {
            @Override
            public void accept(String s, Point3D point3D) {
                if (!model.pointsInModel.containsKey(s)) {
                    model.pointsInModel.put(s, new Point3D(Point3D.O0));
                }
            }
        });
        model.pointsInModel.forEach(new BiConsumer<String, Point3D>() {
            @Override
            public void accept(String s, Point3D point3D) {
                if (!model.pointsInImage.containsKey(s)) {
                    model.pointsInImage.put(s, new Point3D(Point3D.O0));
                }
            }
        });
    }

    public void selectPointPosition() {
        //mode = SELECT_POINT_POSITION;
    }

    public void loadTxtOut(File selectedFile) {
        model.outTxtType = model.SINGLE;
        if (model.image != null && model != null) {
            model.pointsInModel = new HashMap<>();
            try {
                Scanner bufferedReader = new Scanner(new FileReader(selectedFile));
                String line = "";
                while (bufferedReader.hasNextLine()) {
                    line = bufferedReader.nextLine().trim();
                    String landmarkType;
                    double x;
                    double y;
                    if (!line.isEmpty()) {
                        if (Character.isLetter(line.charAt(0))) {
                            landmarkType = line;
                            // X
                            line = bufferedReader.nextLine().trim();
                            x = Double.parseDouble(line);
                            // Y
                            line = bufferedReader.nextLine().trim();
                            y = Double.parseDouble(line);
                            // Blank line
                            line = bufferedReader.nextLine().trim();

                            model.pointsInModel.put(landmarkType, new Point3D(x, y, 0.0));
                        }
                    }
                }
                model.pointsInImage.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        if (!model.pointsInModel.containsKey(s)) {
                            model.pointsInModel.put(s, point3D);
                        }
                    }
                });
                model.pointsInModel.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        if (!model.pointsInImage.containsKey(s)) {
                            model.pointsInImage.put(s, point3D);
                        }
                    }
                });

                Logger.getAnonymousLogger().log(Level.INFO, "Loaded {0} points in image", model.pointsInModel.size());
                bufferedReader.close();

                model.hasChangedAorB = true;

            } catch (IOException | RuntimeException ex) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Seems file is not good ", ex);
            }
        } else {
            Logger.getAnonymousLogger().log(Level.INFO, "Load image and model first before points", model.pointsInModel.size());
        }

    }

    public void saveTxtOutRightMddel(File selectedFile) {
        PrintWriter dataWriter = null;
        try {
            dataWriter = new PrintWriter(selectedFile);
            PrintWriter finalDataWriter = dataWriter;
            model.pointsInModel.forEach((s, point3D) -> {
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
            model.pointsInImage.forEach((s, point3D) -> {
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
        model.inTxtType = model.MULTIPLE;
        this.model.txtOutDirectory = selectedFile;
    }

    public void loadTxtVideoDirectory(File selectedFile) {
        if (selectedFile.exists() && selectedFile.isDirectory()) {
            model.outTxtType = model.MULTIPLE;
            this.model.txtInDirectory = selectedFile;
        }

    }

    public void loadImages(File selectedFile) {
        if (selectedFile.exists() && selectedFile.isDirectory()) {
            model.inImageType = model.MULTIPLE;
            this.model.imagesDirectory = selectedFile;
        }

    }

    public void stopRenderer() {
        model.hasChangedAorB = false;
        model.testHumanHeadTexturing.stop();
        //while (TestHumanHeadTexturing.threadTest.isAlive()) {
        //     TestHumanHeadTexturing.threadTest.interrupt();
        //}
        while (model.threadTextureCreation != null && model.threadTextureCreation.isAlive()) {
            model.threadTextureCreation.interrupt();
        }
        if (model.threadTextureCreation != null && model.threadTextureCreation.isAlive()) {
            while (model.threadTextureCreation != null && model.threadTextureCreation.isAlive()) {
                model.threadTextureCreation.interrupt();
            }
            if (model.threadTextureCreation != null && !model.threadTextureCreation.isAlive()) {
                model.threadTextureCreation = null;
            }
        }
        model.iTextureMorphMove = null;
        model.threadTextureCreation = null;
        model.threadDistanceIsNotRunning = true;
        model.testHumanHeadTexturing = TestHumanHeadTexturing.startAll(this,
                model.image, model.imageFileRight, model.model);
        model.renderingStopped = true;
        model.hasChangedAorB = true;
    }


    public Representable getModel() {
        return model.model;
    }

    public void loadTxt3(File selectedFile) {
        model.outTxtType = model.SINGLE;
        if (model.image != null && model != null) {
            model.points3 = new HashMap<>();
            try {
                Scanner bufferedReader = new Scanner(new FileReader(selectedFile));
                String line = "";
                while (bufferedReader.hasNextLine()) {
                    line = bufferedReader.nextLine().trim();
                    String landmarkType;
                    double x;
                    double y;
                    if (!line.isEmpty()) {
                        if (Character.isLetter(line.charAt(0))) {
                            landmarkType = line;
                            // X
                            line = bufferedReader.nextLine().trim();
                            x = Double.parseDouble(line);
                            // Y
                            line = bufferedReader.nextLine().trim();
                            y = Double.parseDouble(line);
                            // Blank line
                            line = bufferedReader.nextLine().trim();

                            model.points3.put(landmarkType, new Point3D(x, y, 0.0));
                        }
                    }
                }
                model.pointsInImage.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        if (!model.points3.containsKey(s)) {
                            model.points3.put(s, point3D);
                        }
                    }
                });
                model.pointsInModel.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        if (!model.points3.containsKey(s)) {
                            model.points3.put(s, point3D);
                        }
                    }
                });
                model.points3.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        if (!model.pointsInImage.containsKey(s)) {
                            model.pointsInImage.put(s, point3D);
                        }
                    }
                });
                model.points3.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        if (!model.pointsInModel.containsKey(s)) {
                            model.pointsInModel.put(s, point3D);
                        }
                    }
                });

                Logger.getAnonymousLogger().log(Level.INFO, "Loaded {0} points in image", model.pointsInModel.size());
                bufferedReader.close();



                model.hasChangedAorB = true;

            } catch (IOException | RuntimeException ex) {
                Logger.getAnonymousLogger().log(Level.SEVERE, "Seems file is not good ", ex);
            }
        } else {
            Logger.getAnonymousLogger().log(Level.INFO, "Load image and model first before points", model.pointsInModel.size());
        }

    }
}
