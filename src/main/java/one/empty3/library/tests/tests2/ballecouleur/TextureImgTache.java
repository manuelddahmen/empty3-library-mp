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
 * Global license : * CC Attribution
 * <p>
 * author Manuel Dahmen <manuel.dahmen@gmx.com>
 * <p>
 * *
 */
package one.empty3.library.tests.tests2.ballecouleur;

import one.empty3.libs.*;
import one.empty3.library.*;
import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.Point2D;
import one.empty3.library.TextureImg;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;
import java.util.HashMap;

/*__
 *
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
public class TextureImgTache extends TextureImg {

    private final HashMap<Point2D, Color> map;
    double dist = 0.0;
    Color actu = null;
    float actuA;
    float actuR;
    float actuG;
    float actuB;

    public TextureImgTache(HashMap<Point2D, Color> colors) {
        super(new  one.empty3.libs.Image(100, 100,  one.empty3.libs.Image.TYPE_INT_ARGB));
        map = colors;
    }

    @Override
    public int getColorAt(double x, double y) {
        return calculerCouleur(x, y).getRGB();
    }


    public Color calculerCouleur(double x, double y) {
        final Point2D pData = new Point2D(x, y);
        actu = Color.newCol(0f, 0f, 0f);

        final HashMap<Point2D, Double> pond;
        pond = new HashMap<Point2D, Double>();

        map.forEach((u, t) -> {
                    double dist2 = u.distance(pData);

                    pond.put(u, dist2);

                    dist += dist2;
                }
        );

        pond.forEach((t, u) -> {
            actuA += map.get(t).getAlpha() / 256f * u;
            actuR += map.get(t).getRed() / 256f * u;
            actuG += map.get(t).getGreen() / 256f * u;
            actuB += map.get(t).getBlue() / 256f * u;

            actu = Color.newCol((float) (actuR / dist), (float) (actuG / dist), (float) (actuB / dist));
        });

        return actu;
    }

}
