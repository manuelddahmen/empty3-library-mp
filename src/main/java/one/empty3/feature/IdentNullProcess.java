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

package one.empty3.feature;

import one.empty3.io.ObjectWithProperties;
import one.empty3.io.ProcessFile;

import one.empty3.libs.Image;
import one.empty3.ECImage;

import java.io.File;
import java.io.IOException;

public class IdentNullProcess extends ProcessFile {


    public IdentNullProcess() {
        super();
        getProperties().addProperty("luminanceFactor", ObjectWithProperties.ClassTypes.AtomicDouble, 1.0);
        getProperties().addProperty("redFactor", ObjectWithProperties.ClassTypes.AtomicDouble, 1.0);
        getProperties().addProperty("blueFactor", ObjectWithProperties.ClassTypes.AtomicDouble, 1.0);
        getProperties().addProperty("greenFactor", ObjectWithProperties.ClassTypes.AtomicDouble, 1.0);
    }

    @Override
    public boolean process(File in, File out) {
        try {
            PixM pixM = null;
            pixM = PixM.getPixM(new one.empty3.libs.Image(javax.imageio.ImageIO.read(in)), maxRes);


            double l = (double) getProperties().getProperty("luminanceFactor");
            double r = (double) getProperties().getProperty("redFactor");
            double g = (double) getProperties().getProperty("blueFactor");
            double b = (double) getProperties().getProperty("greenFactor");


            for (int i = 0; i < pixM.columns; i++) {
                for (int j = 0; j < pixM.getLines(); j++) {
                    pixM.setCompNo(0);
                    pixM.set(i, j, pixM.get(i, j) * l * r);
                    pixM.setCompNo(1);
                    pixM.set(i, j, pixM.get(i, j) * l * g);
                    pixM.setCompNo(2);
                    pixM.set(i, j, pixM.get(i, j) * l * b);
                }
            }

            Image image = pixM.getImage();
            one.empty3.feature.app.replace.javax.imageio.ImageIO.write(image, "jpg", out);
            addSource(out);
            return true;
        } catch (
                IOException e) {
            e.printStackTrace();
            return false;
        }

    }

}
