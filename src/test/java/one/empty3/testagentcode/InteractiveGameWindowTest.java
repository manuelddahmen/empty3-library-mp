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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InteractiveGameWindowTest {

    @Test
    public void testGameControllerComponents() {
        InteractiveHumanRenderer2.CharacterController character = new InteractiveHumanRenderer2.CharacterController();
        InteractiveHumanRenderer2.PoseTransitionController animator = new InteractiveHumanRenderer2.PoseTransitionController();

        Assertions.assertNotNull(character);
        Assertions.assertNotNull(animator);

        // Verify camera framing calculations
        character.moveForward(5.0);
        character.turnDegrees(45.0);

        InteractiveHumanRenderer2.PoseState current = animator.getCurrentPose();
        InteractiveHumanRenderer2.PoseState worldPose = character.getWorldPose(current);

        Assertions.assertNotNull(worldPose);
        Assertions.assertEquals(22, worldPose.getJoints().size());
    }
}
