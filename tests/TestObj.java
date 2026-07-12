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

import com.jogamp.opengl.GL2;
import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.Camera;
import one.empty3.library.Point3D;
import one.empty3.library.RepresentableConteneur;
import one.empty3.library.objloader.GLModel;
import one.empty3.library.objloader.ModelLoaderOBJ;

public class TestObj extends TestObjetSub {
    private GL2 glDrawContext;

    @Override
    public void finit() throws Exception {
        super.finit();
        scene().clear();
        GLModel glModel = ModelLoaderOBJ.LoadModel("resources/models/test1.obj", "resources/models/", null);
        RepresentableConteneur representableConteneur = new RepresentableConteneur();
        glModel.addToRepresentable(representableConteneur);
        scene.add(representableConteneur);

        scene().cameraActive(new Camera(Point3D.Z.mult(3), Point3D.O0, Point3D.Y));
    }

    @Override
    public void ginit() {
        super.ginit();
    }

    public GL2 getGlDrawContext() {
        return glDrawContext;
    }

    public void setGlDrawContext(GL2 glDrawContext) {
        this.glDrawContext = glDrawContext;
    }

    public static void main(String[] args) {
        TestObj testObj = new TestObj();
        testObj.setMaxFrames(1);

        new Thread(testObj).start();
    }
}
