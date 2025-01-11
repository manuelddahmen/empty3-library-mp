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

package one.empty3.library1.shader;

import one.empty3.library1.tree.AlgebraicFormulaSyntaxException;
import one.empty3.library1.tree.AlgebraicTree;
import one.empty3.library1.tree.TreeNodeEvalException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class VecAlTree extends Vec {
    protected String formula;
    AlgebraicTree tree;
    private boolean invalidTree = true;

    public VecAlTree(String formula, int dim) {
        super(dim);
        String[] formulas = formula.split(",");


        this.formula = formula;

        try {
            tree = new AlgebraicTree(formula)
            ;
            tree.construct();
            invalidTree = false;
        } catch (AlgebraicFormulaSyntaxException t) {
            Logger.getAnonymousLogger().log(Level.INFO, "error vecaltreecondtruct\n" + tree);
            invalidTree = true;
        }

    }

    public void setParameter(String p, Double d) {
        tree.setParameter(p, d);
    }

    public Double[] getValue() {
        try {
            return new Double[]{
                    tree.eval().getElem()};
        } catch (TreeNodeEvalException ex) {
            ex.printStackTrace();
            return new Double[]{0.0};
        } catch (AlgebraicFormulaSyntaxException ex) {
            ex.printStackTrace();
            return new Double[]{0.0};
        }


    }
}
