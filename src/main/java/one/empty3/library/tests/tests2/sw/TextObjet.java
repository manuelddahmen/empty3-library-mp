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

package one.empty3.library.tests.tests2.sw;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.Point3D;
import one.empty3.library.TextureCol;
import one.empty3.library.TextureImg;
import one.empty3.library.core.tribase.Plan3D;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.libs.Color;

import java.awt.*;
import java.awt.image.BufferedImage;

/*__
 * Created by Win on 16-01-16.
 */
public class TextObjet extends Plan3D {
    private Point3D orig;
    private Point3D x2Vect;
    private Point3D y2Vect;

    private Color texTextureCol;


    private  one.empty3.libs.Image prerenderedImg;
    private String textString;


    public TextObjet(Point3D orig, Point3D x2Vect, Point3D y2Vect) {
        this.pointOrigine(orig);
        this.pointXExtremite(orig.plus(x2Vect));
        this.pointYExtremite(orig.plus(y2Vect));

        TextureCol c = new TextureCol(one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f));
        texture(c);


    }

    public Color texTextureCol() {
        return texTextureCol;
    }

    public void setTexTextureCol(Color color) {
        this.texTextureCol = color;
    }

    public void setText(String txt) {
        this.textString = txt;
        prerenderedImg = new  one.empty3.libs.Image(new BufferedImage(1920, 1080 / 5 * textString.length(), BufferedImage.TYPE_INT_ARGB));

        Graphics prerenderedImgGraphics = prerenderedImg.getGraphics();

        prerenderedImgGraphics.setColor(texTextureCol);

        prerenderedImgGraphics.drawString(txt, 0, 0);

        texture(new TextureImg(prerenderedImg));

    }


    public void deplace(Point3D point3D) {
        this.pointOrigine(this.pointOrigine().plus(point3D.getX()));
        this.pointXExtremite(this.pointXExtremite().plus(point3D.getY()));
        this.pointYExtremite(this.pointYExtremite().plus(point3D.getY()));
    }
}
