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

package one.empty3.library.tests.tests2.mammaires;

import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.TextureCol;
import one.empty3.library.core.move.Trajectoires;

import one.empty3.libs.*;
import one.empty3.apps.testobject.TestObjetSub;


public class TestSeins extends TestObjetSub {
    public static void main(String[] args) {

        TestSeins target = new TestSeins();
        target.loop(true);
        target.setMaxFrames(600);
        target.setGenerate(GENERATE_IMAGE | GENERATE_MODEL | GENERATE_MOVIE);
        new Thread(target).start();
    }

    public void ginit() {
        this.setMaxFrames(3);

    }

    @Override
    public void finit() {
        if (frame() == 0) {
            mammaires.Sein1 s = new mammaires.Sein1();
            s.texture(new TextureCol(one.empty3.libs.Color.newCol(0,0,1)));
            scene().add(s);
        } else if (frame() == 1) {
            mammaires.Sein2 s = new mammaires.Sein2();
            s.texture(new TextureCol(one.empty3.libs.Color.newCol(0,0,1)));
            scene().clear();
            scene().add(s);
        } else if (frame() == 2) {
            mammaires.Sein3 s = new mammaires.Sein3();
            s.texture(new TextureCol(one.empty3.libs.Color.newCol(0,0,1)));
            scene().clear();
            scene().add(s);
        }
    }

    private Point3D cercle() {

        return Trajectoires.sphere(0.0 + 1.0 * frame / getMaxFrames(), 0, 3);
    }

    public void testScene() {
        scene().cameraActive(new Camera(cercle(), Point3D.O0));

    }
}
