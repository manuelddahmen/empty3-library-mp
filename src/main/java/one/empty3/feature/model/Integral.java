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

package one.empty3.feature.model;

import one.empty3.feature.PixM;
import one.empty3.io.ProcessFile;

import one.empty3.library.Lumiere;
import one.empty3.libs.*;

import java.io.File;

public class Integral extends ProcessFile {
    @Override
    public boolean process(File in, File out) {
        try {
            PixM imageP = PixM.getPixM(new Image(in), maxRes);
            Image image = imageP.getImage();
            Image imageOut = new Image(imageP.getColumns(), imageP.getLines());
            int[] colors = new int[image.getheight()];
            for (int i = 0; i < image.getHeight(); i++) {
                for (int j = 0; j < image.getWidth(); j++) {
                    int color = colors[j];
                    Color colorO = new Color(color);
                    double[] doubles = Lumiere.getDoubles(color);
                    double[] doublesPoint = Lumiere.getDoubles(image.getRgb(j, i));

                    double[] dFinal;
                    for (int k = 0; k < 3; k++) {
                        dFinal[k] = doublesPoint[k] + doubles[k];

                    }

                    int cFinale = Lumiere.getInt(dFinal);

                    colors[j] = cFinale;

                    imageOut.setRgb(j, i, cFinale);
                }

            }
            imageOut.saveTo(out);

        } catch (RuntimeException ex) {
            return true;
        }
    }
}
