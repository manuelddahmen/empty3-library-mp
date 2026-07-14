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

package one.empty3.library.tests;

import one.empty3.library.Camera;
import one.empty3.library.ColorTexture;
import one.empty3.library.Point3D;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestVoiture extends TestObjetSub {
    public void ginit() {
        scene().add(new Voiture());
        scene().cameraActive(new Camera());
        setMaxFrames(1);
        loop(false);
        scene().cameraActive(new Camera(new Point3D(0.0, 0.0, 5000.0),
                new Point3D(0.0, 0.0, 0.0), Point3D.Y.mult(1.)));
        z().texture(new ColorTexture(one.empty3.libs.Color.newCol(0.0f, 0.0f, 1f)));
    }

    @Override
    public void finit() throws Exception {
        super.finit();
        Logger.getAnonymousLogger().log(Level.INFO, String.valueOf(scene()));
    }

    public static void main(String[] args) {
        TestVoiture testVoiture = new TestVoiture();
        testVoiture.setGenerate(GENERATE_MODEL | GENERATE_IMAGE | GENERATE_MOVIE);
        testVoiture.setPublish(true);
        new Thread(testVoiture).start();
    }
}
