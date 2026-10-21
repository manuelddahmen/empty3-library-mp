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

public class PoseInterpolationTest {

    @Test
    public void testJointTypeCompleteness() {
        InteractiveHumanRenderer2.JointType[] joints = InteractiveHumanRenderer2.JointType.values();
        Assertions.assertEquals(22, joints.length);
        Assertions.assertNotNull(InteractiveHumanRenderer2.JointType.valueOf("HEAD"));
        Assertions.assertNotNull(InteractiveHumanRenderer2.JointType.valueOf("NOSE"));
        Assertions.assertNotNull(InteractiveHumanRenderer2.JointType.valueOf("FOOT_R_END"));
    }

    @Test
    public void testPoseStateDeepCopy() {
        InteractiveHumanRenderer2.PoseState stand = InteractiveHumanRenderer2.PoseLibrary.stand();
        InteractiveHumanRenderer2.PoseState copy = stand.copy();

        Point3D headOriginal = stand.get(InteractiveHumanRenderer2.JointType.HEAD);
        Point3D headCopy = copy.get(InteractiveHumanRenderer2.JointType.HEAD);
        Assertions.assertEquals(headOriginal.getX(), headCopy.getX(), 1e-6);
        Assertions.assertEquals(headOriginal.getY(), headCopy.getY(), 1e-6);
        Assertions.assertEquals(headOriginal.getZ(), headCopy.getZ(), 1e-6);

        // Mutating copy shouldn't affect original
        copy.set(InteractiveHumanRenderer2.JointType.HEAD, new Point3D(10.0, 10.0, 10.0));
        Assertions.assertEquals(headOriginal.getY(), stand.get(InteractiveHumanRenderer2.JointType.HEAD).getY(), 1e-6);
        Assertions.assertEquals(10.0, copy.get(InteractiveHumanRenderer2.JointType.HEAD).getY(), 1e-6);
    }

    @Test
    public void testPoseInterpolationLinear() {
        InteractiveHumanRenderer2.PoseState stand = InteractiveHumanRenderer2.PoseLibrary.stand();
        InteractiveHumanRenderer2.PoseState sit = InteractiveHumanRenderer2.PoseLibrary.sit();

        InteractiveHumanRenderer2.PoseState atZero = stand.interpolate(sit, 0.0);
        InteractiveHumanRenderer2.PoseState atOne = stand.interpolate(sit, 1.0);
        InteractiveHumanRenderer2.PoseState atHalf = stand.interpolate(sit, 0.5);

        Point3D standHead = stand.get(InteractiveHumanRenderer2.JointType.HEAD);
        Point3D sitHead = sit.get(InteractiveHumanRenderer2.JointType.HEAD);
        Point3D halfHead = atHalf.get(InteractiveHumanRenderer2.JointType.HEAD);

        Assertions.assertEquals(standHead.getY(), atZero.get(InteractiveHumanRenderer2.JointType.HEAD).getY(), 1e-6);
        Assertions.assertEquals(sitHead.getY(), atOne.get(InteractiveHumanRenderer2.JointType.HEAD).getY(), 1e-6);

        double expectedHalfY = (standHead.getY() + sitHead.getY()) * 0.5;
        Assertions.assertEquals(expectedHalfY, halfHead.getY(), 1e-6);
    }

    @Test
    public void testPoseInterpolationSmoothstep() {
        InteractiveHumanRenderer2.PoseState stand = InteractiveHumanRenderer2.PoseLibrary.stand();
        InteractiveHumanRenderer2.PoseState sit = InteractiveHumanRenderer2.PoseLibrary.sit();

        InteractiveHumanRenderer2.PoseState smoothHalf = stand.interpolateSmooth(sit, 0.5);
        InteractiveHumanRenderer2.PoseState linearHalf = stand.interpolate(sit, 0.5);

        // At t=0.5, smoothstep(0.5) = 0.5
        Assertions.assertEquals(
                linearHalf.get(InteractiveHumanRenderer2.JointType.HEAD).getY(),
                smoothHalf.get(InteractiveHumanRenderer2.JointType.HEAD).getY(),
                1e-6
        );

        // At t=0.25, smoothstep is slower than linear
        InteractiveHumanRenderer2.PoseState smoothQuarter = stand.interpolateSmooth(sit, 0.25);
        InteractiveHumanRenderer2.PoseState linearQuarter = stand.interpolate(sit, 0.25);

        double standY = stand.get(InteractiveHumanRenderer2.JointType.HEAD).getY();
        double sitY = sit.get(InteractiveHumanRenderer2.JointType.HEAD).getY();
        double deltaSmooth = Math.abs(smoothQuarter.get(InteractiveHumanRenderer2.JointType.HEAD).getY() - standY);
        double deltaLinear = Math.abs(linearQuarter.get(InteractiveHumanRenderer2.JointType.HEAD).getY() - standY);
        Assertions.assertTrue(deltaSmooth < deltaLinear, "Smoothstep should ease-in at t=0.25");
    }

    @Test
    public void testPoseLibraryStandardPoses() {
        Assertions.assertNotNull(InteractiveHumanRenderer2.PoseLibrary.stand());
        Assertions.assertNotNull(InteractiveHumanRenderer2.PoseLibrary.handsUp());
        Assertions.assertNotNull(InteractiveHumanRenderer2.PoseLibrary.sit());
        Assertions.assertNotNull(InteractiveHumanRenderer2.PoseLibrary.lieDown());
        Assertions.assertNotNull(InteractiveHumanRenderer2.PoseLibrary.crouch());
        Assertions.assertNotNull(InteractiveHumanRenderer2.PoseLibrary.walk(0.0));
        Assertions.assertNotNull(InteractiveHumanRenderer2.PoseLibrary.run(0.0));

        InteractiveHumanRenderer2.PoseState byNameStand = InteractiveHumanRenderer2.PoseLibrary.getPose("stand");
        Assertions.assertNotNull(byNameStand);
        Assertions.assertEquals(
                InteractiveHumanRenderer2.PoseLibrary.stand().get(InteractiveHumanRenderer2.JointType.HEAD).getY(),
                byNameStand.get(InteractiveHumanRenderer2.JointType.HEAD).getY(),
                1e-6
        );
    }
}
