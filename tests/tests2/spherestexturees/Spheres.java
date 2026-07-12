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

package one.empty3.library.tests.tests2.spherestexturees;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.RepresentableConteneur;
import one.empty3.library.TextureCol;
import one.empty3.library.core.lighting.Colors;

import java.util.logging.Level;
import java.util.logging.Logger;

/*__
 * @author Manuel DAHMEN
 */
public class Spheres extends TestObjetSub {
    Point3D[] points;
    Point3D[] speed;
    int pointCount;
    double maxSpeed;
    double dimension;

    public Spheres(int n, double v, double dim) {
        pointCount = n;
        maxSpeed = v;
        dimension = dim;
    }

    public static void main(String[] args) {
        Spheres s = new Spheres(100000, 5, 100);
        s.setMaxFrames(10000);
        new Thread(s).start();
    }

    @Override
    public void ginit() {
        RepresentableConteneur representableConteneur = new RepresentableConteneur();
        points = new Point3D[pointCount];
        speed = new Point3D[pointCount];
        for (int i = 0; i < pointCount; i++) {
            points[i] = Point3D.random2(dimension);

            points[i].texture(new TextureCol(one.empty3.libs.Color.newCol((float)Math.random(),(float)Math.random(),(float)Math.random())));
            points[i].setNormale(Point3D.Z);
            representableConteneur.add(points[i]);

            speed[i] = Point3D.random2(maxSpeed);

        }
        Camera camera = new Camera(Point3D.Z.mult(-dimension * 2), Point3D.O0);
        scene().add(representableConteneur);
        scene().cameraActive(camera);

        Logger.getAnonymousLogger().log(Level.INFO, "" + representableConteneur);
    }

    public void bounce(int i) {
        points[i] = points[i].plus(speed[i]);
        if (points[i].getX() > dimension && speed[i].getX() > 0) {
            speed[i].setX(-speed[i].getX());
        }
        if (points[i].getX() < -dimension && speed[i].getX() < 0) {
            speed[i].setX(-speed[i].getX());
        }
        if (points[i].getY() > dimension && speed[i].getY() > 0) {
            speed[i].setY(-speed[i].getY());
        }
        if (points[i].getY() < -dimension && speed[i].getY() < 0) {
            speed[i].setY(-speed[i].getY());
        }
        if (points[i].getZ() > dimension && speed[i].getZ() > 0) {
            speed[i].setZ(-speed[i].getZ());
        }
        if (points[i].getZ() < -dimension && speed[i].getZ() < 0) {
            speed[i].setZ(-speed[i].getZ());
        }
    }

    @Override
    public void finit() throws Exception {
        for (int i = 0; i < pointCount; i++)
            bounce(i);
    }

}
