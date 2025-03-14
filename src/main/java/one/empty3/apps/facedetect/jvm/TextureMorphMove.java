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

import java.awt.*;

import one.empty3.apps.testobject.Resolution;
import one.empty3.library.*;

import java.awt.geom.Dimension2D;
import java.util.*;
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
            if (distanceABclass.equals(DistanceProxLinear43.class) && editPanel.imageFileRight != null)
                editPanel.convexHull3 = new ConvexHull(editPanel.points3.values().stream().toList(),
                        new Dimension(editPanel.imageFileRight.getWidth(), editPanel.imageFileRight.getHeight()));
            if (distanceABclass.equals(DistanceProxLinear44.class) && editPanel.imageFileRight != null)
                editPanel.convexHull3 = new ConvexHull(editPanel.points3.values().stream().toList(),
                        new Dimension(editPanel.imageFileRight.getWidth(), editPanel.imageFileRight.getHeight()));
        }
        if (!editPanel.pointsInImage.isEmpty())
            editPanel.convexHull1 = new ConvexHull(editPanel.pointsInImage.values().stream().toList(), new Dimension(editPanel.image.getWidth(), editPanel.image.getHeight()));
        if (!editPanel.pointsInModel.isEmpty())
            editPanel.convexHull2 = new ConvexHull(editPanel.pointsInModel.values().stream().toList(), new Dimension((int) editPanel.dimPictureBox.getWidth(), (int) editPanel.dimPictureBox.getHeight()));
        if(editPanel.hdTextures) {
            Dimension dim1 = new Dimension(Resolution.HD1080RESOLUTION.x(), Resolution.HD1080RESOLUTION.y());
            if (!editPanel.pointsInModel.isEmpty())
                editPanel.convexHull2 = new ConvexHull(editPanel.pointsInModel.values().stream().toList(), dim1);

        }

    }


    @Override
    public int getColorAt(double u, double v) {

        if (distanceAB == null)
            return 0;
        if (distanceAB instanceof DistanceIdent) {
            Point3D ident = distanceAB.findAxPointInB(u, v);

            Point3D point3D = new Point3D(ident.getX() * editPanel.image.getWidth(), ident.getY() * editPanel.image.getHeight(), 0.0);

            int x = (int) (Math.max(0, Math.min(point3D.getX(), (double) editPanel.image.getWidth() - 1)));
            int y = (int) (Math.max(0, Math.min((point3D.getY()), (double) editPanel.image.getHeight() - 1)));

            /*if (x == 0 || y == 0 || x == editPanel.getWidth() - 1 || y == editPanel.getHeight() - 1) {
                return 0;
            }*/
            return editPanel.image.getRGB(x, y);
        }
        if (distanceAB.isInvalidArray()) {
            return 0;
        }

        if (editPanel.image != null) {
            // Inside TextureMorphMove class (assuming you have a list of Point3D for your polygon)


            int x1 = (int) (u * (editPanel.image.getWidth() - 1));
            int y1 = (int) (v * (editPanel.image.getHeight() - 1));
            if (distanceAB.getClass().isAssignableFrom(DistanceBezier3.class))
                ;
            else if ((distanceAB.sAij == null || distanceAB.sBij == null) && !distanceAB.getClass().isAssignableFrom(DistanceBezier3.class)) {
                //Logger.getAnonymousLogger().log(Level.SEVERE, "DistanceAB .sAij or DistanceAB . sBij is null");
                return 0;
            }
            try {
                Point3D axPointInB = distanceAB.findAxPointInB(u, v);
                if (axPointInB != null) {
                    Point3D p = new Point3D(axPointInB.getX() * editPanel.image.getWidth(), axPointInB.getY() * editPanel.image.getHeight(), 0.0);

                    double percentB = distanceToConvexHull(distanceAB.bDimReal, editPanel.convexHull1, p) + 10;

                    percentB = percentB / 20;
                    if (percentB < 0) {
                        percentB = 0;
                    }
                    if (percentB > 1)
                        percentB = 1.0;

                    //percentB = 0.5;


                    int xLeft = (int) (Math.max(0, Math.min(p.getX(), (double) editPanel.image.getWidth() - 1)));
                    int yLeft = (int) (Math.max(0, Math.min(p.getY(), (double) editPanel.image.getHeight() - 1)));

                    boolean markA = false;

                    if (distanceAB instanceof DistanceProxLinear43 dist4 && dist4.jpgRight != null) {
                        Point3D c = dist4.findAxPointInBa13(u, v);
                        if (c != null) {
                            c = c.multDot(new Point3D((double) dist4.jpgRight.getWidth(), (double) dist4.jpgRight.getHeight(), 0.0));
                            int x3 = (int) (Math.max(0, Math.min(c.getX(), (double) editPanel.imageFileRight.getWidth() - 1)));
                            int y3 = (int) (Math.max(0, Math.min(c.getY(), (double) editPanel.imageFileRight.getHeight() - 1)));
                            //if(dist4.checkedListC[x3][y3]) {
                            if (/*editPanel.convexHull3!=null &&editPanel.convexHull3.testIfIn(x3, y3)*/
                                /*&&*/editPanel.convexHull1 != null && editPanel.convexHull1.testIfIn(xLeft, yLeft)
                                /*&&editPanel.convexHull2!=null &&editPanel.convexHull2.testIfIn(x1, y1)*/) {
                                markA = true;
                                return dist4.jpgRight.getRGB(x3, y3);
                            }
                        }
                    } else if (distanceAB instanceof DistanceProxLinear44 dist4 && dist4.jpgRight != null) {
                        Point3D c = dist4.findAxPointInBa13(u, v);
                        if (c != null) {
                            c = c.multDot(new Point3D((double) dist4.jpgRight.getWidth(), (double) dist4.jpgRight.getHeight(), 0.0));
                            int x3 = (int) (Math.max(0, Math.min(c.getX(), (double) editPanel.imageFileRight.getWidth() - 1)));
                            int y3 = (int) (Math.max(0, Math.min(c.getY(), (double) editPanel.imageFileRight.getHeight() - 1)));
                            //if(dist4.checkedListC[x3][y3]) {
                            if (/*editPanel.convexHull3!=null &&editPanel.convexHull3.testIfIn(x3, y3)*/
                                /*&&*/editPanel.convexHull1 != null && editPanel.convexHull1.testIfIn(xLeft, yLeft)
                                /*&&editPanel.convexHull2!=null &&editPanel.convexHull2.testIfIn(x1, y1)*/) {
                                markA = true;
                                double[] color = new double[3];
                                double[] rgb3 = Lumiere.getDoubles(dist4.jpgRight.getRGB(x3, y3));
                                double[] rgb1 = Lumiere.getDoubles(editPanel.image.getRGB(xLeft, yLeft));
                                for (int k = 0; k < 3; k++) {
                                    color[k] = rgb1[k] + (rgb3[k] - rgb1[k]) * percentB;
                                }

                                //return one.empty3.libs.Color.newCol((float) color[0], (float) color[1], (float) color[2]).getRGB();
                                return new Color((float) color[0], (float) color[1], (float) color[2]).getRGB();
                            }
                        }
                    } else if (!(distanceAB instanceof DistanceProxLinear44)) {
                        return editPanel.image.getRGB(xLeft, yLeft);
                    }
                }
                return editPanel.image.getRGB(x1, y1);

            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }

        return one.empty3.libs.Color.YELLOW.getRGB();
    }

    private double distanceToConvexHull(Dimension2D bDimReal, ConvexHull convexHull2, Point3D axPointInB) {
        List<Point3D> p = new ArrayList<>();
/*
        for (int i = 0; i < convexHull2.p.size(); i++) {
            p.add(convexHull2.p.get(i).multDot(new Point3D(bDimReal.getHeight(),bDimReal.getHeight()));
        }
*/
        double d = PolygonDistance.distanceToPolygon(axPointInB, convexHull2.p);
        return (d * (convexHull2.testIfIn((int) axPointInB.getX(), (int) axPointInB.getY()) ? 1 : -1) + 1) / 2.0;
    }


    public void setDistanceABclass(Class<? extends DistanceAB> distanceMap) {
        Dimension bDimReal;


        if (editPanel.hdTextures) {
            bDimReal = new Dimension(Resolution.HD1080RESOLUTION.x(), Resolution.HD1080RESOLUTION.y());
        } else {
            bDimReal = new Dimension((int) editPanel.dimPictureBox.getWidth(), (int) editPanel.dimPictureBox.getHeight());
        }
        Dimension cDimReal = null;
        if (editPanel.imageFileRight != null) {
            cDimReal = new Dimension(editPanel.imageFileRight.getWidth(), editPanel.imageFileRight.getHeight());
        }
        List<Point3D> lA = new ArrayList<>();
        List<Point3D> lB = new ArrayList<>();
        List<Point3D> lC = new ArrayList<>();
        /**
         * Double A, B avec ai correspond à bi ( en se servant des HashMap)
         * **/
        synchronized (editPanel.pointsInImage) {
            editPanel.pointsInImage.forEach(new BiConsumer<String, Point3D>() {
                @Override
                public void accept(String s, Point3D point3D) {
                    editPanel.pointsInModel.forEach(new BiConsumer<String, Point3D>() {
                        @Override
                        public void accept(String sB, Point3D point3D) {
                            if (s.equals(sB)) {
                                lA.add(editPanel.pointsInImage.get(s));
                                lB.add(editPanel.pointsInModel.get(s));
                                if (editPanel.points3.get(s) != null)
                                    lC.add(editPanel.points3.get(s));
                            }
                        }
                    });
                }
            });
        }
        if (editPanel.image != null && editPanel.model != null) {
            long timeStarted = System.nanoTime();
            try {
                if (distanceMap.isAssignableFrom(DistanceProxLinear1.class)) {
                    distanceAB = new DistanceProxLinear1(lA, lB, new Dimension(editPanel.image.getWidth(), editPanel.image.getHeight()),
                            bDimReal, editPanel.opt1, editPanel.optimizeGrid);
                } else if (distanceMap.isAssignableFrom(DistanceProxLinear2.class)) {
                    distanceAB = new DistanceProxLinear2(lA, lB, new Dimension(editPanel.image.getWidth(), editPanel.image.getHeight()),
                            bDimReal, editPanel.opt1, editPanel.optimizeGrid);
                } else if (distanceMap.isAssignableFrom(DistanceProxLinear3.class)) {
                    distanceAB = new DistanceProxLinear3(lA, lB, new Dimension(editPanel.image.getWidth(), editPanel.image.getHeight()),
                            bDimReal, editPanel.opt1, editPanel.optimizeGrid);
                } else if (distanceMap.isAssignableFrom(DistanceProxLinear4.class)) {
                    distanceAB = new DistanceProxLinear4(lA, lB, new Dimension(editPanel.image.getWidth(), editPanel.image.getHeight()),
                            bDimReal, editPanel.opt1, editPanel.optimizeGrid);
                    if (editPanel.imageFileRight != null)
                        distanceAB.jpgRight = editPanel.imageFileRight;
                } else if (distanceMap.isAssignableFrom(DistanceProxLinear42.class)) {
                    distanceAB = new DistanceProxLinear42(lA, lB, new Dimension(editPanel.image.getWidth(), editPanel.image.getHeight()),
                            bDimReal, editPanel.opt1, editPanel.optimizeGrid);
                    if (editPanel.imageFileRight != null)
                        distanceAB.jpgRight = editPanel.imageFileRight;
                } else if (distanceMap.isAssignableFrom(DistanceProxLinear43.class)) {
                    distanceAB = new DistanceProxLinear43(lA, lB, lC, new Dimension(editPanel.image.getWidth(), editPanel.image.getHeight()),
                            bDimReal, cDimReal, editPanel.opt1, editPanel.optimizeGrid);
                    ((DistanceProxLinear43) distanceAB).setJpgRight(editPanel.imageFileRight);
                    ((DistanceProxLinear43) distanceAB).setComputeMaxTime(editPanel.getComputeTimeMax());
                    editPanel.convexHull3 = new ConvexHull(lC, new Dimension(editPanel.imageFileRight.getWidth(), editPanel.imageFileRight.getHeight()));
                    if (editPanel.imageFileRight != null)
                        distanceAB.jpgRight = editPanel.imageFileRight;
                } else if (distanceMap.isAssignableFrom(DistanceProxLinear44.class)) {
                    distanceAB = new DistanceProxLinear44(lA, lB, lC, new Dimension(editPanel.image.getWidth(), editPanel.image.getHeight()),
                            bDimReal, cDimReal, editPanel.opt1, editPanel.optimizeGrid);
                    distanceAB.setJpgRight(editPanel.imageFileRight);
                    ((DistanceProxLinear44) distanceAB).setComputeMaxTime(editPanel.getComputeTimeMax());
                    editPanel.convexHull3 = new ConvexHull(lC, new Dimension(editPanel.imageFileRight.getWidth(), editPanel.imageFileRight.getHeight()));
                    if (editPanel.imageFileRight != null)
                        distanceAB.jpgRight = editPanel.imageFileRight;
                } else if (distanceMap.isAssignableFrom(DistanceBezier3.class)) {
                    distanceAB = new DistanceBezier3(lA, lB, new Dimension(editPanel.image.getWidth(), editPanel.image.getHeight()),
                            bDimReal, editPanel.opt1, editPanel.optimizeGrid);
                } else if (distanceMap.isAssignableFrom(DistanceIdent.class)) {
                    distanceAB = new DistanceIdent();
                } else {
                    distanceAB = new DistanceIdent();

                }

                editPanel.hasChangedAorB = true;

                if (distanceMap != null) {
                    this.distanceABclass = (Class<? extends DistanceBezier2>) distanceMap;
                    editPanel.iTextureMorphMove = this;
                    editPanel.iTextureMorphMove.distanceAB = distanceAB;
                    editPanel.hasChangedAorB = true;
                    editPanel.distanceABClass = distanceABclass;
                } else {
                    throw new NullPointerException("distanceMap is null in TextureMorphMove");
                }
            } catch (RuntimeException ex) {
                editPanel.hasChangedAorB = true;
                ex.printStackTrace();
            }
            long nanoElapsed = System.nanoTime() - timeStarted;
            Logger.getAnonymousLogger().log(Level.INFO, "Temps écoulé à produire l'object DistanceAB (" + distanceMap.getCanonicalName() +
                    ") à : " + 10E-9 * nanoElapsed);
        }
    }


}