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

package one.empty3.feature;


import java.io.File;

import one.empty3.io.ObjectWithProperties;
import one.empty3.library.Point3D;
import one.empty3.io.ProcessFile;


import javax.imageio.ImageIO;

import one.empty3.feature.*;

import java.util.logging.*;

public class ProxyValue extends ProcessFile {
    private double min = 0.3;
    private int p;
    public ProxyValue() {
        getProperties().addProperty("min", ObjectWithProperties.ClassTypes.AtomicDouble,
                min);
        getProperties().addProperty("p", ObjectWithProperties.ClassTypes.AtomicInt,
                p);

    }

    public boolean process(File in, File out) {
        if (!isImage(in))
            return false;
        File file = in;
        PixM original = null;

        p = (int) getProperties().getProperty("p");

        try {
            original = PixM.getPixM(new one.empty3.libs.Image(ImageIO.read(in)), maxRes);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
            // assertTrue(false);

        }
        PixM copy = new PixM(original.columns, original.lines);


        min = (double) getProperties().getProperty("min");

        for (int i = 0; i < original.columns; i++)

            for (int j = 0; j < original.lines; j++)


                if (original.luminance(i, j) < min) {

                    searchFromTo(original, copy, i, j,
                            min * 2);
                    p++;

                } else {
                    copy.set(i, j, original.get(i, j));
                }


        try {
            ImageIO.write(copy.getImage(), "jpg", out);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }


        Logger.getAnonymousLogger().log(Level.INFO, "point " + p);

        return true;
    }


    public void searchFromTo(
            PixM original, PixM copy, int i, int j, double min) {
        Point3D p1 = null;
        int i2 = i, j2 = j;
        
     /*   for(int k=0; k<original.columns*original.lines;k++)
                { 
            
              int [] k1 = new int[] {incr[(k/2)%8], 
                                     incr[(k/2+1)%8]};
                i2+= k1[0];
                j2 += k1[1];
            
           */
        for (int l = 1; l < original.columns; l++) {
            int[] incr = new int[]{

                    -l, -l, 0, 1,
                    l, -l, 0, 1,
                    -l, l, 1, 0,
                    -l, -l, 1, 0

            };

            for (int sq = 0; sq < incr.length; sq += 4) {
                int pass = 0;
                for (int i3 = incr[sq]; i3 < l && pass > -1; i3 += incr[(sq + 2)]) {

                    for (int j3 = incr[(sq + 1)]; j3 < l && pass > -1; j3 += incr[(sq + 3)]) {

                        pass++;
                        i2 = i + i3;
                        j2 = j + j3;
                        p1 = null;


                        if (original.luminance(i2, j2) >= min) {


                            copyPixel(original, i2,
                                    j2,
                                    copy, i, j);
                            return;
                        }


                        if (pass > 2 * l) pass = -1;

                    }
                }
            }
        }
        // Logger.getAnonymousLogger().log(Level.INFO, "error not found");

        return;
    }

    public void copyPixel(PixM m1, int i, int j,
                          PixM m2, int i2, int j2) {
        for (int c = 0; c < 3; c++) {

            m1.setCompNo(c);
            m2.setCompNo(c);

            m2.set(i2, j2, m1.get(i, j));
        }

    }
} 
