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
import one.empty3.libs.Color;
import one.empty3.libs.Image;

import java.io.File;

/**
 * Génère un rendu 3D réaliste et texturé d'un corps humain
 * à l'aide de la bibliothèque empty3.
 */
public class HumanBodyRenderer {

    public static void main(String[] args) {
        // 1. Initialisation de la scène 3D
        Scene scene = new Scene();

        // 2. Définition des textures de couleurs (ColorTexture)
        // Utilisation de one.empty3.libs.Color.newCol conformément aux règles
        ColorTexture skinTexture = new ColorTexture(Color.newCol(0.92f, 0.76f, 0.65f)); // Teint chair
        ColorTexture hairTexture = new ColorTexture(Color.newCol(0.20f, 0.12f, 0.08f)); // Brun foncé
        ColorTexture eyeWhiteTexture = new ColorTexture(Color.newCol(0.95f, 0.95f, 0.95f)); // Blanc des yeux
        ColorTexture irisTexture = new ColorTexture(Color.newCol(0.15f, 0.35f, 0.60f)); // Iris bleu
        ColorTexture shirtTexture = new ColorTexture(Color.newCol(0.85f, 0.15f, 0.15f)); // T-shirt rouge
        ColorTexture pantsTexture = new ColorTexture(Color.newCol(0.12f, 0.25f, 0.45f)); // Jean bleu
        ColorTexture shoeTexture = new ColorTexture(Color.newCol(0.15f, 0.15f, 0.15f)); // Chaussures grises/noires
        ColorTexture grassTexture = new ColorTexture(Color.newCol(0.25f, 0.50f, 0.22f)); // Sol vert herbe

        // 3. Modélisation du sol (une immense sphère centrée très bas pour un effet de sol plan sous les pieds)
        Sphere ground = new Sphere(new Point3D(0.0, -100.0, 0.0), 100.0);
        ground.texture(grassTexture);
        scene.add(ground);

        // ==========================================
        // 4. MODÉLISATION DE LA TÊTE ET DU VISAGE
        // ==========================================
        
        // Crâne
        Sphere head = new Sphere(new Point3D(0.0, 1.62, 0.0), 0.10);
        head.texture(skinTexture);
        scene.add(head);

        // Mâchoire / Bas du visage
        Sphere jaw = new Sphere(new Point3D(0.0, 1.54, 0.03), 0.08);
        jaw.texture(skinTexture);
        scene.add(jaw);

        // Nez
        Sphere nose = new Sphere(new Point3D(0.0, 1.52, 0.10), 0.015);
        nose.texture(skinTexture);
        scene.add(nose);

        // Chevelure (assemblage volumétrique de mèches)
        Sphere hairTop = new Sphere(new Point3D(0.0, 1.70, -0.02), 0.085);
        hairTop.texture(hairTexture);
        scene.add(hairTop);

        Sphere hairLeft = new Sphere(new Point3D(-0.06, 1.66, -0.05), 0.07);
        hairLeft.texture(hairTexture);
        scene.add(hairLeft);

        Sphere hairRight = new Sphere(new Point3D(0.06, 1.66, -0.05), 0.07);
        hairRight.texture(hairTexture);
        scene.add(hairRight);

        Sphere hairBack = new Sphere(new Point3D(0.0, 1.60, -0.08), 0.08);
        hairBack.texture(hairTexture);
        scene.add(hairBack);

        // Yeux (Œil gauche + Iris)
        Sphere eyeLeft = new Sphere(new Point3D(-0.035, 1.57, 0.09), 0.015);
        eyeLeft.texture(eyeWhiteTexture);
        scene.add(eyeLeft);

        Sphere pupilLeft = new Sphere(new Point3D(-0.035, 1.57, 0.102), 0.007);
        pupilLeft.texture(irisTexture);
        scene.add(pupilLeft);

        // Yeux (Œil droit + Iris)
        Sphere eyeRight = new Sphere(new Point3D(0.035, 1.57, 0.09), 0.015);
        eyeRight.texture(eyeWhiteTexture);
        scene.add(eyeRight);

        Sphere pupilRight = new Sphere(new Point3D(0.035, 1.57, 0.102), 0.007);
        pupilRight.texture(irisTexture);
        scene.add(pupilRight);

        // Cou
        Cylinder neck = new Cylinder(new Point3D(0.0, 1.48, 0.0), new Point3D(0.0, 1.40, 0.0), 0.05);
        neck.texture(skinTexture);
        scene.add(neck);

        // ==========================================
        // 5. MODÉLISATION DU TRONC (Haut du corps)
        // ==========================================
        
        // Thorax / Poitrine (T-shirt)
        Sphere chest = new Sphere(new Point3D(0.0, 1.25, 0.0), 0.18);
        chest.texture(shirtTexture);
        scene.add(chest);

        // Pectoraux (relief réaliste)
        Sphere pecLeft = new Sphere(new Point3D(-0.07, 1.27, 0.11), 0.08);
        pecLeft.texture(shirtTexture);
        scene.add(pecLeft);

        Sphere pecRight = new Sphere(new Point3D(0.07, 1.27, 0.11), 0.08);
        pecRight.texture(shirtTexture);
        scene.add(pecRight);

        // Épaules
        Sphere shoulderLeft = new Sphere(new Point3D(-0.18, 1.36, 0.0), 0.075);
        shoulderLeft.texture(shirtTexture);
        scene.add(shoulderLeft);

        Sphere shoulderRight = new Sphere(new Point3D(0.18, 1.36, 0.0), 0.075);
        shoulderRight.texture(shirtTexture);
        scene.add(shoulderRight);

        // Abdomen / Taille
        Sphere belly = new Sphere(new Point3D(0.0, 1.05, -0.01), 0.16);
        belly.texture(shirtTexture);
        scene.add(belly);

        // Pelvis / Bassin (Début du pantalon)
        Sphere pelvis = new Sphere(new Point3D(0.0, 0.86, 0.0), 0.17);
        pelvis.texture(pantsTexture);
        scene.add(pelvis);

        // ==========================================
        // 6. MEMBRES SUPÉRIEURS (Bras)
        // ==========================================
        
        // BRAS GAUCHE
        // Manche du T-shirt
        Cylinder sleeveLeft = new Cylinder(new Point3D(-0.18, 1.36, 0.0), new Point3D(-0.23, 1.23, 0.0), 0.05);
        sleeveLeft.texture(shirtTexture);
        scene.add(sleeveLeft);

        // Biceps / Triceps (peau)
        Cylinder armLeft = new Cylinder(new Point3D(-0.23, 1.23, 0.0), new Point3D(-0.28, 1.08, -0.02), 0.04);
        armLeft.texture(skinTexture);
        scene.add(armLeft);

        Sphere bicepsLeft = new Sphere(new Point3D(-0.25, 1.15, -0.01), 0.045);
        bicepsLeft.texture(skinTexture);
        scene.add(bicepsLeft);

        // Coude
        Sphere elbowLeft = new Sphere(new Point3D(-0.28, 1.08, -0.02), 0.042);
        elbowLeft.texture(skinTexture);
        scene.add(elbowLeft);

        // Avant-bras
        Cylinder forearmLeft = new Cylinder(new Point3D(-0.28, 1.08, -0.02), new Point3D(-0.31, 0.88, 0.05), 0.035);
        forearmLeft.texture(skinTexture);
        scene.add(forearmLeft);

        // Main
        Sphere handLeft = new Sphere(new Point3D(-0.31, 0.88, 0.05), 0.04);
        handLeft.texture(skinTexture);
        scene.add(handLeft);

        // BRAS DROIT
        // Manche du T-shirt
        Cylinder sleeveRight = new Cylinder(new Point3D(0.18, 1.36, 0.0), new Point3D(0.23, 1.23, 0.0), 0.05);
        sleeveRight.texture(shirtTexture);
        scene.add(sleeveRight);

        // Biceps / Triceps (peau)
        Cylinder armRight = new Cylinder(new Point3D(0.23, 1.23, 0.0), new Point3D(0.28, 1.08, -0.02), 0.04);
        armRight.texture(skinTexture);
        scene.add(armRight);

        Sphere bicepsRight = new Sphere(new Point3D(0.25, 1.15, -0.01), 0.045);
        bicepsRight.texture(skinTexture);
        scene.add(bicepsRight);

        // Coude
        Sphere elbowRight = new Sphere(new Point3D(0.28, 1.08, -0.02), 0.042);
        elbowRight.texture(skinTexture);
        scene.add(elbowRight);

        // Avant-bras
        Cylinder forearmRight = new Cylinder(new Point3D(0.28, 1.08, -0.02), new Point3D(0.31, 0.88, 0.05), 0.035);
        forearmRight.texture(skinTexture);
        scene.add(forearmRight);

        // Main
        Sphere handRight = new Sphere(new Point3D(0.31, 0.88, 0.05), 0.04);
        handRight.texture(skinTexture);
        scene.add(handRight);

        // ==========================================
        // 7. MEMBRES INFÉRIEURS (Jambes & Pieds)
        // ==========================================
        
        // JAMBE GAUCHE
        // Cuisse (pantalon jean)
        Cylinder thighLeft = new Cylinder(new Point3D(-0.09, 0.84, 0.0), new Point3D(-0.09, 0.48, 0.0), 0.085);
        thighLeft.texture(pantsTexture);
        scene.add(thighLeft);

        // Genou (peau)
        Sphere kneeLeft = new Sphere(new Point3D(-0.09, 0.48, 0.0), 0.065);
        kneeLeft.texture(skinTexture);
        scene.add(kneeLeft);

        // Mollet
        Cylinder calfLeft = new Cylinder(new Point3D(-0.09, 0.48, 0.0), new Point3D(-0.09, 0.12, 0.0), 0.055);
        calfLeft.texture(skinTexture);
        scene.add(calfLeft);

        // Muscle galbé du mollet arrière
        Sphere calfMuscleLeft = new Sphere(new Point3D(-0.09, 0.36, -0.02), 0.065);
        calfMuscleLeft.texture(skinTexture);
        scene.add(calfMuscleLeft);

        // Cheville
        Sphere ankleLeft = new Sphere(new Point3D(-0.09, 0.12, 0.0), 0.045);
        ankleLeft.texture(skinTexture);
        scene.add(ankleLeft);

        // JAMBE DROITE
        // Cuisse (pantalon jean)
        Cylinder thighRight = new Cylinder(new Point3D(0.09, 0.84, 0.0), new Point3D(0.09, 0.48, 0.0), 0.085);
        thighRight.texture(pantsTexture);
        scene.add(thighRight);

        // Genou (peau)
        Sphere kneeRight = new Sphere(new Point3D(0.09, 0.48, 0.0), 0.065);
        kneeRight.texture(skinTexture);
        scene.add(kneeRight);

        // Mollet
        Cylinder calfRight = new Cylinder(new Point3D(0.09, 0.48, 0.0), new Point3D(0.09, 0.12, 0.0), 0.055);
        calfRight.texture(skinTexture);
        scene.add(calfRight);

        // Muscle galbé du mollet arrière
        Sphere calfMuscleRight = new Sphere(new Point3D(0.09, 0.36, -0.02), 0.065);
        calfMuscleRight.texture(skinTexture);
        scene.add(calfMuscleRight);

        // Cheville
        Sphere ankleRight = new Sphere(new Point3D(0.09, 0.12, 0.0), 0.045);
        ankleRight.texture(skinTexture);
        scene.add(ankleRight);

        // CHAUSSURES (Pieds)
        Cylinder footLeft = new Cylinder(new Point3D(-0.09, 0.06, -0.05), new Point3D(-0.09, 0.04, 0.15), 0.05);
        footLeft.texture(shoeTexture);
        scene.add(footLeft);

        Cylinder footRight = new Cylinder(new Point3D(0.09, 0.06, -0.05), new Point3D(0.09, 0.04, 0.15), 0.05);
        footRight.texture(shoeTexture);
        scene.add(footRight);


        // ==========================================
        // 8. CONFIGURATION DE LA CAMÉRA & CONTEXTE DE RENDU
        // ==========================================
        
        int width = 1200;
        int height = 1600;

        // Positionnement de la caméra : de face, légèrement surélevée et reculée
        // Le lookAt cible le milieu du torse du personnage (y = 0.9)
        Point3D cameraEye = new Point3D(0.0, 1.0, 3.2);
        Point3D cameraLookAt = new Point3D(0.0, 0.9, 0.0);
        
        // Règle 8 : Utilisation obligatoire d'un vecteur "haut" explicite (Point3D.Y)
        Camera camera = new Camera(cameraEye, cameraLookAt, Point3D.Y);
        scene.cameraActive(camera);

        // 9. Calcul du rendu 3D via le ZBuffer
        ZBufferImpl zBuffer = new ZBufferImpl(width, height);
        zBuffer.scene(scene);
        zBuffer.camera(camera);
        zBuffer.draw();

        // 10. Sauvegarde de l'image de sortie
        try {
            Image image = zBuffer.image();
            File outputFile = new File("corps_humain.png");
            
            // Règle 11 : Utilisation exclusive de la méthode saveFile(File)
            image.saveFile(outputFile);
            System.out.println("Rendu réussi avec succès ! L'image du corps humain a été enregistrée sous : " + outputFile.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Une erreur est survenue lors de la sauvegarde de l'image.");
            e.printStackTrace();
        }
    }
}
