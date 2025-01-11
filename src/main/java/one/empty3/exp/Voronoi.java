/*
 *
 *  *
 *  *  * Copyright (c) 2025. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2024 Manuel Daniel Dahmen
 *  *  *
 *  *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *  *    you may not use this file except in compliance with the License.
 *  *  *    You may obtain a copy of the License at
 *  *  *
 *  *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *  *
 *  *  *    Unless required by applicable law or agreed to in writing, software
 *  *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  *    See the License for the specific language governing permissions and
 *  *  *    limitations under the License.
 *  *
 *  *
 *
 *
 *
 *  * Created by $user $date
 *
 *
 */

package one.empty3.exp;

import one.empty3.feature.PixM;
import one.empty3.io.ProcessFile;
import one.empty3.library.Point3D;
import one.empty3.libs.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Voronoi extends ProcessFile {
    private ArrayList<Point3D> pointsA = new ArrayList<>();
    private ArrayList<Point3D> pointsB = new ArrayList<>();

    public void processTest() {
        try {
            PixM mIn = new PixM(2000, 2000);
            PixM mOut = new PixM(mIn.getColumns(), mIn.getLines());

            initPixMmIn(mIn);

            mOut = voronoi(mIn, mOut);
            Image image = mOut.getImage();
            image.saveToFile("results/voronoi.jpg");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initPixMmIn(PixM mIn) {
        for (int i = 0; i < mIn.getColumns(); i++) {
            for (int j = 0; j < mIn.getLines(); j++) {
                mIn.setP(i, j, new Point3D(1.0 * i / mIn.getColumns(),
                        1.0 * j / mIn.getLines(), 0.0));

            }

        }
    }

    @Override
    public boolean process(File in, File out) {
        try {
            PixM mIn = new PixM((Image) Objects.requireNonNull(new Image(1,1,1).getFromFile(in)));

            PixM mOut = new PixM(mIn.getColumns(), mIn.getLines());
            voronoi(mIn, mOut);
            Image image = mIn.getImage();
            image.saveToFile(out.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void genRandomPoints(PixM in, int n) {
        Random r = new Random();
        for (int i = 0; i < n; i++) {
            pointsA.add(new Point3D((double) r.nextInt(in.getColumns()), (double) (r.nextInt(in.getLines())), 0.0));
            pointsB.add(new Point3D((double) r.nextInt(in.getColumns()), (double) (r.nextInt(in.getLines())), 0.0));
        }
    }

    private PixM voronoi(PixM mIn, PixM mOut) {
        genRandomPoints(mIn, 20);
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        for (int i = 0; i < mIn.getColumns(); i++) {
            for (int j = 0; j < mIn.getLines(); j++) {
                Point3D d0 = new Point3D((double) i, (double) j, 0.0);
                Point3D d = new Point3D(d0);
                for (int k = 0; k < pointsA.size(); k++) {
                    d = d.plus(mIn.getP(i, j).plus(Point3D.distance(pointsA.get(k).moins(d0), pointsB.get(k).moins(d0))));
                }
                d = d.mult(1.0 / pointsA.size());
                mOut.setP(i, j, d);
                maxX = Math.max(mIn.getP(i, j).getX(), maxX);
                maxY = Math.max(mIn.getP(i, j).getY(), maxY);
                minX = Math.min(mIn.getP(i, j).getX(), minX);
                minY = Math.min(mIn.getP(i, j).getY(), minY);
            }
        }
        double dx = maxX - minX;
        double dy = maxY - minY;
        double scaleX = 1.0 / dx;
        double scaleY = 1.0 / dy;
        for (int i = 0; i < mIn.getColumns(); i++) {
            for (int j = 0; j < mIn.getLines(); j++) {
                mOut.setP(i, j, mIn.getP((int) ((i - minX) * scaleX * mIn.getColumns()),
                        (int) ((j - minY) * scaleY * mIn.getLines())));
            }
        }
        return mOut.normalize(0, 1);
    }

    public static void main(String[] args) {
        Voronoi voronoi = new Voronoi();
        voronoi.processTest();
    }
}
