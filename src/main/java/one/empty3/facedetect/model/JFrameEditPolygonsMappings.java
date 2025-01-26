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
 * Created by JFormDesigner on Sat May 18 12:25:12 CEST 2024
 */

package one.empty3.facedetect.model;

import one.empty3.facedetect.model.gcp.FaceDetectApp;
import one.empty3.facedetect.model.vecmesh.Rotate;
import one.empty3.library.Config;
import one.empty3.library.Point3D;
import one.empty3.library.Scene;
import one.empty3.library.objloader.E3Model;
import one.empty3.libs.Image;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author manue
 */
public class JFrameEditPolygonsMappings extends JFrame {

    private final EditPolygonsMappings editPolygonsMappings2;
    public double computeTimeMax;
    private Rotate rotate;


    public void setComputeMaxTime(double value) {
        this.computeTimeMax = value;
    }
    public double getComputeTimeMax() {
        return computeTimeMax;
    }

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

    public JFrameEditPolygonsMappings() {

        editPolygonsMappings2 = new EditPolygonsMappings();

        config = new Config();
        File fileDirectoryDefault = config.getDefaultFileOutput();
        if (fileDirectoryDefault == null)
            config.setDefaultFileOutput(new File("."));
        String lastDirectoryTmpStr = config.getMap().computeIfAbsent("D3ModelFaceTexturing", k -> ".");
        config.save();
        lastDirectory = new File(lastDirectoryTmpStr);
        if (!lastDirectory.exists())
            config.save();
        initParameters();
        threadDisplay = new Thread(editPolygonsMappings2);
        threadDisplay.start();

        Filter filter = new MyFilter();

        Logger.getAnonymousLogger().setFilter(filter);

    }

    public void initParameters() {
        if (editPolygonsMappings2 != null) {
            editPolygonsMappings2.model.opt1 = false;
            editPolygonsMappings2.model.hasChangedAorB = true;
            editPolygonsMappings2.model.distanceABClass = DistanceProxLinear2.class;
            //editPolygonsMappings2.iTextureMorphMoveImage = new TextureMorphMove();
            editPolygonsMappings2.model.optimizeGrid = false;
            editPolygonsMappings2.model.typeShape = DistanceAB.TYPE_SHAPE_QUADR;
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

    private void loadImageRight(ActionEvent e) {
        JFileChooser loadImage = new JFileChooser();
        if (lastDirectory != null)
            loadImage.setCurrentDirectory(lastDirectory);
        int ret = loadImage.showOpenDialog(this);
        if (ret == JFileChooser.APPROVE_OPTION) {
            editPolygonsMappings2.loadImageRight(loadImage.getSelectedFile());
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

            editPolygonsMappings2.model.isRunning = false;
            if (TestHumanHeadTexturing.threadTest != null) {
                editPolygonsMappings2.model.testHumanHeadTexturing.stop(); // TestObjet stop method
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
        private final E3Model model;
        private BufferedImage image;

        public SaveTexture(@NotNull BufferedImage image, @NotNull E3Model model) {
            this.model = model;
            this.image = image;

        }

        public BufferedImage computeTexture() {
            image = editPolygonsMappings2.model.image;
            TextureMorphMove iTextureMorphMoveImage = new TextureMorphMove(editPolygonsMappings2, editPolygonsMappings2.model.distanceABClass);
            BufferedImage imageOut = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            model.texture(iTextureMorphMoveImage);
            for (double u = 0; u < 1.0; u += 1.0 / image.getWidth()) {
                for (double v = 0; v < 1.0; v += 1.0 / image.getHeight()) {
                    int colorAt1 = editPolygonsMappings2.model.iTextureMorphMove.getColorAt(u, v);
                    imageOut.setRGB((int) Math.min(editPolygonsMappings2.model.image.getWidth() - 1, u * editPolygonsMappings2.model.image.getWidth()),
                            (int) Math.min(editPolygonsMappings2.model.image.getHeight() - 1, v * editPolygonsMappings2.model.image.getHeight()), colorAt1);
                }
                Logger.getAnonymousLogger().log(Level.INFO, "Image column #" + ((int) (u * 100)) + "% : done");
            }
            return imageOut;
        }
    }

    private void menuItemHD(ActionEvent e) {
        Runnable jpg = () -> {
            if (editPolygonsMappings2.model.image == null || editPolygonsMappings2.model.pointsInImage == null || editPolygonsMappings2.model.pointsInModel == null
                    || editPolygonsMappings2.model == null) {
                return;
            }
            /*
            TextureMorphMove textureMorphMoveImage = new TextureMorphMove(editPolygonsMappings2, DistanceProxLinear1.class);
            textureMorphMoveImage.distanceAB = new DistanceProxLinear1(editPolygonsMappings2.pointsInImage.values().stream().toList(),
                    editPolygonsMappings2.pointsInModel.values().stream().toList(),
                    new Dimension(editPolygonsMappings2.image.getWidth(),
                            editPolygonsMappings2.image.getHeight()), new Dimension(Resolution.HD1080RESOLUTION.x(), Resolution.HD1080RESOLUTION.y(), false, false)
            );

             */
            E3Model model = editPolygonsMappings2.model.model;
            File defaultFileOutput = config.getDefaultFileOutput();
            SaveTexture saveTexture = new SaveTexture(editPolygonsMappings2.model.image, model);
            BufferedImage bufferedImage = saveTexture.computeTexture();
            File file = new File(config.getDefaultFileOutput()
                    + File.separator + "output-face-on-model-texture" + UUID.randomUUID() + ".jpg");
            new Image(bufferedImage).saveToFile(file.getAbsolutePath());

            Logger.getAnonymousLogger().log(Level.INFO, file.getAbsolutePath());
        };
        Thread thread = new Thread(jpg);
        thread.start();
    }

    private void menuItem4K(ActionEvent e) {
        menuItemHD(e);
    }


    private void menuBar1FocusLost(FocusEvent e) {
        editPolygonsMappings2.model.notMenuOpen = true;
    }

    private void checkBoxMenuItemShapeTypePolygons(ActionEvent e) {
        if (e.getSource() instanceof JCheckBoxMenuItem r) {
            if (r.isSelected()) {
                editPolygonsMappings2.model.iTextureMorphMove.distanceAB.typeShape = DistanceAB.TYPE_SHAPE_QUADR;
                editPolygonsMappings2.model.typeShape = DistanceAB.TYPE_SHAPE_QUADR;
            }
        }
        editPolygonsMappings2.model.hasChangedAorB = true;
    }

    private void checkBoxMenuItemTypeShapeBezier(ActionEvent e) {
        if (e.getSource() instanceof JCheckBoxMenuItem r) {
            if (r.isSelected()) {
                editPolygonsMappings2.model.iTextureMorphMove.distanceAB.typeShape = DistanceAB.TYPE_SHAPE_BEZIER;
                editPolygonsMappings2.model.typeShape = DistanceAB.TYPE_SHAPE_BEZIER;
            }
        }
        editPolygonsMappings2.model.hasChangedAorB = true;
    }

    private void checkBoxMenuItem1(ActionEvent e) {
        if (e.getSource() instanceof JCheckBoxMenuItem r) {
            if (r.isSelected()) {
                editPolygonsMappings2.model.iTextureMorphMove.distanceAB.opt1 = true;
                editPolygonsMappings2.model.opt1 = true;
            } else {
                editPolygonsMappings2.model.iTextureMorphMove.distanceAB.opt1 = true;
                editPolygonsMappings2.model.opt1 = true;
            }
        }
        editPolygonsMappings2.model.hasChangedAorB = true;
    }

    private void menuItemClassBezier2(ActionEvent e) {
        //editPolygonsMappings2.iTextureMorphMove.setDistanceABclass(DistanceBezier3.class);
        editPolygonsMappings2.model.distanceABClass = DistanceBezier3.class;
        editPolygonsMappings2.model.hasChangedAorB = true;
    }

    private void menuItem1DistanceBB(ActionEvent e) {
        ///editPolygonsMappings2.iTextureMorphMove.setDistanceABclass(DistanceBB.class);
        editPolygonsMappings2.model.distanceABClass = DistanceBB.class;
        editPolygonsMappings2.model.hasChangedAorB = true;
    }

    private void menuItemLinearProx1(ActionEvent e) {
        //editPolygonsMappings2.iTextureMorphMove.setDistanceABclass(DistanceProxLinear1.class);
        editPolygonsMappings2.model.distanceABClass = DistanceProxLinear1.class;
        editPolygonsMappings2.model.hasChangedAorB = true;
    }

    private void menuItemLinearProx2(ActionEvent e) {
        //editPolygonsMappings2.iTextureMorphMove.setDistanceABclass(DistanceProxLinear2.class);
        editPolygonsMappings2.model.distanceABClass = DistanceProxLinear2.class;
        editPolygonsMappings2.model.hasChangedAorB = true;
    }

    private void menuItemLinearProx3(ActionEvent e) {
        //editPolygonsMappings2.iTextureMorphMove.setDistanceABclass(DistanceProxLinear3.class);
        editPolygonsMappings2.model.distanceABClass = DistanceProxLinear3.class;
        editPolygonsMappings2.model.hasChangedAorB = true;
    }

    private void distanceLinear4(ActionEvent e) {
        //editPolygonsMappings2.iTextureMorphMove.setDistanceABclass(DistanceProxLinear3.class);
        editPolygonsMappings2.model.distanceABClass = DistanceProxLinear4.class;
        //editPolygonsMappings2.hasChangedAorB = true;
    }

    private void menuItem4Plus(ActionEvent e) {
        try {
            String name = ((JMenuItem) (e.getSource())).getText();
            Class<?> aClass = Class.forName("one.empty3.apps.facedetect.DistanceProxLinear" + name);
            editPolygonsMappings2.model.distanceABClass = (Class<? extends DistanceAB>) aClass;
            editPolygonsMappings2.model.hasChangedAorB = true;

        } catch (ClassNotFoundException | ClassCastException ex) {
            ex.printStackTrace();
        }
    }

    private void optimizeGrid(ActionEvent e) {
        if (e.getSource() instanceof JCheckBoxMenuItem r) {

            editPolygonsMappings2.model.iTextureMorphMove.distanceAB.optimizeGrid = (r.isSelected());
        }
        editPolygonsMappings2.model.hasChangedAorB = true;
    }

    private void menuBar1MouseEnteredMenu(MouseEvent e) {
        editPolygonsMappings2.model.notMenuOpen = false;
    }

    private void menuBar1MouseExited(MouseEvent e) {
        editPolygonsMappings2.model.notMenuOpen = true;

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
        editPolygonsMappings2.model.distanceABClass = DistanceProxLinear43.class;
        editPolygonsMappings2.model.hasChangedAorB = true;

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
        if (editPolygonsMappings2.model.pointsInImage != null && editPolygonsMappings2.model.pointsInModel != null) {
            UUID num = UUID.randomUUID();
            editPolygonsMappings2.model.pointsInImage.put("LANDMARK_" + num, Point3D.O0);
            editPolygonsMappings2.model.pointsInModel.put("LANDMARK_" + num, Point3D.O0);
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
        editPolygonsMappings2.stopRenderer();
    }

    private void startRenderer(ActionEvent e) {

        editPolygonsMappings2.model.iTextureMorphMove = new TextureMorphMove(editPolygonsMappings2, editPolygonsMappings2.model.distanceABClass);

        if (editPolygonsMappings2.model.pointsInImage.size() >= 3 && editPolygonsMappings2.model.pointsInModel.size() >= 3) {
        }
        editPolygonsMappings2.model.threadDistanceIsNotRunning = true;
        editPolygonsMappings2.model.hasChangedAorB = true;
        editPolygonsMappings2.model.renderingStarted = true;

    }

    private void photoPlaneRepresentable(ActionEvent e) {
    }

    private void addPlane(ActionEvent e) {
        //editPolygonsMappings2.add3DModelFillPanel(new File("resources/models/plane blender2.obj"));
    }

    private void menuItemDistBezier2(ActionEvent e) {
        // TODO add your code here
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
            editPolygonsMappings2.model.durationMilliS = 30000;
            if (editPolygonsMappings2.model.inImageType == editPolygonsMappings2.model.MULTIPLE) {
                if (editPolygonsMappings2.model.imagesDirectory.isDirectory()) {
                    imagesIn = new File[editPolygonsMappings2.model.imagesDirectory.listFiles().length];
                    Object[] array = Arrays.stream(editPolygonsMappings2.model.imagesDirectory.listFiles()).sequential().filter(file -> file.getAbsolutePath().substring(-4).toLowerCase().equals(".jpg") || file.getAbsolutePath().substring(-4).toLowerCase().equals(".png")).toArray();
                    for (int i = 0; i < array.length; i++) {
                        imagesIn[i] = (File) array[i];
                    }
                }
            }
            if (editPolygonsMappings2.model.inTxtType == editPolygonsMappings2.model.MULTIPLE) {
                if (editPolygonsMappings2.model.txtInDirectory.isDirectory()) {
                    txtIn = new File[editPolygonsMappings2.model.txtInDirectory.listFiles().length];
                    Object[] array = Arrays.stream(editPolygonsMappings2.model.txtInDirectory.listFiles()).sequential().filter(file -> file.getAbsolutePath().substring(-4).toLowerCase().equals(".txt")).toArray();
                    for (int i = 0; i < array.length; i++) {
                        txtIn[i] = (File) array[i];
                    }
                }
            }
            if (editPolygonsMappings2.model.outTxtType == editPolygonsMappings2.model.MULTIPLE) {
                if (editPolygonsMappings2.model.txtOutDirectory.isDirectory()) {
                    txtout = new File[editPolygonsMappings2.model.txtOutDirectory.listFiles().length];
                    Object[] array = Arrays.stream(editPolygonsMappings2.model.txtInDirectory.listFiles()).sequential().filter(file -> file.getAbsolutePath().substring(-4).toLowerCase().equals(".txt")).toArray();
                    for (int i = 0; i < array.length; i++) {
                        txtout[i] = (File) array[i];
                    }
                }
            }
            if (editPolygonsMappings2.model != null) {
                model = editPolygonsMappings2.model.model;
            }

            BufferedImage currentZbufferImage = editPolygonsMappings2.model.testHumanHeadTexturing.zBufferImage();
            if (editPolygonsMappings2.model.inImageType == editPolygonsMappings2.model.MULTIPLE
                    && editPolygonsMappings2.model.inTxtType == editPolygonsMappings2.model.MULTIPLE
                    && editPolygonsMappings2.model.outTxtType == editPolygonsMappings2.model.SINGLE) {
                for (int j = 0; j < imagesIn.length; j++) {
                    File ii = imagesIn[j];
                    File ti = txtIn[j];
                    File to = txtout[j];
                    editPolygonsMappings2.loadImage(ii);
                    editPolygonsMappings2.loadTxt(ti);
                    editPolygonsMappings2.loadImage(to);

                    startRenderer(null);

                    while (editPolygonsMappings2.model.testHumanHeadTexturing.zBufferImage() == currentZbufferImage && j < imagesIn.length) {


                        try {
                            Thread.sleep(199);
                        } catch (InterruptedException ex) {
                            throw new RuntimeException(ex);
                        }
                        currentZbufferImage = editPolygonsMappings2.model.testHumanHeadTexturing.zBufferImage();
                    }

                    try {
                        javax.imageio.ImageIO.write(currentZbufferImage, "xjpg", new File(config.getDefaultFileOutput() + File.separator + String.format("FRAME%d.jpg", j)));
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }

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
            editPolygonsMappings2.model.imagesDirectory = loadImageDeformed.getSelectedFile();
        }
        lastDirectory = loadImageDeformed.getCurrentDirectory();
    }

    private void faceDetector(ActionEvent e) {
        try {
            String s = (editPolygonsMappings2.model.txtFile != null ? editPolygonsMappings2.model.txtFile.getAbsolutePath() :
                    editPolygonsMappings2.model.imageFile.getAbsolutePath()) + ".txt";
            FaceDetectApp.main(new String[]{editPolygonsMappings2.model.imageFile.getAbsolutePath(),
                    editPolygonsMappings2.model.imageFile.getAbsolutePath() + ".jpg", s});
            editPolygonsMappings2.loadTxt(new File(s));
        } catch (IOException | GeneralSecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void menuItemHDTextures(ActionEvent e) {
        editPolygonsMappings2.model.hasChangedAorB = editPolygonsMappings2.model.hdTextures != ((JCheckBoxMenuItem) (e.getSource())).isSelected();
        editPolygonsMappings2.model.hdTextures = ((JCheckBoxMenuItem) (e.getSource())).isSelected();
    }

    private void wiredTextures(ActionEvent e) {
        editPolygonsMappings2.model.textureWired = ((JCheckBoxMenuItem) (e.getSource())).isSelected();
    }

    private void textureDirect(ActionEvent e) {
        //editPolygonsMappings2.model.iTextureMorphMove.setDistanceABclass(DistanceProxLinear3.class);
        editPolygonsMappings2.model.distanceABClass = DistanceIdent.class;
        editPolygonsMappings2.model.hasChangedAorB = true;

    }

    private void saveImageLeft(ActionEvent e) {
        JFileChooser saveImageDeformed = new JFileChooser();
        if (lastDirectory != null)
            saveImageDeformed.setCurrentDirectory(lastDirectory);
        if (saveImageDeformed.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            new Image(editPolygonsMappings2.model.image).saveToFile( saveImageDeformed.getSelectedFile().getAbsolutePath());
        }
        lastDirectory = saveImageDeformed.getCurrentDirectory();
    }

    private void saveImageRight(ActionEvent e) {
        JFileChooser saveImageDeformed = new JFileChooser();
        if (lastDirectory != null)
            saveImageDeformed.setCurrentDirectory(lastDirectory);
        if (saveImageDeformed.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            new Image(editPolygonsMappings2.model.zBufferImage).saveToFile(saveImageDeformed.getSelectedFile().getAbsolutePath());
        }
        lastDirectory = saveImageDeformed.getCurrentDirectory();
    }


    private void refiineMatrix(ActionEvent e) {
        editPolygonsMappings2.model.aDimReduced = editPolygonsMappings2.model.iTextureMorphMove.distanceAB.aDimReduced;
        editPolygonsMappings2.model.aDimReduced.setSize(new Dimension((int) (editPolygonsMappings2.model.aDimReduced.getWidth() * 2), (int) (editPolygonsMappings2.model.aDimReduced.getHeight() * 5 + 1)));//Demeter
        editPolygonsMappings2.model.bDimReduced = editPolygonsMappings2.model.iTextureMorphMove.distanceAB.bDimReduced;
        editPolygonsMappings2.model.bDimReduced.setSize(new Dimension((int) (editPolygonsMappings2.model.bDimReduced.getWidth() * 2), (int) (editPolygonsMappings2.model.bDimReduced.getHeight() * 5 + 1)));
    }

    private void divideItem(ActionEvent e) {
        editPolygonsMappings2.model.aDimReduced = editPolygonsMappings2.model.iTextureMorphMove.distanceAB.aDimReduced;
        editPolygonsMappings2.model.aDimReduced.setSize(new Dimension((int) (editPolygonsMappings2.model.aDimReduced.getWidth() * 2), (int) (editPolygonsMappings2.model.aDimReduced.getHeight() / 5)));
        editPolygonsMappings2.model.bDimReduced = editPolygonsMappings2.model.iTextureMorphMove.distanceAB.bDimReduced;
        editPolygonsMappings2.model.bDimReduced.setSize(new Dimension((int) (editPolygonsMappings2.model.bDimReduced.getWidth() * 2), (int) (editPolygonsMappings2.model.bDimReduced.getHeight() / 5)));
    }

    private void buttonRenduFil(ActionEvent e) {
        // TODO add your code here
    }

    private void buttonRenduPlein(ActionEvent e) {
        // TODO add your code here
    }


    private void buttonRenderNow(ActionEvent e) {
        // TODO add your code here
    }

    private synchronized void moveLinesDown(ActionEvent e) {
        int selectedPointNo = editPolygonsMappings2.model.selectedPointNo;
        final HashMap<String, Point3D> pointsInModel = editPolygonsMappings2.model.pointsInModel;
        final HashMap<String, Point3D> pointsInImage = editPolygonsMappings2.model.pointsInImage;
        if (selectedPointNo >= 0 && selectedPointNo < editPolygonsMappings2.model.pointsInModel.size()) {
            final int[] i = {0};
            synchronized (pointsInModel) {
                pointsInModel.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        if (i[0] == selectedPointNo) {
                            pointsInModel.remove(s);
                            pointsInImage.remove(s);
                        }
                        i[0]++;
                    }
                });
            }
        }

    }

    private void menuItemModifiedVertex3(ActionEvent e) {
        JFileChooser loadImageDeformed = new JFileChooser();
        if (lastDirectory != null)
            loadImageDeformed.setCurrentDirectory(lastDirectory);
        if (loadImageDeformed.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            editPolygonsMappings2.loadTxt3(loadImageDeformed.getSelectedFile());
        }
        lastDirectory = loadImageDeformed.getCurrentDirectory();
    }


    private void stopRender(ActionEvent e) {
        stopRenderer(e);
    }


    public static void main(String[] args) {
        JFrameEditPolygonsMappings jFrameEditPolygonsMappings = new JFrameEditPolygonsMappings();
    }

    @Override
    public void dispose() {
        super.dispose();
        System.exit(0);
    }

    public Rotate getRotate() {
        return rotate;
    }

    public void setRotate(Rotate rotate) {
        this.rotate = rotate;
    }

    public EditPolygonsMappings getEditPolygonsMappings2() {
        return editPolygonsMappings2;
    }

    public void setEditPolygonsMappings2(EditPolygonsMappings editPolygonsMappings2) {
        editPolygonsMappings2 = editPolygonsMappings2;
    }
}
