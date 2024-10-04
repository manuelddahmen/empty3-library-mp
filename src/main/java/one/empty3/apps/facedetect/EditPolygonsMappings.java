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

package one.empty3.apps.facedetect;

import com.google.common.util.concurrent.AtomicDouble;
import net.miginfocom.swing.MigLayout;
import one.empty3.apps.morph.Main;
import one.empty3.feature.ConvHull;
import one.empty3.library.*;
import one.empty3.library.core.testing.TestObjet;
import one.empty3.library.objloader.E3Model;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author manue
 */
public class EditPolygonsMappings extends JPanel implements Runnable {
    private static final int EDIT_POINT_POSITION = 1;
    private static final int SELECT_POINT_POSITION = 2;
    private static final int SELECT_POINT_VERTEX = 4;
    public static final int MULTIPLE = 1;
    public static final int SINGLE = 2;
    public BufferedImage zBufferImage;
    public int typeShape = DistanceAB.TYPE_SHAPE_QUADR;
    public boolean refineMatrix = false;
    public Dimension2D aDimReduced = new Dimension(20, 20);
    public Dimension2D bDimReduced = new Dimension(20, 20);
    public int durationMilliS = 30000;
    public File imageFile;
    public File txtFile;
    public boolean hdTextures = false;
    public boolean textureWired = false;
    private final int mode = EDIT_POINT_POSITION;
    int selectedPointNo = -1;
    protected E3Model model;
    protected TestHumanHeadTexturing testHumanHeadTexturing;
    boolean threadDistanceIsNotRunning = true;
    protected boolean isRunning = true;
    private Point3D pFound = null;
    private String landmarkType = "";
    private double u;
    private double v;
    private int selectedPointNoOut = -1;
    private Point3D selectedPointOutUv = null;
    private Point3D selectedPointVertexOut;
    TextureMorphMove iTextureMorphMove;
    boolean hasChangedAorB = true;
    boolean notMenuOpen = true;
    public HashMap<String, Point3D> pointsInModel = new HashMap<>();
    public HashMap<String, Point3D> pointsInImage = new HashMap<>();
    BufferedImage image;
    public int distanceABdimSize = 25;
    public Class<? extends DistanceAB> distanceABClass = DistanceProxLinear2.class;
    public boolean opt1 = false;
    public boolean optimizeGrid = false;
    boolean renderingStarted = false;
    boolean renderingStopped = true;
    int inImageType;
    int outTxtType;
    int inTxtType;
    File imagesDirectory;
    File txtInDirectory;
    File txtOutDirectory;
    private File modelFile;
    private Thread threadDisplay;
    Thread threadTextureCreation;


    public EditPolygonsMappings(Window owner) {
        this();
    }

    EditPolygonsMappings() {
        initComponents();
        distanceABClass = DistanceProxLinear2.class;
        testHumanHeadTexturing = new TestHumanHeadTexturing();
    }

    public JPanel getContentPanel() {
        return contentPanel;
    }

    private void panelModelViewMouseDragged(MouseEvent e) {
        Point point = e.getPoint();
        if (image != null && model != null && selectedPointNo > -1) {
            int x = point.x;
            int y = point.y;
            ZBufferImpl.ImageMapElement ime = ((ZBufferImpl) testHumanHeadTexturing.getZ()).ime;
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
                    pointsInModel.forEach((landmarkTypeItem, point3D) -> {
                        if (landmarkTypeItem.equals(landmarkType)) {
                            pointsInModel.put(landmarkTypeItem, finalPointIme);
                        }
                    });
                    hasChangedAorB = true;
                } else {
                    Logger.getAnonymousLogger().log(Level.INFO, "Representable null : " + elementRepresentable);
                }
            } else {
                Logger.getAnonymousLogger().log(Level.INFO, "Point out of bounds : " + pointIme);
            }
        }
    }


    private void panelPictureMouseClicked(MouseEvent e) {
        Point point = e.getPoint();
        if (image != null && model != null) {
            Point3D[] pNear = new Point3D[]{new Point3D(point.getX() / panelPicture.getWidth(),
                    point.getY() / panelPicture.getHeight(), 0.)};
            AtomicDouble distanceMin = new AtomicDouble(Double.MAX_VALUE);
            pointsInImage.forEach((s, point3D) -> {
                if (Point3D.distance(pNear[0], point3D) < distanceMin.get()) {
                    distanceMin.set(Point3D.distance(pNear[0], point3D));
                    pFound = point3D;
                    landmarkType = s;
                    selectedPointNo = 0;
                }

            });
        }
    }

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
        if (testHumanHeadTexturing != null) {
            testHumanHeadTexturing.loop(false);
            testHumanHeadTexturing.setMaxFrames(0);
            testHumanHeadTexturing.stop();
            if (testHumanHeadTexturing.threadTest != null)
                TestHumanHeadTexturing.threadTest.interrupt();
        }
        testHumanHeadTexturing = TestHumanHeadTexturing.startAll(this, image, model, null);
        hasChangedAorB = true;
    }

    private void panelPictureMouseDragged(MouseEvent e) {
        Point point = e.getPoint();
        if (image != null && model != null && selectedPointNo > -1) {
            int x = point.x;
            int y = point.y;
            //ime.getElementPoint(x, y);
            final Point3D finalPointIme = new Point3D((double) (1.0 * x / panelPicture.getWidth()), (double) (1.0 * y / panelPicture.getHeight()), 0.0);
            pointsInImage.forEach((landmarkTypeItem, point3D) -> {
                if (landmarkTypeItem.equals(landmarkType)) {
                    pointsInImage.put(landmarkTypeItem, finalPointIme);
                }
            });
            hasChangedAorB = true;

        }

    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        var panel1 = new JPanel();
        splitPane1 = new JSplitPane();
        splitPane2 = new JSplitPane();
        panelPicture = new JPanel();
        panelModelView = new JPanel();
        scrollPane1 = new JScrollPane();
        textAreaTextOutput = new JTextArea();

        //======== this ========
        setPreferredSize(new Dimension(1280, 780));
        setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setMinimumSize(new Dimension(800, 600));
                contentPanel.setPreferredSize(new Dimension(0, 0));
                contentPanel.setLayout(new MigLayout(
                    "fill,hidemode 3",
                    // columns
                    "[fill]",
                    // rows
                    "[fill]"));

                //======== panel1 ========
                {
                    panel1.setMinimumSize(new Dimension(800, 600));
                    panel1.setLayout(new MigLayout(
                        "fill,hidemode 3",
                        // columns
                        "[fill]",
                        // rows
                        "[fill]"));

                    //======== splitPane1 ========
                    {
                        splitPane1.setOrientation(JSplitPane.VERTICAL_SPLIT);
                        splitPane1.setResizeWeight(0.5);

                        //======== splitPane2 ========
                        {
                            splitPane2.setResizeWeight(0.5);

                            //======== panelPicture ========
                            {
                                panelPicture.setMinimumSize(new Dimension(400, 300));
                                panelPicture.setPreferredSize(new Dimension(0, 0));
                                panelPicture.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        panelPictureMouseClicked(e);
                                    }
                                });
                                panelPicture.addMouseMotionListener(new MouseMotionAdapter() {
                                    @Override
                                    public void mouseDragged(MouseEvent e) {
                                        panelPictureMouseDragged(e);
                                        panelPictureMouseDragged(e);
                                    }
                                });
                                panelPicture.setLayout(new MigLayout(
                                    "fill,hidemode 3",
                                    // columns
                                    "[fill]" +
                                    "[fill]",
                                    // rows
                                    "[]" +
                                    "[]" +
                                    "[]"));
                            }
                            splitPane2.setLeftComponent(panelPicture);

                            //======== panelModelView ========
                            {
                                panelModelView.setMinimumSize(new Dimension(400, 300));
                                panelModelView.setPreferredSize(new Dimension(0, 0));
                                panelModelView.addComponentListener(new ComponentAdapter() {
                                    @Override
                                    public void componentResized(ComponentEvent e) {
                                        panelModelViewComponentResized(e);
                                    }
                                });
                                panelModelView.addMouseListener(new MouseAdapter() {
                                    @Override
                                    public void mouseClicked(MouseEvent e) {
                                        panelModelViewMouseClicked(e);
                                    }
                                });
                                panelModelView.addMouseMotionListener(new MouseMotionAdapter() {
                                    @Override
                                    public void mouseDragged(MouseEvent e) {
                                        panelModelViewMouseDragged(e);
                                    }
                                });
                                panelModelView.setLayout(new MigLayout(
                                    "fill,hidemode 3",
                                    // columns
                                    "[fill]" +
                                    "[fill]",
                                    // rows
                                    "[]" +
                                    "[]" +
                                    "[]"));
                            }
                            splitPane2.setRightComponent(panelModelView);
                        }
                        splitPane1.setTopComponent(splitPane2);

                        //======== scrollPane1 ========
                        {
                            scrollPane1.setMinimumSize(new Dimension(180, 320));

                            //---- textAreaTextOutput ----
                            textAreaTextOutput.setMinimumSize(new Dimension(800, 300));
                            textAreaTextOutput.setPreferredSize(new Dimension(0, 0));
                            textAreaTextOutput.setRows(3);
                            textAreaTextOutput.setColumns(150);
                            scrollPane1.setViewportView(textAreaTextOutput);
                        }
                        splitPane1.setBottomComponent(scrollPane1);
                    }
                    panel1.add(splitPane1, "cell 0 0");
                }
                contentPanel.add(panel1, "cell 0 0");
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
        }
        add(dialogPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JSplitPane splitPane1;
    private JSplitPane splitPane2;
    JPanel panelPicture;
    JPanel panelModelView;
    private JScrollPane scrollPane1;
    private JTextArea textAreaTextOutput;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    public void loadImage(File selectedFile) {
        try {
            image = ImageIO.read(selectedFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (image != null && testHumanHeadTexturing != null) {
            testHumanHeadTexturing.setJpg(image);
            imageFile = selectedFile;
        }

        Logger.getAnonymousLogger().log(Level.INFO, "Loaded image");

    }

    public void run() {
        testHumanHeadTexturing = TestHumanHeadTexturing.startAll(this, image, model, TestObjet.HD1080);
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
                        if (zBufferImage != null && zBufferImage.getWidth() == panelModelView.getWidth() && zBufferImage.getHeight() == panelModelView.getHeight()) {
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
                if (pointsInImage != null && panelModelView != null && !pointsInImage.isEmpty()
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
                                if (iTextureMorphMove == null || !distanceABClass.getClass().equals(iTextureMorphMove.distanceAB)) {
                                    iTextureMorphMove = new TextureMorphMove(this, distanceABClass);
                                    if (pointsInModel != null && pointsInImage != null && !pointsInImage.isEmpty() && !pointsInModel.isEmpty()) {

                                        if (pointsInImage != null && pointsInImage.size() >= 3 && pointsInModel != null && pointsInModel.size() >= 3) {
                                            iTextureMorphMove.setConvHullAB();
                                        }
                                        if (!iTextureMorphMove.distanceAB.isInvalidArray()) {
                                            // Display 3D scene
                                            if (model != null) {
                                                iTextureMorphMove.distanceAB.setModel(model);
                                                model.texture(iTextureMorphMove);
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
            } catch (RuntimeException ex) {
                ex.printStackTrace();
                hasChangedAorB = true;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if (testHumanHeadTexturing == null || !testHumanHeadTexturing.isRunning()
                    && image != null && model != null) {
                //testHumanHeadTexturing = TestHumanHeadTexturing.startAll(this, image, model);
                Logger.getAnonymousLogger().log(Level.INFO, "Le thread :TestObjet est arrêté ou non attribute");
                Logger.getAnonymousLogger().log(Level.INFO, "Il y a (pas nécessairement exact) %d instances de classes dérivées de TsestObjet");
                Logger.getAnonymousLogger().log(Level.INFO, "Une nouvelle instance a été démarrée");
            }

        }
    }

    private boolean isNotMenuOpen() {
        return notMenuOpen;
    }


    private boolean hasChangedAorB() {
        return hasChangedAorB;
    }

    private void displayPointsIn(HashMap<String, Point3D> points) {
        if (points == null) return;
        JPanel panelDraw = panelPicture;
        try {
            Thread.sleep(200);
            if (image != null && panelDraw != null) {
                Graphics graphics = panelDraw.getGraphics();
                if (graphics != null) {
                    points.forEach((s, point3D) -> {
                        Graphics graphics1 = panelDraw.getGraphics();
                        if (landmarkType != null && landmarkType.equals(s))
                            graphics1.setColor(Color.ORANGE);
                        else
                            graphics1.setColor(Color.GREEN);
                        graphics1.fillOval((int) (double) (point3D.getX() * panelDraw.getWidth()) - 3,
                                (int) (double) (point3D.getY() * panelDraw.getHeight()) - 3, 7, 7);
                    });
                    // Display 3D scene
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void displayPointsOut(HashMap<String, Point3D> points) {
        JPanel panelDraw = panelModelView;
        if (image != null && panelDraw != null && testHumanHeadTexturing != null && testHumanHeadTexturing.getZ().la() == panelModelView.getWidth()
                && testHumanHeadTexturing.getZ().ha() == panelModelView.getHeight()) {
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
                                Point point = testHumanHeadTexturing.scene().cameraActive().coordonneesPoint2D(uvFace, testHumanHeadTexturing.getZ());
                                Point point2 = testHumanHeadTexturing.scene().cameraActive().coordonneesPoint2D(uvFace, testHumanHeadTexturing.getZ());
                                if (point != null && point2 != null) {
                                    point.setLocation(point.getX() / testHumanHeadTexturing.getZ().la() * panelDraw.getWidth(),
                                            point.getY() / testHumanHeadTexturing.getZ().ha() * panelDraw.getHeight());
                                    point2.setLocation(point2.getX() * panelModelView.getWidth(), point2.getX() * panelModelView.getWidth());
                                    Graphics graphics = panelDraw.getGraphics();
                                    // point.setLocation(point.getX(), point.getY());
                                    if (testHumanHeadTexturing.getZ().checkScreen(point)) {
                                        if (landmarkType != null && landmarkType.equals(s)) {
                                            graphics.setColor(Color.PINK);
                                        } else {
                                            graphics.setColor(Color.GREEN);
                                        }
                                        graphics.fillOval((int) (point.getX() - 3),
                                                (int) ((point.getY()) - 3),
                                                7, 7);
                                    }
                                    /*
                                    if (testHumanHeadTexturing.getZ().checkScreen(point2)
                                            && iTextureMorphMove != null && iTextureMorphMove.distanceAB != null
                                            && !iTextureMorphMove.distanceAB.isInvalidArray()) {
                                        Point3D point3 = iTextureMorphMove.distanceAB.findAxPointInB(point2.getX(), point2.getY());
                                        if (selectedPointNo == i[0]) {
                                            graphics.setColor(Color.PINK);
                                        } else {
                                            graphics.setColor(Color.YELLOW);
                                        }
                                        graphics.fillOval((int) (point3.getX() - 3),
                                                (int) ((point3.getY()) - 3),
                                                7, 7);
                                    }*/
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

    public void add3DModelFillPanel(File selectedFile) {
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(selectedFile));
            model = new E3Model(bufferedReader, true, selectedFile.getAbsolutePath());
            model.texture(iTextureMorphMove);
            testHumanHeadTexturing.setObj(model);
            Logger.getAnonymousLogger().log(Level.INFO, "Loaded model");

            testHumanHeadTexturing.defautZwidth = (panelModelView.getWidth() * Math.sqrt(2) / 2) * 2;
            testHumanHeadTexturing.defautZheight = (panelModelView.getHeight() * Math.sqrt(2) / 2) * 2;

            Point3D minWidthPanel = new Point3D((double) panelModelView.getWidth(),
                    (double) panelModelView.getHeight() * (1.0 * panelModelView.getWidth() / panelModelView.getHeight()), 0.0).mult(Math.sqrt(2));
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


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadTxt(File selectedFile) {
        inTxtType = SINGLE;
        if (image != null && model != null) {
            pointsInImage = new HashMap<String, Point3D>();
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

                            pointsInImage.put(landmarkType, new Point3D(x, y, 0.0));
                        }
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
                this.txtFile = selectedFile;
                hasChangedAorB = true;

                Logger.getAnonymousLogger().log(Level.INFO, "Loaded {0} points in model view", pointsInImage.size());
            } catch (IOException | RuntimeException ex) {
                throw new RuntimeException(ex);
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

    public void loadTxtOut(File selectedFile) {
        outTxtType = SINGLE;
        if (image != null && model != null) {
            pointsInModel = new HashMap<>();
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

                            pointsInModel.put(landmarkType, new Point3D(x, y, 0.0));
                        }
                    }
                }
                Logger.getAnonymousLogger().log(Level.INFO, "Loaded {0} points in image", pointsInModel.size());
                bufferedReader.close();

                hasChangedAorB = true;

            } catch (IOException | RuntimeException ex) {
                throw new RuntimeException(ex);
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
            throw new RuntimeException(e);
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
        //while (TestHumanHeadTexturing.threadTest.isAlive()) {
        //     TestHumanHeadTexturing.threadTest.interrupt();
        //}
        while (threadTextureCreation != null && threadTextureCreation.isAlive()) {
            threadTextureCreation.interrupt();
        }
        if (threadTextureCreation != null && threadTextureCreation.isAlive()) {
            while (threadTextureCreation != null && threadTextureCreation.isAlive()) {
                threadTextureCreation.interrupt();
            }
            if (threadTextureCreation != null && !threadTextureCreation.isAlive()) {
                threadTextureCreation = null;
            }
        }
        iTextureMorphMove = null;
        threadTextureCreation = null;
        threadDistanceIsNotRunning = true;
        testHumanHeadTexturing = TestHumanHeadTexturing.startAll(this,
                image, model, TestObjet.HD1080);
        renderingStopped = true;
        hasChangedAorB = true;
    }
}
