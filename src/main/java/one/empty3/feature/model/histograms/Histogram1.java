/*
 * Copyright (c) 2024.
 *
 *
 *  Copyright 2023 Manuel Daniel Dahmen
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package one.empty3.feature.model.histograms;

import one.empty3.io.ProcessFile;
import one.empty3.feature.PixM;
import one.empty3.library.Point3D;



import java.io.File;

/*
 * radial density of region (x, y, r)
 * by mean or mean square or somewhat else.
 */
public class Histogram1 extends ProcessFile {

    private int kMax = 3;
    private double fractMax = 0.2;

    public class Circle {
        public double x, y, r;
        public double i;
        public Point3D maxColor;

        public Circle(double x, double y, double r) {
            this.x = x;
            this.y = y;
            this.r = r;
        }

        @Override
        public String toString() {
            return "Circle{" +
                    "x=" + x +
                    ", y=" + y +
                    ", r=" + r +
                    ", i=" + i +
                    "n maxColor = p " + maxColor.toString() +
                    '}';
        }
    }

    //private final int[][][] levels;


    public void makeHistogram(double r) {

    }

    public double nPoints(int x, int y, int w, int h) {
        return 0.0;
    }

    public Circle getLevel(Circle c, PixM m) {
        // I mean. Parcourir le cercle
        // mesurer I / numPoints
        // for(int i=Math.sqrt()
        //  return c;
        double sum = 0;
        int count = 0;
        c.maxColor = Point3D.O0;
        double intensity = 0.0;
        for (double i = c.x - c.r; i <= c.x + c.r; i++) {
            for (double j = c.y - c.r; j <= c.y + c.r; j++) {
                if (Math.sqrt((i - c.x) * (i - c.x) + (j - c.y) * (j - c.y)) <= c.r
                        && c.x - c.r >= 0 && c.y - c.r >= 0 && c.x + c.r < m.getColumns() && c.y + c.r < m.getLines()) {
                    intensity += m.getIntensity((int) i, (int) j);
                    count++;
                    Point3D p = m.getP((int) i, (int) j);
                    if (p.norme() > 0.3 && p.moins(c.maxColor).norme() > 0.3) {
                        c.maxColor = p;
                        sum++;
                    }
                }
            }
        }
        c.maxColor = c.maxColor.mult(1 / (sum + 1));
        if (count > 0)
            c.i = intensity / count;
        else {
            c.i = 0.0;
            c.r = 1;
        }


        return c;
    }

    public Circle getLevel(Circle c, PixM m, double r0) {
        // I mean. Parcourir le cercle
        // mesurer I / numPoints
        // for(int i=Math.sqrt()
        //  return c;
        int count = 0;
        double intensity = 0.0;
        double sum = 0.0;
        c.maxColor = Point3D.O0;
        for (double i = c.x - c.r; i <= c.x + c.r; i++) {
            for (double j = c.y - c.r; j <= c.y + c.r; j++) {
                if (Math.sqrt((i - c.x) * (i - c.x) + (j - c.y) * (j - c.y)) <= c.r
                        && Math.sqrt((i - c.x) * (i - c.x) + (j - c.y) * (j - c.y)) >= r0
                        && c.x - c.r >= 0 && c.y - c.r >= 0 && c.x + c.r < m.getColumns() && c.x + c.r < m.getLines()) {
                    intensity += m.getIntensity((int) i, (int) j);
                    count++;
                    if (m.getP((int) i, (int) j).norme() > c.maxColor.norme()) {
                        c.maxColor = m.getP((int) i, (int) j);
                        sum++;
                    }
                }
            }
        }
        c.maxColor = c.maxColor.mult(1 / (sum + 1));

        if (count > 0)
            c.i = intensity / count;
        else {
            c.i = 0.0;
            c.r = 1;
        }


        return c;
    }

    @Override
    public boolean process(File in, File out) {
        PixM inP;
        inP = PixM.getPixM(new one.empty3.libs.Image(in), maxRes);
        PixM outP = inP.copy();
        double maxR = Math.min(inP.getLines(), inP.getColumns()) * fractMax;
        for (int i = 0; i < inP.getColumns(); i++) {
            for (int j = 0; j < inP.getLines(); j++) {
                double maxIJ = 0.0;
                int rIJ = 3;
                Circle cc = null;
                for (int k = 3; k < maxR; k += this.kMax) {
                    Circle c = getLevel(new Circle(i, j, k), inP);
                    double candidateI = c.i;
                    double candidateR = c.r;
                    if (cc == null)
                        cc = c;
                    if (candidateI > maxIJ) {
                        maxIJ = candidateI;
                        rIJ = k;
                        cc = c;
                    }
                }

                Point3D intensityColor = Point3D.n(maxIJ, maxIJ, maxIJ).mult(maxIJ * maxR / rIJ).multDot(
                        inP.getP(i, j));
                if(cc!=null)
                    outP.setP(i, j, cc.maxColor);
            }
        }

// Colorier en fonction des pixels voisins
//        Circle c2 = getLevel(cc, inP, cc.r/2);

        outP.getImage().saveFile(out);

        return true;

    }

}
