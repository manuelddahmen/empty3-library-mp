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

package one.empty3.library.tests.tests2.film_1;

import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.TextureCol;
import one.empty3.library.core.lighting.Colors;
import one.empty3.library.core.move.Trajectoires;
import one.empty3.apps.testobject.TestObjet;
import one.empty3.library.core.tribase.TRISphere;
import one.empty3.libs.Color;

import java.util.ArrayList;

/*__
 * Meta Description missing
 * @author Manuel Dahmen dathewolf@gmail.com
 */
public class AnneauDeSpheresQuiTournent extends TestObjet {

    private final int NOMBRE = 6;
    private final double PCPS = 0.1;
    private final ArrayList<TRISphere> spheres = new ArrayList<TRISphere>();
    private TRISphere s1;
    private TRISphere s2;

    {
        description = "Satan me pare de son auréole noire";
    }

    public static void main(String[] args) {
        AnneauDeSpheresQuiTournent s = new AnneauDeSpheresQuiTournent();

        s.loop(true);
        s.setMaxFrames(6000);
        s.setGenerate(GENERATE_IMAGE | GENERATE_MOVIE);
        new Thread(s).start();
    }

    private double longiI(int i) {
        return (i * 1.0 / NOMBRE) + (1.0) * frame() / 25. * PCPS;
    }

    private double latI(int i) {
        return 0;
    }

    private double RI(int i) {
        return 1000;
    }

    private double longiC() {
        return 0;
    }

    private double latC() {
        return 1.0 * frame() / 25. * PCPS;
    }

    private double RC() {
        return 5000;
    }

    @Override
    public void afterRenderFrame() {
    }

    @Override
    public void finit() {
    }

    @Override
    public void ginit() {
        /*s1 = new TRISphere(Point3D.X.mult(1000), 100);
         s2 = new TRISphere(Point3D.X.mult(-1000), 100);

         s1.texture(new TextureCol(one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f)));
         s2.texture(new TextureCol(one.empty3.libs.Color.newCol(0.0f,0.0f,0.0f)));

         scene().add(s1);
         scene().add(s2);
         */
        for (int i = 0; i < NOMBRE; i++) {
            s1 = new TRISphere(Point3D.X.mult(1000d), 100d);
            s1.texture(new TextureCol(
                    Color.newCol(0.f,1.f,1.0f)));
            spheres.add(s1);
            scene().add(s1);
        }

        scene().cameraActive(new Camera(Point3D.Z.mult(RC()), Point3D.O0));
    }

    @Override
    public void testScene() throws Exception {
        for (int i = 0; i < NOMBRE; i++) {
            s1 = spheres.get(i);
            Point3D centre = s1.getCentre();
            centre.texture(new TextureCol(Color.newCol(1f,0.4f,0.4f)));
            scene().add(centre);
            s1.setCentre(Trajectoires.sphere(longiI(i), latI(i), RI(i)));
        }
        scene().cameraActive().setEye(Trajectoires.sphere(longiC(), latC(), RC()));
        scene().cameraActive().calculerMatrice(null);
    }

    public void afterRender() {
    }

    @Override
    public void publishResult() {

    }
}
