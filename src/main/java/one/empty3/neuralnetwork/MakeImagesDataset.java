/*
 *
 *  *
 *  *  * Copyright (c) 2025. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2024 Manuel Daniel Dahmen
 *  *  *
 *  *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *    you may not use this file except in compliance with the License.
 *  *  *    You may obtain a copy of the License at
 *  *  *
 *  *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  *    Unless required by applicable law or agreed to in writing, software
 *  *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *    See the License for the specific language governing permissions and
 *  *  *    limitations under the License.
 *  *
 *  *
 *
 *
 *
 *  * Created by $user $date
 *
 *
 */

package one.empty3.neuralnetwork;

import one.empty3.csv.*;

import java.io.File;
import java.util.Arrays;

public class MakeImagesDataset {
    private final CsvWriter csvWriter;
    CsvReader csvReader;
    long lineId = 100000;

    public MakeImagesDataset(File output, File[] directories) {
        csvWriter = new CsvWriter("\n", "\t");
        csvWriter.openFile(output);
        csvWriter.writeLine(new String[]{"LineID", "Path", "Object location", "Labels"});
        for (int i = 0; i < directories.length; i++) {
            File directory = directories[i];
            browseDirectory(directory);
        }
        CsvLine csvLine;
        csvWriter.closeFile();
    }

    private void browseDirectory(File directory) {
        if (directory.isDirectory()) {
            File[] files = directory.listFiles();

            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.isDirectory()) {
                    browseDirectory(file);
                } else {
                    if (Arrays.asList("jpg", "png").contains(
                            file.getAbsolutePath()
                                    .substring(file.getAbsolutePath().length() - 3,
                                            file.getAbsolutePath().length()).toLowerCase()))
                        csvWriter.writeLine(new String[]{"" + (lineId++), file.getAbsolutePath(),
                                "{}", "{}"});
                }
            }
        }
    }

    public static void main(String[] args) {
        new MakeImagesDataset(new File("datasetMyImageCollectionManuelDahmen.input.csv"),
                new File[]{new File("./images/s"),
                        new File("./images/m"),
                        new File("./images/batiments"),
                        new File("./images/"),
                        new File("./images/out")});
    }
}
