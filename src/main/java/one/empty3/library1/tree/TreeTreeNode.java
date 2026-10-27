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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import one.empty3.library.StructureMatrix;
import org.jcodec.api.NotImplementedException;

public class TreeTreeNode extends TreeNode {
    private Field field = null;
    private List<AlgebraicTree> argTrees = new ArrayList<>();
    private String functionName;
    private Method method = null;
    private boolean mathType = true;

    public TreeTreeNode(AlgebraicTree tree, TreeNode t, Object[] objects, TreeNodeType type) {
        super(tree, t, objects, type);
        try {
            String fParamString = "";
            String call = "";
            boolean isMathMethod = false;
            if (objects != null) {
                if (objects.length >= 4 && objects[3] instanceof Boolean) {
                    fParamString = objects[0] instanceof String ? (String) objects[0] : "";
                    call = objects[2] instanceof String ? (String) objects[2] : "";
                    isMathMethod = (Boolean) objects[3];
                } else if (objects.length >= 3) {
                    call = objects[0] instanceof String ? (String) objects[0] : "";
                    fParamString = objects[2] instanceof String ? (String) objects[2] : "";
                } else if (objects.length > 0 && objects[0] instanceof String) {
                    fParamString = (String) objects[0];
                }
            }

            if (!call.isEmpty() && isMathMethod) {
                Method m2 = null;
                try {
                    m2 = Math.class.getMethod(call, double.class, double.class);
                } catch (NoSuchMethodException e) {
                    // Non 2-arg
                }
                if (m2 != null) {
                    method = m2;
                } else {
                    try {
                        method = Math.class.getMethod(call, double.class);
                    } catch (NoSuchMethodException e1) {
                        try {
                            method = Math.class.getMethod(call);
                        } catch (NoSuchMethodException e0) {
                            // ignore
                        }
                    }
                }
            } else if (!call.isEmpty()) {
                mathType = false;
                functionName = call;
            }

            int expectedArgs = 1;
            if (method != null) {
                expectedArgs = method.getParameterCount();
            } else if (functionName != null) {
                expectedArgs = -1; // dynamique selon la présence de virgules
            }

            List<String> argStrs = new ArrayList<>();
            if (expectedArgs == 2 || (expectedArgs == -1 && fParamString.contains(","))) {
                argStrs = splitArguments(fParamString);
            } else {
                argStrs.add(fParamString);
            }

            for (String argStr : argStrs) {
                if (!argStr.isEmpty()) {
                    AlgebraicTree argTree = (tree != null) ? tree.createWithSameParameters(argStr) : new AlgebraicTree(argStr);
                    try {
                        String tf = argTree.getFormula();
                        if (tf == null || !tf.trim().equals(call)) {
                            argTree.construct();
                            argTrees.add(argTree);
                        }
                    } catch (Exception e) {
                        // ignore
                    }
                }
            }
            if (argTrees.isEmpty() && tree != null) {
                try {
                    String treeFormula = tree.getFormula();
                    if (treeFormula == null || !treeFormula.trim().equals(call)) {
                        tree.construct();
                        argTrees.add(tree);
                    }
                } catch (Exception e) {
                    // ignore
                }
            }
        } catch (AlgebraicFormulaSyntaxException e) {
            //e.printStackTrace();
        }
    }

    public static List<String> splitArguments(String argsStr) {
        List<String> args = new ArrayList<>();
        if (argsStr == null || argsStr.trim().isEmpty()) {
            return args;
        }
        int depth = 0;
        int start = 0;
        for (int i = 0; i < argsStr.length(); i++) {
            char c = argsStr.charAt(i);
            if (c == '(' || c == '[' || c == '{') {
                depth++;
            } else if (c == ')' || c == ']' || c == '}') {
                depth--;
            } else if (c == ',' && depth == 0) {
                args.add(argsStr.substring(start, i).trim());
                start = i + 1;
            }
        }
        args.add(argsStr.substring(start).trim());
        return args;
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
        AlgebraicTree mainTree = getMainAlgebraicTree();

        if (functionName != null && mainTree != null && mainTree.getUserFunctions() != null && mainTree.getUserFunctions().containsKey(functionName)) {
            AlgebraicTree.UserDefinedFunction udf = mainTree.getUserFunctions().get(functionName);
            List<Double> argList = new ArrayList<>();
            for (AlgebraicTree argTree : argTrees) {
                StructureMatrix<Double> evalArg = argTree.eval();
                if (evalArg.getDim() == 0) {
                    argList.add(evalArg.getElem());
                } else if (evalArg.getDim() == 1 && !evalArg.getData1d().isEmpty()) {
                    argList.add(evalArg.getData1d().get(0));
                } else {
                    argList.add(0.0);
                }
            }
            double r = udf.evaluate(argList, mainTree.getParametersValues(), mainTree.getUserFunctions());
            return res.setElem(r);
        }

        if (argTrees.size() == 1) {
            StructureMatrix<Double> eval = argTrees.get(0).eval();
            if (eval.getDim() == 0) {
                double r = eval.getElem();
                if (method != null) {
                    try {
                        r = (Double) method.invoke(Math.class, r);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                return res.setElem(r);
            } else if (eval.getDim() == 1) {
                int size = eval.getData1d().size();
                res = new StructureMatrix<>(1, Double.class);
                try {
                    for (int i = 0; i < size; i++) {
                        Double arg = eval.getData1d().get(i);
                        if (arg != null) {
                            double r = method != null ? (Double) method.invoke(Math.class, arg) : arg;
                            res.setElem(r, i);
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return res;
            }
        } else if (argTrees.size() == 2) {
            StructureMatrix<Double> eval1 = argTrees.get(0).eval();
            StructureMatrix<Double> eval2 = argTrees.get(1).eval();

            if (eval1.getDim() == 0 && eval2.getDim() == 0) {
                double arg1 = eval1.getElem();
                double arg2 = eval2.getElem();
                double r = 0.0;
                if (method != null) {
                    try {
                        if (method.getParameterCount() == 2) {
                            r = (Double) method.invoke(Math.class, arg1, arg2);
                        } else {
                            r = (Double) method.invoke(Math.class, arg1);
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                return res.setElem(r);
            } else if (argTrees.size() == 0) {
                double r = 0.0;
                if (method != null) {
                    try {
                        r = (Double) method.invoke(Math.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (field != null) {
                    try {
                        r = field.getDouble(null);
                    } catch (Exception e) {
                        // ignore
                    }
                }
                return res.setElem(r);
            } else {
                int size1 = eval1.getDim() == 1 ? eval1.getData1d().size() : 1;
                int size2 = eval2.getDim() == 1 ? eval2.getData1d().size() : 1;
                int size = Math.max(size1, size2);
                res = new StructureMatrix<>(1, Double.class);
                try {
                    for (int i = 0; i < size; i++) {
                        double arg1 = eval1.getDim() == 1 ? (i < size1 ? eval1.getData1d().get(i) : eval1.getData1d().get(0)) : eval1.getElem();
                        double arg2 = eval2.getDim() == 1 ? (i < size2 ? eval2.getData1d().get(i) : eval2.getData1d().get(0)) : eval2.getElem();
                        double r = 0.0;
                        if (method != null) {
                            if (method.getParameterCount() == 2) {
                                r = (Double) method.invoke(Math.class, arg1, arg2);
                            } else {
                                r = (Double) method.invoke(Math.class, arg1);
                            }
                        }
                        res.setElem(r, i);
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return res;
            }
        } else if (!argTrees.isEmpty()) {
            List<Double> argList = new ArrayList<>();
            for (AlgebraicTree at : argTrees) {
                try {
                    StructureMatrix<Double> evalArg = at.eval();
                    if (evalArg.getDim() == 0) argList.add(evalArg.getElem());
                    else if (evalArg.getDim() == 1 && !evalArg.getData1d().isEmpty()) argList.add(evalArg.getData1d().get(0));
                    else argList.add(0.0);
                } catch (Exception e) {
                    argList.add(0.0);
                }
            }
            double r = 0.0;
            if (method != null) {
                try {
                    r = (Double) method.invoke(Math.class, argList.toArray());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (!argList.isEmpty()) {
                r = argList.get(0);
            }
            return res.setElem(r);
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
        StringBuilder argsStr = new StringBuilder();
        for (int i = 0; i < argTrees.size(); i++) {
            if (i > 0) argsStr.append(", ");
            if (argTrees.get(i).getRoot() != null) {
                argsStr.append(argTrees.get(i).getRoot().toFormula());
            } else {
                argsStr.append(argTrees.get(i).getFormula());
            }
        }
        if (argsStr.length() == 0 && objects != null && objects.length > 0 && objects[0] instanceof String) {
            argsStr.append((String) objects[0]);
        }
        return func + "(" + argsStr.toString() + ")";
    }

    @Override
    public String differentiate(String variableName) {
        String func = method != null ? method.getName() : (getFunctionName() != null ? getFunctionName() : (objects != null && objects.length > 2 && objects[2] instanceof String ? (String) objects[2] : ""));
        String arg = "";
        String argDiff = "0.0";
        if (!argTrees.isEmpty() && argTrees.get(0).getRoot() != null) {
            arg = argTrees.get(0).getRoot().toFormula();
            argDiff = argTrees.get(0).getRoot().differentiate(variableName);
        } else if (!argTrees.isEmpty() && argTrees.get(0) != null) {
            arg = argTrees.get(0).getFormula();
            AlgebraicTree dt = argTrees.get(0).derivative(variableName);
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