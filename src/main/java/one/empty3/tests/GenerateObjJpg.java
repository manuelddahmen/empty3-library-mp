/*
 *
 *  *
 *  *  * Copyright (c) 2025. Manuel Daniel Dahmen
 *  *  *
 *  *  *
 *  *  *    Copyright 2024 Manuel Daniel Dahmen
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

package one.empty3.tests;

import one.empty3.library.Camera;
import one.empty3.library.ColorTexture;
import one.empty3.library.Scene;
import one.empty3.library.ZBufferImpl;
import one.empty3.library.objloader.E3Model;
import one.empty3.library.objloader.ModelLoaderOBJ;
import one.empty3.libs.Color;

import javax.imageio.ImageIO;
import java.io.*;

public class GenerateObjJpg {
    public static void runMain(String[] args) {
        long timeIn = System.currentTimeMillis();
        try {
        File obj = new File(args[0]);
        File jpg = new File(args[1]);

        ZBufferImpl zBuffer = new ZBufferImpl(1000, 1000);

        Scene scene = new Scene();
        E3Model e3Model = new E3Model(new BufferedReader(new FileReader(obj.getAbsolutePath())), true, null);
        scene.add(e3Model);
            scene.getObjets().getElem(0).texture(new ColorTexture(new Color(one.empty3.libs.Color.newCol(0.0f, 0.0f, 0.0f))));
        scene.cameraActive(new Camera());
        scene.cameraActive().getEye().setX(10.);
        zBuffer.scene(scene);

        zBuffer.setIncrementOptimizer(new ZBufferImpl.IncrementOptimizer(1/100.0, 1/1000.0));


        zBuffer.draw();


        long timeOut = System.currentTimeMillis();
        double timeElapsed = (timeOut-timeIn)/1000.0;

        System.out.println(timeElapsed);

        if(zBuffer.image().saveFile( jpg)) {
            System.out.println("File written = "+ jpg.getAbsolutePath());
        }
        } catch (RuntimeException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }
    public static void main(String[] args) {

        if(args.length!=2) {
            GenerateObjJpg.runMain(new String[]{"resources/models/head.obj69A757E0-9740-44E9-AE25-FBEA2C6928BD.obj",
                    "imageObjHead.jpg"});
        } else {

            GenerateObjJpg.runMain(args);
        }
    }
}
