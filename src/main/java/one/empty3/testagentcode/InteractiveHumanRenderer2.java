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
 * Interactive 3D Human Animation & Rendering System.
 * Supports modular anatomical joint tracking, smooth pose transitions with easing,
 * 3D character displacement & yaw orientation, real-time GUI game controller,
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
    // 3. CENTRALIZED POSE LIBRARY
    // =========================================================================

    public static class PoseLibrary {

        public static PoseState stand() {
            PoseState pose = new PoseState();
            double yHead = 1.62;
            double yJaw = 1.54;
            double yNose = 1.52;
            double yNeck = 1.44;
            double yShoulders = 1.36;
            double yElbows = 1.08;
            double yWrists = 0.88;
            double yHips = 0.84;
            double yKnees = 0.48;
            double yAnkles = 0.12;

            pose.set(JointType.HEAD, new Point3D(0.0, yHead, 0.0));
            pose.set(JointType.JAW, new Point3D(0.0, yJaw, 0.03));
            pose.set(JointType.NOSE, new Point3D(0.0, yNose, 0.10));
            pose.set(JointType.NECK, new Point3D(0.0, yNeck, 0.0));

            pose.set(JointType.SHOULDER_L, new Point3D(-0.18, yShoulders, 0.0));
            pose.set(JointType.SHOULDER_R, new Point3D(0.18, yShoulders, 0.0));
            pose.set(JointType.ELBOW_L, new Point3D(-0.25, yElbows, -0.02));
            pose.set(JointType.ELBOW_R, new Point3D(0.25, yElbows, -0.02));
            pose.set(JointType.WRIST_L, new Point3D(-0.28, yWrists, 0.02));
            pose.set(JointType.WRIST_R, new Point3D(0.28, yWrists, 0.02));
            pose.set(JointType.HAND_L, new Point3D(-0.30, yWrists - 0.10, 0.04));
            pose.set(JointType.HAND_R, new Point3D(0.30, yWrists - 0.10, 0.04));

            pose.set(JointType.HIP_L, new Point3D(-0.09, yHips, 0.0));
            pose.set(JointType.HIP_R, new Point3D(0.09, yHips, 0.0));
            pose.set(JointType.KNEE_L, new Point3D(-0.09, yKnees, 0.0));
            pose.set(JointType.KNEE_R, new Point3D(0.09, yKnees, 0.0));
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
            pose.set(JointType.ELBOW_L, new Point3D(-0.22, 1.55, 0.0));
            pose.set(JointType.ELBOW_R, new Point3D(0.22, 1.55, 0.0));
            pose.set(JointType.WRIST_L, new Point3D(-0.25, 1.82, 0.05));
            pose.set(JointType.WRIST_R, new Point3D(0.25, 1.82, 0.05));
            pose.set(JointType.HAND_L, new Point3D(-0.27, 1.96, 0.08));
            pose.set(JointType.HAND_R, new Point3D(0.27, 1.96, 0.08));
            return pose;
        }

        public static PoseState sit() {
            PoseState pose = new PoseState();
            double ySeat = 0.45;
            double thighLength = 0.36;
            double dy = 0.84 - ySeat;

            pose.set(JointType.HEAD, new Point3D(0.0, 1.62 - dy, 0.0));
            pose.set(JointType.JAW, new Point3D(0.0, 1.54 - dy, 0.03));
            pose.set(JointType.NOSE, new Point3D(0.0, 1.52 - dy, 0.10));
            pose.set(JointType.NECK, new Point3D(0.0, 1.44 - dy, 0.0));

            pose.set(JointType.SHOULDER_L, new Point3D(-0.18, 1.36 - dy, 0.0));
            pose.set(JointType.SHOULDER_R, new Point3D(0.18, 1.36 - dy, 0.0));

            pose.set(JointType.ELBOW_L, new Point3D(-0.24, 1.08 - dy + 0.05, 0.15));
            pose.set(JointType.ELBOW_R, new Point3D(0.24, 1.08 - dy + 0.05, 0.15));
            pose.set(JointType.WRIST_L, new Point3D(-0.21, 0.52, 0.28));
            pose.set(JointType.WRIST_R, new Point3D(0.21, 0.52, 0.28));
            pose.set(JointType.HAND_L, new Point3D(-0.19, 0.49, 0.35));
            pose.set(JointType.HAND_R, new Point3D(0.19, 0.49, 0.35));

            pose.set(JointType.HIP_L, new Point3D(-0.09, ySeat, 0.0));
            pose.set(JointType.HIP_R, new Point3D(0.09, ySeat, 0.0));
            pose.set(JointType.KNEE_L, new Point3D(-0.09, ySeat, thighLength));
            pose.set(JointType.KNEE_R, new Point3D(0.09, ySeat, thighLength));
            pose.set(JointType.ANKLE_L, new Point3D(-0.09, 0.12, thighLength));
            pose.set(JointType.ANKLE_R, new Point3D(0.09, 0.12, thighLength));

            pose.set(JointType.FOOT_L_START, new Point3D(-0.09, 0.06, thighLength - 0.05));
            pose.set(JointType.FOOT_L_END, new Point3D(-0.09, 0.04, thighLength + 0.15));
            pose.set(JointType.FOOT_R_START, new Point3D(0.09, 0.06, thighLength - 0.05));
            pose.set(JointType.FOOT_R_END, new Point3D(0.09, 0.04, thighLength + 0.15));

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

        public static PoseState walk(double t) {
            PoseState pose = stand();
            double swing = 0.24 * Math.sin(t);
            double swingOpp = 0.24 * Math.sin(t + Math.PI);

            pose.set(JointType.KNEE_L, new Point3D(-0.09, 0.48, swing));
            double plieLeft = swing < 0 ? -0.08 * swing : 0.0;
            pose.set(JointType.ANKLE_L, new Point3D(-0.09, 0.12 + plieLeft, swing - 0.03));
            pose.set(JointType.FOOT_L_START, new Point3D(-0.09, 0.06 + plieLeft, swing - 0.08));
            pose.set(JointType.FOOT_L_END, new Point3D(-0.09, 0.04 + plieLeft, swing + 0.12));

            pose.set(JointType.KNEE_R, new Point3D(0.09, 0.48, swingOpp));
            double plieRight = swingOpp < 0 ? -0.08 * swingOpp : 0.0;
            pose.set(JointType.ANKLE_R, new Point3D(0.09, 0.12 + plieRight, swingOpp - 0.03));
            pose.set(JointType.FOOT_R_START, new Point3D(0.09, 0.06 + plieRight, swingOpp - 0.08));
            pose.set(JointType.FOOT_R_END, new Point3D(0.09, 0.04 + plieRight, swingOpp + 0.12));

            pose.set(JointType.ELBOW_L, new Point3D(-0.25, 1.08, swingOpp * 1.2));
            pose.set(JointType.WRIST_L, new Point3D(-0.27, 0.88, swingOpp * 1.5));
            pose.set(JointType.HAND_L, new Point3D(-0.29, 0.78, swingOpp * 1.6));

            pose.set(JointType.ELBOW_R, new Point3D(0.25, 1.08, swing * 1.2));
            pose.set(JointType.WRIST_R, new Point3D(0.28, 0.88, swing * 1.5));
            pose.set(JointType.HAND_R, new Point3D(0.29, 0.78, swing * 1.6));

            return pose;
        }

        public static PoseState run(double t) {
            PoseState stand = stand();
            PoseState pose = new PoseState();

            double bounce = 0.07 * Math.abs(Math.sin(t));
            double lean = 0.08;

            pose.set(JointType.HEAD, stand.get(JointType.HEAD).plus(new Point3D(0.0, bounce, lean + 0.04)));
            pose.set(JointType.JAW, stand.get(JointType.JAW).plus(new Point3D(0.0, bounce, lean + 0.06)));
            pose.set(JointType.NOSE, stand.get(JointType.NOSE).plus(new Point3D(0.0, bounce, lean + 0.08)));
            pose.set(JointType.NECK, stand.get(JointType.NECK).plus(new Point3D(0.0, bounce, lean + 0.03)));

            pose.set(JointType.SHOULDER_L, stand.get(JointType.SHOULDER_L).plus(new Point3D(0.0, bounce, lean)));
            pose.set(JointType.SHOULDER_R, stand.get(JointType.SHOULDER_R).plus(new Point3D(0.0, bounce, lean)));

            double swing = 0.42 * Math.sin(t);
            double swingOpp = 0.42 * Math.sin(t + Math.PI);

            pose.set(JointType.HIP_L, stand.get(JointType.HIP_L).plus(new Point3D(0.0, bounce, 0.0)));
            pose.set(JointType.HIP_R, stand.get(JointType.HIP_R).plus(new Point3D(0.0, bounce, 0.0)));

            double bendL = swing < 0 ? 0.14 * -swing : 0.01;
            pose.set(JointType.KNEE_L, new Point3D(-0.09, 0.48 + bounce, swing + lean));
            pose.set(JointType.ANKLE_L, new Point3D(-0.09, 0.12 + bounce + bendL, swing - bendL * 0.5));
            pose.set(JointType.FOOT_L_START, new Point3D(-0.09, 0.06 + bounce + bendL, swing - bendL * 0.5 - 0.05));
            pose.set(JointType.FOOT_L_END, new Point3D(-0.09, 0.04 + bounce + bendL, swing - bendL * 0.5 + 0.15));

            double bendR = swingOpp < 0 ? 0.14 * -swingOpp : 0.01;
            pose.set(JointType.KNEE_R, new Point3D(0.09, 0.48 + bounce, swingOpp + lean));
            pose.set(JointType.ANKLE_R, new Point3D(0.09, 0.12 + bounce + bendR, swingOpp - bendR * 0.5));
            pose.set(JointType.FOOT_R_START, new Point3D(0.09, 0.06 + bounce + bendR, swingOpp - bendR * 0.5 - 0.05));
            pose.set(JointType.FOOT_R_END, new Point3D(0.09, 0.04 + bounce + bendR, swingOpp - bendR * 0.5 + 0.15));

            pose.set(JointType.ELBOW_L, new Point3D(-0.25, 1.15 + bounce, swingOpp * 0.8 + lean));
            pose.set(JointType.WRIST_L, new Point3D(-0.24, 1.25 + bounce, swingOpp * 0.8 + lean + 0.18));
            pose.set(JointType.HAND_L, new Point3D(-0.22, 1.24 + bounce, swingOpp * 0.8 + lean + 0.24));

            pose.set(JointType.ELBOW_R, new Point3D(0.25, 1.15 + bounce, swing * 0.8 + lean));
            pose.set(JointType.WRIST_R, new Point3D(0.24, 1.25 + bounce, swing * 0.8 + lean + 0.18));
            pose.set(JointType.HAND_R, new Point3D(0.22, 1.24 + bounce, swing * 0.8 + lean + 0.24));

            return pose;
        }

        public static PoseState getPose(String name) {
            return getPose(name, 0.0);
        }

        public static PoseState getPose(String name, double cycleTime) {
            if (name == null) return stand();
            String clean = name.trim().toLowerCase().replaceAll("[_\\-\\s]", "");
            switch (clean) {
                case "stand":
                case "debout":
                case "1":
                    return stand();
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
                    return stand();
            }
        }

        public static boolean isCyclic(String name) {
            if (name == null) return false;
            String clean = name.trim().toLowerCase().replaceAll("[_\\-\\s]", "");
            return clean.equals("walk") || clean.equals("marche") || clean.equals("5")
                    || clean.equals("run") || clean.equals("course") || clean.equals("6");
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
        private double cycleSpeed = 4.0; // Radians per second for walk/run cycles

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
    }

    // =========================================================================
    // 5. CHARACTER CONTROLLER (DISPLACEMENT & ORIENTATION)
    // =========================================================================

    public static class CharacterController {
        private Point3D position = new Point3D(0.0, 0.0, 0.0);
        private double yaw = 0.0; // Heading angle in radians
        private double moveSpeed = 1.8; // Units per second
        private double turnSpeed = Math.PI; // Radians per second

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
            }
        }

        public synchronized double getYaw() {
            return yaw;
        }

        public synchronized void setYaw(double yaw) {
            this.yaw = yaw;
        }

        public synchronized double getYawDegrees() {
            return Math.toDegrees(yaw);
        }

        public synchronized void setYawDegrees(double deg) {
            this.yaw = Math.toRadians(deg);
        }

        public synchronized void moveForward(double distance) {
            double dx = distance * Math.sin(yaw);
            double dz = distance * Math.cos(yaw);
            position = position.plus(new Point3D(dx, 0.0, dz));
        }

        public synchronized void moveBackward(double distance) {
            moveForward(-distance);
        }

        public synchronized void strafe(double distance) {
            double dx = distance * Math.cos(yaw);
            double dz = -distance * Math.sin(yaw);
            position = position.plus(new Point3D(dx, 0.0, dz));
        }

        public synchronized void turn(double angleDeltaRadians) {
            this.yaw += angleDeltaRadians;
            if (this.yaw > Math.PI * 2.0) this.yaw -= Math.PI * 2.0;
            if (this.yaw < -Math.PI * 2.0) this.yaw += Math.PI * 2.0;
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
    }

    // =========================================================================
    // 6. GEOMETRY BUILDER & SCENE RENDERER
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
            sphere.texture(tex);
            s.add(sphere);
        }

        private static void addCylinder(Scene s, Point3D start, Point3D end, double radius, ColorTexture tex) {
            Cylinder cylinder = new Cylinder(start, end, radius);
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

        public InteractiveGameWindow(CharacterController character, PoseTransitionController animator, int width, int height) {
            super("Empty3 - Interactive Human 3D Controller");
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
                    animator.transitionTo("stand", 0.6);
                    break;
                case KeyEvent.VK_2:
                    animator.transitionTo("hands_up", 0.6);
                    break;
                case KeyEvent.VK_3:
                    animator.transitionTo("sit", 0.8);
                    break;
                case KeyEvent.VK_4:
                    animator.transitionTo("lie_down", 1.0);
                    break;
                case KeyEvent.VK_5:
                    animator.transitionTo("walk", 0.5);
                    break;
                case KeyEvent.VK_6:
                    animator.transitionTo("run", 0.5);
                    break;
                case KeyEvent.VK_C:
                    animator.transitionTo("crouch", 0.6);
                    break;
                case KeyEvent.VK_Z:
                    animator.transitionTo("lie_down", 1.0);
                    break;
                case KeyEvent.VK_SPACE:
                    animator.transitionTo("stand", 0.5);
                    break;
                case KeyEvent.VK_R:
                    character.setPosition(new Point3D(0.0, 0.0, 0.0));
                    character.setYaw(0.0);
                    break;
            }
        }

        private void updatePhysics(double deltaSeconds) {
            double speedMultiplier = activeKeys.contains(KeyEvent.VK_SHIFT) ? 2.2 : 1.0;
            double moveDist = character.getMoveSpeed() * speedMultiplier * deltaSeconds;
            double turnAngle = character.getTurnSpeed() * deltaSeconds;

            boolean moving = false;
            boolean moveForward = activeKeys.contains(KeyEvent.VK_W) || activeKeys.contains(KeyEvent.VK_UP);
            boolean moveBackward = activeKeys.contains(KeyEvent.VK_S) || activeKeys.contains(KeyEvent.VK_DOWN);
            boolean turnLeft = activeKeys.contains(KeyEvent.VK_A) || activeKeys.contains(KeyEvent.VK_LEFT);
            boolean turnRight = activeKeys.contains(KeyEvent.VK_D) || activeKeys.contains(KeyEvent.VK_RIGHT);

            if (moveForward && !moveBackward) {
                character.moveForward(moveDist);
                moving = true;
            } else if (moveBackward && !moveForward) {
                character.moveBackward(moveDist * 0.7);
                moving = true;
            }

            if (turnLeft && !turnRight) {
                character.turnLeft(turnAngle);
            } else if (turnRight && !turnLeft) {
                character.turnRight(turnAngle);
            }

            if (activeKeys.contains(KeyEvent.VK_Q)) {
                character.strafe(-moveDist * 0.8);
                moving = true;
            }
            if (activeKeys.contains(KeyEvent.VK_E)) {
                character.strafe(moveDist * 0.8);
                moving = true;
            }

            // Auto-transition to locomotion when moving
            if (moving && !animator.isTransitioning()) {
                String cur = animator.getCurrentPoseName();
                if (activeKeys.contains(KeyEvent.VK_SHIFT)) {
                    if (!cur.equals("run")) {
                        animator.transitionTo("run", 0.3);
                    }
                } else {
                    if (!cur.equals("walk")) {
                        animator.transitionTo("walk", 0.3);
                    }
                }
            } else if (!moving && !animator.isTransitioning()) {
                String cur = animator.getCurrentPoseName();
                if (cur.equals("walk") || cur.equals("run")) {
                    animator.transitionTo("stand", 0.3);
                }
            }

            animator.update(deltaSeconds);
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

                    // Build 3D Scene
                    Scene scene = new Scene();

                    // Ground plane
                    Sphere ground = new Sphere(new Point3D(0.0, -100.0, 0.0), 100.0);
                    ground.texture(new ColorTexture(Color.newCol(0.25f, 0.50f, 0.22f)));
                    scene.add(ground);

                    // Character geometry in world coordinates
                    PoseState worldPose = character.getWorldPose(animator.getCurrentPose());
                    HumanBodyGeometry.drawToScene(scene, worldPose);

                    // 3rd Person Chase Camera
                    Point3D charPos = character.getPosition();
                    double yaw = character.getYaw();

                    // Camera position placed behind character relative to facing
                    double camDist = 3.2;
                    double camHeight = 1.3;
                    Point3D camEye = charPos.plus(new Point3D(
                            -camDist * Math.sin(yaw) + 0.4 * Math.cos(yaw),
                            camHeight,
                            -camDist * Math.cos(yaw) - 0.4 * Math.sin(yaw)
                    ));
                    Point3D camTarget = charPos.plus(new Point3D(0.0, 0.9, 0.0));

                    Camera camera = new Camera(camEye, camTarget, Point3D.Y);
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
                g2.setColor(new java.awt.Color(0, 0, 0, 160));
                g2.fillRoundRect(10, 10, 480, 155, 10, 10);

                g2.setColor(java.awt.Color.GREEN);
                g2.setFont(new Font("Monospaced", Font.BOLD, 12));
                Point3D pos = character.getPosition();
                g2.drawString(String.format("FPS: %.1f", fps), 20, 30);
                g2.drawString(String.format("POS: (%.2f, %.2f, %.2f)", pos.getX(), pos.getY(), pos.getZ()), 20, 50);
                g2.drawString(String.format("YAW: %.1f deg", character.getYawDegrees()), 20, 70);
                g2.drawString(String.format("POSE: %s", animator.getCurrentPoseName()), 20, 90);
                if (animator.isTransitioning()) {
                    g2.setColor(java.awt.Color.YELLOW);
                    g2.drawString(String.format("TRANSITION: -> %s (%.0f%%)",
                            animator.getTargetPoseName(), animator.getTransitionProgress() * 100.0), 20, 110);
                } else {
                    g2.drawString("TRANSITION: Idle", 20, 110);
                }

                g2.setColor(java.awt.Color.WHITE);
                g2.drawString("CONTROLS: Arrows / WASD: Move & Turn | Shift: Run", 20, 130);
                g2.drawString("HOTKEYS: 1-6: Poses | C: Crouch | Z: Lie | R: Reset", 20, 148);
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
            System.out.println("  Empty3 Interactive Human Renderer - CLI Terminal");
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
                            animator.setPose(parts[1]);
                            System.out.println("Pose set to: " + parts[1]);
                        }
                        break;
                    case "transition":
                        if (parts.length < 2) {
                            System.out.println("Usage: transition <targetPose> [durationInSeconds]");
                        } else {
                            String target = parts[1];
                            double duration = parts.length >= 3 ? Double.parseDouble(parts[2]) : 1.0;
                            animator.transitionTo(target, duration);
                            System.out.printf("Transitioning to '%s' over %.2f seconds...%n", target, duration);
                        }
                        break;
                    case "move":
                        if (parts.length == 2) {
                            double dist = Double.parseDouble(parts[1]);
                            character.moveForward(dist);
                            System.out.printf("Moved forward %.2f units. New pos: (%.2f, %.2f, %.2f)%n",
                                    dist, character.getPosition().getX(), character.getPosition().getY(), character.getPosition().getZ());
                        } else if (parts.length >= 3) {
                            double dx = Double.parseDouble(parts[1]);
                            double dz = Double.parseDouble(parts[2]);
                            character.setPosition(character.getPosition().plus(new Point3D(dx, 0.0, dz)));
                            System.out.printf("Displaced by (%.2f, 0.0, %.2f). New pos: (%.2f, %.2f, %.2f)%n",
                                    dx, dz, character.getPosition().getX(), character.getPosition().getY(), character.getPosition().getZ());
                        } else {
                            System.out.println("Usage: move <distance> OR move <dx> <dz>");
                        }
                        break;
                    case "turn":
                        if (parts.length < 2) {
                            System.out.println("Usage: turn <degrees>");
                        } else {
                            double deg = Double.parseDouble(parts[1]);
                            character.turnDegrees(deg);
                            System.out.printf("Turned by %.1f deg. New yaw: %.1f deg%n", deg, character.getYawDegrees());
                        }
                        break;
                    case "pos":
                        if (parts.length >= 4) {
                            double x = Double.parseDouble(parts[1]);
                            double y = Double.parseDouble(parts[2]);
                            double z = Double.parseDouble(parts[3]);
                            character.setPosition(new Point3D(x, y, z));
                            System.out.printf("Position set to (%.2f, %.2f, %.2f)%n", x, y, z);
                        } else {
                            System.out.println("Usage: pos <x> <y> <z>");
                        }
                        break;
                    case "yaw":
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
                        System.out.printf("Status: Pos=(%.2f, %.2f, %.2f) | Yaw=%.1f deg | Pose=%s | Transitioning=%b%n",
                                p.getX(), p.getY(), p.getZ(), character.getYawDegrees(),
                                animator.getCurrentPoseName(), animator.isTransitioning());
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
            System.out.println("  pose <name>                        - Switch immediately to pose (stand, sit, lie_down, etc.)");
            System.out.println("  transition <name> [duration]       - Smoothly interpolate to target pose");
            System.out.println("  move <distance> | move <dx> <dz>   - Translate character in world space");
            System.out.println("  turn <degrees>                     - Rotate character heading by delta degrees");
            System.out.println("  pos <x> <y> <z>                    - Set absolute world coordinates");
            System.out.println("  yaw <degrees>                      - Set absolute facing heading in degrees");
            System.out.println("  status                             - Display position, heading, and animation state");
            System.out.println("  snapshot [filename]                - Render and export current scene to PNG");
            System.out.println("  export [outputDir]                 - Batch render standard pose catalog");
            System.out.println("  sequence <p1> <p2> <dur> <fps> <pre>- Batch render multi-frame transition sequence");
            System.out.println("  help                               - Show this help summary");
            System.out.println("  exit / quit                        - Exit application");
            System.out.println();
            System.out.println("GUI Controls (Game Window):");
            System.out.println("  Arrows / WASD : Move forward/backward & turn left/right (Q/E to strafe)");
            System.out.println("  Shift + Move  : Sprint (run pose)");
            System.out.println("  1-6, C, Z, R  : Poses (Stand, HandsUp, Sit, LieDown, Walk, Run, Crouch) & Reset");
        }

        /**
         * Renders character scene to image file with camera
         */
        private void takeSnapshot(String filename, int width, int height) {
            Scene scene = new Scene();
            Sphere ground = new Sphere(new Point3D(0.0, -100.0, 0.0), 100.0);
            ground.texture(new ColorTexture(Color.newCol(0.25f, 0.50f, 0.22f)));
            scene.add(ground);

            PoseState worldPose = character.getWorldPose(animator.getCurrentPose());
            HumanBodyGeometry.drawToScene(scene, worldPose);

            Point3D charPos = character.getPosition();
            double yaw = character.getYaw();
            Point3D camEye = charPos.plus(new Point3D(
                    -3.2 * Math.sin(yaw) + 0.5 * Math.cos(yaw),
                    1.2,
                    -3.2 * Math.cos(yaw) - 0.5 * Math.sin(yaw)
            ));
            Point3D camLookAt = charPos.plus(new Point3D(0.0, 0.9, 0.0));

            Camera camera = new Camera(camEye, camLookAt, Point3D.Y);
            scene.cameraActive(camera);

            ZBufferImpl zBuffer = new ZBufferImpl(width, height);
            zBuffer.scene(scene);
            // Persists rendered scene buffer to local file system
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

        System.out.println("Starting anatomical pose catalog export...");

        for (int i = 0; i < poseNames.length; i++) {
            Scene scene = new Scene();

            Sphere ground = new Sphere(new Point3D(0.0, -100.0, 0.0), 100.0);
            ground.texture(new ColorTexture(Color.newCol(0.25f, 0.50f, 0.22f)));
            scene.add(ground);

            PoseState pose;
            switch (i) {
                case 0: pose = PoseLibrary.stand(); break;
                case 1: pose = PoseLibrary.handsUp(); break;
                case 2: pose = PoseLibrary.sit(); break;
                case 3: pose = PoseLibrary.lieDown(); break;
                case 4: pose = PoseLibrary.walk(Math.PI / 4.0); break;
                case 5: pose = PoseLibrary.run(Math.PI / 3.0); break;
                default: pose = PoseLibrary.stand();
            }

            HumanBodyGeometry.drawToScene(scene, pose);

            Point3D cameraEye = new Point3D(0.5, 1.0, 3.2);
            Point3D cameraLookAt = new Point3D(0.0, 0.9, 0.0);

            if (i == 2) {
                cameraEye = new Point3D(0.7, 0.8, 2.8);
                cameraLookAt = new Point3D(0.0, 0.6, 0.1);
            } else if (i == 3) {
                cameraEye = new Point3D(0.8, 1.2, 2.2);
                cameraLookAt = new Point3D(0.0, 0.2, 0.0);
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
            Sphere ground = new Sphere(new Point3D(0.0, -100.0, 0.0), 100.0);
            ground.texture(new ColorTexture(Color.newCol(0.25f, 0.50f, 0.22f)));
            scene.add(ground);

            HumanBodyGeometry.drawToScene(scene, interpolated);

            Camera camera = new Camera(new Point3D(0.5, 1.0, 3.2), new Point3D(0.0, 0.9, 0.0), Point3D.Y);
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
