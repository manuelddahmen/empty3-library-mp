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

import one.empty3.apps.testobject.TestObjet;
import one.empty3.library.*;
import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.libs.Color;

import java.io.File;

/*
 * Animation d'un clin d'oeil.
 * L'oeil gauche se ferme et s'ouvre périodiquement.
 */
public class TestClinDoeil extends TestObjetSub {

    private Sphere oeilGauche;
    private Sphere oeilDroit;
    private Sphere tete;

    @Override
    public void ginit() {
        // Configuration de la scène
        scene = new Scene();

        // Création de la tête (une grande sphère rose/peau)
        tete = new Sphere(new Point3D(0d, 0d, 0d), 2.0d);
        tete.texture(new TextureCol(Color.newCol(1.0f, 0.8f, 0.6f)));

        // Création de l'œil droit (fixe)
        oeilDroit = new Sphere(new Point3D(0.7d, 0.5d, 1.8d), 0.3d);
        oeilDroit.texture(new TextureCol(new Color(Color.WHITE.getRGB())));

        // Création de l'œil gauche (celui qui clignera)
        oeilGauche = new Sphere(new Point3D(-0.7d, 0.5d, 1.8d), 0.3d);
        oeilGauche.texture(new TextureCol(new Color(Color.WHITE.getRGB())));

        // Ajout des objets à la scène
        scene.add(tete);
        scene.add(oeilDroit);
        scene.add(oeilGauche);

        // Configuration de la caméra (Vecteur "UP" explicite pour éviter la matrice nulle)
        camera(new Camera(new Point3D(0d, 0d, 10d), Point3D.O0, Point3D.Y));
    }

    @Override
    public void finit() {
        // Calcul du facteur de fermeture (oscillation entre 0.1 et 1.0)
        // On utilise le temps (frame) pour créer une animation cyclique
        double time = Math.PI * 2 * (frame / 100.0); // Cycle sur 100 images

        // On simule un clin d'oeil : l'oeil reste ouvert la plupart du temps
        // et se ferme rapidement.
        double scaleY = Math.abs(Math.sin(time));
        if (scaleY > 0.2) {
            scaleY = 1.0; // Oeil ouvert
        } else {
            scaleY = 0.1; // Oeil fermé (écrasé)
        }

        // Application de la transformation sur l'oeil gauche
        // On change le vecteur Y de l'objet pour l'aplatir
        oeilGauche.setVectY(new Point3D(0d, scaleY * 0.3d, 0d));

        // Note: l'oeil droit reste inchangé
    }

    public static void main(String[] args) {
        TestClinDoeil test = new TestClinDoeil();
        test.setGenerate(test.getGenerate() | TestObjet.GENERATE_MOVIE);
        test.setLoopLimit(100); // 100 images pour l'animation
        new Thread(test).start();
    }
}
