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

package one.empty3.library.tests.tests2.modeleStl;

import one.empty3.apps.testobject.TestObjetSub;
import one.empty3.library.P;
import one.empty3.library.Scene;
import one.empty3.library.stl_loader.IncorrectFormatException;
import one.empty3.library.stl_loader.ParsingErrorException;
import one.empty3.library.stl_loader.StlFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

public class TestStl2 extends TestObjetSub {
    private BufferedReader reader;

    @Override
    public void ginit() {
        StlFile file = new StlFile();
        Scene load = new Scene();
        try {
            File file1 = new File("samples/stl/another_nude_girl-ascii.stl");
            load = file.load(file1.getAbsolutePath());
        } catch (IncorrectFormatException | IOException | ParsingErrorException e) {
            e.printStackTrace();
        }
        scene().add(load.getObjets().getElem(0));

        camera().setEye(P.n(0, 0, -1500.0));
    }


    public static void main(String[] args) {
        TestStl2 stl = new TestStl2();
        new Thread(stl).start();
    }

}
