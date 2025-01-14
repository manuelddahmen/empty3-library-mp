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

package one.empty3.feature_2;

import java.io.File;
import java.util.ArrayList;

public abstract class ProcessEval {
    private int width = 24;
    private int height = 24;
    private ArrayList<File> files = new ArrayList<>();
    private ArrayList<matrix.PixM> pixMaps = new ArrayList<>();
    private ArrayList<double[]> features = new ArrayList<>();
    private double[] x;
    private double[] y;
    private double[] w;
    private double[] w1;
    private double[] b;
    private double[][] candidatesVectors;
    private double[][] detectedAsPositives;
    private double[][] detectedAsNegatives;

    public ProcessEval(int width, int height) {

    }
    // y values  -1 à 1
    // xi = pixels

    /***
     * f(x) = w . x  + b // Hyperplane separating POS and NEG
     * w'= SOMME ( d'i yi xi)
     *f(x) = SOMME (d'yi * (xi . x ) + b
     * w' = SOMME (d'i yi xi)
     *
     * f(x) = SOMME (d'i yi (phi(xi) . phi(x)) + B
     *
     *
     */
    public void addFile(File file) {
        files.add(file);
    }

    public void addFiles(File[] files) {
        for (int i = 0; i < files.length; i++) {
            this.files.add(files[i]);
        }
    }

    public void setFeature(double[] f) {
        this.features.add(f);
    }

    private double[] add(double[] a, double[] b) {
        double[] c = new double[x.length];
        for (int i = 0; i < a.length; i++)
            c[i] = a[i] + b[i];
        return a;
    }

    private double[] dotOuter(double[] x, double[] w) {
        double[] a = new double[x.length];
        for (int i = 0; i < a.length; i++)
            a[i] = x[i] * w[i];
        return a;
    }

    public double matchFeature(double[] x, double[] f) {
        double sumFeaturing = 0;
        for (int i = 0; i < x.length; i++) {
            sumFeaturing += f[i] * x[i];
        }
        double sumWithoutFeaturing = 0;
        for (int i = 0; i < x.length; i++) {
            sumWithoutFeaturing += f[i] * x[i];
        }
        return sumFeaturing - sumWithoutFeaturing;
    }
}
