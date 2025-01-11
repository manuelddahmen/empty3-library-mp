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

package one.empty3.growth;

import one.empty3.library1.tree.AlgebraicTree;
import one.empty3.library1.tree.AlgebraicTree;
import one.empty3.library1.tree.AlgebraicFormulaSyntaxException;
import one.empty3.library1.tree.AlgebraicTree;
import one.empty3.library1.tree.TreeNodeEvalException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FunctionalParameter extends Parameter {
    private String formula;

    public FunctionalParameter(String name, Double parameter, String formula) {
        super(name, parameter);
        this.formula = formula;
    }

    public String getFormula() {
        return formula;
    }


    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Double eval(int t, int recurse) {
        recurse--;
        if (t == 0 || recurse < 0)
            return getValue();
        else {
            Map<String, Double> parametersValues = new HashMap<>();
            parametersValues.put("t", (double) t);
            final Parameter that = this;

            final HashMap<String, Parameter> paramsT0 = new HashMap<>();

            paramsT0.putAll(lSystem.getParams(t - 1));

            if (paramsT0.size() == 0) {
                lSystem.getInitialParameters().forEach(new BiConsumer<Symbol, Parameter>() {
                    @Override
                    public void accept(Symbol symbol, Parameter parameter) {
                        paramsT0.put("" + symbol.getValue(), parameter);
                    }
                });
            }
            paramsT0.forEach((s, parameter) -> {
                if (/*parameter != that && */ parameter.getName() != that.getName()) {

                    parametersValues.put(s, parameter.eval(t, 0));

                }
            });
            AlgebraicTree tree = null;
            try {
                tree = new AlgebraicTree(getFormula(), parametersValues);
                tree.construct();
            } catch (AlgebraicFormulaSyntaxException e) {
                e.printStackTrace();
            }
            try {
                Double d = (Double) (tree.eval().getElem());
                setValue(d);
                Logger.getAnonymousLogger().log(Level.INFO, "" + d);
                return d;
            } catch (TreeNodeEvalException | AlgebraicFormulaSyntaxException e) {
                e.printStackTrace();
            }

        }
        return 0.;
    }

    @Override
    public String toString() {
        String s = "";
        return "Parameter: " + getName() + " Value: " + getValue() + " Formula: " + getFormula();
    }
}
