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
package one.empty3.library;


import one.empty3.libs.Image;
import one.empty3.libs.Color;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/*__
 * @author manu
 */
public class ImageTexture extends ITexture {
    private final StructureMatrix<Image> image = new StructureMatrix<>(0, Image.class);


    public ImageTexture(@NotNull Image imageE) {
        image.setElem(imageE);
        if(image.getElem()==null || image.getElem().getWidth()==0 || image.getElem().getHeight()==0)
            throw new RuntimeException("Image null");
    }

    public ImageTexture(File bif) {
        try {
            if(bif==null||!bif.exists()||!bif.isFile()) {
                throw new RuntimeException("Image file null or not exists" );
            }
            Image image1;
            image1= (Image) Image.getFromFile(bif);
            if(image1==null&&image1.getBi()==null || image1.getWidth()==0 || image1.getHeight()==0)
                throw new RuntimeException("Image null");
            image.setElem(image1);
            if(image.getElem()==null || image.getElem().getWidth()==0 || image.getElem().getHeight()==0)
                throw new RuntimeException("Image null");
        } catch (RuntimeException ex) {
            System.err.println("Error constructor" + this.getClass() + "\n" + ex.getMessage());
            ex.printStackTrace();
        }
    }

    @Override
    public void iterate() throws EOFVideoException {

    }

    @Override
    public Point2D getCoord(double x, double y) {
        if (repeatX <= 1 && repeatY <= 1) {
            return super.getCoord(x, y);
        } else {
            Point2D coords = getRepeatCords(x, y);
            return super.getCoord(coords.x, coords.y);
        }
    }

    @Override
    public int getColorAt(double x, double y) {
        Point2D trans = getCoord(x, y);
        return couleur(trans.x, trans.y);
    }

    public Point2D getRepeatCords(double xr, double yr) {

        return new Point2D(
                Math.IEEEremainder(xr, 1.0 / repeatX) * repeatX,
                Math.IEEEremainder(yr, 1.0 / repeatY) * repeatY);
    }

    protected int couleur(double rx, double ry) {
        int x = (int) (rx * image.getElem().getWidth());
        int y = (int) (ry * image.getElem().getHeight());
        if (x < 0) {
            x = 0;
        }
        if (y < 0) {
            y = 0;
        }
        if (x >= image.getElem().getWidth()) {
            x = image.getElem().getWidth() - 1;
        }
        if (y >= image.getElem().getHeight()) {
            y = image.getElem().getHeight() - 1;
        }


        return image.getElem() != null ? image.getElem().getRgb(x, y) : transparent;
    }



    public Image getImage() {
        return image.getElem();
    }

    public void setImage(@NotNull Image bi) {
        image.setElem(bi);
    }


    public void setTransparent(Color tr) {
        this.transparent = tr.getRGB();
    }

    public void timeNext() {
    }

    public void timeNext(long milli) {
    }

    @Override
    public StructureMatrix getDeclaredProperty(String name) {
        return image;
    }

    @Override
    public MatrixPropertiesObject copy() throws CopyRepresentableError, IllegalAccessException, InstantiationException {
        return null;
    }
}
