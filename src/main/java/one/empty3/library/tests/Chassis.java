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
import one.empty3.library.Polygon;
import one.empty3.library.core.lighting.Colors;
import one.empty3.library.core.nurbs.CourbeParametriquePolynomialeBezier;
import one.empty3.library.core.nurbs.ExtrusionCurveCurve;

import java.awt.*;

public class Chassis extends RepresentableConteneur {
    private final Voiture voiture;
    private double largeurMuseau;
    private double longueurArriere = 100.;

    public Chassis(Voiture voiture) {
        this.voiture = voiture;
        init();
    }

    public void init() {
        double largeur = voiture.getLargeur();
        double longueur = voiture.getLongueur();
        largeurMuseau = largeur;
        // Mettre 2 rectangles entre les roues Un sur le sol à 20cm de hauteur, l'autre à 40
        add(new Polygon(new Point3D[]{
                P.n(-voiture.getLongueur() / 2, 20, -largeur),
                P.n(voiture.getLongueur() / 2, 20, -largeur),
                P.n(voiture.getLongueur() / 2, 20, largeur),
                P.n(-voiture.getLongueur() / 2, 20, largeur)}, new ColorTexture(Colors.random())
        ));
        add(new Polygon(new Point3D[]{
                P.n(-voiture.getLongueur() / 2, 40., -largeur),
                P.n(voiture.getLongueur() / 2, 40., -largeur),
                P.n(voiture.getLongueur() / 2, 40., largeur),
                P.n(-voiture.getLongueur() / 2, 40., largeur)}, new ColorTexture(Colors.random())
        ));
        // Metre 4 rectangles pour l'epace entre les roues
        // 2 pour l'arrière
        add(new Polygon(new Point3D[]{
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue() - voiture.getEspacementRoues(), 20., -largeur + voiture.getEpaisseurRoue()),
                P.n(-voiture.getLongueur() / 2, 20., -largeur + voiture.getEpaisseurRoue()),
                P.n(-voiture.getLongueur() / 2, 20., largeur - voiture.getEpaisseurRoue()),
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue() - voiture.getEspacementRoues(), 20., largeur - voiture.getEpaisseurRoue())}, new ColorTexture(Colors.random())
        ));
        add(new Polygon(new Point3D[]{
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue() - voiture.getEspacementRoues(), 40., -largeur + voiture.getEpaisseurRoue()),
                P.n(-voiture.getLongueur() / 2, 40., -largeur + voiture.getEpaisseurRoue()),
                P.n(-voiture.getLongueur() / 2, 40., largeur - voiture.getEpaisseurRoue()),
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue() - voiture.getEspacementRoues(), 40., largeur - voiture.getEpaisseurRoue())}, new ColorTexture(Colors.random())
        ));
        // 2 pour l'avant
        add(new Polygon(new Point3D[]{
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue() - voiture.getEspacementRoues(), 20., -largeur + voiture.getEpaisseurRoue()),
                P.n(-voiture.getLongueur() / 2, 20., -largeur + voiture.getEpaisseurRoue()),
                P.n(-voiture.getLongueur() / 2, 20., largeur - voiture.getEpaisseurRoue()),
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue() - voiture.getEspacementRoues(), 20., largeur - voiture.getEpaisseurRoue())}, new ColorTexture(Colors.random())
        ));
        add(new Polygon(new Point3D[]{
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue() - voiture.getEspacementRoues(), 40., -largeur + voiture.getEpaisseurRoue()),
                P.n(-voiture.getLongueur() / 2, 40., -largeur + voiture.getEpaisseurRoue()),
                P.n(-voiture.getLongueur() / 2, 40., largeur - voiture.getEpaisseurRoue()),
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue() - voiture.getEspacementRoues(), 40., largeur - voiture.getEpaisseurRoue())}, new ColorTexture(Colors.random())
        ));
        // Largeur avant
        double longueurAvant = 100;
        add(new Polygon(new Point3D[]{
                P.n(voiture.getLongueur() / 2 + voiture.getRayonRoue() + longueurAvant, 20., -largeurMuseau),
                P.n(voiture.getLongueur() / 2 + voiture.getRayonRoue(), 20., -largeurMuseau),
                P.n(voiture.getLongueur() / 2, 20., largeurMuseau),
                P.n(voiture.getLongueur() / 2 + voiture.getRayonRoue() + longueurAvant, 20., largeurMuseau)}, new ColorTexture(Colors.random())
        ));
        add(new Polygon(new Point3D[]{
                P.n(voiture.getLongueur() / 2 + voiture.getRayonRoue() + longueurAvant, 40., -largeurMuseau),
                P.n(voiture.getLongueur() / 2 + voiture.getRayonRoue(), 40., -largeurMuseau),
                P.n(voiture.getLongueur() / 2, 40., largeurMuseau),
                P.n(voiture.getLongueur() / 2 + voiture.getRayonRoue() + longueurAvant, 40., largeurMuseau)}, new ColorTexture(Colors.random())
        ));
        // Coffre et arrière
        add(new Polygon(new Point3D[]{
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue() + longueurArriere, 20., -largeurMuseau),
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue(), 20., -largeurMuseau),
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue(), 20., largeurMuseau),
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue() + longueurArriere, 20., largeurMuseau)}, new ColorTexture(Colors.random())
        ));
        add(new Polygon(new Point3D[]{
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue() + longueurArriere, 40., -largeurMuseau),
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue(), 40., -largeurMuseau),
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue(), 40., largeurMuseau),
                P.n(-voiture.getLongueur() / 2 - voiture.getRayonRoue() + longueurArriere, 40., largeurMuseau)}, new ColorTexture(Colors.random())
        ));


        ExtrusionCurveCurve extrusionCurveCurve = new ExtrusionCurveCurve();
        extrusionCurveCurve.getBase().setElem(new PolyLine(new Point3D[]{
                new Point3D(-10., voiture.getHauteurPorte() + 20., voiture.getLargeur() - 20.),
                new Point3D(10., voiture.getHauteurPorte() + 20., voiture.getLargeur() - 20.),
                new Point3D(10., voiture.getHauteurPorte() + 20., voiture.getLargeur()),
                new Point3D(-10., voiture.getHauteurPorte() + 20., voiture.getLargeur()),
        }, Colors.random()));
        CourbeParametriquePolynomialeBezier courbeParametriquePolynomialeBezier = new CourbeParametriquePolynomialeBezier();
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., 0., 0.), 0);
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., voiture.getHauteurPorte() / 2, -15.), 1);
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., voiture.getHauteurPorte(), -30.), 1);
        extrusionCurveCurve.getPath().setElem(courbeParametriquePolynomialeBezier);
        add(extrusionCurveCurve);


        extrusionCurveCurve = new ExtrusionCurveCurve();
        extrusionCurveCurve.getBase().setElem(new PolyLine(new Point3D[]{
                new Point3D(-10., voiture.getHauteurPorte() + 20., -voiture.getLargeur() + 20.),
                new Point3D(10., voiture.getHauteurPorte() + 20., -voiture.getLargeur() + 20.),
                new Point3D(10., voiture.getHauteurPorte() + 20., -voiture.getLargeur()),
                new Point3D(-10., voiture.getHauteurPorte() + 20., -voiture.getLargeur()),
        }, Colors.random()));
        courbeParametriquePolynomialeBezier = new CourbeParametriquePolynomialeBezier();
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., 0., 0.), 0);
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., voiture.getHauteurPorte() / 2, 15.), 1);
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., voiture.getHauteurPorte(), 30.), 1);
        extrusionCurveCurve.getPath().setElem(courbeParametriquePolynomialeBezier);
        add(extrusionCurveCurve);

        // Montant avant
        extrusionCurveCurve = new ExtrusionCurveCurve();
        extrusionCurveCurve.getBase().setElem(new PolyLine(new Point3D[]{
                new Point3D(voiture.getEspacementRoues() / 2., voiture.getHauteurPorte() / 2., voiture.getLargeur() - 20.),
                new Point3D(voiture.getEspacementRoues() / 2 + 20., voiture.getHauteurPorte() / 2., voiture.getLargeur() - 20.),
                new Point3D(voiture.getEspacementRoues() / 2 + 20., voiture.getHauteurPorte() / 2., voiture.getLargeur()),
                new Point3D(voiture.getEspacementRoues() / 2., voiture.getHauteurPorte() / 2., voiture.getLargeur()),
        }, Colors.random()));
        courbeParametriquePolynomialeBezier = new CourbeParametriquePolynomialeBezier();
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., 0., 0.), 0);
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., voiture.getHauteurPorte() / 3, -10.), 1);
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., voiture.getHauteurPorte() / 2, -30.), 1);
        extrusionCurveCurve.getPath().setElem(courbeParametriquePolynomialeBezier);
        add(extrusionCurveCurve);


        extrusionCurveCurve = new ExtrusionCurveCurve();
        extrusionCurveCurve.getBase().setElem(new PolyLine(new Point3D[]{
                new Point3D(voiture.getEspacementRoues() / 2., voiture.getHauteurPorte() / 2., -voiture.getLargeur() + 20.),
                new Point3D(voiture.getEspacementRoues() / 2 + 20., voiture.getHauteurPorte() / 2., -voiture.getLargeur() + 20.),
                new Point3D(voiture.getEspacementRoues() / 2 + 20., voiture.getHauteurPorte() / 2., -voiture.getLargeur()),
                new Point3D(voiture.getEspacementRoues() / 2., voiture.getHauteurPorte() / 2., -voiture.getLargeur()),
        }, Colors.random()));
        courbeParametriquePolynomialeBezier = new CourbeParametriquePolynomialeBezier();
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., 0., 0.), 0);
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., voiture.getHauteurPorte() / 3, 10.), 1);
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., voiture.getHauteurPorte() / 2, 30.), 1);
        extrusionCurveCurve.getPath().setElem(courbeParametriquePolynomialeBezier);
        add(extrusionCurveCurve);

        // Montant arrière
        extrusionCurveCurve = new ExtrusionCurveCurve();
        extrusionCurveCurve.getBase().setElem(new PolyLine(new Point3D[]{
                new Point3D(-voiture.getEspacementRoues() / 2., voiture.getHauteurPorte() / 2., voiture.getLargeur() - 20.),
                new Point3D(-voiture.getEspacementRoues() / 2 + 20., voiture.getHauteurPorte() / 2., voiture.getLargeur() - 20.),
                new Point3D(-voiture.getEspacementRoues() / 2 + 20., voiture.getHauteurPorte() / 2., voiture.getLargeur()),
                new Point3D(-voiture.getEspacementRoues() / 2., voiture.getHauteurPorte() / 2., voiture.getLargeur()),
        }, Colors.random()));
        courbeParametriquePolynomialeBezier = new CourbeParametriquePolynomialeBezier();
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., 0., 0.), 0);
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., voiture.getHauteurPorte() / 3, -10.), 1);
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., voiture.getHauteurPorte() / 2, -30.), 1);
        extrusionCurveCurve.getPath().setElem(courbeParametriquePolynomialeBezier);
        add(extrusionCurveCurve);


        extrusionCurveCurve = new ExtrusionCurveCurve();
        extrusionCurveCurve.getBase().setElem(new PolyLine(new Point3D[]{
                new Point3D(-voiture.getEspacementRoues() / 2., voiture.getHauteurPorte() / 2., -voiture.getLargeur() + 20.),
                new Point3D(-voiture.getEspacementRoues() / 2 + 20., voiture.getHauteurPorte() / 2., -voiture.getLargeur() + 20.),
                new Point3D(-voiture.getEspacementRoues() / 2 + 20., voiture.getHauteurPorte() / 2., -voiture.getLargeur()),
                new Point3D(-voiture.getEspacementRoues() / 2., voiture.getHauteurPorte() / 2., -voiture.getLargeur()),
        }, Colors.random()));
        courbeParametriquePolynomialeBezier = new CourbeParametriquePolynomialeBezier();
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., 0., 0.), 0);
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., voiture.getHauteurPorte() / 3, 10.), 1);
        courbeParametriquePolynomialeBezier.getCoefficients().setElem(new Point3D(0., voiture.getHauteurPorte() / 2, 30.), 1);
        extrusionCurveCurve.getPath().setElem(courbeParametriquePolynomialeBezier);
        add(extrusionCurveCurve);

    }


}
