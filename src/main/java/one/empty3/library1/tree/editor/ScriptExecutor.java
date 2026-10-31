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

            // Itérer sur les logs de sortie de l'exécuteur
            List<String> out = li.runInstructions();
            if (out != null && !out.isEmpty()) {
                output.append("--- Execution Logs ---\n");
                for (String s : out) {
                    output.append(s).append("\n");
                }
                output.append("----------------------\n");
            }

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