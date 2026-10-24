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

package one.empty3.library1.tree;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class ListInstructionsTest {

    private static final double EPSILON = 1e-3;

    @Test
    public void testUserDefinedFunctionEquals() {
        String script = "f(x) = x * x + 2.0\n" +
                        "y = f(3.0)";
        ListInstructions li = new ListInstructions();
        li.addInstructions(script);
        List<String> results = li.runInstructions();

        assertNotNull(results);
        assertTrue(li.getCurrentParamsValues().containsKey("y"));
        assertEquals(11.0, li.getCurrentParamsValues().get("y"), EPSILON);
    }

    @Test
    public void testUserDefinedFunctionBraced() {
        String script = "f(x) { x + 5.0 }\n" +
                        "z = f(4.0)";
        ListInstructions li = new ListInstructions();
        li.addInstructions(script);
        List<String> results = li.runInstructions();

        assertNotNull(results);
        assertTrue(li.getCurrentParamsValues().containsKey("z"));
        assertEquals(9.0, li.getCurrentParamsValues().get("z"), EPSILON);
    }

    @Test
    public void testMultiLineFunctionBracedListInstructions() {
        String script = "f(x) {\n" +
                        "  a = x * 2.0\n" +
                        "  b = a + 3.0\n" +
                        "  b\n" +
                        "}\n" +
                        "res = f(5.0)";
        ListInstructions li = new ListInstructions();
        li.addInstructions(script);
        List<String> results = li.runInstructions();

        assertNotNull(results);
        assertTrue(li.getCurrentParamsValues().containsKey("res"));
        assertEquals(13.0, li.getCurrentParamsValues().get("res"), EPSILON);
    }
}
