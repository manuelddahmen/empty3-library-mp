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

package one.empty3.library.core.tribase;

import one.empty3.library.HeightMapSurface;
import one.empty3.library.Point3D;

import java.util.ArrayList;
import java.util.List;

public class HeightMapSurface1 extends HeightMapSurface {
    List<Point3D> point3DS1 = new ArrayList<>();


    @Override
    public double heightDouble(double u, double v) {
        if(image==null)
            return Double.NaN;
        return search(u, v).getZ();
    }
    public void setList(List<Point3D> point3DS) {
        this.point3DS1 = point3DS;
    }

    public Point3D search(double u, double v) {
        Point3D current = new Point3D(u, v, 0.0);
        Point3D p = Point3D.INFINI;
        int near = -1;
        for (double i = getStartU(); i < getEndU(); i += getIncrU()) {
            // Iterates UV space; finds the nearest point in a list
            for (double j = getStartV(); j < getEndV(); j += getIncrV()) {
                // Clamps X coordinate to image width
                int x = Math.max(0,(int) Math.min(image.getElem().getImage().getElem().getWidth(), u * (image.getElem().getImage().getElem().getWidth() - 1)));
                // Clamps Y coordinate to image height
                int y = Math.max(0,(int) Math.min(image.getElem().getImage().getElem().getHeight(), v * (image.getElem().getImage().getElem().getHeight() - 1)));
                Double distance = Double.MAX_VALUE;
                // Finds the nearest point and its index in list
                for (Point3D comparing : point3DS1) {
                    if (comparing.distance(current) < distance) {
                        distance = comparing.distance(current);
                        p = new Point3D(u, v, comparing.get(2));
                        near = point3DS1.indexOf(comparing);
                    }
                }
            }
        }
        if(near>=0) {
            return p;
        } else {
            return Point3D.O0;
        }
    }

    @Override
    public Point3D calculerPoint3D(double u, double v) {
        return transformVec(search(u, v));
    }
}