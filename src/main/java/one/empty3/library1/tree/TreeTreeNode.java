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
    private List<AlgebraicTree> argTrees = new ArrayList<>();
    private String functionName;
    private Method method = null;
    private boolean mathType = true;

    public TreeTreeNode(AlgebraicTree tree, TreeNode t, Object[] objects, TreeNodeType type) {
        super(tree, t, objects, type);
        try {
            String fParamString = "";
            if (objects != null && objects.length > 0 && objects[0] instanceof String) {
                fParamString = (String) objects[0];
            }
            List<String> argStrs = splitArguments(fParamString);
            for (String argStr : argStrs) {
                if (!argStr.isEmpty()) {
                    AlgebraicTree argTree = tree.createWithSameParameters(argStr);
                    argTree.construct();
                    argTrees.add(argTree);
                }
            }
            if (argTrees.isEmpty() && tree != null) {
                tree.construct();
                argTrees.add(tree);
            }

            if (objects.length > 2 && objects[2] instanceof String && !((String) objects[2]).isEmpty()) {
                String call = (String) objects[2];
                if ((objects.length >= 4 && objects[3].equals(false)) || call.length() > 1) {
                    if ((objects.length < 4) || (objects[3].equals(true))) {
                        int argCount = argTrees.size();
                        if (argCount == 2) {
                            try {
                                method = Math.class.getMethod(call, double.class, double.class);
                            } catch (NoSuchMethodException e) {
                                method = Math.class.getMethod(call, double.class);
                            }
                        } else {
                            method = Math.class.getMethod(call, double.class);
                        }
                    } else {
                        mathType = false;
                        functionName = call;
                    }
                }
            }
        } catch (AlgebraicFormulaSyntaxException | NoSuchMethodException e) {
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

        List<Double> argValues = new ArrayList<>();
        boolean isVectorArg = false;
        List<List<Double>> vectorArgs = new ArrayList<>();
        int vectorSize = -1;

        for (AlgebraicTree argTree : argTrees) {
            StructureMatrix<Double> argEval = argTree.eval();
            if (argEval.getDim() == 0) {
                argValues.add(argEval.getElem());
                vectorArgs.add(List.of(argEval.getElem()));
            } else if (argEval.getDim() == 1) {
                isVectorArg = true;
                List<Double> d1 = argEval.getData1d();
                vectorArgs.add(d1);
                if (vectorSize == -1 || d1.size() > vectorSize) {
                    vectorSize = d1.size();
                }
                argValues.add(d1.isEmpty() ? 0.0 : d1.get(0));
            } else {
                argValues.add(0.0);
                vectorArgs.add(List.of(0.0));
            }
        }

        if (!isVectorArg) {
            double r = 0.0;
            if (method != null) {
                try {
                    Object[] argsArray = argValues.toArray();
                    if (method.getParameterCount() == 2 && argsArray.length >= 2) {
                        r = (Double) method.invoke(Math.class, argsArray[0], argsArray[1]);
                    } else if (method.getParameterCount() == 1 && argsArray.length >= 1) {
                        r = (Double) method.invoke(Math.class, argsArray[0]);
                    } else {
                        r = (Double) method.invoke(Math.class, argsArray);
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            } else if (functionName != null && mainTree != null && mainTree.getUserFunctions() != null && mainTree.getUserFunctions().containsKey(functionName)) {
                AlgebraicTree.UserDefinedFunction udf = mainTree.getUserFunctions().get(functionName);
                r = udf.evaluate(argValues, mainTree.getParametersValues(), mainTree.getUserFunctions());
            } else if (!argValues.isEmpty()) {
                r = argValues.get(0);
            }
            return res.setElem(r);
        } else {
            res = new StructureMatrix<>(1, Double.class);
            if (method != null) {
                try {
                    for (int i = 0; i < vectorSize; i++) {
                        Object[] invokeArgs = new Object[argTrees.size()];
                        boolean valid = true;
                        for (int j = 0; j < argTrees.size(); j++) {
                            List<Double> vArg = vectorArgs.get(j);
                            if (i < vArg.size() && vArg.get(i) != null) {
                                invokeArgs[j] = vArg.get(i);
                            } else if (!vArg.isEmpty()) {
                                invokeArgs[j] = vArg.get(0);
                            } else {
                                valid = false;
                                break;
                            }
                        }
                        if (valid) {
                            double r;
                            if (method.getParameterCount() == 2) {
                                r = (Double) method.invoke(Math.class, invokeArgs[0], invokeArgs[1]);
                            } else {
                                r = (Double) method.invoke(Math.class, invokeArgs[0]);
                            }
                            res.setElem(r, i);
                        }
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
                return res;
            } else if (functionName != null && mainTree != null && mainTree.getUserFunctions() != null && mainTree.getUserFunctions().containsKey(functionName)) {
                AlgebraicTree.UserDefinedFunction udf = mainTree.getUserFunctions().get(functionName);
                double r = udf.evaluate(argValues, mainTree.getParametersValues(), mainTree.getUserFunctions());
                res.setElem(r, 0);
                return res;
            }
            for (int i = 0; i < vectorSize; i++) {
                res.setElem(argValues.get(0), i);
            }
            return res;
        }
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
