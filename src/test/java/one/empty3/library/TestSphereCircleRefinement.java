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
 *  * Created by $user $date
 *
 *
 */

package one.empty3.library;

import org.junit.Test;

import static org.junit.Assert.*;

public class TestSphereCircleRefinement {

    @Test
    public void testBasisCalculationStability() {
        // Create a Circle with an axis that is (1.0, 0.0, 0.0) which might cause issues
        // with the pRef (1.0, 0.0, 0.0) in the heuristic.
        Point3D center = new Point3D(0.0, 0.0, 0.0);
        Point3D axisVector = new Point3D(1.0, 0.0, 0.0);
        Axe axis = new Axe(center.plus(axisVector), center.moins(axisVector));
        Circle circle = new Circle(axis, 10.0);

        circle.calculerRepere1();

        // Assertions for orthonormality
        assertNotNull(circle.getVectX());
        assertNotNull(circle.getVectY());
        assertNotNull(circle.getVectZ());

        double dotXY = circle.getVectX().prodScalaire(circle.getVectY());
        double dotYZ = circle.getVectY().prodScalaire(circle.getVectZ());
        double dotXZ = circle.getVectX().prodScalaire(circle.getVectZ());

        assertEquals(0.0, dotXY, 1e-6);
        assertEquals(0.0, dotYZ, 1e-6);
        assertEquals(0.0, dotXZ, 1e-6);

        assertEquals(1.0, circle.getVectX().norme(), 1e-6);
        assertEquals(1.0, circle.getVectY().norme(), 1e-6);
        assertEquals(1.0, circle.getVectZ().norme(), 1e-6);
    }

    @Test
    public void testSphereOrientation() {
        Point3D center = new Point3D(0.0, 0.0, 0.0);
        // Axis vector along Z
        Point3D axisVector = new Point3D(0.0, 0.0, 1.0);
        Axe axis = new Axe(center.plus(axisVector), center.moins(axisVector));
        Sphere sphere = new Sphere(axis, 10.0);

        // The sphere's equator is the circle. Its normal should be aligned with the axis.
        // We can check a point on the sphere (e.g. u=0, v=0.5 -> equator)
        Point3D point = sphere.calculerPoint3D(0.0, 0.5);

        // At equator, the point should be at (10, 0, 0) if oriented correctly (axis in Z)
        // Actually, depending on the basis construction, it might be different, 
        // but it should be orthogonal to the axis vector.

        // Axis is along Z, so point should be in XY plane
        assertEquals(0.0, point.get(2), 1e-6);
        assertEquals(10.0, point.norme(), 1e-6);
    }
}
