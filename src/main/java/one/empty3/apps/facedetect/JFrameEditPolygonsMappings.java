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
 * Created by JFormDesigner on Sat May 18 12:25:12 CEST 2024
 */

package one.empty3.apps.facedetect;

import net.miginfocom.swing.MigLayout;
import one.empty3.apps.facedetect.gcp.FaceDetectApp;
import one.empty3.feature.app.replace.javax.imageio.ImageIO;
import one.empty3.library.Config;
import one.empty3.library.Point3D;
import one.empty3.library.Resolution;
import one.empty3.library.objloader.E3Model;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author manue
 */
public class JFrameEditPolygonsMappings extends JFrame {

    public class MyFilter implements Filter {
        public boolean isLoggable(LogRecord record) {
            return record.getLevel().intValue() >= Level.SEVERE.intValue();
        }
    }

    File lastDirectory;
    private final Config config;
    Thread threadDisplay;
    private int mode = 0;
    private final int SELECT_POINT = 1;
    private Resolution resolutionOut;

    public JFrameEditPolygonsMappings() {
        initComponents();

        editPolygonsMappings2 = new EditPolygonsMappings(this);

        config = new Config();
        File fileDirectoryDefault = config.getDefaultFileOutput();
        if (fileDirectoryDefault == null)
            config.setDefaultFileOutput(new File("."));
        String lastDirectoryTmpStr = config.getMap().computeIfAbsent("D3ModelFaceTexturing", k -> ".");
        config.save();
        lastDirectory = new File(lastDirectoryTmpStr);
        if (!lastDirectory.exists())
            config.save();
        setContentPane(editPolygonsMappings2);
        pack();
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        initParameters();
        threadDisplay = new Thread(editPolygonsMappings2);
        threadDisplay.start();

        Filter filter = new MyFilter();

        Logger.getAnonymousLogger().setFilter(filter);

    }

    public void initParameters() {
        if (editPolygonsMappings2 != null) {
            editPolygonsMappings2.opt1 = false;
            editPolygonsMappings2.hasChangedAorB = true;
            editPolygonsMappings2.distanceABClass = DistanceProxLinear2.class;
            //editPolygonsMappings2.iTextureMorphMoveImage = new TextureMorphMove();
            editPolygonsMappings2.optimizeGrid = false;
            editPolygonsMappings2.typeShape = DistanceAB.TYPE_SHAPE_QUADR;
        }

    }

    private void menuItemLoadImage(ActionEvent e) {
        JFileChooser loadImage = new JFileChooser();
        if (lastDirectory != null)
            loadImage.setCurrentDirectory(lastDirectory);
        int ret = loadImage.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            editPolygonsMappings2.loadImage(loadImage.getSelectedFile());
            lastDirectory = loadImage.getCurrentDirectory();
        } else if (ret == JFileChooser.ERROR_OPTION) {
            System.exit(-1);
        }
    }

    private void menuItemAdd3DModel(ActionEvent e) {
        JFileChooser add3DModel = new JFileChooser();
        if (lastDirectory != null)
            add3DModel.setCurrentDirectory(lastDirectory);
        if (add3DModel.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            editPolygonsMappings2.add3DModel(add3DModel.getSelectedFile());
        lastDirectory = add3DModel.getCurrentDirectory();
    }

    private void menuItemLoadTxt(ActionEvent e) {
        JFileChooser loadImagePoints = new JFileChooser();
        if (lastDirectory != null)
            loadImagePoints.setCurrentDirectory(lastDirectory);
        if (loadImagePoints.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
            editPolygonsMappings2.loadTxt(loadImagePoints.getSelectedFile());
        lastDirectory = loadImagePoints.getCurrentDirectory();
    }

    private void menuItemEditPointPosition(ActionEvent e) {
        editPolygonsMappings2.editPointPosition();
    }

    private void thisWindowClosing(WindowEvent e) {
        config.getMap().put("D3ModelFaceTexturing", lastDirectory.getAbsolutePath());
        config.save();
        try {

            editPolygonsMappings2.testHumanHeadTexturing.setMaxFrames(0);
            editPolygonsMappings2.isRunning = false;
            if (TestHumanHeadTexturing.threadTest != null) {
                editPolygonsMappings2.testHumanHeadTexturing.stop(); // TestObjet stop method
                TestHumanHeadTexturing.threadTest.join(); // join thread as it's dying
            }
            Thread.sleep(1000);
        } catch (InterruptedException | RuntimeException ex) {
            ex.printStackTrace();
        }
        super.dispose();
        System.exit(0);
    }

    private void menuItemSelectPoint(ActionEvent e) {
        mode = SELECT_POINT;
        editPolygonsMappings2.selectPointPosition();
    }

    private void panelModelViewMouseClicked(ActionEvent e) {
    }

    private void menuItemSaveModifiedVertex(ActionEvent e) {
        JFileChooser saveImageDeformed = new JFileChooser();
        if (lastDirectory != null)
            saveImageDeformed.setCurrentDirectory(lastDirectory);
        if (saveImageDeformed.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            editPolygonsMappings2.saveTxtOutRightMddel(saveImageDeformed.getSelectedFile());
        }
        lastDirectory = saveImageDeformed.getCurrentDirectory();
    }

    private void menuItemLoadModifiedVertex(ActionEvent e) {
        JFileChooser loadImageDeformed = new JFileChooser();
        if (lastDirectory != null)
            loadImageDeformed.setCurrentDirectory(lastDirectory);
        if (loadImageDeformed.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            editPolygonsMappings2.loadTxtOut(loadImageDeformed.getSelectedFile());
        }
        lastDirectory = loadImageDeformed.getCurrentDirectory();
    }

    public class SaveTexture {
        private final one.empty3.library.core.testing.Resolution resolution;
        private final E3Model model;
        private BufferedImage image;

        public SaveTexture(@NotNull one.empty3.library.core.testing.Resolution resolution, @NotNull BufferedImage image, @NotNull E3Model model) {
            this.resolution = resolution;
            this.model = model;
            this.image = image;

        }

        public BufferedImage computeTexture() {
            image = editPolygonsMappings2.image;
            TextureMorphMove iTextureMorphMoveImage = new TextureMorphMove(editPolygonsMappings2, editPolygonsMappings2.distanceABClass);
            BufferedImage imageOut = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            model.texture(iTextureMorphMoveImage);
            for (double u = 0; u < 1.0; u += 1.0 / image.getWidth()) {
                for (double v = 0; v < 1.0; v += 1.0 / image.getHeight()) {
                    int colorAt1 = editPolygonsMappings2.iTextureMorphMove.getColorAt(u, v);
                    imageOut.setRGB((int) Math.min(editPolygonsMappings2.image.getWidth() - 1, u * editPolygonsMappings2.image.getWidth()),
                            (int) Math.min(editPolygonsMappings2.image.getHeight() - 1, v * editPolygonsMappings2.image.getHeight()), colorAt1);
                }
                Logger.getAnonymousLogger().log(Level.INFO, "Image column #" + ((int) (u * 100)) + "% : done");
            }
            return imageOut;
        }
    }

    private void menuItemHD(ActionEvent e) {
        if (resolutionOut == null) {
            resolutionOut = Resolution.HD1080RESOLUTION;
        }
        Runnable jpg = () -> {
            while (editPolygonsMappings2.image == null || editPolygonsMappings2.pointsInImage == null || editPolygonsMappings2.pointsInModel == null
                    || editPolygonsMappings2.model == null) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            /*
            TextureMorphMove textureMorphMoveImage = new TextureMorphMove(editPolygonsMappings2, DistanceProxLinear1.class);
            textureMorphMoveImage.distanceAB = new DistanceProxLinear1(editPolygonsMappings2.pointsInImage.values().stream().toList(),
                    editPolygonsMappings2.pointsInModel.values().stream().toList(),
                    new Dimension(editPolygonsMappings2.image.getWidth(),
                            editPolygonsMappings2.image.getHeight()), new Dimension(Resolution.HD1080RESOLUTION.x(), Resolution.HD1080RESOLUTION.y(), false, false)
            );

             */
            TextureMorphMove textureMorphMove = editPolygonsMappings2.iTextureMorphMove;
            E3Model model = editPolygonsMappings2.model;
            File defaultFileOutput = config.getDefaultFileOutput();
            SaveTexture saveTexture = new SaveTexture(resolutionOut, editPolygonsMappings2.image, model);
            BufferedImage bufferedImage = saveTexture.computeTexture();
            ImageIO.write(bufferedImage, "jpg", new File(config.getDefaultFileOutput()
                    + File.separator + "output-face-on-model-texture" + UUID.randomUUID() + ".jpg"));
            if (resolutionOut.equals(Resolution.HD1080RESOLUTION))
                Logger.getAnonymousLogger().log(Level.INFO, "Smart generated HD image");
            else
                Logger.getAnonymousLogger().log(Level.INFO, "Smart generated 4K image");
            resolutionOut = null;
        };
        Thread thread = new Thread(jpg);
        thread.start();
    }

    private void menuItem4K(ActionEvent e) {
        this.resolutionOut = Resolution.K4RESOLUTION;
        menuItemHD(e);
    }


    private void menuBar1FocusLost(FocusEvent e) {
        editPolygonsMappings2.notMenuOpen = true;
    }

    private void checkBoxMenuItemShapeTypePolygons(ActionEvent e) {
        if (e.getSource() instanceof JCheckBoxMenuItem r) {
            if (r.isSelected()) {
                editPolygonsMappings2.iTextureMorphMove.distanceAB.typeShape = DistanceAB.TYPE_SHAPE_QUADR;
                editPolygonsMappings2.typeShape = DistanceAB.TYPE_SHAPE_QUADR;
            }
        }
        editPolygonsMappings2.hasChangedAorB = true;
    }

    private void checkBoxMenuItemTypeShapeBezier(ActionEvent e) {
        if (e.getSource() instanceof JCheckBoxMenuItem r) {
            if (r.isSelected()) {
                editPolygonsMappings2.iTextureMorphMove.distanceAB.typeShape = DistanceAB.TYPE_SHAPE_BEZIER;
                editPolygonsMappings2.typeShape = DistanceAB.TYPE_SHAPE_BEZIER;
            }
        }
        editPolygonsMappings2.hasChangedAorB = true;
    }

    private void checkBoxMenuItem1(ActionEvent e) {
        if (e.getSource() instanceof JCheckBoxMenuItem r) {
            if (r.isSelected()) {
                editPolygonsMappings2.iTextureMorphMove.distanceAB.opt1 = true;
                editPolygonsMappings2.opt1 = true;
            } else {
                editPolygonsMappings2.iTextureMorphMove.distanceAB.opt1 = true;
                editPolygonsMappings2.opt1 = true;
            }
        }
        editPolygonsMappings2.hasChangedAorB = true;
    }

    private void menuItemClassBezier2(ActionEvent e) {
        //editPolygonsMappings2.iTextureMorphMove.setDistanceABclass(DistanceBezier3.class);
        editPolygonsMappings2.distanceABClass = DistanceBezier3.class;
        editPolygonsMappings2.hasChangedAorB = true;
    }

    private void menuItem1DistanceBB(ActionEvent e) {
        ///editPolygonsMappings2.iTextureMorphMove.setDistanceABclass(DistanceBB.class);
        editPolygonsMappings2.distanceABClass = DistanceBB.class;
        editPolygonsMappings2.hasChangedAorB = true;
    }

    private void menuItemLinearProx1(ActionEvent e) {
        //editPolygonsMappings2.iTextureMorphMove.setDistanceABclass(DistanceProxLinear1.class);
        editPolygonsMappings2.distanceABClass = DistanceProxLinear1.class;
        editPolygonsMappings2.hasChangedAorB = true;
    }

    private void menuItemLinearProx2(ActionEvent e) {
        //editPolygonsMappings2.iTextureMorphMove.setDistanceABclass(DistanceProxLinear2.class);
        editPolygonsMappings2.distanceABClass = DistanceProxLinear2.class;
        editPolygonsMappings2.hasChangedAorB = true;
    }

    private void menuItemLinearProx3(ActionEvent e) {
        //editPolygonsMappings2.iTextureMorphMove.setDistanceABclass(DistanceProxLinear3.class);
        editPolygonsMappings2.distanceABClass = DistanceProxLinear3.class;
        editPolygonsMappings2.hasChangedAorB = true;
    }

    private void distanceLinear4(ActionEvent e) {
        //editPolygonsMappings2.iTextureMorphMove.setDistanceABclass(DistanceProxLinear3.class);
        editPolygonsMappings2.distanceABClass = DistanceProxLinear4.class;
        editPolygonsMappings2.hasChangedAorB = true;
    }

    private void optimizeGrid(ActionEvent e) {
        if (e.getSource() instanceof JCheckBoxMenuItem r) {

            editPolygonsMappings2.iTextureMorphMove.distanceAB.optimizeGrid = (r.isSelected());
        }
        editPolygonsMappings2.hasChangedAorB = true;
    }

    private void menuBar1MouseEnteredMenu(MouseEvent e) {
        editPolygonsMappings2.notMenuOpen = false;
    }

    private void menuBar1MouseExited(MouseEvent e) {
        editPolygonsMappings2.notMenuOpen = true;

    }

    private void menuItemSaveLandmarksRight(ActionEvent e) {
        JFileChooser saveImageDeformed = new JFileChooser();
        if (lastDirectory != null)
            saveImageDeformed.setCurrentDirectory(lastDirectory);
        if (saveImageDeformed.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            editPolygonsMappings2.saveTxtOutRightMddel(saveImageDeformed.getSelectedFile());
        }
        lastDirectory = saveImageDeformed.getCurrentDirectory();
    }

    private void menuItemLandmarksLeft(ActionEvent e) {
        JFileChooser saveImageDeformed = new JFileChooser();
        if (lastDirectory != null)
            saveImageDeformed.setCurrentDirectory(lastDirectory);
        if (saveImageDeformed.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            editPolygonsMappings2.saveTxtOutLeftPicture(saveImageDeformed.getSelectedFile());
        }
        lastDirectory = saveImageDeformed.getCurrentDirectory();
    }

    private void menuItem8(ActionEvent e) {
        // TODO add your code here
    }

    private void menuItem13(ActionEvent e) {
        // TODO add your code here
    }

    private void editPolygonsMappings2MouseDragged(MouseEvent e) {
        // TODO add your code here
    }

    private void checkBoxMenuItemPoly(ActionEvent e) {
        // TODO add your code here
    }

    private void checkBoxMenuItemBezier(ActionEvent e) {
        // TODO add your code here
    }

    private void addPoint(ActionEvent e) {
        if (editPolygonsMappings2.pointsInImage != null && editPolygonsMappings2.pointsInModel != null) {
            UUID num = UUID.randomUUID();
            editPolygonsMappings2.pointsInImage.put("LANDMARK_" + num, Point3D.O0);
            editPolygonsMappings2.pointsInModel.put("LANDMARK_" + num, Point3D.O0);
        } else {
            Logger.getAnonymousLogger().log(Level.WARNING, "Map of points image/model is null");
        }
    }

    private void loadTxtOut(ActionEvent e) {
        JFileChooser loadImageDeformed = new JFileChooser();
        if (lastDirectory != null)
            loadImageDeformed.setCurrentDirectory(lastDirectory);
        if (loadImageDeformed.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            editPolygonsMappings2.loadTxtOut(loadImageDeformed.getSelectedFile());
        }
        lastDirectory = loadImageDeformed.getCurrentDirectory();

    }

    private void stopRenderer(ActionEvent e) {
        editPolygonsMappings2.hasChangedAorB = false;
        editPolygonsMappings2.testHumanHeadTexturing.stop();
        //while (TestHumanHeadTexturing.threadTest.isAlive()) {
        //     TestHumanHeadTexturing.threadTest.interrupt();
        //}
        while (editPolygonsMappings2.threadDisplay != null && editPolygonsMappings2.threadDisplay.isAlive()) {
            editPolygonsMappings2.threadDisplay.interrupt();
        }
        if (editPolygonsMappings2.threadDisplay != null && editPolygonsMappings2.threadDisplay.isAlive()) {
            while (editPolygonsMappings2.threadDisplay != null && editPolygonsMappings2.threadDisplay.isAlive()) {
                editPolygonsMappings2.threadDisplay.interrupt();
            }
            if (editPolygonsMappings2.threadDisplay != null && !editPolygonsMappings2.threadDisplay.isAlive()) {
                editPolygonsMappings2.threadDisplay = null;
            }
        }
        editPolygonsMappings2.iTextureMorphMove = null;
        editPolygonsMappings2.threadDisplay = null;
        editPolygonsMappings2.threadDistanceIsNotRunning = true;
        editPolygonsMappings2.testHumanHeadTexturing = TestHumanHeadTexturing.startAll(editPolygonsMappings2,
                editPolygonsMappings2.image, editPolygonsMappings2.model);
        editPolygonsMappings2.hasChangedAorB = false;
        editPolygonsMappings2.renderingStopped = true;
    }

    private void startRenderer(ActionEvent e) {

        editPolygonsMappings2.iTextureMorphMove = new TextureMorphMove(editPolygonsMappings2, editPolygonsMappings2.distanceABClass);

        if (editPolygonsMappings2.pointsInImage.size() >= 3 && editPolygonsMappings2.pointsInModel.size() >= 3) {
            editPolygonsMappings2.iTextureMorphMove.setConvHullAB();
        }

        editPolygonsMappings2.hasChangedAorB = true;
        editPolygonsMappings2.renderingStarted = true;

    }

    private void photoPlaneRepresentable(ActionEvent e) {
    }

    private void addPlane(ActionEvent e) {
        editPolygonsMappings2.add3DModelFillPanel(new File("resources/models/plane blender2.obj"));
    }

    private void menuItemDistBezier2(ActionEvent e) {
        // TODO add your code here
    }

    private void checkBoxRefiineMatric(ActionEvent e) {
        if (e.getSource() instanceof JCheckBoxMenuItem r) {
            if (r.isSelected()) {
                editPolygonsMappings2.aDimReduced = editPolygonsMappings2.iTextureMorphMove.distanceAB.aDimReduced;
                editPolygonsMappings2.aDimReduced.setSize(new Dimension((int) (editPolygonsMappings2.aDimReduced.getWidth() * 2), (int) (editPolygonsMappings2.aDimReduced.getHeight() * 2)));
                editPolygonsMappings2.bDimReduced = editPolygonsMappings2.iTextureMorphMove.distanceAB.bDimReduced;
                editPolygonsMappings2.bDimReduced.setSize(new Dimension((int) (editPolygonsMappings2.bDimReduced.getWidth() * 2), (int) (editPolygonsMappings2.bDimReduced.getHeight() * 2)));
                //editPolygonsMappings2.iTextureMorphMove.distanceAB.refineMatrix = true;
            } else {
                //editPolygonsMappings2.iTextureMorphMove.distanceAB.refineMatrix = false;
            }
        }
        editPolygonsMappings2.hasChangedAorB = true;
    }

    /***
     * Starts rendering loop
     * @param e
     */
    private void renderVideo(ActionEvent e) {
        Thread videoCreationThread = new Thread(() -> {
            System.out.println("Video creation stub start");
            File[] imagesIn = null;
            File[] txtIn = null;
            File[] txtout = null;
            E3Model model;
            editPolygonsMappings2.durationMilliS = 30000;
            if (editPolygonsMappings2.inImageType == EditPolygonsMappings.MULTIPLE) {
                if (editPolygonsMappings2.imagesDirectory.isDirectory()) {
                    imagesIn = new File[editPolygonsMappings2.imagesDirectory.listFiles().length];
                    Object[] array = Arrays.stream(editPolygonsMappings2.imagesDirectory.listFiles()).sequential().filter(file -> file.getAbsolutePath().substring(-4).toLowerCase().equals(".jpg") || file.getAbsolutePath().substring(-4).toLowerCase().equals(".png")).toArray();
                    for (int i = 0; i < array.length; i++) {
                        imagesIn[i] = (File) array[i];
                    }
                }
            }
            if (editPolygonsMappings2.inTxtType == EditPolygonsMappings.MULTIPLE) {
                if (editPolygonsMappings2.txtInDirectory.isDirectory()) {
                    txtIn = new File[editPolygonsMappings2.txtInDirectory.listFiles().length];
                    Object[] array = Arrays.stream(editPolygonsMappings2.txtInDirectory.listFiles()).sequential().filter(file -> file.getAbsolutePath().substring(-4).toLowerCase().equals(".txt")).toArray();
                    for (int i = 0; i < array.length; i++) {
                        txtIn[i] = (File) array[i];
                    }
                }
            }
            if (editPolygonsMappings2.outTxtType == EditPolygonsMappings.MULTIPLE) {
                if (editPolygonsMappings2.txtOutDirectory.isDirectory()) {
                    txtout = new File[editPolygonsMappings2.txtOutDirectory.listFiles().length];
                    Object[] array = Arrays.stream(editPolygonsMappings2.txtInDirectory.listFiles()).sequential().filter(file -> file.getAbsolutePath().substring(-4).toLowerCase().equals(".txt")).toArray();
                    for (int i = 0; i < array.length; i++) {
                        txtout[i] = (File) array[i];
                    }
                }
            }
            if (editPolygonsMappings2.model != null) {
                model = editPolygonsMappings2.model;
            }

            BufferedImage currentZbufferImage = editPolygonsMappings2.testHumanHeadTexturing.zBufferImage();
            if (editPolygonsMappings2.inImageType == EditPolygonsMappings.MULTIPLE
                    && editPolygonsMappings2.inTxtType == EditPolygonsMappings.MULTIPLE
                    && editPolygonsMappings2.outTxtType == EditPolygonsMappings.SINGLE) {
                for (int j = 0; j < imagesIn.length; j++) {
                    File ii = imagesIn[j];
                    File ti = txtIn[j];
                    File to = txtout[j];
                    editPolygonsMappings2.loadImage(ii);
                    editPolygonsMappings2.loadTxt(ti);
                    editPolygonsMappings2.loadImage(to);

                    startRenderer(null);

                    while (editPolygonsMappings2.testHumanHeadTexturing.zBufferImage() == currentZbufferImage && j < imagesIn.length) {


                        try {
                            Thread.sleep(199);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        currentZbufferImage = editPolygonsMappings2.testHumanHeadTexturing.zBufferImage();
                    }

                    ImageIO.write(currentZbufferImage, "xjpg", new File(config.getDefaultFileOutput() + File.separator + String.format("FRAME%d.jpg", j)));

                }
            }
        });
        videoCreationThread.start();
    }

    private void loadMovieIn(ActionEvent e) {
        JFileChooser loadImageDeformed = new JFileChooser();
        loadImageDeformed.setDialogType(JFileChooser.DIRECTORIES_ONLY);
        if (lastDirectory != null)
            loadImageDeformed.setCurrentDirectory(lastDirectory);
        if (loadImageDeformed.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            editPolygonsMappings2.loadImages(loadImageDeformed.getSelectedFile());
        }
        lastDirectory = loadImageDeformed.getCurrentDirectory();
    }

    private void loadResultsFromVideoLeft(ActionEvent e) {
        JFileChooser loadImageDeformed = new JFileChooser();
        loadImageDeformed.setDialogType(JFileChooser.DIRECTORIES_ONLY);
        if (lastDirectory != null)
            loadImageDeformed.setCurrentDirectory(lastDirectory);
        if (loadImageDeformed.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            editPolygonsMappings2.loadTxtVideoDirectory(loadImageDeformed.getSelectedFile());
        }
        lastDirectory = loadImageDeformed.getCurrentDirectory();
    }

    private void loadResultsFromVideoRight(ActionEvent e) {
        JFileChooser loadImageDeformed = new JFileChooser();
        if (lastDirectory != null)
            loadImageDeformed.setCurrentDirectory(lastDirectory);
        if (loadImageDeformed.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            editPolygonsMappings2.loadTxtOutVideoDirectory(loadImageDeformed.getSelectedFile());
        }
        lastDirectory = loadImageDeformed.getCurrentDirectory();
    }

    private void chargeVideoDirectory(ActionEvent e) {
        JFileChooser loadImageDeformed = new JFileChooser();
        if (lastDirectory != null)
            loadImageDeformed.setCurrentDirectory(lastDirectory);
        if (loadImageDeformed.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            editPolygonsMappings2.imagesDirectory = loadImageDeformed.getSelectedFile();
        }
        lastDirectory = loadImageDeformed.getCurrentDirectory();
    }

    private void faceDetector(ActionEvent e) {
        try {
            String s = (editPolygonsMappings2.txtFile != null ? editPolygonsMappings2.txtFile.getAbsolutePath() :
                    editPolygonsMappings2.imageFile.getAbsolutePath()) + ".txt";
            FaceDetectApp.main(new String[]{editPolygonsMappings2.imageFile.getAbsolutePath(),
                    editPolygonsMappings2.imageFile.getAbsolutePath() + ".jpg", s});
            editPolygonsMappings2.loadTxt(new File(s));
        } catch (IOException | GeneralSecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void menuItemHDTextures(ActionEvent e) {
        editPolygonsMappings2.hasChangedAorB = editPolygonsMappings2.hdTextures != ((JCheckBoxMenuItem) (e.getSource())).isSelected();
        editPolygonsMappings2.hdTextures = ((JCheckBoxMenuItem) (e.getSource())).isSelected();
    }

    private void wiredTextures(ActionEvent e) {
        editPolygonsMappings2.textureWired = ((JCheckBoxMenuItem) (e.getSource())).isSelected();
    }

    private void textureDirect(ActionEvent e) {
        //editPolygonsMappings2.iTextureMorphMove.setDistanceABclass(DistanceProxLinear3.class);
        editPolygonsMappings2.distanceABClass = DistanceIdent.class;
        editPolygonsMappings2.hasChangedAorB = true;

    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        ResourceBundle bundle = ResourceBundle.getBundle("one.empty3.library.core.testing.Bundle");
        menuBar1 = new JMenuBar();
        menu2 = new JMenu();
        menuItem1 = new JMenuItem();
        menuItemChargeVideoDirectory = new JMenuItem();
        menuItem4 = new JMenuItem();
        menuItem3 = new JMenuItem();
        menuItemLoadResultsFromVideoLeft = new JMenuItem();
        menuItemLoadTxtOut = new JMenuItem();
        menuItemLoadResultsFromVideoRight = new JMenuItem();
        menuItem12 = new JMenuItem();
        menuItem8 = new JMenuItem();
        menuItemFaceDetector = new JMenuItem();
        menu7 = new JMenu();
        menu1 = new JMenu();
        menuItemAddPoint = new JMenuItem();
        menuItemMoveLinesDown = new JMenuItem();
        menuItemMoveLinesLeft = new JMenuItem();
        menuItemMoveColumnsRight = new JMenuItem();
        menuItemMoveRectangle = new JMenuItem();
        menuItem5 = new JMenuItem();
        menuItem6 = new JMenuItem();
        menuItem7 = new JMenuItem();
        menuItemwiredTextures = new JMenuItem();
        menu6 = new JMenu();
        menuItemLoadedModel = new JMenuItem();
        menuItemAddPlane = new JMenuItem();
        menu4 = new JMenu();
        menuItem10 = new JMenuItem();
        menuItem11 = new JMenuItem();
        menuItemStartRenderer = new JMenuItem();
        menuItemRenderVideo = new JMenuItem();
        menuItemStopRenderer = new JMenuItem();
        menuItemStopRender = new JMenuItem();
        menu5 = new JMenu();
        menuItemHDTextures = new JCheckBoxMenuItem();
        checkBoxMenuItemTypeShapePolyogns = new JCheckBoxMenuItem();
        checkBoxMenuItemTypeShapeBezier = new JCheckBoxMenuItem();
        checkBoxMenuItem1 = new JCheckBoxMenuItem();
        checkBoxMenuItemOptimizeGrid = new JCheckBoxMenuItem();
        checkBoxRefiineMatric = new JCheckBoxMenuItem();
        menuItemDistanceBB = new JMenuItem();
        menuItemTextureDict = new JRadioButtonMenuItem();
        menuItemDistBezier2 = new JRadioButtonMenuItem();
        menuItemDistLinearProx1 = new JRadioButtonMenuItem();
        menuItemDistLinearProx2 = new JRadioButtonMenuItem();
        menuItemDistLinearProx3 = new JRadioButtonMenuItem();
        menuItemDistanceLinear4 = new JRadioButtonMenuItem();
        editPolygonsMappings2 = new EditPolygonsMappings();
        menu3 = new JMenu();

        //======== this ========
        setMinimumSize(new Dimension(830, 600));
        setTitle(bundle.getString("JFrameEditPolygonsMappings.this.title"));
        setMaximizedBounds(null);
        setIconImage(new ImageIcon("D:\\Current\\empty3-library-generic\\mite.png").getImage());
        setFocusTraversalPolicyProvider(true);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                thisWindowClosing(e);
            }
        });
        var contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
            "fill,novisualpadding,hidemode 3",
            // columns
            "[fill]",
            // rows
            "[]"));

        //======== menuBar1 ========
        {
            menuBar1.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    menuBar1MouseEnteredMenu(e);
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    menuBar1MouseExited(e);
                }
            });

            //======== menu2 ========
            {
                menu2.setText(bundle.getString("JFrameEditPolygonsMappings.menu2.text"));

                //---- menuItem1 ----
                menuItem1.setText(bundle.getString("JFrameEditPolygonsMappings.menuItem1.text"));
                menuItem1.addActionListener(e -> menuItemLoadImage(e));
                menu2.add(menuItem1);

                //---- menuItemChargeVideoDirectory ----
                menuItemChargeVideoDirectory.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemChargeVideoDirectory.text"));
                menuItemChargeVideoDirectory.addActionListener(e -> chargeVideoDirectory(e));
                menu2.add(menuItemChargeVideoDirectory);

                //---- menuItem4 ----
                menuItem4.setText(bundle.getString("JFrameEditPolygonsMappings.menuItem4.text"));
                menuItem4.addActionListener(e -> menuItemAdd3DModel(e));
                menu2.add(menuItem4);

                //---- menuItem3 ----
                menuItem3.setText(bundle.getString("JFrameEditPolygonsMappings.menuItem3.text"));
                menuItem3.addActionListener(e -> menuItemLoadTxt(e));
                menu2.add(menuItem3);

                //---- menuItemLoadResultsFromVideoLeft ----
                menuItemLoadResultsFromVideoLeft.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemLoadResultsFromVideoLeft.text"));
                menuItemLoadResultsFromVideoLeft.addActionListener(e -> {
			loadResultsFromVideoLeft(e);
			loadResultsFromVideoLeft(e);
		});
                menu2.add(menuItemLoadResultsFromVideoLeft);

                //---- menuItemLoadTxtOut ----
                menuItemLoadTxtOut.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemLoadTxtOut.text"));
                menuItemLoadTxtOut.addActionListener(e -> loadTxtOut(e));
                menu2.add(menuItemLoadTxtOut);

                //---- menuItemLoadResultsFromVideoRight ----
                menuItemLoadResultsFromVideoRight.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemLoadResultsFromVideoRight.text"));
                menuItemLoadResultsFromVideoRight.addActionListener(e -> loadResultsFromVideoRight(e));
                menu2.add(menuItemLoadResultsFromVideoRight);

                //---- menuItem12 ----
                menuItem12.setText(bundle.getString("JFrameEditPolygonsMappings.menuItem12.text"));
                menuItem12.addActionListener(e -> menuItemLandmarksLeft(e));
                menu2.add(menuItem12);

                //---- menuItem8 ----
                menuItem8.setText(bundle.getString("JFrameEditPolygonsMappings.menuItem8.text"));
                menuItem8.addActionListener(e -> menuItemSaveModifiedVertex(e));
                menu2.add(menuItem8);

                //---- menuItemFaceDetector ----
                menuItemFaceDetector.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemFaceDetector.text"));
                menuItemFaceDetector.addActionListener(e -> faceDetector(e));
                menu2.add(menuItemFaceDetector);
            }
            menuBar1.add(menu2);

            //======== menu7 ========
            {
                menu7.setText(bundle.getString("JFrameEditPolygonsMappings.menu7.text"));
            }
            menuBar1.add(menu7);

            //======== menu1 ========
            {
                menu1.setText(bundle.getString("JFrameEditPolygonsMappings.menu1.text"));

                //---- menuItemAddPoint ----
                menuItemAddPoint.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemAddPoint.text"));
                menuItemAddPoint.addActionListener(e -> {
			addPoint(e);
			addPoint(e);
		});
                menu1.add(menuItemAddPoint);

                //---- menuItemMoveLinesDown ----
                menuItemMoveLinesDown.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemMoveLinesDown.text"));
                menu1.add(menuItemMoveLinesDown);

                //---- menuItemMoveLinesLeft ----
                menuItemMoveLinesLeft.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemMoveLinesLeft.text"));
                menu1.add(menuItemMoveLinesLeft);

                //---- menuItemMoveColumnsRight ----
                menuItemMoveColumnsRight.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemMoveColumnsRight.text"));
                menu1.add(menuItemMoveColumnsRight);

                //---- menuItemMoveRectangle ----
                menuItemMoveRectangle.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemMoveRectangle.text"));
                menu1.add(menuItemMoveRectangle);

                //---- menuItem5 ----
                menuItem5.setText(bundle.getString("JFrameEditPolygonsMappings.menuItem5.text"));
                menu1.add(menuItem5);

                //---- menuItem6 ----
                menuItem6.setText(bundle.getString("JFrameEditPolygonsMappings.menuItem6.text"));
                menuItem6.setFocusable(true);
                menuItem6.setFocusCycleRoot(true);
                menuItem6.setFocusPainted(true);
                menuItem6.setFocusTraversalPolicyProvider(true);
                menuItem6.addActionListener(e -> {
			menuItemSelectPoint(e);
			panelModelViewMouseClicked(e);
		});
                menu1.add(menuItem6);

                //---- menuItem7 ----
                menuItem7.setText(bundle.getString("JFrameEditPolygonsMappings.menuItem7.text"));
                menu1.add(menuItem7);

                //---- menuItemwiredTextures ----
                menuItemwiredTextures.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemwiredTextures.text"));
                menuItemwiredTextures.addActionListener(e -> wiredTextures(e));
                menu1.add(menuItemwiredTextures);
            }
            menuBar1.add(menu1);

            //======== menu6 ========
            {
                menu6.setText(bundle.getString("JFrameEditPolygonsMappings.menu6.text"));

                //---- menuItemLoadedModel ----
                menuItemLoadedModel.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemLoadedModel.text"));
                menu6.add(menuItemLoadedModel);

                //---- menuItemAddPlane ----
                menuItemAddPlane.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemAddPlane.text"));
                menuItemAddPlane.addActionListener(e -> {
			photoPlaneRepresentable(e);
			addPlane(e);
		});
                menu6.add(menuItemAddPlane);
            }
            menuBar1.add(menu6);

            //======== menu4 ========
            {
                menu4.setText(bundle.getString("JFrameEditPolygonsMappings.menu4.text"));

                //---- menuItem10 ----
                menuItem10.setText(bundle.getString("JFrameEditPolygonsMappings.menuItem10.text"));
                menuItem10.addActionListener(e -> menuItemHD(e));
                menu4.add(menuItem10);

                //---- menuItem11 ----
                menuItem11.setText(bundle.getString("JFrameEditPolygonsMappings.menuItem11.text"));
                menuItem11.addActionListener(e -> menuItem4K(e));
                menu4.add(menuItem11);

                //---- menuItemStartRenderer ----
                menuItemStartRenderer.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemStartRenderer.text"));
                menuItemStartRenderer.setBackground(new Color(0x00ff66));
                menuItemStartRenderer.addActionListener(e -> startRenderer(e));
                menu4.add(menuItemStartRenderer);

                //---- menuItemRenderVideo ----
                menuItemRenderVideo.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemRenderVideo.text"));
                menuItemRenderVideo.addActionListener(e -> renderVideo(e));
                menu4.add(menuItemRenderVideo);

                //---- menuItemStopRenderer ----
                menuItemStopRenderer.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemStopRenderer.text"));
                menuItemStopRenderer.setBackground(new Color(0xff3300));
                menuItemStopRenderer.addActionListener(e -> stopRenderer(e));
                menu4.add(menuItemStopRenderer);

                //---- menuItemStopRender ----
                menuItemStopRender.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemStopRender.text"));
                menuItemStopRender.addActionListener(e -> stopRender(e));
                menu4.add(menuItemStopRender);
            }
            menuBar1.add(menu4);

            //======== menu5 ========
            {
                menu5.setText(bundle.getString("JFrameEditPolygonsMappings.menu5.text"));

                //---- menuItemHDTextures ----
                menuItemHDTextures.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemHDTextures.text"));
                menuItemHDTextures.addActionListener(e -> menuItemHDTextures(e));
                menu5.add(menuItemHDTextures);

                //---- checkBoxMenuItemTypeShapePolyogns ----
                checkBoxMenuItemTypeShapePolyogns.setText(bundle.getString("JFrameEditPolygonsMappings.checkBoxMenuItemTypeShapePolyogns.text"));
                checkBoxMenuItemTypeShapePolyogns.setSelected(true);
                checkBoxMenuItemTypeShapePolyogns.addActionListener(e -> checkBoxMenuItemPoly(e));
                menu5.add(checkBoxMenuItemTypeShapePolyogns);

                //---- checkBoxMenuItemTypeShapeBezier ----
                checkBoxMenuItemTypeShapeBezier.setText(bundle.getString("JFrameEditPolygonsMappings.checkBoxMenuItemTypeShapeBezier.text"));
                checkBoxMenuItemTypeShapeBezier.setSelected(true);
                checkBoxMenuItemTypeShapeBezier.addActionListener(e -> checkBoxMenuItemBezier(e));
                menu5.add(checkBoxMenuItemTypeShapeBezier);

                //---- checkBoxMenuItem1 ----
                checkBoxMenuItem1.setText(bundle.getString("JFrameEditPolygonsMappings.checkBoxMenuItem1.text"));
                checkBoxMenuItem1.addActionListener(e -> {
			checkBoxMenuItem1(e);
			checkBoxMenuItem1(e);
			checkBoxMenuItem1(e);
			checkBoxMenuItem1(e);
		});
                menu5.add(checkBoxMenuItem1);

                //---- checkBoxMenuItemOptimizeGrid ----
                checkBoxMenuItemOptimizeGrid.setText(bundle.getString("JFrameEditPolygonsMappings.checkBoxMenuItemOptimizeGrid.text"));
                checkBoxMenuItemOptimizeGrid.addActionListener(e -> optimizeGrid(e));
                menu5.add(checkBoxMenuItemOptimizeGrid);

                //---- checkBoxRefiineMatric ----
                checkBoxRefiineMatric.setText(bundle.getString("JFrameEditPolygonsMappings.checkBoxRefiineMatric.text"));
                checkBoxRefiineMatric.addActionListener(e -> checkBoxRefiineMatric(e));
                menu5.add(checkBoxRefiineMatric);

                //---- menuItemDistanceBB ----
                menuItemDistanceBB.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemDistanceBB.text"));
                menuItemDistanceBB.addActionListener(e -> menuItem1DistanceBB(e));
                menu5.add(menuItemDistanceBB);

                //---- menuItemTextureDict ----
                menuItemTextureDict.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemTextureDict.text"));
                menuItemTextureDict.addActionListener(e -> textureDirect(e));
                menu5.add(menuItemTextureDict);

                //---- menuItemDistBezier2 ----
                menuItemDistBezier2.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemDistBezier2.text"));
                menuItemDistBezier2.addActionListener(e -> {
			menuItemClassBezier2(e);
			menuItemDistBezier2(e);
		});
                menu5.add(menuItemDistBezier2);

                //---- menuItemDistLinearProx1 ----
                menuItemDistLinearProx1.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemDistLinearProx1.text"));
                menuItemDistLinearProx1.addActionListener(e -> menuItemLinearProx1(e));
                menu5.add(menuItemDistLinearProx1);

                //---- menuItemDistLinearProx2 ----
                menuItemDistLinearProx2.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemDistLinearProx2.text"));
                menuItemDistLinearProx2.addActionListener(e -> menuItemLinearProx2(e));
                menu5.add(menuItemDistLinearProx2);

                //---- menuItemDistLinearProx3 ----
                menuItemDistLinearProx3.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemDistLinearProx3.text"));
                menuItemDistLinearProx3.addActionListener(e -> menuItemLinearProx3(e));
                menu5.add(menuItemDistLinearProx3);

                //---- menuItemDistanceLinear4 ----
                menuItemDistanceLinear4.setText(bundle.getString("JFrameEditPolygonsMappings.menuItemDistanceLinear4.text"));
                menuItemDistanceLinear4.addActionListener(e -> distanceLinear4(e));
                menu5.add(menuItemDistanceLinear4);
            }
            menuBar1.add(menu5);
        }
        setJMenuBar(menuBar1);

        //---- editPolygonsMappings2 ----
        editPolygonsMappings2.setMinimumSize(new Dimension(800, 600));
        contentPane.add(editPolygonsMappings2, "cell 0 0");

        //======== menu3 ========
        {
            menu3.setText(bundle.getString("JFrameEditPolygonsMappings.menu3.text"));
            menu3.setVisible(false);
        }
        contentPane.add(menu3, "cell 0 0");
        setLocationRelativeTo(getOwner());

        //---- buttonGroup1 ----
        var buttonGroup1 = new ButtonGroup();
        buttonGroup1.add(checkBoxMenuItemTypeShapePolyogns);
        buttonGroup1.add(checkBoxMenuItemTypeShapeBezier);

        //---- buttonGroup2 ----
        var buttonGroup2 = new ButtonGroup();
        buttonGroup2.add(menuItemTextureDict);
        buttonGroup2.add(menuItemDistBezier2);
        buttonGroup2.add(menuItemDistLinearProx1);
        buttonGroup2.add(menuItemDistLinearProx2);
        buttonGroup2.add(menuItemDistLinearProx3);
        buttonGroup2.add(menuItemDistanceLinear4);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    private void stopRender(ActionEvent e) {
        stopRenderer(e);
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JMenuBar menuBar1;
    private JMenu menu2;
    private JMenuItem menuItem1;
    private JMenuItem menuItemChargeVideoDirectory;
    private JMenuItem menuItem4;
    private JMenuItem menuItem3;
    private JMenuItem menuItemLoadResultsFromVideoLeft;
    private JMenuItem menuItemLoadTxtOut;
    private JMenuItem menuItemLoadResultsFromVideoRight;
    private JMenuItem menuItem12;
    private JMenuItem menuItem8;
    private JMenuItem menuItemFaceDetector;
    private JMenu menu7;
    private JMenu menu1;
    private JMenuItem menuItemAddPoint;
    private JMenuItem menuItemMoveLinesDown;
    private JMenuItem menuItemMoveLinesLeft;
    private JMenuItem menuItemMoveColumnsRight;
    private JMenuItem menuItemMoveRectangle;
    private JMenuItem menuItem5;
    private JMenuItem menuItem6;
    private JMenuItem menuItem7;
    private JMenuItem menuItemwiredTextures;
    private JMenu menu6;
    private JMenuItem menuItemLoadedModel;
    private JMenuItem menuItemAddPlane;
    private JMenu menu4;
    private JMenuItem menuItem10;
    private JMenuItem menuItem11;
    private JMenuItem menuItemStartRenderer;
    private JMenuItem menuItemRenderVideo;
    private JMenuItem menuItemStopRenderer;
    private JMenuItem menuItemStopRender;
    private JMenu menu5;
    private JCheckBoxMenuItem menuItemHDTextures;
    private JCheckBoxMenuItem checkBoxMenuItemTypeShapePolyogns;
    private JCheckBoxMenuItem checkBoxMenuItemTypeShapeBezier;
    private JCheckBoxMenuItem checkBoxMenuItem1;
    private JCheckBoxMenuItem checkBoxMenuItemOptimizeGrid;
    private JCheckBoxMenuItem checkBoxRefiineMatric;
    private JMenuItem menuItemDistanceBB;
    private JRadioButtonMenuItem menuItemTextureDict;
    private JRadioButtonMenuItem menuItemDistBezier2;
    private JRadioButtonMenuItem menuItemDistLinearProx1;
    private JRadioButtonMenuItem menuItemDistLinearProx2;
    private JRadioButtonMenuItem menuItemDistLinearProx3;
    private JRadioButtonMenuItem menuItemDistanceLinear4;
    EditPolygonsMappings editPolygonsMappings2;
    private JMenu menu3;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on

    public static void main(String[] args) {
        JFrameEditPolygonsMappings jFrameEditPolygonsMappings = new JFrameEditPolygonsMappings();
    }

    @Override
    public void dispose() {
        super.dispose();
        System.exit(0);
    }
}
