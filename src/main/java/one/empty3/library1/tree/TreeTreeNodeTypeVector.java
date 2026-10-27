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
 *  * Created by Manuel D Dahmen -2026
 *
 *
 */

package one.empty3.library1.tree;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import one.empty3.library.StructureMatrix;
import one.empty3.library1.tree.TreeNodeType;


public class TreeTreeNodeTypeVector extends TreeNodeType {
    private final String fName;
    private final String paramsString;

    public TreeTreeNodeTypeVector(String fParamString, Map<String, Double> parametersValues, String fName) {
        super();
        this.paramsString = fParamString;
        this.fName = fName;
    }


    @Override
    public StructureMatrix<Double> eval() {
        List<String> args =
                TreeTreeNode.splitArguments(paramsString);

        List<Double> values = new ArrayList<>();

        for (String arg : args) {
            try {
                AlgebraicTree tree = new AlgebraicTree(arg);
                tree.construct();

                StructureMatrix<Double> value = tree.eval();

                if (value.getDim() == 0) {
                    values.add(value.getElem());
                } else if (value.getDim() == 1) {
                    values.addAll(value.getData1d());
                }

            } catch (Exception e) {
                throw new IllegalArgumentException(
                        "Cannot evaluate argument: " + arg, e
                );
            }
        }

        double result = Functions.search(
                fName,
                values.toArray(new Double[0])
        );

        StructureMatrix<Double> matrix =
                new StructureMatrix<>(1, Double.class);

        matrix.setElem(result, 0);

        return matrix;
    }

    public String getfName() {
        return fName;
    }

    public String getParamsString() {
        return paramsString;
    }
}
