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

package one.empty3.library.tests;

import one.empty3.library.*;
import one.empty3.library.core.lighting.Colors;

public class Coque extends RepresentableConteneur {
    StructureMatrix<Point3D> pointsAccrocheLateraux = new StructureMatrix<Point3D>(1, Point3D.class);

    public Coque(Voiture voiture) {
        int i = 0;
        pointsAccrocheLateraux.setElem(P.n(voiture.getLongueur(), voiture.getHauteurPorte() / 3, 0.), i++);
        pointsAccrocheLateraux.setElem(P.n(voiture.getEspacementRoues() / 2, voiture.getHauteurPorte() / 2, 0.), i++);
        pointsAccrocheLateraux.setElem(P.n(voiture.getLongueur() - voiture.getEpaisseurRoue() / 3, voiture.getHauteurPorte(), 0.), i++);
        pointsAccrocheLateraux.setElem(P.n(0, voiture.getHauteurPorte(), 0.), i++);
        pointsAccrocheLateraux.setElem(P.n(-voiture.getEspacementRoues(), voiture.getHauteurCoffre(), 0.), i++);
        pointsAccrocheLateraux.setElem(P.n(-voiture.getLongueur() + 30., voiture.getHauteurCoffre(), 0.), i++);
        pointsAccrocheLateraux.setElem(P.n(-voiture.getLongueur(), voiture.getHauteurCoffre(), 0.), i++);

        pointsAccrocheLateraux.setElem(P.n(-voiture.getLongueur(), voiture.getHauteurBasCaisse(), 0.), i++);
        pointsAccrocheLateraux.setElem(P.n(-voiture.getLongueur() + 30., voiture.getHauteurBasCaisse(), 0.), i++);
        pointsAccrocheLateraux.setElem(P.n(-voiture.getEspacementRoues(), voiture.getHauteurBasCaisse(), 0.), i++);
        pointsAccrocheLateraux.setElem(P.n(0, voiture.getHauteurBasCaisse(), 0.), i++);
        pointsAccrocheLateraux.setElem(P.n(voiture.getLongueur() - voiture.getEpaisseurRoue() / 3, voiture.getHauteurBasCaisse(), 0.), i++);
        pointsAccrocheLateraux.setElem(P.n(voiture.getEspacementRoues() / 2, voiture.getHauteurBasCaisse(), 0.), i++);
        pointsAccrocheLateraux.setElem(P.n(voiture.getLongueur(), voiture.getHauteurBasCaisse(), 0.), i++);


        Point3D[] gauche = new Point3D[pointsAccrocheLateraux.getData1d().size() / 2];
        Point3D[] droit = new Point3D[pointsAccrocheLateraux.getData1d().size() / 2];

        for (i = 0; i < pointsAccrocheLateraux.getData1d().size(); i += 2) {
            gauche[i / 2] = new Point3D(0., 0., voiture.getLargeur()).plus(pointsAccrocheLateraux.getElem(i));
            droit[i / 2] = new Point3D(0., 0., -voiture.getLargeur()).plus(pointsAccrocheLateraux.getElem(i + 1));
        }


        add(new Polygon(pointsAccrocheLateraux.getData1d().toArray(gauche), Colors.random()));
        add(new Polygon(pointsAccrocheLateraux.getData1d().toArray(droit), Colors.random()));
    }
}
