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

package one.empty3.apps.facedetect.jvm;

import one.empty3.library.objloader.E3Model;
import one.empty3.libs.Image;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Base64;
import java.util.Map;
import java.util.Objects;

public class ProcessData implements Runnable {
    boolean isRunning = true;
    EditPolygonsMappings editPolygonsMappings;
    Map<String, byte[]> data;
    private int count = 0;

    /***
     * Constructor
     * @param data POST data encoded as String and Base64 for files
     */
    public ProcessData(Map<String, byte[]> data) {
        this.data = data;
    }

    @Override
    public void run() {

    }
}
