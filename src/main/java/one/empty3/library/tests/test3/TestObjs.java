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

package one.empty3.library.tests.test3;

import one.empty3.library.core.testing.TestObjetSub;
import one.empty3.library.objloader.E3Model;
import one.empty3.library.objloader.ModelLoaderOBJ;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestObjs extends TestObjetSub {
    String directory = "resources/models/samples/";

    public TestObjs() {

    }

    public void finit() {
        testObjs();
    }

    public void testObjs() {
        String[] list = new File(directory).list();
        assert list != null;
        setMaxFrames(list.length);
        Logger.getAnonymousLogger().log(Level.INFO, "Obj directory. Number of files : " + list.length);
        scene().getObjets().getData1d().clear();
        int i = frame();
        if (i < list.length) {
            File file = new File(directory + list[i]);

            if (file.exists() && file.getAbsolutePath().toLowerCase().endsWith(".obj")) {

                E3Model e3Model = ModelLoaderOBJ.LoadModelE3(file.getAbsolutePath(), file.getAbsolutePath());

                scene().add(e3Model);

                Logger.getAnonymousLogger().log(Level.INFO, "Obj file added. " + file.getName());
            } else {
                Logger.getAnonymousLogger().log(Level.INFO, "Not a obj file");
            }
        }
    }

    public static void main(String[] args) {
        TestObjs testObjs = new TestObjs();
        new Thread(testObjs).start();
    }
}
