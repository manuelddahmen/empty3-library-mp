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

package one.empty3.library.tests.tests2;

import one.empty3.library.*;
import one.empty3.library.core.nurbs.CameraInPath;
import one.empty3.library.core.testing.TestObjetSub;
import one.empty3.library.objloader.E3Model;
import one.empty3.library.objloader.ModelLoaderOBJ;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestObjTeapot extends TestObjetSub {
    private CameraInPath cameraInPath;

    public void ginit() {
        E3Model modelLoaderOBJ = ModelLoaderOBJ.LoadModelE3("resources/teapot.obj", "");
        modelLoaderOBJ.texture(new ColorTexture(Color.BLACK));
        scene().texture(new ColorTexture(Color.WHITE));
        scene().add(modelLoaderOBJ);
        scene().cameraActive(new Camera());
        scene().cameraActive().getEye().setZ(-5.);
        setPublish(true);
        setMaxFrames(100);
        cameraInPath = new CameraInPath(new Circle(new Axe(Point3D.Z, Point3D.Z.mult(-1.)), 10.), Point3D.Z);
        scene().cameraActive(cameraInPath);
    }

    public void finit() {
        cameraInPath.setT(1.0 * frame() / getMaxFrames());
        cameraInPath.calculerMatrice(Point3D.Y);
        Point3D eye = cameraInPath.getEye();
        //scene().cameraActive().setEye(eye);
        Logger.getAnonymousLogger().log(Level.INFO, "" + eye);
        eye = scene().cameraActive().eye();
        Logger.getAnonymousLogger().log(Level.INFO, "" + eye);
        Logger.getAnonymousLogger().log(Level.INFO, "" + cameraInPath.getLookat());
        z.idzpp();
    }

    public static void main(String[] args) {
        TestObjTeapot testObjTeapot = new TestObjTeapot();
        new Thread(testObjTeapot).start();
    }
}
