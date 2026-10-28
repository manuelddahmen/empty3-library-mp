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

package one.empty3.library1.tree.editor;

import one.empty3.library1.tree.ListInstructions;
import one.empty3.library.StructureMatrix;

import java.util.List;
import java.util.Map;

public class ScriptExecutor {

    public static String execute(String scriptContent) {
        if (scriptContent == null || scriptContent.trim().isEmpty()) {
            return "Empty script.";
        }
        StringBuilder output = new StringBuilder();
        try {
            ListInstructions li = new ListInstructions();
            li.addInstructions(scriptContent);
            List<String> resultLogs = li.runInstructions();
            output.append("Execution completed successfully.\n");
            
            Map<String, Double> params = li.getCurrentParamsValues();
            if (params != null && !params.isEmpty()) {
                output.append("\nVariables / Parameters:\n");
                for (Map.Entry<String, Double> entry : params.entrySet()) {
                    output.append("  ").append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
                }
            }

            Map<String, StructureMatrix<Double>> vecComputed = li.getCurrentParamsValuesVecComputed();
            if (vecComputed != null && !vecComputed.isEmpty()) {
                output.append("\nVector Computed Results:\n");
                for (Map.Entry<String, StructureMatrix<Double>> entry : vecComputed.entrySet()) {
                    output.append("  ").append(entry.getKey()).append(" = ").append(entry.getValue()).append("\n");
                }
            }
        } catch (Exception e) {
            output.append("Error during script execution: ").append(e.getMessage()).append("\n");
        }
        return output.toString();
    }
}