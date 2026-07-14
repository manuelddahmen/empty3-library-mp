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
package one.empty3.library.tests.tests2.repereAssocieAUneCourbeEX;


import one.empty3.library.Point3D;
import one.empty3.library.core.tribase.TRIObjetGenerateurAbstract;

/*__
 * @author manue_001
 */
public class TRITRINuage extends TRIObjetGenerateurAbstract {
    private final double dx;
    private final double dz;
    private final double densite;
    private final double dy;
    private final double taille;
    private Point3D[] p;

    public TRITRINuage(double dx, double dy, double dz, double densite, double taille) {
        this.dx = dx;
        this.dy = dy;
        this.dz = dz;
        this.densite = densite;
        this.taille = taille;

    }

    public static void main(String[] args) {

    }

    @Override
    public Point3D coordPoint3D(int x, int y) {
        double cx = 1.0 * x / getMaxX();
        double cy = 1.0 * y / getMaxY();

        if (p == null)
            p = new Point3D[getMaxX() * getMaxY()];

        if (p[y * getMaxX() + x] == null)
            p[y * getMaxX() + x] = new Point3D(Math.random() * dx, Math.random() * dy, Math.random() * dz);
        return p[y * getMaxX() + x];
    }


}
