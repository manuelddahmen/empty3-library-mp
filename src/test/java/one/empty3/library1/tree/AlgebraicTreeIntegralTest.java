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

import static org.junit.jupiter.api.Assertions.*;

public class AlgebraicTreeIntegralTest {

    private static final double EPSILON = 1e-3;

    @Test
    public void testScalarDefiniteIntegralPolynomial() throws Exception {
        // \int_0^2 x^2 dx = [x^3 / 3]_0^2 = 8 / 3 \approx 2.6667
        AlgebraicTree tree = new AlgebraicTree("x ^ 2.0");
        tree.construct();
        double result = tree.integrate("x", 0.0, 2.0);
        assertEquals(8.0 / 3.0, result, EPSILON);
    }

    @Test
    public void testScalarDefiniteIntegralTrig() throws Exception {
        // \int_0^\pi \sin(x) dx = 2.0
        AlgebraicTree tree = new AlgebraicTree("sin(x)");
        tree.construct();
        double result = tree.integrate("x", 0.0, Math.PI);
        assertEquals(2.0, result, EPSILON);
    }

    @Test
    public void testVectorDefiniteIntegral() throws Exception {
        // \int_0^1 [t, t^2] dt = [0.5, 1/3]
        AlgebraicTree tree = new AlgebraicTree("t, t ^ 2.0");
        tree.construct();
        StructureMatrix<Double> result = tree.integrateVector("t", 0.0, 1.0);
        assertNotNull(result);
        assertEquals(1, result.getDim());
        assertEquals(0.5, result.getElem(0), EPSILON);
        assertEquals(1.0 / 3.0, result.getElem(1), EPSILON);
    }

    @Test
    public void testIterated2DIntegral() throws Exception {
        // \int_0^1 \int_0^1 (x + y) dx dy = 1.0
        AlgebraicTree tree = new AlgebraicTree("x + y");
        tree.construct();
        double result = tree.integrate2D("x", 0.0, 1.0, "y", 0.0, 1.0);
        assertEquals(1.0, result, EPSILON);
    }

    @Test
    public void testAntiderivative() throws Exception {
        AlgebraicTree tree = new AlgebraicTree("x ^ 2.0");
        tree.construct();
        AlgebraicTree anti = tree.antiderivative("x");
        assertNotNull(anti);
        anti.setParameter("x", 3.0);
        // Antiderivative of x^2 is (1/3) * x^3 (or similar expression)
        double val = anti.eval().getElem();
        assertEquals(9.0, val, EPSILON); // (3^3)/3 = 9.0
    }
}
