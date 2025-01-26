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

package one.empty3.feature20220726;

import one.empty3.io.ProcessFile;

import javaAnd.awt.image.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

public class TransformColor extends ProcessFile {
    @Override
    public boolean process(File in, File out) {

        PixM pix = PixM.getPixM(new one.empty3.libs.Image(in), maxRes);

        for (int i = 0; i < pix.getColumns(); i++) {
            for (int j = 0; j < pix.getLines(); j++) {
                for (int c = 0; c < pix.getCompNo(); c++) {
                    pix.setCompNo(c);
                    pix.set(i, j, 1 - pix.get(i, j));
                }
            }
        }

        new one.empty3.libs.Image(1,1,1.saveToFile(pix.normalize(0.0, 1.0).getImage(), "jpg", out);

        return true;
    }
}
