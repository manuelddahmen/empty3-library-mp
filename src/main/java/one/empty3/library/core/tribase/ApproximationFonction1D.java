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
package one.empty3.library.core.tribase;

import one.empty3.*;
import one.empty3.library.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/*__
 * @author Manuel DAHMEN
 */
public class ApproximationFonction1D implements Courbe {

    public static final int TYPE_DROITE = 0;
    public static final int TYPE_BEZIER = 1;
    private final ArrayList<Point3D> points;
    public double DELTA = 0.0001;
    private ArrayList<Double> longueurs;
    private Double slongueurs;
    private int type_last;
    private ArrayList<Representable> objets;
    private HashMap<Double, Representable> r;

    public ApproximationFonction1D() {
        points = new ArrayList<Point3D>();
        objets = new ArrayList<Representable>();
        longueurs = new ArrayList<Double>();
        r = new HashMap<Double, Representable>();
    }

    public void add(int type, double x, double y) {
        points.add(new Point3D(x, y, 0d));
        if (type == TYPE_DROITE) {
            if (points.size() == 2) {
                LineSegment sd = new LineSegment(points.get(0), points.get(1));
                objets.add(sd);
                r.put(points.get(0).get(0), sd);
                points.clear();
            }
        } else if (type == TYPE_BEZIER) {
            if (points.size() == 4) {
                BezierCubique bc = new BezierCubique();
                objets.add(bc);
                r.put(points.get(0).get(0), bc);
                bc.add(points.get(0));
                bc.add(points.get(1));
                bc.add(points.get(2));
                bc.add(points.get(3));
                points.clear();
            }
        }
    }

    public Point3D calculerPoint3D(double t) {
        double t2 = 0;
        int i = 0;
        while (i < longueurs.size() && t2 + longueurs.get(i) / slongueurs < t) {
            t2 += longueurs.get(i++) / slongueurs;
        }
        Representable r2 = objets.get(i);
        if (r2 instanceof BezierCubique) {
            return ((BezierCubique) r2).calculerPoint3D((t - t2) * longueurs.get(i));
        } else if (r2 instanceof LineSegment) {
            return ((LineSegment) r2).calculerPoint3D((t - t2) * longueurs.get(i));
        }
        return null;
    }

    public ArrayList<Representable> getObjets() {
        return objets;
    }

    public void rectification() {
        slongueurs = 0.0;
        Iterator<Representable> it = objets.iterator();
        while (it.hasNext()) {
            Representable r = it.next();
            if (r instanceof LineSegment) {
                LineSegment sd = (LineSegment) r;
                double d = Point3D.distance(sd.getExtremite(), sd.getOrigine());
                slongueurs += d;
                longueurs.add(d);
            } else if (r instanceof BezierCubique) {
                BezierCubique bc = (BezierCubique) r;
                double d = 0;
                for (double t = 0; t < 1.0 - DELTA; t += DELTA) {
                    d += Point3D.distance(bc.calculerPoint3D(t), bc.calculerPoint3D(t + DELTA));
                }
                slongueurs += d;
                longueurs.add(d);
            }

        }
    }
}
