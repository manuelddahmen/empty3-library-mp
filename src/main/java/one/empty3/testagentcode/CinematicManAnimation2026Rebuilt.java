/*
 *
 *  * Copyright (c) 2026. Manuel Daniel Dahmen
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *     http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package one.empty3.testagentcode;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.*;
import one.empty3.libs.Color;

/*
 * Version de CinematicManAnimation2026 où tous les éléments de la scène
 * sont reconstruits à chaque étape (frame) à partir des coordonnées calculées,
 * sans utiliser Representable.setOrig() ni setVectX() pour le déplacement/animation.
 */
public class CinematicManAnimation2026Rebuilt extends TestObjetSub {
    private int frameCount = 0;

    static void main(String[] args) {
        CinematicManAnimation2026Rebuilt cinematic = new CinematicManAnimation2026Rebuilt();
        cinematic.loop(true);
        cinematic.setMaxFrames(300);
        cinematic.setGenerate(GENERATE_IMAGE | GENERATE_MOVIE | GENERATE_SAVE_IMAGE);
        new Thread(cinematic).start();
    }

    @Override
    public void ginit() {
        scene = new Scene();
    }

    @Override
    public void finit() throws Exception {
        super.finit();
        scene.clear();

        // 1. Génération des gratte-ciels en arrière-plan (reconstruits à chaque frame)
        for (int i = 0; i < 8; i++) {
            double zOffset = -6.0 + i * 2.0;

            // Bâtiment gauche
            double wLeft = 0.6, hLeft = 2.5 + (i % 3) * 0.8, dLeft = 0.6;
            Point3D origLeft = new Point3D(-1.8, 0.0, zOffset);
            Point3D aLeft = origLeft.plus(new Point3D(wLeft, 0.0, 0.0));
            Point3D bLeftPt = origLeft.plus(new Point3D(0.0, hLeft, 0.0));
            Point3D cLeftPt = origLeft.plus(new Point3D(0.0, 0.0, dLeft));
            Box bLeft = new Box(origLeft, aLeft, bLeftPt, cLeftPt, new ColorTexture(Color.newCol(0.15f, 0.15f, 0.18f)));
            scene.add(bLeft);

            // Bâtiment droit
            double wRight = 0.6, hRight = 2.0 + (i % 2) * 1.2, dRight = 0.6;
            Point3D origRight = new Point3D(1.8, 0.0, zOffset);
            Point3D aRight = origRight.plus(new Point3D(wRight, 0.0, 0.0));
            Point3D bRightPt = origRight.plus(new Point3D(0.0, hRight, 0.0));
            Point3D cRightPt = origRight.plus(new Point3D(0.0, 0.0, dRight));
            Box bRight = new Box(origRight, aRight, bRightPt, cRightPt, new ColorTexture(Color.newCol(0.12f, 0.12f, 0.15f)));
            scene.add(bRight);
        }

        // 2. Génération du pont (reconstruit à chaque frame)
        Point3D bridgeOrig = new Point3D(-0.6, 1.0, 2.0);
        Point3D bridgeA = bridgeOrig.plus(new Point3D(1.2, 0.0, 0.0));
        Point3D bridgeB = bridgeOrig.plus(new Point3D(0.0, 0.1, 0.0));
        Point3D bridgeC = bridgeOrig.plus(new Point3D(0.0, 0.0, 5.0));
        Box bridgeRoad = new Box(bridgeOrig, bridgeA, bridgeB, bridgeC, new ColorTexture(Color.newCol(0.35f, 0.35f, 0.38f)));
        scene.add(bridgeRoad);

        for (double z = 2.0; z <= 7.0; z += 1.0) {
            Cylinder leftPillar = new Cylinder(new Point3D(-0.6, -1.5, z), new Point3D(-0.6, 1.0, z), 0.08);
            leftPillar.texture(new ColorTexture(Color.newCol(0.4f, 0.4f, 0.4f)));
            scene.add(leftPillar);

            Cylinder rightPillar = new Cylinder(new Point3D(0.6, -1.5, z), new Point3D(0.6, 1.0, z), 0.08);
            rightPillar.texture(new ColorTexture(Color.newCol(0.4f, 0.4f, 0.4f)));
            scene.add(rightPillar);
        }

        // 3. Génération de l'eau (reconstruite à chaque frame)
        Point3D waterOrig = new Point3D(-7.5, -1.5, 3.0);
        Point3D waterA = waterOrig.plus(new Point3D(15.0, 0.0, 0.0));
        Point3D waterB = waterOrig.plus(new Point3D(0.0, 0.1, 0.0));
        Point3D waterC = waterOrig.plus(new Point3D(0.0, 0.0, 15.0));
        Box water = new Box(waterOrig, waterA, waterB, waterC, new ColorTexture(Color.newCol(0.0f, 0.3f, 0.7f)));
        scene.add(water);

        // 4. Couleurs
        Color skinColor = Color.newCol(0.95f, 0.80f, 0.69f);
        Color shirtColor = Color.newCol(0.85f, 0.20f, 0.20f);
        Color pantsColor = Color.newCol(0.10f, 0.10f, 0.15f);

        // États physiques initiaux
        double yHips = 1.0;
        double zPos = -6.0;
        double swingAngle = 0.0;
        double torsoLean = 0.0;
        double diveRotation = 0.0;

        if (frameCount <= 100) {
            double p = frameCount / 100.0;
            zPos = -5.0 + p * 7.0;
            yHips = 1.0;
            swingAngle = Math.sin(frameCount * 0.15) * 0.35;
            torsoLean = 0.05;
        } else if (frameCount <= 200) {
            double p = (frameCount - 100) / 100.0;
            zPos = 2.0 + p * 5.0;
            yHips = 1.0 + Math.abs(Math.sin(frameCount * 0.35)) * 0.12;
            swingAngle = Math.sin(frameCount * 0.35) * 0.75;
            torsoLean = 0.22;
        } else {
            double p = (frameCount - 200) / 100.0;
            zPos = 7.0 + p * 2.0;
            yHips = 1.0 + 1.8 * p - 4.5 * p * p;
            diveRotation = p * Math.PI * 0.65;
            swingAngle = 0.05;
        }

        double totalRotation = torsoLean + diveRotation;
        Point3D torsoOrig = new Point3D(0.0, yHips, zPos);

        // Torso Box reconstruit à partir des coordonnées calculées et orientées
        double tw = 0.4, th = 0.7, td = 0.2;
        Point3D torsoLocalBase = new Point3D(-tw / 2.0, 0.0, -td / 2.0);
        Point3D torsoBase = torsoOrig.plus(rotateVectorX(torsoLocalBase, totalRotation));
        Point3D torsoA = torsoBase.plus(new Point3D(tw, 0.0, 0.0)); // X axis unaffected by X rotation
        Point3D torsoB = torsoBase.plus(rotateVectorX(new Point3D(0.0, th, 0.0), totalRotation));
        Point3D torsoC = torsoBase.plus(rotateVectorX(new Point3D(0.0, 0.0, td), totalRotation));
        Box torso = new Box(torsoBase, torsoA, torsoB, torsoC, new ColorTexture(shirtColor));
        scene.add(torso);

        // Head Sphere reconstruit
        Point3D headOffset = new Point3D(0.0, 0.9, 0.0);
        Point3D headCenter = torsoOrig.plus(rotateVectorX(headOffset, totalRotation));
        Sphere head = new Sphere(headCenter, 0.16);
        head.texture(new ColorTexture(skinColor));
        scene.add(head);

        // Jambe gauche
        Point3D leftHipOffset = new Point3D(-0.15, 0.0, 0.0);
        Point3D leftHip = torsoOrig.plus(rotateVectorX(leftHipOffset, totalRotation));
        double leftLegAngle = swingAngle + totalRotation;
        Point3D leftFoot = leftHip.plus(rotateVectorX(new Point3D(0.0, -1.0, 0.0), leftLegAngle));
        Cylinder leftLeg = new Cylinder(leftHip, leftFoot, 0.06);
        leftLeg.texture(new ColorTexture(pantsColor));
        scene.add(leftLeg);

        // Jambe droite
        Point3D rightHipOffset = new Point3D(0.15, 0.0, 0.0);
        Point3D rightHip = torsoOrig.plus(rotateVectorX(rightHipOffset, totalRotation));
        double rightLegAngle = -swingAngle + totalRotation;
        Point3D rightFoot = rightHip.plus(rotateVectorX(new Point3D(0.0, -1.0, 0.0), rightLegAngle));
        Cylinder rightLeg = new Cylinder(rightHip, rightFoot, 0.06);
        rightLeg.texture(new ColorTexture(pantsColor));
        scene.add(rightLeg);

        // Bras gauche
        Point3D leftShoulderOffset = new Point3D(-0.25, 0.6, 0.0);
        Point3D leftShoulder = torsoOrig.plus(rotateVectorX(leftShoulderOffset, totalRotation));
        double leftArmAngle = (frameCount > 200) ? -Math.PI * 0.85 + totalRotation : -swingAngle + totalRotation;
        Point3D leftHand = leftShoulder.plus(rotateVectorX(new Point3D(0.0, -0.65, 0.0), leftArmAngle));
        Cylinder leftArm = new Cylinder(leftShoulder, leftHand, 0.05);
        leftArm.texture(new ColorTexture(skinColor));
        scene.add(leftArm);

        // Bras droit
        Point3D rightShoulderOffset = new Point3D(0.25, 0.6, 0.0);
        Point3D rightShoulder = torsoOrig.plus(rotateVectorX(rightShoulderOffset, totalRotation));
        double rightArmAngle = (frameCount > 200) ? -Math.PI * 0.85 + totalRotation : swingAngle + totalRotation;
        Point3D rightHand = rightShoulder.plus(rotateVectorX(new Point3D(0.0, -0.65, 0.0), rightArmAngle));
        Cylinder rightArm = new Cylinder(rightShoulder, rightHand, 0.05);
        rightArm.texture(new ColorTexture(skinColor));
        scene.add(rightArm);

        // Caméra
        Point3D cameraPos;
        Point3D lookAtTarget = new Point3D(0.0, yHips, zPos);

        if (frameCount <= 100) {
            cameraPos = new Point3D(2.5, 2.0, zPos + 1.5);
        } else if (frameCount <= 200) {
            cameraPos = new Point3D(2.8, 1.8, zPos + 2.5);
        } else {
            cameraPos = new Point3D(3.5, 1.2, 6.0);
        }

        Camera camera = new Camera(cameraPos, lookAtTarget, Point3D.Y);
        scene.cameraActive(camera);

        frameCount++;
    }

    private Point3D rotateVectorX(Point3D vec, double theta) {
        double y = vec.getY() * Math.cos(theta) - vec.getZ() * Math.sin(theta);
        double z = vec.getY() * Math.sin(theta) + vec.getZ() * Math.cos(theta);
        return new Point3D(vec.getX(), y, z);
    }
}
