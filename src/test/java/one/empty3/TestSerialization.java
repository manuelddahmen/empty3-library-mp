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
 *  * Created by $user $date
 *
 *
 */

package one.empty3;

import one.empty3.gui.DataModel;
import one.empty3.library.Camera;
import one.empty3.library.Scene;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class TestSerialization {

    /**
     * Tests that a scene edited via DataModel can be saved to XML and loaded back.
     * Verifies:
     * - XML file is created and non-empty
     * - XML content includes the Scene class name and Camera class name
     * - Loaded DataModel produces a non-null Scene
     */
    @Test
    public void testSceneSerializationWithDataModel() throws IOException {
        // Step 1: Create DataModel and edit the scene
        DataModel dataModel = new DataModel();
        dataModel.setScene(new Scene());
        Scene scene = dataModel.getScene();
        scene.setAuthor("TestAuthor1");
        scene.setDescription("Test scene for serialization");

        Camera camera = new Camera();
        scene.cameraActive(camera);
        File[] xmlFiles = new File[1];
        xmlFiles[0] = new File("testSer.mood.xml");
        assert scene.cameraActive() != null : "Scene should have an active camera before save";

        // Step 2: Get the output directory and save the scene
        dataModel.save(xmlFiles[0].getAbsolutePath());

        // Step 3: Find the saved XML file in the output directory
        //File dirFile = new File(dir);
        //File[] xmlFiles = dirFile.listFiles((d, name) -> name.endsWith(".mood.xml"));
        //assert xmlFiles != null && xmlFiles.length > 0
        //        : "Expected at least one XML file in " + dir + ", but none found";

        File savedFile = xmlFiles[0];
        System.out.println("Saved scene to: " + savedFile.getAbsolutePath());
        assert savedFile.exists() : "Saved XML file should exist on disk";
        assert savedFile.length() > 0 : "Saved XML file should not be empty";

        // Step 4: Verify XML content contains expected class names
        String xmlContent = new String(Files.readAllBytes(savedFile.toPath()));
        System.out.println("XML content (first 500 chars):\n" +
                xmlContent.substring(0, Math.min(500, xmlContent.length())));

        assert xmlContent.contains(Scene.class.getName())
                : "XML should reference Scene class: " + Scene.class.getName();
        assert xmlContent.contains(Camera.class.getName())
                : "XML should reference Camera class: " + Camera.class.getName();

        // Step 5: Load the scene back from the saved XML file
        DataModel loadedModel = new DataModel(savedFile);
        Scene loadedScene = loadedModel.getScene();

        assert loadedScene != null : "Loaded scene should not be null";

        System.out.println("Original scene camera: " + scene.cameraActive());
        System.out.println("Loaded scene: " + loadedScene);
        System.out.println("Serialization test passed.");
    }
}
