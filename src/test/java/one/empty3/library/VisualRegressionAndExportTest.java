/*
 * Copyright (c) 2026. Manuel Daniel Dahmen
 * Licensed under the Apache License, Version 2.0
 */

package one.empty3.library;

import one.empty3.library.core.export.ObjExport;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class VisualRegressionAndExportTest {

    @Test
    public void testSphereExportObj() throws IOException {
        Scene scene = new Scene();
        Sphere sphere = new Sphere(Point3D.O0, 2.0);
        sphere.texture(new ColorTexture(0xFF0000));
        scene.add(sphere);

        File tempFile = File.createTempFile("sphere_test", ".obj");
        tempFile.deleteOnExit();

        ObjExport.save(tempFile, scene, true);

        assertTrue(tempFile.exists(), "OBJ file should be successfully created.");
        assertTrue(tempFile.length() > 0, "OBJ file should contain exported 3D data.");
    }
}
