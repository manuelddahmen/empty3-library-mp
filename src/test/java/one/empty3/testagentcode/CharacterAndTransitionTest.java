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

import one.empty3.library.Point3D;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicBoolean;

public class CharacterAndTransitionTest {

    @Test
    public void testCharacterMovementAndHeading() {
        InteractiveHumanRenderer2.CharacterController cc = new InteractiveHumanRenderer2.CharacterController();
        Assertions.assertEquals(0.0, cc.getPosition().getX(), 1e-6);
        Assertions.assertEquals(0.0, cc.getPosition().getZ(), 1e-6);

        // Facing yaw = 0 (+Z forward)
        cc.moveForward(2.0);
        Assertions.assertEquals(0.0, cc.getPosition().getX(), 1e-6);
        Assertions.assertEquals(2.0, cc.getPosition().getZ(), 1e-6);

        // Turn 90 degrees right (+X forward)
        cc.turnDegrees(90.0);
        Assertions.assertEquals(90.0, cc.getYawDegrees(), 1e-4);
        cc.moveForward(3.0);
        Assertions.assertEquals(3.0, cc.getPosition().getX(), 1e-4);
        Assertions.assertEquals(2.0, cc.getPosition().getZ(), 1e-4);

        // Strafe
        cc.strafe(1.0); // Facing 90 deg, right is -Z
        Assertions.assertEquals(3.0, cc.getPosition().getX(), 1e-4);
        Assertions.assertEquals(1.0, cc.getPosition().getZ(), 1e-4);
    }

    @Test
    public void testWorldPoseTransformation() {
        InteractiveHumanRenderer2.CharacterController cc = new InteractiveHumanRenderer2.CharacterController(
                new Point3D(5.0, 1.0, 10.0),
                Math.toRadians(90.0) // 90 deg rotation around Y
        );

        InteractiveHumanRenderer2.PoseState localStand = InteractiveHumanRenderer2.PoseLibrary.stand();
        InteractiveHumanRenderer2.PoseState worldPose = cc.getWorldPose(localStand);

        Point3D localHead = localStand.get(InteractiveHumanRenderer2.JointType.HEAD);
        Point3D worldHead = worldPose.get(InteractiveHumanRenderer2.JointType.HEAD);

        // localHead is at (0, 1.62, 0).
        // Rotated by 90 deg: (0, 1.62, 0). Translated by (5, 1, 10) -> (5.0, 2.62, 10.0)
        Assertions.assertEquals(5.0, worldHead.getX(), 1e-4);
        Assertions.assertEquals(localHead.getY() + 1.0, worldHead.getY(), 1e-4);
        Assertions.assertEquals(10.0, worldHead.getZ(), 1e-4);

        // Nose was at (0, 1.52, 0.10) in local.
        // Rotated 90 deg around Y: (0.10, 1.52, 0). Translated: (5.10, 2.52, 10.0)
        Point3D worldNose = worldPose.get(InteractiveHumanRenderer2.JointType.NOSE);
        Assertions.assertEquals(5.10, worldNose.getX(), 1e-4);
        Assertions.assertEquals(1.52 + 1.0, worldNose.getY(), 1e-4);
        Assertions.assertEquals(10.0, worldNose.getZ(), 1e-4);
    }

    @Test
    public void testPoseTransitionControllerProgress() {
        InteractiveHumanRenderer2.PoseTransitionController controller = new InteractiveHumanRenderer2.PoseTransitionController();
        controller.setPose("stand");
        Assertions.assertEquals("stand", controller.getCurrentPoseName());
        Assertions.assertFalse(controller.isTransitioning());

        AtomicBoolean completed = new AtomicBoolean(false);
        controller.transitionTo("sit", 2.0, () -> completed.set(true));

        Assertions.assertTrue(controller.isTransitioning());
        Assertions.assertEquals("sit", controller.getTargetPoseName());
        Assertions.assertEquals(0.0, controller.getTransitionProgress(), 1e-4);

        // Advance 1 second (halfway)
        controller.update(1.0);
        Assertions.assertTrue(controller.isTransitioning());
        Assertions.assertEquals(0.5, controller.getTransitionProgress(), 1e-4);
        Assertions.assertFalse(completed.get());

        // Advance 1 more second (completed)
        controller.update(1.0);
        Assertions.assertFalse(controller.isTransitioning());
        Assertions.assertEquals(1.0, controller.getTransitionProgress(), 1e-4);
        Assertions.assertEquals("sit", controller.getCurrentPoseName());
        Assertions.assertTrue(completed.get());

        // Verify joint coordinate matches sit pose
        InteractiveHumanRenderer2.PoseState current = controller.getCurrentPose();
        InteractiveHumanRenderer2.PoseState expectedSit = InteractiveHumanRenderer2.PoseLibrary.sit();
        Assertions.assertEquals(
                expectedSit.get(InteractiveHumanRenderer2.JointType.HEAD).getY(),
                current.get(InteractiveHumanRenderer2.JointType.HEAD).getY(),
                1e-4
        );
    }

    @Test
    public void testLocomotionCycleAnimation() {
        InteractiveHumanRenderer2.PoseTransitionController controller = new InteractiveHumanRenderer2.PoseTransitionController();
        controller.setPose("walk");

        InteractiveHumanRenderer2.PoseState pose0 = controller.getCurrentPose();
        Point3D kneeL0 = pose0.get(InteractiveHumanRenderer2.JointType.KNEE_L);

        controller.update(0.25);
        InteractiveHumanRenderer2.PoseState pose1 = controller.getCurrentPose();
        Point3D kneeL1 = pose1.get(InteractiveHumanRenderer2.JointType.KNEE_L);

        // Cyclic update should animate knees continuously
        Assertions.assertNotEquals(kneeL0.getZ(), kneeL1.getZ(), 1e-4);
    }

    @Test
    public void testArrowKeyTurnLeftAndRightDirection() {
        InteractiveHumanRenderer2.CharacterController cc = new InteractiveHumanRenderer2.CharacterController();
        Assertions.assertEquals(0.0, cc.getYaw(), 1e-6);

        // Turn Left decreases yaw (counter-clockwise -> facing -X / left)
        cc.turnLeft(Math.PI / 2.0);
        Assertions.assertEquals(-Math.PI / 2.0, cc.getYaw(), 1e-4);

        InteractiveHumanRenderer2.PoseState standPose = InteractiveHumanRenderer2.PoseLibrary.stand();
        InteractiveHumanRenderer2.PoseState worldLeft = cc.getWorldPose(standPose);
        Point3D noseLeft = worldLeft.get(InteractiveHumanRenderer2.JointType.NOSE);
        // Nose facing left (-X)
        Assertions.assertTrue(noseLeft.getX() < -0.05, "Nose should face -X when turned left");

        // Turn Right from -PI/2 back to 0
        cc.turnRight(Math.PI / 2.0);
        Assertions.assertEquals(0.0, cc.getYaw(), 1e-4);

        // Turn Right to +PI/2 (clockwise -> facing +X / right)
        cc.turnRight(Math.PI / 2.0);
        Assertions.assertEquals(Math.PI / 2.0, cc.getYaw(), 1e-4);

        InteractiveHumanRenderer2.PoseState worldRight = cc.getWorldPose(standPose);
        Point3D noseRight = worldRight.get(InteractiveHumanRenderer2.JointType.NOSE);
        // Nose facing right (+X)
        Assertions.assertTrue(noseRight.getX() > 0.05, "Nose should face +X when turned right");
    }

    @Test
    public void testArrowKeyMoveForwardAndBackward() {
        InteractiveHumanRenderer2.CharacterController cc = new InteractiveHumanRenderer2.CharacterController();

        // Facing yaw = 0 (+Z)
        cc.moveForward(2.0);
        Assertions.assertEquals(0.0, cc.getPosition().getX(), 1e-6);
        Assertions.assertEquals(2.0, cc.getPosition().getZ(), 1e-6);

        cc.moveBackward(1.0);
        Assertions.assertEquals(0.0, cc.getPosition().getX(), 1e-6);
        Assertions.assertEquals(1.0, cc.getPosition().getZ(), 1e-6);

        // Turn 90 degrees right (+X facing)
        cc.turnRight(Math.PI / 2.0);
        cc.moveForward(2.0);
        Assertions.assertEquals(2.0, cc.getPosition().getX(), 1e-4);
        Assertions.assertEquals(1.0, cc.getPosition().getZ(), 1e-4);

        cc.moveBackward(1.0);
        Assertions.assertEquals(1.0, cc.getPosition().getX(), 1e-4);
        Assertions.assertEquals(1.0, cc.getPosition().getZ(), 1e-4);
    }

    @Test
    public void testCombinedTurningAndForwardLocomotionTrajectory() {
        InteractiveHumanRenderer2.CharacterController cc = new InteractiveHumanRenderer2.CharacterController();
        Point3D start = cc.getPosition();

        int steps = 120;
        double angleStep = (2.0 * Math.PI) / steps;
        double stepDistance = 0.1;

        for (int i = 0; i < steps; i++) {
            cc.moveForward(stepDistance);
            cc.turnLeft(angleStep);

            Point3D pos = cc.getPosition();
            Assertions.assertFalse(Double.isNaN(pos.getX()));
            Assertions.assertFalse(Double.isNaN(pos.getY()));
            Assertions.assertFalse(Double.isNaN(pos.getZ()));
        }

        // Circular trajectory should return close to starting point
        Point3D end = cc.getPosition();
        Assertions.assertEquals(start.getX(), end.getX(), 0.15);
        Assertions.assertEquals(start.getZ(), end.getZ(), 0.15);
    }

    @Test
    public void testAlternatingTurnsStability() {
        InteractiveHumanRenderer2.CharacterController cc = new InteractiveHumanRenderer2.CharacterController();
        for (int i = 0; i < 50; i++) {
            cc.turnLeft(0.2);
            cc.turnRight(0.2);
        }
        Assertions.assertEquals(0.0, cc.getYaw(), 1e-4);
    }
}
