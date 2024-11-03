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

import one.empty3.library.ColorTexture;
import one.empty3.library.core.lighting.Colors;

import javax.imageio.ImageIO;
import java.awt.*;

import one.empty3.libs.Image;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ColoredMaps {

    public static void main(String[] args) {
        Logger.getAnonymousLogger().log(Level.INFO,
                "Colored Maps -- first pass Setting Rect Boundaries"
        );


        CsvReader csvReader = new CsvReader(new File("allCountries/allCountries.txt"),
                "" + '\t', "" + '\n', false);
        SetMinMax setMinMax = new SetMinMax();
        csvReader.setAction(setMinMax);
        csvReader.process();


        HashMap<String, SetMinMax.MyDim> myDims = setMinMax.getMyDims();

        myDims.forEach((s, dim) -> {
            Logger.getAnonymousLogger().log(Level.INFO, s);
            Logger.getAnonymousLogger().log(Level.INFO, dim.toString());
        });
        Logger.getAnonymousLogger().log(Level.INFO, "myDims size " + myDims.size());


        String serial = Seriald.newSerial();


        myDims.forEach((countryCode, myDim) -> {
            Logger.getAnonymousLogger().log(Level.INFO,
                    "Colored Maps map " + countryCode
            );
            try {
                Image image = new Image(1800,
                        (int)
                                Ratio.imageHeight(myDim.latitudeExtend(),
                                        myDim.longitudeExtend(),
                                        0,
                                        1800), Image.TYPE_INT_RGB);
                Pixeler pixeler = new Pixeler(image);
                Graphics graphics = image.getGraphics();
                graphics.setColor(new Color(Color.TRANSLUCENT));
                graphics.fillRect(0, 0, image.getWidth() - 1, image.getHeight() - 1);

                CsvReader csvReader1 = new CsvReader(new File("allCountries/allCountries.txt"),
                        "" + '\t', "" + '\n', false);
                csvReader1.setAction(new DrawOneCountryCsvAction(
                        pixeler,
                        myDim,
                        new ColorTexture(Colors.random())
                ));
                csvReader1.process();
                ImageIO.write(pixeler.getImage(), "jpg", new File(
                        getColoredMaps(serial, countryCode)));

            } catch (Exception ex) {
                ex.printStackTrace();
            }


        });
    }

    private static String getColoredMaps(String serial, String countryCode) {
        String fileStr1 = ".\\generated_Maps\\"
                + "\\map-" + serial + "\\" + countryCode;
        File file = new File(fileStr1);
        file.mkdirs();
        return fileStr1 + ".jpg";
    }
}
