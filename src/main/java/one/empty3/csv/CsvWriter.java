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

package one.empty3.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/*__
 * Created by Win on 01-07-18.
 */
public class CsvWriter {
    private final String columnSeparator;
    private final String lineSeparator;
    FileOutputStream fis;

    public CsvWriter(String lineSeparator,
                     String columnSeparator
    ) {
        this.lineSeparator = lineSeparator;
        this.columnSeparator = columnSeparator;
    }

    public int openFile(File csvFile) {
        try {
            fis = new FileOutputStream(csvFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
        return 0;
    }

    public int writeLine(String[] lineArray) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (String column : lineArray) {
            sb.append(column);
            if (i < lineArray.length - 1)
                sb.append(columnSeparator);
        }
        sb.append(lineSeparator);

        try {
            fis.write(sb.substring(0).getBytes());
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }

    }

    public int closeFile() {
        try {
            fis.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return -2;
        }
        try {
            fis.close();
            return 0;
        } catch (IOException e) {

            e.printStackTrace();
            return -1;
        }
    }

}
