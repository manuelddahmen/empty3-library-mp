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

package one.empty3.feature;

import one.empty3.io.ProcessFile;

import java.io.File;

public class ProcesssX2Process extends ProcessFile{
    protected File input1;
    protected File input2;
    public void ProcesssX2Process() {
    }

    public final boolean process2Files(File input1, File input2,  File out) {
        this.input1 = input1;
        this.input2 = input2;

        return process(null, out);
    }


    /***
     * Can you fields input1, input2, outTemp1, outTemp2
     * @param in null
     * @param out definitive result
     * @return
     */
    @Override
    public boolean process(File in, File out) {
        return false;
    }
}
