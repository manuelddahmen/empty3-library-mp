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
            PixM image = new PixM(imageP.getColumns(), imageP.getLines());
            int[] colors = new int[image.getLines()];
            for (int j = 0; j < image.getLines(); j++) {
                for (int i = 0; i < image.getColumns(); i++) {
                    int color = colors[j];
                    Color colorO = new Color(color);
                    double[] doubles = Lumiere.getDoubles(color);
                    double[] doublesPoint = Lumiere.getDoubles(image.getInt(i, j));

                    double[] dFinal = new double[] {0,0,0, 0};
                    for (int k = 0; k < 4; k++) {
                        dFinal[k] = doublesPoint[k] + doubles[k];

                    }

                    int cFinale = Lumiere.getInt(dFinal);

                    colors[j] = cFinale;

                    image.set(image.index(i, j), cFinale);
                }

            }
            image.normalize(0, 1).getImage().saveFile(out);

            return true;
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
