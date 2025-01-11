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
 * Global license : * Microsoft Public Licence
 * <p>
 * author Manuel Dahmen _manuel.dahmen@gmx.com_
 * <p>
 * *
 */
package one.empty3.library.core.lighting;


import one.empty3.*;
import one.empty3.library.*;
import one.empty3.library.core.tribase.TRIObjetGenerateurAbstract;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/*__
 * Meta Description missing
 * @author Manuel Dahmen dathewolf@gmail.com
 */
public class Intersection {

    private final Point3D source;
    private final Point3D vecteurPorteur;
    private final Scene scene;
    private List<IntersectionElement> parcours = new ArrayList<IntersectionElement>();

    public Intersection(Scene scene, Point3D source, Point3D vecteurPorteur) {
        this.scene = scene;
        this.source = source;
        this.vecteurPorteur = vecteurPorteur;

        IntersectionElement ie = new IntersectionElement();
        ie.direction = vecteurPorteur;
        ie.eof = false;
        ie.facteurTransparence = new float[]{1, 1, 1};
        ie.point = source;
        ie.matiere = null;
        parcours.add(ie);

    }

    public IntersectionElement etablirPoint(IntersectionElement ie) throws IllegalAccessException {
        if (ie.eof) {
            throw new IllegalAccessException();
        }

        IntersectionElement newie = new IntersectionElement();
        ItererScene itererScene = new ItererScene(scene);

        while (itererScene.hasNext()) {
            Representable r = itererScene.next();

            if (r instanceof TRI) {
                LineSegment sd = new LineSegment(ie.point, ie.point.plus(ie.direction.moins(ie.point).mult(10000000d)));

                Representable i = sd.intersection((TRI) r);

                if (!(i instanceof Infini)) {
                    newie.point = (Point3D) i;
                    newie.eof = true;
                    newie.facteurTransparence = new float[]{0, 0, 0};
                    newie.matiere = r;
                    newie.direction = ie.direction;

                    return newie;
                }

            }
        }
        newie.point = Point3D.INFINI;
        newie.eof = true;
        newie.facteurTransparence = new float[]{0, 0, 0};
        newie.matiere = Infini.Default;
        newie.direction = ie.direction;

        return newie;
    }
}

class IntersectionElement {

    public Point3D point;

    public Point3D direction;

    public Representable matiere;
    public float[] facteurTransparence;
    public boolean eof = true;

    public IntersectionElement() {
    }

    public IntersectionElement(Point3D point, Point3D direction, Representable matiere, float[] facteurTransparence) {
        this.point = point;
        this.direction = direction;
        this.matiere = matiere;
        this.facteurTransparence = facteurTransparence;
    }
}

class ItererScene implements Iterator {

    Scene scene;
    Iterator<Representable> it;
    int numx;
    int numy;
    private Representable C;
    private boolean objetpartends = true;
    private int numtri;
    private Representable next;
    private boolean hasnext = false;

    public ItererScene(Scene scene) {
        this.scene = scene;
    }

    public boolean hasNext() {
        hasnext = false;
        if (hasNext()) {
            if (it == null) {
                it = scene.iterator();
            }
            if (objetpartends) {
                objetpartends = false;
                C = it.next();
            }
            if (C instanceof Point3D) {
                next = C;
                hasnext = true;
            } else if (C instanceof LineSegment) {
                next = C;
                hasnext = true;
            } else if (C instanceof TRI) {
                next = C;
                hasnext = true;
            } else if (C instanceof TRIObjetGenerateurAbstract) {

                TRIObjetGenerateurAbstract to = (TRIObjetGenerateurAbstract) C;
                TRI[] tris = new TRI[2];
                if (numx == -1) {
                    numx++;
                }
                if (numx < to.getMaxX() - 1 && numy >= to.getMaxY() - 1) {
                    numx++;
                }
                if (numtri == 0) {
                    numtri++;

                } else {
                    numtri = 0;
                }

                to.getTris(numx, numy, tris);
                next = tris[numtri];
                hasnext = true;
            }

        }
        return hasnext;
    }

    public Representable next() {
        return next;
    }

    public void remove() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
