/*
 * Copyright (c) 2024.
 *
 *
 *  Copyright 2023 Manuel Daniel Dahmen
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package one.empty3.feature.model;


import one.empty3.feature.M3;
import one.empty3.feature.PixM;
import one.empty3.io.ProcessFile;

import java.io.File;

/*
 * Magnitude² filter
 */
public class MagnitudeProcess extends ProcessFile {

    public boolean process(File in, File out) {

        if (!in.getName().endsWith(".jpg"))
            return false;
        File file = in;
        PixM pix;
        try {
            pix = PixM.getPixM(new one.empty3.libs.Image(file), maxRes);
            GradientFilter gf = new GradientFilter(pix.getColumns(),
                    pix.getLines());
            PixM[][] imagesMatrix = gf.filter(
                    new M3(
                            pix, 2, 2)
            ).getImagesMatrix();
            Linear linearProd1 = new Linear(imagesMatrix[0][0], imagesMatrix[0][0],
                    new PixM(pix.getColumns(), pix.getLines()));
            linearProd1.op2d2d(new char[]{'*'}, new int[][]{{1, 0}}, new int[]{2});
            Linear linearProd2 = new Linear(imagesMatrix[0][1], imagesMatrix[0][1],
                    new PixM(pix.getColumns(), pix.getLines()));
            linearProd2.op2d2d(new char[]{'*'}, new int[][]{{1, 0}}, new int[]{2});
            Linear res = new Linear(linearProd1.getImages()[2], linearProd2.getImages()[2],
                    new PixM(pix.getColumns(), pix.getLines()));
            res.op2d2d(new char[]{'+'}, new int[][]{{1, 0}}, new int[]{2});
            one.empty3.libs.Image.saveFile(res.getImages()[2].normalize(0.0, 1.0).getImage(), "jpg", out, shouldOverwrite);

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
