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

import one.empty3.libs.Color;

import one.empty3.feature.PixM;
import one.empty3.io.ProcessFile;

import java.io.File;

import one.empty3.libs.Image;

public class IsleProcess extends ProcessFile {
    public boolean process(File in, File out) {


        if (!in.getName().endsWith(".jpg"))

            return false;

        File file = in;

        PixM pix = null;
        Image img = null;
        try {
            img = new one.empty3.libs.Image(file);
            pix = PixM.getPixM(img, -10.0);

        } catch (Exception ex) {

            ex.printStackTrace();

            return false;

            // assertTrue(false);


        }

        IsleFilterPixM il = new IsleFilterPixM(pix);
        il.setValues(Color.BLUE.getRGB(), Color.WHITE.getRGB(), 0.4);
        il.filter();
        try {

            pix.getImage().saveFile( out);

        } catch (Exception ex) {

        }

        return false;
    }
}
