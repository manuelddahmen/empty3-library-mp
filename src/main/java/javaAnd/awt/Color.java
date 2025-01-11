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

package javaAnd.awt;

import one.empty3.library.Lumiere;

public class Color extends java.awt.Color/*java.awt.Color*/ {

    public Color(int c) {
        super(c);
    }

    public Color(float a, float b, float c) {
        super(a, b, c);
    }
    public static java.awt.Color color(double v, double v1, double v2) {
        return new Color((float) v, (float) v1, (float) v2);

    }

    public static java.awt.Color random() {
        java.awt.Color random = color((float) Math.random(), (float) Math.random(), (float) Math.random());
        return random;
    }

    public static java.awt.Color color(float r, float r1, float r2) {
        return new Color(r, r1, r2);
    }

    public static java.awt.Color color(int r, int g, int b) {

        return new Color(r, g, b);
    }


    public static float[] getColorComponents(java.awt.Color rgba) {
        float[] aRgba = new float[]{rgba.getRed(), rgba.getGreen(), rgba.getBlue(), rgba.getAlpha()};
        return aRgba;
    }

    public static int intConv(java.awt.Color color) {
        return color.getRGB();
    }

    public static float[] intConvToFloatArray(int color) {
        java.awt.Color color1 = Lumiere.getColor(color);
        return new float[]{color1.getRed(), color1.getGreen(), color1.getBlue()};
    }

    public static int floatArrayConvToIntcolorComponents(float[] colorComponents) {
        return new Color(colorComponents[0], colorComponents[1], colorComponents[2]).getRGB();
    }

    public static java.awt.Color valueOf(int rgb) {
        return new Color(rgb);
    }

    public java.awt.Color Color(int rgb) {
        return new Color(rgb);
    }

}
