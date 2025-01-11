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

package one.empty3.neuralnetwork;

public class FeatureDescriptor {
    public int ordPix(int x, int y, int comp, int sizeX, int sizeY, int comps) {
        return comp * sizeX * sizeY + sizeX * y + x;
    }

    private final int resX;
    private final int resY;
    private double[] fd;
    private int comps = 3;

    /***
     * Feature descriptor, subimage as : comp{red=0,green=1,blue=2}*(columns*lineNo+columnNo) // TODO Verify.
     */
    public FeatureDescriptor(int resX, int resY) {
        fd = new double[resX * resY * comps];
        this.resX = resX;
        this.resY = resY;
    }

    /***
     * Feature descriptor, subimage as : comp{red=0,green=1,blue=2}*(columns*lineNo+columnNo) // TODO Verify.
     * @return descriptor array.
     */
    public double[] getFd() {
        return fd;
    }

    public void setFd(double[] fd) {
        this.fd = fd;
    }
/*
    public double match(Neuron neuron) {
        double highScore = -1.0;
        double score = 0.0;
        int wd = resX;
        int hd = resY;
        int wi = neuron.getSizeX();
        int hi = neuron.getSizeY();

        Resolution dimD = new Resolution(wd, hd);
        Resolution dimI = new Resolution(wi, hi);
        Point2D min = new Point2D(0, 0);
        Point2D max = new Point2D(0, 0);

        double incrX, incrY;
        if (wd <= wi && hd <= hi) { // Feature descriptor size <<< sample size
            if (wd > hd) {
                min.x = 1.0;
                min.y = 1.0 / dimI.x() * dimI.y();
                max.x = dimI.x();
                max.y = dimI.y();

                incrX = 1.0 / dimI.x();
                incrY = 1.0 / dimI.x();
            } else {
                min.x = 1.0 / dimI.y() * dimI.x();
                min.y = 1.0;
                max.x = dimI.x();
                max.y = dimI.y();

                incrX = 1.0 / dimI.y();
                incrY = 1.0 / dimI.y();
            }
        } else {
            return -1;
        }
        double xRes = 0, yRes = 0;
        double xCurr = 0, yCurr = 0;
        while (xRes < max.x && yRes < max.y) {
            for (xCurr = 0; xCurr <= neuron.getSizeX() - resX; xCurr += 1) {
                for (yCurr = 0; yCurr <= neuron.getSizeY() - resY; yCurr += 1) {
                    Rectangle rectangle = new Rectangle((int) (xCurr), (int) (yCurr),
                            neuron.getSizeX(), neuron.getSizeY());

                    // Match feature of size rectangle.wh in rectangle
                    // According to colors variations.
                    // Tenir en compte les composantes rgb.??
                    int colorComp = 0;
                    for (int i = 0; i < rectangle.x - rectangle.width; i++)
                        for (int j = 0; j < rectangle.y - rectangle.height; j++) {
                            Point3D color1 = neuron.getPixelColorComponents(i, j);
                            Point3D colorDescription1 = new Point3D(
                                    fd[ordPix(i, j, 0, dimD.x(), dimD.y(), 3)],
                                    fd[ordPix(i, j, 1, dimD.x(), dimD.y(), 3)],
                                    fd[ordPix(i, j, 2, dimD.x(), dimD.y(), 3)]);
                            if (Point3D.distance(color1, colorDescription1) < 0.01) {
                                colorComp++;
                            }
                        }
                    if (colorComp == rectangle.width * rectangle.height)
                        return 1.0;
                    double currentScore = colorComp
                            * 1.0 / rectangle.width / rectangle.height;
                    if (currentScore > highScore) {
                        highScore = currentScore;
                        // ADD CURRENT FEATURE
                    }
                }

            }

        }
        return highScore;
    }*/
}
