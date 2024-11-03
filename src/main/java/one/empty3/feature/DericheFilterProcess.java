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

import one.empty3.io.ProcessFile;
import one.empty3.library.ECImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class DericheFilterProcess extends ProcessFile {


    @Override
    public boolean process(File in, File out) {
        PixM pixM = null;
        try {
            pixM = PixM.getPixM(new ECImage(ImageIO.read(in)), maxRes);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        try {
            ImageIO.write(pixM.getImage(), "jpg", out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        return false;
    }
}
