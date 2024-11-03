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

package one.empty3.feature.kmeansbinary;
/*
 * Programmed by Shephalika Shekhar
 * Class for Kmeans Clustering implemetation
 */

import one.empty3.feature.PixM;
import one.empty3.library.core.lighting.Colors;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class K_Clusterer extends ReadDataset {
    private static final int K = 2;
    static int k = 2;
    static Color[] colors = new Color[k];
    protected Map<double[], Integer> clustersPrint;
    protected Map<double[], Integer> clusters;
    public Map<Integer, double[]> centroids;
    protected Distance[] distances = new Distance[k];
    private int[] count = new int[k];
    protected int maxClusterSize = 0;

    public K_Clusterer() {
        super();
    }


    //main method
    public void process(File in, File inCsv, File out, int res) throws IOException {
        distances[0] = new DistanceFunctionBackground();
        distances[1] = new DistanceFunctionForeground();

        if (colors[0] == null)
            colors[0] = Color.YELLOW;
        if (colors[1] == null)
            colors[1] = Color.CYAN;

        final PixM pix;
        try {
            if (res > 0)
                pix = PixM.getPixM(new one.empty3.libs.Image(ImageIO.read(in)), res);
            else
                pix = new PixM(new one.empty3.libs.Image(ImageIO.read(in)));
            PixM pix2 = new PixM(
                    pix.getColumns(),
                    pix.getLines()
            );

            maxClusterSize = pix2.getColumns() * pix2.getLines();
            String fileCsv = inCsv.getAbsolutePath();
            features.clear();
            read(inCsv); //load data


            ReadDataset r1 = new ReadDataset();
            r1.features.clear();
            Logger.getAnonymousLogger().log(Level.INFO, "Enter the filename with path");
            r1.read(inCsv); //load data
            int ex = 1;
            clusters = new HashMap<>();
            centroids = new HashMap<>();
            do {
                final int k = K;
                //Scanner sc = new Scanner(System.in);
                int max_iterations = 100;//sc.nextInt();
                int distance = 1;//sc.nextInt();
                //Hashmap to store centroids with index
                // calculating initial centroids
                double[] x1 = new double[numberOfFeatures];
                int r = 0;
                for (int i = 0; i < k; i++) {

                    x1 = r1.features.get(r++);
                    centroids.put(i, x1);

                }
                //Hashmap for finding cluster indexes
                clusters = kmeans(distance, centroids);
                // initial cluster print
	/*	for (double[] key : clusters.keySet()) {
			for (int i = 0; i < key.length; i++) {
				System.out.print(key[i] + ", ");
			}
			System.out.print(clusters.get(key) + "\n");
		}
		*/
                double[] db = new double[numberOfFeatures];
                //reassigning to new clusters
                for (int i = 0; i < max_iterations; i++) {
                    for (int j = 0; j < k; j++) {
                        List<double[]> list = new ArrayList<>();
                        for (double[] key : clusters.keySet()) {
                            if (clusters.get(key) == j) {
                                list.add(key);
//					for(int x=0;x<key.length;x++){
//						System.out.print(key[x]+", ");
//						}
//					
                            }
                        }
                        db = centroidCalculator(j, list);
                        centroids.put(j, db);

                    }
                    clusters.clear();
                    clusters = kmeans(distance, centroids);

                }

                //final cluster print
                ////Logger.getAnonymousLogger().log(Level.INFO, "\nFinal Clustering of Data");
                ////Logger.getAnonymousLogger().log(Level.INFO, "Feature1\tFeature2\tFeature3\tFeature4\tCluster");
                for (double[] key : clusters.keySet()) {
                    for (int i = 0; i < key.length; i++) {
                        //System.out.print(key[i] + "\t \t");
                    }
                    ////System.out.print(clusters.get(key) + "\n");
                }

                //Calculate WCSS
                double wcss = 0;

                for (int i = 0; i < k; i++) {
                    double sse = 0;
                    for (double[] key : clusters.keySet()) {
                        if (clusters.get(key) == i) {
                            sse += Math.pow(distances[i].distance(key, centroids.get(i)), 2);
                        }
                    }
                    wcss += sse;
                }
                String dis = "";
                if (distance == 1)
                    dis = "Euclidean";
                else
                    dis = "Manhattan";
                Logger.getAnonymousLogger().log(Level.INFO, "\n*********Programmed by Shephalika Shekhar************\n*********Results************\nDistance Metric: " + dis);
                Logger.getAnonymousLogger().log(Level.INFO, "Iterations: " + max_iterations);
                Logger.getAnonymousLogger().log(Level.INFO, "Number of Clusters: " + k);
                Logger.getAnonymousLogger().log(Level.INFO, "WCSS: " + wcss);
                Logger.getAnonymousLogger().log(Level.INFO, "Press 1 if you want to continue else press 0 to exit..");
                ex = 0;//sc.nextInt();
            } while (ex == 1);

            for (int i = 0; i < k; i++)
                if (colors[i] == null) {
                    colors[i] = Colors.random();
                }

            clustersPrint = clusters;

            centroids.forEach((integer1, db1) -> clustersPrint.forEach((doubles, integer2) -> {
                pix2.setValues((int) (float) (doubles[0]), (int) (float) (doubles[1]),
                        K_Clusterer.colors[integer2].getRed(), K_Clusterer.colors[integer2].getGreen(),
                        K_Clusterer.colors[integer2].getBlue());

            }));

            ImageIO.write(pix2.normalize(0, 1).getImage(), "jpg", out);

        } catch (Exception ex1) {
            ex1.printStackTrace();
        }
    }

    //method to calculate centroids
    public double[] centroidCalculator(int id, List<double[]> a) {

        count[id] = 0;
        //double x[] = new double[ReadDataset.numberOfFeatures];
        double sum = 0.0;
        double[] centroids = new double[numberOfFeatures];
        for (int i = 0; i < numberOfFeatures; i++) {
            sum = 0.0;
            count[id] = 0;
            for (double[] x : a) {
                count[id]++;
                sum = sum + x[i];
            }
            centroids[i] = sum / count[id];
        }
        return centroids;
    }

    //method for putting features to clusters and reassignment of clusters.
    public Map<double[], Integer> kmeans(int distance, Map<Integer, double[]> centroids) {
        Map<double[], Integer> clusters = new HashMap<>();
        int k1 = 0;
        double dist = 0.0;
        for (double[] x : features) {
            double minimum = 999999.0;
            for (int j = 0; j < k; j++) {
                if (distance == 1) {
                    dist = distances[j].distance(centroids.get(j), x);
                } else if (distance == 2) {
                    dist = distances[j].distance(centroids.get(j), x);
                }
                if (dist < minimum) {
                    minimum = dist;
                    k1 = j;
                }

            }
            if (count[k1] < maxClusterSize / K)
                clusters.put(x, k1);
        }

        return clusters;
    }

}
