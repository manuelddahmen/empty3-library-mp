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
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AlgebraicTreeDerivativeTest {

    private static final double EPSILON = 1e-4;

    @Test
    public void testConstantDerivative() throws Exception {
        AlgebraicTree tree = new AlgebraicTree("42.0");
        AlgebraicTree diff = tree.derivative("x");
        assertNotNull(diff);
        assertEquals(0.0, diff.eval().getElem(), EPSILON);

        AlgebraicTree tree2 = new AlgebraicTree("3.14 * 2.0");
        AlgebraicTree diff2 = tree2.derivative("x");
        assertNotNull(diff2);
        assertEquals(0.0, diff2.eval().getElem(), EPSILON);
    }

    @Test
    public void testVariableDerivative() throws Exception {
        AlgebraicTree tree = new AlgebraicTree("x");
        AlgebraicTree diff = tree.derivative("x");
        assertNotNull(diff);
        assertEquals(1.0, diff.eval().getElem(), EPSILON);

        // Unknown / other variable partial derivative
        AlgebraicTree treeY = new AlgebraicTree("y");
        AlgebraicTree diffYwithRespectToX = treeY.derivative("x");
        assertNotNull(diffYwithRespectToX);
        assertEquals(0.0, diffYwithRespectToX.eval().getElem(), EPSILON);

        AlgebraicTree diffYwithRespectToY = treeY.derivative("y");
        assertNotNull(diffYwithRespectToY);
        assertEquals(1.0, diffYwithRespectToY.eval().getElem(), EPSILON);
    }

    @Test
    public void testPolynomialDerivative() throws Exception {
        // f(x) = 3*x^3 + 2*x^2 - 5*x + 7
        // f'(x) = 9*x^2 + 4*x - 5
        AlgebraicTree tree = new AlgebraicTree("3.0 * (x ^ 3.0) + 2.0 * (x ^ 2.0) - 5.0 * x + 7.0");
        AlgebraicTree diff = tree.derivative("x");
        assertNotNull(diff);

        double xVal = 2.0;
        diff.setParameter("x", xVal);
        double expected = 9.0 * Math.pow(xVal, 2) + 4.0 * xVal - 5.0; // 9*4 + 8 - 5 = 39.0
        assertEquals(expected, diff.eval().getElem(), EPSILON);
    }

    @Test
    public void testProductRule() throws Exception {
        // f(x) = x * sin(x)
        // f'(x) = sin(x) + x * cos(x)
        AlgebraicTree tree = new AlgebraicTree("x * sin(x)");
        AlgebraicTree diff = tree.derivative("x");
        assertNotNull(diff);

        double xVal = 1.0;
        diff.setParameter("x", xVal);
        double expected = Math.sin(xVal) + xVal * Math.cos(xVal);
        assertEquals(expected, diff.eval().getElem(), EPSILON);
    }

    @Test
    public void testQuotientRule() throws Exception {
        // f(x) = x / (x + 1.0)
        // f'(x) = 1.0 / (x + 1.0)^2
        AlgebraicTree tree = new AlgebraicTree("x / (x + 1.0)");
        AlgebraicTree diff = tree.derivative("x");
        assertNotNull(diff);

        double xVal = 2.0;
        diff.setParameter("x", xVal);
        double expected = 1.0 / Math.pow(xVal + 1.0, 2); // 1 / 9
        assertEquals(expected, diff.eval().getElem(), EPSILON);
    }

    @Test
    public void testPowerRuleConstantExponent() throws Exception {
        // f(x) = x ^ 0.5 (sqrt(x))
        // f'(x) = 0.5 * x^(-0.5)
        AlgebraicTree tree = new AlgebraicTree("x ^ 0.5");
        AlgebraicTree diff = tree.derivative("x");
        assertNotNull(diff);

        double xVal = 4.0;
        diff.setParameter("x", xVal);
        double expected = 0.5 * Math.pow(xVal, -0.5); // 0.25
        assertEquals(expected, diff.eval().getElem(), EPSILON);
    }

    @Test
    public void testPowerRuleVariableExponent() throws Exception {
        // f(x) = x ^ x
        // f'(x) = x^x * (ln(x) + 1)
        AlgebraicTree tree = new AlgebraicTree("x ^ x");
        AlgebraicTree diff = tree.derivative("x");
        assertNotNull(diff);

        double xVal = 2.0;
        diff.setParameter("x", xVal);
        double expected = Math.pow(xVal, xVal) * (Math.log(xVal) + 1.0);
        assertEquals(expected, diff.eval().getElem(), EPSILON);
    }

    @Test
    public void testTrigonometricFunctions() throws Exception {
        // sin(2 * x) -> 2 * cos(2 * x)
        AlgebraicTree sinTree = new AlgebraicTree("sin(2.0 * x)");
        AlgebraicTree sinDiff = sinTree.derivative("x");
        sinDiff.setParameter("x", 0.5);
        assertEquals(2.0 * Math.cos(2.0 * 0.5), sinDiff.eval().getElem(), EPSILON);

        // cos(x) -> -sin(x)
        AlgebraicTree cosTree = new AlgebraicTree("cos(x)");
        AlgebraicTree cosDiff = cosTree.derivative("x");
        cosDiff.setParameter("x", 1.0);
        assertEquals(-Math.sin(1.0), cosDiff.eval().getElem(), EPSILON);

        // tan(x) -> 1 + tan(x)^2
        AlgebraicTree tanTree = new AlgebraicTree("tan(x)");
        AlgebraicTree tanDiff = tanTree.derivative("x");
        tanDiff.setParameter("x", 0.5);
        assertEquals(1.0 + Math.pow(Math.tan(0.5), 2), tanDiff.eval().getElem(), EPSILON);
    }

    @Test
    public void testInverseTrigonometricFunctions() throws Exception {
        // asin(x) -> 1 / sqrt(1 - x^2)
        AlgebraicTree asinTree = new AlgebraicTree("asin(x)");
        AlgebraicTree asinDiff = asinTree.derivative("x");
        asinDiff.setParameter("x", 0.5);
        assertEquals(1.0 / Math.sqrt(1.0 - 0.25), asinDiff.eval().getElem(), EPSILON);

        // atan(x) -> 1 / (1 + x^2)
        AlgebraicTree atanTree = new AlgebraicTree("atan(x)");
        AlgebraicTree atanDiff = atanTree.derivative("x");
        atanDiff.setParameter("x", 1.0);
        assertEquals(0.5, atanDiff.eval().getElem(), EPSILON);
    }

    @Test
    public void testExponentialAndLogarithm() throws Exception {
        // exp(x) -> exp(x)
        AlgebraicTree expTree = new AlgebraicTree("exp(x)");
        AlgebraicTree expDiff = expTree.derivative("x");
        expDiff.setParameter("x", 2.0);
        assertEquals(Math.exp(2.0), expDiff.eval().getElem(), EPSILON);

        // log(x) -> 1 / x
        AlgebraicTree logTree = new AlgebraicTree("log(x)");
        AlgebraicTree logDiff = logTree.derivative("x");
        logDiff.setParameter("x", 3.0);
        assertEquals(1.0 / 3.0, logDiff.eval().getElem(), EPSILON);

        // sqrt(x) -> 1 / (2 * sqrt(x))
        AlgebraicTree sqrtTree = new AlgebraicTree("sqrt(x)");
        AlgebraicTree sqrtDiff = sqrtTree.derivative("x");
        sqrtDiff.setParameter("x", 9.0);
        assertEquals(1.0 / (2.0 * 3.0), sqrtDiff.eval().getElem(), EPSILON);
    }

    @Test
    public void testHyperbolicFunctions() throws Exception {
        // sinh(x) -> cosh(x)
        AlgebraicTree sinhTree = new AlgebraicTree("sinh(x)");
        AlgebraicTree sinhDiff = sinhTree.derivative("x");
        sinhDiff.setParameter("x", 1.0);
        assertEquals(Math.cosh(1.0), sinhDiff.eval().getElem(), EPSILON);

        // cosh(x) -> sinh(x)
        AlgebraicTree coshTree = new AlgebraicTree("cosh(x)");
        AlgebraicTree coshDiff = coshTree.derivative("x");
        coshDiff.setParameter("x", 1.0);
        assertEquals(Math.sinh(1.0), coshDiff.eval().getElem(), EPSILON);

        // tanh(x) -> 1 - tanh(x)^2
        AlgebraicTree tanhTree = new AlgebraicTree("tanh(x)");
        AlgebraicTree tanhDiff = tanhTree.derivative("x");
        tanhDiff.setParameter("x", 1.0);
        assertEquals(1.0 - Math.pow(Math.tanh(1.0), 2), tanhDiff.eval().getElem(), EPSILON);
    }

    @Test
    public void testPartialDerivativesMultiVariable() throws Exception {
        // f(x, y) = x^2 * y + y^3
        // df/dx = 2 * x * y
        // df/dy = x^2 + 3 * y^2
        AlgebraicTree tree = new AlgebraicTree("(x ^ 2.0) * y + (y ^ 3.0)");
        tree.setParameter("x", 2.0);
        tree.setParameter("y", 3.0);

        AlgebraicTree dfDx = tree.derivative("x");
        dfDx.setParameter("x", 2.0);
        dfDx.setParameter("y", 3.0);
        double expectedDfDx = 2.0 * 2.0 * 3.0; // 12.0
        assertEquals(expectedDfDx, dfDx.eval().getElem(), EPSILON);

        AlgebraicTree dfDy = tree.derivative("y");
        dfDy.setParameter("x", 2.0);
        dfDy.setParameter("y", 3.0);
        double expectedDfDy = Math.pow(2.0, 2) + 3.0 * Math.pow(3.0, 2); // 4 + 27 = 31.0
        assertEquals(expectedDfDy, dfDy.eval().getElem(), EPSILON);
    }

    @Test
    public void testHigherOrderDerivatives() throws Exception {
        // f(x) = sin(x)
        // f'(x) = cos(x)
        // f''(x) = -sin(x)
        AlgebraicTree f = new AlgebraicTree("sin(x)");
        AlgebraicTree f1 = f.derivative("x");
        AlgebraicTree f2 = f1.derivative("x");

        double xVal = 0.75;
        f2.setParameter("x", xVal);
        assertEquals(-Math.sin(xVal), f2.eval().getElem(), EPSILON);
    }

    @Test
    public void testVectorDerivative() throws Exception {
        // f(t) = (cos(t), sin(t), t)
        // f'(t) = (-sin(t), cos(t), 1.0)
        AlgebraicTree vectorTree = new AlgebraicTree("cos(t), sin(t), t");
        AlgebraicTree diff = vectorTree.derivative("t");
        assertNotNull(diff);

        double tVal = 0.5;
        diff.setParameter("t", tVal);
        StructureMatrix<Double> result = diff.eval();
        assertEquals(1, result.getDim());
        assertEquals(-Math.sin(tVal), result.getElem(0), EPSILON);
        assertEquals(Math.cos(tVal), result.getElem(1), EPSILON);
        assertEquals(1.0, result.getElem(2), EPSILON);
    }

    @Test
    public void testFiniteDifferenceComparison() throws Exception {
        String[] formulas = new String[]{
                "x ^ 3.0 - 2.0 * (x ^ 2.0) + 4.0 * x - 1.0",
                "sin(x) * exp(x)",
                "log(x ^ 2.0 + 1.0)",
                "tan(x / 2.0)",
                "sqrt(x ^ 2.0 + 4.0)",
                "(x + 1.0) / (x + 2.0)"
        };

        double[] testPoints = new double[]{0.5, 1.0, 1.5, 2.0};
        double h = 1e-6;

        for (String formula : formulas) {
            AlgebraicTree func = new AlgebraicTree(formula);
            AlgebraicTree derivativeTree = func.derivative("x");

            for (double x0 : testPoints) {
                // Analytical derivative from tree
                derivativeTree.setParameter("x", x0);
                double analytical = derivativeTree.eval().getElem();

                // Finite difference approximation: (f(x0 + h) - f(x0 - h)) / (2h)
                func.setParameter("x", x0 + h);
                double fPlus = func.eval().getElem();
                func.setParameter("x", x0 - h);
                double fMinus = func.eval().getElem();
                double numerical = (fPlus - fMinus) / (2.0 * h);

                assertEquals(numerical, analytical, 1e-4,
                        "Failed for formula: " + formula + " at x = " + x0 +
                                " (analytical: " + analytical + ", numerical: " + numerical + ")");
            }
        }
    }
}
