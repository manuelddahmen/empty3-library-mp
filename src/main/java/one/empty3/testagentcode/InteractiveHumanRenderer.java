package one.empty3.testagentcode;

import one.empty3.library.*;
import one.empty3.libs.Color;
import one.empty3.libs.Image;

import java.io.File;

public class InteractiveHumanRenderer {

    // Définition de l'objet anatomique articulé
    public static class HumanBody {
        // Articulations clés (Points 3D)
        // AJOUT : "nose" est maintenant correctement déclaré ici
        public Point3D head, jaw, nose, neck;
        public Point3D shoulderLeft, shoulderRight;
        public Point3D elbowLeft, elbowRight;
        public Point3D wristLeft, wristRight;
        public Point3D handLeft, handRight;
        public Point3D hipLeft, hipRight;
        public Point3D kneeLeft, kneeRight;
        public Point3D ankleLeft, ankleRight;
        public Point3D footLeftStart, footLeftEnd;
        public Point3D footRightStart, footRightEnd;

        // Textures de couleurs
        private static final ColorTexture skinTex = new ColorTexture(Color.newCol(0.92f, 0.76f, 0.65f));
        private static final ColorTexture hairTex = new ColorTexture(Color.newCol(0.20f, 0.12f, 0.08f));
        private static final ColorTexture eyeWhiteTex = new ColorTexture(Color.newCol(0.95f, 0.95f, 0.95f));
        private static final ColorTexture irisTex = new ColorTexture(Color.newCol(0.15f, 0.35f, 0.60f));
        private static final ColorTexture shirtTex = new ColorTexture(Color.newCol(0.85f, 0.15f, 0.15f));
        private static final ColorTexture pantsTex = new ColorTexture(Color.newCol(0.12f, 0.25f, 0.45f));
        private static final ColorTexture shoeTex = new ColorTexture(Color.newCol(0.15f, 0.15f, 0.15f));

        public HumanBody() {
            poseStand(); // Pose par défaut au démarrage
        }

        // --- POSTURE 1 : Debout standard ---
        public void poseStand() {
            double yHead = 1.62;
            double yJaw = 1.54;
            double yNose = 1.52; // Hauteur du nez
            double yNeck = 1.44;
            double yShoulders = 1.36;
            double yElbows = 1.08;
            double yWrists = 0.88;
            double yHips = 0.84;
            double yKnees = 0.48;
            double yAnkles = 0.12;

            head = new Point3D(0.0, yHead, 0.0);
            jaw = new Point3D(0.0, yJaw, 0.03);
            nose = new Point3D(0.0, yNose, 0.10); // AJOUT : Initialisation du nez
            neck = new Point3D(0.0, yNeck, 0.0);

            shoulderLeft = new Point3D(-0.18, yShoulders, 0.0);
            shoulderRight = new Point3D(0.18, yShoulders, 0.0);
            elbowLeft = new Point3D(-0.25, yElbows, -0.02);
            elbowRight = new Point3D(0.25, yElbows, -0.02);
            wristLeft = new Point3D(-0.28, yWrists, 0.02);
            wristRight = new Point3D(0.28, yWrists, 0.02);
            handLeft = new Point3D(-0.30, yWrists - 0.10, 0.04);
            handRight = new Point3D(0.30, yWrists - 0.10, 0.04);
            hipLeft = new Point3D(-0.09, yHips, 0.0);
            hipRight = new Point3D(0.09, yHips, 0.0);
            kneeLeft = new Point3D(-0.09, yKnees, 0.0);
            kneeRight = new Point3D(0.09, yKnees, 0.0);
            ankleLeft = new Point3D(-0.09, yAnkles, 0.0);
            ankleRight = new Point3D(0.09, yAnkles, 0.0);
            footLeftStart = new Point3D(-0.09, yAnkles - 0.06, -0.05);
            footLeftEnd = new Point3D(-0.09, yAnkles - 0.08, 0.15);
            footRightStart = new Point3D(0.09, yAnkles - 0.06, -0.05);
            footRightEnd = new Point3D(0.09, yAnkles - 0.08, 0.15);
        }

        // --- POSTURE 2 : Mains en l'air ---
        public void poseHandsInTheAir() {
            poseStand(); // Garde la base stable et le nez déjà configuré
            // Élévation verticale des coudes, poignets et mains
            elbowLeft = new Point3D(-0.22, 1.55, 0.0);
            elbowRight = new Point3D(0.22, 1.55, 0.0);
            wristLeft = new Point3D(-0.25, 1.82, 0.05);
            wristRight = new Point3D(0.25, 1.82, 0.05);
            handLeft = new Point3D(-0.27, 1.96, 0.08);
            handRight = new Point3D(0.27, 1.96, 0.08);
        }

        // --- POSTURE 3 : S'asseoir ---
        public void poseSit() {
            double ySeat = 0.45;
            double thighLength = 0.36;
            double dy = 0.84 - ySeat; // Affaissement général du bassin et de la tête

            head = new Point3D(0.0, 1.62 - dy, 0.0);
            jaw = new Point3D(0.0, 1.54 - dy, 0.03);
            nose = new Point3D(0.0, 1.52 - dy, 0.10); // AJOUT : Ajustement de la hauteur du nez assis
            neck = new Point3D(0.0, 1.44 - dy, 0.0);

            shoulderLeft = new Point3D(-0.18, 1.36 - dy, 0.0);
            shoulderRight = new Point3D(0.18, 1.36 - dy, 0.0);

            elbowLeft = new Point3D(-0.24, 1.08 - dy + 0.05, 0.15);
            elbowRight = new Point3D(0.24, 1.08 - dy + 0.05, 0.15);
            wristLeft = new Point3D(-0.21, 0.52, 0.28);
            wristRight = new Point3D(0.21, 0.52, 0.28);
            handLeft = new Point3D(-0.19, 0.49, 0.35);
            handRight = new Point3D(0.19, 0.49, 0.35);

            hipLeft = new Point3D(-0.09, ySeat, 0.0);
            hipRight = new Point3D(0.09, ySeat, 0.0);
            kneeLeft = new Point3D(-0.09, ySeat, thighLength);
            kneeRight = new Point3D(0.09, ySeat, thighLength);
            ankleLeft = new Point3D(-0.09, 0.12, thighLength);
            ankleRight = new Point3D(0.09, 0.12, thighLength);

            footLeftStart = new Point3D(-0.09, 0.06, thighLength - 0.05);
            footLeftEnd = new Point3D(-0.09, 0.04, thighLength + 0.15);
            footRightStart = new Point3D(0.09, 0.06, thighLength - 0.05);
            footRightEnd = new Point3D(0.09, 0.04, thighLength + 0.15);
        }

        // --- POSTURE 4 : Se coucher ---
        public void poseLieDown() {
            poseStand();
            // AJOUT : "nose" subit la rotation 3D de 90° autour de l'axe horizontal X
            head = rotateX90(head);
            jaw = rotateX90(jaw);
            nose = rotateX90(nose);
            neck = rotateX90(neck);
            shoulderLeft = rotateX90(shoulderLeft);
            shoulderRight = rotateX90(shoulderRight);
            elbowLeft = rotateX90(elbowLeft);
            elbowRight = rotateX90(elbowRight);
            wristLeft = rotateX90(wristLeft);
            wristRight = rotateX90(wristRight);
            handLeft = rotateX90(handLeft);
            handRight = rotateX90(handRight);
            hipLeft = rotateX90(hipLeft);
            hipRight = rotateX90(hipRight);
            kneeLeft = rotateX90(kneeLeft);
            kneeRight = rotateX90(kneeRight);
            ankleLeft = rotateX90(ankleLeft);
            ankleRight = rotateX90(ankleRight);
            footLeftStart = rotateX90(footLeftStart);
            footLeftEnd = rotateX90(footLeftEnd);
            footRightStart = rotateX90(footRightStart);
            footRightEnd = rotateX90(footRightEnd);
        }

        private Point3D rotateX90(Point3D p) {
            return new Point3D(p.getX(), 0.12 + p.getZ(), p.getY() - 0.80);
        }

        // --- POSTURE 5 : Se lever (Interpolation continue t de 0.0 à 1.0) ---
        public void poseGetUp(double t) {
            HumanBody lying = new HumanBody();
            lying.poseLieDown();
            HumanBody standing = new HumanBody();
            standing.poseStand();

            head = interpolate(lying.head, standing.head, t);
            jaw = interpolate(lying.jaw, standing.jaw, t);
            nose = interpolate(lying.nose, standing.nose, t); // AJOUT : Interpolation du nez
            neck = interpolate(lying.neck, standing.neck, t);
            shoulderLeft = interpolate(lying.shoulderLeft, standing.shoulderLeft, t);
            shoulderRight = interpolate(lying.shoulderRight, standing.shoulderRight, t);
            elbowLeft = interpolate(lying.elbowLeft, standing.elbowLeft, t);
            elbowRight = interpolate(lying.elbowRight, standing.elbowRight, t);
            wristLeft = interpolate(lying.wristLeft, standing.wristLeft, t);
            wristRight = interpolate(lying.wristRight, standing.wristRight, t);
            handLeft = interpolate(lying.handLeft, standing.handLeft, t);
            handRight = interpolate(lying.handRight, standing.handRight, t);
            hipLeft = interpolate(lying.hipLeft, standing.hipLeft, t);
            hipRight = interpolate(lying.hipRight, standing.hipRight, t);
            kneeLeft = interpolate(lying.kneeLeft, standing.kneeLeft, t);
            kneeRight = interpolate(lying.kneeRight, standing.kneeRight, t);
            ankleLeft = interpolate(lying.ankleLeft, standing.ankleLeft, t);
            ankleRight = interpolate(lying.ankleRight, standing.ankleRight, t);
            footLeftStart = interpolate(lying.footLeftStart, standing.footLeftStart, t);
            footLeftEnd = interpolate(lying.footLeftEnd, standing.footLeftEnd, t);
            footRightStart = interpolate(lying.footRightStart, standing.footRightStart, t);
            footRightEnd = interpolate(lying.footRightEnd, standing.footRightEnd, t);
        }

        private Point3D interpolate(Point3D p1, Point3D p2, double t) {
            return new Point3D(
                    p1.getX() + (p2.getX() - p1.getX()) * t,
                    p1.getY() + (p2.getY() - p1.getY()) * t,
                    p1.getZ() + (p2.getZ() - p1.getZ()) * t
            );
        }

        // --- POSTURE 6 : Marcher ---
        public void poseWalk(double t) {
            poseStand(); // Les positions de la tête et du nez restent neutres

            double swing = 0.24 * Math.sin(t);
            double swingOpp = 0.24 * Math.sin(t + Math.PI);

            kneeLeft = new Point3D(-0.09, 0.48, swing);
            double plieLeft = swing < 0 ? -0.08 * swing : 0.0;
            ankleLeft = new Point3D(-0.09, 0.12 + plieLeft, swing - 0.03);
            footLeftStart = new Point3D(-0.09, 0.06 + plieLeft, swing - 0.08);
            footLeftEnd = new Point3D(-0.09, 0.04 + plieLeft, swing + 0.12);

            kneeRight = new Point3D(0.09, 0.48, swingOpp);
            double plieRight = swingOpp < 0 ? -0.08 * swingOpp : 0.0;
            ankleRight = new Point3D(0.09, 0.12 + plieRight, swingOpp - 0.03);
            footRightStart = new Point3D(0.09, 0.06 + plieRight, swingOpp - 0.08);
            footRightEnd = new Point3D(0.09, 0.04 + plieRight, swingOpp + 0.12);

            elbowLeft = new Point3D(-0.25, 1.08, swingOpp * 1.2);
            wristLeft = new Point3D(-0.27, 0.88, swingOpp * 1.5);
            handLeft = new Point3D(-0.29, 0.78, swingOpp * 1.6);

            elbowRight = new Point3D(0.25, 1.08, swing * 1.2);
            wristRight = new Point3D(0.28, 0.88, swing * 1.5);
            handRight = new Point3D(0.29, 0.78, swing * 1.6);
        }

        // --- POSTURE 7 : Courir ---
        public void poseRun(double t) {
            poseStand();

            double bounce = 0.07 * Math.abs(Math.sin(t)); // Oscillation verticale
            double lean = 0.08; // Inclinaison de course

            // Translation coordonnée de la tête, de la mâchoire et du nez
            head = head.plus(new Point3D(0.0, bounce, lean + 0.04));
            jaw = jaw.plus(new Point3D(0.0, bounce, lean + 0.06));
            nose = nose.plus(new Point3D(0.0, bounce, lean + 0.08)); // AJOUT : Déplacement du nez
            neck = neck.plus(new Point3D(0.0, bounce, lean + 0.03));

            shoulderLeft = shoulderLeft.plus(new Point3D(0.0, bounce, lean));
            shoulderRight = shoulderRight.plus(new Point3D(0.0, bounce, lean));

            double swing = 0.42 * Math.sin(t);
            double swingOpp = 0.42 * Math.sin(t + Math.PI);

            kneeLeft = new Point3D(-0.09, 0.48 + bounce, swing + lean);
            double bendL = swing < 0 ? 0.14 * -swing : 0.01;
            ankleLeft = new Point3D(-0.09, 0.12 + bounce + bendL, swing - bendL * 0.5);
            footLeftStart = new Point3D(-0.09, 0.06 + bounce + bendL, swing - bendL * 0.5 - 0.05);
            footLeftEnd = new Point3D(-0.09, 0.04 + bounce + bendL, swing - bendL * 0.5 + 0.15);

            kneeRight = new Point3D(0.09, 0.48 + bounce, swingOpp + lean);
            double bendR = swingOpp < 0 ? 0.14 * -swingOpp : 0.01;
            ankleRight = new Point3D(0.09, 0.12 + bounce + bendR, swingOpp - bendR * 0.5);
            footRightStart = new Point3D(0.09, 0.06 + bounce + bendR, swingOpp - bendR * 0.5 - 0.05);
            footRightEnd = new Point3D(0.09, 0.04 + bounce + bendR, swingOpp - bendR * 0.5 + 0.15);

            elbowLeft = new Point3D(-0.25, 1.15 + bounce, swingOpp * 0.8 + lean);
            wristLeft = new Point3D(-0.24, 1.25 + bounce, swingOpp * 0.8 + lean + 0.18);
            handLeft = new Point3D(-0.22, 1.24 + bounce, swingOpp * 0.8 + lean + 0.24);

            elbowRight = new Point3D(0.25, 1.15 + bounce, swing * 0.8 + lean);
            wristRight = new Point3D(0.24, 1.25 + bounce, swing * 0.8 + lean + 0.18);
            handRight = new Point3D(0.22, 1.24 + bounce, swing * 0.8 + lean + 0.24);
        }

        // Génère géométriquement les solides du corps dans la scène
        public void drawToScene(Scene scene) {
            // Tête, mâchoire et nez
            addSphere(scene, head, 0.10, skinTex);
            addSphere(scene, jaw, 0.08, skinTex);
            addSphere(scene, nose, 0.015, skinTex); // CORRECTION : "nose" est maintenant parfaitement déclaré et utilisable !

            // Chevelure
            addSphere(scene, head.plus(new Point3D(0.0, 0.08, -0.02)), 0.085, hairTex);
            addSphere(scene, head.plus(new Point3D(-0.06, 0.04, -0.05)), 0.07, hairTex);
            addSphere(scene, head.plus(new Point3D(0.06, 0.04, -0.05)), 0.07, hairTex);
            addSphere(scene, head.plus(new Point3D(0.0, 0.0, -0.08)), 0.08, hairTex);

            // Yeux
            addSphere(scene, jaw.plus(new Point3D(-0.035, 0.03, 0.06)), 0.015, eyeWhiteTex);
            addSphere(scene, jaw.plus(new Point3D(-0.035, 0.03, 0.072)), 0.007, irisTex);
            addSphere(scene, jaw.plus(new Point3D(0.035, 0.03, 0.06)), 0.015, eyeWhiteTex);
            addSphere(scene, jaw.plus(new Point3D(0.035, 0.03, 0.072)), 0.007, irisTex);

            // Cou & Tronc
            addCylinder(scene, head.plus(new Point3D(0.0, -0.12, 0.0)), neck, 0.05, skinTex);

            // Torse / Vêtements
            addSphere(scene, neck.plus(new Point3D(0.0, -0.15, 0.0)), 0.18, shirtTex);
            addSphere(scene, neck.plus(new Point3D(-0.07, -0.13, 0.11)), 0.08, shirtTex); // Pectoraux G
            addSphere(scene, neck.plus(new Point3D(0.07, -0.13, 0.11)), 0.08, shirtTex);  // Pectoraux D
            addSphere(scene, shoulderLeft, 0.075, shirtTex);
            addSphere(scene, shoulderRight, 0.075, shirtTex);

            Point3D bellyCenter = hipLeft.plus(hipRight).mult(0.5).plus(new Point3D(0.0, 0.18, -0.01));
            addSphere(scene, bellyCenter, 0.16, shirtTex);

            // Bassin
            Point3D pelvisCenter = hipLeft.plus(hipRight).mult(0.5);
            addSphere(scene, pelvisCenter, 0.17, pantsTex);

            // Bras gauche
            addCylinder(scene, shoulderLeft, shoulderLeft.plus(elbowLeft.moins(shoulderLeft).mult(0.4)), 0.05, shirtTex); // Manche
            addCylinder(scene, shoulderLeft.plus(elbowLeft.moins(shoulderLeft).mult(0.4)), elbowLeft, 0.04, skinTex);
            addSphere(scene, shoulderLeft.plus(elbowLeft.moins(shoulderLeft).mult(0.5)), 0.045, skinTex); // Biceps G
            addSphere(scene, elbowLeft, 0.042, skinTex);
            addCylinder(scene, elbowLeft, wristLeft, 0.035, skinTex);
            addSphere(scene, handLeft, 0.04, skinTex);

            // Bras droit
            addCylinder(scene, shoulderRight, shoulderRight.plus(elbowRight.moins(shoulderRight).mult(0.4)), 0.05, shirtTex); // Manche
            addCylinder(scene, shoulderRight.plus(elbowRight.moins(shoulderRight).mult(0.4)), elbowRight, 0.04, skinTex);
            addSphere(scene, shoulderRight.plus(elbowRight.moins(shoulderRight).mult(0.5)), 0.045, skinTex); // Biceps D
            addSphere(scene, elbowRight, 0.042, skinTex);
            addCylinder(scene, elbowRight, wristRight, 0.035, skinTex);
            addSphere(scene, handRight, 0.04, skinTex);

            // Jambe gauche
            addCylinder(scene, hipLeft, kneeLeft, 0.085, pantsTex);
            addSphere(scene, kneeLeft, 0.065, skinTex);
            addCylinder(scene, kneeLeft, ankleLeft, 0.055, skinTex);
            addSphere(scene, kneeLeft.plus(ankleLeft.moins(kneeLeft).mult(0.3)).plus(new Point3D(0.0, 0.0, -0.02)), 0.065, skinTex); // Mollet G
            addSphere(scene, ankleLeft, 0.045, skinTex);

            // Jambe droite
            addCylinder(scene, hipRight, kneeRight, 0.085, pantsTex);
            addSphere(scene, kneeRight, 0.065, skinTex);
            addCylinder(scene, kneeRight, ankleRight, 0.055, skinTex);
            addSphere(scene, kneeRight.plus(ankleRight.moins(kneeRight).mult(0.3)).plus(new Point3D(0.0, 0.0, -0.02)), 0.065, skinTex); // Mollet D
            addSphere(scene, ankleRight, 0.045, skinTex);

            // Chaussures (Cylindres d'avant en arrière)
            addCylinder(scene, footLeftStart, footLeftEnd, 0.05, shoeTex);
            addCylinder(scene, footRightStart, footRightEnd, 0.05, shoeTex);
        }

        private void addSphere(Scene s, Point3D pos, double radius, ColorTexture tex) {
            Sphere sphere = new Sphere(pos, radius);
            sphere.texture(tex);
            s.add(sphere);
        }

        private void addCylinder(Scene s, Point3D start, Point3D end, double radius, ColorTexture tex) {
            Cylinder cylinder = new Cylinder(start, end, radius);
            cylinder.texture(tex);
            s.add(cylinder);
        }
    }

    // --- APPLICATION PRINCIPALE : ENREGISTREMENT DES IMAGES DE POSES ---
    public static void main(String[] args) {
        String[] poseNames = {
                "pose_1_debout.png",
                "pose_2_mains_en_lair.png",
                "pose_3_assis.png",
                "pose_4_couche.png",
                "pose_5_marche.png",
                "pose_6_course.png"
        };

        System.out.println("Début de la génération de la planche anatomique...");

        for (int i = 0; i < poseNames.length; i++) {
            Scene scene = new Scene();

            // Sol gazonné
            Sphere ground = new Sphere(new Point3D(0.0, -100.0, 0.0), 100.0);
            ground.texture(new ColorTexture(Color.newCol(0.25f, 0.50f, 0.22f)));
            scene.add(ground);

            HumanBody body = new HumanBody();

            // Choix et calcul de la posture active
            switch (i) {
                case 0: body.poseStand(); break;
                case 1: body.poseHandsInTheAir(); break;
                case 2: body.poseSit(); break;
                case 3: body.poseLieDown(); break;
                case 4: body.poseWalk(Math.PI / 4.0); break; // Échantillon à t = 45° du cycle de marche
                case 5: body.poseRun(Math.PI / 3.0); break;  // Échantillon à t = 60° du cycle de course
            }

            // Injection géométrique du corps dans la scène
            body.drawToScene(scene);

            // Ajustement dynamique de la caméra selon la hauteur ou l'étendue de la posture
            Point3D cameraEye = new Point3D(0.5, 1.0, 3.2); // Vue de face légèrement désaxée pour la perspective
            Point3D cameraLookAt = new Point3D(0.0, 0.9, 0.0);

            if (i == 2) { // Caméra abaissée pour la position assise
                cameraEye = new Point3D(0.7, 0.8, 2.8);
                cameraLookAt = new Point3D(0.0, 0.6, 0.1);
            } else if (i == 3) { // Caméra ajustée pour l'allongement au sol
                cameraEye = new Point3D(0.8, 1.2, 2.2);
                cameraLookAt = new Point3D(0.0, 0.2, 0.0);
            }

            // Règle 8 : Utilisation du vecteur haut explicite (Point3D.Y)
            Camera camera = new Camera(cameraEye, cameraLookAt, Point3D.Y);
            scene.cameraActive(camera);

            // Rendu de l'image
            int width = 800;
            int height = 1000;
            ZBufferImpl zBuffer = new ZBufferImpl(width, height);
            zBuffer.scene(scene);
            zBuffer.camera(camera);
            zBuffer.draw();

            // Enregistrement de l'image
            try {
                Image image = zBuffer.image();
                File file = new File(poseNames[i]);
                image.saveFile(file); // Règle 11
                System.out.println("  [✔] Fichier généré avec succès : " + file.getName());
            } catch (Exception e) {
                System.err.println("  [❌] Échec pour la pose : " + poseNames[i]);
                e.printStackTrace();
            }
        }
        System.out.println("Génération terminée ! Les 6 poses sont disponibles au format PNG.");
    }
}
