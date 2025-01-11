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

package one.empty3.library.core.nurbs;

import one.empty3.library.Point3D;
import one.empty3.library.StructureMatrix;
import one.empty3.library1.tree.AlgebraicFormulaSyntaxException;
import one.empty3.library1.tree.AlgebraicTree;
import one.empty3.library1.tree.TreeNodeEvalException;

import java.util.HashMap;

public class FunctionSurface extends ParametricSurface {
    private StructureMatrix<String> x = new StructureMatrix<>(0, String.class);
    private StructureMatrix<String> y = new StructureMatrix<>(0, String.class);
    private StructureMatrix<String> z = new StructureMatrix<>(0, String.class);
    private AlgebraicTree treeX;
    private AlgebraicTree treeY;
    private AlgebraicTree treeZ;
    final HashMap<String, Double> hashMap = new HashMap<>(2);
    private boolean drawable;

    public FunctionSurface() throws AlgebraicFormulaSyntaxException {
        super();
        x.setElem("0.0");
        y.setElem("0.0");
        z.setElem("0.0");
        recomputeTrees();
    }

    FunctionSurface(String xEqFuv, String yEqFuv, String zEqFuv) throws AlgebraicFormulaSyntaxException {
        this();
        hashMap.put("u", 0d);
        hashMap.put("v", 0d);

        this.x.setElem(xEqFuv);
        this.y.setElem(yEqFuv);
        this.z.setElem(zEqFuv);


        treeX = new AlgebraicTree(xEqFuv);
        treeX.getParametersValues().putAll(hashMap);
        treeX.construct();
        treeY = new AlgebraicTree(yEqFuv);
        treeY.getParametersValues().putAll(hashMap);
        treeY.construct();
        treeZ = new AlgebraicTree(zEqFuv);
        treeZ.getParametersValues().putAll(hashMap);
        treeZ.construct();
        setDrawable(true);
    }

    public String getX() {
        return x.getElem();
    }

    public void setX(String x) {
        this.x.setElem(x);
        recomputeTrees();
    }

    private void recomputeTrees() {
        try {
            treeX = new AlgebraicTree(x.getElem());
            treeX.setParametersValues(hashMap);
            treeX.construct();
            treeY = new AlgebraicTree(y.getElem());
            treeY.setParametersValues(hashMap);
            treeY.construct();
            treeZ = new AlgebraicTree(z.getElem());
            treeZ.setParametersValues(hashMap);
            treeZ.construct();
            setDrawable(true);
            declareProperties();
        } catch (AlgebraicFormulaSyntaxException e) {
            setDrawable(false);
            e.printStackTrace();
        }
    }

    public String getY() {
        return y.getElem();
    }

    public void setY(String y) {
        this.y.setElem(y);
        recomputeTrees();
    }

    public String getZ() {
        return z.getElem();
    }

    public void setZ(String z) {
        this.z.setElem(z);
        recomputeTrees();
    }

    public Point3D calculerPoint3D(double u, double v) {
        try {
            hashMap.put("u", u);
            hashMap.put("v", v);
            double evalX = treeX.eval().getElem();
            double evalY = treeY.eval().getElem();
            double evalZ = treeZ.eval().getElem();
            return new Point3D(evalX, evalY, evalZ);
        } catch (TreeNodeEvalException | AlgebraicFormulaSyntaxException | NullPointerException exceptione) {
            exceptione.printStackTrace();
        }

        return null;
    }

    public void setDrawable(boolean drawable) {
        this.drawable = drawable;
    }

    @Override
    public void declareProperties() {
        super.declareProperties();
        getDeclaredDataStructure().put("x/function (x = f(u,v))", x);
        getDeclaredDataStructure().put("y/function (y = f(u,v))", y);
        getDeclaredDataStructure().put("z/function (z = f(u,v))", z);
    }

}