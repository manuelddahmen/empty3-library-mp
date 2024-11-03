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

/*__
 * *
 * Global license : * Microsoft Public Licence
 * <p>
 * author Manuel Dahmen _manuel.dahmen@gmx.com_
 * <p>
 * *
 */
package one.empty3.library;


import one.empty3.ECImage;

//import com.xuggle.mediatool.MediaListenerAdapter;

import java.awt.*;
import java.util.HashMap;

/*__
 * @author manu
 */
public abstract class ITexture implements MatrixPropertiesObject{
    private int transparent = Color.BLACK.getRGB();
    public static final int COLOR_IDENT = 0;
    public static final int COLOR_MIROR_X = 1;
    public static final int COLOR_MIROR_Y = 2;
    public static final int COLOR_MIROR_XY = 4;
    public static final int COLOR_ROT_090 = 8;
    public static final int COLOR_ROT_180 = 16;
    public static final int COLOR_ROT_270 = 32;
    public int onTextureEnds = 0;
    protected int colorMask = COLOR_IDENT;
    protected int repeatX = 1;
    protected int repeatY = 1;

    DeformMap dm;

    public int getColorMask() {
        return colorMask;
    }
    
    public void setColorMask(int colorMask) {
        this.colorMask = colorMask;
    }

    public Point2D getCoord(double x, double y) {

        Point2D p = new Point2D(x, y);

        if (getColorMask() == COLOR_IDENT) {

        } else if ((getColorMask() & COLOR_MIROR_X) > 0) {
            p = new Point2D(1.0 - p.x, p.y);
        } else if ((getColorMask() & COLOR_MIROR_Y) > 0) {
            p = new Point2D(p.x, 1.0 - p.y);
        } else if ((getColorMask() & COLOR_MIROR_XY) > 0) {
            p = new Point2D(p.y, p.x);
        } else if ((getColorMask() & COLOR_ROT_090) > 0) {
            p = new Point2D(1.0 - p.y, p.x);
        } else if ((getColorMask() & COLOR_ROT_180) > 0) {
            p = new Point2D(1.0 - p.x, 1.0 - p.x);
        } else if ((getColorMask() & COLOR_ROT_270) > 0) {
            p = new Point2D(p.y, 1.0 - p.x);
        }
        return p;
    }

    public void setDeformMap(DeformMap map) {
        dm = map;
    }

    public DeformMap getDeformMap(DeformMap map) {
        return dm;
    }

    /*__
     * Retourne color at point (coordArr*textresx, y*textresy)
     *
     * @param x 0..1
     * @param y 0..1
     * @return color;
     */
    public abstract int getColorAt(double x, double y);


    public void timeNext(){} 

    public void timeNext(long milli){} 

    public void iterate() throws EOFVideoException {

    }
    @Override
    public StructureMatrix getDeclaredProperty(String name) {
        return null;
    }

    @Override
    public void declareProperties() {

    }

    @Override
    public HashMap<String, StructureMatrix> declarations() {
        return null;
    }

    public int getRepeatX() {
        return repeatX;
    }

    public void setRepeatX(int repeatX) {
        this.repeatX = repeatX;
    }

    public int getRepeatY() {
        return repeatY;
    }

    public void setRepeatY(int repeatY) {
        this.repeatY = repeatY;
    }

    public int getTransparent() {
        return transparent;
    }

    public void setTransparent(int transparent) {
        this.transparent = transparent;
    }
}

