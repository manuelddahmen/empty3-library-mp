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

package one.empty3.library.core;

import one.empty3.*;
import one.empty3.library.*;

import java.awt.*;

/*__
 * Created by manue on 23-11-19.
 */
public class CameraArcBall {

/*__
 * Created by manue on 06-11-19.
 */
    private final ZBufferImpl zBuffer;
    private Point3D pointCenter;
    private double radius;
    private Point3D currentPosition;
    private Camera currentCamera;
    private double lastX, lastY;
    private Representable representable;

    /*__
     *
     * @param ray origin: camera.eye ; extrem : vector dir
     * @return t :: orig+t*v
     */
    public Point3D  intersect(Axe ray)
    {
        double t0, t1; // solutions for t if the ray intersects
        // geometric solution
        Point3D L = pointCenter.moins(ray.getP1().getElem());
        double tca = L.prodScalaire(ray.getVector());
        // if (tca < 0) return false;
        double d2 = L.prodScalaire(L) - tca * tca;
        if (d2 > radius) throw new NullPointerException();
        double thc = Math.sqrt(radius*radius - d2);
        t0 = tca - thc;
        t1 = tca + thc;
        if (t0 > t1) {
            double tmp = t0;
            t0 = t1;
            t1 = tmp;
        }

        if (t0 < 0) {
            t0 = t1; // if t0 is negative, let's use t1 instead
            if (t0 < 0) throw new NullPointerException();
        }

        double t = t0;

        return ray.getP1().getElem().plus(ray.getVector().mult(t));
    }
    public CameraArcBall(Camera camera, Point3D point, double radius, ZBufferImpl zBuffer)
    {
        currentCamera = camera;
        pointCenter = point;
        this.radius = radius;
        this.zBuffer = zBuffer;
    }

    public void init(Representable representable) {
        this.representable = representable;
        Point p = currentCamera.coordonneesPoint2D(pointCenter, zBuffer);
        lastX = p.getX();
        lastY = p.getY();

    }
    public void moveTo(int x, int y) {
        Point3D mult = zBuffer.invert(new Point3D((double) x, (double) y, 0.0), currentCamera, 1.0);//??
        Point3D intersect = intersect(new Axe(currentCamera.eye(), mult));
        computeMatrix(pointCenter, currentPosition, intersect);
        currentPosition = intersect;
    }

    Matrix33 rot;

    public void computeMatrix(Point3D p0, Point3D intersect1, Point3D intersect2)
    {
        if(p0!=null && intersect1!=null && intersect2!=null)
        {
            Matrix33 matrix33 = representable.getRotation().getElem().getRot().getElem();
            if(!currentPosition.equals(pointCenter) && !intersect1.equals(intersect2)) {
                Point3D vY = intersect1.moins(p0).prodVect(intersect2.moins(p0)).norme1();
                Point3D mvZ = currentPosition.moins(pointCenter).norme1();
                Point3D vX = vY.prodVect(mvZ).norme1();


                rot = new Matrix33(new Point3D[]{vX, vY, mvZ});
            }

        }
    }
    public Matrix33 matrix()
    {
        return rot;
    }
}
