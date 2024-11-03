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

import one.empty3.library.*;

import java.awt.*;

import one.empty3.libs.Image;

class HeightMapSurfaceSphere extends HeightMapSurface {
    ITexture heightMap;

    @Override
    public Point3D calculerPoint3D(double u, double v) {
        return surface.getData0d().calculerPoint3D(u, v);
    }

    public HeightMapSurfaceSphere() {
        super();
    }

    public HeightMapSurfaceSphere(Axe axe, double radius, Image bi) {
        super(new Sphere(axe, radius), bi);
    }

    public Point3D height(double u, double v) {
        int i = (int) (u * (image.getElem().getImage().getElem().getWidth()));
        int j = (int) (v * (image.getElem().getImage().getElem().getHeight()));
        if (i < 0) i = 0;
        if (j < 0) j = 0;
        if (i >= image.getElem().getImage().getElem().getWidth())
            i = image.getElem().getImage().getElem().getWidth() - 1;
        if (j >= image.getElem().getImage().getElem().getHeight())
            j = image.getElem().getImage().getElem().getHeight() - 1;

        Point3D mult = surface.getElem().calculerPoint3D(u, v).moins(((Sphere) surface.getElem()).getCircle().getCenter()).norme1().
                mult((
                        image.getElem().getImage().getElem().getRGB(i, j) & 0x00FF0000 >> 16) / 256.);
        return mult


                ;
    }
}
