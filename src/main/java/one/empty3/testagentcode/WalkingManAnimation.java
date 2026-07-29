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
import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.libs.Color;

public class WalkingManAnimation extends TestObjetSub {
    @Override
    public void ginit() {
        // Initialisation de la scène globale
        scene = new Scene();
    }

    @Override
    public void finit() throws Exception {
        super.finit();
        // 1. Effacer la scène du frame précédent pour dessiner le nouveau frame
        scene.clear();
        // 2. Calculer le progrès de l'animation (de 0.0 à 1.0)
        double progress = (double) frame() / getMaxFrames();
        // 3. Position d'avancement de l'homme sur l'axe Z
        double startZ = -3.0;
        double endZ = 3.0;
        double currentZ = startZ + (endZ - startZ) * progress;
        // 4. Calcul de l'angle d'oscillation des membres (4 cycles de marche complets)
        double cycles = (double) getMaxFrames() / getFps();
        double angle = Math.sin(progress * Math.PI * 2.0 * cycles) * 0.4;
        // 5. Définition des couleurs
        Color skinColor = Color.newCol(0.95f, 0.80f, 0.69f);
        Color shirtColor = Color.newCol(0.10f, 0.50f, 0.80f);
        Color pantsColor = Color.newCol(0.15f, 0.15f, 0.25f);
        Color shoeColor = Color.newCol(0.20f, 0.10f, 0.05f);
        // --- Tête (Sphère) ---
        Sphere head = new Sphere(new Point3D(0.0, 1.8, currentZ), 0.2);
        head.texture(new ColorTexture(skinColor));
        scene.add(head);
        // --- Torse (Boîte) ---
        Box torso = new Box(0.4, 0.6, 0.2);
        torso.setOrig(new Point3D(-0.2, 1.0, currentZ - 0.1));
        torso.texture(new ColorTexture(shirtColor));
        scene.add(torso);
        // --- Bras gauche (Cylindre balancé en opposition à la jambe gauche) ---
        Point3D leftShoulder = new Point3D(-0.25, 1.5, currentZ);
        double armLength = 0.6;
        double leftHandZ = currentZ + armLength * Math.sin(-angle);
        double leftHandY = 1.5 - armLength * Math.cos(-angle);
        Point3D leftHand = new Point3D(-0.25, leftHandY + 0.002, leftHandZ);
        Cylinder leftArm = new Cylinder(leftShoulder, leftHand, 0.15);
        leftArm.texture(new ColorTexture(skinColor));
        scene.add(leftArm);
        // --- Bras droit ---
        Point3D rightShoulder = new Point3D(0.25, 1.5, currentZ);
        double rightHandZ = currentZ + armLength * Math.sin(angle);
        double rightHandY = 1.5 - armLength * Math.cos(angle);
        Point3D rightHand = new Point3D(0.25, rightHandY - 0.002, rightHandZ);
        Cylinder rightArm = new Cylinder(rightShoulder, rightHand, 0.15);
        rightArm.texture(new ColorTexture(skinColor));
        scene.add(rightArm);
        // --- Jambe gauche (Cylindre) ---
        Point3D leftHip = new Point3D(-0.12, 1.0, currentZ);
        double legLength = 0.9;
        double leftFootZ = currentZ + legLength * Math.sin(angle);
        double leftFootY = 1.0 - legLength * Math.cos(angle);
        Point3D leftFoot = new Point3D(-0.12, leftFootY + 0.002, leftFootZ);
        Cylinder leftLeg = new Cylinder(leftHip, leftFoot, 0.20);
        leftLeg.texture(new ColorTexture(pantsColor));
        scene.add(leftLeg);
        // --- Jambe droite ---
        Point3D rightHip = new Point3D(0.12, 1.0, currentZ);
        double rightFootZ = currentZ + legLength * Math.sin(-angle);
        double rightFootY = 1.0 - legLength * Math.cos(-angle);
        Point3D rightFoot = new Point3D(0.12, rightFootY - 0.001, rightFootZ);
        Cylinder rightLeg = new Cylinder(rightHip, rightFoot, 0.20);
        rightLeg.texture(new ColorTexture(pantsColor));
        scene.add(rightLeg);
        // --- Chaussures (Sphères au bout des jambes) ---
        Sphere leftShoe = new Sphere(leftFoot, 0.09);
        leftShoe.texture(new ColorTexture(shoeColor));
        scene.add(leftShoe);
        Sphere rightShoe = new Sphere(rightFoot, 0.09);
        rightShoe.texture(new ColorTexture(shoeColor));
        scene.add(rightShoe);
        // --- Sol ---
        Box ground = new Box(10.0, 10, 10.0);
        ground.setOrig(new Point3D(-5.0, -0.01, -5.0));
        ground.texture(new ColorTexture(Color.newCol(0.3f, 0.3f, 0.3f)));
        scene.add(ground);
        // --- Caméra active qui traque le personnage en déplacement ---
        Point3D cameraPos = new Point3D(2.5, 2.0, currentZ + 3.0);
        Point3D lookAt = new Point3D(0.0, 1.0, currentZ);
        Camera camera = new Camera(cameraPos, lookAt, Point3D.Y);
        scene.cameraActive(camera);
    }

    public static void main(String[] args) {
        WalkingManAnimation animation = new WalkingManAnimation();
        animation.loop(true);
        animation.setMaxFrames(25 * 20);
        // Rendu de 200 images de marche
        new Thread(animation).start();
    }
}