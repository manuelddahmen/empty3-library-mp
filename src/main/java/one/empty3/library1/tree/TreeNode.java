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

import one.empty3.library.Point2D;
import one.empty3.library.Point3D;
import one.empty3.library.StructureMatrix;


import java.util.ArrayList;

/*__
 * Created by Manuel Dahmen on 15-12-16.
 */
public class TreeNode {
    protected final AlgebraicTree tree;
    //protected AlgebraicTree algebraicTree;
    protected Object[] objects;
    protected TreeNodeType type = null;
    protected ArrayList<TreeNode> children = new ArrayList<TreeNode>();
    protected TreeNode parent;
    protected String expressionString;
    private TreeNodeValue value;

    private enum TreeNodeClassType {
        Double, Integer, Boolean, String, Char
    }

    private TreeNodeClassType treeNodeClassType = TreeNodeClassType.Double;

    public TreeNode(AlgebraicTree tree, String expStr) {
        this.tree = tree;
        this.parent = null;
        if (expStr.trim().isEmpty()) expressionString = "0.0";
        this.expressionString = expStr;
    }

    /***
     * Base constructor for all TreeNodes
     * @param src parent (!=null)
     * @param objects [0] = expressionString
     * @param clazz type associated to this TreeNode
     */
    public TreeNode(AlgebraicTree tree, TreeNode src, Object[] objects, TreeNodeType clazz) {
        this.tree = tree;
        this.parent = src;
        //this.algebraicTree = src.algebraicTree;
        this.objects = objects;
        clazz.instantiate(objects);
        this.type = clazz;
        expressionString = (String) objects[0];
    }

    public Object getValue() {
        return value;
    }

    public void setValue(TreeNodeValue value) {
        this.value = value;
    }


    public StructureMatrix<Double> eval() throws TreeNodeEvalException, AlgebraicFormulaSyntaxException {
        /*if(this instanceof TreeNode) {
            return Double.parseDouble(((TreeNode) this).expressionString);
        }*/
        TreeNodeType cType = (getChildren().size() == 0) ? type : getChildren().get(0).type;

        StructureMatrix<Double> evalRes = null;
        if (cType instanceof VectorTreeNodeType) {
            evalRes = new StructureMatrix<>(1, Double.class);
        } else {
            evalRes = new StructureMatrix<>(0, Double.class);
            evalRes.setElem(0.0);
        }
        if (cType instanceof IdentTreeNodeType) {
            return getChildren().get(0).eval();
        } else if (cType instanceof EquationTreeNodeType) {
            //Logger.getAnonymousLogger().log(Level.INFO, cType);
            //Logger.getAnonymousLogger().log(Level.INFO, getChildren().get(0));
            StructureMatrix<Double> child0Eval = getChildren().get(0).getChildren().get(0).eval();
            if (child0Eval != null) {
                switch (child0Eval.getDim()) {
                    case 0:
                        evalRes = new StructureMatrix<>(0, Double.class);
                        Double val0 = child0Eval.getElem();
                        if (val0 != null) {
                            evalRes.setElem(val0);
                        }
                        break;
                    case 1:
                        int sum = 0;
                        evalRes = new StructureMatrix<>(1, Double.class);
                        for (int j = 0; j < getChildren().get(0).getChildren().size(); j++) {
                            StructureMatrix<Double> eval = getChildren().get(0).getChildren().get(j).eval();
                            if (eval != null && eval.data1d != null) {
                                for (int i = 0; i < eval.data1d.size(); i++) {
                                    evalRes.setElem(eval.getElem(i), sum);
                                    sum++;
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
            StructureMatrix<Double> child1Eval = getChildren().get(0).getChildren().get(1).eval();
            if (child1Eval != null) {
                switch (child1Eval.getDim()) {
                    case 0:
                        Double val1 = child1Eval.getElem();
                        if (val1 != null) {
                            evalRes.setElem(val1);
                        }
                        break;
                    case 1:
                        evalRes = new StructureMatrix<>(1, Double.class);
                        if (child1Eval.data1d != null) {
                            int size = child1Eval.data1d.size();
                            for (int i = 0; i < size; i++)
                                evalRes.setElem(child1Eval.getElem(i), i);
                        }
                        break;
                    default:
                        break;
                }
            }
            ArrayList<TreeNode> childrenVars = getChildren().get(0).getChildren().get(0).getChildren();
            ArrayList<TreeNode> childrenValues = getChildren().get(0).getChildren().get(1).getChildren();
            for (int i = 0; i < childrenVars.size(); i++) {
                if (childrenVars.get(i).type.getClass().equals(VariableTreeNodeType.class)) {
                    String varName = childrenVars.get(i).expressionString;
                    if (varName != null) {
                        //StructureMatrix<Double> put = algebraicTree.getParametersValuesVecComputed().put(varName, childrenValues.get(i).eval());
                    }
                }
            }
            return evalRes;//TO REVIEW
        } else if (cType instanceof DoubleTreeNodeType) {
            return cType.eval();
        } else if (cType instanceof VariableTreeNodeType) {
            try {
                return cType.eval();
            } catch (RuntimeException ex) {
                return getChildren().get(0).eval();//cType.eval();
            }
        } else if (cType instanceof PowerTreeNodeType) {
            StructureMatrix<Double> eval1 = getChildren().get(0).eval();
            StructureMatrix<Double> eval2 = getChildren().get(1).eval();
            int dim1, dim2;
            if (eval1.getDim() == 1 && eval2.getDim() == 1) {
                dim1 = eval1.data1d.size();
                dim2 = eval2.data1d.size();
                Point3D res3 = Point3D.O0;
                Point2D res2 = new Point2D();
                double res1 = 1;
                for (int i = 0; i < getChildren().size() - 1; i++) {
                    StructureMatrix<Double> evalI = getChildren().get(i).eval();
                    StructureMatrix<Double> evalI1 = getChildren().get(i + 1).eval();
                    if (dim1 == dim2 && dim1 == 3) {
                        Point3D point3D2 = new Point3D(eval2.data1d.get(0), eval2.data1d.get(1), eval2.data1d.get(2));
                        if (i == 0)
                            res3 = p3(evalI);
                        res3 = res3.prodVect(p3(evalI1));
                        evalRes = new StructureMatrix<>(1, Double.class);
                        evalRes.setElem(res3.get(0), 0);
                        evalRes.setElem(res3.get(1), 1);
                        evalRes.setElem(res3.get(2), 2);
                    } else if (dim1 == dim2 && dim1 == 2) {//???
                        if (i == 0)
                            res2 = p2(evalI);
                        Point2D point2D2 = new Point2D(evalI1.data1d.get(0), evalI1.data1d.get(1));
                        res2 = res2.mult(point2D2);
                        evalRes = new StructureMatrix<>(1, Double.class);
                        evalRes.setElem(res2.get(0), 0);
                        evalRes.setElem(res2.get(1), 1);
                    } else if (dim1 == dim2 && dim1 == 0) {
                        if (i == 0)
                            res1 = evalI.getElem(0);
                        res1 = Math.pow(res1, evalI1.getElem(0));
                        evalRes = new StructureMatrix<>(1, Double.class);
                        evalRes.setElem(res1, 0);
                    }

                }
            } else if (eval1.getDim() == 0 && eval2.getDim() == 0) {
                double res1 = 1.0;
                for (int i = 0; i < getChildren().size() - 1; i++) {
                    StructureMatrix<Double> evalI = getChildren().get(i).eval();
                    StructureMatrix<Double> evalI1 = getChildren().get(i + 1).eval();
                    if (i == 0)
                        res1 = evalI.getElem();
                    res1 = Math.pow(res1, evalI1.getElem());
                    evalRes = new StructureMatrix<>(0, Double.class);
                    evalRes.setElem(res1);
                }
            }
/*
 double pow = evalRes.getElem();
                for (int i = 1; i < getChildren().size(); i++) {
                    pow = Math.pow(pow, getChildren().get(i).eval().getElem());
                }
 */
            return evalRes;

        } else if (cType instanceof FactorTreeNodeType) {
            if (getChildren().size() == 1) {
                evalRes = getChildren().get(0).eval();///SIGN
                if (evalRes.getDim() == 1) {
                    return evalRes;
                } else if (evalRes.getDim() == 0) {
                    double v = ((Double) getChildren().get(0).eval().getElem()) * getChildren().get(0).type.getSign1();
                    return evalRes.setElem(v);
                }
                return evalRes;
            } else if (getChildren().size() > 1) {
                double dot = 1.0;
                TreeNode treeNode1 = getChildren().get(0);
                int dim = treeNode1.eval().getDim();
                if (dim == 1) {
                    evalRes = new StructureMatrix<>(1, Double.class);
                    evalRes.setElem(1.0, 0);
                } else if (dim == 0) {
                    evalRes = new StructureMatrix<>(0, Double.class);
                    evalRes.setElem(1.0);
                }
                TreeNode treeNode = getChildren().get(0);
                evalRes = initVoid(treeNode, 1.0);
                for (int i = 0; i < getChildren().size(); i++) {
                    treeNode = getChildren().get(i);
                    StructureMatrix<Double> treeNodeEval = treeNode.eval();
                    double op1;
                    Double mult = 1.0;
                    int leftOp = evalRes.getDim() == 1 ? evalRes.data1d.size() : 0;
                    int rightOp = treeNodeEval.getDim() == 1 ? treeNodeEval.data1d.size() : 0;
                    int finalSize = Math.max(leftOp, rightOp);
                    double argMultiIndices = (double) leftOp / rightOp;

                    StructureMatrix<Double> argLeft = evalRes;
                    StructureMatrix<Double> argRight = treeNodeEval;
                    if (leftOp == 0) {
                        StructureMatrix<Double> e2 = new StructureMatrix<>(1, Double.class);
                        e2.setElem(argLeft.getDim() == 1 ? argLeft.getElem(0) : argLeft.getElem(), 0);
                        evalRes = e2;
                        leftOp = 1;
                    }
                    if (rightOp == 0) {
                        StructureMatrix<Double> e2 = new StructureMatrix<>(1, Double.class);
                        e2.setElem(argRight.getDim() == 1 ? argRight.getElem(0) : argRight.getElem(), 0);
                        treeNodeEval = e2;
                        rightOp = 1;
                    }
                    finalSize = Math.max(leftOp, rightOp);
                    argMultiIndices = (double) (leftOp / rightOp);
                    for (int j = 0; j < finalSize; j++) {
                        double argJ0 = 1.0;
                        double argJ1 = 1.0;
                        if (argLeft.getDim() == 0) {
                            argJ0 = argLeft.getElem() == null ? 1.0 : argLeft.getElem();
                        } else if (argLeft.getDim() == 1) {
                            argJ0 = argLeft.getElem((int) ((rightOp > leftOp) ? (j * argMultiIndices) : j));
                        }

                        if (argRight.getDim() == 0) {
                            argJ1 = argRight.getElem() == null ? 1.0 : argRight.getElem();
                        } else if (argRight.getDim() == 1) {
                            argJ1 = argRight.getElem((int) ((rightOp > leftOp) ? (j) : (j / argMultiIndices)));
                        }


                        if (treeNode.type instanceof FactorTreeNodeType) {
                            op1 = ((FactorTreeNodeType) treeNode.type).getSignFactor();
                        } else if (treeNode.type instanceof SignTreeNodeType) {
                            op1 = ((SignTreeNodeType) treeNode.type).getSign();
                        } else {
                            op1 = treeNode.type.sign1;
                        }
                        if (op1 == 1) {
                            evalRes.setElem(argJ0 * argJ1, j);
                        } else {
                            evalRes.setElem(argJ0 / argJ1, j);
                        }
                    }
                    if (evalRes.getDim() == 1 && evalRes.getData1d().size() <= 1) {
                        StructureMatrix<Double> evalRes1 = new StructureMatrix<Double>(0, Double.class);
                        evalRes1.setElem(evalRes.getElem(0));
                        evalRes = evalRes1;
                    }
                }
                return evalRes;

            }
        } else if (cType instanceof TermTreeNodeType) {
            /*
            if (getChildren().size() == 1) {

                evalRes = getChildren().get(0).eval();///SIGN
                if (evalRes.getDim() == 1) {
                    return evalRes;
                } else if (evalRes.getDim() == 0) {
                    double v = ((Double) getChildren().get(0).eval().getElem()) * getChildren().get(0).type.getSign1();
                    return evalRes.setElem(v);
                }
                return evalRes;
            } else if (getChildren().size() > 1) {
                TreeNode treeNode1 = getChildren().get(0);
                int dim = treeNode1.eval().getDim();
                if (dim == 1) {
                    evalRes = new StructureMatrix<>(1, Double.class);
                    evalRes.setElem(0.0, 0);
                } else if (dim == 0) {
                    evalRes = new StructureMatrix<>(0, Double.class);
                    evalRes.setElem(0.0);
                }
                evalRes = initVoid(getChildren().get(0), 0.0);
                TreeNode treeNode = getChildren().get(0);
                for (int i = 0; i < getChildren().size(); i++) {
                    treeNode = getChildren().get(i);
                    StructureMatrix<Double> treeNodeEval = treeNode.eval();
                    double op1 = 1.0;
                    int leftOp = evalRes.getDim() == 1 ? evalRes.data1d.size() : 0;
                    int rightOp = treeNodeEval.getDim() == 1 ? treeNodeEval.data1d.size() : 0;

                    int finalSize = Math.max(leftOp, rightOp);
                    double argMultiIndices = (double) leftOp / rightOp;
                    StructureMatrix<Double> argLeft = evalRes;
                    StructureMatrix<Double> argRight = treeNodeEval;
                    if (leftOp == 0) {
                        StructureMatrix<Double> e2 = new StructureMatrix<>(1, Double.class);
                        e2.setElem(argLeft.getDim() == 1 ? (argLeft.getData1d().isEmpty() ? 0.0 : argLeft.getElem(0)) : argLeft.getElem(), 0);
                        evalRes = e2;
                        leftOp = 1;
                    }
                    if (rightOp == 0) {
                        StructureMatrix<Double> e2 = new StructureMatrix<>(1, Double.class);
                        e2.setElem(argRight.getDim() == 1 ? (argRight.getData1d().isEmpty() ? 0.0 : argLeft.getElem(0)) : argRight.getElem(), 0);
                        rightOp = 1;
                    }
                    finalSize = Math.max(leftOp, rightOp);
                    argMultiIndices = (((double) leftOp) / rightOp);
                    for (int j = 0; j < finalSize; j++) {
                        double argJ0 = 0.0;
                        double argJ1 = 0.0;
                        if (argLeft.getDim() == 0) {
                            argJ0 = argLeft.getElem() == null ? 0.0 : argLeft.getElem();
                        } else if (argLeft.getDim() == 1) {
                            int i1 = (int) ((rightOp > leftOp) ? (j * argMultiIndices) : j);
                            if (i1 < argLeft.getData1d().size()) argJ0 = argLeft.getElem(i1);
                            else argJ0 = 0.0;
                        }

                        if (argRight.getDim() == 0) {
                            argJ1 = argRight.getElem() == null ? 0.0 : argRight.getElem();
                        } else if (argRight.getDim() == 1) {
                            int i1 = (int) ((rightOp > leftOp) ? (j * argMultiIndices) : (j));
                            if (i1 < argRight.getData1d().size()) argJ1 = argRight.getElem(i1);
                            else argJ1 = 0.0;
                        }

                        if (treeNode.type instanceof TermTreeNodeType) {
                            op1 = ((TermTreeNodeType) treeNode.type).getSign1();
                        } else if (treeNode.type != null) {
                            op1 = treeNode.type.getSign1();
                        } else op1 = 1.0;
                        if (op1 == 0)
                            op1 = 1.0;
                        if (op1 == 1) {
                            evalRes.setElem(argJ0 + op1 * argJ1, j);
                        } else {
                            evalRes.setElem(argJ0 + op1 * argJ1, j);
                        }

                        if (op1 == 1) {
                            evalRes.setElem(argJ0 + op1 * argJ1, j);
                        } else {
                            evalRes.setElem(argJ0 + op1 * argJ1, j);
                        }
                    }
                    if (evalRes.getDim() == 1 && evalRes.getData1d().size() <= 1) {
                        StructureMatrix<Double> evalRes1 = new StructureMatrix<Double>(0, Double.class);
                        evalRes1.setElem(evalRes.getData1d().size() > 0 ? evalRes.getElem(0) : evalRes.getElem());
                        evalRes = evalRes1;
                    }
                }

                return evalRes;
            }
            */
            if (getChildren().size() == 1) {
                return evalRes.setElem(((Double) getChildren().get(0).eval().getElem()) * getChildren().get(0).type.getSign1());
            }
            double sum = 0.0;
            int dimChild0 = getChildren().get(0).eval().getDim();
            if (dimChild0 == 0) {
                evalRes = new StructureMatrix<>(0, Double.class);
            } else {
                evalRes = new StructureMatrix<>(1, Double.class);
                for (int j = 0; j < evalRes.data1d.size(); j++)
                    evalRes.setElem(0.0, j);
            }
            for (int i = 0; i < getChildren().size(); i++) {
                TreeNode treeNode1 = getChildren().get(i);
                double op1 = treeNode1.type.getSign1();
                StructureMatrix<Double> eval = treeNode1.eval();
                if (eval.getDim() == 1) {
                    for (int j = 0; j < eval.data1d.size(); j++) {
                        double e = 0.0;
                        if (evalRes.data1d != null && j < evalRes.data1d.size()) {
                            e = evalRes.getElem(j);
                        } else {
                            evalRes.setElem(e, j);
                        }
                        evalRes.setElem(e + eval.getElem(j) * op1, j);
                    }
                } else if (eval.getDim() == 0) {
                    double current = op1 * (Double) ((eval.getElem() == null) ? 0.0 : eval.getElem());
                    double evalSum = (evalRes.getElem() == null) ? 0.0 : evalRes.getElem();
                    evalRes.setElem(evalSum + current);
                }
            }
            return evalRes;
        } else if (cType instanceof TreeTreeNodeTypeVector c) {
            String function = c.getfName();
            System.out.println("cName in TreeTreeNodeTypeVector = " + function);
            if (!getChildren().isEmpty()) {
                if (!getChildren().get(0).getChildren().isEmpty() /*&& getChildren().get(0).getChildren().get(0).type instanceof VectorTreeNodeType*/) {
                    evalRes = new StructureMatrix<>(1, Double.class);
                    int k = 0;
                    for (int j = 0; j < getChildren().size(); j++) {
                        switch (evalRes.getDim()) {
                            case 0:
                                for (int i = 0; i < getChildren().get(j).getChildren().size(); i++) {
                                    StructureMatrix<Double> eval = getChildren().get(j).getChildren().get(i).eval();
                                    evalRes.setElem(eval.getElem(i), k);
                                    k++;
                                }
                                break;
                            case 1:
                                for (int i = 0; i < getChildren().get(j).getChildren().size(); i++) {
                                    StructureMatrix<Double> eval = getChildren().get(j).getChildren().get(i).eval();
                                    if (eval.getDim() == 1) {
                                        for (int l = 0; l < eval.data1d.size(); l++) {
                                            evalRes.setElem(eval.getElem(l), k);
                                            k++;
                                        }
                                    } else if (eval.getDim() == 0) {
                                        evalRes.setElem(eval.getElem(), k);
                                        k++;
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }
            if (evalRes.getDim() == 1) {
                Double[] vec = new Double[evalRes.getData1d().size()];
                for (int i = 0; i < evalRes.getData1d().size(); i++) {
                    vec[i] = evalRes.getElem(i);
                }
                double search = Functions.search(function, vec);
                evalRes = new StructureMatrix<>(0, Double.class);
                evalRes.setElem(search);


            } else if (evalRes.getDim() == 0) {
                Double[] vec = new Double[]{evalRes.getElem()};
                double search = Functions.search(function, vec);
                evalRes = new StructureMatrix<>(0, Double.class);
                evalRes.setElem(search);
            }
            return evalRes;
        } else if (cType instanceof TreeTreeNodeType) {
            if (!getChildren().isEmpty()) {
                if (!getChildren().get(0).getChildren().isEmpty() && getChildren().get(0).getChildren().get(0).type instanceof VectorTreeNodeType) {
                    evalRes = new StructureMatrix<>(1, Double.class);
                    switch (evalRes.getDim()) {
                        case 0:
                            for (int i = 0; i < getChildren().get(0).getChildren().size(); i++) {
                                StructureMatrix<Double> eval = getChildren().get(0).getChildren().get(i).eval();
                                evalRes.setElem(eval.getElem(i), i);
                            }
                            break;
                        case 1:
                            int k = 0;
                            for (int i = 0; i < getChildren().get(0).getChildren().size(); i++) {
                                StructureMatrix<Double> eval = getChildren().get(0).getChildren().get(i).eval();
                                if (eval.getDim() == 1) {
                                    for (int j = 0; j < eval.data1d.size(); j++) {
                                        evalRes.setElem(eval.getElem(j), k++);
                                    }
                                } else if (eval.getDim() == 0) {
                                    evalRes.setElem(eval.getElem(), k++);
                                }
                            }
                            break;
                    }
                    return evalRes;
                } else {
                    if (!getChildren().isEmpty()) {
                        int k = 0;
                        for (int j = 0; j < getChildren().size(); j++) {

                            StructureMatrix<Double> eval = getChildren().get(j).eval();
                            if (eval.getDim() == 1) {
                                evalRes = new StructureMatrix<>(1, Double.class);
                                for (int i = 0; i < eval.data1d.size(); i++) {
                                    evalRes.setElem(eval.getElem(i), k);
                                    k++;
                                }
                            } else if (eval.getDim() == 0) {
                                evalRes = new StructureMatrix<>(0, Double.class);
                                evalRes.setElem(eval.getElem());
                                k++;
                            }
                        }
                    }
                }
                return evalRes;
            }
            return null;
        } /*else if (cType instanceof TreeTreeNodeTypeVector) {
            if (!getChildren().isEmpty()) {
                if (!getChildren().get(0).getChildren().isEmpty() && getChildren().get(0).getChildren().get(0).type instanceof VectorTreeNodeType) {
                    evalRes = new StructureMatrix<>(1, Double.class);
                    switch (evalRes.getDim()) {
                        case 0:
                            for (int i = 0; i < getChildren().get(0).getChildren().size(); i++) {
                                StructureMatrix<Double> eval = getChildren().get(0).getChildren().get(i).eval();
                                evalRes.setElem(eval.getElem(i), i);
                            }
                            break;
                        case 1:
                            int k = 0;
                            for (int i = 0; i < getChildren().get(0).getChildren().size(); i++) {
                                StructureMatrix<Double> eval = getChildren().get(0).getChildren().get(i).eval();
                                if (eval.getDim() == 1) {
                                    for (int j = 0; j < eval.data1d.size(); j++) {
                                        evalRes.setElem(eval.getElem(j), k++);
                                    }
                                } else if (eval.getDim() == 0) {
                                    evalRes.setElem(eval.getElem(), k++);
                                }
                            }
                            break;
                    }
                    return evalRes;
                } else {
                    StructureMatrix<Double> eval = getChildren().get(0).eval();
                    if (eval.getDim() == 1) {
                        for (int i = 0; i < eval.data1d.size(); i++) {
                            evalRes.setElem(eval.getElem(i), i);
                        }
                    } else if (eval.getDim() == 0) {
                        evalRes.setElem(eval.getElem());
                    }
                }
                return evalRes;
            }
            return null;

            }*/ else if (cType instanceof SignTreeNodeType) {
            double s1 = /* ((SignTreeNodeType) cType).getSign(); */cType.sign1;
            StructureMatrix<Double> eval;
            if (!getChildren().isEmpty()) {
                eval = getChildren().get(0).eval();
            } else {
                eval = evalRes.setElem(0.0);
            }
            if (eval.getDim() == 1) {
                for (int i = 0; i < eval.data1d.size(); i++) {
                    evalRes.setElem(eval.getElem(i) * s1, i);
                }
            } else if (eval.getDim() == 0) {
                evalRes.setElem(s1 * eval.getElem());
            }
            return evalRes;
        } else if (cType instanceof VectorTreeNodeType || this instanceof VectorTreeNode) {
            if (getChildren().size() == 1) {
                return getChildren().get(0).eval();
            }
            evalRes = new StructureMatrix<>(1, Double.class);
            for (int i = 0; i < getChildren().size(); i++) {
                StructureMatrix<Double> eval = getChildren().get(i).eval();
                double val = (eval.getDim() == 1) ? (eval.getData1d().isEmpty() ? 0.0 : eval.getElem(0)) : eval.getElem();
                evalRes.setElem(val, i);
            }
            return evalRes;
        } else if (cType instanceof LogicalNumericTreeNodeType) {

        } else if (cType instanceof LogicalLogicalTreeNodeType) {

        }

        StructureMatrix<Double> eval = new StructureMatrix<>(0, Double.class);

        if (type != null) {
            eval = type.eval();
        } else if (!getChildren().isEmpty()) {
            eval = getChildren().get(0).eval();
        }

        return eval == null ? evalRes.setElem(0.0) : eval;

    }

    private Point2D p2(StructureMatrix<Double> evalI) {
        return new Point2D(evalI.getElem(0), evalI.getElem(1));
    }

    private StructureMatrix<Double> initVoid(TreeNode treeNode, double val) throws TreeNodeEvalException, AlgebraicFormulaSyntaxException {
        StructureMatrix<Double> evalRes = treeNode.eval();
        switch (evalRes.getDim()) {
            case 0 -> {
                return evalRes.setElem(val);
            }
            case 1 -> {
                for (int i = 0; i < evalRes.getData1d().size(); i++) {
                    evalRes.setElem(val, i);
                }
                return evalRes;
            }
        }
        return null;
    }

    public String toFormula() {
        TreeNodeType cType = (getChildren().size() == 0) ? type : getChildren().get(0).type;
        if (cType instanceof DoubleTreeNodeType || this instanceof TreeNodeDouble) {
            if (objects != null && objects.length > 0 && objects[0] instanceof String) {
                return (String) objects[0];
            }
            return expressionString != null ? expressionString : "0.0";
        } else if (cType instanceof VariableTreeNodeType || this instanceof TreeNodeVariable) {
            if (objects != null && objects.length > 0 && objects[0] instanceof String) {
                return (String) objects[0];
            }
            return expressionString != null ? expressionString : "x";
        } else if (cType instanceof SignTreeNodeType) {
            double s1 = cType.getSign1();
            String inner = !getChildren().isEmpty() ? getChildren().get(0).toFormula() : (expressionString != null ? expressionString : "0.0");
            if (s1 < 0) {
                return "(-" + inner + ")";
            }
            return inner;
        } else if (cType instanceof TermTreeNodeType) {
            if (getChildren().isEmpty()) {
                return expressionString != null ? expressionString : "0.0";
            }
            if (getChildren().size() == 1) {
                double sign = getChildren().get(0).type != null ? getChildren().get(0).type.getSign1() : 1.0;
                String f = getChildren().get(0).toFormula();
                return sign < 0 ? "(-" + f + ")" : f;
            }
            StringBuilder sb = new StringBuilder("(");
            for (int i = 0; i < getChildren().size(); i++) {
                TreeNode child = getChildren().get(i);
                double sign = child.type != null ? child.type.getSign1() : 1.0;
                String f = child.toFormula();
                if (i == 0) {
                    if (sign < 0) sb.append("-").append(f);
                    else sb.append(f);
                } else {
                    if (sign < 0) sb.append(" - ").append(f);
                    else sb.append(" + ").append(f);
                }
            }
            sb.append(")");
            return sb.toString();
        } else if (cType instanceof FactorTreeNodeType) {
            if (getChildren().isEmpty()) {
                return expressionString != null ? expressionString : "1.0";
            }
            if (getChildren().size() == 1) {
                return getChildren().get(0).toFormula();
            }
            String acc = getChildren().get(0).toFormula();
            for (int i = 1; i < getChildren().size(); i++) {
                TreeNode child = getChildren().get(i);
                double sign = child.type != null ? child.type.getSign1() : 1.0;
                String f = child.toFormula();
                if (sign < 0) {
                    acc = "(" + acc + " / " + f + ")";
                } else {
                    acc = "(" + acc + " * " + f + ")";
                }
            }
            return acc;
        } else if (cType instanceof PowerTreeNodeType) {
            if (getChildren().isEmpty()) {
                return expressionString != null ? expressionString : "1.0";
            }
            if (getChildren().size() == 1) {
                return getChildren().get(0).toFormula();
            }
            String acc = getChildren().get(0).toFormula();
            for (int i = 1; i < getChildren().size(); i++) {
                acc = "(" + acc + " ^ " + getChildren().get(i).toFormula() + ")";
            }
            return acc;
        } else if (cType instanceof VectorTreeNodeType || this instanceof VectorTreeNode) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < getChildren().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(getChildren().get(i).toFormula());
            }
            return sb.toString();
        } else if (cType instanceof TreeTreeNodeType) {
            if (!getChildren().isEmpty()) {
                return "(" + getChildren().get(0).toFormula() + ")";
            }
            return expressionString != null ? expressionString : "0.0";
        } else if (cType instanceof IdentTreeNodeType) {
            if (!getChildren().isEmpty()) {
                return getChildren().get(0).toFormula();
            }
            return expressionString != null ? expressionString : "0.0";
        }

        if (!getChildren().isEmpty()) {
            return getChildren().get(0).toFormula();
        }
        return expressionString != null ? expressionString : "0.0";
    }

    public String differentiate(String variableName) {
        TreeNodeType cType = (getChildren().size() == 0) ? type : getChildren().get(0).type;
        if (cType instanceof DoubleTreeNodeType || this instanceof TreeNodeDouble) {
            return "0.0";
        } else if (cType instanceof VariableTreeNodeType || this instanceof TreeNodeVariable) {
            String varName = toFormula().trim();
            return varName.equals(variableName) ? "1.0" : "0.0";
        } else if (cType instanceof SignTreeNodeType) {
            double s1 = cType.getSign1();
            String inner = !getChildren().isEmpty() ? getChildren().get(0).differentiate(variableName) : "0.0";
            if (s1 < 0) {
                return FormulaSimplifier.simplifyNegation(inner);
            }
            return inner;
        } else if (cType instanceof TermTreeNodeType) {
            if (getChildren().isEmpty()) {
                return "0.0";
            }
            if (getChildren().size() == 1) {
                double sign = getChildren().get(0).type != null ? getChildren().get(0).type.getSign1() : 1.0;
                String d = getChildren().get(0).differentiate(variableName);
                return sign < 0 ? FormulaSimplifier.simplifyNegation(d) : d;
            }
            String acc = null;
            for (int i = 0; i < getChildren().size(); i++) {
                TreeNode child = getChildren().get(i);
                double sign = child.type != null ? child.type.getSign1() : 1.0;
                String d = child.differentiate(variableName);
                if (i == 0) {
                    if (sign < 0) acc = FormulaSimplifier.simplifyNegation(d);
                    else acc = d;
                } else {
                    if (sign < 0) acc = FormulaSimplifier.simplifyDifference(acc, d);
                    else acc = FormulaSimplifier.simplifySum(acc, d);
                }
            }
            return acc != null ? acc : "0.0";
        } else if (cType instanceof FactorTreeNodeType) {
            if (getChildren().isEmpty()) {
                return "0.0";
            }
            if (getChildren().size() == 1) {
                return getChildren().get(0).differentiate(variableName);
            }
            String accExpr = getChildren().get(0).toFormula();
            String accDiff = getChildren().get(0).differentiate(variableName);
            for (int i = 1; i < getChildren().size(); i++) {
                TreeNode child = getChildren().get(i);
                double sign = child!=null?(child.type != null ? child.type.getSign1() : 1.0):0;
                String bExpr = child.toFormula();
                String bDiff = child.differentiate(variableName);
                if (sign < 0) {
                    // Quotient rule: (u / v)' = (u' * v - u * v') / (v ^ 2)
                    String term1 = FormulaSimplifier.simplifyProduct(accDiff, bExpr);
                    String term2 = FormulaSimplifier.simplifyProduct(accExpr, bDiff);
                    String numerator = FormulaSimplifier.simplifyDifference(term1, term2);
                    String denom = FormulaSimplifier.simplifyPower(bExpr, "2.0");
                    accDiff = FormulaSimplifier.simplifyQuotient(numerator, denom);
                    accExpr = FormulaSimplifier.simplifyQuotient(accExpr, bExpr);
                } else {
                    // Product rule: (u * v)' = u' * v + u * v'
                    String term1 = FormulaSimplifier.simplifyProduct(accDiff, bExpr);
                    String term2 = FormulaSimplifier.simplifyProduct(accExpr, bDiff);
                    accDiff = FormulaSimplifier.simplifySum(term1, term2);
                    accExpr = FormulaSimplifier.simplifyProduct(accExpr, bExpr);
                }
            }
            return accDiff;
        } else if (cType instanceof PowerTreeNodeType) {
            if (getChildren().isEmpty()) {
                return "0.0";
            }
            if (getChildren().size() == 1) {
                return getChildren().get(0).differentiate(variableName);
            }
            String u = getChildren().get(0).toFormula();
            String uDiff = getChildren().get(0).differentiate(variableName);
            for (int i = 1; i < getChildren().size(); i++) {
                TreeNode child = getChildren().get(i);
                String v = child.toFormula();
                String vDiff = child.differentiate(variableName);
                if (FormulaSimplifier.isZero(vDiff) && FormulaSimplifier.isZero(uDiff)) {
                    uDiff = "0.0";
                } else if (FormulaSimplifier.isZero(vDiff)) {
                    // Constant exponent: n * u^(n-1) * u'
                    String expMinus1 = FormulaSimplifier.simplifyDifference(v, "1.0");
                    String uPow = FormulaSimplifier.simplifyPower(u, expMinus1);
                    String nU = FormulaSimplifier.simplifyProduct(v, uPow);
                    uDiff = FormulaSimplifier.simplifyProduct(nU, uDiff);
                } else {
                    // General power rule: u^v * (v' * log(u) + v * u' / u)
                    String logU = "(log(" + u + "))";
                    String term1 = FormulaSimplifier.simplifyProduct(vDiff, logU);
                    String vUdiff = FormulaSimplifier.simplifyProduct(v, uDiff);
                    String term2 = FormulaSimplifier.simplifyQuotient(vUdiff, u);
                    String sum = FormulaSimplifier.simplifySum(term1, term2);
                    String uPowV = FormulaSimplifier.simplifyPower(u, v);
                    uDiff = FormulaSimplifier.simplifyProduct(uPowV, sum);
                }
                u = FormulaSimplifier.simplifyPower(u, v);
            }
            return uDiff;
        } else if (cType instanceof VectorTreeNodeType || this instanceof VectorTreeNode) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < getChildren().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(getChildren().get(i).differentiate(variableName));
            }
            return sb.toString();
        } else if (cType instanceof TreeTreeNodeType) {
            if (!getChildren().isEmpty()) {
                return "(" + getChildren().get(0).differentiate(variableName) + ")";
            }
            return "0.0";
        } else if (cType instanceof IdentTreeNodeType) {
            if (!getChildren().isEmpty()) {
                return getChildren().get(0).differentiate(variableName);
            }
            return "0.0";
        }

        if (!getChildren().isEmpty()) {
            return getChildren().get(0).differentiate(variableName);
        }
        return "0.0";
    }

    public String integrateSymbolic(String variableName) {
        TreeNodeType cType = (getChildren().size() == 0) ? type : getChildren().get(0).type;
        if (cType instanceof DoubleTreeNodeType || this instanceof TreeNodeDouble) {
            String val = toFormula();
            return FormulaSimplifier.simplifyProduct(val, variableName);
        } else if (cType instanceof VariableTreeNodeType || this instanceof TreeNodeVariable) {
            String varName = toFormula().trim();
            if (varName.equals(variableName)) {
                return "0.5 * (" + variableName + " ^ 2.0)";
            } else {
                return FormulaSimplifier.simplifyProduct(varName, variableName);
            }
        } else if (cType instanceof SignTreeNodeType) {
            double s1 = cType.getSign1();
            String inner = !getChildren().isEmpty() ? getChildren().get(0).integrateSymbolic(variableName) : "0.0";
            if (s1 < 0) {
                return FormulaSimplifier.simplifyNegation(inner);
            }
            return inner;
        } else if (cType instanceof TermTreeNodeType) {
            if (getChildren().isEmpty()) {
                return "0.0";
            }
            String acc = null;
            for (int i = 0; i < getChildren().size(); i++) {
                TreeNode child = getChildren().get(i);
                double sign = child.type != null ? child.type.getSign1() : 1.0;
                String d = child.integrateSymbolic(variableName);
                if (i == 0) {
                    if (sign < 0) acc = FormulaSimplifier.simplifyNegation(d);
                    else acc = d;
                } else {
                    if (sign < 0) acc = FormulaSimplifier.simplifyDifference(acc, d);
                    else acc = FormulaSimplifier.simplifySum(acc, d);
                }
            }
            return acc != null ? acc : "0.0";
        } else if (cType instanceof FactorTreeNodeType) {
            if (getChildren().isEmpty()) {
                return "0.0";
            }
            if (getChildren().size() == 1) {
                return getChildren().get(0).integrateSymbolic(variableName);
            }
            String firstExpr = getChildren().get(0).toFormula();
            if (!containsVariable(getChildren().get(0), variableName)) {
                StringBuilder rest = new StringBuilder();
                for (int i = 1; i < getChildren().size(); i++) {
                    TreeNode child = getChildren().get(i);
                    double sign = child.type != null ? child.type.getSign1() : 1.0;
                    if (i > 1 || sign < 0) {
                        rest.append(sign < 0 ? " / " : " * ");
                    }
                    rest.append(child.toFormula());
                }
                AlgebraicTree subTree = new AlgebraicTree(rest.toString());
                try {
                    subTree.construct();
                    String subInt = subTree.getRoot().integrateSymbolic(variableName);
                    return FormulaSimplifier.simplifyProduct(firstExpr, "(" + subInt + ")");
                } catch (Exception e) {
                    // fall back
                }
            }
            return FormulaSimplifier.simplifyProduct(toFormula(), variableName);
        } else if (cType instanceof PowerTreeNodeType) {
            if (getChildren().size() >= 2) {
                String base = getChildren().get(0).toFormula();
                String exp = getChildren().get(1).toFormula();
                if (base.trim().equals(variableName) && !containsVariable(getChildren().get(1), variableName)) {
                    if (exp.equals("-1.0") || exp.equals("-1") || exp.equals("(-1.0)")) {
                        return "ln(" + variableName + ")";
                    } else {
                        String expPlus1 = FormulaSimplifier.simplifySum(exp, "1.0");
                        String denom = expPlus1;
                        String pow = FormulaSimplifier.simplifyPower(variableName, expPlus1);
                        return FormulaSimplifier.simplifyQuotient(pow, denom);
                    }
                }
            }
            return FormulaSimplifier.simplifyProduct(toFormula(), variableName);
        } else if (cType instanceof VectorTreeNodeType || this instanceof VectorTreeNode) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < getChildren().size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(getChildren().get(i).integrateSymbolic(variableName));
            }
            return sb.toString();
        } else if (cType instanceof TreeTreeNodeType) {
            String funcName = ((TreeTreeNode) this).getFunctionName();
            if (funcName == null && ((TreeTreeNode) this).getMethod() != null) {
                funcName = ((TreeTreeNode) this).getMethod().getName();
            }
            if ("sin".equals(funcName) && getChildren().size() > 0 && getChildren().get(0).toFormula().trim().equals(variableName)) {
                return "-cos(" + variableName + ")";
            } else if ("cos".equals(funcName) && getChildren().size() > 0 && getChildren().get(0).toFormula().trim().equals(variableName)) {
                return "sin(" + variableName + ")";
            } else if ("exp".equals(funcName) && getChildren().size() > 0 && getChildren().get(0).toFormula().trim().equals(variableName)) {
                return "exp(" + variableName + ")";
            }
        }

        return FormulaSimplifier.simplifyProduct(toFormula(), variableName);
    }

    public static boolean containsVariable(TreeNode node, String variableName) {
        if (node == null) return false;
        if (node.type instanceof VariableTreeNodeType || node instanceof TreeNodeVariable) {
            return node.toFormula().trim().equals(variableName);
        }
        for (TreeNode child : node.getChildren()) {
            if (containsVariable(child, variableName)) return true;
        }
        return false;
    }

    public ArrayList<TreeNode> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<TreeNode> children) {
        this.children = children;
    }

    public TreeNode getParent() {
        return parent;
    }

    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public String getExpressionString() {
        return expressionString;
    }

    public void setExpressionString(String expressionString) {
        this.expressionString = expressionString;
    }


    
    public String toString() {
        String s = "TreeNode " + this.getClass().getSimpleName() + "\nExpression string: " + expressionString + (type == null ? "Type null" : "Type: " + type.getClass() + "\n " + type.toString()) + "\nChildren: " + getChildren().size() + "\n";
        int i = 0;
        for (TreeNode t : getChildren()) {
            s += "Child (" + i++ + ") : " + t.toString() + "\n";
        }
        return s + "\n";
    }

    private Point3D p3(StructureMatrix<Double> eval1) {
        return new Point3D(eval1.data1d.get(0), eval1.data1d.get(1), eval1.data1d.get(2));
    }
}
