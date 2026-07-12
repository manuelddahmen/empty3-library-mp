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

package one.empty3.library.tests;

import one.empty3.library.Point3D;
import one.empty3.library1.tree.AlgebraicFormulaSyntaxException;
import one.empty3.library1.tree.AlgebraicTree;
import one.empty3.library1.tree.TreeNodeEvalException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class AngleMovment {
    AlgebraicTree[] tree;
    List<AlgebraicTree> fxs = new ArrayList();
    HashMap<String, Double> vars;

    public AngleMovment(int size) {
        tree = new AlgebraicTree[size];
        vars = new HashMap<>(size);
    }

    public void var(String var, double val) {
        vars.put(var, val);
    }

    public void setFormula(int index, String chars) {

        tree[index] = new AlgebraicTree(chars, vars);
        tree[index].setParametersValues(vars);
        try {
            tree[index].construct();
        } catch (AlgebraicFormulaSyntaxException e) {
            e.printStackTrace();
        }
    }

    public double calcculerAngle(int index) {
        try {
            tree[index].setParametersValues(vars);
            tree[index].construct();
            return (double) (tree[index].eval().getElem());
        } catch (TreeNodeEvalException | AlgebraicFormulaSyntaxException e) {
            e.printStackTrace();
        }
        return Double.NaN;
    }

    public void addFx(String formulaN) {
        fxs.add(new AlgebraicTree(formulaN));

    }

    public Point3D getPoint3D() {
        double[] values = new double[tree.length];
        for (int i = 0; i < tree.length; i++) {
            try {
                values[i] = tree[i].eval().getElem();
            } catch (TreeNodeEvalException | AlgebraicFormulaSyntaxException ex) {
                ex.printStackTrace();
            }


        }
        return new Point3D(values);
    }
}
