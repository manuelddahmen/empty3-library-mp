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

package one.empty3.library;


import one.empty3.ECImage;

/*__
 * Represents polygons built on a up axis. Each point of one polygon is joint by
 * his neighbors left, right, up down '''
 *
 * @author DAHMEN Manuel
 */
//
//public class PolyMap extends Representable {
//
//    private List<Polygon> polys = new ArrayList<Polygon>();
//    private int size;
//
//    /*__
//     * *
//     * Constructor
//     *
//     * @param size Size of polygons
//     */
//   public PolyMap(int size) {
//
//        this.size = size;
//        if (!(size > 2)) {
//            this.size = 3;
//            throw new UnsupportedOperationException("Maillage de polygones");
//        }
//
//    }
//
//    /*__
//     * *
//     * Add point
//     *
//     * @param coordArr height
//     * @param p point to add
//     */
//    public void addPoint(int coordArr, Point3D p) {
//        if (coordArr >= 0) {
//
//            if (coordArr == polys.size()) {
//            }
//
//            polys.get(coordArr).getPoints().add(p);
//
//        }
//    }
//
//    /*__
//     * *
//     *
//     * @return maillage as an array
//     * @throws IllegalOperationException
//     */
//    public Point3D[][] getMaillage() throws IllegalOperationException {
//        if (polys.isEmpty()) {
//            throw new IllegalOperationException("Maillage incorrect");
//        }
//        int dim2 = 0;
//        for (int i = 0; i < polys.size(); i++) {
//            if (i == 0 || dim2 == polys.get(i).getPoints().size()) {
//                dim2 = polys.get(i).getPoints().size();
//            } else {
//                throw new IllegalOperationException("Maillage incorrect");
//            }
//        }
//        Point3D[][] pts = new Point3D[polys.size()][dim2];
//        for (int i = 0; i < polys.size(); i++) {
//            for (int j = 0; j < polys.get(i).getPoints().size(); j++) {
//                pts[i][j] = polys.get(i).getPoints().get(j);
//            }
//        }
//        return pts;
//    }
//
//    /*__
//     * *
//     * Gets points on a (coordArr,y) matrix;
//     *
//     * @param coordArr width coordArr>=0 && coordArr<polys.size() @para
//     *          m y height y>=0 && y< each poly.size() in polys @return
//     */
//    public Point3D getPoint(int coordArr, int y) {
//
//        return coordArr < size && y < polys.size()
//                ? polys.get(coordArr).getPoints().get(y)
//                : null;
//
//    }
//
//    /*__
//     * *
//     * Polygon's list Each polygon must have the same size than others
//     *
//     * @return polygones
//     */
//    public List<Polygon> getPolys() {
//        return polys;
//    }
//}
