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

package one.empty3.feature_2;

import matrix.PixM;
import one.empty3.Image;
import one.empty3.io.ProcessFile;

import one.empty3.ImageIO;

import java.io.File;

public class DericheFilterProcess extends ProcessFile {


    @Override
    public boolean process(File in, File out) {
        PixM pixM = PixM.getPixM(ImageIO.read(in), maxRes);


        Image.saveFile(pixM.getImage(), "jpg", out, shouldOverwrite);


        return false;
    }
}
