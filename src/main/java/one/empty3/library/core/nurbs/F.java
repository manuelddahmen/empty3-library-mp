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

package one.empty3.library.core.nurbs;

import one.empty3.library1.tree.*;

public class F {

    /*__
     * Created by md 2020 4 8 */
    private int dima, dimb;
    private AlgebraicTree treeF;

    /*__
     * input x(n:int) : formula
     * separator ; y(n) = formula(x(n:int))
     */
    public void result(Double[] input, Double[] output) {

    }

    private String fs = "1:1:1.0";
    private String formula = "1.0";

    /*__
     *
     * @param fs formula:dima(int):dimb(int)
     * // TODO algebre tree n dim
     *
     */
    public F(String fs) {

        String[] splits = fs.split(":");

        if (splits.length == 3) {
            this.fs = fs;
            this.formula = splits[0];
            dima = Integer.parseInt(splits[1]);
            dimb = Integer.parseInt(splits[2]);
        }
        treeF = new AlgebraicTree(formula);

    }


}
