/*
 * Copyright (c) 2024.
 *
 *
 *  Copyright 2012-2023 Manuel Daniel Dahmen
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

package one.empty3.feature_2.selection;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import one.empty3.io.ProcessFile;
import one.empty3.library.Lumiere;
import matrix.PixM;
import one.empty3.library.Point3D;

/*
 * Trouver les primitives.
 */
public abstract class Selection extends ProcessFile {
    private List<Point3D> selectedPoints = new ArrayList<>();
    private matrix.PixM image;
    public static final int SELECTION_TYPE_COLOR = 1;
    public static final int SELECTION_TYPE_COLOR_FROM_POINT = 2;
    public static final int SELECTION_TYPE_COLOR_FROM_RECT = 4;
    public static final int SELECTION_OPTION_PRESELECTION = 8;

    public List<Point3D> select(List<Point3D> preSelection, matrix.PixM pix,
                                int rgb, double threshold) {
        List<Point3D> list = new ArrayList<>();
        double[] doubles = Lumiere.getDoubles(rgb);
        for (int i = 0; i < pix.getColumns(); i++) {
            for (int j = 0; j < pix.getLines(); j++) {
                for (int c = 0; c < pix.getCompNo(); c++) {
                    if (pix.get(i, j) <= doubles[c] - threshold && pix.get(i, j) >= doubles[c] + threshold) {
                        list.add(Point3D.n(i, j, 0));
                    }
                }
            }
        }
        return list;
    }

    public List<Point3D> selectColorPoint(List<Point3D> preSelection, matrix.PixM pix,
                                          int rgb, int x, int y, double threshold) {
        List<Point3D> list = new ArrayList<>();
        double[] doubles = Lumiere.getDoubles(rgb);
        for (int i = 0; i < pix.getColumns(); i++) {
            for (int j = 0; j < pix.getLines(); j++) {
                for (int c = 0; c < pix.getCompNo(); c++) {
                    if (pix.get(i, j) <= doubles[c] - threshold && pix.get(i, j) >= doubles[c] + threshold) {
                        list.add(Point3D.n(i, j, 0));
                    }
                }
            }
        }
        return list;
    }


    public abstract List<Point3D> selectPoint(List<Point3D> preSelection, matrix.PixM pix,
                                              int x, int y);

    public abstract List<Point3D> selectInRect(List<Point3D> preSelection, matrix.PixM pix,
                                               int x1, int y1, int x2, int y2);

    @Override
    public boolean process(File in, File out) {
        return false;
    }
}
