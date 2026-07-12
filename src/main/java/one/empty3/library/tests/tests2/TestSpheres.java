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

package one.empty3.library.tests.tests2;

import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.TextureCol;
import one.empty3.library.core.lighting.Colors;
import one.empty3.library.core.testing.TestObjet;
import one.empty3.library.core.tribase.TRISphere;

/*__
 * Created by Manuel Dahmen on 05-02-16.
 */
public class TestSpheres extends TestObjet {
    double i = 0.2, j = 0.2, k = 0.2;
    private double RMax = 0.01;
    private int NUMBER = 400;
    TRISphere[] arraySp = new TRISphere[NUMBER];
    private TRISphere triSphereCamEye = new TRISphere(Point3D.X, 0.2);
    private TRISphere triSphereCamLookAt = new TRISphere(Point3D.O0, 1.0);
    private double[] limits = new double[]{-1, 1, -1, 1, -1, 1};
    private double D = 1.0;
    private double VMoy = 0.1;

    public static void main(String[] args) {
        TestSpheres t = new TestSpheres();
        t.setMaxFrames(3600);
        new Thread(t).start();
    }

    @Override
    public void afterRenderFrame() {

    }

    @Override
    public void finit() {
        double[] arr = ploc(0.5, 2.0, frame() * 1.0 / getMaxFrames());
        int incr = 0;
        Point3D v = Point3D.O0;
        triSphereCamEye.setCentre(triSphereCamEye.getCentre().plus(Point3D.random2(j)));
        triSphereCamLookAt.setCentre(triSphereCamLookAt.getCentre().plus(Point3D.random2(k)));

        for (TRISphere triSphere : arraySp) {
            triSphere.setCentre(triSphere.getCentre().plus(Point3D.random2(i).mult(VMoy)));
            triSphere.setRadius(arr[incr] * RMax);
            incr++;
            Point3D s = triSphere.getCentre();
            bounce(s, v);
        }
        bounce(triSphereCamEye.getCentre(), v);
        bounce(triSphereCamLookAt.getCentre(), v);
    }

    private void bounce(Point3D s, Point3D v) {
        if (s.getX() > 1 * D || s.getX() < -1 * D) {
            s.setX(-s.getX());
        }
        if (s.getX() > 1 * D && s.getX() < -1 * D) {
            s.setX(-s.getX());
        }
        if (s.getY() > 1 * D || s.getY() < -1 * D) {
            s.setY(-s.getY());
        }
        if (s.getY() > 1 * D && s.getY() < -1 * D) {
            s.setY(-s.getY());
        }
        if (s.getZ() > 1 * D || s.getZ() < -1 * D) {
            s.setZ(-s.getZ());
        }
        if (s.getZ() > -1 * D && s.getZ() < -1 * D) {
            s.setZ(-s.getZ());
        }
    }

    @Override
    public void ginit() {
        int i = 0;
        for (i = 0; i < NUMBER; i++) {
            arraySp[i] = new TRISphere(Point3D.random2(RMax), Math.random() * RMax);
            arraySp[i].texture(new TextureCol(Colors.random()));
            scene().add(arraySp[i]);
        }
        scene().cameraActive(new Camera(triSphereCamEye.getCentre(), triSphereCamLookAt.getCentre()));

    }

    private double[] ploc(double min, double max, double t) {
        double[] arr = new double[NUMBER];
        for (int i = 0; i < NUMBER; i++) {
            arr[i] = ((Math.sin(t + i * Math.PI * 2.0 / NUMBER)) * (Math.abs(max) + 1)) + min; // NON MANU -- MANU
        }
        return arr;
    }

    @Override
    public void afterRender() {

    }

    @Override
    public void testScene() throws Exception {

    }

}
