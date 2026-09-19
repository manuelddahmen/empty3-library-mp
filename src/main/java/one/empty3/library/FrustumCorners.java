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

import java.util.List;

public class FrustumCorners {

    public final Point3D nearTopLeft;
    public final Point3D nearTopRight;
    public final Point3D nearBottomRight;
    public final Point3D nearBottomLeft;

    public final Point3D farTopLeft;
    public final Point3D farTopRight;
    public final Point3D farBottomRight;
    public final Point3D farBottomLeft;

    public FrustumCorners(
            Point3D eye,
            Point3D forward,
            Point3D vertical,
            double angleX,
            double angleY,
            double near,
            double far
    ) {
        forward = forward.norme1();

        // Axe horizontal de la caméra.
        Point3D right = forward.prodVect(vertical).norme1();

        // Axe vertical corrigé.
        Point3D up = right.prodVect(forward).norme1();

        // Sommets du plan proche.
        Point3D[] nearCorners = calculatePlaneCorners(
                eye,
                forward,
                right,
                up,
                angleX,
                angleY,
                near
        );

        // Sommets du plan lointain.
        Point3D[] farCorners = calculatePlaneCorners(
                eye,
                forward,
                right,
                up,
                angleX,
                angleY,
                far
        );

        nearTopLeft = nearCorners[0];
        nearTopRight = nearCorners[1];
        nearBottomRight = nearCorners[2];
        nearBottomLeft = nearCorners[3];

        farTopLeft = farCorners[0];
        farTopRight = farCorners[1];
        farBottomRight = farCorners[2];
        farBottomLeft = farCorners[3];
    }

    private static Point3D[] calculatePlaneCorners(
            Point3D eye,
            Point3D forward,
            Point3D right,
            Point3D up,
            double angleX,
            double angleY,
            double depth
    ) {
        double halfWidth = depth * Math.tan(angleX);
        double halfHeight = depth * Math.tan(angleY);

        Point3D center = eye.plus(
                forward.mult(depth)
        );

        Point3D horizontal = right.mult(halfWidth);
        Point3D vertical = up.mult(halfHeight);

        Point3D topLeft = center
                .moins(horizontal)
                .plus(vertical);

        Point3D topRight = center
                .plus(horizontal)
                .plus(vertical);

        Point3D bottomRight = center
                .plus(horizontal)
                .moins(vertical);

        Point3D bottomLeft = center
                .moins(horizontal)
                .moins(vertical);

        return new Point3D[]{
                topLeft,
                topRight,
                bottomRight,
                bottomLeft
        };
    }

}