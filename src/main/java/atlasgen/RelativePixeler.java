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

package atlasgen;

import java.awt.*;

import one.empty3.libs.Image;

/*__
 * Created by manuel on 19-06-18.
 */
public class RelativePixeler {
    private final SetMinMax.MyDim minMax;
    private Image image;
    private int width;
    private int height;


    public RelativePixeler(Image image, SetMinMax.MyDim minMax) {
        this.image = image;
        width = image.getWidth();
        height = image.getHeight();
        this.minMax = minMax;
    }

    public void pixelize(int x, int y, Color color) {
        image.setRGB(x < 0 ? 0 : (x > width - 1 ? width - 1 : x),
                y < 0 ? 0 : (y > width - 1 ? width - 1 : y),
                color.getRGB());
    }

    public Point convert(double ratioX, double ratioY) {
        return new Point((int) (ratioX * width), (int) (ratioY * height));
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
}
