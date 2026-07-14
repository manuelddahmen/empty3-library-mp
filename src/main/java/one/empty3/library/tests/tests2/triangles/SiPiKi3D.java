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

package one.empty3.library.tests.tests2.triangles;

import one.empty3.library.Point3D;
import one.empty3.library.RepresentableConteneur;
import one.empty3.library.TRI;

import java.util.ArrayList;
import java.util.Iterator;

public class SiPiKi3D extends RepresentableConteneur {
    /*__
     *
     */
    private static final long serialVersionUID = 1L;
    private final int nRecursion;

    public SiPiKi3D(int nRecursion) {
        super();
        this.nRecursion = nRecursion;
    }

    /*__
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

    }


    public void add(TRI t0, int nRecursions) {

        ArrayList<TRI>[] t = new ArrayList[2];

        t[0] = new ArrayList<TRI>();

        t[0].add(t0);


        for (int i = 0; i < nRecursions; i++) {
            t[1] = new ArrayList<TRI>();

            for (int j = 0; j < t[0].size(); j++) {
                TRI tt = t[0].get(j);
                for (int k = 0; k < 3; k++) {
                    Point3D p0 = tt.getSommet().getElem(k);
                    Point3D p1 = tt.getSommet().getElem((3 + k - 1) % 3).plus(tt.getSommet().getElem(k)).mult(0.5);
                    Point3D p2 = tt.getSommet().getElem((3 + k + 1) % 3).plus(tt.getSommet().getElem(k)).mult(0.5);
                    t[1].add(new TRI(p0, p1, p2, tt.texture()));
                }
            }


            t[0] = t[1];
        }

        Iterator<TRI> it = t[0].iterator();
        while (it.hasNext()) {
            TRI tt = it.next();
            super.add(tt);
        }
    }


}
