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

package one.empty3.feature20220726.selection;

import one.empty3.feature20220726.PixM;
import one.empty3.io.ProcessFile;
import one.empty3.library.ColorTexture;
import one.empty3.library.ITexture;
import one.empty3.library.Lumiere;
import one.empty3.library.Point3D;

import javaAnd.awt.image.imageio.ImageIO;
import javaAnd.awt.*;

import one.empty3.libs.Image;
import one.empty3.libs.Image;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class PasteBlank extends ProcessFile {

    /*
     *
     * @param img Image sur laquelle dessiner
     * @param col Couleur ou texture de dessin
     */
    public PixM pasteList(PixM img, ITexture col) {

        final PixM res = new PixM(img.getColumns(), img.getLines());
        for (int i = 0; i < img.getColumns() * img.getLines(); i++) {

            int ix = (i % img.getColumns());
            int iy = (i / img.getColumns());
            Point3D pi = new Point3D(1.0 * ix, 1.0 * iy, 0.0);


            int x = ix;
            int y = iy;

            int rgb = col.getColorAt(
                    pi.getX() / img.getColumns(),
                    pi.getY() / img.getLines());
            rgb = Color.BLACK.getRGB();
            double[] rgbD = lookForColor(img, x, y, Lumiere.getDoubles(rgb));
            for (int comp = 0; comp < 3; comp++) {
                img.setCompNo(comp);
                res.setCompNo(comp);
                if (rgbD != null)
                    res.set(x, y, rgbD[comp]);
                else
                    res.set(x, y, img.get(x, y));
            }

        }
        return res;
    }

    private Point3D spirale(int x, int y, double r, double a) {
        return new Point3D(x + Math.cos(a) * r, y + Math.sin(a) * r, 0.0);
    }

    public boolean checkPointColorEquals(PixM img, int x, int y, int x1, int y1) {
        return img.getP(x, y).moins(img.getP(x1, y1)).norme() < 0.3;
    }

    private double[] lookForColor(PixM img, int x, int y, double[] search) {
        Point3D searchP = new Point3D(search);
        double[] colorProxy = new double[]{0., 0., 0.};
        Point3D c0 = img.getP(x, y);
        int countValues = 0;
        double[] values = new double[3];
        if (Arrays.equals(img.getValues(x, y), search)) {
            double radius = 1;
            double iA = 0;
            while (countValues < 4 && radius < 20) {
                //Parcourir autour de (x,y) dans un rayon radius
                // j: rayon du cercle courant dans la spirale de longueur L
                // i: angle du cercle courant dans "
                Point3D p = spirale(x, y, radius, iA);//Emplacement

                Point3D c1 = img.getP((int) (double) (p.get(0)), (int) (double) (p.get(1)));//Couleur


                if (searchP.moins(c1).norme() >= 0.3) {
                    for (int c = 0; c < colorProxy.length; c++) {
                        colorProxy[c] += c1.get(c);
                    }
                    countValues++;
                }

                iA += 1.0 / (2 * Math.PI * radius);
                if (iA >= 2 * Math.PI * radius) {
                    radius++;
                    iA = 0;
                }
            }
        }
        if (countValues > 0) {
            for (int i = 0; i < 3; i++) {
                colorProxy[i] /= countValues;
            }
            return colorProxy;

        }
        return null;
    }

    @Override
    public boolean process(File in, File out) {
        if (!in.getAbsolutePath().endsWith("jpg"))
            return false;
        Image read = new one.empty3.libs.Image(new one.empty3.libs.Image(in));
        PixM pixM = PixM.getPixM(read, maxRes);
        PixM pixM1 = pasteList(pixM, new ColorTexture(Color.BLACK));
        new one.empty3.libs.Image(1,1,1.saveToFile(pixM1.normalize(0, 1).getImage(), "jpg", out);
        return true;
    }

}
