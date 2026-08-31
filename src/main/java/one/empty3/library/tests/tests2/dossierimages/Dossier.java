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

/*
 * 2013 Manuel Dahmen
 */
package one.empty3.library.tests.tests2.dossierimages;

import one.empty3.libs.Image;

import one.empty3.libs.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Dossier {
    public int limite = Integer.MAX_VALUE;
    private ArrayList<Image> images = new ArrayList<Image>();

    public void addDossier(File f) {
        if (f != null && f.exists() && f.isDirectory() && images.size() < limite) {
            File[] listFiles = f.listFiles();

            for (File l : listFiles) {

                awaitForImage(l);

            }
        }
        //Logger.getAnonymousLogger().log(Level.INFO, "Images size: " + images.size());
    }

    public void awaitForImage(File f) {
        if (f != null && f.exists() && images.size() < limite) {
            BufferedImage read = Image.staticLoadFile(f);

            if (read != null) {
                images.add(new Image(read));
            }


        }
    }

    public void awaitForRemove(Image i) {
        images.remove(i);
    }

    public void run() {
    }

    public ArrayList<Image> getImages() {
        return images;
    }


}