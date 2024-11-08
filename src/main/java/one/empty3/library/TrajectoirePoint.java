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
package one.empty3.library;


import one.empty3.libs.Image;

public class TrajectoirePoint implements Trajectoire {

    private Point3D[] points;
    private int ind = 0;
    private double indD = 0;
    private double[] duree;
    private double d = 0.0;
    private int nbrFrames = 0;
    private int FPS = 25;
    private int f = 0;
    private double t = 0;
    private double tDebut;
    private double tFin;

    public TrajectoirePoint(Point3D[] point, double[] duree) {
        this.points = points;
        this.duree = duree;

        for (int i = 0; i < duree.length; i++) {
            d += duree[i];
        }
        nbrFrames = (int) d * FPS;
        indD = duree[0];

    }

    public boolean asuivant() {
        if (ind >= points.length - 1) {
            return false;
        }

        return t <= tFin;
    }

    public int frame() {
        return f;
    }

    public void frame(int f) {
        this.f = f;
    }

    public Point3D point() {
        double dureeEcoulee = t - tDebut;
        if (dureeEcoulee > indD) {
            ind++;
            indD += duree[ind];
        }

        t += 1.0 / FPS;
        indD += 1.0 / FPS;

        return points[ind + 1].mult(indD - t).plus(points[ind].mult(duree[ind] - (indD - t)));
    }

    /*__
     * *
     * * DEPRECATED
     */
    public double t() {
        return 0.0;
    }

    /*__
     * *
     * * DEPRECATED
     */
    public void t(double t) {

    }

    public double tDebut() {
        return tDebut;
    }

    public void tDebut(double t) {
        this.tDebut = tDebut;
        this.t = tDebut;
    }

    public double tFin() {
        return tDebut;
    }

    public void tFin(double t) {
        this.tFin = tFin;
    }

}
