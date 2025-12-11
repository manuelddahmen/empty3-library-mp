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

package one.empty3.library.core.tribase;

import one.empty3.library.HeightMapSurface;
import one.empty3.library.LineSegment;
import one.empty3.library.Point3D;
import one.empty3.library.StructureMatrix;
import one.empty3.library.core.nurbs.ParametricCurve;
import one.empty3.libs.Image;

/***
 * @author manuelddahmen
 * @see one.empty3.library.core.tribase.Tubulaire3
 * @see one.empty3.library.HeightMapSurface
 *
 * Tube (cylindre à âme courbe et sculptée par une image en niveauc de gris (0-255)
 */
public class T3D extends Tubulaire3 {
    /***
     * @serialField surfaceUV
     */
    protected StructureMatrix<HeightMapSurface> surfaceUV = new StructureMatrix<HeightMapSurface>(0, HeightMapSurface.class);

    /***
     * Constructor by default.
     *
     */
    public T3D() {
        super();
        soulCurve = new StructureMatrix<>(0, ParametricCurve.class);
        surfaceUV = new StructureMatrix<>(0, HeightMapSurface.class);
        surfaceUV.setElem(new HeightMapSurface() {
            @Override
            public Point3D height(double u, double v) {
                return super.height(u, v);
            }
        });
    }

    @Override
    public Point3D calculerPoint3D(double v, double u) {
        if (level == 0 && quad_not_computed > 0) {
            super.calculerPoint3D(v, u);
        }
        Point3D[] vectPerp = vectPerp(u, v);
      /*  if(v==0) {
            vectPerp1 = vectPerp(u, v);
            vectPerp = vectPerp(u, v);
        } else {
            vectPerp1 = vectPerp(u, v);
            Matrix33 matrix33 = new Matrix33(vectPerp);
            double angle = Math.acos(vectPerp[0].prodScalaire(vectPerp1[0])/(vectPerp[0].norme()*vectPerp1[0].norme()));
            Matrix33 mult = matrix33.mult(new Matrix33(new Point3D[]{Point3D.X, Point3D.Y, vectPerp[0]}));

            vectPerp = mult.getColVectors();
        }
        */
        return soulCurve.getElem().calculerPoint3D(u).plus(
                vectPerp[1].mult(diameterFunction.getElem().result(u) * Math.cos(2 * Math.PI * v)*
                        surfaceUV.getElem().heightDouble(u, v)
                    )).plus(
                vectPerp[2].mult(diameterFunction.getElem().result(u) * Math.sin(2 * Math.PI * v)*
                        surfaceUV.getElem().heightDouble(u, v)
                    ));
    }

    public StructureMatrix<HeightMapSurface> getSurfaceUV() {
        return surfaceUV;
    }

    public void setSurfaceUV(StructureMatrix<HeightMapSurface> surfaceUV) {
        this.surfaceUV = surfaceUV;
    }
}
