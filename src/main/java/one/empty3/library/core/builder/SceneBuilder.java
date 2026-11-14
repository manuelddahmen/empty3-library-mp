/*
 * Copyright (c) 2026. Manuel Daniel Dahmen
 * Licensed under the Apache License, Version 2.0
 */

package one.empty3.library.core.builder;

import one.empty3.library.*;

/**
 * Fluent Builder API for modernizing Scene and 3D object construction in empty3-library-mp.
 */
public class SceneBuilder {
    private final Scene scene;

    /**
     * Constructs a new SceneBuilder instance.
     */
    public SceneBuilder() {
        this.scene = new Scene();
    }

    /**
     * Configures the active camera for the scene.
     *
     * @param eye         position of the camera
     * @param lookAt      target point the camera is looking at
     * @param orientation up-vector orientation of the camera
     * @return this SceneBuilder instance
     */
    public SceneBuilder withCamera(Point3D eye, Point3D lookAt, Point3D orientation) {
        Camera camera = new Camera(eye, lookAt, orientation);
        scene.cameraActive(camera);
        return this;
    }

    /**
     * Adds a sphere to the scene with specified center, radius, and color.
     *
     * @param center   center coordinate of the sphere
     * @param radius   radius of the sphere
     * @param colorRgb RGB color integer
     * @return this SceneBuilder instance
     */
    public SceneBuilder addSphere(Point3D center, double radius, int colorRgb) {
        Sphere sphere = new Sphere(center, radius);
        sphere.texture(new ColorTexture(colorRgb));
        scene.add(sphere);
        return this;
    }

    /**
     * Adds a cube to the scene with specified position, size, and color.
     *
     * @param position position coordinate of the cube
     * @param size     side length of the cube
     * @param colorRgb RGB color integer
     * @return this SceneBuilder instance
     */
    public SceneBuilder addCube(Point3D position, double size, int colorRgb) {
        Cube cube = new Cube(size, position);
        cube.texture(new ColorTexture(colorRgb));
        scene.add(cube);
        return this;
    }

    /**
     * Builds and returns the constructed Scene.
     *
     * @return the constructed Scene instance
     */
    public Scene build() {
        return scene;
    }
}
