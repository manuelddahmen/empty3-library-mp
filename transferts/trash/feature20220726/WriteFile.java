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

import javaAnd.awt.image.imageio.ImageIO;

import one.empty3.libs.Image;
import one.empty3.library.ECImage;

import java.io.File;
import java.util.logging.*;

public class WriteFile {
    static int no = 1;
    static String directory = "./output/";

    public static void init() {

    }

    public static boolean writeNext(Image imageJpeg, String name) {
        writeNext(name, imageJpeg);
        return true;
    }

    public static void writeNext(String name, Image imageJpeg) {

        File n = new File(directory);

        n = new File(directory + File.separator + no + "-" + name + ".jpg");
        new File(n.getAbsolutePath().substring(0, n.getAbsolutePath().lastIndexOf('/'))).mkdirs();
        try {

            no++;
            ImageIO.write(imageJpeg, "jpg", n);
            Logger.getLogger(WriteFile.class.toString()).log(Level.INFO, "file written: " + n.getAbsolutePath());

        } catch (Exception ex) {

            ex.printStackTrace();
        }

    }
}
