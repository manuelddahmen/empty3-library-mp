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

package one.empty3.library.tests.tests2.simula;/*package tests.simula;

import one.empty3.library.*;
import one.empty3.library.core.physics.Bille;
import one.empty3.library.core.physics.Force;
import one.empty3.library.core.testing.TestObjetSub;

import java.awt.*;

public class TestSimula extends TestObjetSub {
    private int N = 10;
    private ITexture tc = new TextureCol(Color.red);
    private double V = 0.03;
    private double D = 1;
    private TextureMov textureMov;
    private Point3D[] positions = new Point3D[N * N * N];
    private Point3D[] s;
    private Point3D[] v = new Point3D[N * N * N];
    private double masseMoyenne = 1000;
    private Bille[] billes;

    @Override
    public void ginit() {
        super.ginit();
        billes = new Bille[N * N * N];
        for (int x = 0; x < N; x++)
            for (int y = 0; y < N; y++)
                for (int z = 0; z < N; z++) {
                    int index = x * N * N + y * N + z;
                    positions[index] = Point3D.random2(D);
                    v[index] = Point3D.random2(V);

                    billes[index] = new Bille();
                    billes[index].position = positions[index];
                    billes[index].vitesse = v[index];
                    billes[index].color = new Color(
                            1.0f * x / N,
                            1.0f * y / N, 1.0f * z / N);
                    billes[index].masse = masseMoyenne / N / N / N;
                    billes[index].attraction = 10000;
                    billes[index].repulsion = 0.1;
                    billes[index].amortissement = 0.2;

                }

        force = new Force();

        force.setFusion(false);
        force.configurer(billes);
    }

    Force force;

    @Override
    public void finit() throws Exception {
        super.finit();
        force.calculer();

    }

    @Override
    public void testScene() throws Exception {
        scene().clear();
        super.testScene();
        for (int i = 0; i < N * N * N; i++) {
            Point3D p = billes[i].position;

            p.texture(new TextureCol(billes[i].color));

            scene().add(p);


        }
        scene().cameraActive(new Camera(Point3D.Z.mult(D), Point3D.O0));
    }

    public static void main(String[] args) {

        TestSimula t = new TestSimula();

        t.setResx(1800);
        t.setResy(1600);
        t.loop(true);
        t.setMaxFrames(10000);
        t.publishResult(true);
        t.setFileExtension("jpg");

        new Thread(t).start();

    }

}

*/