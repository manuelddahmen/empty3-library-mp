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

/***
 * Gemini AI code generation 2025
 */
package one.empty3.facedetect.model;

import one.empty3.library.Lumiere;
import one.empty3.library.Point3D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Dimension2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConvexHull {
    private final List<Point3D> list;
    private final BufferedImage mask;
    List<Point3D> p = new ArrayList<>();

    public ConvexHull(List<Point3D> list, Dimension2D dimension2D) {
        List<Point3D> list1 = new ArrayList<>();

        mask = new BufferedImage((int) dimension2D.getWidth(), (int) dimension2D.getHeight(), BufferedImage.TYPE_INT_RGB );

        for (Point3D point3D : list) {
            list1.add(point3D.multDot(new Point3D(dimension2D.getWidth(), dimension2D.getHeight(), 0.0)));
        }
        this.list = list1;

        createConvexHull();
    }


    public List<Point3D> computeHull() {
        List<Point3D> hull = new ArrayList<>();
        if (list.size() < 3) return new ArrayList<>(list); // Handle cases with < 3 points

        // Find the leftmost point (starting point)
        Point3D onHull = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            if (list.get(i).getX() < onHull.getX()) {
                onHull = list.get(i);
            }
        }

        Point3D endpoint;
        do {
            hull.add(onHull);
            endpoint = list.get(0); // Initial guess for the next point on the hull
            for (int j = 1; j < list.size(); j++) {
                Point3D p = list.get(j);
                if (endpoint == onHull || isLeftTurn(onHull, endpoint, p)) {
                    endpoint = p;
                }
            }
            onHull = endpoint;
        } while (endpoint != hull.get(0)); // Stop when we wrap back to the start



        return hull;
    }


    private boolean isLeftTurn(Point3D p1, Point3D p2, Point3D p3) {
        return (p2.getX() - p1.getX()) * (p3.getY() - p1.getY()) -
                (p2.getY() - p1.getY()) * (p3.getX() - p1.getX()) > 0;
    }


    public void createConvexHull() {
        List<Point3D> hull = computeHull();
        /*
        Logger.getAnonymousLogger().log(Level.INFO, "ConvexHull started");
        double maxX = 0;
        Point3D first = null;
        for (Point3D d : list) {
            if (d.getX() > maxX) {
                maxX = d.getX();
                first = d;
            } else if (d.getX() == maxX && (first == null || d.getY() > first.getY())) {
                first = d;
            }
        }

        if(first==null) {
            Logger.getAnonymousLogger().log(Level.INFO , "ConvexHull first =="+first);
            return;
        }
        p.add(first);

        double a=Math.PI/2; // a vers -2*PI
        double angleMax = a+Math.PI;
        Point3D current = first;
        angleMax = 0;
        double at = 0;
        Point3D t;
        while(!list.isEmpty()) {
            Point3D selPoint = null;
            angleMax = a+Math.PI;
            for (Point3D point3D : list) {
                if (!p.contains(point3D)) {
                    t = point3D.moins(current).norme1();
                    at = Math.atan2(t.getY(), t.getX());
                    if (at>a && at <= angleMax) {
                        selPoint = point3D;
                        a = angleMax;
                    }
                }
            }
            if(selPoint==null||(selPoint==first&&p.size()>1))
                break;
            current = selPoint;
            p.add(current);
        }
*/


        p = hull;

        int [] xPoints = new int[p.size()];
        int [] yPoints = new int[p.size()];
        for(int i=0; i<p.size(); i++) {
            xPoints[i] = (int) p.get(i).getX();
            yPoints[i] = (int) p.get(i).getY();
        }

        Graphics graphics = mask.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillPolygon(xPoints, yPoints, p.size());


        try {
            File file = new File(".\\storage\\");
            if(!file.exists())
                file.mkdir();
            if(file.exists()) {
                File file1 = new File(".\\storage\\convexHull.jpg");
                if(file1.exists())
                    file1.delete();
                if (ImageIO.write(mask, "jpg", file1)) {
                    Logger.getAnonymousLogger().log(Level.INFO, "ConvexHull done");
                } else
                    Logger.getAnonymousLogger().log(Level.INFO, "ConvexHull failed "+mask.getWidth()+"/"+mask.getHeight());
            }
        } catch (IOException e) {
            Logger.getAnonymousLogger().log(Level.INFO, "ConvexHull failed "+mask.getWidth()+"/"+mask.getHeight());
            e.printStackTrace();
        }

        Logger.getAnonymousLogger().log(Level.INFO, "ConvexHull done " + p.size()+"/"+list.size());
   }

    public boolean testIfIn(int x, int y) {
        double[] rgb1 = Lumiere.getDoubles(mask.getRGB(x,y));
        double[] rgb2 = Lumiere.getDoubles(Color.WHITE.getRGB());
        if(x>=0&&x<mask.getWidth()&&y>=0&&y<mask.getHeight()) {
            return rgb1[0] == rgb2[0] && rgb1[1] == rgb2[1] && rgb1[2] == rgb2[2] && rgb1[0] >= 0.9
                    && rgb1[1] >= 0.9 && rgb1[2] >= 0.9 && rgb2[0] >= 0.9 && rgb2[1] >= 0.9 && rgb2[2] >= 0.9;
        }
        return false;
    }
}
