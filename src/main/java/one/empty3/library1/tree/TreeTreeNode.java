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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import one.empty3.library.StructureMatrix;
import org.jcodec.api.NotImplementedException;

/**
 * J'ai un problème dans ALgebraicTree soit au niveau de la construction de l'arbre (construct() ) soit au niveau de l'eval() .: les fonctions à plusieurs paramètres ne fonctionnent pas. Il semble que le construct ou l'eval quelque part, ne prend qu'un paramètre alors qu'il devrait prendre tous les paramètres entre parenthèses.
 */


public class TreeTreeNode extends TreeNode {
    private String functionName;
    private Method method = null;
    private boolean mathType = true;

    public TreeTreeNode(AlgebraicTree tree, TreeNode t, Object[] objects, TreeNodeType type) {
        super(tree, t, objects, type);
        //tree = new AlgebraicTree((String) objects[0], (Map<String, Double>) objects[1]);
        try {
            tree.construct();
            if (objects[2] instanceof String && !((String) objects[2]).isEmpty()) {
                String call = (String) objects[2];
                if ((objects.length >= 4 && objects[3].equals(false)) || call.length() > 1) {
                    if ((objects.length < 4) || (objects[3].equals(true))) {
                        method = Math.class.getMethod(call, double.class);
                    } else {
                        mathType = false;
                        functionName = call;
                        //throw new NotImplementedException("Custom functions not implemented yet");
                    }
                }
            }
        } catch (AlgebraicFormulaSyntaxException | NoSuchMethodException e) {
            //e.printStackTrace();
        }
    }

    private AlgebraicTree getMainAlgebraicTree() {
        TreeNode curr = this;
        while (curr.parent != null) {
            curr = curr.parent;
        }
        return curr.tree != null ? curr.tree : this.tree;
    }

    @Override
    public StructureMatrix<Double> eval() throws TreeNodeEvalException, AlgebraicFormulaSyntaxException {
        StructureMatrix<Double> res = new StructureMatrix<>(0, Double.class);
        double r;
        StructureMatrix<Double> eval = tree.eval();
        AlgebraicTree mainTree = getMainAlgebraicTree();
        if (eval.getDim() == 0) {
            r = tree.eval().getElem();

            if (method != null) {
                try {
                    r = (Double) method.invoke(Math.class, r);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else if (functionName != null && mainTree != null && mainTree.getUserFunctions() != null && mainTree.getUserFunctions().containsKey(functionName)) {
                AlgebraicTree.UserDefinedFunction udf = mainTree.getUserFunctions().get(functionName);
                List<Double> argList = new ArrayList<>();
                argList.add(r);
                r = udf.evaluate(argList, mainTree.getParametersValues(), mainTree.getUserFunctions());
                return res.setElem(r);
            }
            return res.setElem(r);

        } else if (eval.getDim() == 1) {

            int size = eval.getData1d().size();
            Double[] args = new Double[size];
            for (int i = 0; i < size; i++) {
                args[i] = eval.getData1d().get(i);
            }
            res = new StructureMatrix<>(1, Double.class);
            if (method != null) {
                try {
                    int k = 0;
                    for (int i = 0; i < eval.getData1d().size(); i++) {
                        if (args[i] != null) {
                            r = (Double) method.invoke(Math.class, args[i]);
                            res.setElem(r, i);
                            k++;
                        }
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                return res;
            } else if (functionName != null && mainTree != null && mainTree.getUserFunctions() != null && mainTree.getUserFunctions().containsKey(functionName)) {
                AlgebraicTree.UserDefinedFunction udf = mainTree.getUserFunctions().get(functionName);
                List<Double> argList = new ArrayList<>();
                for (Double arg : args) {
                    if (arg != null) argList.add(arg);
                }
                r = udf.evaluate(argList, mainTree.getParametersValues(), mainTree.getUserFunctions());
                res.setElem(r, 0);
            }
            for (int i = 0; i < eval.getData1d().size(); i++) {
                r = eval.getElem(i);
                res.setElem(r, i);
            }
        }
        return res;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean isMathType() {
        return mathType;
    }

    public void setMathType(boolean mathType) {
        this.mathType = mathType;
    }

    @Override
    public String toFormula() {
        String func = method != null ? method.getName() : (getFunctionName() != null ? getFunctionName() : (objects != null && objects.length > 2 && objects[2] instanceof String ? (String) objects[2] : ""));
        String arg = "";
        if (tree != null && tree.getRoot() != null) {
            arg = tree.getRoot().toFormula();
        } else if (objects != null && objects.length > 0 && objects[0] instanceof String) {
            arg = (String) objects[0];
        }
        return func + "(" + arg + ")";
    }

    @Override
    public String differentiate(String variableName) {
        String func = method != null ? method.getName() : (getFunctionName() != null ? getFunctionName() : (objects != null && objects.length > 2 && objects[2] instanceof String ? (String) objects[2] : ""));
        String arg = "";
        String argDiff = "0.0";
        if (tree != null && tree.getRoot() != null) {
            arg = tree.getRoot().toFormula();
            argDiff = tree.getRoot().differentiate(variableName);
        } else if (tree != null) {
            arg = tree.getFormula();
            AlgebraicTree dt = tree.derivative(variableName);
            argDiff = dt != null ? dt.getFormula() : "0.0";
        } else if (objects != null && objects.length > 0 && objects[0] instanceof String) {
            arg = (String) objects[0];
            try {
                AlgebraicTree at = new AlgebraicTree(arg);
                at.construct();
                argDiff = at.getRoot().differentiate(variableName);
            } catch (Exception e) {
                argDiff = "0.0";
            }
        }

        if (FormulaSimplifier.isZero(argDiff)) {
            return "0.0";
        }

        String fPrime;
        switch (func.toLowerCase()) {
            case "sin" -> fPrime = "cos(" + arg + ")";
            case "cos" -> fPrime = "(-sin(" + arg + "))";
            case "tan" -> fPrime = "(1.0 + (tan(" + arg + ") ^ 2.0))";
            case "asin" -> fPrime = "(1.0 / sqrt(1.0 - (" + arg + " ^ 2.0)))";
            case "acos" -> fPrime = "(-1.0 / sqrt(1.0 - (" + arg + " ^ 2.0)))";
            case "atan" -> fPrime = "(1.0 / (1.0 + (" + arg + " ^ 2.0)))";
            case "exp" -> fPrime = "exp(" + arg + ")";
            case "log", "ln" -> fPrime = "(1.0 / (" + arg + "))";
            case "sqrt" -> fPrime = "(1.0 / (2.0 * sqrt(" + arg + ")))";
            case "sinh" -> fPrime = "cosh(" + arg + ")";
            case "cosh" -> fPrime = "sinh(" + arg + ")";
            case "tanh" -> fPrime = "(1.0 - (tanh(" + arg + ") ^ 2.0))";
            case "abs" -> fPrime = "((" + arg + ") / abs(" + arg + "))";
            case "cbrt" -> fPrime = "(1.0 / (3.0 * (cbrt(" + arg + ") ^ 2.0)))";
            default -> fPrime = null;
        }

        if (fPrime == null) {
            return "0.0";
        }

        return FormulaSimplifier.simplifyProduct(fPrime, argDiff);
    }
}
