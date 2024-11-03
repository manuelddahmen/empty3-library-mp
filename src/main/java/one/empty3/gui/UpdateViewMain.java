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
import one.empty3.library.core.nurbs.ParametricSurface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by manue on 26-06-19.
 */
public class UpdateViewMain extends JPanel implements RepresentableEditor {
    private Main main;
    private Scene scene;
    private Representable currentRepresentable;
    private MyObservableList<Representable> translate = null;
    private boolean graphicalEditing;
    private FunctionView view;
    private ZRunnerMain zRunner;
    private boolean psMode;
    private ParametricSurface ps;
    private double u;
    private double v;


    public UpdateViewMain() {
        setView(new FunctionView());
        setzRunner(new ZRunnerMain());
        addMouseListener(new MouseAdapter() {
            public Representable representable;
            public ThreadDrawing threadDrawing = null;
            public Point3D mousePoint3Dorig;
            Point3D mousePoint3D;
            Point mousePoint = null;
            ArcBall2 arcBall;

            @Override
            public void mousePressed(MouseEvent e) {
                Logger.getAnonymousLogger().log(Level.INFO, "Mouse Pressed MouseEvent e in select ROTATE");
                if (threadDrawing != null) {
                    threadDrawing.setRunning(false);
                    threadDrawing.setStop(true);
                    threadDrawing = null;
                }
                mousePoint = null;
                mousePoint3D = null;

                mousePoint3D = zRunner.getzBuffer().clickAt(e.getX(), e.getY());
                representable = zRunner.getzBuffer().representableAt(e.getX(), e.getY());

                if (main.getGraphicalEdit2().getActionToPerform().equals(GraphicalEdit2.Action.ROTATE)) {
                    Logger.getAnonymousLogger().log(Level.INFO, "Mouse Pressed");
                    Logger.getAnonymousLogger().log(Level.INFO, "Mouse starts dragging rotating");
                    arcBall = new ArcBall2(getzRunner().getzBuffer().camera(), mousePoint3D,
                            Math.atan(2.0 * Math.PI *
                                    e.getX() / getWidth()),

                            getzRunner().getzBuffer());
                    arcBall.init(representable);
                } else if (main.getGraphicalEdit2().getActionToPerform().equals(GraphicalEdit2.Action.SELECT)) {
                }

                if (threadDrawing == null) {
                    threadDrawing = new ThreadDrawing();
                    threadDrawing.start();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
               if (main.getGraphicalEdit2().getActionToPerform().equals(GraphicalEdit2.Action.ROTATE)) {
                    if (arcBall.matrix() != null) {
                        Logger.getAnonymousLogger().log(Level.INFO, "Mouse Released");
                        representable.getRotation().getElem().getRot().setElem(arcBall.matrix());
                        Logger.getAnonymousLogger().log(Level.INFO, "Matrix changed = " + representable.getRotation().getElem().getRot().getElem());
                        representable.getRotation().getElem().getCentreRot().setElem(mousePoint3D);
                        Logger.getAnonymousLogger().log(Level.INFO, "centreRot changed" + representable.getRotation().getElem().getCentreRot().getElem());
                        Logger.getAnonymousLogger().log(Level.INFO, "class re" + representable.getClass());
                    } else {
                        Logger.getAnonymousLogger().log(Level.INFO, "Matrix == null");
                    }
                }

                if (threadDrawing != null) {
                    threadDrawing.setRunning(false);
                    threadDrawing = null;
                }
                mousePoint3Dorig = null;
                mousePoint3D = null;
                mousePoint = null;
            }

            @Override
            public void mouseDragged(MouseEvent e) {

            }

            class ThreadDrawing extends Thread {
                boolean running;
                private boolean pause = false;
                private boolean isStopped;

                public void run() {
                    running = true;
                    while (isRunning()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Point location = MouseInfo.getPointerInfo().getLocation();
                    SwingUtilities.convertPointFromScreen(location, main.getUpdateView());
                    mousePoint = location;
                    try {
                        if (main.getGraphicalEdit2().getActionToPerform().equals(GraphicalEdit2.Action.ROTATE)) {
                            arcBall.moveTo((int) mousePoint.getX(), (int) mousePoint.getY());
                            ZBufferImpl zBuffer = main.getUpdateView().getzRunner().getzBuffer();
                            //unit3t2
                            Point p1 = zBuffer.camera().coordonneesPoint2D(mousePoint3D.plus(Point3D.random(1.0).norme1()), zBuffer);
                            Point p2 = zBuffer.camera().coordonneesPoint2D(mousePoint3D.plus(Point3D.random(1.0).norme1()), zBuffer);

                            double sqrt = Math.sqrt(p1.getX() * p2.getX() + p2.getY() + p2.getY());


                            Graphics graphics = getGraphics();
                            graphics.drawOval((int) (location.getX() - sqrt / 2), (int) (location.getY() - sqrt / 2),
                                    (int) (location.getX() + sqrt / 2), (int) (location.getY() + sqrt / 2));
                            arcBall.setRadius(10 * sqrt / getWidth());

                            //
                            //
                            //
                            //    arcBall.getRadius()), (int)location.getX()+unit3t2(arcBall.getRadius()));
////Logger.getAnonymousLogger().log(Level.INFO, "Mouse rotation moved");
                        } else if (main.getGraphicalEdit2().getActionToPerform().equals(GraphicalEdit2.Action.TRANSLATE)) {

                        }

                    } catch (ArrayIndexOutOfBoundsException ex) {
                        ex.printStackTrace();
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                private boolean isRunning() {
                    return running;
                }

                public void setRunning(boolean running) {
                    this.running = running;
                }

                public boolean isPause() {
                    return pause;
                }

                public void setStop(boolean stop) {
                    this.isStopped = true;
                }
            }
        });
    }

    public void drawGraphicsEdit() {

    }

    public void setFF(Main ff) {
        this.main = ff;
        this.getzRunner().setMain(ff);
    }

    public FunctionView getView() {
        return view;
    }

    public void setView(FunctionView view) {
        FunctionView old = this.view;
        this.view = view;

        firePropertyChange("view", old, view);
    }

    public void afterSet() {

    }

    public ZRunnerMain getzRunner() {
        return zRunner;
    }

    public void setzRunner(ZRunnerMain zRunner) {
        ZRunnerMain old = this.zRunner;
        this.zRunner = zRunner;
        getView().addPropertyChangeListener(getzRunner());
        addPropertyChangeListener(getzRunner());
        firePropertyChange("zRunner", old, zRunner);
    }

    @Override
    public void initValues(Representable representable) {
        this.currentRepresentable = representable;
    }

    public MyObservableList<Representable> getTranslate() {
        return translate;
    }

    public void setTranslate(MyObservableList<Representable> translate) {
        this.translate = translate;
    }

    public boolean isGraphicalEditing() {
        return graphicalEditing;
    }

    public void setGraphicalEditing(boolean graphicalEditing) {
        this.graphicalEditing = graphicalEditing;

    }

    public void setRuv(ParametricSurface ps, double u, double v) {
        this.psMode = true;
        this.ps = ps;
        this.u = u;
        this.v = v;
    }
    public Point3D getRuv() {
        return ps.calculerPoint3D(u, v);
    }
}
