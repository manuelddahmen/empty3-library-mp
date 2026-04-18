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
import one.empty3.libs.Color;

import java.io.File;

/*
 * Dessin d'une maison simple composée d'un cube pour la base
 * et d'une pyramide pour le toit.
 */
public class MaisonScene extends TestObjetSub {

    @Override
    public void ginit() {
        // Création de la scène
        Scene scene = new Scene();

        // 1. La base de la maison (un cube)
        // Cube(largeur, hauteur, profondeur)
        Box base = new Box(2.0, 2.0, 2.0);
        base.setOrig(new Point3D(0.0, -1.0, 0.0)); // Centrer la base
        base.texture(new ColorTexture(Color.newCol(0.8f, 0.8f, 0.8f))); // Murs gris clair
        scene.add(base);

        // 2. Le toit (une pyramide)
        // Pour faire un toit, on utilise une structure de pyramide simple
        // Sommet du toit
        Point3D sommet = new Point3D(0.0, 2.5, 0.0);
        // Coins de la base du toit (un peu plus larges que le cube)
        Point3D p1 = new Point3D(-1.2, 1.0, -1.2);
        Point3D p2 = new Point3D(1.2, 1.0, -1.2);
        Point3D p3 = new Point3D(1.2, 1.0, 1.2);
        Point3D p4 = new Point3D(-1.2, 1.0, 1.2);

        // Ajout des faces du toit à la scène
        ITexture roofTexture = new ColorTexture(Color.newCol(0.6f, 0.2f, 0.2f)); // Rouge brique

        scene.add(new TRI(p1, p2, sommet, roofTexture));
        scene.add(new TRI(p2, p3, sommet, roofTexture));
        scene.add(new TRI(p3, p4, sommet, roofTexture));
        scene.add(new TRI(p4, p1, sommet, roofTexture));

        // Configuration de la caméra
        // Positionnée en (5, 5, 10), regardant vers l'origine (0,0,0), avec le vecteur "Haut" Y
        Camera camera = new Camera(new Point3D(6.0, 4.0, 10.0), Point3D.O0, Point3D.Y);
        scene.cameraActive(camera);

        this.scene = scene;
    }

    @Override
    public void finit() {
        // Animation optionnelle : rotation de la maison autour de l'axe Y au fil du temps
        double angle = 0.01;
        // Note: Pour une animation fluide, on pourrait utiliser frame/fps
    }

    public static void main(String[] args) {
        MaisonScene maison = new MaisonScene();
        maison.setPublish(true);
        maison.setMaxFrames(1); // Pour une image fixe
        new Thread(maison).start();
    }
}