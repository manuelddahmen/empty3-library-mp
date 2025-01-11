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

package one.empty3.library;


import one.empty3.libs.Image;

import one.empty3.libs.*;

/*__
 * @author Manuel Dahmen
 */
//#TODO Repair
public class TextureCol extends ColorTexture {

    private StructureMatrix<Integer> color = new StructureMatrix<>(0, Integer.class);

    public TextureCol() {
        color.setElem(0);
    }

    public TextureCol(Color c) {
        if (c != null) {
            color.setElem(c.getRGB());
        }
        else
            color.setElem(-1);
    }

    public TextureCol(int c) {
        color.setElem(c);
    }
    public int color() {
        return color.getElem();
    }

    public void color(Color c) {
        color.setElem(c.getRGB());
    }

    @Override
    public void iterate() throws EOFVideoException {

    }

    public int getColorAt(double x, double y) {
        return color.getElem();
    }

    public void timeNext() {
    }

    public void timeNext(long milli) {
    }


    /*__
     * Quadrilatère numQuadX = 1, numQuadY = 1, coordArr, y 3----2 ^2y |\ | | 4 |
     * 0--\-1 1 -> 2x
     *
     * @param numQuadX nombre de maillage en coordArr
     * @param numQuadY nombre de maillage en y
     * @param x        valeur de coordArr
     * @param y        valeur de y
     * @return
     */
    public Color getMaillageTexturedColor(int numQuadX, int numQuadY, double x,
                                          double y) {
        return new Color(color.getElem());
    }

    public String toString() {
        long c = this.color.getElem();
        return "texture ( red:" + ((c&0x000000FF)>>0)+ "; green:" +
                ((c&0x0000FF00)>>8)+ "; blue:" + ((c&0x00FF0000)>>16) +
                "; alpha:" + ((c&0xFF000000)>>24) + ")\n";
    }

    @Override
    public MatrixPropertiesObject copy() throws CopyRepresentableError, IllegalAccessException, InstantiationException {
        return null;
    }
}
