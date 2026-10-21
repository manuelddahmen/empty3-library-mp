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

import java.io.File;

public class CommandLineControllerTest {

    @Test
    public void testCliCommands() {
        InteractiveHumanRenderer2.CharacterController character = new InteractiveHumanRenderer2.CharacterController();
        InteractiveHumanRenderer2.PoseTransitionController animator = new InteractiveHumanRenderer2.PoseTransitionController();
        InteractiveHumanRenderer2.CommandLineController cli = new InteractiveHumanRenderer2.CommandLineController(character, animator);

        // Pose command
        cli.executeCommand("pose sit");
        Assertions.assertEquals("sit", animator.getCurrentPoseName());

        // Transition command
        cli.executeCommand("transition stand 1.5");
        Assertions.assertTrue(animator.isTransitioning());
        Assertions.assertEquals("stand", animator.getTargetPoseName());

        // Move command (relative dx dz)
        cli.executeCommand("move 2.0 3.0");
        Point3D p = character.getPosition();
        Assertions.assertEquals(2.0, p.getX(), 1e-4);
        Assertions.assertEquals(3.0, p.getZ(), 1e-4);

        // Move single distance
        cli.executeCommand("move 1.0");
        Point3D p2 = character.getPosition();
        Assertions.assertEquals(2.0, p2.getX(), 1e-4);
        Assertions.assertEquals(4.0, p2.getZ(), 1e-4);

        // Pos command (absolute)
        cli.executeCommand("pos 10.0 5.0 20.0");
        Point3D p3 = character.getPosition();
        Assertions.assertEquals(10.0, p3.getX(), 1e-4);
        Assertions.assertEquals(5.0, p3.getY(), 1e-4);
        Assertions.assertEquals(20.0, p3.getZ(), 1e-4);

        // Turn and Yaw
        cli.executeCommand("yaw 45.0");
        Assertions.assertEquals(45.0, character.getYawDegrees(), 1e-4);

        cli.executeCommand("turn 45.0");
        Assertions.assertEquals(90.0, character.getYawDegrees(), 1e-4);

        // Informational commands
        cli.executeCommand("status");
        cli.executeCommand("help");
        cli.executeCommand("invalid_command_test");
    }

    @Test
    public void testSnapshotExport() {
        InteractiveHumanRenderer2.CharacterController character = new InteractiveHumanRenderer2.CharacterController();
        InteractiveHumanRenderer2.PoseTransitionController animator = new InteractiveHumanRenderer2.PoseTransitionController();
        InteractiveHumanRenderer2.CommandLineController cli = new InteractiveHumanRenderer2.CommandLineController(character, animator);

        File tempOut = new File("test_cli_snapshot.png");
        try {
            cli.executeCommand("snapshot " + tempOut.getAbsolutePath());
            Assertions.assertTrue(tempOut.exists(), "Snapshot file should be created");
            Assertions.assertTrue(tempOut.length() > 0, "Snapshot file should not be empty");
        } finally {
            if (tempOut.exists()) {
                tempOut.delete();
            }
        }
    }
}
