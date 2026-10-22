/*
 *
 *  *
 *  *  * Copyright (c) 2026. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2026 Manuel Daniel Dahmen
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

import one.empty3.library.StructureMatrix;

import java.util.function.DoubleUnaryOperator;

/**
 * High-precision adaptive numerical quadrature engine for definite 1D scalar,
 * vector, and 2D iterated rectangular integration across AlgebraicTree expressions.
 */
public class QuadratureIntegrator {

    public static final double DEFAULT_TOLERANCE = 1e-6;
    public static final int MAX_RECURSION_DEPTH = 25;

    public static double adaptiveSimpson(AlgebraicTree tree, String variableName, double a, double b) {
        return adaptiveSimpson(tree, variableName, a, b, DEFAULT_TOLERANCE);
    }

    public static double adaptiveSimpson(AlgebraicTree tree, String variableName, double a, double b, double tolerance) {
        if (a == b) {
            return 0.0;
        }
        if (b < a) {
            return -adaptiveSimpson(tree, variableName, b, a, tolerance);
        }

        if (tree.getRoot() == null) {
            try {
                tree.construct();
            } catch (AlgebraicFormulaSyntaxException e) {
                throw new RuntimeException("Cannot integrate unconstructed tree", e);
            }
        }

        Double originalVal = tree.getParametersValues().get(variableName);
        try {
            DoubleUnaryOperator f = x -> {
                tree.setParameter(variableName, x);
                try {
                    StructureMatrix<Double> res = tree.eval();
                    return res.getElem();
                } catch (Exception e) {
                    throw new RuntimeException("Evaluation error during quadrature at " + variableName + " = " + x, e);
                }
            };
            return integrateFunction(f, a, b, tolerance);
        } finally {
            if (originalVal != null) {
                tree.setParameter(variableName, originalVal);
            } else {
                tree.getParametersValues().remove(variableName);
            }
        }
    }

    public static StructureMatrix<Double> adaptiveSimpsonVector(AlgebraicTree tree, String variableName, double a, double b) {
        return adaptiveSimpsonVector(tree, variableName, a, b, DEFAULT_TOLERANCE);
    }

    public static StructureMatrix<Double> adaptiveSimpsonVector(AlgebraicTree tree, String variableName, double a, double b, double tolerance) {
        if (tree.getRoot() == null) {
            try {
                tree.construct();
            } catch (AlgebraicFormulaSyntaxException e) {
                throw new RuntimeException("Cannot integrate unconstructed tree", e);
            }
        }

        Double originalVal = tree.getParametersValues().get(variableName);
        try {
            tree.setParameter(variableName, a);
            StructureMatrix<Double> sample = tree.eval();

            if (sample.getDim() == 0) {
                double scalarRes = adaptiveSimpson(tree, variableName, a, b, tolerance);
                StructureMatrix<Double> res = new StructureMatrix<>(0, Double.class);
                res.setElem(scalarRes);
                return res;
            } else if (sample.getDim() == 1) {
                int size = sample.getData1d().size();
                StructureMatrix<Double> res = new StructureMatrix<>(1, Double.class);
                if (a == b) {
                    for (int i = 0; i < size; i++) {
                        res.setElem(0.0, i);
                    }
                    return res;
                }

                for (int i = 0; i < size; i++) {
                    final int compIndex = i;
                    DoubleUnaryOperator f = x -> {
                        tree.setParameter(variableName, x);
                        try {
                            StructureMatrix<Double> evalRes = tree.eval();
                            return evalRes.getElem(compIndex);
                        } catch (Exception e) {
                            throw new RuntimeException("Evaluation error during vector quadrature at " + variableName + " = " + x, e);
                        }
                    };
                    double compIntegral;
                    if (b < a) {
                        compIntegral = -integrateFunction(f, b, a, tolerance);
                    } else {
                        compIntegral = integrateFunction(f, a, b, tolerance);
                    }
                    res.setElem(compIntegral, i);
                }
                return res;
            } else {
                // Dim 2 or higher
                StructureMatrix<Double> res = new StructureMatrix<>(sample.getDim(), Double.class);
                int rows = sample.getData2d().size();
                for (int r = 0; r < rows; r++) {
                    int cols = sample.getData2d().get(r).size();
                    for (int c = 0; c < cols; c++) {
                        final int row = r;
                        final int col = c;
                        DoubleUnaryOperator f = x -> {
                            tree.setParameter(variableName, x);
                            try {
                                StructureMatrix<Double> evalRes = tree.eval();
                                return evalRes.getElem(row, col);
                            } catch (Exception e) {
                                throw new RuntimeException("Evaluation error during matrix quadrature at " + variableName + " = " + x, e);
                            }
                        };
                        double compIntegral;
                        if (a == b) {
                            compIntegral = 0.0;
                        } else if (b < a) {
                            compIntegral = -integrateFunction(f, b, a, tolerance);
                        } else {
                            compIntegral = integrateFunction(f, a, b, tolerance);
                        }
                        res.setElem(compIntegral, row, col);
                    }
                }
                return res;
            }
        } catch (TreeNodeEvalException | AlgebraicFormulaSyntaxException e) {
            throw new RuntimeException("Error initializing vector quadrature", e);
        } finally {
            if (originalVal != null) {
                tree.setParameter(variableName, originalVal);
            } else {
                tree.getParametersValues().remove(variableName);
            }
        }
    }

    public static double integrate2D(AlgebraicTree tree, String varX, double ax, double bx, String varY, double ay, double by) {
        return integrate2D(tree, varX, ax, bx, varY, ay, by, DEFAULT_TOLERANCE);
    }

    public static double integrate2D(AlgebraicTree tree, String varX, double ax, double bx, String varY, double ay, double by, double tolerance) {
        if (ax == bx || ay == by) {
            return 0.0;
        }
        if (bx < ax) {
            return -integrate2D(tree, varX, bx, ax, varY, ay, by, tolerance);
        }
        if (by < ay) {
            return -integrate2D(tree, varX, ax, bx, varY, by, ay, tolerance);
        }

        if (tree.getRoot() == null) {
            try {
                tree.construct();
            } catch (AlgebraicFormulaSyntaxException e) {
                throw new RuntimeException("Cannot integrate unconstructed tree", e);
            }
        }

        Double originalValX = tree.getParametersValues().get(varX);
        Double originalValY = tree.getParametersValues().get(varY);

        try {
            double innerTol = tolerance / Math.max(1.0, (bx - ax));
            DoubleUnaryOperator outerF = x -> {
                tree.setParameter(varX, x);
                return adaptiveSimpson(tree, varY, ay, by, innerTol);
            };

            return integrateFunction(outerF, ax, bx, tolerance);
        } finally {
            if (originalValX != null) {
                tree.setParameter(varX, originalValX);
            } else {
                tree.getParametersValues().remove(varX);
            }

            if (originalValY != null) {
                tree.setParameter(varY, originalValY);
            } else {
                tree.getParametersValues().remove(varY);
            }
        }
    }

    public static double integrateFunction(DoubleUnaryOperator f, double a, double b, double tolerance) {
        double m = (a + b) / 2.0;
        double fa = f.applyAsDouble(a);
        double fb = f.applyAsDouble(b);
        double fm = f.applyAsDouble(m);
        double whole = simpson(fa, fm, fb, a, b);
        return adaptiveSimpsonRec(f, a, b, tolerance, fa, fm, fb, whole, 1);
    }

    private static double simpson(double fa, double fm, double fb, double a, double b) {
        return ((b - a) / 6.0) * (fa + 4.0 * fm + fb);
    }

    private static double adaptiveSimpsonRec(DoubleUnaryOperator f, double a, double b, double tolerance,
                                             double fa, double fm, double fb, double whole, int depth) {
        double m = (a + b) / 2.0;
        double lm = (a + m) / 2.0;
        double rm = (m + b) / 2.0;
        double flm = f.applyAsDouble(lm);
        double frm = f.applyAsDouble(rm);

        double left = simpson(fa, flm, fm, a, m);
        double right = simpson(fm, frm, fb, m, b);
        double delta = left + right - whole;

        if (depth >= MAX_RECURSION_DEPTH || Math.abs(delta) <= 15.0 * tolerance) {
            // Boole's / Richardson correction
            return left + right + delta / 15.0;
        }

        return adaptiveSimpsonRec(f, a, m, tolerance / 2.0, fa, flm, fm, left, depth + 1)
                + adaptiveSimpsonRec(f, m, b, tolerance / 2.0, fm, frm, fb, right, depth + 1);
    }
}
