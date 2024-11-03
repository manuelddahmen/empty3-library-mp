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

 Vous êtes libre de :

 */
package one.empty3.library.core.extra;

import one.empty3.*;
import one.empty3.library.*;

import java.awt.*;
import java.util.Random;

/*__
 * @author Manuel
 */
public class CollineModele3 extends Representable implements TRIGenerable {

    Random r = new Random();
    private TRIObject tris = new TRIObject();
    private String id;
    private Barycentre position;

    public CollineModele3(double altitudeMax) {
        int altMax = 10;
        int pMax = 100;

        Point3D p0 = position == null ? new Point3D(0d, 0d, 0d) : position.calculer(new Point3D(0d, 0d, 0d));
        Color c0 = new Color(128, 0, 255);

        Point3D[][] pA = new Point3D[altMax][pMax];

        Point3D[][] pAB = null;

        for (int alt = 1; alt < altMax; alt++) {
            for (int i = 0; i < pMax; i++) {
                Point3D[] p = new Point3D[3];

                p[0] = p0.plus(new Point3D(aleatoireSigne(alt),
                        aleatoireSigne(alt), 0d));
                p[1] = p[0].plus(new Point3D(aleatoireSigne(alt),
                        aleatoireSigne(alt), 0d));
                p[2] = p[1].plus(new Point3D(aleatoireSigne(alt),
                        aleatoireSigne(alt), 0d));

                p0 = p[2];

                pA[alt][i] = p0;

                TRI t = new TRI(p[0], p[1], p[2], c0);

                tris.add(t);

                if (alt > 1 & i > 0) {
                    tris.add(new TRI(pA[alt - 1][i - 1], pA[alt][i - 1],
                            pA[alt][i], c0));
                    tris.add(new TRI(pA[alt][i - 1], pA[alt - 1][i - 1],
                            pA[alt - 1][i], c0));
                }

            }

            c0 = new Color(128, 0, 255 - 10 * alt);

            // TRI t = new TRI(pA[alt][0].plus(new Point3D(0,-1,0)),
            // pA[alt][pMax/2].plus(new Point3D(0,-1,0)),
            // pA[alt][pMax-1].plus(new Point3D(0,-1,0)), c0);
            // tris.add(t);
            p0 = p0.plus(new Point3D(0d, 0d, -10d));
        }
    }

    private double aleatoireSigne(double alt) {
        return (r.nextInt(1000) - 499.5) / 1000.0 * 100.0 / alt / alt;
    }

    @Override
    public TRIObject generate() {
        return tris;
    }

    public Representable place(MODObjet aThis) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Barycentre position() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean supporteTexture() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public TextureCol texture() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return "colline()\n";
    }

}
