/*
 * Copyright (c) 2024.
 *
 *
 *  Copyright 2023 Manuel Daniel Dahmen
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *
 */

package one.empty3.feature.model;

import one.empty3.feature.PixM;
import one.empty3.io.ProcessFile;

import java.io.File;
import java.util.Objects;

public class IdentNullProcess extends ProcessFile {

    @Override
    public boolean process(File in, File out) {
        PixM pixM = null;
        if (maxRes > 0) {
            pixM = PixM.getPixM(Objects.requireNonNull(new one.empty3.libs.Image(in)), maxRes);
        } else {
            pixM = new PixM(Objects.requireNonNull(new one.empty3.libs.Image(in)));
        }
        assert pixM != null;
        pixM.getImage().saveFile(out);
        addSource(out);
        return true;

    }

}
