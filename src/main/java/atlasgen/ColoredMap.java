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

package atlasgen;

import javax.imageio.ImageIO;
import java.awt.*;

import one.empty3.libs.Image;

import java.io.File;
import java.util.logging.Logger;
import java.util.logging.Level;

public class ColoredMap {

    public static void main(String[] args) {
        Logger.getAnonymousLogger().log(Level.INFO,
                "Colored Map"
        );
        Image image = new Image(1800, 1600, Image.TYPE_INT_RGB);
        Graphics graphics = image.getGraphics();
        graphics.setColor(new Color(Color.TRANSLUCENT));
        graphics.fillRect(0, 0, image.getWidth() - 1, image.getHeight() - 1);
        Pixeler pixeler = new Pixeler(image);
        CsvReader csvReader = new CsvReader(new File("allCountries/allCountries.txt"),
                "" + '\t', "" + '\n', false);
        csvReader.setAction(new DrawPerCountryCsvAction(pixeler));
        csvReader.process();
        try {
            ImageIO.write(pixeler.getImage(), "jpg", Seriald.newOutputFile("ColoredMap"));
        } catch (Exception ex) {
        }

    }
}
