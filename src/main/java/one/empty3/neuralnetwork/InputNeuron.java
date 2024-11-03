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

package one.empty3.neuralnetwork;

import one.empty3.feature.PixM;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InputNeuron extends Neuron {
    private int res;
    private File uri;

    public InputNeuron(int length) {
        super(length);
    }


    public File getUri() {
        return uri;
    }

    public void setUri(File uri) {
        this.uri = uri;
    }


    public boolean loadData(File file) throws Exception {
        PixM p = null;
        try {
            if (ImageIO.getImageReaders(file) == null)
                return false;
            if (!Arrays.asList("jpg", "png").contains(file.getAbsolutePath().substring(
                    file.getAbsolutePath().length() - 3, file.getAbsolutePath().length())))
                return false;
            p = PixM.getPixM(new one.empty3.libs.Image(ImageIO.read(file)), Config.RES);
            this.setW(new double[Config.RES * Config.RES * 3]);
            for (int j = 0; j < Config.RES; j++)
                for (int i = 0; i < Config.RES; i++) {
                    for (int comp = 0; comp < p.getCompCount(); comp++) {
                        p.setCompNo(comp);
                        this.getW()[ordPix(i, j, comp)] = p.get(i, j);
                    }
                }
            this.uri = file;
            return true;
        } catch (IIOException exception) {
            Logger.getAnonymousLogger().log(Level.INFO, "Error reading image. Returns");
            throw new Exception(exception);

        }
    }

    private int ordPix(int i, int j, int comp) {
        return ((j*res+i)*res)*comp+comp;
    }
}
