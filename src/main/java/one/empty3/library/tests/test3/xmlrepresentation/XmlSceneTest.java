/*
 *
 *  * Copyright (c) 2024. Manuel Daniel Dahmen
 *  *
 *  *
 *  *    Copyright 2024 Manuel Daniel Dahmen
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 *
 *
 */

package one.empty3.library.tests.test3.xmlrepresentation;

import junit.framework.TestCase;
import one.empty3.library.Scene;
import org.junit.Test;

import java.util.logging.Level;
import java.util.logging.Logger;

/*__
 * Created by manue on 20-09-19.
 */
public class XmlSceneTest extends TestCase {
    @Test
    public void testScene() {
        Scene scene = new Scene();
        StringBuilder stringBuilder = new StringBuilder();
        scene.declareProperties();
        scene.declarations();
        scene.xmlRepresentation(null, scene, stringBuilder);
        Logger.getAnonymousLogger().log(Level.INFO, "" + stringBuilder);

        assertTrue(stringBuilder.toString().length() > 0);
    }
}
