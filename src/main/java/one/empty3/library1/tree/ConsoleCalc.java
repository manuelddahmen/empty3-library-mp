/*
 *
 *  *
 *  *  * Copyright (c) 2026. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2026 Manuel Daniel Dahmen
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

package one.empty3.library1.tree;

import one.empty3.library.StructureMatrix;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.util.List;

public class ConsoleCalc {
    public static void main(String[] args) {
        String filePath = null;
        for (int i = 0; i < args.length; i++) {
            if ("--file".equals(args[i]) && i + 1 < args.length) {
                filePath = args[i + 1];
                break;
            }
        }

        if (filePath != null) {
            try {
                File file = new File(filePath);
                if (!file.exists()) {
                    System.err.println("Error: File not found: " + filePath);
                    System.exit(1);
                }
                List<String> lines = Files.readAllLines(file.toPath());
                for (String line : lines) {
                    processLine(line);
                }
            } catch (Exception e) {
                System.err.println("Error reading file: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Interactive / stdin mode
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    processLine(line);
                }
            } catch (Exception e) {
                System.err.println("Error reading from stdin: " + e.getMessage());
            }
        }
    }

    private static void processLine(String line) {
        String trimmed = line.trim();
        if (trimmed.isEmpty() || trimmed.startsWith("#")) {
            return;
        }
        try {
            AlgebraicTree tree = new AlgebraicTree(trimmed);
            tree.construct();
            StructureMatrix<Double> result = tree.eval();
            if (result != null) {
                if (result.getDim() == 0) {
                    System.out.println(result.getElem());
                } else {
                    System.out.println(result.toString());
                }
            } else {
                System.out.println("OK");
            }
        } catch (Exception e) {
            System.err.println("Error evaluating expression '" + trimmed + "': " + e.getMessage());
        }
    }
}
