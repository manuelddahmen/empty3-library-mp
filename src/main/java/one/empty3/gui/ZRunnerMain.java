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

package one.empty3.gui;

import one.empty3.*;
import one.empty3.library.*;

import javax.imageio.ImageIO;
import java.awt.*;

import one.empty3.libs.Image;
import one.empty3.ECImage;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ConcurrentModificationException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by manue on 26-06-19.
 */
public class ZRunnerMain extends Thread implements PropertyChangeListener {
    private final Logger log;
    private UpdateViewMain updateViewMain = null;
    private boolean running = true;
    private Image lastImage;
    private ITexture iTexture;
    String x = "0", y = "0", z = "0";
    double u0, u1, v0, v1;
    private ZBufferImpl zBuffer;
    private boolean propertyChanged = false;
    private boolean updateGraphics = false;
    private Main main;
    private Scene scene;
    private PropertyChangeListener changeListener;
    private boolean stopCurrentRender;
    private boolean graphicalEditing;
    private boolean selRot;
    private Thread consumer;


    public ZRunnerMain() {
        log = Logger.getAnonymousLogger();
        log.setLevel(Level.FINEST);
        running = true;
        zBuffer = null;
        Logger.getAnonymousLogger().log(Level.INFO, "ZRunner new instance");
        start();
    }


    public LineSegment getClick(int x, int y) {
        throw new UnsupportedOperationException("No click");
    }

    public Image getLastImage() {
        return lastImage;
    }

    public void setiTexture(ITexture iTexture) {
        this.iTexture = iTexture;
    }

    public void update() {
        updateGraphics = true;
    }


    public void run() {
        boolean renderedImageOK = false;
        log.info("running renderer loop....");
        new Thread() {
            @Override
            public void run() {
                super.run();
                while (running) {
                    if (updateViewMain != null && updateViewMain.getWidth() > 0 && updateViewMain.getHeight() > 0) {
                        Graphics updateViewGraphics = updateViewMain.getGraphics();
                        if (lastImage != null) {
                            updateViewGraphics.drawImage(lastImage, 0, 0, updateViewMain.getWidth(), updateViewMain.getHeight(), null);
                        }
                    }
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
        ThreadGraphicalEditor threadGraphicalEditor = new ThreadGraphicalEditor(getMain());
        threadGraphicalEditor.setMain(getMain());
        threadGraphicalEditor.start();
        changeSupport.addPropertyChangeListener(threadGraphicalEditor);
        while (isRunning()) {
            if (main != null)
                updateViewMain = main.getUpdateView();
            if (updateViewMain != null && updateViewMain.getWidth() > 0 && updateViewMain.getHeight() > 0) {

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (zBuffer == null || zBuffer.largeur() != updateViewMain.getWidth() || updateViewMain.getHeight() != zBuffer.hauteur()) {
                    zBuffer = new ZBufferImpl(updateViewMain.getWidth(), updateViewMain.getHeight());
                    log.info("UpdateView" + updateViewMain.getWidth() + ", " + updateViewMain.getHeight() + " " + updateViewMain.hashCode());
                    log.info("Zbuffer dim" + zBuffer.largeur() + ", " + zBuffer.hauteur());
                }
                //zBuffer.setDimension(updateViewMain.getWidth(), updateViewMain.getHeight());

                try {
                    Scene scene = getMain().getDataModel().getScene();

                    zBuffer.scene(scene);

                    zBuffer.camera(scene.cameraActive());
                    scene.cameraActive.getElem().calculerMatrice(scene.cameraActive.getElem().getVerticale());
                    zBuffer.setDisplayType(updateViewMain.getView().getzDiplayType());
                    showRepere(zBuffer);
                    zBuffer.next();
                    zBuffer.draw(scene);
                    addRepere(scene);
                    lastImage = zBuffer.image();
                    changeSupport.firePropertyChange("renderedImageOK", null, lastImage);
                    renderedImageOK = true;
                    propertyChanged = false;
                    updateGraphics = false;
                    drawSuccess();

                } catch (NullPointerException ex) {
                    changeSupport.firePropertyChange("renderedImageOK", null, 0);
                    drawFailed();
                    renderedImageOK = true;
                    ex.printStackTrace();
                } catch (ConcurrentModificationException ex) {
                    changeSupport.firePropertyChange("renderedImageOK", null, 0);
                    drawFailed();
                    renderedImageOK = true;
                    log.warning("Wait concurrent modification");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    drawFailed();
                    changeSupport.firePropertyChange("renderedImageOK", null, -1);
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info("Ending renderer loop....");
    }

    private void drawSuccess() {
        Graphics graphics = updateViewMain.getGraphics();
        graphics.setColor(Color.GREEN);
        try {
            graphics.drawImage(ImageIO.read(new File("resources/img/RENDEREDOK.PNG")), 0, 0, 50, 50, null);
        } catch (Exception ex) {
        }

    }

    private void drawFailed() {
        Graphics graphics = updateViewMain.getGraphics();
        graphics.setColor(Color.RED);
        try {
            graphics.drawImage(ImageIO.read(new File("resources/img/FAILED.PNG")), 0, 0, 50, 50, null);
        } catch (Exception ex) {
        }

    }

    private void addRepere(Scene scene1) {
        LineSegment ls = new LineSegment(Point3D.O0, Point3D.X.mult(10d));
        ls.texture(new TextureCol(Color.RED));
        zBuffer.draw(ls);
        ls = new LineSegment(Point3D.O0, Point3D.Y.mult(10d));
        ls.texture(new TextureCol(Color.GREEN));
        zBuffer.draw(ls);
        ls = new LineSegment(Point3D.O0, Point3D.Z.mult(10d));
        ls.texture(new TextureCol(Color.BLUE));
        zBuffer.draw(ls);
    }


    public boolean isRunning() {
        return running;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "scene":
                scene = (Scene) evt.getNewValue();
        }

    }


    private void showRepere(ZBuffer zBuffer) {
        Scene scene = new Scene();
        LineSegment ls = new LineSegment(Point3D.O0, Point3D.X);
        ls.texture(new TextureCol(Color.RED));
        scene.add(ls);
        ls = new LineSegment(Point3D.O0, Point3D.Y);
        ls.texture(new TextureCol(Color.GREEN));
        scene.add(ls);
        ls = new LineSegment(Point3D.O0, Point3D.Z);
        ls.texture(new TextureCol(Color.BLUE));
        scene.add(ls);
        zBuffer.draw(scene);
    }

    private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener main) {
        changeSupport.addPropertyChangeListener(main);
    }

    public void setStopCurrentRender(boolean stopCurrentRender) {
        this.stopCurrentRender = stopCurrentRender;
    }

    public void setLastImage(Image lastImage) {
        this.lastImage = lastImage;
    }

    public ZBufferImpl getzBuffer() {
        return zBuffer;
    }

    public void setzBuffer(ZBufferImpl zBuffer) {
        this.zBuffer = zBuffer;
    }

    public void setGraphicalEditing(boolean graphicalEditing) {
        this.graphicalEditing = graphicalEditing;
    }

    public boolean isGraphicalEditing() {
        return graphicalEditing;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public void setSelRot(boolean selRot) {
        this.selRot = selRot;
    }

    public boolean getSelRot() {
        return selRot;
    }

}
