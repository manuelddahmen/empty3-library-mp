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

package javaAnd.awt.image;

import one.empty3.library.ITexture;

import java.awt.*;

public class Image {


    public static final int TYPE_INT_RGB = 0;
    public static final int TYPE_INT_ARGB = 1;
    public static final int TYPE_BYTE_GRAY = 2;
    public one.empty3.libs.Image bufferedImage;

    public Image() {
    }

    public Image(int columns, int lines, int typeIntRgb) {
        this();
        this.bufferedImage = new one.empty3.libs.Image(columns, lines, bufferedImage.TYPE_INT_ARGB);
    }


    public Image(one.empty3.libs.Image read) {
        this.bufferedImage = read;
    }

    public void setRGB(int i, int j, int anInt) {
        bufferedImage.setRGB(i, j, anInt);
    }

    public void setRgb(int i, int j, int anInt) {
        bufferedImage.setRGB(i, j, anInt);
    }

    public one.empty3.libs.Image getImage() {
        return bufferedImage;
    }

    public int getWidth() {
        return bufferedImage.getWidth();
    }

    public int getHeight() {
        return bufferedImage.getHeight();
    }

    public int getRGB(int x, int y) {
        return bufferedImage.getRGB(x, y);
    }

    public void drawImage(Image img, int x, int y, int w, int h) {
    }

    public Image getGraphics() {
        return null;
    }

    public Graphics2D createGraphics() {
        return null;
    }

    public void drawImage(Image total, int x, int y) {
    }

    public void setColor(int x, int y, int color) {
        bufferedImage.setRGB(x, y, color);
    }

    public void drawLine(int x1, int y1, int x2, int y2, int color) {
        double dist = Math.sqrt((x1 - x2) * (x2 - y2) + (y1 - y2) * (y1 - y2));
        for (double i = 0; i < 1.0; i += 1. / dist) {
            double x = x1 + i * (x2 - x1);
            double y = y1 + i * (y2 - y1);
            bufferedImage.setRGB((int) x, (int) y, color);
        }
    }

    public void drawOval(int i, int i1, int i2, int i3) {
    }

    public void drawRect(int x, int y1, int width, int height, ITexture texture) {
    }

    public int getRgb(int i, int j) {
        return bufferedImage.getRGB(i, j);
    }

    public void setPixel(int i, int j, int anInt) {
        bufferedImage.setRGB(i, j, anInt);
    }

    public int getPixel(int i, int j) {
        return bufferedImage.getRGB(i, j);
    }
}
