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
 * 2014 Manuel Dahmen
 */
package one.empty3.library.core.tribase;


import one.empty3.*;
import one.empty3.library.*;

import java.awt.*;
import java.awt.Point;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/*__
 * @author MANUEL DAHMEN
 *         <p>
 *         dev
 *         <p>
 *         15 oct. 2011
 */
public class TRIObjetGenerateurAbstract extends Representable implements TRIObjetGenerateur {
    // Overrides from TriObjetGenerateur
    /*__
     *
     */


    private static final long serialVersionUID = 1L;
    protected StructureMatrix<Integer> maxX = new StructureMatrix<>(0, Integer.class);
    protected StructureMatrix<Integer> maxY = new StructureMatrix<>(0, Integer.class);
    protected StructureMatrix<Boolean> cx = new StructureMatrix<>(0, Boolean.class);
    protected StructureMatrix<Boolean> cy = new StructureMatrix<>(0, Boolean.class);

    public TRIObjetGenerateurAbstract() {
        maxX.setElem(30);
        maxY.setElem(30);
        cx.setElem(false);
        cy.setElem(false);
    }

    @Override
    public void declareProperties() {
        super.declareProperties();
        /*
        getDeclaredDataStructure().put("maxX/ MaxX cordinates iterations for drawing - disable", maxX);
        getDeclaredDataStructure().put("maxY/ MaxY cordinates iterations for drawing - disable", maxY);
        getDeclaredDataStructure().put("cx/ Circulaire X last iterations for drawing - disable", cx);
        getDeclaredDataStructure().put("cy/ Circulaire X last iterations for drawing - disable", cy);
    */
    }

    @Override
    public int getMaxX() {
        return maxX.data0d;
    }

    @Override
    public void setMaxX(int maxX) {
        this.maxX.setElem(maxX);
    }

    @Override
    public int getMaxY() {
        return maxY.data0d;
    }

    @Override
    public void setMaxY(int maxY) {
        this.maxY.setElem(maxY);
    }

    @Override
    public boolean getCirculaireX() {
        return cx.data0d;
    }

    @Override
    public void setCirculaireX(boolean cx) {
        this.cx.setElem(cx);
    }

    @Override
    public boolean getCirculaireY() {
        return cy.data0d;
    }

    @Override
    public void setCirculaireY(boolean cy) {
        this.cy.setElem(cy);
    }

    @Override
    public Point3D coordPoint3D(int x, int y) {
        return null;
    }

    /*__
     * *
     *
     * @param numX num�ro de valeur de coordArr par rapport � maxX
     * @param numY num�ro de valeur de y par rapport � maxY
     * @param tris TRI[1] = ((coordArr,y),(coordArr+1,y),(coordArr+1,y+1)) TRI[2] =
     *             ((coordArr,y),(coordArr,y+1),(coordArr+1,y+1))
     */
    public void getTris(int numX, int numY, TRI[] tris) {
        int nextX = numX + 1;
        int nextY = numY + 1;
        if ((numX >= maxX.getElem() - 1) && cx.getElem()) {
            nextX = 0;
        }
        if ((numY >= maxY.getElem() - 1) && cy.getElem()) {
            nextY = 0;
        }

        for (int t = 0; t < 2; t++) {
            tris[t] = new TRI();
            if (t == 0) {
                tris[t].setSommet(new Point3D[]{
                        coordPoint3D(numX, numY),
                        coordPoint3D(nextX, numY),
                        coordPoint3D(nextX, nextY)});
            } else {
                tris[t].setSommet(new Point3D[]{
                        coordPoint3D(numX, nextY),
                        coordPoint3D(numX, numY),
                        coordPoint3D(nextX, nextY)});
            }

            tris[t].
                    texture(
                            texture);

            Point3D normale = tris[t].getSommet().getElem(1).moins(
                    tris[t].getSommet().getElem(0).prodVect(
                            (tris[t].getSommet().getElem(2).moins(tris[t].getSommet().getElem(0)))));
            for (int i = 0; i < 3; i++) {
                tris[t].getSommet().getElem(i).setNormale(normale);
            }

        }
    }

    /*__
     * Method for interpolate cordinates
     * and textures.
     *
     * @param tris
     * @param numX
     * @param numY
     * @param ratioX
     * @param ratioY
     * @return
     */
    @Override
    public Point3D getPoint3D(TRI[] tris, int numX, int numY, double ratioX,
                              double ratioY) {
        if (ratioX > ratioY) {
            java.util.List<Point3D> sommet = tris[0].getSommet().getData1d();
            Point3D ret = sommet.get(0).plus(
                    sommet.get(1).moins(sommet.get(0)).mult(ratioX)).plus(
                    sommet.get(2).moins(sommet.get(1)).mult(ratioY));
            if (texture() == null) texture = new TextureCol(new Color(255, 128, 0));
            ret.texture(new TextureCol(texture.getColorAt(
                    (numX + (numX + ratioX) / maxX.data0d), (numY + (numY + ratioY) / maxY.data0d))));

            ret.setNormale((tris[0].getSommet().getElem(1).moins(tris[0].getSommet().getElem(0))).prodVect((tris[0]
                    .getSommet().getElem(2).moins(tris[0].getSommet().getElem(0)))));

            return ret;
        } else {
            List<Point3D> sommet = tris[1].getSommet().getData1d();
            if (texture() == null) texture = new TextureCol(new Color(255, 128, 0));
            Point3D ret = sommet.get(1).plus(
                    sommet.get(0).moins(sommet.get(1)).mult(ratioY)).plus(
                    sommet.get(2).moins(sommet.get(0)).mult(ratioX));
            ret.texture(new TextureCol(texture.getColorAt(
                    (numX + (numX + ratioX) / maxX.data0d), (numY + (numY + ratioY) / maxY.data0d))));

            ret.setNormale((tris[1].getSommet().getElem(1).moins(tris[1].getSommet().getElem(0)).prodVect((tris[1]
                    .getSommet().getElem(2).moins(tris[1].getSommet().getElem(0))))));

            return ret;
        }
    }

    /*__
     * Draws in Image with ZBuffer 2D drawing class
     * <p>
     * Ce serait mieux de calculer les points avec
     * des couleurs.. Bien oui c'est encore TODO
     *
     * @param z
     */
    public void draw(ZBuffer z) {
        Point3D INFINI = new Point3D(0d, 0d, 10000d, new TextureCol(Color.BLUE));
        TRI[] tris = new TRI[2];
        tris[0] = new TRI(INFINI, INFINI, INFINI);
        tris[1] = new TRI(INFINI, INFINI, INFINI);
        int borneX = getMaxX();
        int borneY = getMaxY();
        if (getCirculaireX()) {
            borneX++;
        }
        if (getCirculaireY()) {
            borneY++;
        }
        for (int numX = 0; numX < borneX; numX++) {
            for (int numY = 0; numY < borneY; numY++) {
                try {
                    getTris(numX, numY, tris);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    //Exception may occur here'
                }
                boolean drop = false;
                double incrMax = 1;
                for (int t = 0; t < 2; t++) {
                    for (int c = 0; c < 3; c++) {
                        Point p1 = z.camera().coordonneesPoint2D(tris[t]
                                .getSommet().getElem(c), (ZBufferImpl) z);
                        Point p2 = z.camera().coordonneesPoint2D(tris[t]
                                .getSommet().getElem((c + 1) % 3), (ZBufferImpl) z);
                        if (p1 != null & p2 != null) {
                            double incr = 1.0 / (Math
                                    .abs(p1.getX() - p2.getX()) + Math.abs(p1
                                    .getY() - p2.getY()));
                            if (incr < incrMax) {
                                incrMax = incr;
                            }
                        } else {
                            drop = true;
                        }
                    }

                }
                for (double rx = 0; rx < 1.0; rx += incrMax) {
                    for (double ry = 0; ry < 1.0; ry += incrMax) {
                        z.testDeep(getPoint3D(tris, numX, numY, rx,
                                ry));
                    }
                }
            }

        }
    }

}
