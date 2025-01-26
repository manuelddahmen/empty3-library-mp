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

package one.empty3.facedetect.model.vecmesh;

import one.empty3.feature.PixM;
import one.empty3.library.*;

import java.util.ArrayList;
import java.util.List;


public class Voronoi extends ITexture {

    private static final double N = 50;
    private PixM out = null;
    private PixM in = null;

    private Point3D near(Point3D point3D, List<Point3D> p) {
        double dist = 1000000;
        Point3D pRes = null;

        int index2 = 0;

        while (index2 <= p.size() - 1) {
            Point3D p3 = p.get(index2);

            if (Point3D.distance(point3D, p3) < dist && p3 != point3D && !p3.equals(point3D)) {
                dist = Point3D.distance(point3D, p3);
                pRes = p3;


            }
            index2++;
        }
        return pRes;
    }

    public PixM getIn() {
        return in;
    }

    public void setIn(PixM in) {
        this.in = in;
    }

    public void processInMemory() {
        PixM out = in.copy();
        try {
            List<Point3D> points = new ArrayList<>();
            PixM pixM = in;
            for (int i = 0; i < pixM.getColumns(); i++) {
                for (int j = 0; j < pixM.getLines(); j++) {
                    if (pixM.luminance(i, j) > 0.4 && Math.random() < (1. + N) / out.getColumns() / out.getLines()) {
                        points.add(new Point3D((double) i, (double) j, pixM.luminance(i, j)));
                    }
                }
            }


            for (int i = 0; i < pixM.getColumns(); i++) {
                for (int j = 0; j < pixM.getLines(); j++) {
                    Point3D near = near(new Point3D((double) i, (double) j, 0.0), points);
                    if (near != null) {
                        Point3D p = pixM.getP((int) (double) near.get(0), (int) (double) near.get(1));
                        out.setValues(i, j, p.getX(), p.getY(), p.getZ());
                    } else {
                        //Logger.getAnonymousLogger().log(Level.INFO, "Error near==null");
                    }
                }
            }


            this.out = out;


        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public int getColorAt(double x, double y) {
        if (out == null && in != null)
            processInMemory();
        float[] colorOut1 = new float[4];
        out.getColor((int) (x / out.getColumns()), (int) (y / out.getLines()), colorOut1);
        double[] colorOut = new double[4];
        for (int i = 0; i < colorOut.length; i++) {
            colorOut[i] = colorOut1[i];
        }
        return Lumiere.getInt(colorOut);
    }

    @Override
    public MatrixPropertiesObject copy() throws CopyRepresentableError, IllegalAccessException, InstantiationException {
        return null;
    }
}
