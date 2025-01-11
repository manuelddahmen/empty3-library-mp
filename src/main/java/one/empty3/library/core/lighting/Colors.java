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
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package one.empty3.library.core.lighting;

import one.empty3.library.Lumiere;
import one.empty3.libs.*;
import java.util.Random;

/*__
 * Meta Description missing
 * @author Manuel Dahmen dathewolf@gmail.com
 */
public class Colors {
    /*
    public class ColorDist implements Comparable {
        public Color color;
        public double dist;
        
        public int compareTo(Object o) {
            if (o instanceof ColorDist) {
                ColorDist cd = (ColorDist) o;
                return dist<cd.dist?-1:(dist==cd.dist?0:1);
           } else 
                return 0;//throw??
        }
    }
    */
    public static Color TRANSPARENT = new Color(Lumiere.getIntFromFloats(1f, 0f, 0f, .5f));
    private static final Random random = new Random();

    public static Color random() {
        return new Color(Lumiere.getIntFromFloats(
                (float) random.nextDouble(),
                (float) random.nextDouble(),
                (float) random.nextDouble()
        ));
    }


    public abstract static class FArrayElem {
        public abstract double op(double d);
    }

    /***
     * @param c color array
     * @param d factor array
     * @param norm summary totally normal verse
     * @return moyenne ponderee in bloom
     */
    public static Color mean(Color[] c, double[] d, double norm) {
        int compNo = 4;
        if (c == null || d == null || c.length != d.length)
            throw new NullPointerException("index not equals or null");
        float[] r = new float[compNo];
        float[] f = new float[compNo];
        float sum = 0f;
        for (int j = 0; j < compNo; j++)
            r[j] = 0f;
        for (int i = 0; i < c.length; i++) {
            float proximityTerm = ((float) d[i]);
            sum += proximityTerm;
            c[i].getRGBComponents(f);
            for (int j = 0; j < compNo; j++)
                r[j] += (float) (f[j] * proximityTerm * norm);
        }
        return getColor(compNo, r, sum);
    }

    /***
     * @param c color array
     * @param d factor array
     * @param norm summary totally normal verse
     * @return spec funk house gouse in bloom
     */
    public static Color proxymity(Color[] c, double[] d, double norm) {
        int compNo = 4;
        if (c == null || d == null || c.length != d.length)
            throw new NullPointerException("index not equals or null");
        float[] r = new float[compNo];
        float[] f = new float[compNo];
        float sum = 0f;
        for (int j = 0; j < compNo; j++)
            r[j] = 0f;
        for (int i = 0; i < c.length; i++) {

        }
        // float sum=0f;
        for (int i = 0; i < c.length; i++) {

            // besoin de distMin pour faire partiviper les autres?
            float proxymityTerm = (float) Math.exp(-((float) d[i]) / (1f + (float) d[i]));

            sum += proxymityTerm;
            c[i].getRGBComponents(f);
            for (int j = 0; j < compNo; j++)
                r[j] += (float) (f[j] * proxymityTerm * norm);
        }
        return getColor(compNo, r, sum);
    }


    /***
     * True colors results
     * @param norm 1.0
     * @param cd dist sorted array
     * @param n number of effective computed values from array index 0
     * @return interpoled color.
     */
    public static Color proxymity(ColorDist[] cd, double norm, int n) {
        int compNo = 4;
        if (cd == null)
            throw new NullPointerException("index not equals or null");
        float[] r = new float[compNo];
        float[] f = new float[compNo];
        float sum = 0f;
        for (int j = 0; j < compNo; j++)
            r[j] = 0f;
        for (int i = 0; i < cd.length; i++) {

        }
        // float sum=0f;
        for (int i = 0; i < n; i++) {

            // besoin de distMin pour faire partiviper les autres?
            float proxymityTerm = (float) Math.exp(-(float) (1f * cd[i].dist / cd[cd.length - 1].dist));

            sum += proxymityTerm;
            cd[i].color.getRGBComponents(f);
            for (int j = 0; j < compNo; j++)
                r[j] += (float) (f[j] * proxymityTerm * norm / n);
        }
        for (int i = 0; i < compNo; i++) {
            r[i] /= (float) Math.exp(0.0);// ces malafes qui nous gouvernent en vrai.
            if (Float.isNaN(r[i]) || Float.isInfinite(r[i]))
                r[i] = 1f;
        }
        return new Color(Lumiere.getIntFromFloats(r[0], r[1], r[2]));
    }


    /***
     * True colors results
     * @param norm 1.0
     * @param cd dist sorted array 
     * @return interpoled color
     * @param n number of effective computed values from array index 0
     */
    public static Color mean(ColorDist[] cd, double norm, int n) {
        int compNo = 4;
        if (cd == null)
            throw new NullPointerException("index not equals or null");
        float[] r = new float[compNo];
        float[] f = new float[compNo];
        float sum = 0f;
        for (int j = 0; j < compNo; j++)
            r[j] = 0f;
        for (int i = 0; i < n; i++) {
            sum += (float) cd[i].dist;
        }
        // float sum=0f;
        for (int i = 0; i < n; i++) {

            // besoin de distMin pour faire partiviper les autres?
            float proximityTerm = (float) (cd[i].dist);
            cd[i].color.getRGBComponents(f);
            for (int j = 0; j < compNo; j++)
                r[j] += (float) (f[j] * proximityTerm * norm);
        }
        return getColor(compNo, r, sum);
    }

    private static Color getColor(int compNo, float[] r, float sum) {
        for (int i = 0; i < compNo; i++) {
            r[i] /= sum;
            if (Float.isNaN(r[i]) || Float.isInfinite(r[i]))
                r[i] = 1f;
        }
        return new Color(Lumiere.getIntFromFloats(r[0], r[1], r[2]));
    }

}
