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

package one.empty3.apps.facedetect;

import java.awt.Color;

import one.empty3.feature.ConvHull;
import one.empty3.library.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextureMorphMove extends ITexture {
    private static final int WHITE = Color.WHITE.getRGB();
    private EditPolygonsMappings editPanel;
    public int selectedPointNo = -1;
    protected DistanceAB distanceAB;
    private int GRAY = Color.GRAY.getRGB();
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
        setDistanceABclass(distanceABclass);
        setConvHullAB();
    }

    private TextureMorphMove() {
    }

    @Override
    public int getColorAt(double u, double v) {

        if (distanceAB == null)
            return 0;
        if (distanceAB instanceof DistanceIdent dei) {
            Point3D narcissism = distanceAB.findAxPointInB(u, v);

            Point3D point3D = new Point3D(narcissism.getX() * editPanel.image.getWidth(), narcissism.getY() * editPanel.image.getHeight(), 0.0);

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
        setConvHullAB();

        if (editPanel.image != null) {
            if (distanceAB.getClass().isAssignableFrom(DistanceBezier3.class))
                ;
            else if ((distanceAB.sAij == null || distanceAB.sBij == null) && !distanceAB.getClass().isAssignableFrom(DistanceBezier3.class)) {
                //Logger.getAnonymousLogger().log(Level.SEVERE, "DistanceAB .sAij or DistanceAB . sBij is null");
                return 0;
            }
            try {
                Point3D axPointInB = distanceAB.findAxPointInB(u, v);
                if (axPointInB != null) {

                    Point3D point3D = new Point3D(axPointInB.getX() * editPanel.image.getWidth(), axPointInB.getY() * editPanel.image.getHeight(), 0.0);

                    int x = (int) (Math.max(0, Math.min(point3D.getX(), (double) editPanel.image.getWidth() - 1)));
                    int y = (int) (Math.max(0, Math.min((point3D.getY()), (double) editPanel.image.getHeight() - 1)));


                    //if (polyConvB != null && !polyConvB.isEmpty() && polyConvA != null && !polyConvA.isEmpty()) {
                    /*if (!ConvHull.convexHullTestPointIsInside(polyConvB, new Point3D((double) x, (double) y, 0.0))) {
                        int rgb = editPanel.image.getRGB(x, y);
                        return rgb;
                    } else if (ConvHull.convexHullTestPointIsInside(polyConvB, new Point3D((double) x, (double) y, 0.0))) {
                        */
                    int rgb = editPanel.image.getRGB(x, y);
                    return rgb;
                    //}
                    //} else {
                    //    if (ConvHull.convexHullTestPointIsInside(polyConvB, new Point3D((double) x, (double) y, 0.0))) {
                    //        int rgb = Color.ORANGE.getRGB();
                    //        return rgb;
                    //    }
                    //}
                } else {
                    return Color.RED.getRGB();

                }
            } catch (RuntimeException e) {
                e.printStackTrace();
            }/*
            int x = (int) (Math.max(0, Math.min(u * ((double) editPanel.image.getWidth() - 1), (double) editPanel.image.getWidth() - 1)));
            int y = (int) (Math.max(0, Math.min(v * ((double) editPanel.image.getHeight() - 1), (double) editPanel.image.getHeight() - 1)));
            return Color.GREEN.getRGB();*/
        }
        int x = (int) (Math.max(0, Math.min(u * ((double) editPanel.image.getWidth() - 1), (double) editPanel.image.getWidth() - 1)));
        int y = (int) (Math.max(0, Math.min(v * ((double) editPanel.image.getHeight() - 1), (double) editPanel.image.getHeight() - 1)));
        //int rgb = editPanel.image.getRGB(x, y);
        int rgb = Color.YELLOW.getRGB();
        return rgb;

    }

    //public void setEditOPanel(EditPolygonsMappings editPolygonsMappings) {
    //    this.editPanel = editPolygonsMappings;
    //}

    public void setDistanceABclass(Class<? extends DistanceAB> distanceMap) {
        Dimension bDimReal;


        if (editPanel.hdTextures) {
            bDimReal = new Dimension(Resolution.HD1080RESOLUTION.x(), Resolution.HD1080RESOLUTION.y());
        } else {
            bDimReal = new Dimension(editPanel.panelModelView.getWidth(), editPanel.panelModelView.getHeight());
        }

        List<Point3D> lA = new ArrayList<>();
        List<Point3D> lB = new ArrayList<>();
        /**
         * Double A, B avec ai correspond à bi ( en se servant des HashMap)
         * **/
        editPanel.pointsInImage.forEach(new BiConsumer<String, Point3D>() {
            @Override
            public void accept(String s, Point3D point3D) {
                String sA = s;
                editPanel.pointsInModel.forEach(new BiConsumer<String, Point3D>() {
                    @Override
                    public void accept(String s, Point3D point3D) {
                        String sB = s;
                        if (sA.equals(sB)) {
                            lA.add(editPanel.pointsInImage.get(sA));
                            lB.add(editPanel.pointsInModel.get(sB));
                        }
                    }
                });
            }
        });

        long timeStarted = System.nanoTime();
        try {
            if (distanceMap.isAssignableFrom(DistanceProxLinear1.class)) {
                distanceAB = new DistanceProxLinear1(lA, lB, new Dimension(editPanel.panelPicture.getWidth(), editPanel.panelPicture.getHeight()),
                        bDimReal, editPanel.opt1, editPanel.optimizeGrid);
            } else if (distanceMap.isAssignableFrom(DistanceProxLinear2.class)) {
                distanceAB = new DistanceProxLinear2(lA, lB, new Dimension(editPanel.panelPicture.getWidth(), editPanel.panelPicture.getHeight()),
                        bDimReal, editPanel.opt1, editPanel.optimizeGrid);
            } else if (distanceMap.isAssignableFrom(DistanceProxLinear3.class)) {
                distanceAB = new DistanceProxLinear3(lA, lB, new Dimension(editPanel.panelPicture.getWidth(), editPanel.panelPicture.getHeight()),
                        bDimReal, editPanel.opt1, editPanel.optimizeGrid);
            } else if (distanceMap.isAssignableFrom(DistanceProxLinear4.class)) {
                distanceAB = new DistanceProxLinear4(lA, lB, new Dimension(editPanel.panelPicture.getWidth(), editPanel.panelPicture.getHeight()),
                        bDimReal, editPanel.opt1, editPanel.optimizeGrid);
            } else if (distanceMap.isAssignableFrom(DistanceBezier3.class)) {
                distanceAB = new DistanceBezier3(lA, lB, new Dimension(editPanel.panelPicture.getWidth(), editPanel.panelPicture.getHeight()),
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

    public void setConvHullAB() {

        Map<String, Point3D> hashMapA = editPanel.pointsInImage;
        Map<String, Point3D> hashMapB = editPanel.pointsInModel;
        ArrayList<Point3D> aConv = new ArrayList<Point3D>();
        int i = 0;
        for (Map.Entry<String, Point3D> stringPoint3DEntry : hashMapA.entrySet()) {
            aConv.add(stringPoint3DEntry.getValue());
            i++;
        }
        i = 0;
        List<Point3D> bConv = new ArrayList<>();
        for (Map.Entry<String, Point3D> stringPoint3DEntry : hashMapA.entrySet()) {
            bConv.add(stringPoint3DEntry.getValue());
            i++;
        }
        if (aConv.size() >= 3 && bConv.size() >= 3) {
            List<Point3D> convexHullA = ConvHull.convexHull(aConv, aConv.size());
            List<Point3D> convexHullB = ConvHull.convexHull(bConv, bConv.size());
            if (convexHullA != null && convexHullB != null) {
                editPanel.iTextureMorphMove.setConvHullA(convexHullA);
                editPanel.iTextureMorphMove.setConvHullB(convexHullB);
                distanceAB.setInvalidArray(false);
                return;
            }
        }
        distanceAB.setInvalidArray(true);
    }

    public void setConvHullA(@NotNull List<Point3D> polyConvA) {
        this.polyConvA = polyConvA;
    }

    public void setConvHullB(@NotNull List<Point3D> polyConvB) {
        this.polyConvB = polyConvB;
    }
}