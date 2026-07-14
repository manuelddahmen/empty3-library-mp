/*
 *
 *  * Copyright (c) 2024. Manuel Daniel Dahmen
 *  *
 *  *
 *  *    Copyright 2024 Manuel Daniel Dahmen
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 *
 */

package one.empty3.library.tests.test3;

import one.empty3.library.Point3D;
import one.empty3.library.StructureMatrix;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertTrue;


/*__
 * Created by manue on 07-09-19.
 */
public class StructureMatrixTest {
    @BeforeAll
    public void setUp() throws Exception {
    }

    @AfterAll
    public void tearDown() throws Exception {
    }

    @Test
    public void setElem() throws Exception {
        StructureMatrix<Double> tab1 = new StructureMatrix<>(1, Double.class);
        tab1.setElem(0.3, 0);
        tab1.setElem(1.3, 1);
        tab1.setElem(2.3, 2);
        assertTrue(tab1.getElem(0) == 0.3 && tab1.getElem(1) == 1.3 && tab1.getElem(2) == 2.3);
    }

    @Test
    public void setElem1() throws Exception {
    }

    @Test
    public void setElem2() throws Exception {
    }

    @Test
    public void getElem() throws Exception {
    }

    @Test
    public void getElem1() throws Exception {
    }

    @Test
    public void getElem2() throws Exception {
    }

    @Test
    public void getElem3() throws Exception {
    }

    @Test
    public void getData0d() throws Exception {
    }

    @Test
    public void getData1d() throws Exception {
    }

    @Test
    public void getData2d() throws Exception {
    }

    @Test
    public void insert() throws Exception {
    }

    @Test
    public void delete() throws Exception {
    }

    @Test
    public void insert1() throws Exception {
    }

    @Test
    public void add() throws Exception {
    }

    @Test
    public void addRow() throws Exception {
    }

    @Test
    public void array2d() throws Exception {
        Point3D[][] coeff = new Point3D[5][5];
        for (int x = -2
             ; x <= 2; x++)
            for (int y = -2; y <= 2
                    ; y++)
                for (int z = -2; z < 3; z++) {
                    coeff[x + 2][y + 2] = Point3D.n(2. - x, 2. - y, 0.);
                } /*
         coeff = new Point3D[x+2][y+2]{
                , Point3D.n(2, -1, 0), Point3D.n(2, 0, 0), Point3D.n(2, 1, 0), Point3D.n(2, 2, 0)},
                {Point3D.n(1, -2, 0), Point3D.n(1, -1, 0), Point3D.n(1, 0, 0), Point3D.n(1, 1, 0), Point3D.n(1, 2, 0)},
                {Point3D.n(0, -2, 0), Point3D.n(0, -1, 0), Point3D.n(0, 0, 0), Point3D.n(0, 1, 0), Point3D.n(0, 2, 0)},
                {Point3D.n(-1, -2, 0), Point3D.n(-1, -1, 0), Point3D.n(-1, 0, 0), Point3D.n(-1, 1, 0), Point3D.n(-1, 2, 0)},
                {Point3D.n(-2, -2, 0), Point3D.n(-2, -1, 0), Point3D.n(-2, 0, 0), Point3D.n(-2, 1, 0), Point3D.n(-2, 2, 0)}
        };*/
        StructureMatrix<Point3D> matrix = new StructureMatrix<>(2, Point3D.class);
        matrix.setAll(coeff);
        for (int i = 0; i < coeff.length; i++)
            for (int j = 0; j < coeff[i].length; j++)
                assertTrue(coeff[i][j].equals(matrix.getElem(i, j)));
        for (int i = 0; i < matrix.getData2d().size(); i++)
            for (int j = 0; j < matrix.getData2d().get(i).size(); j++) {
                assertTrue(coeff[i][j].equals(matrix.getElem(i, j)));
                Logger.getAnonymousLogger().log(Level.INFO, coeff[i][j].toString());
            }

    }

    @Test
    public void point3D() {
        double d1 = 0.1;
        double d2 = 0.2;
        double d3 = 0.3;
        Point3D p1 = Point3D.n(d1, d2, d3);
        Point3D p2 = Point3D.n(d1, d2, d3);
        assertTrue(p1.get(0) == d1 && p1.get(1) == d2 && p1.get(2) == d3);
        assertTrue(p2.get(0) == d1 && p2.get(1) == d2 && p2.get(2) == d3);
        assertTrue(p1.equals(p1));
        assertTrue(p2.equals(p1));
        assertTrue(p1.plus(p2).equals(p1.mult(2.0)));
    }
}
