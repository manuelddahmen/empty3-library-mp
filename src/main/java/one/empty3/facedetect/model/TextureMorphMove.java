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

import one.empty3.library.CopyRepresentableError;
import one.empty3.library.ITexture;
import one.empty3.library.MatrixPropertiesObject;
import one.empty3.library.Point3D;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextureMorphMove extends ITexture {
    private static final int WHITE = Color.WHITE.getRGB();
    private final EditPolygonsMappings editPanel;
    public int selectedPointNo = -1;
    protected DistanceAB distanceAB;
    private final int GRAY = Color.GRAY.getRGB();
    private Class<? extends DistanceBezier2> distanceABclass;
    private List<Point3D> polyConvA;
    private List<Point3D> polyConvB;

    @Override
    public MatrixPropertiesObject copy() throws CopyRepresentableError, IllegalAccessException, InstantiationException {
        return null;
    }

    public TextureMorphMove(EditPolygonsMappings editPanel, Class<? extends DistanceAB> distanceABclass) {
        super();
        this.editPanel = editPanel;
        if (distanceABclass != null) {
            setDistanceABclass(distanceABclass);
            if(distanceABclass.equals(DistanceProxLinear43.class) && editPanel.model.imageFileRight!=null)
                editPanel.model.convexHull3 = new ConvexHull(editPanel.model.points3.values().stream().toList(),
                        new Dimension(editPanel.model.imageFileRight.getWidth(), editPanel.model.imageFileRight.getHeight()));
            }
        if(!editPanel.model.pointsInImage.isEmpty())
            editPanel.model.convexHull1 = new ConvexHull(editPanel.model.pointsInImage.values().stream().toList(), new Dimension(editPanel.model.image.getWidth(), editPanel.model.image.getHeight()));
        if(!editPanel.model.pointsInModel.isEmpty())
            editPanel.model.convexHull2 = new ConvexHull(editPanel.model.pointsInModel.values().stream().toList(), new Dimension(editPanel.model.image.getWidth(), editPanel.model.image.getHeight()));

    }


    @Override
    public int getColorAt(double u, double v) {

        if (distanceAB == null)
            return 0;
        if (distanceAB instanceof DistanceIdent) {
            Point3D ident = distanceAB.findAxPointInB(u, v);

            Point3D point3D = new Point3D(ident.getX() * editPanel.model.image.getWidth(), ident.getY() * editPanel.model.image.getHeight(), 0.0);

            int x = (int) (Math.max(0, Math.min(point3D.getX(), (double) editPanel.model.image.getWidth() - 1)));
            int y = (int) (Math.max(0, Math.min((point3D.getY()), (double) editPanel.model.image.getHeight() - 1)));

            /*if (x == 0 || y == 0 || x == editPanel.getWidth() - 1 || y == editPanel.getHeight() - 1) {
                return 0;
            }*/
            return editPanel.model.image.getRGB(x, y);
        }
        if (distanceAB.isInvalidArray()) {
            return 0;
        }

        if (editPanel.model.image != null) {
            int x1 = (int) (u*(editPanel.model.image.getWidth()-1));
            int y1 = (int) (v*(editPanel.model.image.getHeight()-1));
            if (distanceAB.getClass().isAssignableFrom(DistanceBezier3.class))
                ;
            else if ((distanceAB.sAij == null || distanceAB.sBij == null) && !distanceAB.getClass().isAssignableFrom(DistanceBezier3.class)) {
                //Logger.getAnonymousLogger().log(Level.SEVERE, "DistanceAB .sAij or DistanceAB . sBij is null");
                return 0;
            }
            try {
                Point3D axPointInB = distanceAB.findAxPointInB(u, v);
                if (axPointInB != null) {

                    Point3D p = new Point3D(axPointInB.getX() * editPanel.model.image.getWidth(), axPointInB.getY() * editPanel.model.image.getHeight(), 0.0);

                    int xLeft = (int) (Math.max(0, Math.min(p.getX(), (double) editPanel.model.image.getWidth() - 1)));
                    int yLeft = (int) (Math.max(0, Math.min(p.getY(), (double) editPanel.model.image.getHeight() - 1)));

                    boolean markA = false;

                    if(distanceAB instanceof DistanceProxLinear43 dist4 &&dist4.jpgRight != null) {
                        Point3D c = dist4.findAxPointInBa13(u, v);
                        if(c!=null) {
                            c = c.multDot(new Point3D((double) dist4.jpgRight.getWidth(), (double) dist4.jpgRight.getHeight(), 0.0));
                            int x3 = (int) (Math.max(0, Math.min(c.getX(), (double) editPanel.model.imageFileRight.getWidth() - 1)));
                            int y3 = (int) (Math.max(0, Math.min(c.getY(), (double) editPanel.model.imageFileRight.getHeight() - 1)));
                            //if(dist4.checkedListC[x3][y3]) {
                            if(/*editPanel.convexHull3!=null &&editPanel.convexHull3.testIfIn(x3, y3)*/
                            /*&&*/editPanel.model.convexHull1!=null &&editPanel.model.convexHull1.testIfIn(xLeft, yLeft)
                            /*&&editPanel.convexHull2!=null &&editPanel.convexHull2.testIfIn(x1, y1)*/) {
                                markA = true;
                                return dist4.jpgRight.getRGB(x3, y3);
                            }
                        }
                    } else if(!(distanceAB instanceof DistanceProxLinear43)) {
                        return editPanel.model.image.getRGB(xLeft, yLeft);
                    }
                }
                return editPanel.model.image.getRGB(x1, y1);

            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        return one.empty3.libs.Color.YELLOW.getRGB();
    }


    public void setDistanceABclass(Class<? extends DistanceAB> distanceMap) {
        Dimension bDimReal;


            bDimReal = new Dimension(editPanel.model.image.getWidth(), editPanel.model.image.getHeight());
        Dimension cDimReal = null;
        if(editPanel.model.imageFileRight!=null) {
            cDimReal = new Dimension(editPanel.model.imageFileRight.getWidth(), editPanel.model.imageFileRight.getHeight());
        }
        List<Point3D> lA = new ArrayList<>();
        List<Point3D> lB = new ArrayList<>();
        List<Point3D> lC = new ArrayList<>();
        /**
         * Double A, B avec ai correspond à bi ( en se servant des HashMap)
         * **/
        synchronized (editPanel.model.pointsInImage) {
            editPanel.model.pointsInImage.forEach(new BiConsumer<String, Point3D>() {
                @Override
                public void accept(String s, Point3D point3D) {
                    editPanel.model.pointsInModel.forEach(new BiConsumer<String, Point3D>() {
                        @Override
                        public void accept(String sB, Point3D point3D) {
                            if (s.equals(sB)) {
                                lA.add(editPanel.model.pointsInImage.get(s));
                                lB.add(editPanel.model.pointsInModel.get(s));
                                if(editPanel.model.points3.get(s)!=null)
                                    lC.add(editPanel.model.points3.get(s));
                            }
                        }
                    });
                }
            });
        }
        if (editPanel.model.image != null && editPanel.model != null) {
            long timeStarted = System.nanoTime();
            try {
                if (distanceMap.isAssignableFrom(DistanceProxLinear1.class)) {
                    distanceAB = new DistanceProxLinear1(lA, lB, new Dimension(editPanel.model.image.getWidth(), editPanel.model.image.getHeight()),
                            bDimReal, editPanel.model.opt1, editPanel.model.optimizeGrid);
                } else if (distanceMap.isAssignableFrom(DistanceProxLinear2.class)) {
                    distanceAB = new DistanceProxLinear2(lA, lB, new Dimension(editPanel.model.image.getWidth(), editPanel.model.image.getHeight()),
                            bDimReal, editPanel.model.opt1, editPanel.model.optimizeGrid);
                } else if (distanceMap.isAssignableFrom(DistanceProxLinear3.class)) {
                    distanceAB = new DistanceProxLinear3(lA, lB, new Dimension(editPanel.model.image.getWidth(), editPanel.model.image.getHeight()),
                            bDimReal, editPanel.model.opt1, editPanel.model.optimizeGrid);
                } else if (distanceMap.isAssignableFrom(DistanceProxLinear4.class)) {
                    distanceAB = new DistanceProxLinear4(lA, lB, new Dimension(editPanel.model.image.getWidth(), editPanel.model.image.getHeight()),
                            bDimReal, editPanel.model.opt1, editPanel.model.optimizeGrid);
                    if(editPanel.model.imageFileRight!=null)
                        distanceAB.jpgRight = editPanel.model.imageFileRight;
                } else if (distanceMap.isAssignableFrom(DistanceProxLinear42.class)) {
                    distanceAB = new DistanceProxLinear42(lA, lB, new Dimension(editPanel.model.image.getWidth(), editPanel.model.image.getHeight()),
                            bDimReal, editPanel.model.opt1, editPanel.model.optimizeGrid);
                    if(editPanel.model.imageFileRight!=null)
                        distanceAB.jpgRight = editPanel.model.imageFileRight;
                } else if (distanceMap.isAssignableFrom(DistanceProxLinear43.class)) {
                    distanceAB = new DistanceProxLinear43(lA, lB, lC, new Dimension(editPanel.model.image.getWidth(), editPanel.model.image.getHeight()),
                            bDimReal, cDimReal, editPanel.model.opt1, editPanel.model.optimizeGrid);
                    ((DistanceProxLinear43) distanceAB).setJpgRight(editPanel.model.imageFileRight);
                    ((DistanceProxLinear43) distanceAB).setComputeMaxTime(editPanel.getComputeTimeMax());
                    editPanel.model.convexHull3 = new ConvexHull(lC, new Dimension(editPanel.model.imageFileRight.getWidth(), editPanel.model.imageFileRight.getHeight()));
                    if(editPanel.model.imageFileRight!=null)
                        distanceAB.jpgRight = editPanel.model.imageFileRight;
                } else if (distanceMap.isAssignableFrom(DistanceBezier3.class)) {
                    distanceAB = new DistanceBezier3(lA, lB, new Dimension(editPanel.model.image.getWidth(), editPanel.model.image.getHeight()),
                            bDimReal, editPanel.model.opt1, editPanel.model.optimizeGrid);
                } else if (distanceMap.isAssignableFrom(DistanceIdent.class)) {
                    distanceAB = new DistanceIdent();
                } else {
                    distanceAB = new DistanceIdent();

                }

                editPanel.model.hasChangedAorB = true;

                if (distanceMap != null) {
                    this.distanceABclass = (Class<? extends DistanceBezier2>) distanceMap;
                    editPanel.model.iTextureMorphMove = this;
                    editPanel.model.iTextureMorphMove.distanceAB = distanceAB;
                    editPanel.model.hasChangedAorB = true;
                    editPanel.model.distanceABClass = distanceABclass;
                } else {
                    throw new NullPointerException("distanceMap is null in TextureMorphMove");
                }
            } catch (RuntimeException ex) {
                editPanel.model.hasChangedAorB = true;
                ex.printStackTrace();
            }
            long nanoElapsed = System.nanoTime() - timeStarted;
            Logger.getAnonymousLogger().log(Level.INFO, "Temps écoulé à produire l'object DistanceAB (" + distanceMap.getCanonicalName() +
                    ") à : " + 10E-9 * nanoElapsed);
        }
    }


}