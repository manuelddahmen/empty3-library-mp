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

package one.empty3.csv; /*__
 * Created by manushell on 19-06-18.
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class CsvReader {
    private final int countLineModulo = 10000;
    private final int MAX_LINE_LENGTH_2 = 5000;
    private boolean fileContainsFieldDescription;
    private String csvFieldSeparator;
    private File csvFile;
    private String csvLineSeparator;
    private List<String> columns = new ArrayList<>();
    private CsvAction csvAction;

    public CsvReader(File csvFile, String csvFieldSeparator, String csvLineSeparator, boolean fileContainsFieldDescription) {
        this.csvFile = csvFile;
        this.csvFieldSeparator = csvFieldSeparator;
        this.csvLineSeparator = csvLineSeparator;
        this.fileContainsFieldDescription = fileContainsFieldDescription;
    }

    public void process() {
        boolean firstLine = true;
        int nRead = 0;

        ArrayList<String[]> lignesRestantes = new ArrayList<>();
        ArrayList<String[]> colonnesRestantes = new ArrayList<>();
        ArrayList<String[]> morceauxRestants = new ArrayList<>();


        String reste = "";

        try {
            FileInputStream fileInputStream = null;
            fileInputStream = new FileInputStream(csvFile);
            byte[] bytes = new byte[MAX_LINE_LENGTH_2];
            int countLines = 0;

            while ((nRead = fileInputStream.read(bytes)) > 0 /*|| reste.toString().length()>0*/) {
                if (countLines % countLineModulo == 0)
                    ;//Logger.getAnonymousLogger().log(Level.INFO, ""+countLines+" lignes traitées");
                String read = "";
                for (int i = 0; i < nRead; i++)
                    read += (char) (bytes[i]);
                read = "" + reste.toString() + read;
                reste = "";

                if (firstLine && fileContainsFieldDescription) {
                    String[] split0 = read.split(csvLineSeparator);
                    String[] split1 = split0[0].split(csvFieldSeparator);
                    for (int i = 0; i < split1.length; i++) {
                        columns.add(split1[i]);
                    }
                    firstLine = false;
                    // Traitement terminé.
                    // Reprendre ce qu'il reste pour la loop.
                    reste += split0[split0.length - 1];

                } else {
                    String[] split0 = read.split(csvLineSeparator);
                    for (int j = 0; j < split0.length - 1; j++) {
                        String[] split1 = split0[0].split(csvFieldSeparator);


                        // Traitement terminé. On passe à l'action
                        csvAction.processLine(new CsvLine(columns, split1));
                        countLines++;
                    }
                    // Reprendre ce qu'il reste pour la loop.

                    reste += split0[split0.length - 1];
                }

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }


    }

    public int getMAX_LINE_LENGTH_2() {
        return MAX_LINE_LENGTH_2;
    }


    public boolean isFileContainsFieldDescription() {
        return fileContainsFieldDescription;
    }

    public void setFileContainsFieldDescription(boolean fileContainsFieldDescription) {
        this.fileContainsFieldDescription = fileContainsFieldDescription;
    }

    public String getCsvFieldSeparator() {
        return csvFieldSeparator;
    }

    public void setCsvFieldSeparator(String csvFieldSeparator) {
        this.csvFieldSeparator = csvFieldSeparator;
    }

    public File getCsvFile() {
        return csvFile;
    }

    public void setCsvFile(File csvFile) {
        this.csvFile = csvFile;
    }

    public String getCsvLineSeparator() {
        return csvLineSeparator;
    }

    public void setCsvLineSeparator(String csvLineSeparator) {
        this.csvLineSeparator = csvLineSeparator;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public CsvAction getAction() {
        return csvAction;
    }

    public void setAction(CsvAction csvAction) {
        this.csvAction = csvAction;
    }
}

