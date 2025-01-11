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

package one.empty3.library.core.nurbs;

import one.empty3.library.Point3D;
import one.empty3.library.StructureMatrix;

/*__
 * Created by manue on 23-11-19.
 */
public class ExtrusionCurveCurve extends ParametricSurface {
    /***
     * base: chemin d'extrusion
     */
    protected StructureMatrix<ParametricCurve> base = new StructureMatrix<>(0, ParametricCurve.class);
    /***
     * base: courbe à extruder le long de base
     */
    protected StructureMatrix<ParametricCurve> path = new StructureMatrix<>(0, ParametricCurve.class);


    public ExtrusionCurveCurve() {
        base.setElem(new CourbeParametriquePolynomialeBezier());
        path.setElem(new CourbeParametriquePolynomialeBezier());
    }

/***
 * Problème : path à partir de P(0,0,0)
 * *//*
    public Point3D calculerPoint3D(double u, double v) {

        Point3D Op, T, NX, NY, pO;

        Op = path.getElem().calculerPoint3D(u);

        T = path.getElem().tangente(u);


        // Plan normal pour le chemin
        Point3D normale = path.getElem().calculerNormale(u);
        T = T.norme1();
        NX = normale.norme1();
        NY = NX.prodVect(T).norme1();
        pO = Op.plus(NX.mult(base.getElem().calculerPoint3D(v))).plus(NY.mult(base.getElem().calculerPoint3D(v)));
        return pO;

    }*/
    public Point3D calculerPoint3D(double u, double v) {
        Point3D T, NX, NY;
        T = path.getElem().tangente(v);
        // Plan normal pour le chemin
        Point3D normale = path.getElem().calculerNormale(v);
        T = T.norme1();
        NY = normale.norme1();
        NX = NY.prodVect(T).norme1();
        // CORRECTION ERREUR INATTENDUE.
        return base.getElem().calculerPoint3D(u).plus(
            path.getElem().calculerPoint3D(v));
    }


    @Override
    public void declareProperties() {
        super.declareProperties();
        getDeclaredDataStructure().put("base/Surface à extruder", base);
        getDeclaredDataStructure().put("path/Chemin d'extrusion", path);
    }

    public StructureMatrix<ParametricCurve> getBase() {
        return base;
    }

    public void setBase(StructureMatrix<ParametricCurve> base) {
        this.base = base;
    }

    public StructureMatrix<ParametricCurve> getPath() {
        return path;
    }

    public void setPath(StructureMatrix<ParametricCurve> path) {
        this.path = path;
    }
}
