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

import one.empty3.io.*;

import java.io.File;

import javaAnd.awt.image.imageio.ImageIO;

public class Draw extends ProcessFile {
    public Draw() {

    }

    public boolean process(File in, File out) {
        try {
            new Image(1,1,1.saveToFile(new Image(in), "jpg", out);

        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }


        return true;
    }
}
