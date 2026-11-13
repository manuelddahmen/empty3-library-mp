/*
 * Copyright (c) 2026. Manuel Daniel Dahmen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package one.empty3.testagentcode;

import one.empty3.NativeLookAndFeelSwing;
import one.empty3.library.*;
import one.empty3.libs.Color;
import one.empty3.libs.Image;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Interactive 3D Human Animation and Rendering System.
 * Supports modular anatomical joint tracking, smooth pose transitions with easing,
 * 3D character displacement ands yaw orientation, real-time GUI game controller,
 * and command-line automation REPL.
 */
public class InteractiveHumanRenderer2 {

    // =========================================================================
    // 1. ANATOMICAL SKELETAL JOINT DESCRIPTORS
    // =========================================================================

    public enum JointType {
        HEAD, JAW, NOSE, NECK,
        SHOULDER_L, SHOULDER_R,
        ELBOW_L, ELBOW_R,
        WRIST_L, WRIST_R,
        HAND_L, HAND_R,
        HIP_L, HIP_R,
        KNEE_L, KNEE_R,
        ANKLE_L, ANKLE_R,
        FOOT_L_START, FOOT_L_END,
        FOOT_R_START, FOOT_R_END
    }

    // =========================================================================
    // 2. POSE STATE & MATHEMATICAL INTERPOLATION
    // =========================================================================

    public static class PoseState {
        private final Map<JointType, Point3D> joints = new EnumMap<>(JointType.class);

        public PoseState() {
        }

        public PoseState(Map<JointType, Point3D> initialJoints) {
            if (initialJoints != null) {
                for (Map.Entry<JointType, Point3D> entry : initialJoints.entrySet()) {
                    if (entry.getKey() != null && entry.getValue() != null) {
                        joints.put(entry.getKey(), copyPoint(entry.getValue()));
                    }
                }
            }
        }

        public Point3D get(JointType joint) {
            Point3D p = joints.get(joint);
            return p != null ? p : Point3D.O0;
        }

        public void set(JointType joint, Point3D point) {
            if (joint != null && point != null) {
                joints.put(joint, copyPoint(point));
            }
        }

        public boolean has(JointType joint) {
            return joints.containsKey(joint);
        }

        public Map<JointType, Point3D> getJoints() {
            return Collections.unmodifiableMap(joints);
        }

        public PoseState copy() {
            PoseState copy = new PoseState();
            for (Map.Entry<JointType, Point3D> entry : joints.entrySet()) {
                copy.set(entry.getKey(), entry.getValue());
            }
            return copy;
        }

        public static double smoothstep(double t) {
            double clamped = Math.max(0.0, Math.min(1.0, t));
            return clamped * clamped * (3.0 - 2.0 * clamped);
        }

        public static Point3D lerp(Point3D a, Point3D b, double t) {
            if (a == null && b == null) return Point3D.O0;
            if (a == null) return copyPoint(b);
            if (b == null) return copyPoint(a);
            return new Point3D(
                    a.getX() + (b.getX() - a.getX()) * t,
                    a.getY() + (b.getY() - a.getY()) * t,
                    a.getZ() + (b.getZ() - a.getZ()) * t
            );
        }

        private static Point3D copyPoint(Point3D p) {
            return new Point3D(p.getX(), p.getY(), p.getZ());
        }

        public PoseState interpolate(PoseState target, double t) {
            if (target == null) return copy();
            PoseState result = new PoseState();
            for (JointType jt : JointType.values()) {
                Point3D p1 = this.get(jt);
                Point3D p2 = target.get(jt);
                result.set(jt, lerp(p1, p2, t));
            }
            return result;
        }

        public PoseState interpolateSmooth(PoseState target, double t) {
            return interpolate(target, smoothstep(t));
        }

        public PoseState transform(Point3D translation, double yawRadians) {
            PoseState world = new PoseState();
            double cos = Math.cos(yawRadians);
            double sin = Math.sin(yawRadians);
            double tx = translation != null ? translation.getX() : 0.0;
            double ty = translation != null ? translation.getY() : 0.0;
            double tz = translation != null ? translation.getZ() : 0.0;

            for (Map.Entry<JointType, Point3D> entry : joints.entrySet()) {
                Point3D local = entry.getValue();
                double lx = local.getX();
                double ly = local.getY();
                double lz = local.getZ();

                // Rotate around vertical Y axis
                double rx = lx * cos + lz * sin;
                double ry = ly;
                double rz = -lx * sin + lz * cos;

                world.set(entry.getKey(), new Point3D(rx + tx, ry + ty, rz + tz));
            }
            return world;
        }
    }

    // =========================================================================
    // 3. CENTRALIZED POSE LIBRARY (SWEET & NATURAL BIOMECHANICAL MOTION)
    // =========================================================================

    public static class PoseLibrary {

        public static PoseState stand() {
            return stand(0.0);
        }

        public static PoseState stand(double breathPhase) {
            PoseState pose = new PoseState();
            double breath = 0.008 * Math.sin(breathPhase);
            double armRelax = 0.006 * Math.sin(breathPhase - 0.5);

            double yHead = 1.62 + breath;
            double yJaw = 1.54 + breath;
            double yNose = 1.52 + breath;
            double yNeck = 1.44 + breath * 0.8;
            double yShoulders = 1.36 + breath * 0.9;
            double yElbows = 1.08 + breath * 0.5;
            double yWrists = 0.88 + breath * 0.2;
            double yHips = 0.84;
            double yKnees = 0.48;
            double yAnkles = 0.12;

            pose.set(JointType.HEAD, new Point3D(0.0, yHead, 0.0));
            pose.set(JointType.JAW, new Point3D(0.0, yJaw, 0.03));
            pose.set(JointType.NOSE, new Point3D(0.0, yNose, 0.10));
            pose.set(JointType.NECK, new Point3D(0.0, yNeck, 0.0));

            pose.set(JointType.SHOULDER_L, new Point3D(-0.18 - armRelax * 0.3, yShoulders, 0.0));
            pose.set(JointType.SHOULDER_R, new Point3D(0.18 + armRelax * 0.3, yShoulders, 0.0));
            pose.set(JointType.ELBOW_L, new Point3D(-0.24 - armRelax, yElbows, -0.02));
            pose.set(JointType.ELBOW_R, new Point3D(0.24 + armRelax, yElbows, -0.02));
            pose.set(JointType.WRIST_L, new Point3D(-0.26 - armRelax, yWrists, 0.03));
            pose.set(JointType.WRIST_R, new Point3D(0.26 + armRelax, yWrists, 0.03));
            pose.set(JointType.HAND_L, new Point3D(-0.27 - armRelax, yWrists - 0.10, 0.05));
            pose.set(JointType.HAND_R, new Point3D(0.27 + armRelax, yWrists - 0.10, 0.05));

            pose.set(JointType.HIP_L, new Point3D(-0.09, yHips, 0.0));
            pose.set(JointType.HIP_R, new Point3D(0.09, yHips, 0.0));
            pose.set(JointType.KNEE_L, new Point3D(-0.09, yKnees, 0.01));
            pose.set(JointType.KNEE_R, new Point3D(0.09, yKnees, 0.01));
            pose.set(JointType.ANKLE_L, new Point3D(-0.09, yAnkles, 0.0));
            pose.set(JointType.ANKLE_R, new Point3D(0.09, yAnkles, 0.0));
            pose.set(JointType.FOOT_L_START, new Point3D(-0.09, yAnkles - 0.06, -0.05));
            pose.set(JointType.FOOT_L_END, new Point3D(-0.09, yAnkles - 0.08, 0.15));
            pose.set(JointType.FOOT_R_START, new Point3D(0.09, yAnkles - 0.06, -0.05));
            pose.set(JointType.FOOT_R_END, new Point3D(0.09, yAnkles - 0.08, 0.15));

            return pose;
        }

        public static PoseState handsUp() {
            PoseState pose = stand();
            pose.set(JointType.ELBOW_L, new Point3D(-0.22, 1.55, 0.02));
            pose.set(JointType.ELBOW_R, new Point3D(0.22, 1.55, 0.02));
            pose.set(JointType.WRIST_L, new Point3D(-0.24, 1.82, 0.06));
            pose.set(JointType.WRIST_R, new Point3D(0.24, 1.82, 0.06));
            pose.set(JointType.HAND_L, new Point3D(-0.25, 1.95, 0.08));
            pose.set(JointType.HAND_R, new Point3D(0.25, 1.95, 0.08));
            return pose;
        }

        public static PoseState sit() {
            PoseState pose = new PoseState();
            double ySeat = 0.45;
            double thighLength = 0.36;
            double dy = 0.84 - ySeat;

            pose.set(JointType.HEAD, new Point3D(0.0, 1.62 - dy, 0.02));
            pose.set(JointType.JAW, new Point3D(0.0, 1.54 - dy, 0.05));
            pose.set(JointType.NOSE, new Point3D(0.0, 1.52 - dy, 0.12));
            pose.set(JointType.NECK, new Point3D(0.0, 1.44 - dy, 0.01));

            pose.set(JointType.SHOULDER_L, new Point3D(-0.18, 1.36 - dy, 0.0));
            pose.set(JointType.SHOULDER_R, new Point3D(0.18, 1.36 - dy, 0.0));

            pose.set(JointType.ELBOW_L, new Point3D(-0.23, 1.08 - dy + 0.04, 0.12));
            pose.set(JointType.ELBOW_R, new Point3D(0.23, 1.08 - dy + 0.04, 0.12));
            pose.set(JointType.WRIST_L, new Point3D(-0.18, 0.54, 0.25));
            pose.set(JointType.WRIST_R, new Point3D(0.18, 0.54, 0.25));
            pose.set(JointType.HAND_L, new Point3D(-0.15, 0.51, 0.32));
            pose.set(JointType.HAND_R, new Point3D(0.15, 0.51, 0.32));

            pose.set(JointType.HIP_L, new Point3D(-0.09, ySeat, 0.0));
            pose.set(JointType.HIP_R, new Point3D(0.09, ySeat, 0.0));
            pose.set(JointType.KNEE_L, new Point3D(-0.10, ySeat, thighLength));
            pose.set(JointType.KNEE_R, new Point3D(0.10, ySeat, thighLength));
            pose.set(JointType.ANKLE_L, new Point3D(-0.10, 0.12, thighLength + 0.02));
            pose.set(JointType.ANKLE_R, new Point3D(0.10, 0.12, thighLength + 0.02));

            pose.set(JointType.FOOT_L_START, new Point3D(-0.10, 0.06, thighLength - 0.03));
            pose.set(JointType.FOOT_L_END, new Point3D(-0.10, 0.04, thighLength + 0.17));
            pose.set(JointType.FOOT_R_START, new Point3D(0.10, 0.06, thighLength - 0.03));
            pose.set(JointType.FOOT_R_END, new Point3D(0.10, 0.04, thighLength + 0.17));

            return pose;
        }

        public static PoseState lieDown() {
            PoseState stand = stand();
            PoseState lie = new PoseState();
            for (JointType jt : JointType.values()) {
                Point3D p = stand.get(jt);
                lie.set(jt, new Point3D(p.getX(), 0.12 + p.getZ(), p.getY() - 0.80));
            }
            return lie;
        }

        public static PoseState crouch() {
            PoseState pose = new PoseState();
            double yCrouch = 0.35;
            double dy = 0.84 - yCrouch;

            pose.set(JointType.HEAD, new Point3D(0.0, 1.62 - dy + 0.05, 0.15));
            pose.set(JointType.JAW, new Point3D(0.0, 1.54 - dy + 0.05, 0.18));
            pose.set(JointType.NOSE, new Point3D(0.0, 1.52 - dy + 0.05, 0.25));
            pose.set(JointType.NECK, new Point3D(0.0, 1.44 - dy + 0.03, 0.12));

            pose.set(JointType.SHOULDER_L, new Point3D(-0.18, 1.36 - dy + 0.02, 0.10));
            pose.set(JointType.SHOULDER_R, new Point3D(0.18, 1.36 - dy + 0.02, 0.10));

            pose.set(JointType.ELBOW_L, new Point3D(-0.24, 0.65, 0.22));
            pose.set(JointType.ELBOW_R, new Point3D(0.24, 0.65, 0.22));
            pose.set(JointType.WRIST_L, new Point3D(-0.20, 0.42, 0.32));
            pose.set(JointType.WRIST_R, new Point3D(0.20, 0.42, 0.32));
            pose.set(JointType.HAND_L, new Point3D(-0.18, 0.38, 0.36));
            pose.set(JointType.HAND_R, new Point3D(0.18, 0.38, 0.36));

            pose.set(JointType.HIP_L, new Point3D(-0.09, yCrouch, -0.05));
            pose.set(JointType.HIP_R, new Point3D(0.09, yCrouch, -0.05));
            pose.set(JointType.KNEE_L, new Point3D(-0.12, 0.38, 0.28));
            pose.set(JointType.KNEE_R, new Point3D(0.12, 0.38, 0.28));
            pose.set(JointType.ANKLE_L, new Point3D(-0.09, 0.10, 0.0));
            pose.set(JointType.ANKLE_R, new Point3D(0.09, 0.10, 0.0));

            pose.set(JointType.FOOT_L_START, new Point3D(-0.09, 0.06, -0.05));
            pose.set(JointType.FOOT_L_END, new Point3D(-0.09, 0.04, 0.15));
            pose.set(JointType.FOOT_R_START, new Point3D(0.09, 0.06, -0.05));
            pose.set(JointType.FOOT_R_END, new Point3D(0.09, 0.04, 0.15));

            return pose;
        }

        /**
         * Sweet, natural human walking gait with pelvic sway, double-frequency vertical bobbing,
         * counter-rotating shoulders, smooth knee flexion, and foot swing clearance.
         */
        public static PoseState walk(double t) {
            PoseState pose = new PoseState();

            double sinT = Math.sin(t);
            double cosT = Math.cos(t);
            double sinOpp = -sinT;
            double cosOpp = -cosT;

            // Gentle vertical bounce twice per stride cycle + subtle lateral weight shift
            double bob = 0.022 * Math.sin(2.0 * t - Math.PI * 0.25);
            double swayX = 0.018 * cosT;
            double forwardLean = 0.025;

            // Head & Neck (softly stabilized with tiny natural nod)
            pose.set(JointType.HEAD, new Point3D(swayX * 0.5, 1.62 + bob * 0.85, forwardLean));
            pose.set(JointType.JAW, new Point3D(swayX * 0.5, 1.54 + bob * 0.85, forwardLean + 0.03));
            pose.set(JointType.NOSE, new Point3D(swayX * 0.5, 1.52 + bob * 0.85, forwardLean + 0.10));
            pose.set(JointType.NECK, new Point3D(swayX * 0.65, 1.44 + bob * 0.9, forwardLean * 0.8));

            // Shoulders counter-rotate gently opposite to hips
            double shoulderTwist = 0.032 * sinT;
            pose.set(JointType.SHOULDER_L, new Point3D(-0.18 + swayX * 0.7, 1.36 + bob - 0.008 * sinT, -shoulderTwist + forwardLean * 0.6));
            pose.set(JointType.SHOULDER_R, new Point3D(0.18 + swayX * 0.7, 1.36 + bob + 0.008 * sinT, shoulderTwist + forwardLean * 0.6));

            // Pelvis / Hips rotate with the advancing leg
            double hipTwist = 0.025 * sinT;
            pose.set(JointType.HIP_L, new Point3D(-0.09 + swayX, 0.84 + bob + 0.010 * sinT, hipTwist));
            pose.set(JointType.HIP_R, new Point3D(0.09 + swayX, 0.84 + bob - 0.010 * sinT, -hipTwist));

            // Left Leg gait (cosT > 0 is forward swing phase; cosT < 0 is stance push-off)
            double strideL = 0.23 * sinT;
            double liftL = Math.max(0.0, cosT) * 0.065 + Math.max(0.0, -sinT) * 0.025;
            double kneeForwardL = strideL * 0.78 + Math.max(0.0, cosT) * 0.08;
            double kneeHeightL = 0.48 + bob + Math.max(0.0, cosT) * 0.04;
            pose.set(JointType.KNEE_L, new Point3D(-0.09 + swayX * 0.5, kneeHeightL, kneeForwardL));
            pose.set(JointType.ANKLE_L, new Point3D(-0.09 + swayX * 0.3, 0.12 + liftL, strideL - 0.02));
            double toeTiltL = 0.015 * sinT;
            pose.set(JointType.FOOT_L_START, new Point3D(-0.09 + swayX * 0.3, 0.06 + liftL - toeTiltL, strideL - 0.07));
            pose.set(JointType.FOOT_L_END, new Point3D(-0.09 + swayX * 0.3, 0.04 + liftL + toeTiltL, strideL + 0.13));

            // Right Leg gait (opposite phase)
            double strideR = 0.23 * sinOpp;
            double liftR = Math.max(0.0, cosOpp) * 0.065 + Math.max(0.0, -sinOpp) * 0.025;
            double kneeForwardR = strideR * 0.78 + Math.max(0.0, cosOpp) * 0.08;
            double kneeHeightR = 0.48 + bob + Math.max(0.0, cosOpp) * 0.04;
            pose.set(JointType.KNEE_R, new Point3D(0.09 + swayX * 0.5, kneeHeightR, kneeForwardR));
            pose.set(JointType.ANKLE_R, new Point3D(0.09 + swayX * 0.3, 0.12 + liftR, strideR - 0.02));
            double toeTiltR = 0.015 * sinOpp;
            pose.set(JointType.FOOT_R_START, new Point3D(0.09 + swayX * 0.3, 0.06 + liftR - toeTiltR, strideR - 0.07));
            pose.set(JointType.FOOT_R_END, new Point3D(0.09 + swayX * 0.3, 0.04 + liftR + toeTiltR, strideR + 0.13));

            // Sweet relaxed arm pendulum with natural elbow bend on forward swing
            double armSwingL = 0.18 * sinOpp;
            double elbowBendL = Math.max(0.0, sinOpp) * 0.07;
            pose.set(JointType.ELBOW_L, new Point3D(-0.24 + swayX * 0.5, 1.08 + bob, armSwingL * 0.75 - 0.01));
            pose.set(JointType.WRIST_L, new Point3D(-0.25 + swayX * 0.4, 0.88 + bob + elbowBendL * 0.6, armSwingL * 1.35 + elbowBendL + 0.03));
            pose.set(JointType.HAND_L, new Point3D(-0.26 + swayX * 0.4, 0.78 + bob + elbowBendL * 0.7, armSwingL * 1.50 + elbowBendL + 0.05));

            double armSwingR = 0.18 * sinT;
            double elbowBendR = Math.max(0.0, sinT) * 0.07;
            pose.set(JointType.ELBOW_R, new Point3D(0.24 + swayX * 0.5, 1.08 + bob, armSwingR * 0.75 - 0.01));
            pose.set(JointType.WRIST_R, new Point3D(0.25 + swayX * 0.4, 0.88 + bob + elbowBendR * 0.6, armSwingR * 1.35 + elbowBendR + 0.03));
            pose.set(JointType.HAND_R, new Point3D(0.26 + swayX * 0.4, 0.78 + bob + elbowBendR * 0.7, armSwingR * 1.50 + elbowBendR + 0.05));

            return pose;
        }

        public static PoseState run(double t) {
            PoseState stand = stand();
            PoseState pose = new PoseState();

            double sinT = Math.sin(t);
            double cosT = Math.cos(t);
            double sinOpp = -sinT;
            double cosOpp = -cosT;

            double bounce = 0.055 * Math.sin(2.0 * t - Math.PI * 0.2);
            double swayX = 0.022 * cosT;
            double lean = 0.09;

            pose.set(JointType.HEAD, stand.get(JointType.HEAD).plus(new Point3D(swayX * 0.4, bounce, lean + 0.04)));
            pose.set(JointType.JAW, stand.get(JointType.JAW).plus(new Point3D(swayX * 0.4, bounce, lean + 0.06)));
            pose.set(JointType.NOSE, stand.get(JointType.NOSE).plus(new Point3D(swayX * 0.4, bounce, lean + 0.08)));
            pose.set(JointType.NECK, stand.get(JointType.NECK).plus(new Point3D(swayX * 0.6, bounce, lean + 0.03)));

            double shoulderTwist = 0.05 * sinT;
            pose.set(JointType.SHOULDER_L, stand.get(JointType.SHOULDER_L).plus(new Point3D(swayX * 0.7, bounce, lean - shoulderTwist)));
            pose.set(JointType.SHOULDER_R, stand.get(JointType.SHOULDER_R).plus(new Point3D(swayX * 0.7, bounce, lean + shoulderTwist)));

            double swing = 0.38 * sinT;
            double swingOpp = 0.38 * sinOpp;

            pose.set(JointType.HIP_L, stand.get(JointType.HIP_L).plus(new Point3D(swayX, bounce, 0.03 * sinT)));
            pose.set(JointType.HIP_R, stand.get(JointType.HIP_R).plus(new Point3D(swayX, bounce, -0.03 * sinT)));

            double kneeLiftL = Math.max(0.0, cosT) * 0.10 + Math.max(0.0, sinT) * 0.05;
            double heelKickL = Math.max(0.0, -sinT) * 0.16 + Math.max(0.0, cosT) * 0.08;
            pose.set(JointType.KNEE_L, new Point3D(-0.09 + swayX * 0.5, 0.48 + bounce + kneeLiftL, swing * 0.85 + lean));
            pose.set(JointType.ANKLE_L, new Point3D(-0.09 + swayX * 0.3, 0.13 + bounce + heelKickL, swing - heelKickL * 0.45));
            pose.set(JointType.FOOT_L_START, new Point3D(-0.09 + swayX * 0.3, 0.07 + bounce + heelKickL, swing - heelKickL * 0.45 - 0.05));
            pose.set(JointType.FOOT_L_END, new Point3D(-0.09 + swayX * 0.3, 0.05 + bounce + heelKickL, swing - heelKickL * 0.45 + 0.15));

            double kneeLiftR = Math.max(0.0, cosOpp) * 0.10 + Math.max(0.0, sinOpp) * 0.05;
            double heelKickR = Math.max(0.0, -sinOpp) * 0.16 + Math.max(0.0, cosOpp) * 0.08;
            pose.set(JointType.KNEE_R, new Point3D(0.09 + swayX * 0.5, 0.48 + bounce + kneeLiftR, swingOpp * 0.85 + lean));
            pose.set(JointType.ANKLE_R, new Point3D(0.09 + swayX * 0.3, 0.13 + bounce + heelKickR, swingOpp - heelKickR * 0.45));
            pose.set(JointType.FOOT_R_START, new Point3D(0.09 + swayX * 0.3, 0.07 + bounce + heelKickR, swingOpp - heelKickR * 0.45 - 0.05));
            pose.set(JointType.FOOT_R_END, new Point3D(0.09 + swayX * 0.3, 0.05 + bounce + heelKickR, swingOpp - heelKickR * 0.45 + 0.15));

            pose.set(JointType.ELBOW_L, new Point3D(-0.24 + swayX * 0.5, 1.13 + bounce, swingOpp * 0.75 + lean));
            pose.set(JointType.WRIST_L, new Point3D(-0.22 + swayX * 0.4, 1.22 + bounce + 0.04 * sinOpp, swingOpp * 0.78 + lean + 0.18));
            pose.set(JointType.HAND_L, new Point3D(-0.20 + swayX * 0.4, 1.24 + bounce + 0.05 * sinOpp, swingOpp * 0.80 + lean + 0.24));

            pose.set(JointType.ELBOW_R, new Point3D(0.24 + swayX * 0.5, 1.13 + bounce, swing * 0.75 + lean));
            pose.set(JointType.WRIST_R, new Point3D(0.22 + swayX * 0.4, 1.22 + bounce + 0.04 * sinT, swing * 0.78 + lean + 0.18));
            pose.set(JointType.HAND_R, new Point3D(0.20 + swayX * 0.4, 1.24 + bounce + 0.05 * sinT, swing * 0.80 + lean + 0.24));

            return pose;
        }

        public static PoseState getPose(String name) {
            return getPose(name, 0.0);
        }

        public static PoseState getPose(String name, double cycleTime) {
            if (name == null) return stand(cycleTime * 0.4);
            String clean = name.trim().toLowerCase().replaceAll("[_\\-\\s]", "");
            switch (clean) {
                case "stand":
                case "debout":
                case "1":
                    return stand(cycleTime * 0.4);
                case "handsup":
                case "mainsenlair":
                case "2":
                    return handsUp();
                case "sit":
                case "assis":
                case "3":
                    return sit();
                case "liedown":
                case "couche":
                case "4":
                    return lieDown();
                case "walk":
                case "marche":
                case "5":
                    return walk(cycleTime);
                case "run":
                case "course":
                case "6":
                    return run(cycleTime);
                case "crouch":
                case "accroupi":
                    return crouch();
                default:
                    return stand(cycleTime * 0.4);
            }
        }

        public static boolean isCyclic(String name) {
            if (name == null) return false;
            String clean = name.trim().toLowerCase().replaceAll("[_\\-\\s]", "");
            return clean.equals("walk") || clean.equals("marche") || clean.equals("5")
                    || clean.equals("run") || clean.equals("course") || clean.equals("6")
                    || clean.equals("stand") || clean.equals("debout") || clean.equals("1");
        }
    }

    // =========================================================================
    // 4. UNIVERSAL POSE TRANSITION CONTROLLER
    // =========================================================================

    public static class PoseTransitionController {
        private PoseState currentPose = PoseLibrary.stand();
        private PoseState sourcePose = PoseLibrary.stand();
        private PoseState targetPose = PoseLibrary.stand();

        private String currentPoseName = "stand";
        private String targetPoseName = "stand";

        private double transitionDuration = 0.0;
        private double transitionElapsed = 0.0;
        private boolean isTransitioning = false;
        private Runnable completionCallback = null;

        private double cycleTime = 0.0;
        private double cycleSpeed = 4.2; // Radians per second for walk/run cycles

        public synchronized void setPose(PoseState pose) {
            this.currentPose = (pose != null) ? pose.copy() : PoseLibrary.stand();
            this.sourcePose = this.currentPose.copy();
            this.targetPose = this.currentPose.copy();
            this.isTransitioning = false;
            this.transitionElapsed = 0.0;
            this.completionCallback = null;
        }

        public synchronized void setPose(String poseName) {
            this.currentPoseName = poseName;
            this.targetPoseName = poseName;
            setPose(PoseLibrary.getPose(poseName, cycleTime));
        }

        public synchronized void transitionTo(PoseState target, double durationSeconds) {
            transitionTo(target, durationSeconds, null);
        }

        public synchronized void transitionTo(PoseState target, double durationSeconds, Runnable onComplete) {
            if (target == null) return;
            this.sourcePose = this.currentPose.copy();
            this.targetPose = target.copy();
            this.transitionDuration = Math.max(0.001, durationSeconds);
            this.transitionElapsed = 0.0;
            this.isTransitioning = true;
            this.completionCallback = onComplete;
        }

        public synchronized void transitionTo(String poseName, double durationSeconds) {
            transitionTo(poseName, durationSeconds, null);
        }

        public synchronized void transitionTo(String poseName, double durationSeconds, Runnable onComplete) {
            if (poseName == null) return;
            if (isTransitioning && poseName.equalsIgnoreCase(targetPoseName)) {
                return;
            }
            if (!isTransitioning && poseName.equalsIgnoreCase(currentPoseName)) {
                return;
            }
            this.sourcePose = this.currentPose.copy();
            this.targetPoseName = poseName;
            this.targetPose = PoseLibrary.getPose(poseName, cycleTime);
            this.transitionDuration = Math.max(0.001, durationSeconds);
            this.transitionElapsed = 0.0;
            this.isTransitioning = true;
            this.completionCallback = onComplete;
        }

        public synchronized void update(double deltaTimeSeconds) {
            if (PoseLibrary.isCyclic(targetPoseName) || PoseLibrary.isCyclic(currentPoseName)) {
                cycleTime += cycleSpeed * deltaTimeSeconds;
                if (cycleTime > Math.PI * 2000.0) {
                    cycleTime = cycleTime % (Math.PI * 2.0);
                }
            }

            if (isTransitioning) {
                transitionElapsed += deltaTimeSeconds;
                double t = Math.min(1.0, transitionElapsed / transitionDuration);

                if (PoseLibrary.isCyclic(targetPoseName)) {
                    targetPose = PoseLibrary.getPose(targetPoseName, cycleTime);
                }

                currentPose = sourcePose.interpolateSmooth(targetPose, t);

                if (t >= 1.0) {
                    isTransitioning = false;
                    currentPoseName = targetPoseName;
                    currentPose = targetPose.copy();
                    Runnable callback = this.completionCallback;
                    this.completionCallback = null;
                    if (callback != null) {
                        try {
                            callback.run();
                        } catch (Exception e) {
                            System.err.println("Transition callback error: " + e.getMessage());
                        }
                    }
                }
            } else if (PoseLibrary.isCyclic(currentPoseName)) {
                currentPose = PoseLibrary.getPose(currentPoseName, cycleTime);
            }
        }

        public synchronized PoseState getCurrentPose() {
            return currentPose.copy();
        }

        public synchronized boolean isTransitioning() {
            return isTransitioning;
        }

        public synchronized double getTransitionProgress() {
            if (!isTransitioning || transitionDuration <= 0.0) return 1.0;
            return Math.min(1.0, transitionElapsed / transitionDuration);
        }

        public synchronized String getCurrentPoseName() {
            return currentPoseName;
        }

        public synchronized String getTargetPoseName() {
            return targetPoseName;
        }

        public synchronized void setCycleSpeed(double speed) {
            this.cycleSpeed = speed;
        }

        public synchronized double getCycleSpeed() {
            return cycleSpeed;
        }
    }

    // =========================================================================
    // 5. CHARACTER CONTROLLER (SWEET INERTIA, COLLISION & AUTO-NAVIGATION)
    // =========================================================================

    public static class CharacterController {
        private Point3D position = new Point3D(0.0, 0.0, -0.5);
        private double yaw = 0.0; // Heading angle in radians (0 = facing +Z towards the house)
        private double moveSpeed = 1.65; // Sweet natural walk speed (units/sec)
        private double turnSpeed = Math.PI * 0.85; // Smooth turning rate (radians/sec)

        // Smooth velocity states for sweet acceleration / deceleration
        private double currentForwardSpeed = 0.0;
        private double currentStrafeSpeed = 0.0;
        private double currentTurnSpeed = 0.0;

        // Environment & Door state
        private boolean collisionEnabled = true;
        private boolean doorOpen = true;
        private double doorOpenAngle = Math.toRadians(82.0); // Target open angle

        // Autonomous Waypoint Navigation (for "enter house", "walk to garden", "sit", etc.)
        private final Queue<Point3D> navWaypoints = new ArrayDeque<>();
        private Double navTargetYaw = null;
        private String navCompletionPose = null;
        private String activeActivityLabel = "Free Roam";

        public CharacterController() {
        }

        public CharacterController(Point3D position, double yaw) {
            if (position != null) this.position = position;
            this.yaw = yaw;
        }

        public synchronized Point3D getPosition() {
            return new Point3D(position.getX(), position.getY(), position.getZ());
        }

        public synchronized void setPosition(Point3D position) {
            if (position != null) {
                this.position = new Point3D(position.getX(), position.getY(), position.getZ());
                this.currentForwardSpeed = 0.0;
                this.currentStrafeSpeed = 0.0;
            }
        }

        public synchronized double getYaw() {
            return yaw;
        }

        public synchronized void setYaw(double yaw) {
            this.yaw = normalizeAngle(yaw);
        }

        public synchronized double getYawDegrees() {
            return Math.toDegrees(yaw);
        }

        public synchronized void setYawDegrees(double deg) {
            this.yaw = normalizeAngle(Math.toRadians(deg));
        }

        public synchronized void moveForward(double distance) {
            double dx = distance * Math.sin(yaw);
            double dz = distance * Math.cos(yaw);
            applyDisplacement(dx, dz);
        }

        public synchronized void moveBackward(double distance) {
            moveForward(-distance);
        }

        public synchronized void strafe(double distance) {
            double dx = distance * Math.cos(yaw);
            double dz = -distance * Math.sin(yaw);
            applyDisplacement(dx, dz);
        }

        public synchronized void applyDisplacement(double dx, double dz) {
            if (!collisionEnabled) {
                position = position.plus(new Point3D(dx, 0.0, dz));
                return;
            }
            // Slide smoothly along walls by testing X and Z axes independently
            double curX = position.getX();
            double curY = position.getY();
            double curZ = position.getZ();

            double nextX = curX + dx;
            if (!HouseAndGardenGeometry.isBlocked(nextX, curZ, doorOpen)) {
                curX = nextX;
            }
            double nextZ = curZ + dz;
            if (!HouseAndGardenGeometry.isBlocked(curX, nextZ, doorOpen)) {
                curZ = nextZ;
            }
            position = new Point3D(curX, curY, curZ);
        }

        /**
         * Updates character motion smoothly with inertia/damping and optional autonomous navigation.
         *
         * @return current horizontal speed magnitude (units/sec)
         */
        public synchronized double updateSmoothMotion(
                double targetForwardInput,
                double targetStrafeInput,
                double targetTurnInput,
                double speedMultiplier,
                double dt,
                PoseTransitionController animator) {

            // Animate door swing smoothly
            double targetDoorAngle = doorOpen ? Math.toRadians(82.0) : 0.0;
            doorOpenAngle += (targetDoorAngle - doorOpenAngle) * Math.min(1.0, dt * 5.0);

            boolean manualInput = Math.abs(targetForwardInput) > 1e-3
                    || Math.abs(targetStrafeInput) > 1e-3
                    || Math.abs(targetTurnInput) > 1e-3;

            if (manualInput && !navWaypoints.isEmpty()) {
                cancelNavigation();
            }

            if (!navWaypoints.isEmpty()) {
                return updateAutoNavigation(dt, animator);
            } else if (navTargetYaw != null) {
                double diff = normalizeAngle(navTargetYaw - yaw);
                if (Math.abs(diff) > 0.04) {
                    double step = Math.signum(diff) * Math.min(Math.abs(diff), turnSpeed * 1.1 * dt);
                    turn(step);
                } else {
                    yaw = normalizeAngle(navTargetYaw);
                    navTargetYaw = null;
                    if (navCompletionPose != null && animator != null) {
                        animator.transitionTo(navCompletionPose, 0.65);
                        navCompletionPose = null;
                    }
                }
            }

            double desiredForward = targetForwardInput * moveSpeed * speedMultiplier;
            double desiredStrafe = targetStrafeInput * moveSpeed * 0.8 * speedMultiplier;
            double desiredTurn = targetTurnInput * turnSpeed;

            // Sweet exponential damping (gentle start and soft stop)
            double velBlend = 1.0 - Math.exp(-7.5 * dt);
            double turnBlend = 1.0 - Math.exp(-9.0 * dt);

            currentForwardSpeed += (desiredForward - currentForwardSpeed) * velBlend;
            currentStrafeSpeed += (desiredStrafe - currentStrafeSpeed) * velBlend;
            currentTurnSpeed += (desiredTurn - currentTurnSpeed) * turnBlend;

            if (Math.abs(currentForwardSpeed) < 0.01) currentForwardSpeed = 0.0;
            if (Math.abs(currentStrafeSpeed) < 0.01) currentStrafeSpeed = 0.0;
            if (Math.abs(currentTurnSpeed) < 0.005) currentTurnSpeed = 0.0;

            if (Math.abs(currentTurnSpeed) > 0.0) {
                turn(currentTurnSpeed * dt);
            }

            double dx = (currentForwardSpeed * Math.sin(yaw) + currentStrafeSpeed * Math.cos(yaw)) * dt;
            double dz = (currentForwardSpeed * Math.cos(yaw) - currentStrafeSpeed * Math.sin(yaw)) * dt;
            if (Math.abs(dx) > 1e-5 || Math.abs(dz) > 1e-5) {
                applyDisplacement(dx, dz);
            }

            return Math.hypot(currentForwardSpeed, currentStrafeSpeed);
        }

        private double updateAutoNavigation(double dt, PoseTransitionController animator) {
            Point3D target = navWaypoints.peek();
            if (target == null) return 0.0;

            double dx = target.getX() - position.getX();
            double dz = target.getZ() - position.getZ();
            double dist = Math.hypot(dx, dz);

            if (dist < 0.14) {
                position = new Point3D(target.getX(), position.getY(), target.getZ());
                navWaypoints.poll();
                if (navWaypoints.isEmpty()) {
                    currentForwardSpeed = 0.0;
                    currentStrafeSpeed = 0.0;
                    if (navTargetYaw == null && navCompletionPose != null && animator != null) {
                        animator.transitionTo(navCompletionPose, 0.65);
                        navCompletionPose = null;
                    } else if (animator != null) {
                        animator.transitionTo("stand", 0.45);
                    }
                    return 0.0;
                }
                target = navWaypoints.peek();
                dx = target.getX() - position.getX();
                dz = target.getZ() - position.getZ();
                dist = Math.hypot(dx, dz);
            }

            double desiredYaw = Math.atan2(dx, dz);
            double angleDiff = normalizeAngle(desiredYaw - yaw);

            // Smoothly steer towards waypoint
            double maxTurn = turnSpeed * 1.4 * dt;
            if (Math.abs(angleDiff) > maxTurn) {
                turn(Math.signum(angleDiff) * maxTurn);
            } else {
                yaw = desiredYaw;
            }

            // Slow down gently when turning sharply or approaching final target
            double turnFactor = Math.max(0.25, Math.cos(Math.min(Math.PI * 0.5, Math.abs(angleDiff))));
            double approachFactor = (navWaypoints.size() == 1 && dist < 0.7) ? Math.max(0.35, dist / 0.7) : 1.0;
            double desiredSpeed = moveSpeed * 0.95 * turnFactor * approachFactor;

            double blend = 1.0 - Math.exp(-6.5 * dt);
            currentForwardSpeed += (desiredSpeed - currentForwardSpeed) * blend;

            // temporarily disable wall block if passing through doorway
            if (!doorOpen && Math.abs(position.getZ() - HouseAndGardenGeometry.HOUSE_Z_MIN) < 1.2) {
                doorOpen = true;
            }
            moveForward(currentForwardSpeed * dt);
            return currentForwardSpeed;
        }

        public synchronized void cancelNavigation() {
            navWaypoints.clear();
            navTargetYaw = null;
            navCompletionPose = null;
            activeActivityLabel = "Free Roam";
        }

        public synchronized boolean isNavigating() {
            return !navWaypoints.isEmpty() || navTargetYaw != null;
        }

        public synchronized String getActiveActivityLabel() {
            return activeActivityLabel;
        }

        /**
         * Smoothly guides the character into the house through the front door.
         */
        public synchronized void walkIntoHouse() {
            navWaypoints.clear();
            doorOpen = true;
            activeActivityLabel = "Entering House";
            if (!isInsideHouse()) {
                // Align with garden path before doorway, then walk inside
                if (position.getZ() < HouseAndGardenGeometry.HOUSE_Z_MIN - 0.6 || Math.abs(position.getX()) > 0.45) {
                    navWaypoints.add(new Point3D(0.0, 0.0, 2.4));
                }
                navWaypoints.add(new Point3D(0.0, 0.0, 4.3));
            }
            navWaypoints.add(new Point3D(0.0, 0.0, 6.2));
            navTargetYaw = 0.0;
            navCompletionPose = "stand";
        }

        /**
         * Smoothly guides the character out of the house into the sunny garden.
         */
        public synchronized void walkIntoGarden() {
            navWaypoints.clear();
            doorOpen = true;
            activeActivityLabel = "Strolling in Garden";
            if (isInsideHouse()) {
                navWaypoints.add(new Point3D(0.0, 0.0, 4.3));
                navWaypoints.add(new Point3D(0.0, 0.0, 2.5));
            }
            navWaypoints.add(new Point3D(0.0, 0.0, -0.4));
            navTargetYaw = Math.PI; // Face toward garden view
            navCompletionPose = "stand";
        }

        /**
         * Walks to either the living room sofa (if inside) or garden bench (if outside) and sits down.
         */
        public synchronized void walkAndSit() {
            navWaypoints.clear();
            doorOpen = true;
            if (isInsideHouse()) {
                activeActivityLabel = "Sitting on House Sofa";
                navWaypoints.add(new Point3D(0.0, 0.0, 6.2));
                navWaypoints.add(new Point3D(1.85, 0.0, 6.2));
                navTargetYaw = -Math.PI * 0.5; // Face left into living room from sofa
                navCompletionPose = "sit";
            } else {
                activeActivityLabel = "Sitting on Garden Bench";
                navWaypoints.add(new Point3D(0.0, 0.0, 0.5));
                navWaypoints.add(new Point3D(-1.95, 0.0, 0.5));
                navTargetYaw = Math.PI * 0.5; // Face right toward garden path & pond
                navCompletionPose = "sit";
            }
        }

        public synchronized boolean isInsideHouse() {
            return HouseAndGardenGeometry.isInsideHouse(position.getX(), position.getZ());
        }

        public synchronized String getCurrentZoneName() {
            return HouseAndGardenGeometry.getZoneName(position.getX(), position.getZ());
        }

        public synchronized boolean isDoorOpen() {
            return doorOpen;
        }

        public synchronized void setDoorOpen(boolean open) {
            this.doorOpen = open;
        }

        public synchronized void toggleDoor() {
            this.doorOpen = !this.doorOpen;
        }

        public synchronized double getDoorOpenAngle() {
            return doorOpenAngle;
        }

        public synchronized void turn(double angleDeltaRadians) {
            this.yaw = normalizeAngle(this.yaw + angleDeltaRadians);
        }

        public synchronized void turnLeft(double angleDeltaRadians) {
            turn(-Math.abs(angleDeltaRadians));
        }

        public synchronized void turnRight(double angleDeltaRadians) {
            turn(Math.abs(angleDeltaRadians));
        }

        public synchronized void turnDegrees(double degrees) {
            turn(Math.toRadians(degrees));
        }

        public synchronized PoseState getWorldPose(PoseState localPose) {
            if (localPose == null) return new PoseState();
            return localPose.transform(position, yaw);
        }

        public synchronized double getMoveSpeed() {
            return moveSpeed;
        }

        public synchronized void setMoveSpeed(double moveSpeed) {
            this.moveSpeed = moveSpeed;
        }

        public synchronized double getTurnSpeed() {
            return turnSpeed;
        }

        public synchronized void setTurnSpeed(double turnSpeed) {
            this.turnSpeed = turnSpeed;
        }

        public synchronized boolean isCollisionEnabled() {
            return collisionEnabled;
        }

        public synchronized void setCollisionEnabled(boolean collisionEnabled) {
            this.collisionEnabled = collisionEnabled;
        }

        private static double normalizeAngle(double angle) {
            while (angle > Math.PI) angle -= Math.PI * 2.0;
            while (angle < -Math.PI) angle += Math.PI * 2.0;
            return angle;
        }
    }

    // =========================================================================
    // 6. GEOMETRY BUILDER & SCENE RENDERER (HUMAN + HOUSE & GARDEN)
    // =========================================================================

    public static class HumanBodyGeometry {
        public static final ColorTexture skinTex = new ColorTexture(Color.newCol(0.92f, 0.76f, 0.65f));
        public static final ColorTexture hairTex = new ColorTexture(Color.newCol(0.20f, 0.12f, 0.08f));
        public static final ColorTexture eyeWhiteTex = new ColorTexture(Color.newCol(0.95f, 0.95f, 0.95f));
        public static final ColorTexture irisTex = new ColorTexture(Color.newCol(0.15f, 0.35f, 0.60f));
        public static final ColorTexture shirtTex = new ColorTexture(Color.newCol(0.85f, 0.15f, 0.15f));
        public static final ColorTexture pantsTex = new ColorTexture(Color.newCol(0.12f, 0.25f, 0.45f));
        public static final ColorTexture shoeTex = new ColorTexture(Color.newCol(0.15f, 0.15f, 0.15f));

        public static void drawToScene(Scene scene, PoseState pose) {
            if (scene == null || pose == null) return;

            Point3D head = pose.get(JointType.HEAD);
            Point3D jaw = pose.get(JointType.JAW);
            Point3D nose = pose.get(JointType.NOSE);
            Point3D neck = pose.get(JointType.NECK);

            Point3D shoulderL = pose.get(JointType.SHOULDER_L);
            Point3D shoulderR = pose.get(JointType.SHOULDER_R);
            Point3D elbowL = pose.get(JointType.ELBOW_L);
            Point3D elbowR = pose.get(JointType.ELBOW_R);
            Point3D wristL = pose.get(JointType.WRIST_L);
            Point3D wristR = pose.get(JointType.WRIST_R);
            Point3D handL = pose.get(JointType.HAND_L);
            Point3D handR = pose.get(JointType.HAND_R);

            Point3D hipL = pose.get(JointType.HIP_L);
            Point3D hipR = pose.get(JointType.HIP_R);
            Point3D kneeL = pose.get(JointType.KNEE_L);
            Point3D kneeR = pose.get(JointType.KNEE_R);
            Point3D ankleL = pose.get(JointType.ANKLE_L);
            Point3D ankleR = pose.get(JointType.ANKLE_R);
            Point3D footLStart = pose.get(JointType.FOOT_L_START);
            Point3D footLEnd = pose.get(JointType.FOOT_L_END);
            Point3D footRStart = pose.get(JointType.FOOT_R_START);
            Point3D footREnd = pose.get(JointType.FOOT_R_END);

            // Compute head orientation frame (Forward, Up, Right)
            Point3D fwd = nose.moins(head);
            Point3D up = head.moins(neck);
            if (fwd.norme() > 1e-4) {
                fwd = fwd.norme1();
            } else {
                fwd = new Point3D(0.0, 0.0, 1.0);
            }
            if (up.norme() > 1e-4) {
                up = up.norme1();
            } else {
                up = new Point3D(0.0, 1.0, 0.0);
            }
            Point3D right = up.prodVect(fwd);
            if (right.norme() > 1e-4) {
                right = right.norme1();
            } else {
                right = new Point3D(1.0, 0.0, 0.0);
            }

            // Head & Jaw & Nose
            addSphere(scene, head, 0.10, skinTex);
            addSphere(scene, jaw, 0.08, skinTex);
            addSphere(scene, nose, 0.015, skinTex);

            // Hair (oriented with head)
            addSphere(scene, head.plus(up.mult(0.08)).plus(fwd.mult(-0.02)), 0.085, hairTex);
            addSphere(scene, head.plus(right.mult(-0.06)).plus(up.mult(0.04)).plus(fwd.mult(-0.05)), 0.07, hairTex);
            addSphere(scene, head.plus(right.mult(0.06)).plus(up.mult(0.04)).plus(fwd.mult(-0.05)), 0.07, hairTex);
            addSphere(scene, head.plus(fwd.mult(-0.08)), 0.08, hairTex);

            // Eyes
            Point3D eyeL = jaw.plus(right.mult(-0.035)).plus(up.mult(0.03)).plus(fwd.mult(0.06));
            Point3D irisL = jaw.plus(right.mult(-0.035)).plus(up.mult(0.03)).plus(fwd.mult(0.072));
            Point3D eyeR = jaw.plus(right.mult(0.035)).plus(up.mult(0.03)).plus(fwd.mult(0.06));
            Point3D irisR = jaw.plus(right.mult(0.035)).plus(up.mult(0.03)).plus(fwd.mult(0.072));

            addSphere(scene, eyeL, 0.015, eyeWhiteTex);
            addSphere(scene, irisL, 0.007, irisTex);
            addSphere(scene, eyeR, 0.015, eyeWhiteTex);
            addSphere(scene, irisR, 0.007, irisTex);

            // Neck & Torso
            addCylinder(scene, head.plus(up.mult(-0.12)), neck, 0.05, skinTex);

            Point3D chestCenter = neck.plus(up.mult(-0.15));
            addSphere(scene, chestCenter, 0.18, shirtTex);
            addSphere(scene, neck.plus(right.mult(-0.07)).plus(up.mult(-0.13)).plus(fwd.mult(0.11)), 0.08, shirtTex);
            addSphere(scene, neck.plus(right.mult(0.07)).plus(up.mult(-0.13)).plus(fwd.mult(0.11)), 0.08, shirtTex);
            addSphere(scene, shoulderL, 0.075, shirtTex);
            addSphere(scene, shoulderR, 0.075, shirtTex);

            Point3D pelvisCenter = hipL.plus(hipR).mult(0.5);
            Point3D bellyCenter = pelvisCenter.plus(up.mult(0.18)).plus(fwd.mult(-0.01));
            addSphere(scene, bellyCenter, 0.16, shirtTex);
            addSphere(scene, pelvisCenter, 0.17, pantsTex);

            // Left Arm
            Point3D armDirL = elbowL.moins(shoulderL);
            Point3D armMidL = shoulderL.plus(armDirL.mult(0.4));
            addCylinder(scene, shoulderL, armMidL, 0.05, shirtTex);
            addCylinder(scene, armMidL, elbowL, 0.04, skinTex);
            addSphere(scene, shoulderL.plus(armDirL.mult(0.5)), 0.045, skinTex);
            addSphere(scene, elbowL, 0.042, skinTex);
            addCylinder(scene, elbowL, wristL, 0.035, skinTex);
            addSphere(scene, handL, 0.04, skinTex);

            // Right Arm
            Point3D armDirR = elbowR.moins(shoulderR);
            Point3D armMidR = shoulderR.plus(armDirR.mult(0.4));
            addCylinder(scene, shoulderR, armMidR, 0.05, shirtTex);
            addCylinder(scene, armMidR, elbowR, 0.04, skinTex);
            addSphere(scene, shoulderR.plus(armDirR.mult(0.5)), 0.045, skinTex);
            addSphere(scene, elbowR, 0.042, skinTex);
            addCylinder(scene, elbowR, wristR, 0.035, skinTex);
            addSphere(scene, handR, 0.04, skinTex);

            // Left Leg
            addCylinder(scene, hipL, kneeL, 0.085, pantsTex);
            addSphere(scene, kneeL, 0.065, skinTex);
            addCylinder(scene, kneeL, ankleL, 0.055, skinTex);
            addSphere(scene, kneeL.plus(ankleL.moins(kneeL).mult(0.3)).plus(fwd.mult(-0.02)), 0.065, skinTex);
            addSphere(scene, ankleL, 0.045, skinTex);

            // Right Leg
            addCylinder(scene, hipR, kneeR, 0.085, pantsTex);
            addSphere(scene, kneeR, 0.065, skinTex);
            addCylinder(scene, kneeR, ankleR, 0.055, skinTex);
            addSphere(scene, kneeR.plus(ankleR.moins(kneeR).mult(0.3)).plus(fwd.mult(-0.02)), 0.065, skinTex);
            addSphere(scene, ankleR, 0.045, skinTex);

            // Shoes
            addCylinder(scene, footLStart, footLEnd, 0.05, shoeTex);
            addCylinder(scene, footRStart, footREnd, 0.05, shoeTex);
        }

        private static void addSphere(Scene s, Point3D pos, double radius, ColorTexture tex) {
            Sphere sphere = new Sphere(pos, radius);
            sphere.setIncrU(0.1);
            sphere.setIncrV(0.1);
            sphere.texture(tex);
            s.add(sphere);
        }

        private static void addCylinder(Scene s, Point3D start, Point3D end, double radius, ColorTexture tex) {
            Cylinder cylinder = new Cylinder(start, end, radius);
            cylinder.setIncrU(0.1);
            cylinder.setIncrV(0.1);
            cylinder.texture(tex);
            s.add(cylinder);
        }
    }

    // =========================================================================
    // 6B. HOUSE & GARDEN 3D ENVIRONMENT BUILDER
    // =========================================================================

    public static class HouseAndGardenGeometry {
        // House spatial bounds
        public static final double HOUSE_X_MIN = -3.8;
        public static final double HOUSE_X_MAX = 3.8;
        public static final double HOUSE_Z_MIN = 3.5;
        public static final double HOUSE_Z_MAX = 9.8;
        public static final double WALL_HEIGHT = 2.6;
        public static final double ROOF_PEAK_Y = 3.85;
        public static final double DOOR_HALF_WIDTH = 0.65;
        public static final double DOOR_HEIGHT = 2.15;

        // Palette textures
        public static final ColorTexture skyTex = new ColorTexture(Color.newCol(0.55f, 0.78f, 0.96f));
        public static final ColorTexture grassTex = new ColorTexture(Color.newCol(0.28f, 0.58f, 0.24f));
        public static final ColorTexture grassBorderTex = new ColorTexture(Color.newCol(0.23f, 0.50f, 0.20f));
        public static final ColorTexture pathStoneTex = new ColorTexture(Color.newCol(0.68f, 0.66f, 0.62f));
        public static final ColorTexture steppingStoneTex = new ColorTexture(Color.newCol(0.76f, 0.74f, 0.70f));

        public static final ColorTexture wallExtTex = new ColorTexture(Color.newCol(0.94f, 0.89f, 0.78f));
        public static final ColorTexture wallBaseTex = new ColorTexture(Color.newCol(0.55f, 0.50f, 0.45f));
        public static final ColorTexture roofTex = new ColorTexture(Color.newCol(0.72f, 0.26f, 0.18f));
        public static final ColorTexture gableTex = new ColorTexture(Color.newCol(0.88f, 0.82f, 0.70f));
        public static final ColorTexture chimneyTex = new ColorTexture(Color.newCol(0.58f, 0.24f, 0.20f));

        public static final ColorTexture floorWoodTex = new ColorTexture(Color.newCol(0.62f, 0.43f, 0.26f));
        public static final ColorTexture porchTex = new ColorTexture(Color.newCol(0.60f, 0.56f, 0.52f));
        public static final ColorTexture matTex = new ColorTexture(Color.newCol(0.52f, 0.22f, 0.16f));
        public static final ColorTexture rugTex = new ColorTexture(Color.newCol(0.22f, 0.42f, 0.58f));

        public static final ColorTexture woodDarkTex = new ColorTexture(Color.newCol(0.38f, 0.24f, 0.13f));
        public static final ColorTexture woodWarmTex = new ColorTexture(Color.newCol(0.54f, 0.35f, 0.18f));
        public static final ColorTexture doorBrassTex = new ColorTexture(Color.newCol(0.90f, 0.75f, 0.20f));
        public static final ColorTexture fenceTex = new ColorTexture(Color.newCol(0.85f, 0.82f, 0.75f));

        public static final ColorTexture trunkTex = new ColorTexture(Color.newCol(0.42f, 0.26f, 0.14f));
        public static final ColorTexture leavesDarkTex = new ColorTexture(Color.newCol(0.16f, 0.46f, 0.18f));
        public static final ColorTexture leavesBrightTex = new ColorTexture(Color.newCol(0.24f, 0.62f, 0.24f));
        public static final ColorTexture flowerRedTex = new ColorTexture(Color.newCol(0.92f, 0.20f, 0.25f));
        public static final ColorTexture flowerYellowTex = new ColorTexture(Color.newCol(0.96f, 0.82f, 0.15f));
        public static final ColorTexture flowerPinkTex = new ColorTexture(Color.newCol(0.94f, 0.45f, 0.72f));
        public static final ColorTexture pondWaterTex = new ColorTexture(Color.newCol(0.22f, 0.58f, 0.82f));

        public static final ColorTexture sofaTex = new ColorTexture(Color.newCol(0.25f, 0.48f, 0.42f));
        public static final ColorTexture bedSheetTex = new ColorTexture(Color.newCol(0.88f, 0.35f, 0.38f));
        public static final ColorTexture pillowTex = new ColorTexture(Color.newCol(0.96f, 0.96f, 0.98f));

        public static boolean isInsideHouse(double x, double z) {
            return x > HOUSE_X_MIN + 0.15 && x < HOUSE_X_MAX - 0.15
                    && z > HOUSE_Z_MIN + 0.10 && z < HOUSE_Z_MAX - 0.15;
        }

        public static String getZoneName(double x, double z) {
            if (isInsideHouse(x, z)) {
                return "Inside House";
            }
            if (Math.abs(x) <= 1.3 && z >= 2.3 && z <= HOUSE_Z_MIN + 0.2) {
                return "Front Porch / Doorway";
            }
            if (Math.abs(x) <= 0.9 && z >= -4.2 && z < 2.3) {
                return "Garden Path";
            }
            return "Garden";
        }

        /**
         * Collision check against house walls, perimeter fence, and major obstacles.
         */
        public static boolean isBlocked(double x, double z, boolean doorOpen) {
            double r = 0.26; // Character collision radius

            // Garden outer boundary
            if (x < -10.5 || x > 10.5 || z < -8.5 || z > 12.5) {
                return true;
            }

            // Front Wall (Z = HOUSE_Z_MIN = 3.5) with doorway at X in [-0.65, 0.65]
            if (Math.abs(z - HOUSE_Z_MIN) < r && x >= HOUSE_X_MIN - r && x <= HOUSE_X_MAX + r) {
                boolean inDoorway = Math.abs(x) < (DOOR_HALF_WIDTH - 0.15);
                if (!inDoorway || !doorOpen) {
                    return true;
                }
            }

            // Back Wall (Z = HOUSE_Z_MAX = 9.8) with back doorway at X in [-0.65, 0.65]
            if (Math.abs(z - HOUSE_Z_MAX) < r && x >= HOUSE_X_MIN - r && x <= HOUSE_X_MAX + r) {
                boolean inBackDoorway = Math.abs(x) < (DOOR_HALF_WIDTH - 0.15);
                if (!inBackDoorway) {
                    return true;
                }
            }

            // Left Wall (X = HOUSE_X_MIN = -3.8)
            if (Math.abs(x - HOUSE_X_MIN) < r && z >= HOUSE_Z_MIN - r && z <= HOUSE_Z_MAX + r) {
                return true;
            }

            // Right Wall (X = HOUSE_X_MAX = 3.8)
            if (Math.abs(x - HOUSE_X_MAX) < r && z >= HOUSE_Z_MIN - r && z <= HOUSE_Z_MAX + r) {
                return true;
            }

            // Dining table obstacle inside house (left side)
            if (x > -2.85 && x < -1.55 && z > 5.55 && z < 6.85) {
                return true;
            }

            return false;
        }

        public static void drawToScene(Scene scene) {
            drawToScene(scene, Math.toRadians(82.0), true);
        }

        public static void drawToScene(Scene scene, double doorOpenAngleRadians, boolean drawRoof) {
            if (scene == null) return;
            scene.texture(skyTex);

            // 1. Garden Lawn & Paths
            drawGardenGround(scene);

            // 2. Garden Trees, Bushes, Flower Beds, Bench, Pond & Fence
            drawGardenProps(scene);

            // 3. House Floor, Porch, Walls, Windows & Animated Door
            drawHouseStructure(scene, doorOpenAngleRadians, drawRoof);

            // 4. Interior Furniture (Table, Chairs, Sofa, Rug, Bed)
            drawHouseFurniture(scene);
        }

        private static void drawGardenGround(Scene scene) {
            // Tiled lawn quads for crisp perspective and frustum clipping
            double[] xs = {-10.0, -4.0, 4.0, 10.0};
            double[] zs = {-8.0, -2.0, 3.5, 10.0, 13.0};
            for (int i = 0; i < xs.length - 1; i++) {
                for (int j = 0; j < zs.length - 1; j++) {
                    ColorTexture tex = ((i + j) % 2 == 0) ? grassTex : grassBorderTex;
                    addQuad(scene,
                            new Point3D(xs[i], 0.0, zs[j]),
                            new Point3D(xs[i + 1], 0.0, zs[j]),
                            new Point3D(xs[i + 1], 0.0, zs[j + 1]),
                            new Point3D(xs[i], 0.0, zs[j + 1]),
                            tex, 0.5);
                }
            }

            // Stone Garden Path leading from front gate (Z = -4.0) to Front Porch (Z = 2.7)
            addQuad(scene,
                    new Point3D(-0.70, 0.012, -4.0),
                    new Point3D(0.70, 0.012, -4.0),
                    new Point3D(0.70, 0.012, 2.7),
                    new Point3D(-0.70, 0.012, 2.7),
                    pathStoneTex, 0.5);

            // Decorative stepping stones along the path
            for (double z = -3.5; z <= 2.1; z += 0.8) {
                addQuad(scene,
                        new Point3D(-0.48, 0.018, z),
                        new Point3D(0.48, 0.018, z),
                        new Point3D(0.48, 0.018, z + 0.52),
                        new Point3D(-0.48, 0.018, z + 0.52),
                        steppingStoneTex, 1.0);
            }

            // Front Porch platform
            addQuad(scene,
                    new Point3D(-1.35, 0.022, 2.65),
                    new Point3D(1.35, 0.022, 2.65),
                    new Point3D(1.35, 0.022, HOUSE_Z_MIN),
                    new Point3D(-1.35, 0.022, HOUSE_Z_MIN),
                    porchTex, 1.0);
        }

        private static void drawGardenProps(Scene scene) {
            // Trees around the garden
            drawTree(scene, -4.8, -1.8, 2.3, 0.95);
            drawTree(scene, 4.8, -1.6, 2.4, 1.00);
            drawTree(scene, -5.6, 2.4, 2.6, 1.10);
            drawTree(scene, 5.6, 2.6, 2.5, 1.05);
            drawTree(scene, -5.2, 10.6, 2.5, 1.00);
            drawTree(scene, 5.2, 10.6, 2.5, 1.00);

            // Flower beds along the garden path and in front of the house windows
            drawFlowerBush(scene, -1.15, -1.5, flowerRedTex);
            drawFlowerBush(scene, 1.15, -1.5, flowerYellowTex);
            drawFlowerBush(scene, -1.15, 1.0, flowerPinkTex);
            drawFlowerBush(scene, 1.15, 1.0, flowerRedTex);

            // Under front windows
            drawFlowerBush(scene, -2.1, 3.05, flowerYellowTex);
            drawFlowerBush(scene, -2.7, 3.05, flowerPinkTex);
            drawFlowerBush(scene, 2.1, 3.05, flowerRedTex);
            drawFlowerBush(scene, 2.7, 3.05, flowerYellowTex);

            // Garden Bench on the left lawn (X = -2.35, Z = 0.5) facing +X toward the path
            drawGardenBench(scene, -2.35, 0.5);

            // Decorative Garden Pond on the right lawn (X = 2.5, Z = 0.5)
            drawGardenPond(scene, 2.5, 0.5);

            // Low wooden garden fence along front (Z = -4.0) with open gate at X in [-1.0, 1.0]
            drawFenceSegment(scene, -6.0, -4.0, -1.0, -4.0, 5);
            drawFenceSegment(scene, 1.0, -4.0, 6.0, -4.0, 5);
        }

        private static void drawTree(Scene scene, double x, double z, double trunkHeight, double canopyScale) {
            addCylinder(scene,
                    new Point3D(x, 0.0, z),
                    new Point3D(x, trunkHeight * 0.72, z),
                    0.16 * canopyScale, trunkTex);
            addSphere(scene, new Point3D(x, trunkHeight, z), 0.72 * canopyScale, leavesDarkTex);
            addSphere(scene, new Point3D(x - 0.35 * canopyScale, trunkHeight - 0.15, z + 0.25 * canopyScale), 0.52 * canopyScale, leavesBrightTex);
            addSphere(scene, new Point3D(x + 0.35 * canopyScale, trunkHeight - 0.12, z - 0.20 * canopyScale), 0.52 * canopyScale, leavesBrightTex);
            addSphere(scene, new Point3D(x, trunkHeight + 0.45 * canopyScale, z), 0.48 * canopyScale, leavesBrightTex);
        }

        private static void drawFlowerBush(Scene scene, double x, double z, ColorTexture blossomTex) {
            addSphere(scene, new Point3D(x, 0.18, z), 0.24, leavesDarkTex);
            addSphere(scene, new Point3D(x - 0.12, 0.15, z + 0.08), 0.18, leavesBrightTex);
            addSphere(scene, new Point3D(x + 0.12, 0.15, z - 0.06), 0.18, leavesBrightTex);
            // Blossoms
            addSphere(scene, new Point3D(x, 0.36, z + 0.04), 0.065, blossomTex);
            addSphere(scene, new Point3D(x - 0.11, 0.30, z - 0.05), 0.055, blossomTex);
            addSphere(scene, new Point3D(x + 0.11, 0.31, z + 0.06), 0.055, blossomTex);
        }

        private static void drawGardenBench(Scene scene, double cx, double cz) {
            double seatY = 0.44;
            double halfLen = 0.65;
            double halfDepth = 0.22;
            // Seat plank
            addQuad(scene,
                    new Point3D(cx - halfDepth, seatY, cz - halfLen),
                    new Point3D(cx + halfDepth, seatY, cz - halfLen),
                    new Point3D(cx + halfDepth, seatY, cz + halfLen),
                    new Point3D(cx - halfDepth, seatY, cz + halfLen),
                    woodWarmTex, 1.0);
            // Backrest plank (on the -X side so sitter faces +X toward garden path)
            addQuad(scene,
                    new Point3D(cx - halfDepth, seatY, cz - halfLen),
                    new Point3D(cx - halfDepth, seatY + 0.42, cz - halfLen),
                    new Point3D(cx - halfDepth, seatY + 0.42, cz + halfLen),
                    new Point3D(cx - halfDepth, seatY, cz + halfLen),
                    woodDarkTex, 1.0);
            // 4 legs
            addCylinder(scene, new Point3D(cx - 0.18, 0.0, cz - 0.55), new Point3D(cx - 0.18, seatY + 0.42, cz - 0.55), 0.04, woodDarkTex);
            addCylinder(scene, new Point3D(cx - 0.18, 0.0, cz + 0.55), new Point3D(cx - 0.18, seatY + 0.42, cz + 0.55), 0.04, woodDarkTex);
            addCylinder(scene, new Point3D(cx + 0.18, 0.0, cz - 0.55), new Point3D(cx + 0.18, seatY, cz - 0.55), 0.04, woodDarkTex);
            addCylinder(scene, new Point3D(cx + 0.18, 0.0, cz + 0.55), new Point3D(cx + 0.18, seatY, cz + 0.55), 0.04, woodDarkTex);
        }

        private static void drawGardenPond(Scene scene, double cx, double cz) {
            double rx = 0.85;
            double rz = 0.65;
            addQuad(scene,
                    new Point3D(cx - rx, 0.02, cz - rz),
                    new Point3D(cx + rx, 0.02, cz - rz),
                    new Point3D(cx + rx, 0.02, cz + rz),
                    new Point3D(cx - rx, 0.02, cz + rz),
                    pondWaterTex, 1.0);
            // Border stones around pond
            for (int i = 0; i < 12; i++) {
                double ang = (Math.PI * 2.0 * i) / 12.0;
                double sx = cx + Math.cos(ang) * (rx + 0.08);
                double sz = cz + Math.sin(ang) * (rz + 0.08);
                addSphere(scene, new Point3D(sx, 0.06, sz), 0.14, pathStoneTex);
            }
        }

        private static void drawFenceSegment(Scene scene, double x1, double z1, double x2, double z2, int posts) {
            for (int i = 0; i <= posts; i++) {
                double t = (double) i / posts;
                double px = x1 + (x2 - x1) * t;
                double pz = z1 + (z2 - z1) * t;
                addCylinder(scene, new Point3D(px, 0.0, pz), new Point3D(px, 0.72, pz), 0.045, fenceTex);
                addSphere(scene, new Point3D(px, 0.75, pz), 0.055, fenceTex);
            }
            addCylinder(scene, new Point3D(x1, 0.28, z1), new Point3D(x2, 0.28, z2), 0.028, fenceTex);
            addCylinder(scene, new Point3D(x1, 0.56, z1), new Point3D(x2, 0.56, z2), 0.028, fenceTex);
        }

        private static void drawHouseStructure(Scene scene, double doorOpenAngle, boolean drawRoof) {
            // Interior Hardwood Floor
            addQuad(scene,
                    new Point3D(HOUSE_X_MIN, 0.02, HOUSE_Z_MIN),
                    new Point3D(HOUSE_X_MAX, 0.02, HOUSE_Z_MIN),
                    new Point3D(HOUSE_X_MAX, 0.02, HOUSE_Z_MAX),
                    new Point3D(HOUSE_X_MIN, 0.02, HOUSE_Z_MAX),
                    floorWoodTex, 0.5);

            // Entrance Welcome Mat
            addQuad(scene,
                    new Point3D(-0.55, 0.03, HOUSE_Z_MIN + 0.08),
                    new Point3D(0.55, 0.03, HOUSE_Z_MIN + 0.08),
                    new Point3D(0.55, 0.03, HOUSE_Z_MIN + 0.78),
                    new Point3D(-0.55, 0.03, HOUSE_Z_MIN + 0.78),
                    matTex, 1.0);

            // ---- FRONT WALL (Z = HOUSE_Z_MIN = 3.5) ----
            // Left of left window (-3.8 to -2.8)
            addWallZ(scene, HOUSE_X_MIN, -2.8, 0.0, WALL_HEIGHT, HOUSE_Z_MIN, wallExtTex);
            // Below & above left window (-2.8 to -1.4, window Y in [0.85, 1.85])
            addWallZ(scene, -2.8, -1.4, 0.0, 0.85, HOUSE_Z_MIN, wallExtTex);
            addWallZ(scene, -2.8, -1.4, 1.85, WALL_HEIGHT, HOUSE_Z_MIN, wallExtTex);
            drawWindowFrameZ(scene, -2.8, -1.4, 0.85, 1.85, HOUSE_Z_MIN);
            // Between left window and doorway (-1.4 to -0.65)
            addWallZ(scene, -1.4, -DOOR_HALF_WIDTH, 0.0, WALL_HEIGHT, HOUSE_Z_MIN, wallExtTex);
            // Above front doorway (-0.65 to 0.65, Y in [2.15, 2.6])
            addWallZ(scene, -DOOR_HALF_WIDTH, DOOR_HALF_WIDTH, DOOR_HEIGHT, WALL_HEIGHT, HOUSE_Z_MIN, wallExtTex);
            // Between doorway and right window (0.65 to 1.4)
            addWallZ(scene, DOOR_HALF_WIDTH, 1.4, 0.0, WALL_HEIGHT, HOUSE_Z_MIN, wallExtTex);
            // Below & above right window (1.4 to 2.8, window Y in [0.85, 1.85])
            addWallZ(scene, 1.4, 2.8, 0.0, 0.85, HOUSE_Z_MIN, wallExtTex);
            addWallZ(scene, 1.4, 2.8, 1.85, WALL_HEIGHT, HOUSE_Z_MIN, wallExtTex);
            drawWindowFrameZ(scene, 1.4, 2.8, 0.85, 1.85, HOUSE_Z_MIN);
            // Right of right window (2.8 to 3.8)
            addWallZ(scene, 2.8, HOUSE_X_MAX, 0.0, WALL_HEIGHT, HOUSE_Z_MIN, wallExtTex);

            // Front Door Frame & Swinging Wooden Door (hinged at X = -0.65, Z = 3.5)
            addCylinder(scene, new Point3D(-DOOR_HALF_WIDTH, 0.0, HOUSE_Z_MIN), new Point3D(-DOOR_HALF_WIDTH, DOOR_HEIGHT, HOUSE_Z_MIN), 0.045, woodDarkTex);
            addCylinder(scene, new Point3D(DOOR_HALF_WIDTH, 0.0, HOUSE_Z_MIN), new Point3D(DOOR_HALF_WIDTH, DOOR_HEIGHT, HOUSE_Z_MIN), 0.045, woodDarkTex);
            addCylinder(scene, new Point3D(-DOOR_HALF_WIDTH, DOOR_HEIGHT, HOUSE_Z_MIN), new Point3D(DOOR_HALF_WIDTH, DOOR_HEIGHT, HOUSE_Z_MIN), 0.045, woodDarkTex);

            double doorWidth = DOOR_HALF_WIDTH * 2.0 - 0.04;
            double doorEndX = -DOOR_HALF_WIDTH + doorWidth * Math.cos(doorOpenAngle);
            double doorEndZ = HOUSE_Z_MIN + doorWidth * Math.sin(doorOpenAngle);
            addQuad(scene,
                    new Point3D(-DOOR_HALF_WIDTH, 0.02, HOUSE_Z_MIN),
                    new Point3D(doorEndX, 0.02, doorEndZ),
                    new Point3D(doorEndX, DOOR_HEIGHT - 0.03, doorEndZ),
                    new Point3D(-DOOR_HALF_WIDTH, DOOR_HEIGHT - 0.03, HOUSE_Z_MIN),
                    woodWarmTex, 1.0);
            // Brass doorknob
            double knobX = -DOOR_HALF_WIDTH + (doorWidth * 0.86) * Math.cos(doorOpenAngle);
            double knobZ = HOUSE_Z_MIN + (doorWidth * 0.86) * Math.sin(doorOpenAngle);
            addSphere(scene, new Point3D(knobX, 1.02, knobZ), 0.035, doorBrassTex);

            // ---- BACK WALL (Z = HOUSE_Z_MAX = 9.8) ----
            addWallZ(scene, HOUSE_X_MIN, -2.6, 0.0, WALL_HEIGHT, HOUSE_Z_MAX, wallExtTex);
            addWallZ(scene, -2.6, -1.3, 0.0, 0.85, HOUSE_Z_MAX, wallExtTex);
            addWallZ(scene, -2.6, -1.3, 1.85, WALL_HEIGHT, HOUSE_Z_MAX, wallExtTex);
            drawWindowFrameZ(scene, -2.6, -1.3, 0.85, 1.85, HOUSE_Z_MAX);
            addWallZ(scene, -1.3, -DOOR_HALF_WIDTH, 0.0, WALL_HEIGHT, HOUSE_Z_MAX, wallExtTex);
            addWallZ(scene, -DOOR_HALF_WIDTH, DOOR_HALF_WIDTH, DOOR_HEIGHT, WALL_HEIGHT, HOUSE_Z_MAX, wallExtTex);
            addWallZ(scene, DOOR_HALF_WIDTH, 1.3, 0.0, WALL_HEIGHT, HOUSE_Z_MAX, wallExtTex);
            addWallZ(scene, 1.3, 2.6, 0.0, 0.85, HOUSE_Z_MAX, wallExtTex);
            addWallZ(scene, 1.3, 2.6, 1.85, WALL_HEIGHT, HOUSE_Z_MAX, wallExtTex);
            drawWindowFrameZ(scene, 1.3, 2.6, 0.85, 1.85, HOUSE_Z_MAX);
            addWallZ(scene, 2.6, HOUSE_X_MAX, 0.0, WALL_HEIGHT, HOUSE_Z_MAX, wallExtTex);

            // ---- LEFT WALL (X = HOUSE_X_MIN = -3.8) with side window ----
            addWallX(scene, HOUSE_X_MIN, HOUSE_Z_MIN, 5.5, 0.0, WALL_HEIGHT, wallExtTex);
            addWallX(scene, HOUSE_X_MIN, 5.5, 7.6, 0.0, 0.85, wallExtTex);
            addWallX(scene, HOUSE_X_MIN, 5.5, 7.6, 1.85, WALL_HEIGHT, wallExtTex);
            drawWindowFrameX(scene, HOUSE_X_MIN, 5.5, 7.6, 0.85, 1.85);
            addWallX(scene, HOUSE_X_MIN, 7.6, HOUSE_Z_MAX, 0.0, WALL_HEIGHT, wallExtTex);

            // ---- RIGHT WALL (X = HOUSE_X_MAX = 3.8) with side window ----
            addWallX(scene, HOUSE_X_MAX, HOUSE_Z_MIN, 5.5, 0.0, WALL_HEIGHT, wallExtTex);
            addWallX(scene, HOUSE_X_MAX, 5.5, 7.6, 0.0, 0.85, wallExtTex);
            addWallX(scene, HOUSE_X_MAX, 5.5, 7.6, 1.85, WALL_HEIGHT, wallExtTex);
            drawWindowFrameX(scene, HOUSE_X_MAX, 5.5, 7.6, 0.85, 1.85);
            addWallX(scene, HOUSE_X_MAX, 7.6, HOUSE_Z_MAX, 0.0, WALL_HEIGHT, wallExtTex);

            // Corner pillars for crisp architectural definition
            addCylinder(scene, new Point3D(HOUSE_X_MIN, 0.0, HOUSE_Z_MIN), new Point3D(HOUSE_X_MIN, WALL_HEIGHT, HOUSE_Z_MIN), 0.06, wallBaseTex);
            addCylinder(scene, new Point3D(HOUSE_X_MAX, 0.0, HOUSE_Z_MIN), new Point3D(HOUSE_X_MAX, WALL_HEIGHT, HOUSE_Z_MIN), 0.06, wallBaseTex);
            addCylinder(scene, new Point3D(HOUSE_X_MIN, 0.0, HOUSE_Z_MAX), new Point3D(HOUSE_X_MIN, WALL_HEIGHT, HOUSE_Z_MAX), 0.06, wallBaseTex);
            addCylinder(scene, new Point3D(HOUSE_X_MAX, 0.0, HOUSE_Z_MAX), new Point3D(HOUSE_X_MAX, WALL_HEIGHT, HOUSE_Z_MAX), 0.06, wallBaseTex);

            // ---- PITCHED ROOF & GABLES ----
            if (drawRoof) {
                double eaveX = 0.28;
                double eaveZ = 0.28;
                // Left roof slope
                addQuad(scene,
                        new Point3D(HOUSE_X_MIN - eaveX, WALL_HEIGHT - 0.05, HOUSE_Z_MIN - eaveZ),
                        new Point3D(0.0, ROOF_PEAK_Y, HOUSE_Z_MIN - eaveZ),
                        new Point3D(0.0, ROOF_PEAK_Y, HOUSE_Z_MAX + eaveZ),
                        new Point3D(HOUSE_X_MIN - eaveX, WALL_HEIGHT - 0.05, HOUSE_Z_MAX + eaveZ),
                        roofTex, 0.5);
                // Right roof slope
                addQuad(scene,
                        new Point3D(0.0, ROOF_PEAK_Y, HOUSE_Z_MIN - eaveZ),
                        new Point3D(HOUSE_X_MAX + eaveX, WALL_HEIGHT - 0.05, HOUSE_Z_MIN - eaveZ),
                        new Point3D(HOUSE_X_MAX + eaveX, WALL_HEIGHT - 0.05, HOUSE_Z_MAX + eaveZ),
                        new Point3D(0.0, ROOF_PEAK_Y, HOUSE_Z_MAX + eaveZ),
                        roofTex, 0.5);
                // Front & Back Gables (triangular quads)
                addQuad(scene,
                        new Point3D(HOUSE_X_MIN, WALL_HEIGHT, HOUSE_Z_MIN),
                        new Point3D(HOUSE_X_MAX, WALL_HEIGHT, HOUSE_Z_MIN),
                        new Point3D(0.0, ROOF_PEAK_Y, HOUSE_Z_MIN),
                        new Point3D(0.0, ROOF_PEAK_Y, HOUSE_Z_MIN),
                        gableTex, 1.0);
                addQuad(scene,
                        new Point3D(HOUSE_X_MIN, WALL_HEIGHT, HOUSE_Z_MAX),
                        new Point3D(HOUSE_X_MAX, WALL_HEIGHT, HOUSE_Z_MAX),
                        new Point3D(0.0, ROOF_PEAK_Y, HOUSE_Z_MAX),
                        new Point3D(0.0, ROOF_PEAK_Y, HOUSE_Z_MAX),
                        gableTex, 1.0);
                // Chimney
                addCylinder(scene, new Point3D(2.1, WALL_HEIGHT + 0.3, 7.8), new Point3D(2.1, ROOF_PEAK_Y + 0.45, 7.8), 0.24, chimneyTex);
            }
        }

        private static void drawHouseFurniture(Scene scene) {
            // 1. Dining Table & Chairs on Left Side (X = -2.2, Z = 6.2)
            double tx = -2.2;
            double tz = 6.2;
            double tableY = 0.74;
            addQuad(scene,
                    new Point3D(tx - 0.55, tableY, tz - 0.55),
                    new Point3D(tx + 0.55, tableY, tz - 0.55),
                    new Point3D(tx + 0.55, tableY, tz + 0.55),
                    new Point3D(tx - 0.55, tableY, tz + 0.55),
                    woodWarmTex, 1.0);
            addCylinder(scene, new Point3D(tx - 0.45, 0.02, tz - 0.45), new Point3D(tx - 0.45, tableY, tz - 0.45), 0.035, woodDarkTex);
            addCylinder(scene, new Point3D(tx + 0.45, 0.02, tz - 0.45), new Point3D(tx + 0.45, tableY, tz - 0.45), 0.035, woodDarkTex);
            addCylinder(scene, new Point3D(tx - 0.45, 0.02, tz + 0.45), new Point3D(tx - 0.45, tableY, tz + 0.45), 0.035, woodDarkTex);
            addCylinder(scene, new Point3D(tx + 0.45, 0.02, tz + 0.45), new Point3D(tx + 0.45, tableY, tz + 0.45), 0.035, woodDarkTex);

            // Chair next to dining table
            drawChair(scene, tx + 0.85, tz, true);

            // 2. Living Room Woven Rug & Sofa on Right Side (X = 2.1, Z = 6.2)
            addQuad(scene,
                    new Point3D(0.75, 0.03, 5.0),
                    new Point3D(3.15, 0.03, 5.0),
                    new Point3D(3.15, 0.03, 7.4),
                    new Point3D(0.75, 0.03, 7.4),
                    rugTex, 1.0);

            double sx = 2.45;
            double sz = 6.2;
            double sofaSeatY = 0.44;
            // Sofa seat
            addQuad(scene,
                    new Point3D(sx - 0.45, sofaSeatY, sz - 0.85),
                    new Point3D(sx + 0.35, sofaSeatY, sz - 0.85),
                    new Point3D(sx + 0.35, sofaSeatY, sz + 0.85),
                    new Point3D(sx - 0.45, sofaSeatY, sz + 0.85),
                    sofaTex, 1.0);
            // Sofa front skirt
            addQuad(scene,
                    new Point3D(sx - 0.45, 0.03, sz - 0.85),
                    new Point3D(sx - 0.45, sofaSeatY, sz - 0.85),
                    new Point3D(sx - 0.45, sofaSeatY, sz + 0.85),
                    new Point3D(sx - 0.45, 0.03, sz + 0.85),
                    sofaTex, 1.0);
            // Sofa backrest
            addQuad(scene,
                    new Point3D(sx + 0.32, sofaSeatY, sz - 0.85),
                    new Point3D(sx + 0.32, sofaSeatY + 0.48, sz - 0.85),
                    new Point3D(sx + 0.32, sofaSeatY + 0.48, sz + 0.85),
                    new Point3D(sx + 0.32, sofaSeatY, sz + 0.85),
                    sofaTex, 1.0);
            // Sofa armrests
            addCylinder(scene, new Point3D(sx - 0.42, sofaSeatY + 0.14, sz - 0.85), new Point3D(sx + 0.32, sofaSeatY + 0.14, sz - 0.85), 0.11, sofaTex);
            addCylinder(scene, new Point3D(sx - 0.42, sofaSeatY + 0.14, sz + 0.85), new Point3D(sx + 0.32, sofaSeatY + 0.14, sz + 0.85), 0.11, sofaTex);

            // 3. Cozy Bed in Back-Left Corner (X = -2.3, Z = 8.4)
            double bx = -2.3;
            double bz = 8.4;
            double bedY = 0.38;
            addQuad(scene,
                    new Point3D(bx - 0.95, bedY, bz - 0.55),
                    new Point3D(bx + 0.95, bedY, bz - 0.55),
                    new Point3D(bx + 0.95, bedY, bz + 0.55),
                    new Point3D(bx - 0.95, bedY, bz + 0.55),
                    bedSheetTex, 1.0);
            // Pillow
            addCylinder(scene, new Point3D(bx - 0.72, bedY + 0.08, bz - 0.35), new Point3D(bx - 0.72, bedY + 0.08, bz + 0.35), 0.12, pillowTex);
        }

        private static void drawChair(Scene scene, double cx, double cz, boolean faceLeft) {
            double seatY = 0.44;
            double s = 0.22;
            addQuad(scene,
                    new Point3D(cx - s, seatY, cz - s),
                    new Point3D(cx + s, seatY, cz - s),
                    new Point3D(cx + s, seatY, cz + s),
                    new Point3D(cx - s, seatY, cz + s),
                    woodWarmTex, 1.0);
            double backX = faceLeft ? cx + s : cx - s;
            addQuad(scene,
                    new Point3D(backX, seatY, cz - s),
                    new Point3D(backX, seatY + 0.42, cz - s),
                    new Point3D(backX, seatY + 0.42, cz + s),
                    new Point3D(backX, seatY, cz + s),
                    woodDarkTex, 1.0);
            addCylinder(scene, new Point3D(cx - s, 0.02, cz - s), new Point3D(cx - s, seatY, cz - s), 0.025, woodDarkTex);
            addCylinder(scene, new Point3D(cx + s, 0.02, cz - s), new Point3D(cx + s, seatY, cz - s), 0.025, woodDarkTex);
            addCylinder(scene, new Point3D(cx - s, 0.02, cz + s), new Point3D(cx - s, seatY, cz + s), 0.025, woodDarkTex);
            addCylinder(scene, new Point3D(cx + s, 0.02, cz + s), new Point3D(cx + s, seatY, cz + s), 0.025, woodDarkTex);
        }

        private static void addWallZ(Scene scene, double x1, double x2, double y1, double y2, double z, ColorTexture tex) {
            addQuad(scene,
                    new Point3D(x1, y1, z),
                    new Point3D(x2, y1, z),
                    new Point3D(x2, y2, z),
                    new Point3D(x1, y2, z),
                    tex, 0.5);
        }

        private static void addWallX(Scene scene, double x, double z1, double z2, double y1, double y2, ColorTexture tex) {
            addQuad(scene,
                    new Point3D(x, y1, z1),
                    new Point3D(x, y1, z2),
                    new Point3D(x, y2, z2),
                    new Point3D(x, y2, z1),
                    tex, 0.5);
        }

        private static void drawWindowFrameZ(Scene scene, double x1, double x2, double y1, double y2, double z) {
            double r = 0.032;
            double xm = 0.5 * (x1 + x2);
            double ym = 0.5 * (y1 + y2);
            addCylinder(scene, new Point3D(x1, y1, z), new Point3D(x2, y1, z), r + 0.01, woodDarkTex);
            addCylinder(scene, new Point3D(x1, y2, z), new Point3D(x2, y2, z), r, woodDarkTex);
            addCylinder(scene, new Point3D(x1, y1, z), new Point3D(x1, y2, z), r, woodDarkTex);
            addCylinder(scene, new Point3D(x2, y1, z), new Point3D(x2, y2, z), r, woodDarkTex);
            // Cross mullions
            addCylinder(scene, new Point3D(xm, y1, z), new Point3D(xm, y2, z), r * 0.6, woodWarmTex);
            addCylinder(scene, new Point3D(x1, ym, z), new Point3D(x2, ym, z), r * 0.6, woodWarmTex);
        }

        private static void drawWindowFrameX(Scene scene, double x, double z1, double z2, double y1, double y2) {
            double r = 0.032;
            double zm = 0.5 * (z1 + z2);
            double ym = 0.5 * (y1 + y2);
            addCylinder(scene, new Point3D(x, y1, z1), new Point3D(x, y1, z2), r + 0.01, woodDarkTex);
            addCylinder(scene, new Point3D(x, y2, z1), new Point3D(x, y2, z2), r, woodDarkTex);
            addCylinder(scene, new Point3D(x, y1, z1), new Point3D(x, y2, z1), r, woodDarkTex);
            addCylinder(scene, new Point3D(x, y1, z2), new Point3D(x, y2, z2), r, woodDarkTex);
            addCylinder(scene, new Point3D(x, y1, zm), new Point3D(x, y2, zm), r * 0.6, woodWarmTex);
            addCylinder(scene, new Point3D(x, ym, z1), new Point3D(x, ym, z2), r * 0.6, woodWarmTex);
        }

        private static void addQuad(Scene s, Point3D p1, Point3D p2, Point3D p3, Point3D p4, ColorTexture tex, double incr) {
            one.empty3.library.Polygon poly = new one.empty3.library.Polygon(new Point3D[]{p1, p2, p3, p4}, tex);
            poly.setIncrU(incr);
            poly.setIncrV(incr);
            s.add(poly);
        }

        private static void addSphere(Scene s, Point3D pos, double radius, ColorTexture tex) {
            Sphere sphere = new Sphere(pos, radius);
            sphere.setIncrU(0.15);
            sphere.setIncrV(0.15);
            sphere.texture(tex);
            s.add(sphere);
        }

        private static void addCylinder(Scene s, Point3D start, Point3D end, double radius, ColorTexture tex) {
            Cylinder cylinder = new Cylinder(start, end, radius);
            cylinder.setIncrU(0.15);
            cylinder.setIncrV(0.15);
            cylinder.texture(tex);
            s.add(cylinder);
        }
    }

    // =========================================================================
    // 7. BACKWARD-COMPATIBLE HUMANBODY FACADE
    // =========================================================================

    public static class HumanBody {
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

        public HumanBody() {
            poseStand();
        }

        public void poseStand() {
            applyPose(PoseLibrary.stand());
        }

        public void poseHandsInTheAir() {
            applyPose(PoseLibrary.handsUp());
        }

        public void poseSit() {
            applyPose(PoseLibrary.sit());
        }

        public void poseLieDown() {
            applyPose(PoseLibrary.lieDown());
        }

        public void poseCrouch() {
            applyPose(PoseLibrary.crouch());
        }

        public void poseWalk(double t) {
            applyPose(PoseLibrary.walk(t));
        }

        public void poseRun(double t) {
            applyPose(PoseLibrary.run(t));
        }

        public void poseGetUp(double t) {
            PoseState lie = PoseLibrary.lieDown();
            PoseState stand = PoseLibrary.stand();
            applyPose(lie.interpolate(stand, t));
        }

        public PoseState toPoseState() {
            PoseState pose = new PoseState();
            pose.set(JointType.HEAD, head);
            pose.set(JointType.JAW, jaw);
            pose.set(JointType.NOSE, nose);
            pose.set(JointType.NECK, neck);

            pose.set(JointType.SHOULDER_L, shoulderLeft);
            pose.set(JointType.SHOULDER_R, shoulderRight);
            pose.set(JointType.ELBOW_L, elbowLeft);
            pose.set(JointType.ELBOW_R, elbowRight);
            pose.set(JointType.WRIST_L, wristLeft);
            pose.set(JointType.WRIST_R, wristRight);
            pose.set(JointType.HAND_L, handLeft);
            pose.set(JointType.HAND_R, handRight);

            pose.set(JointType.HIP_L, hipLeft);
            pose.set(JointType.HIP_R, hipRight);
            pose.set(JointType.KNEE_L, kneeLeft);
            pose.set(JointType.KNEE_R, kneeRight);
            pose.set(JointType.ANKLE_L, ankleLeft);
            pose.set(JointType.ANKLE_R, ankleRight);
            pose.set(JointType.FOOT_L_START, footLeftStart);
            pose.set(JointType.FOOT_L_END, footLeftEnd);
            pose.set(JointType.FOOT_R_START, footRightStart);
            pose.set(JointType.FOOT_R_END, footRightEnd);
            return pose;
        }

        public void applyPose(PoseState pose) {
            if (pose == null) return;
            head = pose.get(JointType.HEAD);
            jaw = pose.get(JointType.JAW);
            nose = pose.get(JointType.NOSE);
            neck = pose.get(JointType.NECK);

            shoulderLeft = pose.get(JointType.SHOULDER_L);
            shoulderRight = pose.get(JointType.SHOULDER_R);
            elbowLeft = pose.get(JointType.ELBOW_L);
            elbowRight = pose.get(JointType.ELBOW_R);
            wristLeft = pose.get(JointType.WRIST_L);
            wristRight = pose.get(JointType.WRIST_R);
            handLeft = pose.get(JointType.HAND_L);
            handRight = pose.get(JointType.HAND_R);

            hipLeft = pose.get(JointType.HIP_L);
            hipRight = pose.get(JointType.HIP_R);
            kneeLeft = pose.get(JointType.KNEE_L);
            kneeRight = pose.get(JointType.KNEE_R);
            ankleLeft = pose.get(JointType.ANKLE_L);
            ankleRight = pose.get(JointType.ANKLE_R);
            footLeftStart = pose.get(JointType.FOOT_L_START);
            footLeftEnd = pose.get(JointType.FOOT_L_END);
            footRightStart = pose.get(JointType.FOOT_R_START);
            footRightEnd = pose.get(JointType.FOOT_R_END);
        }

        public void drawToScene(Scene scene) {
            HumanBodyGeometry.drawToScene(scene, toPoseState());
        }
    }

    // =========================================================================
    // 8. INTERACTIVE GAME WINDOW (SWING GUI CONTROLLER)
    // =========================================================================

    public static class InteractiveGameWindow extends JFrame {
        private final CharacterController character;
        private final PoseTransitionController animator;
        private final RenderCanvas canvas;
        private final int renderWidth;
        private final int renderHeight;
        private final AtomicBoolean running = new AtomicBoolean(true);

        private final Set<Integer> activeKeys = ConcurrentHashMap.newKeySet();
        private double fps = 0.0;

        // Smooth camera tracking state
        private Point3D smoothCamEye = null;
        private Point3D smoothCamTarget = null;
        private int cameraMode = 0; // 0 = Smart Auto Chase, 1 = Close Follow, 2 = Scenic Garden & House Overview
        private boolean showRoof = true;

        public InteractiveGameWindow(CharacterController character, PoseTransitionController animator, int width, int height) {
            super("Empty3 - Interactive Human in House & Garden");
            this.character = character;
            this.animator = animator;
            this.renderWidth = width;
            this.renderHeight = height;

            this.canvas = new RenderCanvas();
            this.canvas.setPreferredSize(new Dimension(width, height));
            this.getContentPane().add(canvas, BorderLayout.CENTER);

            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setResizable(false);
            this.pack();
            this.setLocationRelativeTo(null);

            setupKeyboard();
            startRenderLoop();
        }

        private void setupKeyboard() {
            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    activeKeys.add(e.getKeyCode());
                    handleSingleKeyTriggers(e.getKeyCode());
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    activeKeys.remove(e.getKeyCode());
                }
            });

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusLost(FocusEvent e) {
                    activeKeys.clear();
                }
            });
        }

        private void handleSingleKeyTriggers(int keyCode) {
            switch (keyCode) {
                case KeyEvent.VK_1:
                    character.cancelNavigation();
                    animator.transitionTo("stand", 0.65);
                    break;
                case KeyEvent.VK_2:
                    character.cancelNavigation();
                    animator.transitionTo("hands_up", 0.65);
                    break;
                case KeyEvent.VK_3:
                    character.cancelNavigation();
                    animator.transitionTo("sit", 0.75);
                    break;
                case KeyEvent.VK_4:
                    character.cancelNavigation();
                    animator.transitionTo("lie_down", 0.95);
                    break;
                case KeyEvent.VK_5:
                    character.cancelNavigation();
                    animator.transitionTo("walk", 0.45);
                    break;
                case KeyEvent.VK_6:
                    character.cancelNavigation();
                    animator.transitionTo("run", 0.45);
                    break;
                case KeyEvent.VK_C:
                    character.cancelNavigation();
                    animator.transitionTo("crouch", 0.60);
                    break;
                case KeyEvent.VK_Z:
                    character.cancelNavigation();
                    animator.transitionTo("lie_down", 0.95);
                    break;
                case KeyEvent.VK_SPACE:
                    character.cancelNavigation();
                    animator.transitionTo("stand", 0.55);
                    break;
                case KeyEvent.VK_H:
                    character.walkIntoHouse();
                    break;
                case KeyEvent.VK_G:
                    character.walkIntoGarden();
                    break;
                case KeyEvent.VK_B:
                    character.walkAndSit();
                    break;
                case KeyEvent.VK_O:
                    character.toggleDoor();
                    break;
                case KeyEvent.VK_V:
                    cameraMode = (cameraMode + 1) % 3;
                    break;
                case KeyEvent.VK_T:
                    showRoof = !showRoof;
                    break;
                case KeyEvent.VK_R:
                    character.cancelNavigation();
                    character.setPosition(new Point3D(0.0, 0.0, -0.5));
                    character.setYaw(0.0);
                    animator.transitionTo("stand", 0.5);
                    break;
            }
        }

        private void updatePhysics(double deltaSeconds) {
            boolean sprinting = activeKeys.contains(KeyEvent.VK_SHIFT);
            double speedMultiplier = sprinting ? 1.95 : 1.0;

            boolean moveForward = activeKeys.contains(KeyEvent.VK_W) || activeKeys.contains(KeyEvent.VK_UP);
            boolean moveBackward = activeKeys.contains(KeyEvent.VK_S) || activeKeys.contains(KeyEvent.VK_DOWN);
            boolean turnLeft = activeKeys.contains(KeyEvent.VK_A) || activeKeys.contains(KeyEvent.VK_LEFT);
            boolean turnRight = activeKeys.contains(KeyEvent.VK_D) || activeKeys.contains(KeyEvent.VK_RIGHT);
            boolean strafeLeft = activeKeys.contains(KeyEvent.VK_Q);
            boolean strafeRight = activeKeys.contains(KeyEvent.VK_E);

            double targetForward = 0.0;
            if (moveForward && !moveBackward) targetForward = 1.0;
            else if (moveBackward && !moveForward) targetForward = -0.75;

            double targetStrafe = 0.0;
            if (strafeLeft && !strafeRight) targetStrafe = -1.0;
            else if (strafeRight && !strafeLeft) targetStrafe = 1.0;

            double targetTurn = 0.0;
            if (turnLeft && !turnRight) targetTurn = -1.0;
            else if (turnRight && !turnLeft) targetTurn = 1.0;

            double horizontalSpeed = character.updateSmoothMotion(
                    targetForward, targetStrafe, targetTurn, speedMultiplier, deltaSeconds, animator);

            boolean manualMoving = Math.abs(targetForward) > 0.01 || Math.abs(targetStrafe) > 0.01;
            boolean autoMoving = character.isNavigating() && horizontalSpeed > 0.12;
            boolean isMoving = manualMoving || autoMoving || horizontalSpeed > 0.18;

            // Adapt animation cycle speed smoothly to character velocity
            if (isMoving) {
                if (sprinting && manualMoving) {
                    animator.setCycleSpeed(7.8);
                    animator.transitionTo("run", 0.32);
                } else {
                    double cadence = Math.max(3.4, Math.min(6.2, horizontalSpeed * 3.1));
                    animator.setCycleSpeed(cadence);
                    animator.transitionTo("walk", 0.35);
                }
            } else {
                animator.setCycleSpeed(3.2);
                String targetPose = animator.getTargetPoseName();
                if ("walk".equalsIgnoreCase(targetPose) || "run".equalsIgnoreCase(targetPose)) {
                    animator.transitionTo("stand", 0.45);
                }
            }

            animator.update(deltaSeconds);
        }

        private Camera computeSmoothCamera(double deltaSeconds) {
            Point3D charPos = character.getPosition();
            double yaw = character.getYaw();
            boolean inside = character.isInsideHouse();

            Point3D desiredEye;
            Point3D desiredTarget;

            if (cameraMode == 2) {
                // Scenic overview of garden & house
                desiredEye = new Point3D(-4.2, 3.6, -4.8);
                desiredTarget = charPos.plus(new Point3D(0.0, 0.9, 1.2));
            } else {
                // Smart Indoor/Outdoor Chase Camera (pulls closer inside the house so walls/roof don't occlude)
                double camDist = (cameraMode == 1) ? 1.85 : (inside ? 1.95 : 3.10);
                double camHeight = (cameraMode == 1) ? 1.18 : (inside ? 1.22 : 1.38);
                double shoulderOffset = inside ? 0.18 : 0.32;

                desiredEye = charPos.plus(new Point3D(
                        -camDist * Math.sin(yaw) + shoulderOffset * Math.cos(yaw),
                        camHeight,
                        -camDist * Math.cos(yaw) - shoulderOffset * Math.sin(yaw)
                ));

                // Keep camera inside house walls when character is well inside
                if (inside) {
                    double clampedX = Math.max(HouseAndGardenGeometry.HOUSE_X_MIN + 0.25,
                            Math.min(HouseAndGardenGeometry.HOUSE_X_MAX - 0.25, desiredEye.getX()));
                    double clampedZ = Math.max(HouseAndGardenGeometry.HOUSE_Z_MIN - 1.5,
                            Math.min(HouseAndGardenGeometry.HOUSE_Z_MAX - 0.25, desiredEye.getZ()));
                    desiredEye = new Point3D(clampedX, desiredEye.getY(), clampedZ);
                }

                desiredTarget = charPos.plus(new Point3D(
                        0.35 * Math.sin(yaw),
                        0.92,
                        0.35 * Math.cos(yaw)
                ));
            }

            if (smoothCamEye == null || smoothCamTarget == null) {
                smoothCamEye = desiredEye;
                smoothCamTarget = desiredTarget;
            } else {
                double camBlend = 1.0 - Math.exp(-6.5 * deltaSeconds);
                smoothCamEye = PoseState.lerp(smoothCamEye, desiredEye, camBlend);
                smoothCamTarget = PoseState.lerp(smoothCamTarget, desiredTarget, camBlend);
            }

            return new Camera(smoothCamEye, smoothCamTarget, Point3D.Y);
        }

        private void startRenderLoop() {
            Thread loopThread = new Thread(() -> {
                long lastTime = System.nanoTime();
                int frameCount = 0;
                long fpsTimer = System.currentTimeMillis();

                while (running.get()) {
                    long now = System.nanoTime();
                    double deltaSeconds = (now - lastTime) / 1_000_000_000.0;
                    lastTime = now;
                    if (deltaSeconds > 0.1) deltaSeconds = 0.1;

                    updatePhysics(deltaSeconds);

                    // Build 3D Scene: House, Garden & Character
                    Scene scene = new Scene();
                    HouseAndGardenGeometry.drawToScene(scene, character.getDoorOpenAngle(), showRoof);

                    PoseState worldPose = character.getWorldPose(animator.getCurrentPose());
                    HumanBodyGeometry.drawToScene(scene, worldPose);

                    Camera camera = computeSmoothCamera(deltaSeconds);
                    scene.cameraActive(camera);

                    // Render scene via ZBufferImpl
                    ZBufferImpl zBuffer = new ZBufferImpl(renderWidth, renderHeight);
                    zBuffer.scene(scene);
                    zBuffer.camera(camera);
                    zBuffer.draw();

                    BufferedImage frameImage = zBuffer.image().getBi();
                    canvas.updateImage(frameImage);

                    frameCount++;
                    if (System.currentTimeMillis() - fpsTimer >= 1000) {
                        fps = frameCount * 1000.0 / (System.currentTimeMillis() - fpsTimer);
                        frameCount = 0;
                        fpsTimer = System.currentTimeMillis();
                    }

                    try {
                        Thread.sleep(16); // Target ~60 FPS cap
                    } catch (InterruptedException ignored) {
                        break;
                    }
                }
            }, "HumanRenderer-RenderLoop");
            loopThread.setDaemon(true);
            loopThread.start();
        }

        private class RenderCanvas extends JPanel {
            private volatile BufferedImage currentImage;

            public void updateImage(BufferedImage img) {
                this.currentImage = img;
                repaint();
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                if (currentImage != null) {
                    g2.drawImage(currentImage, 0, 0, getWidth(), getHeight(), null);
                } else {
                    g2.setColor(java.awt.Color.BLACK);
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }

                // HUD Overlay
                g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                g2.setColor(new java.awt.Color(0, 0, 0, 165));
                g2.fillRoundRect(10, 10, 540, 165, 10, 10);

                g2.setColor(new java.awt.Color(120, 255, 140));
                g2.setFont(new Font("Monospaced", Font.BOLD, 12));
                Point3D pos = character.getPosition();
                g2.drawString(String.format("FPS: %.1f | ZONE: %s | DOOR: %s",
                        fps, character.getCurrentZoneName(), character.isDoorOpen() ? "Open" : "Closed"), 20, 30);
                g2.drawString(String.format("POS: (%.2f, %.2f, %.2f) | YAW: %.1f deg | MODE: %s",
                        pos.getX(), pos.getY(), pos.getZ(), character.getYawDegrees(),
                        character.getActiveActivityLabel()), 20, 48);
                g2.drawString(String.format("POSE: %s", animator.getCurrentPoseName()), 20, 66);
                if (animator.isTransitioning()) {
                    g2.setColor(java.awt.Color.YELLOW);
                    g2.drawString(String.format("TRANSITION: -> %s (%.0f%%)",
                            animator.getTargetPoseName(), animator.getTransitionProgress() * 100.0), 160, 66);
                }

                g2.setColor(java.awt.Color.WHITE);
                g2.drawString("MOVE: Arrows / WASD (Smooth Walk) | Q/E: Strafe | Shift: Run", 20, 92);
                g2.drawString("SWEET ACTIONS: H: Enter House | G: Walk to Garden | B: Walk & Sit", 20, 112);
                g2.drawString("ENV & CAM: O: Open/Close Door | V: Camera View | T: Toggle Roof", 20, 132);
                g2.drawString("POSES: 1-6: Stand/HandsUp/Sit/Lie/Walk/Run | C: Crouch | R: Reset", 20, 152);
            }
        }
    }

    // =========================================================================
    // 9. COMMAND-LINE INTERFACE (CLI CONTROLLER REPL)
    // =========================================================================

    public static class CommandLineController {
        private final CharacterController character;
        private final PoseTransitionController animator;
        private final AtomicBoolean running = new AtomicBoolean(true);

        public CommandLineController(CharacterController character, PoseTransitionController animator) {
            this.character = character;
            this.animator = animator;
        }

        public void start() {
            Thread cliThread = new Thread(this::runRepl, "HumanRenderer-CLI");
            cliThread.setDaemon(true);
            cliThread.start();
        }

        private void runRepl() {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("==========================================================");
            System.out.println("  Empty3 Interactive Human Renderer - House & Garden CLI");
            System.out.println("  Type 'help' for command list or 'exit' to terminate.");
            System.out.println("==========================================================");

            while (running.get()) {
                System.out.print("empty3-human> ");
                try {
                    String line = reader.readLine();
                    if (line == null) break;
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    executeCommand(line);
                } catch (Exception e) {
                    System.err.println("CLI Error: " + e.getMessage());
                }
            }
        }

        public void executeCommand(String commandLine) {
            String[] parts = commandLine.trim().split("\\s+");
            String cmd = parts[0].toLowerCase();

            try {
                switch (cmd) {
                    case "help":
                        printHelp();
                        break;
                    case "pose":
                        if (parts.length < 2) {
                            System.out.println("Usage: pose <stand|hands_up|sit|lie_down|crouch|walk|run>");
                        } else {
                            character.cancelNavigation();
                            animator.setPose(parts[1]);
                            System.out.println("Pose set to: " + parts[1]);
                        }
                        break;
                    case "transition":
                        if (parts.length < 2) {
                            System.out.println("Usage: transition <targetPose> [durationInSeconds]");
                        } else {
                            character.cancelNavigation();
                            String target = parts[1];
                            double duration = parts.length >= 3 ? Double.parseDouble(parts[2]) : 1.0;
                            animator.transitionTo(target, duration);
                            System.out.printf("Transitioning to '%s' over %.2f seconds...%n", target, duration);
                        }
                        break;
                    case "enter":
                    case "house":
                        character.walkIntoHouse();
                        System.out.println("Character is walking sweetly along the path into the house...");
                        break;
                    case "garden":
                        character.walkIntoGarden();
                        System.out.println("Character is strolling out into the garden...");
                        break;
                    case "bench":
                    case "sofa":
                        character.walkAndSit();
                        System.out.println("Character is walking to a seat to sit down...");
                        break;
                    case "door":
                        if (parts.length >= 2) {
                            String arg = parts[1].toLowerCase();
                            if (arg.equals("open")) character.setDoorOpen(true);
                            else if (arg.equals("close")) character.setDoorOpen(false);
                            else character.toggleDoor();
                        } else {
                            character.toggleDoor();
                        }
                        System.out.println("House front door is now: " + (character.isDoorOpen() ? "OPEN" : "CLOSED"));
                        break;
                    case "move":
                        character.cancelNavigation();
                        if (parts.length == 2) {
                            double dist = Double.parseDouble(parts[1]);
                            character.moveForward(dist);
                            System.out.printf("Moved forward %.2f units. New pos: (%.2f, %.2f, %.2f) [%s]%n",
                                    dist, character.getPosition().getX(), character.getPosition().getY(),
                                    character.getPosition().getZ(), character.getCurrentZoneName());
                        } else if (parts.length >= 3) {
                            double dx = Double.parseDouble(parts[1]);
                            double dz = Double.parseDouble(parts[2]);
                            character.applyDisplacement(dx, dz);
                            System.out.printf("Displaced by (%.2f, 0.0, %.2f). New pos: (%.2f, %.2f, %.2f) [%s]%n",
                                    dx, dz, character.getPosition().getX(), character.getPosition().getY(),
                                    character.getPosition().getZ(), character.getCurrentZoneName());
                        } else {
                            System.out.println("Usage: move <distance> OR move <dx> <dz>");
                        }
                        break;
                    case "turn":
                        character.cancelNavigation();
                        if (parts.length < 2) {
                            System.out.println("Usage: turn <degrees>");
                        } else {
                            double deg = Double.parseDouble(parts[1]);
                            character.turnDegrees(deg);
                            System.out.printf("Turned by %.1f deg. New yaw: %.1f deg%n", deg, character.getYawDegrees());
                        }
                        break;
                    case "pos":
                        character.cancelNavigation();
                        if (parts.length >= 4) {
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);
                            double z = Double.parseDouble(parts[3]);
                            character.setPosition(new Point3D(x, y, z));
                            System.out.printf("Position set to (%.2f, %.2f, %.2f) [%s]%n",
                                    x, y, z, character.getCurrentZoneName());
                        } else {
                            System.out.println("Usage: pos <x> <y> <z>");
                        }
                        break;
                    case "yaw":
                        character.cancelNavigation();
                        if (parts.length >= 2) {
                            double deg = Double.parseDouble(parts[1]);
                            character.setYawDegrees(deg);
                            System.out.printf("Yaw set to %.1f deg%n", deg);
                        } else {
                            System.out.println("Usage: yaw <degrees>");
                        }
                        break;
                    case "status":
                        Point3D p = character.getPosition();
                        System.out.printf("Status: Pos=(%.2f, %.2f, %.2f) | Zone=%s | Yaw=%.1f deg | Pose=%s | Door=%s%n",
                                p.getX(), p.getY(), p.getZ(), character.getCurrentZoneName(),
                                character.getYawDegrees(), animator.getCurrentPoseName(),
                                character.isDoorOpen() ? "Open" : "Closed");
                        break;
                    case "snapshot":
                        String filename = parts.length >= 2 ? parts[1] : "snapshot_" + System.currentTimeMillis() + ".png";
                        takeSnapshot(filename, 800, 1000);
                        break;
                    case "export":
                        String dir = parts.length >= 2 ? parts[1] : ".";
                        exportCatalog(dir, 800, 1000);
                        break;
                    case "sequence":
                        if (parts.length >= 6) {
                            String p1 = parts[1];
                            String p2 = parts[2];
                            double dur = Double.parseDouble(parts[3]);
                            int fps = Integer.parseInt(parts[4]);
                            String prefix = parts[5];
                            exportTransitionSequence(p1, p2, dur, fps, prefix);
                        } else {
                            System.out.println("Usage: sequence <pose1> <pose2> <durationSeconds> <fps> <filePrefix>");
                        }
                        break;
                    case "exit":
                    case "quit":
                        System.out.println("Terminating session.");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Unknown command: '" + cmd + "'. Type 'help' for command list.");
                }
            } catch (Exception e) {
                System.err.println("Command execution error: " + e.getMessage());
            }
        }

        private void printHelp() {
            System.out.println("Available Commands:");
            System.out.println("  enter / house                      - Smoothly walk along the garden path into the house");
            System.out.println("  garden                             - Smoothly walk out into the garden");
            System.out.println("  bench / sofa                       - Walk to nearest bench/sofa and sit down");
            System.out.println("  door [open|close|toggle]           - Open or close the front door of the house");
            System.out.println("  pose <name>                        - Switch immediately to pose (stand, sit, lie_down, etc.)");
            System.out.println("  transition <name> [duration]       - Smoothly interpolate to target pose");
            System.out.println("  move <distance> | move <dx> <dz>   - Translate character in world space");
            System.out.println("  turn <degrees>                     - Rotate character heading by delta degrees");
            System.out.println("  pos <x> <y> <z>                    - Set absolute world coordinates");
            System.out.println("  yaw <degrees>                      - Set absolute facing heading in degrees");
            System.out.println("  status                             - Display position, zone, heading, and animation state");
            System.out.println("  snapshot [filename]                - Render and export current scene to PNG");
            System.out.println("  export [outputDir]                 - Batch render standard pose catalog in house & garden");
            System.out.println("  sequence <p1> <p2> <dur> <fps> <pre>- Batch render multi-frame transition sequence");
            System.out.println("  help                               - Show this help summary");
            System.out.println("  exit / quit                        - Exit application");
            System.out.println();
            System.out.println("GUI Controls (Game Window):");
            System.out.println("  Arrows / WASD : Smooth walk forward/backward & turn left/right (Q/E to strafe)");
            System.out.println("  Shift + Move  : Sprint (run pose)");
            System.out.println("  H / G / B     : Enter House / Walk to Garden / Walk & Sit on Bench or Sofa");
            System.out.println("  O / V / T     : Open-Close Door / Cycle Camera View / Toggle Roof");
            System.out.println("  1-6, C, Z, R  : Poses (Stand, HandsUp, Sit, LieDown, Walk, Run, Crouch) & Reset");
        }

        /**
         * Renders character scene to image file with camera
         */
        private void takeSnapshot(String filename, int width, int height) {
            Scene scene = new Scene();
            HouseAndGardenGeometry.drawToScene(scene, character.getDoorOpenAngle(), true);

            PoseState worldPose = character.getWorldPose(animator.getCurrentPose());
            HumanBodyGeometry.drawToScene(scene, worldPose);

            Point3D charPos = character.getPosition();
            double yaw = character.getYaw();
            double camDist = character.isInsideHouse() ? 1.95 : 3.1;
            Point3D camEye = charPos.plus(new Point3D(
                    -camDist * Math.sin(yaw) + 0.35 * Math.cos(yaw),
                    1.28,
                    -camDist * Math.cos(yaw) - 0.35 * Math.sin(yaw)
            ));
            Point3D camLookAt = charPos.plus(new Point3D(0.0, 0.9, 0.0));

            Camera camera = new Camera(camEye, camLookAt, Point3D.Y);
            scene.cameraActive(camera);

            ZBufferImpl zBuffer = new ZBufferImpl(width, height);
            zBuffer.scene(scene);
            zBuffer.camera(camera);
            zBuffer.draw();

            try {
                Image image = zBuffer.image();
                File file = new File(filename);
                image.saveFile(file);
                System.out.println("  [✔] Snapshot saved: " + file.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("  [❌] Failed to save snapshot: " + e.getMessage());
            }
        }
    }

    // =========================================================================
    // 10. BATCH EXPORT & TRANSITION SEQUENCE PIPELINE
    // =========================================================================

    public static void exportCatalog(String outputDir, int width, int height) {
        String[] poseNames = {
                "pose_1_debout.png",
                "pose_2_mains_en_lair.png",
                "pose_3_assis.png",
                "pose_4_couche.png",
                "pose_5_marche.png",
                "pose_6_course.png"
        };

        File dir = new File(outputDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        System.out.println("Starting anatomical pose catalog export in House & Garden...");

        for (int i = 0; i < poseNames.length; i++) {
            Scene scene = new Scene();
            HouseAndGardenGeometry.drawToScene(scene);

            PoseState pose;
            Point3D charPos = new Point3D(0.0, 0.0, 0.0);
            double charYaw = Math.PI;

            switch (i) {
                case 0:
                    pose = PoseLibrary.stand();
                    break;
                case 1:
                    pose = PoseLibrary.handsUp();
                    break;
                case 2:
                    pose = PoseLibrary.sit();
                    charPos = new Point3D(-1.95, 0.0, 0.5);
                    charYaw = Math.PI * 0.5;
                    break;
                case 3:
                    pose = PoseLibrary.lieDown();
                    break;
                case 4:
                    pose = PoseLibrary.walk(Math.PI / 4.0);
                    charPos = new Point3D(0.0, 0.0, 1.2);
                    charYaw = 0.0;
                    break;
                case 5:
                    pose = PoseLibrary.run(Math.PI / 3.0);
                    charPos = new Point3D(0.0, 0.0, 0.5);
                    charYaw = 0.0;
                    break;
                default:
                    pose = PoseLibrary.stand();
            }

            PoseState worldPose = pose.transform(charPos, charYaw);
            HumanBodyGeometry.drawToScene(scene, worldPose);

            Point3D cameraEye = new Point3D(0.6, 1.35, -3.1);
            Point3D cameraLookAt = charPos.plus(new Point3D(0.0, 0.95, 0.5));

            if (i == 2) {
                cameraEye = new Point3D(0.4, 1.15, -1.2);
                cameraLookAt = charPos.plus(new Point3D(0.0, 0.65, 0.2));
            } else if (i == 3) {
                cameraEye = new Point3D(0.9, 1.35, -2.3);
                cameraLookAt = new Point3D(0.0, 0.35, 0.3);
            }

            Camera camera = new Camera(cameraEye, cameraLookAt, Point3D.Y);
            scene.cameraActive(camera);

            ZBufferImpl zBuffer = new ZBufferImpl(width, height);
            zBuffer.scene(scene);
            zBuffer.camera(camera);
            zBuffer.draw();

            try {
                Image image = zBuffer.image();
                File file = new File(dir, poseNames[i]);
                image.saveFile(file);
                System.out.println("  [✔] Generated catalog image: " + file.getName());
            } catch (Exception e) {
                System.err.println("  [❌] Failed catalog pose: " + poseNames[i]);
                e.printStackTrace();
            }
        }
        System.out.println("Catalog export complete. 6 PNG images generated.");
    }

    public static void exportTransitionSequence(String poseFrom, String poseTo, double duration, int fps, String outputPrefix) {
        PoseState start = PoseLibrary.getPose(poseFrom);
        PoseState end = PoseLibrary.getPose(poseTo);
        int totalFrames = (int) Math.round(duration * fps);
        if (totalFrames < 1) totalFrames = 1;

        System.out.printf("Exporting transition sequence '%s' -> '%s' (%d frames)...%n", poseFrom, poseTo, totalFrames);

        for (int frame = 0; frame <= totalFrames; frame++) {
            double t = (double) frame / totalFrames;
            PoseState interpolated = start.interpolateSmooth(end, t);

            Scene scene = new Scene();
            HouseAndGardenGeometry.drawToScene(scene);

            PoseState worldPose = interpolated.transform(new Point3D(0.0, 0.0, 0.0), Math.PI);
            HumanBodyGeometry.drawToScene(scene, worldPose);

            Camera camera = new Camera(new Point3D(0.6, 1.3, -3.1), new Point3D(0.0, 0.95, 0.5), Point3D.Y);
            scene.cameraActive(camera);

            ZBufferImpl zBuffer = new ZBufferImpl(640, 800);
            zBuffer.scene(scene);
            zBuffer.camera(camera);
            zBuffer.draw();

            try {
                Image image = zBuffer.image();
                String filename = String.format("%s_%04d.png", outputPrefix, frame);
                File file = new File(filename);
                image.saveFile(file);
            } catch (Exception e) {
                System.err.printf("Error saving frame %d: %s%n", frame, e.getMessage());
            }
        }
        System.out.println("Transition sequence export complete.");
    }

    // =========================================================================
    // 11. MAIN ENTRY POINT
    // =========================================================================

    public static void main(String[] args) {
        boolean launchGui = true;
        boolean launchCli = false;
        boolean batchExport = false;



        for (String arg : args) {
            String lower = arg.toLowerCase();
            if (lower.equals("--gui") || lower.equals("--game") || lower.equals("-g")) {
                launchGui = true;
            } else if (lower.equals("--cli") || lower.equals("-c")) {
                launchCli = true;
            } else if (lower.equals("--batch") || lower.equals("--catalog") || lower.equals("-b")) {
                batchExport = true;
            }
        }

        if(launchGui) {
            NativeLookAndFeelSwing laf = new NativeLookAndFeelSwing();
        }


        // If no flags provided, default to batch catalog rendering (preserving backward compatibility)
        if (!launchGui && !launchCli && !batchExport) {
            batchExport = true;
        }

        if (batchExport) {
            exportCatalog(".", 800, 1000);
        }

        if (launchGui || launchCli) {
            CharacterController character = new CharacterController();
            PoseTransitionController animator = new PoseTransitionController();

            if (launchGui) {
                SwingUtilities.invokeLater(() -> {
                    InteractiveGameWindow window = new InteractiveGameWindow(character, animator, 640, 480);
                    window.setVisible(true);
                });
            }

            if (launchCli) {
                CommandLineController cli = new CommandLineController(character, animator);
                cli.start();
            }
        }
    }
}
