/*
 *
 *  *
 *  *  * Copyright (c) 2026. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2026 Manuel Daniel Dahmen
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

package one.empty3.testagentcode;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.*;
import one.empty3.library.Sphere;
import one.empty3.libs.Color;

import java.io.File;

public class TestChanteuseDansante extends TestObjetSub {

    private Sphere tête;
    private Cylinder tronc;
    private Cylinder brasGauche, brasDroit;
    private Cylinder jambeGauche, jambeDroit;
    private Sphere microTete;
    private Cylinder microManche;

    @Override
    public void ginit() {
        scene = new Scene();

        // Couleurs
        Color peau = Color.newCol(0.9f, 0.7f, 0.6f);
        Color vetement = Color.newCol(0.8f, 0.1f, 0.4f);
        Color microColor = Color.newCol(0.2f, 0.2f, 0.2f);

        // Tête
        tête = new Sphere(new Point3D(0d, 1.2d, 0d), 0.3d);
        tête.texture(new TextureCol(peau));

        // Tronc
        tronc = new Cylinder(new Point3D(0d, 0.3d, 0d), new Point3D(0d, 1.0d, 0d), 0.3d);
        tronc.texture(new TextureCol(vetement));

        // Membres (initialisés verticalement)
        brasGauche = new Cylinder(new Point3D(-0.4d, 0.9d, 0d), new Point3D(-0.8d, 0.4d, 0d), 0.08d);
        brasGauche.texture(new TextureCol(peau));

        brasDroit = new Cylinder(new Point3D(0.4d, 0.9d, 0d), new Point3D(0.8d, 0.4d, 0d), 0.08d);
        brasDroit.texture(new TextureCol(peau));

        jambeGauche = new Cylinder(new Point3D(-0.2d, 0.3d, 0d), new Point3D(-0.2d, -0.7d, 0d), 0.1d);
        jambeGauche.texture(new TextureCol(vetement));

        jambeDroit = new Cylinder(new Point3D(0.2d, 0.3d, 0d), new Point3D(0.2d, -0.7d, 0d), 0.1d);
        jambeDroit.texture(new TextureCol(vetement));

        // Micro
        microManche = new Cylinder(new Point3D(0.6d, 0.4d, 0.2d), new Point3D(0.7d, 0.7d, 0.4d), 0.03d);
        microManche.texture(new TextureCol(microColor));
        microTete = new Sphere(new Point3D(0.7d, 0.75d, 0.45d), 0.06d);
        microTete.texture(new TextureCol(microColor));

        // Ajout à la scène
        scene.add(tête);
        scene.add(tronc);
        scene.add(brasGauche);
        scene.add(brasDroit);
        scene.add(jambeGauche);
        scene.add(jambeDroit);
        scene.add(microManche);
        scene.add(microTete);

        // Caméra avec vecteur UP explicite pour éviter la matrice dégénérée
        camera(new Camera(new Point3D(0d, 0.5d, 5d), new Point3D(0d, 0.5d, 0d), Point3D.Y));
    }

    @Override
    public void finit() {
        double t = frame / 25.0; // Temps basé sur l'image (25 fps)
        // Mouvement de danse (oscillation latérale)
        double décalageX = Math.sin(t * 2.0) * 0.5;
        double rotation = Math.cos(t * 1.5) * 0.2;

        // On déplace et fait pivoter le personnage
        Point3D centreBase = new Point3D(décalageX, 0d, 0d);

        // Appliquer une rotation légère sur l'axe Y pour le "swing"
        Point3D vX = new Point3D(Math.cos(rotation), 0d, Math.sin(rotation));
        Point3D vZ = new Point3D(-Math.sin(rotation), 0d, Math.cos(rotation));

        scene.setOrig(centreBase);
        for (Representable r : scene.getObjets().getData1d()) {
            r.setVectX(vX);
            r.setVectZ(vZ);
            r.setVectY(Point3D.Y);
        }

        // Animation spécifique des bras pour simuler le rythme
        brasGauche.setBase(new Point3D(-0.8d, 0.4d + Math.sin(t * 4.0) * 0.3d, 0.2d));
        brasDroit.setTop(new Point3D(0.8d, 0.4d + Math.cos(t * 4.0) * 0.3d, 0.2d));

        // Le micro suit la main droite
        Point3D mainDroite = brasDroit.getTop();
        microManche.setBase(mainDroite);
        microManche.setTop(mainDroite.plus(new Point3D(0.1, 0.3, 0.2)));
        microTete.setPosition(microManche.getTop());
    }

    public static void main(String[] args) {
        TestChanteuseDansante test = new TestChanteuseDansante();
        test.setMaxFrames(100); // Génère 4 secondes de vidéo à 25fps
        new Thread(test).start();
    }
}