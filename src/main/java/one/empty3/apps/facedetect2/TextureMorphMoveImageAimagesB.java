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

package one.empty3.apps.facedetect2;

import javaAnd.awt.Color;
import one.empty3.apps.facedetect.DistanceAB;
import one.empty3.apps.facedetect.DistanceProxLinear1;
import one.empty3.apps.facedetect.DistanceProxLinear2;
import one.empty3.library.*;

import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class TextureMorphMoveImageAimagesB extends ITexture {
    private static final int WHITE = Color.WHITE.getRGB();
    private final List<Point3D> aSet;
    private final List<Point3D> bSet;
    public int selectedPointNo = -1;
    public HashMap<String, Point3D> pointsInA = new HashMap<>();
    public HashMap<String, Point3D> pointsInB = new HashMap<>();
    protected one.empty3.apps.facedetect.DistanceAB distanceAB;
    private int GRAY = Color.GRAY.getRGB();
    private Class<? extends DistanceAB> distanceABclass;
    private BufferedImage imageA;
    private BufferedImage imageB;
    private ITexture textAlt;

    @Override
    public MatrixPropertiesObject copy() throws CopyRepresentableError, IllegalAccessException, InstantiationException {
        return null;
    }

    public TextureMorphMoveImageAimagesB(BufferedImage imageA, BufferedImage imageB,
                                         Class<? extends one.empty3.apps.facedetect.DistanceAB> distanceAB, Resolution dimensionB,
                                         List<Point3D> pointsInImageA, List<Point3D> pointsInImageB) {
        this.imageA = imageA;
        this.imageB = imageB;
        this.aSet = pointsInImageA;
        this.bSet = pointsInImageB;
        if (distanceAB != null)
            setDistanceABclass(distanceAB);
        textAlt = new ImageTexture(new ECBufferedImage(imageB));
    }

    @Override
    public int getColorAt(double u, double v) {
        if (distanceAB != null && !distanceAB.isInvalidArray() && imageA != null
                && imageB != null) {
            try {
                Point3D axPointInB = distanceAB.findAxPointInB(u, v);
                if (axPointInB != null) {

                    Point3D point3D = new Point3D(axPointInB.getX() * imageA.getWidth(), axPointInB.getY() * imageA.getHeight(), 0.0);
                    int rgb = imageA.getRGB((int) (Math.max(0, Math.min(point3D.getX(), (double) imageA.getWidth() - 1)))
                            , (int) (Math.max(0, Math.min((point3D.getY()), (double) imageA.getHeight() - 1))));
                    return rgb;
                }
            } catch (RuntimeException e) {
                //System.out.print("null/");
            }
        }
        return textAlt.getColorAt(u, v);
    }

    public void setDistanceABclass(Class<? extends one.empty3.apps.facedetect.DistanceAB> newDistanceAB) {
        Thread thread = new Thread(() -> {
            boolean isNotOk = true;
            while (isNotOk) {
                try {
                    if (newDistanceAB.isAssignableFrom(DistanceProxLinear2.class)) {
                        distanceAB = new DistanceProxLinear2(aSet, bSet
                                , new Dimension(imageA.getWidth(), imageA.getHeight()),
                                new Dimension(/*imageB.getWidth(),
                                        imageB.getHeight()*/), false, false);
                        isNotOk = false;
                    } else if (newDistanceAB.isAssignableFrom(DistanceProxLinear1.class)) {
                        distanceAB = new DistanceProxLinear1(pointsInA.values().stream().toList(),
                                pointsInA.values().stream().toList(), new Dimension(imageA.getWidth(), imageA.getHeight()),
                                new Dimension(imageB.getWidth(),
                                        imageB.getHeight()), false, false);
                        isNotOk = false;
                    } else if (newDistanceAB.isAssignableFrom(one.empty3.apps.facedetect.DistanceBezier2.class)) {
                        distanceAB = new one.empty3.apps.facedetect.DistanceBezier3(pointsInA.values().stream().toList(),
                                pointsInA.values().stream().toList(), new Dimension(imageA.getWidth(), imageA.getHeight()),
                                new Dimension(imageB.getWidth(),
                                        imageB.getHeight()), false, false);
                        isNotOk = false;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    this.distanceABclass = newDistanceAB;
                } catch (RuntimeException ex) {
                    ex.printStackTrace();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
        thread.start();
    }
}