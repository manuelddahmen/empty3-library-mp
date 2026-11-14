/*
 * Copyright (c) 2026. Manuel Daniel Dahmen
 * Licensed under the Apache License, Version 2.0
 */

package one.empty3.library;

import one.empty3.library.core.builder.SceneBuilder;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SceneBuilderTest {

    @Test
    public void testFluentSceneBuilder() {
        Scene scene = new SceneBuilder()
                .withCamera(new Point3D(0.0, 0.0, -10.0), Point3D.O0, new Point3D(0.0, 1.0, 0.0))
                .addSphere(Point3D.O0, 2.0, 0xFF0000)
                .addCube(new Point3D(3.0, 0.0, 0.0), 1.0, 0x00FF00)
                .build();

        assertNotNull(scene, "Scene should be successfully built.");
        assertNotNull(scene.getCameraActive(), "Camera should be configured.");
        assertEquals(2, scene.getObjets().getData1d().size(), "Scene should contain 2 objects.");
    }
}
