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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package one.empty3.library.tests.tests2.courbes_bsplines;

import one.empty3.library.Point3D;
import one.empty3.library.TextureCol;

import java.awt.*;

/*__
 * Test BAD UGLY COMME TA MERE
 *
 * Meta Description missing
 * @author Manuel Dahmen dathewolf@gmail.com
 */
public class TestsBSpline {
    public static double[] u(int N) {
        double[] u = new double[N];
        int cpt = 0;
        int cpt0 = cpt;
        for (int i = cpt0; i < N / 3 + cpt0; i++) {
            u[i] = 1.0;

            cpt++;
        }
        cpt0 = cpt;
        for (int i = N / 3; i < 2 * N / 3; i++) {
            u[i] = 1.0 * i / N;

            cpt++;
        }
        cpt0 = cpt;
        for (int i = 2 * N / 3; i < N; i++) {
            u[i] = 1.0;

            cpt++;
        }
        return u;
    }

    public static Point3D[] p(int N) {
        Point3D[] p = new Point3D[N];
        for (int i = 0; i < N; i++) {
            p[i] = new Point3D(1.0 * i * Math.cos((i / 6) * (Math.PI * 2.0) * ((double) i) / N),
                    1.0 * i * Math.sin((i / 6) * (Math.PI * 2.0) * ((double) i) / N), 0d);
            p[i].texture(new TextureCol(Color.WHITE));
        }
        return p;
    }

    public static Point3D[] p2(int N) {
        Point3D[] p = new Point3D[N];
        for (int i = 0; i < N; i++) {
            p[i] = new Point3D(1.0 * i * Math.cos((i / 6.0) * (Math.PI * 2.0) * ((double) i) / N),
                    1.0 * i * Math.sin((i / 6.0) * (Math.PI * 2.0) * ((double) i) / N), 0d);
            p[i].texture(new TextureCol(Color.WHITE));
        }
        return p;
    }
}

