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

package one.empty3.feature_2;

import java.io.File;

import one.empty3.androidFeature.kmeans.K_Clusterer;
import one.empty3.androidFeature.kmeans.MakeDataset;
import one.empty3.io.ProcessFile;


public class KMeans extends ProcessFile {
    protected K_Clusterer kclusterer;

    public boolean process(File in, File out) {
        if (!isImage(in))
            return false;
        // init centroids with random colored
        // points.
        try {
            File dir = new File(out.getParentFile().getAbsolutePath()+File.separator+"subKMeans");
            if(!dir.exists())
                dir.mkdir();

            File inCsv = new File(dir.getAbsolutePath()+File.separator+"kMeans"+in.getName()+".csv");;

            new MakeDataset(in, inCsv, maxRes);

            kclusterer = new K_Clusterer();
            kclusterer.setRandom(false);
            kclusterer.process(in, inCsv, out, maxRes);

            addSource(out);

            return true;


        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
