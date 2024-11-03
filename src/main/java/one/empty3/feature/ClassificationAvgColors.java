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

package one.empty3.feature;

import one.empty3.io.ProcessFile;
import one.empty3.library.ECImage;
import one.empty3.library.Point3D;

import javax.imageio.ImageIO;

import one.empty3.libs.Image;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class ClassificationAvgColors extends ProcessFile {
    public ClassificationAvgColors() {

    }

    @Override
    public boolean process(File in, File out) {
        KMeans classification = new KMeans();
        classification.process(in, out);
        try {
            // Processed by "classification
            // Non filtered image
            Image original = new ECImage(ImageIO.read(in));
            PixM pixMoriginal = new PixM(original);
            PixM toProcess = new PixM(original);
            Map<Integer, double[]> c = classification.k_clusterer.centroids;
            Map<Integer, Point3D> sum = new HashMap<>();
            Map<Integer, Integer> count = new HashMap<>();
            // Faire les moyennes des points de même groupe
            c.forEach(new BiConsumer<Integer, double[]>() {
                @Override
                public void accept(Integer integer, double[] doubles) {
                    sum.putIfAbsent(integer, Point3D.O0);
                    count.putIfAbsent(integer, 0);
                    sum.put(integer, sum.get(integer).plus(pixMoriginal.getP((int) doubles[0], (int) doubles[1])));
                    count.put(integer, count.get(integer) + 1);
                }
            });
            Point3D[] ps = new Point3D[c.keySet().size()];
            c.forEach(new BiConsumer<Integer, double[]>() {
                @Override
                public void accept(Integer integer, double[] doubles) {
                    Integer countI = count.get(integer);
                    Point3D sumI = sum.get(integer);
                    sumI = sumI.mult(1. / countI);
                    ps[integer] = sumI;
                }
            });
            c.forEach(new BiConsumer<Integer, double[]>() {
                @Override
                public void accept(Integer integer, double[] doubles) {
                    Integer countI = count.get(integer);
                    Point3D sumI = sum.get(integer);
                    sumI = sumI.mult(1. / countI);
                    for (int c = 0; c < 3; c++)
                        doubles[c + 2] = ps[integer].get(c);

                    toProcess.setP((int) (doubles[0]), (int) (doubles[1]),
                            sumI);
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }


        return true;
    }
}
