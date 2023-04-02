/*
 * Copyright (c) 2023. Manuel Daniel Dahmen
 *
 *
 *    Copyright 2012-2023 Manuel Daniel Dahmen
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package one.empty3.feature;

import one.empty3.io.ProcessFile;

import java.io.File;

public abstract class ProcessInMemory extends ProcessFile {


    public PixM processInMemory(one.empty3.feature.PixM in) {
        return in.copy();
    }

    @Deprecated
    @Override
    public boolean process(File in, File out) {
        return false;
    }
}
