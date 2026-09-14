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
 *  * Created by Manuel D Dahmen -2026
 *
 *
 */

package one.empty3.testagentcode;

import one.empty3.apps.testobject.Resolution;
import one.empty3.library.*;
import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.libs.Color;

import java.util.ArrayList;
import java.util.List;

public class CinematicManAnimation extends TestObjetSub {
    // Membres du personnage
    private Sphere head;
    private Box torso;
    private Cylinder leftArm, rightArm;
    private Cylinder leftLeg, rightLeg;

    // Décors
    private List<Box> buildings = new ArrayList<>();
    private Box bridgeRoad;
    private List<Cylinder> bridgeArches = new ArrayList<>();
    private Box water;

    private int frameCount = 0;

    @Override
    public void ginit() {
        scene = new Scene();

        // 1. Génération des bâtiments en arrière-plan (Phase 1)
        for (int i = 0; i < 8; i++) {
            double zOffset = -6.0 + i * 2.0;
            // Bâtiments à gauche
            Box bLeft = new Box(0.6, 2.5 + (i % 3) * 0.8, 0.6);
            bLeft.setOrig(new Point3D(-1.8, 0.0, zOffset));
            bLeft.texture(new ColorTexture(Color.newCol(0.15f, 0.15f, 0.18f)));
            buildings.add(bLeft);

            // Bâtiments à droite
            Box bRight = new Box(0.6, 2.0 + (i % 2) * 1.2, 0.6);
            bRight.setOrig(new Point3D(1.8, 0.0, zOffset));
            bRight.texture(new ColorTexture(Color.newCol(0.12f, 0.12f, 0.15f)));
            buildings.add(bRight);
        }

        // 2. Génération du pont (Phase 2)
        // La route s'étend de Z = 2.0 à Z = 7.0 à une hauteur Y = 1.0 (niveau des pieds)
        bridgeRoad = new Box(1.2, 0.1, 5.0);
        bridgeRoad.setOrig(new Point3D(-0.6, 1.0, 2.0));
        bridgeRoad.texture(new ColorTexture(Color.newCol(0.35f, 0.35f, 0.38f)));

        // Arches/Piliers du pont
        for (double z = 2.0; z <= 7.0; z += 1.0) {
            Cylinder leftPillar = new Cylinder(new Point3D(-0.6, -1.5, z), new Point3D(-0.6, 1.0, z), 0.08);
            leftPillar.texture(new ColorTexture(Color.newCol(0.4f, 0.4f, 0.4f)));
            bridgeArches.add(leftPillar);

            Cylinder rightPillar = new Cylinder(new Point3D(0.6, -1.5, z), new Point3D(0.6, 1.0, z), 0.08);
            rightPillar.texture(new ColorTexture(Color.newCol(0.4f, 0.4f, 0.4f)));
            bridgeArches.add(rightPillar);
        }

        // 3. Génération de l'eau (Phase 3)
        // Située en contrebas à Y = -1.5
        water = new Box(15.0, 0.1, 15.0);
        water.setOrig(new Point3D(-7.5, -1.5, 3.0));
        water.texture(new ColorTexture(Color.newCol(0.0f, 0.3f, 0.7f)));

        // 4. Couleurs et textures du personnage
        Color skinColor = Color.newCol(0.95f, 0.80f, 0.69f);
        Color shirtColor = Color.newCol(0.85f, 0.20f, 0.20f); // T-shirt rouge vif
        Color pantsColor = Color.newCol(0.10f, 0.10f, 0.15f); // Pantalon sombre

        // Utilisation d'instances de Point3D indépendantes pour éviter la mutation de Point3D.O0
        head = new Sphere(new Point3D(0.0, 0.0, 0.0), 0.16);
        head.texture(new ColorTexture(skinColor));

        // Torse s'étendant localement de Y = 0.0 à Y = 0.7
        torso = new Box(0.4, 0.7, 0.2);
        torso.texture(new ColorTexture(shirtColor));

        leftArm = new Cylinder(new Point3D(0.0, 0.0, 0.0), new Point3D(0.0, -0.65, 0.0), 0.05);
        leftArm.texture(new ColorTexture(skinColor));

        rightArm = new Cylinder(new Point3D(0.0, 0.0, 0.0), new Point3D(0.0, -0.65, 0.0), 0.05);
        rightArm.texture(new ColorTexture(skinColor));

        leftLeg = new Cylinder(new Point3D(0.0, 0.0, 0.0), new Point3D(0.0, -1.0, 0.0), 0.06);
        leftLeg.texture(new ColorTexture(pantsColor));

        rightLeg = new Cylinder(new Point3D(0.0, 0.0, 0.0), new Point3D(0.0, -1.0, 0.0), 0.06);
        rightLeg.texture(new ColorTexture(pantsColor));
    }

    @Override
    public void finit() throws Exception {
        super.finit();
        scene.clear();

        //z().setIncrementOptimizer(new ZBufferImpl.IncrementOptimizer());//ZBufferImpl.IncrementOptimizer.Strategy.ENSURE_MAXIMUM_PERFORMANCE, 4)

        // Ré-assemblage de la scène
        for (Box b : buildings) scene.add(b);
        scene.add(bridgeRoad);
        for (Cylinder c : bridgeArches) scene.add(c);
        scene.add(water);

        scene.add(head);
        scene.add(torso);
        scene.add(leftArm);
        scene.add(rightArm);
        scene.add(leftLeg);
        scene.add(rightLeg);

        // Variables d'état
        double yHips = 1.0; // Hauteur du bassin (les jambes mesurent 1.0, donc le bassin est à Y = 1.0)
        double zPos = -6.0;
        double swingAngle = 0.0;
        double torsoLean = 0.0;
        double diveRotation = 0.0;

        // Contrôle des phases de l'animation
        if (frameCount <= 100) {
            // PHASE 1 : Marche dans la ville
            double p = frameCount / 100.0;
            zPos = -5.0 + p * 7.0; // Se déplace de Z = -5.0 à Z = 2.0 (entrée du pont)
            yHips = 1.0;
            swingAngle = Math.sin(frameCount * 0.15) * 0.35; // Oscillation tranquille
            torsoLean = 0.05;
        } else if (frameCount <= 200) {
            // PHASE 2 : Course sur le pont
            double p = (frameCount - 100) / 100.0;
            zPos = 2.0 + p * 5.0; // Court de Z = 2.0 à Z = 7.0 (bout du pont)

            // Rebond de course (les pieds touchent le sol à Y = 1.0)
            yHips = 1.0 + Math.abs(Math.sin(frameCount * 0.35)) * 0.12;
            swingAngle = Math.sin(frameCount * 0.35) * 0.75; // Grands pas athlétiques
            torsoLean = 0.22; // Buste penché vers l'avant
        } else {
            // PHASE 3 : Le grand plongeon !
            double p = (frameCount - 200) / 100.0;
            zPos = 7.0 + p * 2.0; // Inertie vers l'avant

            // Parabole de saut (saut puis chute rapide vers l'eau)
            yHips = 1.0 + 1.8 * p - 4.5 * p * p;

            // Rotation du corps pour plonger la tête la première
            diveRotation = p * Math.PI * 0.65; // Pivote de ~115 degrés vers l'avant
            swingAngle = 0.05; // Jambes tendues ensemble
        }

        double totalRotation = torsoLean + diveRotation;

        // Positionnement du torse (origine au niveau du bassin)
        Point3D torsoOrig = new Point3D(0.0, yHips, zPos);
        torso.setOrig(torsoOrig);
        rotateX(torso, totalRotation);

        // Positionnement de la tête (située à 0.9 au-dessus du bassin lorsqu'il est debout)
        Point3D headOffset = new Point3D(0.0, 0.9, 0.0);
        head.setOrig(torsoOrig.plus(rotateVectorX(headOffset, totalRotation)));
        rotateX(head, totalRotation);

        // Jambe gauche (s'attache à la hanche gauche)
        Point3D leftHipOffset = new Point3D(-0.15, 0.0, 0.0);
        leftLeg.setOrig(torsoOrig.plus(rotateVectorX(leftHipOffset, totalRotation)));
        rotateX(leftLeg, swingAngle + totalRotation);

        // Jambe droite (s'attache à la hanche droite)
        Point3D rightHipOffset = new Point3D(0.15, 0.0, 0.0);
        rightLeg.setOrig(torsoOrig.plus(rotateVectorX(rightHipOffset, totalRotation)));
        rotateX(rightLeg, -swingAngle + totalRotation);

        // Bras gauche (s'attache à l'épaule gauche à Y = +0.6 par rapport au bassin)
        Point3D leftShoulderOffset = new Point3D(-0.25, 0.6, 0.0);
        leftArm.setOrig(torsoOrig.plus(rotateVectorX(leftShoulderOffset, totalRotation)));
        if (frameCount > 200) {
            // Pendant le plongeon, les bras se tendent vers l'avant (au-dessus de la tête)
            rotateX(leftArm, -Math.PI * 0.85 + totalRotation);
        } else {
            rotateX(leftArm, -swingAngle + totalRotation);
        }

        // Bras droit (s'attache à l'épaule droite)
        Point3D rightShoulderOffset = new Point3D(0.25, 0.6, 0.0);
        rightArm.setOrig(torsoOrig.plus(rotateVectorX(rightShoulderOffset, totalRotation)));
        if (frameCount > 200) {
            rotateX(rightArm, -Math.PI * 0.85 + totalRotation);
        } else {
            rotateX(rightArm, swingAngle + totalRotation);
        }

        // --- GESTION DE LA CAMÉRA DYNAMIQUE ---
        Point3D cameraPos;
        Point3D lookAtTarget = new Point3D(0.0, yHips, zPos);

        if (frameCount <= 100) {
            // Suivi latéral de la marche
            cameraPos = new Point3D(2.5, 2.0, zPos + 1.5);
        } else if (frameCount <= 200) {
            // Travelling de face/trois-quarts pendant la course
            cameraPos = new Point3D(2.8, 1.8, zPos + 2.5);
        } else {
            // Plan fixe large depuis le côté du pont pour capturer la chute vers l'eau
            cameraPos = new Point3D(3.5, 1.2, 6.0);
        }
        Camera camera = new Camera(cameraPos, lookAtTarget, Point3D.Y);
        scene.cameraActive(camera);

        frameCount++;
    }

    // Effectue une matrice de rotation locale sur l'axe X (Règle 5)
    private void rotateX(Representable obj, double theta) {
        obj.setVectX(Point3D.X);
        obj.setVectY(new Point3D(0.0, Math.cos(theta), Math.sin(theta)));
        obj.setVectZ(new Point3D(0.0, -Math.sin(theta), Math.cos(theta)));
    }

    // Effectue une rotation 3D d'un vecteur d'attache sur l'axe X pour lier les membres au corps pivoté
    private Point3D rotateVectorX(Point3D vec, double theta) {
        double y = vec.getY() * Math.cos(theta) - vec.getZ() * Math.sin(theta);
        double z = vec.getY() * Math.sin(theta) + vec.getZ() * Math.cos(theta);
        return new Point3D(vec.getX(), y, z);
    }

    public static void main(String[] args) {
        CinematicManAnimation cinematic = new CinematicManAnimation();
        cinematic.loop(true);
        cinematic.setDimension(new Resolution(320, 240));
        cinematic.setMaxFrames(300);
        new Thread(cinematic).start();
    }
}