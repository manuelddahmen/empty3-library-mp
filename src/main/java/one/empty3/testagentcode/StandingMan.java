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

import one.empty3.library.*;
import one.empty3.library.Box;
import one.empty3.library.Cylinder;

import java.io.File;
import java.io.IOException;
import java.awt.Color;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StandingMan {

    public static void main(String[] args) {
        Scene scene = new Scene();

        // Camera setup (with explicit UP vector to avoid degenerated matrix)
        Camera camera = new Camera(new Point3D(0.0, 3.0, 5.0), Point3D.O0, Point3D.Y);
        scene.add(camera);

        // --- Head (Sphere) ---
        Sphere head = new Sphere(new Point3D(0.0, 1.8, 0.0), 0.2);
        head.texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f, 0.0f, 1f).getRGB()));
        scene.add(head);

        // --- Torso (Box) ---
        Box torso = new Box(
                new Point3D(-0.2, 1.0, -0.1),
                new Point3D(0.2, 1.6, 0.1)
        );
        torso.texture(new ColorTexture(one.empty3.libs.Color.newCol(0, 0, 1).getRGB()));
        scene.add(torso);

        // --- Left Arm (Cylinder) ---
        Cylinder leftArm = new Cylinder(
                new Point3D(-0.3, 1.5, 0.0),
                new Point3D(-0.3, 1.0, 0.0),
                0.08
        );
        leftArm.texture(new ColorTexture(one.empty3.libs.Color.newCol(1.0f, 0.0f, 0f).getRGB()));
        scene.add(leftArm);

        // --- Right Arm (Cylinder) ---
        Cylinder rightArm = new Cylinder(
                new Point3D(0.3, 1.5, 0.0),
                new Point3D(0.3, 1.0, 0.0),
                0.08
        );
        rightArm.texture(new ColorTexture(one.empty3.libs.Color.newCol(1.0f, 0.0f, 0f).getRGB()));
        scene.add(rightArm);

        // --- Left Leg (Cylinder) ---
        Cylinder leftLeg = new Cylinder(
                new Point3D(-0.1, 1.0, 0.0),
                new Point3D(-0.1, 0.0, 0.0),
                0.1
        );
        leftLeg.texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f, 1.0f, 0f).getRGB()));
        scene.add(leftLeg);

        // --- Right Leg (Cylinder) ---
        Cylinder rightLeg = new Cylinder(
                new Point3D(0.1, 1.0, 0.0),
                new Point3D(0.1, 0.0, 0.0),
                0.1
        );
        rightLeg.texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f, 1.0f, 0f).getRGB()));
        scene.add(rightLeg);

        // --- Ground (Plane) ---
        Box ground = new Box(
                new Point3D(-5.0, -0.01, -5.0),
                new Point3D(5.0, 0.0, 5.0)
        );
        ground.texture(new ColorTexture(Color.DARK_GRAY.getRGB()));
        scene.add(ground);

        // --- Rendering ---
        ZBufferImpl zBuffer = new ZBufferImpl(800, 600); // Image dimensions
        zBuffer.scene(scene);
        zBuffer.camera(camera);

        try {
            zBuffer.draw();
            one.empty3.libs.Image image = zBuffer.image();
            File outputFile = new File("standing_man.png");
            if (image.saveFile(outputFile)) {
                System.out.println("Image saved to: " + outputFile.getAbsolutePath());
            } else {
                System.out.println("Failed to save image to: " + outputFile.getAbsolutePath());
            }
        } catch (Exception ex) {
            Logger.getLogger(StandingMan.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

/***
 Explication du code :

 1. Scene scene = new Scene(); : Crée une nouvelle scène 3D.
 2. Camera camera = new Camera(...) : Définit la position de la caméra, son point de visée (Point3D.O0 pour l'origine) et un vecteur "haut" explicite (Point3D.Y) pour éviter les problèmes de matrice dégénérée.
 3. TRISphere head = new TRISphere(...) : Crée une sphère pour la tête à la position (0.0, 1.8, 0.0) avec un rayon de 0.2.
 4. Box torso = new Box(...) : Crée une boîte pour le torse, définie par deux points opposés.
 5. Cylinder leftArm = new Cylinder(...) et rightArm, leftLeg, rightLeg : Créent des cylindres pour les bras et les jambes, chacun défini par ses deux points d'extrémité et son rayon.
 6. Box ground = new Box(...) : Crée un plan (une très fine boîte) pour simuler le sol.
 7. object.texture(new ColorTexture(one.empty3.libs.Color.newCol(0,0,1))) : Applique une couleur unie à chaque objet.
 8. scene.add(object) : Ajoute chaque objet à la scène.
 9. ZBufferImpl zBuffer = new ZBufferImpl(800, 600); : Initialise le moteur de rendu avec les dimensions de l'image (800x600 pixels).
 10. zBuffer.setScene(scene); et zBuffer.setCamera(camera); : Lie la scène et la caméra au moteur de rendu.
 11. zBuffer.build(); : Lance le processus de rendu pour générer l'image.
 12. image.saveFile(outputFile); : Sauvegarde l'image générée dans un fichier nommé "standing_man.jpg".

 Ce code générera un fichier standing_man.jpg dans le répertoire d'exécution, montrant une représentation simple d'un homme debout.
 */