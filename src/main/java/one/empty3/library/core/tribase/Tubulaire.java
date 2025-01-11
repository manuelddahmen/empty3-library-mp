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

/*__
 * *
 * Global license : * GNU GPL v3
 * <p>
 * author Manuel Dahmen _manuel.dahmen@gmx.com_
 * <p>
 * Creation time 2015-03-25
 * <p>
 * *
 */
package one.empty3.library.core.tribase;

import one.empty3.*;
import one.empty3.library.*;

import one.empty3.libs.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Tubulaire extends Representable implements TRIGenerable, TRIConteneur {

    public float PERCENT = 0.05f;
    private Color couleur = new Color(Color.BLUE.getRGB());
    private String id;
    private ArrayList<Point3D> points;
    //private double ratio;
    private ArrayList<BezierCubique> beziers;
    private double diam = 3.0f;
    private float TAN_FCT = 0.00005f;
    private float NORM_FCT = 0.0005f;
    private int N_TOURS = 40;
    private TRIObject tris = null;
    private Barycentre position;

    public Tubulaire() {
        this.points = new ArrayList<Point3D>();
        //this.ratio = 1.0;

    }

    public void add(Point3D e) {
        points.add(e);
    }

    public void addPoint(Point3D p) {
        points.add(p);
    }

    public Point3D calculerNormale(double t) {
        if (t < beziers.size() - NORM_FCT) {

            return calculerTangente(t + NORM_FCT).moins(calculerTangente(t));
        } else {
            return null;
        }
    }

    public Point3D calculerPoint(double t) {
        return beziers.get((int) t).calculerPoint3D(t - (int) t);
    }

    public Point3D calculerTangente(double t) {
        if (t < beziers.size() - TAN_FCT) {

            return calculerPoint(t + TAN_FCT).moins(calculerPoint(t));
        } else {
            return null;
        }
    }

    public PObjet calculPoints(IFct1D3D funct, double value, double angle) {
        return null;
    }

    public Point3D cerclePerp(Point3D vect, double angle) {
        return null;
    }

    public void clear() {
        points.clear();
    }

    public void couleur(Color c) {
        this.couleur = c;

    }

    public void diam(float diam) {
        this.diam = diam;
    }

    @Override
    public TRIObject generate() {
        if (tris == null) {
            tris = new TRIObject();

            generateWire();

            int length = beziers.size();

            ArrayList<Point3D> tour0 = vectPerp(0);
            for (double t = 0; t < length; t += PERCENT) {
                ArrayList<Point3D> tour1 = vectPerp(t);
                for (int i = 3; i < tour1.size() - 1; i++) {
                    TRI t1 = new TRI(tour0.get(i), tour1.get(i), tour1.get(i + 1), couleur);
                    TRI t2 = new TRI(tour0.get(i), tour0.get(i + 1), tour1.get(i + 1), couleur);
                    t1.setCouleur(CouleurOutils.couleurFactio(couleur, new Color(Color.white.getRGB()), t1, new Point3D(0d, 0d, 1d), false));
                    t2.setCouleur(CouleurOutils.couleurFactio(couleur, new Color(Color.white.getRGB()), t1, new Point3D(0d, 0d, 1d), false));
                    t1.setCouleur(CouleurOutils.couleurFactio(couleur, new Color(Color.white.getRGB()), t1, new Point3D(0d, 0d, 1d), false));
                    t1.setCouleur(couleur);
                    t2.setCouleur(couleur);
                    tris.add(t1);
                    tris.add(t2);
                }

                tour0 = tour1;
            }
        }
        return tris;
    }

    public void generateWire() {
        Logger.getAnonymousLogger().log(Level.INFO, "WIRE SIZE " + points.size());
        beziers = new ArrayList<BezierCubique>();

        for (int i = 0; i < points.size() - 3; i += 4) {
            BezierCubique bc = new BezierCubique();
            bc.add(position == null ? points.get(i) : position.calculer(points.get(i)));
            bc.add(position == null ? points.get(i + 1) : position.calculer(points.get(i + 1)));
            bc.add(position == null ? points.get(i + 2) : position.calculer(points.get(i + 2)));
            bc.add(position == null ? points.get(i + 3) : position.calculer(points.get(i + 3)));
            /*bc.add(points.get(i));
             //bc.add((points.get(i).mult(ratio)).plus((points.get(i + 1).mult(1 - ratio))));
             //bc.add((points.get(i + 2).mult(ratio)).plus((points.get(i + 1).mult(1 - ratio))));
             //bc.add(points.get(i + 2));
             */
            beziers.add(bc);
        }

        Logger.getAnonymousLogger().log(Level.INFO, "Beziers = " + beziers.size());
    }

    @Override
    public Representable getObj() {
        generate();
        return tris;
    }

    @Override
    public Iterable<TRI> iterable() {
        generate();
        return tris.getTriangles();
    }

    public void nbrAnneaux(int n) {
        this.PERCENT = 1.0f * beziers.size() / n;
    }

    public void nbrRotations(int r) {
        this.N_TOURS = r;
    }

    public void radius(double d) {
        diam = d;
    }

    public double tMax() {
        return (double) beziers.size();
    }

    @Override
    public String toString() {
        String s = "tubulaire (\n\t(";
        Iterator<Point3D> it = points.iterator();
        while (it.hasNext()) {
            s += "\n\t" + it.next().toString();
        }
        s += "\n\n)\n\t" + diam + "\n\t" + toStringColor() + "\n)\n";
        return s;
    }

    /*public void ratio(double r) {
     ratio = r;
     }*/
    protected String toStringColor() {
        return "(" + couleur.getRed() + ", " + couleur.getGreen() + ", "
                + couleur.getBlue() + ")";
    }

    private ArrayList<Point3D> vectPerp(double t) {
        ArrayList<Point3D> vecteurs = new ArrayList<Point3D>();

        Point3D p = calculerPoint(t);
        Point3D tangente = calculerTangente(t);

        Point3D ref1 = new Point3D(0d, 0d, 1d);
        Point3D ref2 = new Point3D(1d, 0d, 0d);
        Point3D ref3 = new Point3D(0d, 1d, 0d);

        tangente = tangente.norme1();

        if (tangente != null) {
            Point3D px = calculerNormale(t);///tangente.prodVect(ref1);

            if (px.norme() == 0) {
                px = tangente.prodVect(ref2);
            }
            if (px.norme() == 0) {
                px = tangente.prodVect(ref3);
            }

            Point3D py = px.prodVect(tangente);

            px = px.norme1();
            py = py.norme1();
            //Logger.getAnonymousLogger().log(Level.INFO, "px.py: " +px.prodScalaire(py)+"px.tg: "+px.prodScalaire(tangente)+"py.tg "+py.prodScalaire(tangente));
            vecteurs.add(px);
            vecteurs.add(py);
            vecteurs.add(tangente);

            for (int i = 0; i < N_TOURS + 1; i++) {
                double angle = 2.0f * Math.PI * i / N_TOURS;
                vecteurs.add(p.plus(px.mult(Math.cos(angle) * diam)).plus(
                        py.mult(Math.sin(angle) * diam)));
            }
        }
        return vecteurs;
    }

}
