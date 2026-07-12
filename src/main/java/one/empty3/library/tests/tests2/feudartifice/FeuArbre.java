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

/*__
 * *
 * Global license : * CC Attribution
 * <p>
 * author Manuel Dahmen <manuel.dahmen@gmx.com>
 * <p>
 * *
 */
package one.empty3.library.tests.tests2.feudartifice;

import one.empty3.library.*;
import one.empty3.library.core.move.Trajectoires;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/*__
 *
 * @author Manuel Dahmen <manuel.dahmen@gmx.com>
 */
public class FeuArbre {

    public int NUM = 10;
    public int PROF = 5;
    Noeud racine;
    private int prof = 0;

    public FeuArbre() {
        racine = new Noeud();
        racine.setLocation(Point3D.O0);

    }

    public RepresentableConteneur generate() {
        RepresentableConteneur rc = new RepresentableConteneur();

        generate(racine, rc);

        return rc;
    }

    public void generate(final Noeud n, final RepresentableConteneur rc) {
        ITexture color;
        color = new TextureCol(new Color((float) Math.random(), (float) Math.random(), (float) Math.random()));
        n.addBranches((int) (Math.random() * NUM));
        Iterator<Noeud> iterator = n.iterator();
        while (iterator.hasNext()) {
            Noeud no = iterator.next();
            prof++;
            if (prof < PROF) {
                rc.add(new LineSegment(n.getLocation(), no.getLocation(), color));
                generate(no, rc);
            }
            prof--;
        }

    }

    public class Noeud extends ArrayList<Noeud> {

        private Point3D loc;

        public Point3D getLocation() {
            return loc;
        }

        public void setLocation(Point3D p) {
            loc = p;
        }

        public void addBranches(int n) {
            for (int i = 0; i < n; i++) {
                Point3D sphere = Trajectoires.sphere(Math.random(), Math.random(), 4);
                Noeud noeud = new Noeud();
                noeud.setLocation(getLocation().plus(sphere));
                add(noeud);

            }
        }
    }

}
